/*
 * Error Analyzer Tool for Java
 * 
 * Created on 2007/10/9
 * 
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.fileviewer;

import org.eclipse.jface.viewers.IElementComparer;

/**
 * ファイルツリーアイテムの一致比較ロジックを提供するクラス
 * 
 * @author tatebayashiy
 * 
 */
public class FileTreeItemComparer implements IElementComparer {

	/**
	 * 一致比較を行います。 ファイルパスが同一の場合、一致するとみなされます。 (ただし、ルートアイテムの場合は、名称が一致すれば同一と判断します。)
	 */
	public boolean equals(Object element1, Object element2) {
		// nullの比較は一致と判断
		if (element1 == null && element2 == null) {
			return true;
		}
		// nullと非nullの比較は不一致とする
		if (element1 == null && element2 != null) {
			return false;
		}
		if (element1 != null && element2 == null) {
			return false;
		}
		// インスタンス不一致は不一致と判断
		if (!element1.getClass().equals(element2.getClass())) {
			return false;
		}

		if (element1 instanceof FileTreeRootItem) {
			// アイテムがFileTreeRootItemの場合は、内部に保持の名前で一致確認を行う。
			FileTreeRootItem rootItem1 = (FileTreeRootItem) element1;
			FileTreeRootItem rootItem2 = (FileTreeRootItem) element2;

			return rootItem1.getPathName().equals(rootItem2.getPathName());
		}

		// その他は内部に保持しているFileオブジェクトの一致にて判断
		BaseFileTreeItem item1 = (BaseFileTreeItem) element1;
		BaseFileTreeItem item2 = (BaseFileTreeItem) element2;
		if (item1.getFile() == null && item2.getFile() == null) {
			return true;
		}
		if (item1.getFile() == null && item2.getFile() != null) {
			return false;
		}
		if (item1.getFile() != null && item2.getFile() == null) {
			return false;
		}

		// 内部に保持しているFileオブジェクトにて等価比較します。
		return item1.getFile().equals(item2.getFile());
	}

	/**
	 * ハッシュコードを取得します。 ファイルパスが同一の場合、同じハッシュコードを返します。
	 * (ルートアイテムの場合は、名称のハッシュコードを返します。)
	 */
	public int hashCode(Object element) {
		// パスが等しければ同じハッシュコードを返す
		if (element == null)
			return 0;
		BaseFileTreeItem item = (BaseFileTreeItem) element;
		if (item.getFile() == null)
			return 0;
		if (element instanceof FileTreeRootItem) {
			FileTreeRootItem rootItem = (FileTreeRootItem) element;
			// FileTreeRootItemの場合は名称のハッシュコードを返します。
			return rootItem.getPathName().hashCode();
		}
		return item.getFile().hashCode();
	}

}
