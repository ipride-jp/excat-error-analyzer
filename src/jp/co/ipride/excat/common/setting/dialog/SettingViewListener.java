/*
 * Error Analyzer Tool for Java
 * 
 * Created on 2007/10/9
 * 
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common.setting.dialog;

/**
 * 設定ダイアログの設定ビューのクラスが実装すべき処理のインタフェースを提供します。
 * @author tatebayashiy
 *
 */
public interface SettingViewListener {
	/**
	 * 設定確定前の処理を実装します。
	 */
	public boolean preOkProcessed();
	/**
	 * 設定キャンセル前の処理を実装します。
	 */
	public boolean preCancelProcessed();
	/**
	 * 設定確定後の処理を実装します。
	 */
	public void postOkProcessed();
	/**
	 * 設定キャンセル後の処理を実装します。
	 */
	public void postCancelProcessed();
}
