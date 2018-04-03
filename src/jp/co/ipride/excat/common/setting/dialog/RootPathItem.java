/*
 * Error Analyzer Tool for Java
 * 
 * Created on 2007/10/9
 * 
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common.setting.dialog;

import java.io.Serializable;

import jp.co.ipride.excat.common.utility.HelperFunc;

/**
 * ルートパス設定アイテム
 * 
 * @author tatebayashiy
 * 
 */
public class RootPathItem implements Serializable {

	/** シリアライズバージョンID */
	private static final long serialVersionUID = 1L;

	/** ルートパスのユーザ定義名称 */
	private String name;

	/** ディレクトリパス文字列(絶対パス) */
	private String path;

	/**
	 * ルートパスのユーザ定義名称を取得します。
	 * 
	 * @return ユーザ定義名称
	 */
	public String getName() {
		return name;
	}

	/**
	 * ルートパスのユーザ定義名称を設定します。
	 * 
	 * @param name
	 *            ユーザ定義名称
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * ディレクトリパス文字列(絶対パス)を取得します。
	 * 
	 * @return ディレクトリパス文字列(絶対パス)
	 */
	public String getPath() {
		return path;
	}

	/**
	 * ディレクトリパス文字列(絶対パス)を設定ます。
	 * 
	 * @param path
	 *            ディレクトリパス文字列(絶対パス)
	 */
	public void setPath(String path) {
		this.path = path;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof RootPathItem)) {
			return false;
		}
		
		RootPathItem other = (RootPathItem) obj;
		return HelperFunc.compareObject(name, other.name)
				&& HelperFunc.compareObject(path, other.path);
	}
}
