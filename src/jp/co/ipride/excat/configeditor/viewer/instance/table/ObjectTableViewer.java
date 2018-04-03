package jp.co.ipride.excat.configeditor.viewer.instance.table;

import java.util.Arrays;
import java.util.List;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.instance.ObjectLine;
import jp.co.ipride.excat.configeditor.model.instance.ObjectLineList;
import jp.co.ipride.excat.configeditor.util.DocumetUtil;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.configeditor.viewer.instance.dialog.ObjectRegisterDlg;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;


/**
 * table viewer for object tag
 * @author tu
 * @since 2007/11/14
 *
 */
public class ObjectTableViewer {

	private final int NUMBER_COLUMNS = 4;
	private final int COL_1_WIDTH=400;
	private final int COL_2_WIDTH=300;
	private final int COL_3_WIDTH=150;
	private final int COL_4_WIDTH=70;

	// Set the table column property names
	private final String COL_1_NAME 	= "className";
	private final String COL_2_NAME 	= "classLoadName";
	private final String COL_3_NAME 	= "maxSize";
	private final String COL_4_NAME 	= "use";

	// Set column names
	private String[] columnNames = new String[] {
			COL_1_NAME,
			COL_2_NAME,
			COL_3_NAME,
			COL_4_NAME
			};

	private Composite parent;
	private Table table;
	private TableViewer tableViewer;
	private ObjectLineList objectLineList;

	protected Color red;
	protected Color white;

	public ObjectTableViewer(Composite parent) {
		this.parent=parent;
		this.objectLineList = ConfigModel.getObjectLineList();
		createTable();
		createTableViewer();
		tableViewer.setContentProvider(new ObjectContentProvider());
		tableViewer.setLabelProvider(new ObjectLabelProvider());
		tableViewer.setInput(objectLineList);
		this.red = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		this.white = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
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
				ApplicationResource.getResource("Tab.Object.Col1.Text"));
		column.setWidth(COL_1_WIDTH);
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new ObjectLineSorter(ObjectLineSorter.COL_1));
			}
		});
		// 2st column with text-box
		column = new TableColumn(table, SWT.LEFT, 1);
		column.setText(
				ApplicationResource.getResource("Tab.Object.Col2.Text"));
		column.setWidth(COL_2_WIDTH);
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new ObjectLineSorter(ObjectLineSorter.COL_2));
			}
		});

		// 3nd column with text-box
		column = new TableColumn(table, SWT.CENTER, 2);
		column.setText(
				ApplicationResource.getResource("Tab.Object.Col3.Text"));
		column.setWidth(COL_3_WIDTH);
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new ObjectLineSorter(ObjectLineSorter.COL_3));
			}
		});

		// 4nd column with check-box
		column = new TableColumn(table, SWT.CENTER, 3);
		column.setText(
				ApplicationResource.getResource("Tab.Object.Col4.Text"));
		column.setWidth(COL_4_WIDTH);
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new ObjectLineSorter(ObjectLineSorter.COL_4));
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
//		TextCellEditor textEditor = new TextCellEditor(table);
//		((Text) textEditor.getControl()).setTextLimit(COL_1_WIDTH);
//		editors[0] = textEditor;
		DialogCellEditor dialogEditor = new ObjectDialogCellEditor(table);
