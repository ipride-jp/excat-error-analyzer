package jp.co.ipride.excat.common.action;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.dialog.SourceUpdateDialog;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.sourceupdate.SourceCodeUpdater;
import jp.co.ipride.excat.common.sourceupdate.SvnSourceCodeUpdater;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * 僜乕僗峏怴
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/17
 */
public class SourceUpdateAction extends BaseAction {

	/**
	 *
	 * @param appWindow
	 */
	public SourceUpdateAction(MainViewer appWindow) {
		super(appWindow);
		try {
			String text = ApplicationResource
					.getResource("Menu.Tools.SourceUpdate.Text");
			setText(text);
			//modified by Qiu Song on 20091215 for ツールバーの表示改善
			String toolTipText = ApplicationResource
			.getResource("Menu.Tools.SourceUpdate.ToolTip");
			this.setToolTipText(toolTipText);
            //end of modified by Qiu Song on 20091215 for ツールバーの表示改善

			URL url = MainViewer.class
					.getResource(IconFilePathConstant.SOURCE_UPDATE);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
	    	HelperFunc.logException(e);
		}
	}

	public void doJob() {
		boolean ret = ExcatMessageUtilty.showConfirmDialogBox(
				appWindow.getShell(),
				Message.get("SourceUpdateDialog.ConfirmUpdateSetting"));

		if (!ret){
			return;
		}
		SettingManager.update(appWindow.getShell(), true);
		// 忈奞 #531
		appWindow.analyzerform.sourceViewerPlatform.refreshAll();
//		SourceCodeUpdater sourceCodeUpdater = new SvnSourceCodeUpdater();
//		SourceUpdateDialog dialog = new SourceUpdateDialog(
//				this.appWindow.getShell(),
//				sourceCodeUpdater);
//		dialog.open();
		return;
	}
}