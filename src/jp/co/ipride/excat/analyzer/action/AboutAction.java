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
import jp.co.ipride.excat.common.dialog.AboutDialog;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * 僿儖僾丒傾僋僔儑儞
 * @author GuanXH
 * @since 2009/9/17
 */
public class AboutAction extends BaseAction {

	/**
	 * @param appWindow
	 */
	public AboutAction(MainViewer appWindow) {
		super(appWindow);
		try{
			String text = ApplicationResource.getResource("Menu.Help.About.Text");
			setText(text);
			//modified by Qiu Song on 20091215 for ツールバーの表示改善
			String toolTipText = ApplicationResource
			.getResource("Menu.Help.About.ToolTip");
			this.setToolTipText(toolTipText);
            //end of modified by Qiu Song on 20091215 for ツールバーの表示改善

			URL url = MainViewer.class.getResource(IconFilePathConstant.EXCAT_TM_SMALL);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		}catch(Exception e){
			HelperFunc.getLogger().error("AboutAction", e);
		}
	}



	/**
	 * 傾僋僔儑儞帪偺張棟
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void doJob() {
		AboutDialog dialog = new AboutDialog(appWindow.getShell());
		dialog.open();
	}
}
