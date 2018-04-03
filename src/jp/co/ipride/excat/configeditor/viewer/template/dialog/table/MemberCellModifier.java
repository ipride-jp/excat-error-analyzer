package jp.co.ipride.excat.configeditor.viewer.template.dialog.table;

import jp.co.ipride.excat.configeditor.model.template.Member;
import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TableItem;


public class MemberCellModifier implements ICellModifier{

	private MemberTableViewer viewer;

	public MemberCellModifier(MemberTableViewer viewer){
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
		Member member = (Member) element;
		switch (columnIndex) {
			case 0 : // class name
				result = member.getName();
				break;
			case 1 : // check
				result = new Boolean(member.isUse());
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
		Member member = (Member) item.getData();
		String valueString;

		switch (columnIndex) {
		case 0 : // member name
			valueString = ((String) value).trim();
			member.setName(valueString);
			break;
		case 1 : // check
			member.setUse(((Boolean) value).booleanValue());
			break;
		default :
		}
		viewer.getMemberList().memberChanged(member);
	}


}
