package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.ClassRepository;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.IGoToDeclare;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.MethodDeclarePlace;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.NumberScanner;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.SimpleTypeDeclarePlace;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.ThisFieldDeclarePlace;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.VarFieldDeclarePlace;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.setting.repository.Repository;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jdt.core.compiler.InvalidInputException;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ArrayAccess;
import org.eclipse.jdt.core.dom.ArrayCreation;
import org.eclipse.jdt.core.dom.ArrayInitializer;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.AssertStatement;
import org.eclipse.jdt.core.dom.Assignment;
import org.eclipse.jdt.core.dom.CastExpression;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.ConditionalExpression;
import org.eclipse.jdt.core.dom.ConstructorInvocation;
import org.eclipse.jdt.core.dom.DoStatement;
import org.eclipse.jdt.core.dom.EnhancedForStatement;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.FieldAccess;
import org.eclipse.jdt.core.dom.ForStatement;
import org.eclipse.jdt.core.dom.IfStatement;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.InfixExpression;
import org.eclipse.jdt.core.dom.InstanceofExpression;
import org.eclipse.jdt.core.dom.MemberValuePair;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.NumberLiteral;
import org.eclipse.jdt.core.dom.ParenthesizedExpression;
import org.eclipse.jdt.core.dom.PostfixExpression;
import org.eclipse.jdt.core.dom.PrefixExpression;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.ReturnStatement;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleMemberAnnotation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.SuperConstructorInvocation;
import org.eclipse.jdt.core.dom.SuperFieldAccess;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.eclipse.jdt.core.dom.SwitchStatement;
import org.eclipse.jdt.core.dom.SynchronizedStatement;
import org.eclipse.jdt.core.dom.ThisExpression;
import org.eclipse.jdt.core.dom.ThrowStatement;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.WhileStatement;
import org.eclipse.jdt.internal.compiler.classfmt.ClassFileConstants;

/**
 * Java要素の宣言を取得するためのVisitor
 * @author jiang
 *
 */
public class DeclareVisitor extends BaseVisitor{

	/**
	 * 選択された要素に対応するASTノート
	 */
	private ASTNode selectedNode = null;

	/**
	 * 選択された要素の祖先で、且つタイプであるノート
	 */
	private ASTNode parentTypeNode = null;

	/**
	 * 宣言を取得するためのインスタンス
	 */
	private IGoToDeclare delclareImpl = null;

	/**
	 * ソースコードのVisitor
	 */
	private JavaSourceVisitor sourceVisitor = null;

	/**
	 * コンストラクタ
	 * @param selectedNode
	 */
	public DeclareVisitor(ASTNode selectedNode,JavaSourceVisitor sourceVisitor){
		this.selectedNode = selectedNode;
		this.sourceVisitor = sourceVisitor;
		if(selectedNode != null){
			parentTypeNode = selectedNode.getParent();
			while(parentTypeNode != null){
				if(parentTypeNode instanceof Type){
					break;
				}
				parentTypeNode = parentTypeNode.getParent();
			}

		}
	}

	/**
	 * 処理対象文字列を取得
	 * @return
	 */
	private String getObjectString(Name name){
		String str = name.getFullyQualifiedName();
		int from = selectedNode.getStartPosition() -
		                name.getStartPosition();
		String typeString = str.substring(from,
				from + selectedNode.getLength());
		//親タイプノートの最後を選択する場合、親タイプを処理対象とする
		//たとえば：java.util.ArrayListのArrayListを選択した場合、
		//ArrayListではなくて、java.util.ArrayListを対象とする
		if(parentTypeNode != null){
			if(parentTypeNode.getStartPosition() + parentTypeNode.getLength() ==
				selectedNode.getStartPosition() + selectedNode.getLength()){
				typeString = str;
			}
		}
		return typeString;
	}

	/**
	 * nodeがselectedNodeの祖先であるかどうかを判断する
	 * @param node
	 * @return
	 */
	private boolean isSelectedNodeAncestor(ASTNode node){

		ASTNode parent = selectedNode;
		while(parent != null){
			if(parent == node){
				return true;
			}else{
				parent = parent.getParent();
			}
		}

		return false;
	}

	/**
	 * SimpleType
	 */
	public boolean visit(SimpleType node){
		if(delclareImpl == null && parentTypeNode == node){
			Name name = node.getName();
			String typeString = getObjectString(name);
			generateDelcareImpl(typeString);
		}
		return super.visit(node);
	}

	/**
	 * QualifiedType
	 */
	public boolean visit(QualifiedType node){
		if(delclareImpl == null && parentTypeNode == node){
			Name name = node.getName();
			String typeString = getObjectString(name);
			generateDelcareImpl(typeString);
		}
		return super.visit(node);
	}

	/**
	 * インポート文の定義
	 */
	public boolean visit(ImportDeclaration node){
		if(isSelectedNodeAncestor(node)){
			Name name = node.getName();
			String typeString = getObjectString(name);
			generateDelcareImpl(typeString);
		}
		return super.visit(node);
	}

	/**
	 * visit method declaration
	 */
	public boolean visit(MethodDeclaration node){

		List exceptions =  node.thrownExceptions();
		if(exceptions != null){
			for(int i = 0; i < exceptions.size();i++){
				Name exceptionName = (Name)exceptions.get(i);
				if(exceptionName == selectedNode ||
						isSelectedNodeAncestor(exceptionName)){
					String typeString = getObjectString(exceptionName);
					generateDelcareImpl(typeString);
				}
			}
		}
		return super.visit(node);
	}

