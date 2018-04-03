/*
 * Error Analyzer Tool for Java
 *
 * Created on 2007/10/9
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.action.finder;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;

/**
 * 検索アクション
 *
 * @author tatebayashi
 *
 */
public class FindAction extends BaseFindAction {

	/**
	 * コンストラクタ
	 *
	 * @param appWindow
	 */
	public FindAction(MainViewer appWindow) {
		super(appWindow);
		try {
			String text = ApplicationResource
					.getResource("Menu.edit.find.Text");
			setText(text);
			String toolTipText = ApplicationResource
					.getResource("Menu.edit.find.ToolTipText");
			this.setToolTipText(toolTipText);
			this.setAccelerator(SWT.CTRL | 'F');

			URL url = MainViewer.class
					.getResource(IconFilePathConstant.FIND_ENABLE);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
			url = MainViewer.class
					.getResource(IconFilePathConstant.FIND_DISABLE);
			setDisabledImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
	    	HelperFunc.logException(e);
		}
	}

	/**
	 * アクションによって呼ばれる
	 *
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void doJob() throws Throwable {
		openFindDialog();
	}
}
