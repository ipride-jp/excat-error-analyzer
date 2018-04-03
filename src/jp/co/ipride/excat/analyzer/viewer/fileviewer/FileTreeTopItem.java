/*
 * Error Analyzer Tool for Java
 *
 * Created on 2007/10/9
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.fileviewer;

import java.util.ArrayList;

import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.setting.dialog.RootPathItem;

/**
 * ファイルビューのダミー用ルートノードです。
 *
 * 本アイテムはファイルツリーには表示されません。
 *
 * @author tatebayashi
 *
 */
public class FileTreeTopItem extends BaseFileTreeItem {

	/**
	 * コンストラクタ
	 */
	public FileTreeTopItem() {
		super(null);
	}

	/**
	 * @see jp.co.ipride.excat.analyzer.viewer.fileviewer.BaseFileTreeItem#getChildren()
	 */
	public BaseFileTreeItem[] getChildren() {
		ArrayList<RootPathItem> rootPathList = SettingManager.getSetting().getRootPathList();
		FileTreeRootItem[] rootItems = new FileTreeRootItem[rootPathList.size()];
		for (int i = 0; i < rootPathList.size(); i++) {
			RootPathItem item = rootPathList.get(i);
			rootItems[i] = new FileTreeRootItem(item);
		}
		return rootItems;
	}

	/**
	 * @see jp.co.ipride.excat.analyzer.viewer.fileviewer.BaseFileTreeItem#getParent()
	 */
	public BaseFileTreeItem getParent() {
		return null;
	}

	/**
	 * @see jp.co.ipride.excat.analyzer.viewer.fileviewer.BaseFileTreeItem#getText()
	 */
	public String getText() {
		return "";
	}

}