	/**
	 * Array access expression
	 */
	public boolean visit(ArrayAccess node)  {
		Expression expression =node.getArray();
		handleExpression(expression);
		return super.visit(node);
	}

	/**
	 * Array initializer
	 */
	public boolean visit(ArrayInitializer node) {
		List list = node.expressions();
		handListOfExpression(list);
		return super.visit(node);
	}


	/**
	 * Assert statement
	 */
	public boolean visit(AssertStatement node){
		Expression expression =node.getExpression();
		handleExpression(expression);

		Expression message =node.getMessage();
		handleExpression(message);
		return super.visit(node);
	}

	/**
	 * Assignment expression
	 */
	public boolean visit(Assignment node){

		Expression left =node.getLeftHandSide();
		handleExpression(left);

		Expression right =node.getRightHandSide();
		handleExpression(right);
		return super.visit(node);
	}

	/**
	 * Cast expression
	 */
	public  boolean visit(CastExpression node){

		Expression condition = node.getExpression();
		handleExpression(condition);
		return super.visit(node);
	}


	/**
	 * Conditional expression
	 */
	public boolean visit(ConditionalExpression node) {
		Expression condition = node.getExpression();
		handleExpression(condition);

		Expression thenExpression = node.getThenExpression();
		handleExpression(thenExpression);

		Expression elseExpression = node.getElseExpression();
		handleExpression(elseExpression);
		return super.visit(node);
	}

	/**
	 * constructor invocation
	 */
	public boolean visit(ConstructorInvocation node){

		//引数
		List list = node.arguments();
		handListOfExpression(list);
		return super.visit(node);
	}

	/**
	 * Do statement
	 */
	public boolean visit(DoStatement node) {
		Expression condition = node.getExpression();
		handleExpression(condition);

		return super.visit(node);
	}

	/**
	 * Enhanced For statement
	 */
	public boolean visit(EnhancedForStatement node){

		Expression set = node.getExpression();
		handleExpression(set);

		return super.visit(node);
	}

	/**
	 * Expression Statement
	 */
	public boolean visit(ExpressionStatement node){
		Expression expression = node.getExpression();
		handleExpression(expression);
		return super.visit(node);
	}

	/**
	 * Field access
	 */
	public boolean visit(FieldAccess node){

		Expression expression =node.getExpression();
		handleExpression(expression);

		Name fieldName = node.getName();
		if(fieldName == this.selectedNode){
			String typeName = fieldName.getFullyQualifiedName();
			if(expression.getNodeType() == ASTNode.THIS_EXPRESSION){
				delclareImpl = new ThisFieldDeclarePlace(typeName,
						selectedNode.getStartPosition(),selectedNode.getLength());
			}
		}

		return super.visit(node);
	}

	/**
	 * Simple or qualified "super" field access expression
	 */
	public boolean visit(SuperFieldAccess node) {

		Name name = node.getQualifier();
		if(isSelectedNodeAncestor(node)){
			String typeNameStr = getObjectString(name);
			generateDelcareImpl(typeNameStr);
		}

		//このバージョンでは、他のクラスのフィールドの宣言を開くこと
		//をサポートしない

		return super.visit(node);
	}

    /**
     * For statement
     */
	public boolean visit(ForStatement node){

		Expression condition =node.getExpression();
		handleExpression(condition);

		//初期値
		List initList = node.initializers();
		handListOfExpression(initList);

		//修正値
		List uptList = node.updaters();
		handListOfExpression(uptList);
		return super.visit(node);
	}

	/**
	 * If statement
	 */
	public boolean visit(IfStatement node) {

		Expression condition =node.getExpression();
		handleExpression(condition);
		return super.visit(node);
	}

	/**
	 * Infix expression
	 */
	public boolean visit(InfixExpression node) {

		Expression left = node.getLeftOperand();
		handleExpression(left);

		Expression right = node.getRightOperand();
		handleExpression(right);

		List extendList = node.extendedOperands();
		handListOfExpression(extendList);
		return super.visit(node);
	}

	/**Instanceof expression
	 */
	public boolean visit(InstanceofExpression node){

		Expression expression = node.getLeftOperand();
		handleExpression(expression);
		return super.visit(node);
	}

	/**
	 * Member value pair node (added in JLS3 API).
     * Member value pairs appear in annotations.
	 */
	public boolean visit(MemberValuePair node) {

		Expression value = node.getValue();
		handleExpression(value);
		return super.visit(node);
	}

	/**
	 * 引数のタイプの分析
	 * @param list
	 */
	private List<String> analyzeListOfParams(List<Expression> list){

		List<String> typeList = new ArrayList<String>();

		if(list != null){
			for(int i = 0; i < list.size();i++){
				Expression expression = (Expression)list.get(i);
			    String type = resolveExpresstionType(expression);
			    typeList.add(type);
			}
		}

		return typeList;
	}

