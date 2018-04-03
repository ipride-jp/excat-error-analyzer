package jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MatchMethodInfo;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MethodInfo;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.SourceViewerPlatform;

/**
 * メソッドの宣言を開く機能を実装するクラス
 * @author iPride_Demo
 *
 */
public class MethodDeclarePlace extends BaseDeclarePlace
    implements IGoToDeclare{

	/**
	 * メソッド名
	 */
	private String methodName = null;

	/**
	 * メソッドSignature,Excat専用のフォーマット
	 */
	private String methodSignature = null;

	/**
	 * ソースのパス
	 */
	private String sourcePath = null;

	/**
	 * コンストラクタ
	 * @param fullTypeName
	 * @param mu
	 */
	public MethodDeclarePlace(String fullTypeName,MatchMethodInfo mu,
			String sourcePath){
		super.typeName = fullTypeName;
		super.startPosition = mu.getMethodNameStartPos();
		super.length = mu.getMethodNameOffet();
		this.methodName = mu.getMethodName();
		this.methodSignature = mu.getMethodSignature();
		this.sourcePath = sourcePath;
	}

	/**
	 * 要素を宣言するソース／バイトコードを開く
	 * @throws SourceNotFoundException
	 */
	public void gotoDeclarePlace() throws SourceNotFoundException,
	    NoClassInPathException{
		MethodInfo methodInfo = new MethodInfo();
		methodInfo.setClassSig(typeName);
		methodInfo.setSourceName(sourcePath);
		methodInfo.setMethodName(methodName);
		methodInfo.setStartPosition(super.startPosition);
		methodInfo.setOffset(super.length);
		methodInfo.setMethodSig(methodSignature);
		SourceViewerPlatform.getInstance().showDelcareViewer(methodInfo);
	}

	/**
	 * this is for define a task for config.
	 * @version 3.0
	 * @date 2009/10/26
	 * @return
	 */
	public MethodInfo getMethodInfo(){
		MethodInfo methodInfo = new MethodInfo();
		methodInfo.setClassSig(typeName);
		methodInfo.setSourceName(sourcePath);
		methodInfo.setMethodName(methodName);
		methodInfo.setStartPosition(super.startPosition);
		methodInfo.setOffset(super.length);
		methodInfo.setMethodSig(methodSignature);
		return methodInfo;
	}
}
