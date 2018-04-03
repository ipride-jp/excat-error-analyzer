package jp.co.ipride.excat.common.setting.repository;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.Name;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.BaseVisitor;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.ClassTypeInfo;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.ImportClassInfo;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MatchField;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MatchMethodInfo;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MatchVariableInfo;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MiddleType;
import jp.co.ipride.excat.common.utility.HelperFunc;

/**
 * this visitor is for building a Repository.
 * @author tu-ipride
 * @version 3.0
 */
public class JavaSrcVisitor extends BaseVisitor{

	/**
	 * インポート文のリスト
	 */
	private List<ImportClassInfo> importList = new ArrayList<ImportClassInfo>();

	/**
	 * このファイルにあるクラス宣言のリスト
	 */
	protected List<ClassTypeInfo> classList = new ArrayList<ClassTypeInfo>();

	/**
	 * 当クラスの定義を含むソースファイル名、パス名を含むない
	 * 例：AClass.java
	 * 目的は、インナークラスの定義ファイルを探すためである。
	 */
	protected String sourceFileName = null;

	/**
	 * このファイルにあるメソッドのリスト
	 */
	private List<MatchMethodInfo> methodList = new ArrayList<MatchMethodInfo>();

	/**
	 * このファイルにあるフィールドのリスト
	 */
	private List<MatchField> fieldList = new ArrayList<MatchField>();

	/**
	 * 分析しているメソッド
	 */
	private MatchMethodInfo curMethod = null;


	/**
	 * create list of ClassInfo
	 * @return
	 */
	public List<ClassTypeInfo> getClassList(){
		return classList;
	}

	public void resolveType(){
		for(int i = 0; i < classList.size();i++){
			ClassTypeInfo classTypeInfo =(ClassTypeInfo) classList.get(i);
			classTypeInfo.setSourceFileName(sourceFileName);

			//super classを解析する
			MiddleType mt = classTypeInfo.getSuperClassMType();
			String superClassName = null;
			if(mt != null){
				superClassName = analyzeType(mt, null);
			}else{
				if(!classTypeInfo.isInterfaceType()){
					if(!"java.lang.Object".equals(classTypeInfo.getFullClassName())){
						superClassName = "java.lang.Object";
					}
				}
			}
			classTypeInfo.setSuperClassName(superClassName);

			//インターフェースを解析する
			List<MiddleType> interfaceMTypeList = classTypeInfo.getInterfaceMTypeList();
			List<String> interfaceTypeList = new ArrayList<String>();
			if(interfaceMTypeList != null && interfaceMTypeList.size() > 0){
				for(int ii = 0; ii < interfaceMTypeList.size();ii++){
					MiddleType imt = (MiddleType)interfaceMTypeList.get(ii);
					List<String> possibleInterfaceTypeList = new ArrayList<String>();
					String itype = analyzeType(imt, possibleInterfaceTypeList);
					if (itype == null) {
						for (int index = 0; index < possibleInterfaceTypeList.size(); index++) {
							classTypeInfo.addPossibleInterface(imt.getTypeString(), possibleInterfaceTypeList.get(index));
						}
					} else {
						interfaceTypeList.add(itype);
					}
				}
				//interfaceMTypeListを開放
				interfaceMTypeList.clear();
				classTypeInfo.setInterfaceMTypeList(null);
			}
			classTypeInfo.setInterfaceTypeList(interfaceTypeList);

			//フィールドタイプを解析する
			List<MatchField> fList = classTypeInfo.getFieldList();
			for(int j = 0; j < fList.size();j++){
				MatchField mf = fList.get(j);
				MiddleType fmtype = mf.getFieldMType();
				String fullFieldTypeStr = analyzeType(fmtype, null);
				mf.setFullFieldType(fullFieldTypeStr);
			}

			//メソッドのタイプを解析する
			List<MatchMethodInfo> mList= classTypeInfo.getMethodList();
			for(int j = 0; j < mList.size();j++){
				MatchMethodInfo mmi = mList.get(j);
				MiddleType rmt = mmi.getReturnMType();
				String returnTypeStr = null;
				if(rmt == null){
					//constructor
					returnTypeStr = "void";
				}else{
					returnTypeStr = analyzeType(rmt, null);
				}

				mmi.setReturnType(returnTypeStr);

				//変数を解析する
				List<MatchVariableInfo> vList = mmi.getVariableList();
				List<String> paramTypeList = new ArrayList<String>();
				int paramNumber = mmi.getParamNumber();
				for(int k = 0; k < vList.size(); k++){
					MatchVariableInfo var = vList.get(k);
					MiddleType varMType = var.getVarMType();
					String fullVarType = analyzeType(varMType, null);
					var.setFullType(fullVarType);
					if(k < paramNumber){
						paramTypeList.add(fullVarType);
					}
				}

				//引数のリスト
				mmi.setParamTypeList(paramTypeList);
				//signatureの設定
				mmi.generateSignature();
				//construct name
				String className = HelperFunc.getPureClassName(classTypeInfo.getFullClassName());
				if (className.equals(mmi.getMethodName())){
					mmi.setMethodName("<init>");
				}
			}
		}

	}

