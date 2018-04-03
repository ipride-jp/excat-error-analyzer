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
 * 検索提供元インタフェース
 * 
 * 検索を提供するクラスは本インタフェースを実装する。
 * 
 * @author tatebayashi
 * 
 */
public interface IFindProvider {
	/**
	 * 検索実行
	 * 
	 * @param condition
	 *            検索条件
	 * @return <code>true</code>:見つかった場合、<code>false</code>見つからなかった場合
	 */
	public boolean find(FindCondition condition);
}
