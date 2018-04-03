/**
 *
 */
package jp.co.ipride.excat.configeditor.viewer.action;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

//import jp.co.ipride.excat.configeditor.model.ConfigModel;
//import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

/**
 * 既存コンフィグ・ファイルを開く
 *
 * @author tu
 * @since 2007/11/10
 */
public class OpenConfigFileAction extends BaseAction{

	public OpenConfigFileAction(MainViewer appWindow){
		super(appWindow);
		setToolTipText(ApplicationResource.getResource("Menu.File.Open.ToolTip"));
		setText(ApplicationResource.getResource("Menu.File.Open.Text"));
		URL url = MainViewer.class.getResource(IconFilePathConstant.CONFIG_OPEN);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
	}

	/**
	 * 既存コンフィグ・ファイルを開く
	 * @throws Exception
	 */
	public void doJob() {
		int n = ViewerUtil.reloadConfig(appWindow);
		switch(n){
		case -2:
			//変化なし
			openFile();
			break;
		case 0:
			//保存する
			if (!((MainViewer)appWindow).checkItems()){
				return;
			}
			if (ConfigModel.isNewConfig()){
				FileDialog saveDialog = new FileDialog(appWindow.getShell(),SWT.SAVE);
				saveDialog.setFilterExtensions(new String[]{"*.config"});
				String saveFile = saveDialog.open();
				if (saveFile != null){
					ConfigModel.saveAsNewConfig(saveFile);
					ExcatMessageUtilty.showMessage(
							this.appWindow.getShell(),
							Message.get("Tool.Save.Text"));
					openFile();
				}
			}else{
				if (ConfigModel.update()) {
					ExcatMessageUtilty.showMessage(
							this.appWindow.getShell(),
							Message.get("Tool.Update.Text"));
					openFile();
				}
			}
			break;
		case 2:
			//保存せず
			openFile();
			break;
		case 1:
			//キャンセル
		}
	}

	private void openFile(){
		ConfigModel.setFileOpening(true);
		FileDialog openDialog = new FileDialog(appWindow.getShell(),SWT.OPEN);
		openDialog.setFilterExtensions(new String[]{"*.config"});
		String file = openDialog.open();
		if (file != null){
			((MainViewer)appWindow).closeCurrentConfig();
			((MainViewer)appWindow).openOldConfig(file);
		}
		ConfigModel.setFileOpening(false);
	}
}
