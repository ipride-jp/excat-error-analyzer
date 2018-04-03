package jp.co.ipride.excat.configeditor.viewer.action;

import java.net.URL;

//import jp.co.ipride.excat.configeditor.util.ViewerUtil;
//import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;

/**
 * 上書保存
 * @author tu
 * @since 2007/11/10
 *
 */
public class UpdateConfigAction extends BaseAction
									implements SelectionListener{

	public UpdateConfigAction(MainViewer appWindow){
		super(appWindow);
		setToolTipText(ApplicationResource.getResource("Menu.File.Update.ToolTip"));
		setText(ApplicationResource.getResource("Menu.File.Update.Text"));
		URL url = MainViewer.class.getResource(IconFilePathConstant.CONFIG_UPDATE);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
		URL url1 = MainViewer.class.getResource(IconFilePathConstant.CONFIG_UPDATE_DISABLE);
		this.setDisabledImageDescriptor(ImageDescriptor.createFromURL(url1));
	}

	public void doJob(){
		boolean checkUI = ((MainViewer)appWindow).checkItems();
		if (!checkUI) {
			return;
		}
		boolean checkModel = ConfigModel.checkConfigModel();
		if (checkModel){
			if (ConfigModel.isNewConfig()){
				// 保存用ダイアログを開く
				FileDialog saveDialog = new FileDialog(appWindow.getShell(),SWT.SAVE);
				saveDialog.setFilterExtensions(new String[]{"*.config"});
				String saveFile = saveDialog.open();
				if (saveFile != null){
					if (ConfigModel.saveAsNewConfig(saveFile)) {
						MainViewer.displayFilePath();
						ExcatMessageUtilty.showMessage(
								this.appWindow.getShell(),
								Message.get("Tool.Save.Text"));
					}
				}
			}else{
				if (ConfigModel.update()) {
					ExcatMessageUtilty.showMessage(
							this.appWindow.getShell(),
							Message.get("Tool.Update.Text"));
				}
			}
		}
	}

	/**
	 * implement SelectionListener
	 * @param event
	 */
	public void widgetSelected(SelectionEvent event) {
		//arthur_TODO
		/*
		if (ConfigModel.hasDocument()){
			this.setEnabled(true);
		}else{
			this.setEnabled(false);
		}
		*/
	}
	/**
	 * no implement SelectionListener
	 * @param arg0
	 */
	public void widgetDefaultSelected(SelectionEvent arg0) {}
}
