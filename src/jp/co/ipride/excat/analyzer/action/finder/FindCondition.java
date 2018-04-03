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
 * 検索条件
 * 
 * @author tatebayashi
 * 
 */
public class FindCondition {
	/** 検索対象の文字列 */
	private String targetString;

	/** 検索方向 */
	private boolean forwardSearch;

	/** 大/小文字の区別 */
	private boolean caseSensitive;
	/** 循環検索 */
	private boolean circularSearch;
	/** 単語単位検索 */
	private boolean wordSearch;
	/** 正規表現利用 */
	private boolean regixSearch;

	/**
	 * コンストラクタ(文字列のみ指定)
	 * 
	 * @param targetStr
	 *            検索対象の文字列
	 */
	public FindCondition(String targetStr) {
		this(targetStr, true, false, false, false, false);
	}

	/**
	 * コンストラクタ(検索対象文字列＋検索条件を指定)
	 * 
	 * @param targetStr
	 * @param direction
	 * @param caseSensitive
	 * @param circularSearch
	 * @param wordSearch
	 * @param regixSearch
	 */
	public FindCondition(String targetStr, boolean forwardSearch,
			boolean caseSensitive, boolean circularSearch, boolean wordSearch,
			boolean regixSearch) {
		targetString = targetStr;
		this.forwardSearch = forwardSearch;
		this.caseSensitive = caseSensitive;
		this.circularSearch = circularSearch;
		this.wordSearch = wordSearch;
		this.regixSearch = regixSearch;
	}

	/**
	 * コンストラクタ(FindConditionの複製)
	 * 
	 * @param condition
	 */
	public FindCondition(FindCondition condition) {
		targetString = condition.getTargetString();
		forwardSearch = condition.isForwardSearch();
		caseSensitive = condition.isCaseSensitive();
		circularSearch = condition.isCircularSearch();
		wordSearch = condition.isWordSearch();
		regixSearch = condition.isRegixSearch();
	}

	/**
	 * 大/小文字を区別するかどうかを取得します。
	 * 
	 * @return
	 */
	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	/**
	 * 大/小文字を区別するかどうかを設定します。
	 */
	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	/**
	 * 循環検索を行うかどうかを取得します。
	 * 
	 * @return
	 */
	public boolean isCircularSearch() {
		return circularSearch;
	}

	/**
	 * 循環検索を行うかどうかを設定します。
	 */
	public void setCircularSearch(boolean search) {
		circularSearch = search;
	}

	/**
	 * 正規表現を利用して検索を行うかどうかを取得します。
	 * 
	 * @return
	 */
	public boolean isRegixSearch() {
		return regixSearch;
	}

	/**
	 * 正規表現を利用して検索を行うかどうかを設定します。
	 */
	public void setRegixSearch(boolean search) {
		regixSearch = search;
	}

	/**
	 * 検索対象文字列を取得します。
	 * 
	 * @return
	 */
	public String getTargetString() {
		return targetString;
	}

	/**
	 * 検索対象文字列を設定します。
	 * 
	 * @return
	 */
	public void setTargetString(String string) {
		targetString = string;
	}

	/**
	 * 検索方向が前方検索であるかどうかを取得します。
	 * 
	 * @return <code>true</code>：前方検索、<code>false</code>:後方検索
	 */
	public boolean isForwardSearch() {
		return forwardSearch;
	}

	/**
	 * 検索方向を設定します。
	 */
	public void setForwardSearch(boolean forwardSearch) {
		this.forwardSearch = forwardSearch;
	}

	/**
	 * 単語単位で検索するかどうかを取得します。
	 * 
	 * @return <code>true</code>:単語単位で検索する、<code>false</code>:単語単位で検索しない。
	 */
	public boolean isWordSearch() {
		return wordSearch;
	}

	/**
	 * 単語単位で検索するかどうかを設定します。
	 * 
	 * @param search
	 *            <code>true</code>:単語単位で検索する、<code>false</code>:単語単位で検索しない。
	 */
	public void setWordSearch(boolean search) {
		wordSearch = search;
	}
}
