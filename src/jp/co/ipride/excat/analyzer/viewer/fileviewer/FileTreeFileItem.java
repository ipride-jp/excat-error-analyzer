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
 * ファイルまたはディレクトリを表わすノードのアイテム
 * 
 * @author tatebayashiy
 * 
 */
public class FileTreeFileItem extends BaseFileTreeItem {

	/** 親ノードのアイテム */
	private BaseFileTreeItem parent;

	/**
	 * コンストラクタ
	 * 
	 * @param file
	 *            ファイル
	 * @param parent
	 *            親ノードアイテム
	 */
	public FileTreeFileItem(File file, BaseFileTreeItem parent) {
		super(file);
		this.parent = parent;
	}

	/**
	 * @see jp.co.ipride.excat.analyzer.viewer.fileviewer.BaseFileTreeItem#getChildren()
	 */
	public BaseFileTreeItem[] getChildren() {
		File[] files = file.listFiles();

		if (files == null) {
			return new BaseFileTreeItem[0];
		} else {
			FileTreeFileItem[] children = new FileTreeFileItem[files.length];
			for (int i = 0; i < children.length; i++) {
				children[i] = new FileTreeFileItem(files[i], this);
			}
			return children;
		}
	}

	/**
	 * @see jp.co.ipride.excat.analyzer.viewer.fileviewer.BaseFileTreeItem#getParent()
	 */
	public BaseFileTreeItem getParent() {
		return parent;
	}

	/**
	 * @see jp.co.ipride.excat.analyzer.viewer.fileviewer.BaseFileTreeItem#getText()
	 */
	public String getText() {
		return file.getName();
	}

}
