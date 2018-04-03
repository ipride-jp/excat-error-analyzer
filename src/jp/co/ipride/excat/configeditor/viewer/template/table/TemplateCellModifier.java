package jp.co.ipride.excat.configeditor.viewer.template.table;

import jp.co.ipride.excat.configeditor.model.template.Template;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;

public class TemplateCellModifier implements ICellModifier{

	private TemplateTableViewer viewer;
	
	public TemplateCellModifier(TemplateTableViewer viewer){
		super();
		this.viewer=viewer;
	}

	/**
	 * ICellModifier
	 */
	public boolean canModify(Object element, String property) {
		return true;
	}

	/**
	 * ICellModifier
	 */
	public Object getValue(Object element, String property) {
		int columnIndex = viewer.getColumnNames().indexOf(property);
		Object result = null;
		Template template = (Template) element;
		switch (columnIndex) {
			case 0 : // class name 
				result = template.getClassName();
				break;
			case 1 : // check 
				result = new Boolean(template.isUse());
				break;
			default :
				result = "";
		}
		return result;	

	}

	/**
	 * ICellModifier
	 */
	public void modify(Object element, String property, Object value) {
		int columnIndex = viewer.getColumnNames().indexOf(property);
		TableItem item = (TableItem) element;
		item.setBackground(viewer.white);
		Template template = (Template) item.getData();
		String valueString;
		
		switch (columnIndex) {
		case 0 : // class name 
			valueString = ((String) value).trim();
			template.setClassName(valueString);
			break;
		case 1 : // check 
			template.setUse(((Boolean) value).booleanValue());
			break;
		default :
		}
		viewer.getTemplateList().templateChanged(template);
	}
}
