package jp.co.ipride.excat.common.clipboard;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MethodInfo;

/**
 * 一時格納するオブジェクト
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/26
 */
public class ExcatTemplate {

	private static MethodInfo methodInfo = null;

	/**
	 * 取得して該当オブジェクトを削除する。
	 * @return
	 */
	public static MethodInfo getMethodInfo() {
		MethodInfo m = methodInfo;
		methodInfo = null;
		return m;
	}

	/**
	 * オブジェクトをセットする。
	 * @param methodInfo
	 */
	public static void setMethodInfo(MethodInfo methodInfo) {
		ExcatTemplate.methodInfo = methodInfo;
	}



}