	public void clear(){
		methodList.clear();
		fieldList.clear();
		importList.clear();
		classList.clear();
	}

	/******************************* visitor **********************************/

	/**
	 * visit import sentence
	 */
	public boolean visit(ImportDeclaration node){

		ImportClassInfo importClassInfo = new ImportClassInfo();
		importList.add(importClassInfo);

		importClassInfo.setOnDemand(node.isOnDemand());
		Name name = node.getName();
		importClassInfo.setName(name.getFullyQualifiedName());
		return super.visit(node);
	}

	/**
	 * visit class declaration
	 */
	public boolean visit(TypeDeclaration node){

		SimpleName sn = node.getName();

		ClassTypeInfo classTypeInfo = new ClassTypeInfo();
		classList.add(classTypeInfo);

		//source file name
		classTypeInfo.setSourceFileName(sourceFileName);
		//class name
		String className = sn.getIdentifier();
		classTypeInfo.setClassName(className);
		//start position and length
		classTypeInfo.setClassNameStart(sn.getStartPosition());
		classTypeInfo.setClassNameLength(sn.getLength());
		classTypeInfo.setClassValidFrom(node.getStartPosition());
		classTypeInfo.setClassValidLength(node.getLength());

		//get inner class name with format of a.b
		String fullInnerClassName = getDeclareClassName(node,
				".");
		classTypeInfo.setFullInnerClassName(fullInnerClassName);

		//get inner class name with format of a$b
		String fullInnerClassName2 = getDeclareClassName(node,
		       "$");
		classTypeInfo.setFullInnerClassName2(fullInnerClassName2);

		//親
		ClassTypeInfo father = getDeclareClass(node);
		classTypeInfo.setFather(father);

	    //完全修飾名
		String fullClassName = HelperFunc.getFullClassName(packageName,
				fullInnerClassName2);
		classTypeInfo.setFullClassName(fullClassName);

		//get modifiers
		int modifiers = node.getModifiers();
		if((modifiers & Modifier.PUBLIC) == Modifier.PUBLIC ){
			if(className.equals(fullInnerClassName)){
				sourceFileName = className + ".java";
			}
		}

		//super class
		Type superType = node.getSuperclassType();
		if(superType != null){
			MiddleType mt = new MiddleType(superType);
			classTypeInfo.setSuperClassMType(mt);
		}

		//インタフェース
		classTypeInfo.setInterfaceType(node.isInterface());
		List<Type> listInterface = node.superInterfaceTypes();
		List<MiddleType> listInterfaceTypes = new ArrayList<MiddleType>();
		for(int i = 0; i <listInterface.size();i++ ){
			Type interfaceType = (Type)listInterface.get(i);
			MiddleType mt = new MiddleType(interfaceType);
			listInterfaceTypes.add(mt);
		}
		classTypeInfo.setInterfaceMTypeList(listInterfaceTypes);
		return super.visit(node);
	}