	/**
	 * メソッドの呼び出し
	 */
	public boolean visit(MethodInvocation node){

		Expression expression =node.getExpression();
		handleExpression(expression);

		List args = node.arguments();
		handListOfExpression(args);

		//get class type which declares thie method
		SimpleName methodName = node.getName();
		if(methodName == selectedNode){

			//引数の分析
			List<String> paramsList = analyzeListOfParams(args);

			if(expression == null){
				handleMethodNameWithouType(node,paramsList);
			}else{
				//Expressionのタイプを割り出す
				String expressionType = resolveExpresstionType(expression);
				if(expressionType != null){
					showMethodDeclareInClass(expressionType,
							methodName.getIdentifier(),paramsList);
				}
			}

		}
		return super.visit(node);
	}

	/**
	 * Simple or qualified "super" method invocation express
	 */
	public  boolean visit(SuperMethodInvocation node) {
		//引数
		List args = node.arguments();
		handListOfExpression(args);

		Name name = node.getQualifier();
		if(isSelectedNodeAncestor(name)){
			String typeNameStr = getObjectString(name);
			generateDelcareImpl(typeNameStr);
		}else{
			//get class type which declares thie method
			SimpleName methodName = node.getName();
			if(methodName == selectedNode){

				//引数の分析
				List paramsList = analyzeListOfParams(args);

				if(name == null){
					ClassTypeInfo  currentClass = sourceVisitor.getDeclareClass(node);
					if(currentClass != null){
						showDeclareOfSuperMethodInvocation(currentClass.getFullClassName(),
								methodName,paramsList);
					}
				}else{
					String qaName = resolveName(name);
					if(qaName != null){
						showDeclareOfSuperMethodInvocation(qaName,
								methodName,paramsList);
					}
				}

			}
		}

		return super.visit(node);
	}

	/**
	 * Class instance creation expression
	 */
	public boolean visit(ClassInstanceCreation node) {

		Expression parent = node.getExpression();
		handleExpression(parent);

		//引数
		List args = node.arguments();
		handListOfExpression(args);

		//get class type which declares thie method
		Type constructorType = node.getType();
		boolean selected = false;
		String typeName = null;
		Name constructorName = null;
		if(selectedNode == constructorType){
			selected = true;
		}else{
			if(constructorType.isSimpleType()){
				SimpleType st = (SimpleType)constructorType;
				constructorName = st.getName();

				if(selectedNode == constructorName ||parentTypeNode == constructorType){
					selected = true;
					typeName = constructorName.getFullyQualifiedName();
				}
			}
		}
		if(selected){
			if(constructorType.getStartPosition() + constructorType.getLength() ==
				selectedNode.getStartPosition() + selectedNode.getLength()){
				//show declare of the constructor
				String fullClassName = resolveName(constructorName);
				if(fullClassName != null){
					String methodName = null;
				    int pos = typeName.lastIndexOf('.');
				    if(pos >= 0){
				    	methodName = typeName.substring(pos + 1);
				    }else{
				    	methodName = typeName;
				    }
					//引数の分析
					List<String> paramsList = analyzeListOfParams(args);
				    showMethodDeclareInClass(fullClassName,methodName,paramsList);
				}
			}else{
				//part of the type is selected,show declare of the type
				int from = selectedNode.getStartPosition() - constructorType.getStartPosition();
				typeName = typeName.substring(from, from + selectedNode.getLength());
				generateDelcareImpl(typeName);
			}
		}
		return super.visit(node);
	}

	/**
	 * スーパークラスのメソッドの呼び出しの宣言の開く
	 * @param thisClassName　呼び出しが所属するクラスの完全修飾名
	 * @param methodName
	 */
	private void showDeclareOfSuperMethodInvocation(String thisClassName,
			SimpleName methodName,List paramsList){
		ClassTypeInfo classUnit = ClassRepository.getInstance(
		    ).getClassUnit(thisClassName);
		if(classUnit != null){
			String superClassName = classUnit.getSuperClassName();
			if(superClassName != null){
				showMethodDeclareInClass(superClassName,
						methodName.getIdentifier(),paramsList);
			}
        }
	}

	/**
	 * メソッド名の前にExpressがないメソッドの呼び出しの処理
	 * @param node
	 */
	private void handleMethodNameWithouType(MethodInvocation node,List paramsList){

		//当メソッドを宣言するクラスが、当CompileUnit
		//に定義されるクラスか、そのクラスのSuperクラス
		TypeDeclaration classType = getClassDeclareNode(node);
	    if(classType != null){
	    	String className = getDeclareClassName(classType,
	    			"$");
	    	String fullClassName = HelperFunc.getFullClassName(
	    			sourceVisitor.getPackageName(),className);
	    	SimpleName sn = node.getName();
	    	showMethodDeclareInClass(fullClassName,sn.getIdentifier(),paramsList);
	    }
	}


