package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.TypeDeclaration;

/**
 * Visitorの共通機能をまとめるクラス
 * @author iPride_Demo
 *
 */
public class BaseVisitor extends ASTVisitor{


	/**
	 * パッケージ名
	 */
	protected String packageName = null;
	
	
	/**
	 * メソッドを宣言するクラス名を取得
	 * @param classType クラスを宣言するノート
	 * @param seperator インナークラスとアウトクラスを繋ぐ文字
	 * @return　アウトクラス名を含むクラス名（パッケージ名を含まない）
	 */
	protected String getDeclareClassName(ASTNode classType,String seperator){
		
		String classNameToRoot = null;
		while(classType != null){
			if(classType instanceof TypeDeclaration){
				TypeDeclaration classDeclaration = (TypeDeclaration)classType;
				//クラス名
				String curClassName = null;
				SimpleName sn = classDeclaration.getName();
				curClassName = sn.getIdentifier();	
				if(classNameToRoot == null){
					classNameToRoot = curClassName;
				}else{
					classNameToRoot = curClassName + seperator + classNameToRoot;
				}
				
				classType = classType.getParent();
			}else{
				break;
			}
		}
		
			
		return classNameToRoot;
	}
	
	/**
	 * visit package declaration
	 */
	public boolean visit(PackageDeclaration node){
		Name name = node.getName();
		packageName = name.getFullyQualifiedName();
		return super.visit(node);
	}
	
	public String getPackageName() {
		return packageName;
	}
	
}