	/**
	 * visit method declaration
	 */
	public boolean visit(MethodDeclaration node){

		curMethod = new MatchMethodInfo();
		methodList.add(curMethod);
		ClassTypeInfo  currentClass = getDeclareClass(node);
		currentClass.addMethod(curMethod);
		curMethod.setMyClassTypeInfo(currentClass);  //tu add for v3

		//戻り値タイプ
		Type returnType= node.getReturnType2();
		if(returnType != null){
		    MiddleType mt = new MiddleType(returnType,node.getExtraDimensions());
		    curMethod.setReturnMType(mt);
		}


		//開始位置
		curMethod.setOffset(node.getStartPosition());

		//メソッド長
		curMethod.setLength(node.getLength());

		//メソッド名
		SimpleName sname = node.getName();
		curMethod.setMethodName(sname.getIdentifier());
        curMethod.setMethodNameStartPos(sname.getStartPosition());
        curMethod.setMethodNameOffet(sname.getLength());


		//パラメータ
		List<SingleVariableDeclaration> listParams = node.parameters();
		if(listParams != null){
			curMethod.setParamNumber(listParams.size());
			for(int i = 0;i < listParams.size();i++){
				//引数も変数である
				SingleVariableDeclaration param = listParams.get(i);
				accessSingleVarDeclar(param,
						node.getStartPosition() + node.getLength() - 1 );
			}
		}

		//クラス名
		String curClassName = getDeclareClassName(node.getParent(),"$");
		curMethod.setClassName(curClassName);

		curMethod.setPackageName(packageName);

		return super.visit(node);

	}
	/**
	 * visit the node of field declaration
	 */
	public boolean visit(FieldDeclaration node){
		Type fieldType = node.getType();
		String fieldTypeStr = fieldType.toString();

		//フィールドタイプ
		MiddleType fieldMType = null;
		if(fieldType != null){
			fieldMType = new MiddleType(fieldType);
		}

		ClassTypeInfo  currentClass = getDeclareClass(node);
		//add by Qiu Song on 20090224 for 
		if(currentClass == null){
			return false;
		}
		//end of add by Qiu Song on 20090224 for 
		List<VariableDeclarationFragment> listFragments = node.fragments();
		if(listFragments != null && listFragments.size() > 0){
			for(int i= 0;i < listFragments.size();i++){
				VariableDeclarationFragment frag = listFragments.get(i);
				SimpleName filedSimpleName = frag.getName();
				String fieldName = filedSimpleName.getIdentifier();

				MatchField matchField = new MatchField();
				matchField.setFieldName(fieldName);
				matchField.setFieldType(fieldTypeStr);
				matchField.setFieldMType(fieldMType);
				ASTNode parent = node.getParent();
				matchField.setOffset(parent.getStartPosition());
				matchField.setLength(parent.getLength());
				matchField.setStartPosition(filedSimpleName.getStartPosition());
				matchField.setFieldLength(filedSimpleName.getLength());
				//クラス名
				String curClassName = getDeclareClassName(
						node.getParent(),"$");
				matchField.setClassName(curClassName);

				matchField.setPackageName(packageName);
				fieldList.add(matchField);

				currentClass.addField(matchField);
			}
		}

		return super.visit(node);
	}

	/***************************** private function **********************************/
	/**
	 * nodeを含むするクラスを取得
	 * @param node ノート
	 * @return　ノードを含むクラス
	 */
	private ClassTypeInfo getDeclareClass(ASTNode node){

	    ASTNode classType = node.getParent();
	    ClassTypeInfo cu = null;
	    int from = node.getStartPosition();
	    int length = node.getLength();
		while(classType != null){
			if(classType instanceof TypeDeclaration){
				TypeDeclaration classDeclaration = (TypeDeclaration)classType;
				//クラス名
				String curClassName = null;
				SimpleName sn = classDeclaration.getName();
				curClassName = sn.getIdentifier();
				cu = getClassTypeInfoByName(curClassName,from,length);
				break;

			}else{
				classType = classType.getParent();
			}
		}

		return cu;
	}

