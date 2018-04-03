/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.action;

import java.io.File;
import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * 僟儞僾僼傽僀儖傪嶍彍偡傞傾僋僔儑儞
 *
 * @author sai
 * @since 2009/11/26
 */
public class DeleteFileAction extends BaseAction {

	private File[] files = null;

	public DeleteFileAction(MainViewer appWindow) {
		super(appWindow);
		try {
			String text = ApplicationResource
					.getResource("Menu.View.Delete.Menu.Text");
			setText(text);
			//modified by Qiu Song on 20091215 for ツールバーの表示改善
			String toolTipText = ApplicationResource
			.getResource("Menu.View.Delete.Menu.ToolTip");
			this.setToolTipText(toolTipText);
            //end of modified by Qiu Song on 20091215 for ツールバーの表示改善

			URL url = MainViewer.class.getResource(IconFilePathConstant.DELETE);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Throwable e) {
			HelperFunc.getLogger().error("DeleteFileAction", e);
		}
	}

	/**
	 * 僟儞僾僼傽僀儖傪嶍彍偡傞
	 *
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void doJob() {
		try {
			boolean ret = ExcatMessageUtilty.showConfirmDialogBox(
					this.appWindow.getShell(),
					Message.get("Menu.View.Delete.Confirm"));
			if (ret && files != null){
				for (int i = 0; i < files.length; i++) {
					File file = files[i];
					if (file.exists()) {
						deleteFile(file);
					}
				}
				appWindow.deleteFileAction.setFile(null);
				appWindow.deleteFileAction.setEnabled(false);
			}

		} catch (Exception e) {
			HelperFunc.getLogger().debug(e);
			ExcatMessageUtilty.showMessage(this.appWindow.getShell(),e.getMessage());
		} finally {
			appWindow.updateFileViewer();
		}
	}

	public void setFile(File[] files) {
		this.files = files;
	}

	private void deleteFile(File file) throws Exception {
		File[] files = file.listFiles();
		if (files != null) {
			for (int i = 0; i < files.length; i++) {
				File subFile = files[i];
				if (subFile.isDirectory()) {
					deleteFile(subFile);
				}

				if (subFile.isFile()) {
					if(!subFile.delete()){
	                    throw new Exception(
	                    		String.format(Message.get("Menu.View.Delete.Failed"), subFile.getAbsolutePath()));
	                }
				}
			}
		}
		if(!file.delete()){
            throw new Exception(
            		String.format(Message.get("Menu.View.Delete.Failed"), file.getAbsolutePath()));
        }
	}
}
