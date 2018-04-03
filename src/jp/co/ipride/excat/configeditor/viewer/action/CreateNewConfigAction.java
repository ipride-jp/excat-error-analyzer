/**
 *
 */
package jp.co.ipride.excat.configeditor.viewer.action;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

/**
 * 新規開く
 *
 * @author tu
 * @since 2007/11/10
 *
 */
public class CreateNewConfigAction extends BaseAction{

	public CreateNewConfigAction(MainViewer appWindow){
		super(appWindow);
		setToolTipText(ApplicationResource.getResource("Menu.File.New.ToolTip"));
		setText(ApplicationResource.getResource("Menu.File.New.Text"));
		URL url = MainViewer.class.getResource(IconFilePathConstant.CONFIG_NEW);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
	}

	public void doJob() {
			int n = ViewerUtil.reloadConfig(appWindow);
			switch(n){
			case -2:
				//変化なし
				appWindow.closeCurrentConfig();
				appWindow.createNewConfig();
				break;
			case 0:
				//保存する
				if (!appWindow.checkItems()){
					return;
				}
				if (ConfigModel.isNewConfig()){
					FileDialog saveDialog = new FileDialog(appWindow.getShell(),SWT.SAVE);
					saveDialog.setFilterExtensions(new String[]{"*.config"});
					String saveFile = saveDialog.open();
					if (saveFile != null){
						if (ConfigModel.saveAsNewConfig(saveFile)) {
							ExcatMessageUtilty.showMessage(
									this.appWindow.getShell(),
									Message.get("Tool.Save.Text"));
							appWindow.closeCurrentConfig();
							appWindow.createNewConfig();
						}
					}
				}else{
					if (ConfigModel.update()) {
						ExcatMessageUtilty.showMessage(
								this.appWindow.getShell(),
								Message.get("Tool.Update.Text"));
						appWindow.closeCurrentConfig();
						appWindow.createNewConfig();
					}
				}
				break;
			case 2:
				//保存せず
				appWindow.closeCurrentConfig();
				appWindow.createNewConfig();
				break;
			case 1:
				//キャンセル
			}

	}
}