	/**
	 * クラス名より、ソースファイルで定義されるクラス情報の取得<br>
	 * @param className クラス名、クラス名には以下の可能性があります。<br>
	 *   MyInternalClass 1個Identifierのみのクラス名<br>
	 *   TestClass.MyInternalClass インナークラス名<br>
	 *   abc.TestClass.MyInternalClass パッケージ名を含むインナークラス名<br>
	 *   abc.TestClass$MyInternalClass バイトコードからのフールクラス名
	 * @param from　該当クラスの定義に含まれる要素の開始位置
	 * @param length　該当クラスの定義に含まれる要素の長さ
	 * @return
	 * 注意：ソースファイルを全部Visitした後に、このメソッドを呼ぶべき
	 * そうしないと、アクセスしていないクラスが存在しても、検索できない
	 */
	private ClassTypeInfo getClassTypeInfoByName(String className,
			int from,int length){
		if(className == null){
			return null;
		}
		List<ClassTypeInfo> listClass = new ArrayList<ClassTypeInfo>();
		for(int i = 0; i < classList.size();i++){
			ClassTypeInfo classTypeInfo =(ClassTypeInfo) classList.get(i);
			String className1 = classTypeInfo.getClassName();
			String className2 = classTypeInfo.getFullInnerClassName();
			String className3 = HelperFunc.getFullClassName(packageName,
					className2);
			String className4 = classTypeInfo.getFullClassName();

			if(className.equals(className1)||className.equals(className2)||
					className.equals(className3)||className.equals(className4)){

				if(isInValidScope(classTypeInfo,from,length)){

					listClass.add(classTypeInfo);
				}
			}
		}

		if(listClass.size() <= 0){
			return null;
		}
		//1個のみの場合
		if(listClass.size() == 1){
			return (ClassTypeInfo)listClass.get(0);
		}
		//複数がある場合、インナークラスが優先
		for(int i = 0; i < listClass.size();i++){
			ClassTypeInfo classTypeInfo =(ClassTypeInfo) listClass.get(i);
			if(classTypeInfo.getFather() != null){
				return classTypeInfo;
			}
		}

		return null;
	}

	/**
	 * 指定した要素がクラスのScopeにあるかどうか
	 * @param classTypeInfo
	 * @param from
	 * @param length
	 * @return
	 */
	private boolean isInValidScope(ClassTypeInfo classTypeInfo,
			int from,int length){
		ClassTypeInfo father = classTypeInfo.getFather();
		if(father == null){
			//topクラスの場合、全ソースに有効
			return true;
		}
		//インナークラスの場合、親クラスの範囲には有効
		if(from >= father.getClassValidFrom() &&
				from + length <= father.getClassValidFrom() + father.getClassValidLength()){
			return true;
		}

		return false;
	}

	/**
	 * Single variable's declaring
	 * @param param
	 * @param validToPosition
	 */
	private void accessSingleVarDeclar(SingleVariableDeclaration param,
			int validToPosition){

		Type typeParam = param.getType();
		String typeString = typeParam.toString();

		MatchVariableInfo var = new MatchVariableInfo();
		curMethod.addVariable(var);

		//変数名
		SimpleName sn = param.getName();
		var.setName(sn.getIdentifier());

		//変数タイプ
		var.setType(typeString);
		MiddleType varMType = null;
		if(typeParam != null){
			varMType = new MiddleType(typeParam,param.getExtraDimensions());
		}
		var.setVarMType(varMType);

		//変数の定義箇所
		var.setStartPosition(sn.getStartPosition());
		var.setLength(sn.getLength());

		//有効範囲の開始位置
		var.setValidFrom(param.getStartPosition());

		//有効範囲の終了位置:メソッドの終了位置
		var.setValidTo(validToPosition);
	}
	/**
	 * タイプ名を解析する
	 * @param type
	 * @return
	 */
	protected String analyzeType(MiddleType type, List<String> possibleTypeList) {
		if(type == null){
			return null;
		}

		if(type.isArrayType()){
			Type elementType = type.getElementType();
			MiddleType eMT = new MiddleType(elementType);
			String elementTypeStr = analyzeType(eMT, possibleTypeList);
			if(elementTypeStr != null){
				StringBuffer buf = new StringBuffer(elementTypeStr);
				for(int i = 0; i < type.getDimensions();i++){
					buf.append("[]");
				}
				return buf.toString();
			}else{
				return null;
			}
		}

		if(type.isPrimitiveType()){
			return type.getTypeString();
		}


		if(type.isVoid()){
			return type.getTypeString();
		}
		return analyzeClassType(type, possibleTypeList);
	}

