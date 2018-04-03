package jp.co.ipride.excat.configeditor.viewer.action;

import java.net.URL;

//import jp.co.ipride.excat.configeditor.model.ConfigModel;
//import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.action.BaseAction;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.window.ApplicationWindow;

/**
 *
 * @author tu
 * @since 2007/11/10
 *
 */
public class ExitAction extends BaseAction{

	public ExitAction(MainViewer appWindow){
		super(appWindow);
		setToolTipText(ApplicationResource.getResource("Menu.File.Exit.Text"));
		setText(ApplicationResource.getResource("Menu.File.Exit.Text"));
		URL url = MainViewer.class.getResource("config/close.png");
		setImageDescriptor(ImageDescriptor.createFromURL(url));
	}

	public void doJob() {
		//arthur_TODO
		/*
		int n = ViewerUtil.reloadConfig(appWindow);
		switch(n){
		case -1:
			//•Ï‰»‚È‚µ
			appWindow.close();
			break;
		case 0:
			//•Û‘¶‚·‚é
			if (!((ConfigEditorApp)appWindow).checkItems()){
				return;
			}
			if (ConfigModel.isNewConfig()){
				FileDialog saveDialog = new FileDialog(appWindow.getShell(),SWT.SAVE);
				saveDialog.setFilterExtensions(new String[]{"*.config"});
				String saveFile = saveDialog.open();
				if (saveFile != null){
					ConfigModel.saveAsNewConfig(saveFile);
					ViewerUtil.showInfoMessageDialog("Tool.Save.Text");
					appWindow.close();
				}
			}else{
				ConfigModel.update();
				ViewerUtil.showInfoMessageDialog("Tool.Update.Text");
				appWindow.close();
			}
			break;
		case 1:
			//•Û‘¶‚¹‚¸
			appWindow.close();
			break;
		case 2:
			//ƒLƒƒƒ“ƒZƒ‹
		}
		 */
	}
}
