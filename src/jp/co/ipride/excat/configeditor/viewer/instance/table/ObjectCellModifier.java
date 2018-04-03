package jp.co.ipride.excat.configeditor.viewer.instance.table;

import jp.co.ipride.excat.configeditor.model.instance.ObjectLine;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;



/**
 * 
 * @author tu
 * @since 2007/11/14
 */
public class ObjectCellModifier implements ICellModifier{
	
	private ObjectTableViewer viewer;
	
	public ObjectCellModifier(ObjectTableViewer viewer){
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
		ObjectLine objectLine = (ObjectLine) element;
		switch (columnIndex) {
			case 0 : // class name 
				result = objectLine.getClassName();
				break;
			case 1 : // class laod name
				result = objectLine.getClassLoadName();
				break;
			case 2 : // max size
				result = objectLine.getMaxSize();
				break;
			case 3 : // check 
				result = new Boolean(objectLine.isUse());
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
		ObjectLine objectLine = (ObjectLine) item.getData();
		String valueString;
		
		switch (columnIndex) {
		case 0 : // class name 
			valueString = ((String) value).trim();
			objectLine.setClassName(valueString);
			break;
		case 1 : // class laod name
			valueString = ((String) value).trim();
			objectLine.setClassLoadName(valueString);
			break;
		case 2 : // max size
			valueString = ((String) value).trim();
			objectLine.setMaxSize(valueString);
			break;
		case 3 : // check 
			objectLine.setUse(((Boolean) value).booleanValue());
			break;
		default :
		}
		viewer.getObjectLineList().objectLineChanged(objectLine);
	}

}