	/**
	 * クラスタイプを解析する
	 * @param classType
	 * @return スーパークラス名（パッケージ名を含む）
	 */
	protected String analyzeClassType(MiddleType classType, List<String> possibleTypeList){

		String typeName = classType.getTypeString();

		// 障害#501 総称型対応
		int index = typeName.indexOf("<");
		if (index > 0) {
			typeName = typeName.substring(0, index);
		}

		//同じソースファイルにあるかどうか
		String fullClassName =null;
		ClassTypeInfo cu = getClassTypeInfoByName(typeName,classType.getStartPosition(),
				classType.getLength());
		if(cu != null){
			//同じファイルにも同名クラスが存在する可能性がある
			fullClassName = cu.getFullClassName();
			return fullClassName;
		}

		//import 文にあるかどうかをチェックする
		String importClassName = getSingleImportType(typeName);
		if(importClassName != null){
			return importClassName;
		}

		//?
		if(classType.isQualifiedType()){
//			JavaClass clazz = SettingManager.getSetting(
//			    ).getByteCodeContents(typeName);
//			if(clazz != null){
//				return 	typeName;
//			}
			Object ci = RepositoryFactory.getDefinedClassMap().get(typeName);
			if (ci != null){
				return 	typeName;
			}
		}

		//java.langのクラスであるかどうか
		//障害#529
		//String langClass = "java.lang." + typeName;
		String langClass = typeName.startsWith("java.lang.") ? typeName : "java.lang." + typeName;
		if(HelperFunc.isJavaLangClass(langClass)){
			return langClass;
		}

        //同じパッケージにあるか？
		fullClassName= HelperFunc.getFullClassName(packageName,	typeName);
		if (possibleTypeList != null) {
			possibleTypeList.add(fullClassName);
		}
		Object ci = RepositoryFactory.getDefinedClassMap().get(fullClassName);
		if (ci != null){
			return 	fullClassName;
		}
//		String fileContents = SettingManager.getSetting()
//		    .getJavaSourceContent(fullClassName);
//		if(fileContents != null){
//			return fullClassName;
//		}

		//複数のタイプをインポートするパッケージ名のリストの取得
		List<String> listImportM = getMultiImportType();
		if(listImportM.size() != 0 && typeName.lastIndexOf(".") <= 0){
			//import xxx.*の場合、
			for(int i = 0;i < listImportM.size();i++){
				String importPackageName = (String)listImportM.get(i);
				fullClassName= HelperFunc.getFullClassName(importPackageName,
						typeName);
				if(listImportM.size() == 1){
					return fullClassName;
				}else{
					//複数のimport xxxx.*がある場合、設定されたクラスパス
					//にあるクラスを有効な対象クラスとする？
//					JavaClass clazz = SettingManager.getSetting(
//					).getByteCodeContents(fullClassName);
//					if(clazz != null){
//						return fullClassName;
//					}
					if (possibleTypeList != null) {
						possibleTypeList.add(fullClassName);
					}
					Object o = RepositoryFactory.getDefinedClassMap().get(fullClassName);
					if (o != null){
						return 	fullClassName;
					}
				}

			}
		}
		return null;
	}
	/**
	 * クラス名を用いて、該当インポート文を検索する。
	 * @param className
	 * @return
	 */
	private String getSingleImportType(String className){
		if(className == null){
			return null;
		}
		String pointClassName = "." + className;
		for(int i = 0; i < importList.size();i++){
			ImportClassInfo importClassInfo =(ImportClassInfo) importList.get(i);
			if(!importClassInfo.isOnDemand()){
				String importName = importClassInfo.getName();
				if(importName.endsWith(pointClassName)){
					return importName;
				}
			}

		}
		return null;
	}
	/**
	 * 複数タイプをインポートするパッケージ名のリストを取得
	 * @return
	 */
	private List<String> getMultiImportType(){
		List<String> list = new ArrayList<String>();
		for(int i = 0; i < importList.size();i++){
			ImportClassInfo importClassInfo =(ImportClassInfo) importList.get(i);
			if(importClassInfo.isOnDemand()){
				String importName = importClassInfo.getName();
				list.add(importName);
			}

		}

		return list;
	}

}