	/**
	 * メソッドの宣言を開く
	 * @param fullClassName クラスの完全修飾名
	 * @param methodNameStr
	 */
	private void showMethodDeclareInClass(String fullClassName,
			String methodNameStr,List paramsList){
		ClassRepository classRepository = ClassRepository.getInstance();
		//当メソッドを宣言するクラスが、当CompileUnit
		//に定義されるクラスか、そのクラスのSuperクラス

		ClassTypeInfo classUnit = classRepository.getClassUnit(fullClassName);

		if(classUnit != null){
	    	//get method only be name:to be changed
	    	MatchMethodInfo methodUnit = classUnit.getMethod(methodNameStr,paramsList);
	        //クラスに該当メソッドがある
	    	if(methodUnit != null){
	    		delclareImpl  = new MethodDeclarePlace(fullClassName,
	    				methodUnit,classUnit.getSourceFileName());

	    	}else{
	    		HelperFunc.getLogger().debug("can't find mehthod in class:" +
	    				fullClassName +"," + methodNameStr);
	    		//check for super class
	    		String superClassName = classUnit.getSuperClassName();

	    		//スーバークラス名の解析が成功した場合
	    		if(superClassName != null){
	    			showMethodDeclareInClass(superClassName,methodNameStr,
	    					paramsList);
	    		}
	    	}
    	}else{
    		//delete by Qiu Song on 20091126 for メソッド定義箇所がない場合、エラーメッセージを表示しない
//    		String msg = Message.get("SourceViewer.NoClassFound")
//    					+ "[ " + fullClassName + " ]";
//    		ExcatMessageUtilty.showMessage(null, msg);
    		//end of delete by Qiu Song on 20091126 for メソッド定義箇所がない場合、エラーメッセージを表示しない
    	}
	}

	/**
	 * メソッドの戻り値のタイプを取得する
	 * @param fullClassName クラスの完全修飾名
	 * @param methodNameStr
	 */
	private String getMethodReturnType(String fullClassName,
			String methodNameStr,List paramsList){
		ClassRepository classRepository = ClassRepository.getInstance();
		//当メソッドを宣言するクラスが、当CompileUnit
		//に定義されるクラスか、そのクラスのSuperクラス

		ClassTypeInfo classUnit = classRepository.getClassUnit(fullClassName);
    	if(classUnit != null){
	    	//get method only be name:to be changed
	    	MatchMethodInfo methodUnit = classUnit.getMethod(methodNameStr,paramsList);
	        //クラスに該当メソッドがある
	    	if(methodUnit != null){
	    		return methodUnit.getReturnType();

	    	}else{
	    		HelperFunc.getLogger().debug("can't find mehthod in class:" +
	    				fullClassName +"," + methodNameStr);
	    		//check for super class
	    		String superClassName = classUnit.getSuperClassName();

	    		//スーバークラス名の解析が成功した場合
	    		if(superClassName != null){
	    			return getMethodReturnType(superClassName,
	    					methodNameStr,paramsList);
	    		}else{
	    			return null;
	    		}
	    	}
    	}

    	return null;
	}


	/**
	 * nodeをカーバーするクラスのノードを取得
	 * @param node
	 * @return　インナークラスではない場合、packageName.className <Br>
	 * インナークラスの場合、packageName.outClassName$innerClassName
	 */
	private TypeDeclaration getClassDeclareNode(ASTNode node){

		ASTNode parent = node;
		TypeDeclaration classTypeNode = null;
		while(parent != null){
			if(parent instanceof TypeDeclaration){
				classTypeNode = (TypeDeclaration)parent;
				break;
			}

			parent = parent.getParent();
		}

		return classTypeNode;
	}

	/**
	 * Parenthesized expression
	 */
	public boolean visit(ParenthesizedExpression node){

		Expression expression =node.getExpression();
		handleExpression(expression);
		return super.visit(node);
	}

	/**
	 * Postfix expression
	 */
	public boolean visit(PostfixExpression node) {
		Expression operand =node.getOperand();
		handleExpression(operand);
		return super.visit(node);
	}

	/**
	 * Prefix expression
	 */
	public boolean visit(PrefixExpression node){
		Expression operand =node.getOperand();
		handleExpression(operand);
		return super.visit(node);
	}

	/**
	 * return statement
	 */
	public boolean visit(ReturnStatement node){
		Expression expression =node.getExpression();
		handleExpression(expression);
		return super.visit(node);
	}

	/**
	 * Single member annotation node
	 */
	public boolean visit(SingleMemberAnnotation node) {

		Expression value =node.getValue();
		handleExpression(value);
		return super.visit(node);
	}

	/**
	 * single variable definition
	 */
	public boolean visit(SingleVariableDeclaration node) {

		Expression init =node.getInitializer();
		handleExpression(init);
		return super.visit(node);
	}


	/**
	 * Super constructor invocation statement
	 */
	public boolean visit(SuperConstructorInvocation node) {

		Expression expression =node.getExpression();
		handleExpression(expression);

		//引数
		List list = node.arguments();
		handListOfExpression(list);
		return super.visit(node);
	}


	/**
	 * Switch statement
	 */
	public boolean visit(SwitchStatement node) {

		Expression expression =node.getExpression();
		handleExpression(expression);

		return super.visit(node);
	}

	/**
	 * Synchronized statement
	 */
	public boolean visit(SynchronizedStatement node){
		Expression expression =node.getExpression();
		handleExpression(expression);

		return super.visit(node);
	}

	/**
	 * Simple or qualified "this"
	 */
	public boolean visit(ThisExpression node) {

		Name name = node.getQualifier();
		if(isSelectedNodeAncestor(node)){
			String typeString = getObjectString(name);
			generateDelcareImpl(typeString);
		}
		return super.visit(node);
	}

	/**
	 * throw statement
	 */
	public boolean visit(ThrowStatement node) {

		Expression expression =node.getExpression();
		handleExpression(expression);

		return super.visit(node);
	}

