package jp.co.ipride.excat.common.action;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;

/**
 * output setting object
 * @author tu-ipride
 * @version 3.0
 * @date 2009/9/18
 */
public class SettingOutputAction extends BaseAction{

	public SettingOutputAction(MainViewer appWindow) {
		super(appWindow);
		String text = ApplicationResource
			.getResource("Menu.Tools.SettingOutput.Text");
	setText(text);
	this.setToolTipText(text);
	URL url = MainViewer.class.getResource(IconFilePathConstant.SETTING_OUTPUT);
	setImageDescriptor(ImageDescriptor.createFromURL(url));
	}

	@Override
	public void doJob() throws Throwable {
	}

}
