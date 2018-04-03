package jp.co.ipride.excat.configeditor.viewer.template.table;

import java.util.Arrays;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.template.ITemplateListViewer;
import jp.co.ipride.excat.configeditor.model.template.Template;
import jp.co.ipride.excat.configeditor.model.template.TemplateList;
import jp.co.ipride.excat.configeditor.viewer.template.dialog.TemplateRegisterDlg;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.DialogCellEditor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
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
 * table viewer for template
 * @author tu
 * @since 2007/11/18
 */
public class TemplateTableViewer {

	private final int NUMBER_COLUMNS = 2;
	private final int COL_1_WIDTH=600;
	private final int COL_2_WIDTH=150;

	// Set the table column property names
	private final String COL_1_NAME 	= "className";
	private final String COL_2_NAME 	= "use";

	// Set column names
	private String[] columnNames = new String[] {
			COL_1_NAME,
			COL_2_NAME,
			};

	private Composite parent;
	private Table table;
	private TableViewer tableViewer;
	private TemplateList templateList;

	protected Color red;
	protected Color white;

	public TemplateTableViewer(Composite parent) {
		this.parent=parent;
		this.templateList = ConfigModel.getTemplateList();
		this.red = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		this.white = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		createTable();
		createTableViewer();
		tableViewer.setContentProvider(new TemplateContentProvider());
		tableViewer.setLabelProvider(new TemplateLabelProvider());
		tableViewer.setInput(templateList);
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
				ApplicationResource.getResource("Tab.Template.Col1.Text"));
		column.setWidth(COL_1_WIDTH);
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new TemplateSorter(TemplateSorter.COL_1));
			}
		});

		// 2nd column with check-box
		column = new TableColumn(table, SWT.CENTER, 1);
		column.setText(
				ApplicationResource.getResource("Tab.Template.Col2.Text"));
		column.setWidth(COL_2_WIDTH);
		column.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				tableViewer.setSorter(new TemplateSorter(TemplateSorter.COL_2));
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
		DialogCellEditor textEditor = new TemplateDialogCellEditor(table);
		editors[0] = textEditor;

		// Column 2 : Checkbox
		editors[1] = new CheckboxCellEditor(table);

		// Assign the cell editors to the viewer
		tableViewer.setCellEditors(editors);
		// Set the cell modifier for the viewer
		tableViewer.setCellModifier(new TemplateCellModifier(this));

	}

	public void addNewTemplate(){
		templateList.addTemplate();
	}

	public void deleteTemplate(){
		Template template = (Template) ((IStructuredSelection)
				tableViewer.getSelection()).getFirstElement();
		if (template != null){
			templateList.removeTemplate(template);
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
	public TemplateList getTemplateList() {
		return templateList;
	}

	public java.util.List<String> getColumnNames() {
		return Arrays.asList(columnNames);
	}

	/**
	 * Return the parent composite
	 */
	public Control getControl() {
		return table.getParent();
	}

	/**
	 * １?D空白??目のチェック
	 *
	 * 単??目チェック
	 * @return
	 */
	public boolean checkItems(){
		for (int i=0; i<table.getItemCount(); i++){
			TableItem item = table.getItem(i);
			item.setBackground(white);
		}
		boolean result = true;
		for (int i=0; i<table.getItemCount(); i++){
			TableItem item = table.getItem(i);
			Template template = (Template)item.getData();
			if ("".equals(template.getClassName())){
				item.setBackground(red);
				result=false;
			}
		}
		return result;
	}
	/**
	 * ContentProvider
	 * @author tu
	 * @since 2007/11/17
	 */
	class TemplateContentProvider implements IStructuredContentProvider,
										ITemplateListViewer {

		/**
		 * IStructuredContentProvider
		 */
		public Object[] getElements(Object parent) {
			return templateList.getTemplates().toArray();
		}

		/**
		 * IStructuredContentProvider
		 */
		public void dispose() {
			templateList.removeChangeListener(this);
		}

		/**
		 * IStructuredContentProvider
		 */
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
			if (newInput != null)
				((TemplateList) newInput).addChangeListener(this);
			if (oldInput != null)
				((TemplateList) oldInput).removeChangeListener(this);
		}

		/**
		 * IObjectLineListViewer
		 * @param objectLine
		 */
		public void addTemplate(Template template) {
			tableViewer.add(template);
		}

		/**
		 * IObjectLineListViewer
		 * @param objectLine
		 */
		public void removeTemplate(Template template) {
			tableViewer.remove(template);
		}

		/**
		 * IObjectLineListViewer
		 * @param objectLine
		 */
		public void updateTemplate(Template template) {
			tableViewer.update(template, null);
		}
	}

	class TemplateDialogCellEditor extends DialogCellEditor {

		protected TemplateDialogCellEditor(Composite parent) {
	         super(parent);
	      }

		protected Object openDialogBox(Control cellEditorWindow) {
			TemplateRegisterDlg dlg = new TemplateRegisterDlg(cellEditorWindow.getShell());
			int index = table.getSelectionIndex();
			Template template = (Template)tableViewer.getElementAt(index);
			Template cloneTemplate = new Template();
			template.copyTo(cloneTemplate);
			dlg.init(cloneTemplate, template);
			int r = dlg.open();
			if (r == Dialog.OK){
				cloneTemplate.copyTo(template);
			}
			return template.getClassName();
		}

	}
}