	/**
	 * Variable declaration fragment
	 */
	public boolean visit(VariableDeclarationFragment node) {

		Expression init =node.getInitializer();
		handleExpression(init);

		return super.visit(node);
	}

	/**
	 * While statement
	 */
	public boolean visit(WhileStatement node) {

		Expression condition =node.getExpression();
		handleExpression(condition);

		return super.visit(node);
	}

	/**
	 * Expressionの処理
	 * @param expression
	 */
	private void handleExpression(Expression expression){

		if(expression != null){
			if(expression == selectedNode){
				//問題点：QualifiedNameの場合、FieldAccessである
				//可能性もあるので、このバージョンでは対応しない(2007/11/27)
				//@see org.eclipse.jdt.core.dom.FieldAccess
				if(expression instanceof SimpleName){
					Name name = (Name)expression;
					generateDelcareImpl(name.getFullyQualifiedName());
				}
			}
		}
	}

	/**
	 * Expression Listの処理
	 * @param list
	 */
	private void handListOfExpression(List<Expression> list){

		if(list != null){
			for(int i = 0; i < list.size();i++){
				Expression expression = list.get(i);
				handleExpression(expression);
			}
		}
	}

	/**
	 * Nameの宣言を開くインスタンスの生成
	 * @param name
	 */
	private void generateDelcareImpl(String name){
		if(name == null){
			return;
		}
		int start = selectedNode.getStartPosition();
		int length = selectedNode.getLength();
		//変数かどうか
		MatchVariableInfo var = sourceVisitor.getMatchedVar(start,length,name);
        if(var != null){
        	delclareImpl = new VarFieldDeclarePlace(name,var.getStartPosition(),
        			var.getLength());
        	return;
        }

		//fieldかどうか
        MatchField field = sourceVisitor.getMatchedField(start,length,name);
        if(field != null){
//tu modify 2009/10/28
//        	delclareImpl = new VarFieldDeclarePlace(name,field.getStartPosition(),
//        			field.getFieldLength());
        	delclareImpl = new ThisFieldDeclarePlace(name,field.getStartPosition(),
        			field.getFieldLength());
        	return;
        }

        //nameが解析できるタイプであるかどうか
		MiddleType mt = new MiddleType(name,start,length);
        String fullClassName = sourceVisitor.analyzeClassType(mt);
        if(fullClassName != null){
        	delclareImpl = new SimpleTypeDeclarePlace(fullClassName,
        			start,length);
        }
	}

	public IGoToDeclare getDelclareImpl() {
		return delclareImpl;
	}

	/**
	 * Expressionのタイプを解析する。<br>
	 * @param expression
	 * @return
	 */
	private String resolveExpresstionType(Expression expression){
		int nodeType = expression.getNodeType();
		switch(nodeType){
		case ASTNode.SIMPLE_NAME:
			return resolveSimpleName((SimpleName)expression);
		case ASTNode.QUALIFIED_NAME:
			return resolveQualifiedName((QualifiedName)expression);
		case ASTNode.TYPE_LITERAL:
			return "java.lang.Class";
		case ASTNode.NULL_LITERAL:
			return "null";
		case ASTNode.BOOLEAN_LITERAL:
			return "boolean";
		case ASTNode.CHARACTER_LITERAL:
			return "char";
		case ASTNode.STRING_LITERAL:
			return "java.lang.String";
		case ASTNode.NUMBER_LITERAL:
			return resolveNumberLiteral((NumberLiteral)expression);
		case ASTNode.INSTANCEOF_EXPRESSION:
			return "boolean";
		case ASTNode.THIS_EXPRESSION:
			return resolveThisExpression((ThisExpression)expression);
		case ASTNode.SUPER_FIELD_ACCESS:
			return resolveSuperFieldAccess((SuperFieldAccess)expression);
		case ASTNode.METHOD_INVOCATION:
			return resolveMethodInvocation((MethodInvocation)expression);
		case ASTNode.SUPER_METHOD_INVOCATION:
			return resolveSuperMethodInvocation((SuperMethodInvocation)expression);
		case ASTNode.ARRAY_ACCESS:
			return resolveArrayAccess((ArrayAccess)expression);
		case ASTNode.CAST_EXPRESSION:
			return resolveCastExpression((CastExpression)expression);
		case ASTNode.PARENTHESIZED_EXPRESSION:
			return resolveParenthesizedExpression((ParenthesizedExpression)expression);
		case ASTNode.PREFIX_EXPRESSION:
			return resolvePrefixExpression((PrefixExpression)expression);
		case ASTNode.POSTFIX_EXPRESSION:
			return resolvePostfixExpression((PostfixExpression)expression);
		case ASTNode.INFIX_EXPRESSION:
			return resolveInfixExpression((InfixExpression)expression);
		case ASTNode.CONDITIONAL_EXPRESSION:
			return resolveConditionExpression((ConditionalExpression)expression);
		case ASTNode.ASSIGNMENT:
			return resolveAssignExpression((Assignment)expression);
		case ASTNode.CLASS_INSTANCE_CREATION:
			return resolveClassInstanceCreateionType((ClassInstanceCreation)expression);
		case ASTNode.ARRAY_CREATION:
			return resovleArrayCreateType((ArrayCreation)expression);
		default:
			break;

		}
		return null;
	}

