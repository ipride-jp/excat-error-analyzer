package jp.co.ipride.excat.configeditor.viewer.template.dialog.table;

import java.util.Arrays;
import java.util.List;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.configeditor.model.template.Member;
import jp.co.ipride.excat.configeditor.model.template.MemberList;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;


import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * テンプレートのメンバーを表示する
 * @author tu-ipride
 * @version 2.0
 */
public class MemberTableViewer {

	private final int NUMBER_COLUMNS = 2;
	private final int COL_1_WIDTH=350;
	private final int COL_2_WIDTH=80;

	// Set the table column property names
	private final String COL_1_NAME 	= "name";
	private final String COL_2_NAME 	= "use";

	// Set column names
	private String[] columnNames = new String[] {
			COL_1_NAME,
			COL_2_NAME,
			};

	private Composite parent;
	private Table table;
	private TableViewer tableViewer;
	private MemberList memberList;

	protected Color red;
	protected Color white;

	public MemberTableViewer(Composite parent, MemberList memberList) {
		this.parent=parent;
		this.memberList=memberList;
		this.red = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		this.white = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		createTable();
		createTableViewer();
		tableViewer.setContentProvider(new MemberContentProvider());
		tableViewer.setLabelProvider(new MemberLabelProvider());
		tableViewer.setInput(memberList);
	}

	private void createTable(){
		int style = SWT.SINGLE | SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL |
		SWT.FULL_SELECTION | SWT.HIDE_SELECTION;

		table = new Table(parent, style);

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalSpan = 3;
		table.setLayoutData(gridData);

		table.setLinesVisible(true);
		table.setHeaderVisible(true);

		// 1st column with text-box
		TableColumn column = new TableColumn(table, SWT.LEFT, 0);
		column.setText(
				ApplicationResource.getResource("Tab.Template.Member.Col1.Text"));
		column.setWidth(COL_1_WIDTH);
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new MemberSorter(MemberSorter.COL_1));
			}
		});

		// 2nd column with check-box
		column = new TableColumn(table, SWT.CENTER, 1);
		column.setText(
				ApplicationResource.getResource("Tab.Template.Member.Col2.Text"));
		column.setWidth(COL_2_WIDTH);
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new MemberSorter(MemberSorter.COL_2));
			}
		});

	}

	private void createTableViewer(){
		tableViewer = new TableViewer(table);
		tableViewer.setUseHashlookup(true);

		tableViewer.setColumnProperties(columnNames);

		// Create the cell editors
		CellEditor[] editors = new CellEditor[columnNames.length];

		// Column 1 : class name
		TextCellEditor textEditor = new TextCellEditor(table);
		editors[0] = textEditor;

		// Column 2 : Checkbox
		editors[1] = new CheckboxCellEditor(table);

		// Assign the cell editors to the viewer
		tableViewer.setCellEditors(editors);
		// Set the cell modifier for the viewer
		tableViewer.setCellModifier(new MemberCellModifier(this));

	}

	public void addNewMember(){
		memberList.addMember();
	}
	public void addNewMember(String name){
		memberList.addMember(name);
	}

	public void deleteMember(){
		Member member = (Member) ((IStructuredSelection)
				tableViewer.getSelection()).getFirstElement();
		if (member != null){
			memberList.removeMember(member);
		}
	}

	public void removeAll(){
		memberList.removeAll();
	}

	/**
	 * @return currently selected item
	 */
	public ISelection getSelection() {
		return tableViewer.getSelection();
	}

	/**
	 * Return the objectLineList
	 */
	public MemberList getMemberList() {
		return memberList;
	}

	public List<String> getColumnNames() {
		return Arrays.asList(columnNames);
	}

	/**
	 * Return the parent composite
	 */
	public Control getControl() {
		return table.getParent();
	}

	public boolean checkItems(){

		for (int i=0; i<table.getItemCount(); i++){
			TableItem item = table.getItem(i);
			item.setBackground(white);
		}

		//１．メンバー名が入力されていない場合、ＮＧ
		boolean result = true;
		for (int i=0; i<table.getItemCount(); i++){
			TableItem item = table.getItem(i);
			Member member = (Member)item.getData();
			if (!ViewerUtil.checkMethodName(member.getName())){
				item.setBackground(red);
				result=false;
			}
		}
		if (!result){
			return false;
		}
		//２．各アイテムの名は重複がある場合、
		for (int i=0; i<table.getItemCount(); i++){
			TableItem item1= table.getItem(i);
			for (int j=0; j<table.getItemCount(); j++){
				TableItem item2= table.getItem(j);
				if (item1.equals(item2)){
					continue;
				}
				Member member1 = (Member)item1.getData();
				Member member2 = (Member)item2.getData();
				if (member1.getName().equals(member2.getName())){
					item1.setBackground(red);
					item2.setBackground(red);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * ContentProvider
	 * @author tu
	 * @since 2007/11/17
	 */
	class MemberContentProvider implements IStructuredContentProvider,
										IMemberListViewer {

		/**
		 * IStructuredContentProvider
		 */
		public Object[] getElements(Object parent) {
			return memberList.getMembers().toArray();
		}

		/**
		 * IStructuredContentProvider
		 */
		public void dispose() {
			memberList.removeChangeListener(this);
		}

		/**
		 * IStructuredContentProvider
		 */
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null)
				((MemberList) newInput).addChangeListener(this);
			if (oldInput != null)
				((MemberList) oldInput).removeChangeListener(this);
		}

		/**
		 * IObjectLineListViewer
		 * @param objectLine
		 */
		public void addMember(Member member) {
			tableViewer.add(member);
		}

		/**
		 * IObjectLineListViewer
		 * @param objectLine
		 */
		public void updateMember(Member member) {
			tableViewer.update(member, null);
		}

		public void removeMember(Member member) {
			tableViewer.remove(member);

		}
	}

}
