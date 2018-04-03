package jp.co.ipride.excat.common.action;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.clipboard.ExcatClipboard;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;

public class CopyAction extends BaseAction{

	public CopyAction(MainViewer appWindow) {
		super(appWindow);
		String text = ApplicationResource
		.getResource("Menu.edit.copy");
		setText(text);
		//modified by Qiu Song on 20091215 for ツールバーの表示改善
		String toolTipText = ApplicationResource
		.getResource("Menu.edit.copy.ToolTip");
		this.setToolTipText(toolTipText);
        //end of modified by Qiu Song on 20091215 for ツールバーの表示改善
		URL url = MainViewer.class.getResource(IconFilePathConstant.COPY);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
		ExcatClipboard.setCopyAction(this);
	}

	@Override
	public void doJob() {
		ExcatClipboard.copy();
	}

}
