package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.swt.graphics.Point;

/**
 * AST Treeをアクセスして、該当する最小のASTNodeを取得する
 * @author iPride_Demo
 *
 */
public class SimpleVisitor extends ASTVisitor{

	private Point selection = null;
	private ASTNode matchedNode = null;
	
	
	public SimpleVisitor(Point sel){
		selection = sel;
	}
	
	public void preVisit(ASTNode node){
		int startPos = node.getStartPosition();
		int len = node.getLength();
		if(startPos == selection.x && len == selection.y){
			matchedNode = node;
		}
	}

	public ASTNode getMatchedNode() {
		return matchedNode;
	}
	
	
}
