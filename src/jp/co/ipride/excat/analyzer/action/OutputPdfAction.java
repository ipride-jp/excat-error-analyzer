/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2007/10/05
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
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * PDFへの出力用アクションクラス
 * @version 1.0 2007/10/05
 * @author 公　続亮
 */
public class OutputPdfAction extends BaseAction {

	/**
	 * コンストラクタ
	 * @param appWindow
	 * @return なし
	 */
	public OutputPdfAction(MainViewer appWindow) {
		super(appWindow);

		try {
			String text = ApplicationResource.getResource("Menu.File.Pdf.Text");
			setText(text);
	        setEnabled(false);
			this.setToolTipText(text);

			URL url = MainViewer.class.getResource(IconFilePathConstant.PDF);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
			setDisabledImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			HelperFunc.getLogger().error("OutputPdfAction", e);
			ExcatMessageUtilty.showErrorMessage(this.appWindow.getShell(),e);
		}
	}

	/**
	 * アクションによって呼ばれる
	 * @param なし
	 * @return なし
	 * @throws Exception
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void doJob() throws Exception {
		try {
			((MainViewer) this.appWindow).outputToPdf(appWindow);
		} catch (Throwable e) {
			HelperFunc.getLogger().debug(e);
		}
	}
}