//		((Text)dialogEditor.getControl()).setTextLimit(COL_1_WIDTH);
		editors[0] = dialogEditor;

		// Column 2 : class load name
		TextCellEditor textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).setTextLimit(COL_2_WIDTH);
		editors[1] = textEditor;

		// Column 3 : max size
		textEditor = new TextCellEditor(table);
		((Text) textEditor.getControl()).setTextLimit(COL_3_WIDTH);
		((Text) textEditor.getControl()).addVerifyListener(
				new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						e.doit=ViewerUtil.verifyNumbers(e.text);
					}
				});

		editors[2] = textEditor;

		// Column 3 : Checkbox
		editors[3] = new CheckboxCellEditor(table);

		// Assign the cell editors to the viewer
		tableViewer.setCellEditors(editors);
		// Set the cell modifier for the viewer
		tableViewer.setCellModifier(new ObjectCellModifier(this));

	}

	public void addNewObjectLine(){
		objectLineList.addObjectLine();
	}

	public void deleteObjectLine(){
		ObjectLine objectLine = (ObjectLine) ((IStructuredSelection)
				tableViewer.getSelection()).getFirstElement();
		if (objectLine != null){
			objectLineList.removeObjectLine(objectLine);
			ConfigModel.setChanged();
		}
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
	public ObjectLineList getObjectLineList() {
		return objectLineList;
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

	/**
	 * クラス名とクラス・ロードをチェックする
	 * １．クラス名が入力されていない場合、ＮＧ
	 *
	 * ２．各アイテムのクラス名は重複がある場合、
	 * 　　①．そのうち、一つのアイテムはクラス・ロードが無い場合、ＮＧ
	 * 　　②．クラス・ロードが全てある場合、
	 * 　　　　・クラス・ロードも重複であれば、ＮＧ
	 * ３．メソッドを監視するクラスとの重複がある場合、
	 * 　　①．メソッドが監視するクラスのクラス・ロードが無い場合、
	 * 　　　　「特定」クラスのロードがあれば、ＮＧ
	 * 　　②．メソッドが監視するクラスのクラス・ロードが無い場合、
	 * 　　　　「特定」クラスのロードが無ければ、ＮＧ
	 *
	 * ３．チェックのタイミング：保存時
	 *
	 * @return　true: OK / false:NG
	 */
	public boolean checkItems(){

		for (int i=0; i<table.getItemCount(); i++){
			TableItem item = table.getItem(i);
			item.setBackground(white);
		}

		//１．クラス名が入力されていない場合、ＮＧ
		boolean result = true;
		for (int i=0; i<table.getItemCount(); i++){
			TableItem item = table.getItem(i);
			ObjectLine object = (ObjectLine)item.getData();
			if (!ViewerUtil.checkClassName(object.getClassName()) ||
					!ViewerUtil.isLargerThanMinValue(object.getMaxSize(),0)){
				item.setBackground(red);
				result=false;
			}
		}
		if (!result){
			return false;
		}
		//２．各アイテムのクラス名は重複がある場合、
		for (int i=0; i<table.getItemCount(); i++){
			TableItem item1= table.getItem(i);
			for (int j=0; j<table.getItemCount(); j++){
				TableItem item2= table.getItem(j);
				if (item1.equals(item2)){
					continue;
				}
				ObjectLine object1 = (ObjectLine)item1.getData();
				ObjectLine object2 = (ObjectLine)item2.getData();
				if (object1.getClassName().equals(object2.getClassName())){
					if ("".equals(object1.getClassLoadName()) &&
						!"".equals(object2.getClassLoadName())	){
						item1.setBackground(red);
						item2.setBackground(red);
						return false;
					}
					if (!"".equals(object1.getClassLoadName()) &&
						"".equals(object2.getClassLoadName())){
						item1.setBackground(red);
						item2.setBackground(red);
						return false;
					}
					if (DocumetUtil.checkClassLoaderURL(object1.getClassLoadName(),object2.getClassLoadName())){
						item1.setBackground(red);
						item2.setBackground(red);
						return false;
					}
				}
			}
		}
		//３．メソッドを監視するクラスとの重複がある場合、
		for (int i=0; i<table.getItemCount(); i++){
			TableItem item= table.getItem(i);
			ObjectLine object = (ObjectLine)item.getData();
			int n = ConfigModel.checkClassOfInstanceDef(object);
			switch(n){
			case 1:
				item.setBackground(red);
				ExcatMessageUtilty.showMessage(
						parent.getShell(),
						Message.get("Dialog.Instance.ClassLoad.1.Text"));
				return false;
			case 2:
				item.setBackground(red);
				ExcatMessageUtilty.showMessage(
						parent.getShell(),
						Message.get("Dialog.Instance.ClassLoad.2.Text"));
				return false;
			}
		}
		return true;
	}


	/**
	 * ContentProvider
	 * @author tu
	 * @since 2007/11/17
	 */
	class ObjectContentProvider implements IStructuredContentProvider,
										IObjectLineListViewer {

		/**
		 * IStructuredContentProvider
		 */
		public Object[] getElements(Object parent) {
			return objectLineList.getObjectLines().toArray();
		}

		/**
		 * IStructuredContentProvider
		 */
		public void dispose() {
			objectLineList.removeChangeListener(this);
		}

		/**
		 * IStructuredContentProvider
		 */
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null)
				((ObjectLineList) newInput).addChangeListener(this);
			if (oldInput != null)
				((ObjectLineList) oldInput).removeChangeListener(this);
		}

		/**
		 * IObjectLineListViewer
		 * @param objectLine
		 */
		public void addObjectLine(ObjectLine objectLine) {
			tableViewer.add(objectLine);
		}

		/**
		 * IObjectLineListViewer
		 * @param objectLine
		 */
		public void removeObjectLine(ObjectLine objectLine) {
			tableViewer.remove(objectLine);
		}

		/**
		 * IObjectLineListViewer
		 * @param objectLine
		 */
		public void updateObjectLine(ObjectLine objectLine) {
			tableViewer.update(objectLine, null);
		}

	}
	class ObjectDialogCellEditor extends DialogCellEditor {

		protected ObjectDialogCellEditor(Composite parent) {
	         super(parent);
	      }

		protected Object openDialogBox(Control cellEditorWindow) {
			ObjectRegisterDlg dlg = new ObjectRegisterDlg(cellEditorWindow.getShell());
			int index = table.getSelectionIndex();
			ObjectLine objectLine = (ObjectLine)tableViewer.getElementAt(index);
			dlg.init(objectLine.getClassName());
			int r = dlg.open();
			if (r == Dialog.OK){
				objectLine.setClassName(dlg.getClassName());
			}
			return objectLine.getClassName();
		}

	}
}
