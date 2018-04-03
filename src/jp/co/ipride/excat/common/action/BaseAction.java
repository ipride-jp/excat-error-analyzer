/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common.action;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.action.Action;

/**
 * ダイアローグのベースクラス
 *
 * @author GuanXH
 *
 */
public abstract class BaseAction extends Action {

	/**
	 * @uml.property name="appWindow"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	protected MainViewer appWindow;

	/**
	 * コンストラクタ
	 *
	 * @param appWindow
	 */
	public BaseAction(MainViewer appWindow) {
		this.appWindow = appWindow;
	}

	/**
	 * コンストラクタ
	 *
	 * @param appWindow
	 * @param text
	 * @param style
	 */
	public BaseAction(MainViewer appWindow, String text, int style) {
		super(text, style);
		this.appWindow = appWindow;
	}

	public void run() {
		try {
			doJob();
		} catch (Throwable e) {
	    	HelperFunc.logException(e);
		}
	}

	public abstract void doJob() throws Throwable;
}
