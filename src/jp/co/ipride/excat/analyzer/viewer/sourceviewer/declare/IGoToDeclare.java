package jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.JavaSourceViewer;

/**
 * 選択された要素の宣言を開く
 * @author jiang
 *
 */
public interface IGoToDeclare {

	/**
	 * 要素を宣言するソース／バイトコードを開く
	 * @throws SourceNotFoundException
	 */
	public void gotoDeclarePlace() throws SourceNotFoundException,NoClassInPathException;
	
	/**
	 * Source viewerの設定
	 * @param sourceViewer
	 */
	public void setJavaSourceViewer(JavaSourceViewer javaSourceViewer);
}
