package jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare;

import java.util.List;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.ClassTypeInfo;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MatchField;
import jp.co.ipride.excat.common.setting.repository.Repository;

/**
 * クラスに所属するフィールドの宣言を開く
 * @author iPride_Demo
 *
 */
public class ThisFieldDeclarePlace extends BaseDeclarePlace
    implements IGoToDeclare{

	/**
	 *
	 * @param typeName
	 * @param startPosition
	 * @param length
	 */
	public ThisFieldDeclarePlace(String typeName,int startPosition,
			int length){
		this.typeName = typeName;
		this.startPosition = startPosition;
		this.length = length;
	}

	/**
	 * 要素を宣言するソース／バイトコードを開く
	 * @throws SourceNotFoundException
	 */
	public void gotoDeclarePlace() throws SourceNotFoundException, NoClassInPathException{
        MatchField field = javaSourceVisitor.getMatchedField(
        		startPosition,length,typeName);
        if(field != null){
        	gotoDeclareInside(field.getStartPosition(),field.getFieldLength());
        	return;
        }else{
        	//tu add 2009/10/28
        	List<ClassTypeInfo> classList = javaSourceVisitor.getClassList();
        	for (ClassTypeInfo classTypeInfo: classList){
        		String fullClassName = classTypeInfo.getFullClassName();
        		String superClassName = Repository.getInstance()
        								.getHasFieldClassName(fullClassName, typeName);
        		if (superClassName != null){
        			MatchField matchField = new MatchField();
        			matchField.setFullClassName(fullClassName);
        			matchField.setFieldName(typeName);
        			matchField.setStartPosition(startPosition);
        			matchField.setLength(length);
                	gotoFieldDeclare(matchField);
        		}
        	}
        }
	}
}
