/*
 * Error Analyzer Tool for Java
 *
 * Created on 2007/10/9
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.action.finder;


import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.action.finder.FindDialog;
import jp.co.ipride.excat.common.action.BaseAction;

/**
 * 検索関連のアクションのベースクラス
 *
 * @author tatebayashi
 *
 */
public abstract class BaseFindAction extends BaseAction {

	/** 検索ダイアログ */
	protected FindDialog dialog;
	/** 検索提供元 */
	protected IFindProvider findProvider;
	/** 検索文字列提供元 */
	protected IFindStringProvider findStrProvider;

	/** コンストラクタ */
	public BaseFindAction(MainViewer appWindow) {
		super(appWindow);
		dialog = new FindDialog(appWindow.getShell());
	}

	/**
	 * 検索ダイアログを設定します。
	 *
	 * @param dialog
	 */
	public void setFindDialog(FindDialog dialog) {
		this.dialog = dialog;
	}

	/**
	 * 検索提供元を設定します。
	 *
	 * @param findProvider
	 */
	public void setFindProvider(IFindProvider findProvider) {
		this.findProvider = findProvider;
	}

	/**
	 * 検索文字列提供元を設定します。
	 *
	 * @param findStrProvider
	 */
	public void setFindStringProvider(IFindStringProvider findStrProvider) {
		this.findStrProvider = findStrProvider;
	}

	/**
	 * 検索ダイアログを表示します
	 *
	 * @param findProvider
	 * @param findStrProvider
	 */
	protected void openFindDialog() {
		dialog.setFindProvider(findProvider);
		String str = findStrProvider.getFindString();

		if (str != null && !"".equals(str)) {
			dialog.setFindString(str);
		}
		dialog.open();
	}

}
