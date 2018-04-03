package jp.co.ipride.excat.analyzer.action;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * close all of pages
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/8
 */
public class CloseAllEdition extends BaseAction {

	private MainViewer window;

	public CloseAllEdition(MainViewer appWindow) {
		super(appWindow);
		window = appWindow;
		try{
			String text = ApplicationResource.getResource("Menu.File.CloseAllEditor.Text");
			setText(text);
			this.setToolTipText(text);

			URL url = MainViewer.class.getResource(IconFilePathConstant.CLOSE_ALL);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		}catch(Exception e){
			HelperFunc.getLogger().error("CloseAllEdition", e);
		}
	}

	public void doJob() {
		if (window instanceof MainViewer){
			((MainViewer)window).closeAllEditors();
		}
	}
}
