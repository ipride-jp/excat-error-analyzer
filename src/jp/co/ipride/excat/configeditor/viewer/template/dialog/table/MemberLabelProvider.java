package jp.co.ipride.excat.configeditor.viewer.template.dialog.table;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.configeditor.model.template.Member;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 *
 * @version 2.0
 *
 */
public class MemberLabelProvider extends LabelProvider
								implements ITableLabelProvider{

	public static final String CHECKED_IMAGE 	= "config/checked.jpg";
	public static final String UNCHECKED_IMAGE  = "config/unchecked.jpg";

	private static ImageRegistry imageRegistry = new ImageRegistry();

	static {
		imageRegistry.put(CHECKED_IMAGE, ImageDescriptor.createFromFile(
				MainViewer.class, IconFilePathConstant.BASE_FOLDER_PATH + CHECKED_IMAGE
				)
			);
		imageRegistry.put(UNCHECKED_IMAGE, ImageDescriptor.createFromFile(
				MainViewer.class, IconFilePathConstant.BASE_FOLDER_PATH + UNCHECKED_IMAGE
				)
			);
	}

	private Image getImage(boolean isSelected) {
		String key = isSelected ? CHECKED_IMAGE : UNCHECKED_IMAGE;
		return  imageRegistry.get(key);
	}

	/**
	 * ITableLabelProvider
	 */
	public Image getColumnImage(Object element, int columnIndex) {
		if (columnIndex==1){
			return getImage(((Member)element).isUse());
		}else{
			return null;
		}
	}

	/**
	 * ITableLabelProvider
	 */
	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		Member member = (Member) element;
		switch (columnIndex) {
			case 0:  // class name
				result = member.getName();
				break;
			case 1 :
				break;
			default :
				break;
		}
		return result;
	}

}
