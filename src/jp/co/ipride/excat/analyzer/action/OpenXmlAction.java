/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.action;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;

/**
 * 僟儞僾僼傽僀儖傪慖戰偡傞僟僀傾儘乕僌
 *
 * @author 搄
 * @since 2006/9/17
 */
public class OpenXmlAction extends BaseAction {

	public OpenXmlAction(MainViewer appWindow) {
		super(appWindow);
		try {
			String text = ApplicationResource
					.getResource("Menu.File.OpenXml.Text");
			setText(text);
			
			//modified by Qiu Song on 20091215 for ツールバーの表示改善
			String toolTipText = ApplicationResource
			.getResource("Menu.File.OpenXml.ToolTip");
			this.setToolTipText(toolTipText);
			//end of modified by Qiu Song on 20091215 for ツールバーの表示改善

			URL url = MainViewer.class.getResource(IconFilePathConstant.OPEN);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Throwable e) {
			HelperFunc.getLogger().error("OpenXmlAction", e);
		}
	}

	/**
	 * 僟儞僾僼傽僀儖傪慖戰偡傞僟僀傾儘乕僌傪奐偔
	 *
	 * @throws Exception
	 *
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void doJob() throws Throwable {
		try {
			FileDialog dialog = new FileDialog(appWindow.getShell(), SWT.OPEN);
			String FileKind = ApplicationResource.getResource("FileOpenDialog.FileKind");
			//String FileKindZip = ApplicationResource.getResource("FileOpenDialog.FileKindZip");

			String[] exts = { "*.dat;*.zip"};
			String[] names = { FileKind};
			dialog.setFilterExtensions(exts);
			dialog.setFilterNames(names);
			dialog.setFilterPath(SettingManager.getSetting().getCurrentDumpFilePath());

			HelperFunc.getLogger().info(
					"Current path:" + SettingManager.getSetting().getCurrentDumpFilePath());

			String path = dialog.open();
			if (path == null) {
				return;
			}
			this.appWindow.openXmlFile(path);

		} catch (Throwable e) {
			HelperFunc.getLogger().debug(e);
			ExcatMessageUtilty.showErrorMessage(this.appWindow.getShell(),e);
			throw e;
		}
	}
}
