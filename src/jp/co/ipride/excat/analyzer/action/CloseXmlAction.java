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
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.apache.log4j.Logger;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * 僟儞僾僼傽僀儖廔椆帪偺傾僋僔儑儞
 *
 * @author GuanXH
 */
public class CloseXmlAction extends BaseAction {

	/**
	 * 僐儞僗僩儔僋僞
	 *
	 * @param appWindow
	 */
	public CloseXmlAction(MainViewer appWindow) {

		super(appWindow);

		try {
			String text = ApplicationResource
					.getResource("Menu.File.CloseXml.Text");
			setText(text);
			//modified by Qiu Song on 20091215 for ツールバーの表示改善
			String toolTipText = ApplicationResource
			.getResource("Menu.File.CloseXml.ToolTip");
			this.setToolTipText(toolTipText);
            //end of modified by Qiu Song on 20091215 for ツールバーの表示改善
			
			URL url = MainViewer.class.getResource(IconFilePathConstant.CLOSE);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			HelperFunc.getLogger().debug(e);
		}
	}

	/**
	 * 傾僋僔儑儞偵傛偭偰屇偽傟傞
	 *
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void doJob() {
		HelperFunc.getLogger().info(
				Message.get("CloseXmlAction.Start")	+ appWindow.fileName);
				appWindow.closeDumpFile();
		appWindow.fileName = "";
		HelperFunc.getLogger().info(
				Message.get("CloseXmlAction.End"));
	}
}