	/**
	 * 配列生成タイプの解析
	 * @param arrayCreate
	 * @return
	 */
	private String resovleArrayCreateType(ArrayCreation arrayCreate){
		ArrayType type = arrayCreate.getType();
		return resolveType(type);
	}

	/**
	 * クラスインスタンス生成のタイプの解析
	 * @param createExp
	 * @return
	 */
	private String resolveClassInstanceCreateionType(ClassInstanceCreation createExp){
		Type type = createExp.getType();
		return resolveType(type);
	}

	/**
	 * assignmentタイプの解析
	 * @param assignment
	 */
	private String resolveAssignExpression(Assignment assignment){
		Expression leftside = assignment.getLeftHandSide();
		Expression rightside = assignment.getRightHandSide();

		return resolveTwoOperarands(leftside,rightside);
	}

	/**
	 * ２つのOperandがある場合のタイプを解析
	 * @param ex1
	 * @param ex2
	 * @return
	 */
	private String resolveTwoOperarands(Expression ex1,Expression ex2){
		int returnTypeRank = NumberScanner.TokenNameNotNumber;
		String returnType = null;
		String oneType = resolveExpresstionType(ex1);

		if(oneType == null){
			//解析不能
			return null;
		}

		int rank = 	getNumberTypeRank(oneType);
		if(rank == NumberScanner.TokenNameNotNumber){
			//数字タイプではない
			return oneType;
		}
		returnTypeRank = rank;
		returnType = oneType;

		String twoType = resolveExpresstionType(ex2);
		if(twoType == null){
			//解析不能
			return null;
		}

		rank = 	getNumberTypeRank(twoType);
		if(rank == NumberScanner.TokenNameNotNumber){
			//数字タイプではない
			return twoType;
		}
		if(returnTypeRank < rank ){
			returnTypeRank = rank;
			returnType = twoType;
		}
		return returnType;
	}

	/**
	 * Condition Expressionの解析
	 * @param condExpression
	 * @return
	 */
	private String resolveConditionExpression(ConditionalExpression condExpression){

		Expression thenExpression = condExpression.getThenExpression();
		Expression elseExpression = condExpression.getElseExpression();
		return resolveTwoOperarands(thenExpression,elseExpression);
	}

	/**
	 * infix expressionタイプの解析
	 * @param infixExpression
	 * @return
	 */
	private String resolveInfixExpression(InfixExpression infixExpression){
		InfixExpression.Operator operator = infixExpression.getOperator();
		if(operator == InfixExpression.Operator.CONDITIONAL_AND ||
				operator == InfixExpression.Operator.CONDITIONAL_OR ||
				operator == InfixExpression.Operator.LESS ||
				operator == InfixExpression.Operator.LESS_EQUALS ||
				operator == InfixExpression.Operator.GREATER ||
				operator == InfixExpression.Operator.GREATER_EQUALS ||
				operator == InfixExpression.Operator.EQUALS ||
				operator == InfixExpression.Operator.NOT_EQUALS){
			return "boolean";
		}
		int returnTypeRank = NumberScanner.TokenNameNotNumber;
		String returnType = null;
		//左
		Expression left = infixExpression.getLeftOperand();
		String leftType = resolveExpresstionType(left);
		if(leftType == null){
			//解析不能
			return null;
		}

		int rank = 	getNumberTypeRank(leftType);
		if(rank == NumberScanner.TokenNameNotNumber){
			//数字タイプではない
			return leftType;
		}
		returnTypeRank = rank;
		returnType = leftType;
		//右
		Expression right = infixExpression.getRightOperand();
		String rightType = resolveExpresstionType(right);
		if(right == null){
			//解析不能
			return null;
		}

		rank = 	getNumberTypeRank(rightType);
		if(rank == NumberScanner.TokenNameNotNumber){
			//数字タイプではない
			return rightType;
		}
		if(returnTypeRank < rank ){
			returnTypeRank = rank;
			returnType = rightType;
		}

		//extended operand
		if(infixExpression.hasExtendedOperands()){
			List listEx = infixExpression.extendedOperands();
			for(int i = 0; i < listEx.size();i++){
				Expression extendedExpression = (Expression)listEx.get(i);
				String extendedType = resolveExpresstionType(extendedExpression);
				if(extendedType == null){
					return null;
				}
				rank = 	getNumberTypeRank(extendedType);
				if(returnTypeRank < rank ){
					returnTypeRank = rank;
					returnType = extendedType;
				}
			}
		}

		return returnType;
	}

	private int getNumberTypeRank(String type){
		if("double".equals(type)){
			return NumberScanner.TokenNameDoubleLiteral;
		}else if("float".equals(type)){
			return NumberScanner.TokenNameFloatingPointLiteral;
		}else if("long".equals(type)){
			return NumberScanner.TokenNameLongLiteral;
		}else if("int".equals(type) ||
				   "short".equals(type) ||"byte".equals(type) ){
			return NumberScanner.TokenNameIntegerLiteral;
		}else if("char".equals(type)){
			return NumberScanner.TokenNameCharLiteral;
		}

		return NumberScanner.TokenNameNotNumber;
	}

