package jp.co.ipride.excat.analyzer.action;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;

/**
 * update dump data viewer.
 * this is active when the file viewer is active.
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/5
 */
public class ReflashFileViewerAction extends BaseAction{

	public ReflashFileViewerAction(MainViewer appWindow) {
		super(appWindow);
		String text = ApplicationResource.getResource("Menu.View.Refresh.Menu.Text");
		setText(text);
		//modified by Qiu Song on 20091215 for ツールバーの表示改善
		String toolTipText = ApplicationResource
		.getResource("Menu.View.Refresh.Menu.ToolTip");
		this.setToolTipText(toolTipText);
        //end of modified by Qiu Song on 20091215 for ツールバーの表示改善
		URL url = MainViewer.class.getResource(IconFilePathConstant.UPDATE_FILE_VIEW);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
	}

	@Override
	public void doJob() throws Throwable {
		((MainViewer)appWindow).updateFileViewer();
	}

}
