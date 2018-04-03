package jp.co.ipride.excat.common.action;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.clipboard.ExcatClipboard;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;

/**
 * paste action
 * @author tu-ipride
 * @version 3.0
 * @since 2009/10/3
 */
public class PasteAction extends BaseAction{

	public PasteAction(MainViewer appWindow) {
		super(appWindow);
		String text = ApplicationResource
		.getResource("Menu.edit.paste");
		setText(text);
		//modified by Qiu Song on 20091215 for ツールバーの表示改善
		String toolTipText = ApplicationResource
		.getResource("Menu.edit.paste.ToolTip");
		this.setToolTipText(toolTipText);
        //end of modified by Qiu Song on 20091215 for ツールバーの表示改善
		URL url = MainViewer.class.getResource(IconFilePathConstant.PASTE);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
		ExcatClipboard.setPasteAction(this);
	}

	@Override
	public void doJob() {
		ExcatClipboard.paste();
	}

}
