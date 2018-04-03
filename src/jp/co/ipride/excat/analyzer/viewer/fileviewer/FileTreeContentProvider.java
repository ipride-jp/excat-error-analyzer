/*
 * Error Analyzer Tool for Java
 * 
 * Created on 2007/10/9
 * 
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.fileviewer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 * ファイルツリーの内容を取得処理プロバイダ
 * 
 * @author tatebayashiy
 * 
 */
public class FileTreeContentProvider implements ITreeContentProvider {

	/**
	 * 子ノードの一覧を取得します。
	 */
	public Object[] getChildren(Object element) {
		return ((BaseFileTreeItem) element).getChildren();
	}

	/**
	 * 親ノードを取得します。
	 */
	public Object getParent(Object element) {
		return ((BaseFileTreeItem) element).getParent();
	}

	/**
	 * 子ノードを持つかどうかを返却します。
	 */
	public boolean hasChildren(Object element) {
		return getChildren(element).length > 0;
	}

	/**
	 * 子ノードの一覧を取得します。
	 */
	public Object[] getElements(Object element) {
		return getChildren(element);
	}

	/**
	 * 未実装
	 */
	public void dispose() {

	}

	/**
	 * 未実装
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
