package jp.co.ipride.excat.configeditor.viewer.template.table;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.configeditor.model.template.Template;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class TemplateLabelProvider extends LabelProvider
								   implements ITableLabelProvider {

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
			return getImage(((Template)element).isUse());
		}else{
			return null;
		}
	}

	/**
	 * ITableLabelProvider
	 */
	public String getColumnText(Object element, int columnIndex) {
		String result = "";
		Template template = (Template) element;
		switch (columnIndex) {
			case 0:  // class name
				result = template.getClassName();
				break;
			case 1 :
				break;
			default :
				break;
		}
		return result;
	}

}

