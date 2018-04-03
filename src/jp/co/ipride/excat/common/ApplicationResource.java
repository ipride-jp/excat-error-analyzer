/*
 * Error Anaylzer Tool for Java
 * 
 * Created on 2006/4/1
 * 
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * ApplicationResource取得する
 * 
 * @author GuanXH
 */
public class ApplicationResource {
	// リソース
	private static final ResourceBundle applicationResource;
	// プロパティを取得
	static {
		applicationResource = ResourceBundle.getBundle("ApplicationResource",
				Locale.getDefault());
	}

	private ApplicationResource() {
	}

	public static String getResource(String name) {
		String str = applicationResource.getString(name);
		return str;
	}
}
