package jp.co.ipride.excat.configeditor.viewer;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.configeditor.model.task.ITask;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class configLabelProvider  implements ILabelProvider {

	public configLabelProvider(MainViewer appWindow) {

	}

	//@Override
	public Image getImage(Object arg0) {
		if (arg0 instanceof String){
			URL url = MainViewer.class.getResource(IconFilePathConstant.TREE_STACKTRACE);
			ImageDescriptor nid = ImageDescriptor.createFromURL(url);
			return nid.createImage();
		}else if (arg0 instanceof ITask){
			URL url;
			ITask task = (ITask)arg0;
			if (task.isEffect()){
				url = MainViewer.class.getResource(IconFilePathConstant.TASK_ON);
			}else{
				url = MainViewer.class.getResource(IconFilePathConstant.TASK_OFF);
			}
			ImageDescriptor nid = ImageDescriptor.createFromURL(url);
			return nid.createImage();
		}
		return null;
	}

	//@Override
	public String getText(Object arg0) {
		if (arg0 instanceof String){
			String id = (String)arg0;
			String name = ApplicationResource.getResource(id);
			return name;
		}else if (arg0 instanceof ITask){
			ITask task = (ITask)arg0;
			return task.getTaskName();
		}else{
			return "";
		}
	}

	//@Override
	public void addListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub
	}

	//@Override
	public void dispose() {
		// TODO Auto-generated method stub

	}

	//@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		// TODO Auto-generated method stub
		return false;
	}

	//@Override
	public void removeListener(ILabelProviderListener arg0) {
		// TODO Auto-generated method stub

	}

}
