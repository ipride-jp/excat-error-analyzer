package jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare;

/**
 * 変数/クラス内フィールドのの宣言を開く
 * @author jiang
 *
 */
public class VarFieldDeclarePlace extends BaseDeclarePlace 
    implements IGoToDeclare{

	/**
	 * Constructor
	 * @param typeName
	 * @param startPosition
	 * @param length
	 */
	public VarFieldDeclarePlace(String typeName,int startPosition,
			int length){
		this.typeName = typeName;
		this.startPosition = startPosition;
		this.length = length;
	}
	
	/**
	 * 要素を宣言するソース／バイトコードを開く
	 * @throws SourceNotFoundException
	 */
	public void gotoDeclarePlace() throws SourceNotFoundException,
	    NoClassInPathException{
        	gotoDeclareInside(startPosition,length);
	}
}