	/**
	 * postfix expressionのタイプの解析
	 * @param postfixExpression
	 * @return
	 */
	private String resolvePostfixExpression(PostfixExpression postfixExpression){

		return resolveExpresstionType(postfixExpression.getOperand());
	}

	/**
	 * Prefix　Expressionタイプの解析
	 * @param prefixExpression
	 * @return
	 */
	private String resolvePrefixExpression(PrefixExpression prefixExpression){

		PrefixExpression.Operator operator = prefixExpression.getOperator();

		if(operator == PrefixExpression.Operator.NOT){
			return "boolean"; //not
		}else{
			return resolveExpresstionType(prefixExpression.getOperand());
		}
	}

	/**
	 * 数字タイプの解析
	 * @param number
	 * @return
	 */
	private String resolveNumberLiteral(NumberLiteral number){
		String token = number.getToken();
		NumberScanner ns = new NumberScanner(ClassFileConstants.JDK1_5,token);
		try {
			int kind = ns.parseNumber();
			if(kind == NumberScanner.TokenNameIntegerLiteral){
				return "int";
			}else if(kind == NumberScanner.TokenNameLongLiteral){
				return "long";
			}else if(kind == NumberScanner.TokenNameFloatingPointLiteral){
				return "float";
			}else if(kind == NumberScanner.TokenNameDoubleLiteral){
				return "double";
			}

		} catch (InvalidInputException e) {
	        return null;
		}
		return null;
	}

	/**
	 * 括弧つきExpressionの解析
	 * @param expression
	 * @return
	 */
	private String resolveParenthesizedExpression(ParenthesizedExpression expression){
		Expression middle = expression.getExpression();
		if(middle != null){
			return resolveExpresstionType(middle);
		}
		return null;
	}

	/**
	 * cast typeの解析
	 * @param expression
	 * @return
	 */
	private String resolveCastExpression(CastExpression expression){

		Type type = expression.getType();
		if(type != null){
			return resolveType(type);
		}
		return null;
	}

	/**
	 * typeの解析
	 * @param type
	 * @return
	 */
	private String resolveType(Type type){

		MiddleType mt = new MiddleType(type);
		return sourceVisitor.analyzeType(mt);
	}

	/**
	 * 配列タイプの解析
	 * @param expression
	 * @return
	 */
	private String resolveArrayAccess(ArrayAccess expression){

		Expression element = expression.getArray();
		if(element != null){
			String elementType= resolveExpresstionType(element);
			if(elementType != null){
				int typeStrLen = elementType.length();
				if(typeStrLen > 2){
					//remove []
					String elementAccess = elementType.substring(0,
							typeStrLen-2);
					return elementAccess;
				}
			}
		}
		return null;
	}

	/**
	 * スーパークラスのメソッドの呼び出しの戻り値のタイプを解析する。
	 * @param expression
	 * @return
	 */
	private String resolveSuperMethodInvocation(SuperMethodInvocation expression){

		String callerClassName = null;
		Name qualifier = expression.getQualifier();
		SimpleName sn = expression.getName();
		String methodNameStr = sn.getIdentifier();

		if(qualifier == null){
			ClassTypeInfo  currentClass = sourceVisitor.getDeclareClass(expression);
			if(currentClass == null){
				return null;
			}
			//super class is not resolved in current class
			ClassTypeInfo cu = ClassRepository.getInstance().getClassUnit(
					currentClass.getFullClassName());
			if(cu ==null){
				return null;
			}
			callerClassName = cu.getSuperClassName();

		}else{
			String qaName = resolveName(qualifier);
			if(qaName != null){
				ClassTypeInfo cu = ClassRepository.getInstance().getClassUnit(qaName);
				if(cu != null){
					callerClassName = cu.getSuperClassName();
				}
			}
		}

		if(callerClassName != null){
			//引数の分析
			List args = expression.arguments();
			List paramsList = analyzeListOfParams(args);

			return getMethodReturnType(callerClassName,methodNameStr,paramsList);
		}
		return null;
	}

	/**
	 * メソッドの呼び出しの戻り値のタイプを解析する。
	 *
	 * @param expression
	 * @return
	 */
	private String resolveMethodInvocation(MethodInvocation expression){

		Expression callerExpression = expression.getExpression();
		String callerClassName = null;
		if(callerExpression == null){
			TypeDeclaration classType = getClassDeclareNode(expression);
		    if(classType != null){
		    	String className = sourceVisitor.getDeclareClassName(classType,
		    			"$");
		    	callerClassName = HelperFunc.getFullClassName(
		    			sourceVisitor.getPackageName(),className);
		    }
		}else{
			callerClassName = resolveExpresstionType(callerExpression);
		}
		if(callerClassName != null){
			SimpleName methodName = expression.getName();
			String methodNameStr = methodName.getIdentifier();
			//引数の分析
			List args = expression.arguments();
			List paramsList = analyzeListOfParams(args);
			return getMethodReturnType(callerClassName,methodNameStr,paramsList);

		}else{
			return null;
		}
	}

