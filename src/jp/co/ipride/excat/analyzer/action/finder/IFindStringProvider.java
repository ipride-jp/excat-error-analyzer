/*
 * Error Analyzer Tool for Java
 * 
 * Created on 2007/10/9
 * 
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.action.finder;

/**
 * 検索文字列提供インタフェース
 * 
 * 検索文字列を提供するクラスは本インタフェースを実装する。
 * 
 * @author tatebayashi
 * 
 */
public interface IFindStringProvider {
	/**
	 * 検索対象文字列を取得します。
	 * 
	 * @return 検索対象文字列
	 */
	public String getFindString();
}
