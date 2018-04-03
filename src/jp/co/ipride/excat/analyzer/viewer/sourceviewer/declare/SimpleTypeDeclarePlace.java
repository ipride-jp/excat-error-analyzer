package jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.ClassTypeInfo;


/**
 * Simple typeの宣言を開くクラス
 * @author jiang
 *
 */
public class SimpleTypeDeclarePlace extends BaseDeclarePlace
    implements IGoToDeclare{

	/**
	 * コンストラクタ
	 * @param nodeOfMySelf
	 */
	public SimpleTypeDeclarePlace(String typeName,int startPosition,int length){
		this.typeName = typeName;
		this.startPosition = startPosition;
		this.length = length;
	}


	/**
	 * 要素を宣言するソース／バイトコードを開く
	 * @throws SourceNotFoundException
	 */
	public void gotoDeclarePlace() throws SourceNotFoundException,NoClassInPathException{
		//同じソースファイルにあるかどうか
		ClassTypeInfo cu = super.javaSourceVisitor.getClassTypeInfoByName(
				typeName,startPosition,length);
		if(cu != null){
	        gotoDeclareInside(cu.getClassNameStart(), cu.getClassNameLength());
		}else{
			gotoClassDeclare(typeName);
		}

	}

}
