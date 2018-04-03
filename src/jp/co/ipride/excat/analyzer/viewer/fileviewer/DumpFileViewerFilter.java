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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import jp.co.ipride.excat.common.setting.SettingManager;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

/**
 * ダンプファイルビューワのフィルター
 *
 * @author tatebayashiy
 *
 */
public class DumpFileViewerFilter extends ViewerFilter {

	/** 表示ファイルの拡張子 */
	// private static final String[] TARGET_EXTENTION = {".dat", ".DAT"};
	private String[] targetExtentions = { ".dat", ".zip"};

	private List<String> filteringList = new ArrayList<String>();

	/**
	 * フィルタリングのロジック。
	 *
	 * フォルダアイテムと、ダンプファイル用の拡張子(.dat)のファイルの アイテムのみが表示されるようにフィルタリングを行います。
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element == null)
			return false;

		boolean targeted = false;

		if (isDirectoryItem(element)) {
			if (isIncludesTargetString(element)) {
				targeted = true;
			} else {
				targeted = false;
			}
		} else if (isTargetExtentionFileItem(element)) {
			if (isIncludesTargetString(element)) {
				targeted = true;
			} else {
				targeted = false;
			}
		}

		return targeted;
	}

	/**
	 * ディレクトリを表わすアイテムかどうか判定します。
	 *
	 * @param element
	 * @return
	 */
	private boolean isDirectoryItem(Object element) {
		BaseFileTreeItem item = (BaseFileTreeItem) element;
		File file = item.getFile();

		if (item instanceof FileTreeTopItem) {
			return true;
		}

		if (item instanceof FileTreeRootItem) {
			return true;
		}

		if (file != null) {
			// ディレクトリの場合は表示する
			if (file.isDirectory()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 対象の拡張子を持つファイルアイテムであるかどうか判定します。
	 *
	 * @param element
	 * @return
	 */
	private boolean isTargetExtentionFileItem(Object element) {

		BaseFileTreeItem item = (BaseFileTreeItem) element;
		File file = item.getFile();

		String[] filterExtentions = SettingManager.getSetting().getFilterExtentions();
		if (filterExtentions != null) {
			targetExtentions = SettingManager.getSetting()
					.getFilterExtentions();
		}

		if (file != null) {
			if (file.isFile()) {
				// 対象の拡張子のみを対象とする。
				for (int i = 0; i < targetExtentions.length; i++) {
					if (file.getPath().toLowerCase().endsWith(targetExtentions[i])) {
						return true;
					}
				}
			}
		}

		return false;
	}

	/**
	 * 設定されている文字列をパス内に持っているかどうかを判定します。
	 *
	 * @param element
	 * @return
	 */
	private boolean isIncludesTargetString(Object element) {

		if (filteringList.size() > 0) {
			BaseFileTreeItem item = (BaseFileTreeItem) element;
			File file = item.getFile();
			if (file != null) {
				String path = file.getPath();

				for (Iterator<String> it = filteringList.iterator(); it.hasNext();) {
					String targetStr = (String) it.next();
					if (path.indexOf(targetStr) >= 0) {
						// 対象アイテムのパスに対象文字列を含む場合はtrueを返却します。
						return true;
					} else {
						// 対象アイテムの子ノード内に１つでも表示対象が存在する場合は、
						// 表示対象とする必要があるので、子ノードの探索も行います。
						BaseFileTreeItem[] children = item.getChildren();
						if (children != null) {
							for (int i = 0; i < children.length; i++) {
								if (isIncludesTargetString(children[i])) {
									return true;
								}
							}
						}
					}
				}
			}
		} else {
			// 未設定の場合は行いません。
			return true;
		}
		return false;
	}

	/**
	 * フィルタリングにて表示対象とするアイテムの文字列を追加します。
	 *
	 * @param str
	 */
	public void addFilteringString(String str) {
		filteringList.add(str);
	}

	/**
	 * フィルタリングにて表示対象とする文字列のリストを設定します。
	 *
	 * @param filterStringList
	 */
	public void setFilteringStringList(List<String> filterStringList) {
		filteringList = filterStringList;
	}

	/**
	 * 表示対象の拡張子の配列を設定します。
	 *
	 * @param extentions
	 */
	public void setFilteringExtentions(String[] extentions) {
		targetExtentions = extentions;
	}

}