	/**
	 * メソッドの呼び出しの前にあるsuper field access expressionを解析する
	 * @param sfa
	 * @return
	 */
	private String resolveSuperFieldAccess(SuperFieldAccess sfa){

		Name qualifier = sfa.getQualifier();
		SimpleName sn = sfa.getName();
		String fieldStr = sn.getFullyQualifiedName();
		if(qualifier == null){
			ClassTypeInfo  currentClass = sourceVisitor.getDeclareClass(sfa);
			if(currentClass == null){
				return null;
			}
			//super class is not resolved in currentClass
			ClassTypeInfo cu = ClassRepository.getInstance().getClassUnit(
					currentClass.getFullClassName());
			if(cu ==null){
				return null;
			}
			return resolveSuperClassField(cu,fieldStr);
		}else{
			String qaName = resolveName(qualifier);
			if(qaName != null){
				ClassTypeInfo cu = ClassRepository.getInstance().getClassUnit(qaName);
				if(cu != null){
					return resolveSuperClassField(cu,fieldStr);
				}
			}
		}
		return null;
	}

	/**
	 * メソッドの呼び出しの前にあるthis expressionを解析する
	 * @param thisExpression
	 * @return
	 */
	private String resolveThisExpression(ThisExpression thisExpression){
		Name qualifier = thisExpression.getQualifier();
		if(qualifier == null){
			ClassTypeInfo  currentClass = sourceVisitor.getDeclareClass(thisExpression);
			if(currentClass != null){
				return currentClass.getFullClassName();
			}
		}
		else{
			return resolveName(qualifier);
		}
		return null;
	}

	/**
	 * Nameの解析
	 * @param name
	 * @return
	 */
	private String resolveName(Name name){

		MiddleType mt = null;
		if(name.isSimpleName()){
			mt = new MiddleType((SimpleName)name);
		}else{
			mt = new MiddleType((QualifiedName)name);
		}

		return sourceVisitor.analyzeClassType(mt);
	}

	/**
	 * メソッドの呼び出しの前にあるQualifiedNameを解析する
	 * @param qname
	 * @return
	 */
	private String resolveQualifiedName(QualifiedName qname){

		//まず、TypeNameであるかどうかを判断する
        //クラスタイプかどうか
        MiddleType mt = new MiddleType(qname);
        String mtClassType = sourceVisitor.analyzeClassType(mt);
		if(mtClassType != null){
			return mtClassType;
		}

		//field accessかどうか
		Name qfather = qname.getQualifier();
		if(qfather.isSimpleName()){
			mt = new MiddleType((SimpleName)qfather);
		}else{
			mt = new MiddleType((QualifiedName)qfather);
		}
		String mtFather = sourceVisitor.analyzeClassType(mt);
		if(mtFather != null){
			ClassTypeInfo cu = ClassRepository.getInstance().getClassUnit(mtFather);
			if(cu != null){
				SimpleName fieldName = qname.getName();
				String fieldStr = fieldName.getFullyQualifiedName();
				MatchField field = cu.getMatchedField(fieldStr);
				if(field != null){
					return field.getFullFieldType();
				}else{
					//親クラスのfieldであるかどうか
					return resolveSuperClassField(cu,fieldStr);
				}
			}
		}
		return null;
	}

	/**
	 * スーパークラスのフィールドのタイプを解析する
	 * @param cu　現在のクラス
	 * @param fieldStr　クラス名
	 * @return　フィールドのフールクラス名
	 */
	private String resolveSuperClassField(ClassTypeInfo cu,String fieldStr){

		String superClass = cu.getSuperClassName();
		while(superClass != null){
			cu = ClassRepository.getInstance().getClassUnit(superClass);
			if(cu == null){
				break;
			}
			MatchField field = cu.getMatchedField(fieldStr);
			if(field == null){
				superClass = cu.getSuperClassName();
			}else{
				return field.getFullFieldType();
			}
		}
		return null;
	}

	/**
	 * メソッドの呼び出しの前にあるSimpleNameを解析する
	 * @param sn
	 * @return
	 */
	private String resolveSimpleName(SimpleName sn){

     	ClassTypeInfo  currentClass = sourceVisitor.getDeclareClass(sn);
     	ClassTypeInfo cu = null;
    	if(currentClass != null){
    		String fullClassName = currentClass.getFullClassName();
    		cu = ClassRepository.getInstance().getClassUnit(fullClassName);
    	}

		//変数かどうか
		String name = sn.getFullyQualifiedName();
		MatchVariableInfo var = sourceVisitor.getMatchedVar(
				sn.getStartPosition(),sn.getLength(),name);
        if(var != null){
        	//varのfulltypeが設定されていないため、ClassRepositoryを調べる
    		if(cu != null){
    			//タイプが解析済み
    			var = cu.getMatchedVar(
        				sn.getStartPosition(),sn.getLength(),name);
    			if(var != null){
    				return var.getFullType();
    			}
    		}
         	//変数の完全修飾タイプが解析されていない
         	return null;
        }


        //フィールド名かどうか
        MatchField field = sourceVisitor.getMatchedField(
        		sn.getStartPosition(),sn.getLength(),name);
        if(field != null){
        	//fieldのfulltypeが設定されていないため、ClassRepositoryを調べる
        	if(cu != null){
        		field = cu.getMatchedField(name);
        		if(field != null){
        			return field.getFullFieldType();
        		}
        	}
        	//fieldの完全修飾タイプが解析されていない
        	return null;
        }

        //クラスタイプかどうか
        MiddleType mt = new MiddleType(sn);
        String mtClassType = sourceVisitor.analyzeClassType(mt);
		return mtClassType;
	}
}
