/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.viewer.localviewer.VariableTable;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;

/**
 * 抽象ソースビューア
 *
 * @author 屠偉新
 * @since 2006/9/17
 */
public abstract class AbstractViewer {
	// ビューア・タイプ
	public final static int JAVA_SOURCE = 0;
	public final static int BYTE_CODE = 1;
	public final static int SOURCE_REPOSITORY = 2;
	public final static int DUMMY = 4;

	// SourceViewerPlatformの属性
	protected CTabItem item = null;
	// table folder
//	protected CTabFolder tabFolder = null;
	// ローカル変数ビューア
	protected VariableTable variableTable = null;
	// 親form
	protected SashForm parent = null;
	// window
	protected MainViewer appWindow = null;
	// 対応のメソッド情報
	protected MethodInfo methodInfo = null;
	// ビューア・タイプ
	protected int type;
	// ビューアのソースパース
	protected String sourcePath = null;

	//key of tab items
	private String tabKey = null;


	/**
	 * ソースビューア作成
	 */
	protected abstract void createSrcViewer();

	/**
	 * ビューア・タイプを取得
	 *
	 * @return
	 */
	public abstract int getType();

	/**
	 * 選択ラインを描く
	 *
	 * @param lineNum
	 */
	public abstract void highlight(int lineNum);

	/**
	 * 選択ラインをクリア
	 *
	 */
	public abstract void clearSelectLine();

	/**
	 * 実行される行を表示するようにViewerをScrollする
	 *
	 */
	public abstract void showCalledPlace(MethodInfo methodInfo);

	/**
	 * クラス/メソッドを宣言する箇所を表示する。
	 */
	public void showDeclaredPlace(MethodInfo methodInfo){

	}

	/**
	 * フィールドを宣言する箇所を表示する。
	 * @param matchField
	 * @version 3.0
	 * @date 2009/10/28
	 * @author tu-ipride
	 */
	public void showDeclaredPlace(MatchField matchField){

	}

	/**
	 * 該当ビューアの識別
	 *
	 * @param methodInfo
	 * @return
	 */
	public boolean identify(MethodInfo methodInfo) {
		return this.methodInfo.identify(methodInfo.getNode());
	}

	/**
	 *
	 * @return
	 */
	public CTabItem getTabItem() {
		return item;
	}

	public String getSourcePath() {
		return sourcePath;
	}

	public void setSourcePath(String sourcePath) {
		this.sourcePath = sourcePath;
	}

	/**
	 * 該当ビューアのメソッド情報を取得
	 *
	 * @return
	 */
	public MethodInfo getMethodInfo() {
		return methodInfo;
	}

	/**
	 * add a listner to tree.
	 *
	 * @param listener
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {

	}

	public String getTabKey() {
		return tabKey;
	}

	public void setTabKey(String tabKey) {
		this.tabKey = tabKey;
	}
}
