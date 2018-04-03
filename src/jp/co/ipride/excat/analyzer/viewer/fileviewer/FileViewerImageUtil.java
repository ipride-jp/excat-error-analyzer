/*
 * Error Analyzer Tool for Java
 * 
 * Created on 2007/10/9
 * 
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.fileviewer;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;

/**
 * ファイルビューで利用するイメージ用のユーティリティクラスです。
 * 
 * @author tatebayashiy
 * 
 */
public class FileViewerImageUtil {
	private static ImageRegistry imageRegistry;

	public final static String IMGID_DRIVE = "DRIVE";
	public final static String IMGID_FOLDER = "FOLDER";
	public final static String IMGID_FILE = "FILE";

	/**
	 * ファイルビューワに利用するイメージが格納された、 ImageRegistryを取得します。
	 * 
	 * @return
	 */
	public static ImageRegistry getImageRegistry() {
		if (imageRegistry == null) {
			imageRegistry = new ImageRegistry();
			imageRegistry.put(IMGID_DRIVE, ImageDescriptor
					.createFromURL(MainViewer.class
							.getResource(IconFilePathConstant.FILE_TREE_DRIVE)));
			imageRegistry.put(IMGID_FOLDER, ImageDescriptor
					.createFromURL(MainViewer.class
							.getResource(IconFilePathConstant.FILE_TREE_FORDER)));
			imageRegistry.put(IMGID_FILE, ImageDescriptor
					.createFromURL(MainViewer.class
							.getResource(IconFilePathConstant.FILE_TREE_FILE)));
		}
		return imageRegistry;
	}
}
