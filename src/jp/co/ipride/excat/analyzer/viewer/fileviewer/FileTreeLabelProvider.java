/*
 * Error Analyzer Tool for Java
 * 
 * Created on 2007/10/9
 * 
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.fileviewer;

import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * ファイルビューのラベルプロバイダ
 * 
 * @author tatebayashiy
 * 
 */
public class FileTreeLabelProvider extends LabelProvider {

	/**
	 * 表示テキストを取得します。
	 */
	public String getText(Object element) {
		return ((BaseFileTreeItem) element).getText();
	}

	/**
	 * ラベルに追加するイメージを取得します。
	 */
	public Image getImage(Object element) {
		BaseFileTreeItem item = (BaseFileTreeItem) element;
		ImageRegistry imageRegistry = FileViewerImageUtil.getImageRegistry();
		if (item instanceof FileTreeRootItem) {
			return imageRegistry.get(FileViewerImageUtil.IMGID_DRIVE);
		}
		if (item instanceof FileTreeFileItem) {
			// ファイル、ディレクトリのみアイコン表示
			if (item.getFile().isFile()) {
				// ファイル
				return imageRegistry.get(FileViewerImageUtil.IMGID_FILE);
			} else if (item.getFile().isDirectory()) {
				// ディレクトリ
				return imageRegistry.get(FileViewerImageUtil.IMGID_FOLDER);
			} else {
				// その他
			}
		}
		return super.getImage(element);
	}

}
