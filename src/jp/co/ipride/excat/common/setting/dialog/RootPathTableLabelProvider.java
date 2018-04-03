/*
 * Error Analyzer Tool for Java
 * 
 * Created on 2007/10/9
 * 
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common.setting.dialog;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * ルートパステーブルのラベルプロバイダ
 * 
 * @author tatebayashiy
 * 
 */
public class RootPathTableLabelProvider implements ITableLabelProvider {

	/**
	 * イメージを取得します。 ルートパステーブルの設定ではイメージは指定しません。
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnImage()
	 */
	public Image getColumnImage(Object element, int columnNum) {
		return null;
	}

	/**
	 * テキストを取得します。
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#getColumnText()
	 */
	public String getColumnText(Object element, int columnNum) {
		RootPathItem item = (RootPathItem) element;
		switch (columnNum) {
		case 0:
			return item.getName();
		case 1:
			return item.getPath();
		default:
			return null;
		}
	}

	/**
	 * 未実装
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#addListener()
	 */
	public void addListener(ILabelProviderListener listener) {

	}

	/**
	 * 未実装
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#dispose()
	 */
	public void dispose() {
	}

	/**
	 * 未実装
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#isLabelProperty()
	 */
	public boolean isLabelProperty(Object element, String columnNum) {
		return false;
	}

	/**
	 * 未実装
	 * 
	 * @see org.eclipse.jface.viewers.ITableLabelProvider#removeListener()
	 */
	public void removeListener(ILabelProviderListener listener) {
	}

}
