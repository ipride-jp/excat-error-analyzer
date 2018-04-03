package jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.JavaSourceViewer;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.JavaSourceVisitor;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MatchField;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MethodInfo;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.SourceViewerPlatform;
import jp.co.ipride.excat.common.setting.repository.Repository;

/**
 * 宣言を開くするためのベースクラス
 * @author iPride_Demo
 *
 */
public class BaseDeclarePlace {

	/**
	 * visitor of java source file
	 */
	protected JavaSourceVisitor javaSourceVisitor = null;

	/**
	 * 元のソースファイルを表示するビュー
	 */
	protected JavaSourceViewer javaSourceViewer = null;

	/**
	 * type name
	 */
	protected String typeName = null;

	/**
	 * 選択された要素の開始位置
	 */
	protected int startPosition = 0;

	/**
	 * 選択された要素の長さ
	 */
	protected int length = 0;

	/**
	 * パッケージ名を取得
	 * @param fullClassName
	 * @return
	 */
	protected String getPackageName(String fullClassName){

		if(fullClassName == null){
			return null;
		}
		int pos = fullClassName.lastIndexOf('.');
		if(pos < 0){
			return null;
		}

		return fullClassName.substring(0,pos);
	}

	/**
	 * クラス名を取得
	 * @param fullClassName
	 * @return
	 */
	protected String getClassNameWithoudPackage(String fullClassName){

		if(fullClassName == null){
			return null;
		}
		int pos = fullClassName.lastIndexOf('.');
		if(pos < 0){
			return fullClassName;
		}

		return fullClassName.substring(pos + 1);
	}


	/**
	 * ソースファイル外にあるクラス若しくはメソッドの宣言を開く
	 * @param fullClassName
	 * @throws SourceNotFoundException
	 */
	protected void gotoClassDeclare(String fullClassName) {
		MethodInfo methodInfo = new MethodInfo();
		methodInfo.setClassSig(fullClassName);
		SourceViewerPlatform.getInstance().showDelcareViewer(methodInfo);
	}

	/**
	 * フィールドを宣言する箇所を表示する。
	 * もし、該当ｸﾗｽになければ、親クラスを探す。
	 * @param matchField
	 * @version 3.0
	 * @date 2009/10/28
	 * @author tu-ipride
	 */
	public void gotoFieldDeclare(MatchField matchField){
		SourceViewerPlatform.getInstance().showDelcareViewer(matchField);
	}

	/**
	 * ソースファイル内にある宣言を開く。
	 * @param classTypeInfo
	 */
	protected void gotoDeclareInside(int start,int length){

		int end = start + length;
		javaSourceViewer.setSelectionWithTrace(start, end);

	}

	/**
	 * Source viewerの設定
	 * @param sourceViewer
	 */
	public void setJavaSourceViewer(JavaSourceViewer javaSourceViewer){
		this.javaSourceViewer = javaSourceViewer;
		this.javaSourceVisitor = javaSourceViewer.getSourceVisitor();
	}

	public String getTypeName() {
		return typeName;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public int getLength() {
		return length;
	}
}
