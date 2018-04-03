/*
 * Error Analyzer Tool for Java
 *
 * Created on 2007/10/9
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.fileviewer;

import java.io.File;

/**
 * ダンプファイルビューワのツリーアイテムとして設定するアイテムの抽象クラス
 *
 * @author tatebayashiy
 *
 */
public abstract class BaseFileTreeItem {

	/** ファイルパスを格納するファイルオブジェクト */
	protected File file = null;

	/**
	 * コンストラクタ
	 *
	 * @param file
	 *            本アイテムを表わす対象ファイル
	 */
	public BaseFileTreeItem(File file) {
		this.file = file;
	}

	/**
	 * ファイルパスを格納したファイルオブジェクトを取得します。
	 *
	 * @return 本アイテムを表わす対象ファイルオブジェクト
	 */
	public File getFile() {
		return file;
	}

	/**
	 * 子ノードを取得します。
	 *
	 * @return
	 */
	public abstract BaseFileTreeItem[] getChildren();

	/**
	 * 親ノードを取得します。
	 *
	 * @return
	 */
	public abstract BaseFileTreeItem getParent();

	/**
	 * 表示テキストを取得します。
	 *
	 * @return
	 */
	public abstract String getText();

	public String getFilePath(){
		return file.getPath();
	}

}
