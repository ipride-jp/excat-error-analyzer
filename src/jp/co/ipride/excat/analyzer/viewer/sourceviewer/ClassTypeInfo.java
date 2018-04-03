package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ソースファイルに定義されるクラスの情報
 * @author jiang
 * @date 2009/9/23 add for v3 by tu
 */
public class ClassTypeInfo{

	/**
	 * クラス名
	 */
	private String className = null;

	/**
	 * クラス名の開始位置
	 */
	private int classNameStart = 0;

	/**
	 * クラス名のの長さ
	 */
	private int classNameLength = 0;

	/**
	 * クラス定義の開始位置
	 */
	private int classValidFrom = 0;

	/**
	 * クラス定義の有効位置
	 */
	private int classValidLength = 0;

	/**
	 * 親クラス
	 */
	private ClassTypeInfo father = null;

	/**
	 * インナークラスである場合、親クラスを含むクラス名
	 * インナークラス名とアウトクラス名の間が"."である.
	 * パッケージ名をふくまない
	 */
	private String fullInnerClassName = null;

	/**
	 * インナークラスである場合、親クラスを含むクラス名
	 * インナークラス名とアウトクラス名の間が"$"である。
	 * パッケージ名をふくまない
	 */
	private String fullInnerClassName2 = null;

	/**
	 * メソッドのリスト
	 */
	private List<MatchMethodInfo> methodList = new ArrayList<MatchMethodInfo>();

	/**
	 * フィールドのリスト
	 */
	private List<MatchField> fieldList = new ArrayList<MatchField>();

	/**
	 * スーパークラスの中間タイプ
	 */
	private MiddleType superClassMType = null;

	/**
	 * スーパークラスの完全修飾名
	 */
	private String superClassName = null;

	/**
	 * フールクラス名
	 */
	private String fullClassName = null;

	/**
	 * インターフェースであるかどうか
	 */
	private boolean interfaceType = false;

	/**
	 * abstract
	 */
	private boolean abstractType = false;

	/**
	 * 実装したインターフェースのリスト
	 */
	private List<String> interfaceTypeList = null;

	/**
	 * 実装したインタフェースの中間タイプのリスト
	 */
	private List<MiddleType> interfaceMTypeList = null;

	/**
	 * クラス名.java
	 */
	private String sourceFileName = null;

	/**
	 * add for v3
	 */
	private String jarPathForClassFile = null;

	/**
	 * add for v3
	 */
	private String jarPathForJavaFile = null;

	/**
	 * add for v3
	 */
	private String classFileFullPath = null;

	/**
	 * add for v3
	 */
	private String javaFileFullPath = null;

	/**
	 * add for v3
	 */
	private List<ClassTypeInfo> implClassList = null;

	/**
	 * add for v3
	 */
	private boolean exceptionType = false;

	/**
	 * add for v3
	 */
	private Map<String, List<String>> possibleInterfaceType = null;

	public void addPossibleInterface(String simpleName, String fullName) {
		if (possibleInterfaceType == null) {
			possibleInterfaceType = new HashMap<String, List<String>>();
		}
		List<String> typeList = possibleInterfaceType.get(simpleName);
		if (typeList == null) {
			typeList = new ArrayList<String>();
			typeList.add(fullName);
			possibleInterfaceType.put(simpleName, typeList);
		} else {
			typeList.add(fullName);
		}
	}

	public Map<String, List<String>> getPossibleInterfaceType() {
		return possibleInterfaceType;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public int getClassNameLength() {
		return classNameLength;
	}

	public void setClassNameLength(int classNameLength) {
		this.classNameLength = classNameLength;
	}

	public int getClassNameStart() {
		return classNameStart;
	}

	public void setClassNameStart(int classNameStart) {
		this.classNameStart = classNameStart;
	}

	/**
	 * インナークラス名とアウトクラス名の間が"."である.
	 * @return
	 */
	public String getFullInnerClassName() {
		return fullInnerClassName;
	}

	public void setFullInnerClassName(String fullInnerClassName) {
		this.fullInnerClassName = fullInnerClassName;
	}

	/**
	 * インナークラス名とアウトクラス名の間が"$"である.
	 * @return
	 */
	public String getFullInnerClassName2() {
		return fullInnerClassName2;
	}

	public void setFullInnerClassName2(String fullInnerClassName2) {
		this.fullInnerClassName2 = fullInnerClassName2;
	}

	public void addMethod(MatchMethodInfo mi){
		methodList.add(mi);
	}

	public void addField(MatchField mf){
		fieldList.add(mf);

	}

	public MatchMethodInfo getMethodBySignature(String name,String sig){

		for(int i = 0; i< methodList.size();i++){
			MatchMethodInfo mi = methodList.get(i);
			String declareName = mi.getMethodName();
			String declareSig = mi.getMethodSignature();
			if(name.equals(declareName) && sig.equals(declareSig)){
				return mi;
			}
		}

		return null;
	}

	/**
	 * to be changed
	 * @param methodName
	 * @param
	 * @return
	 */
	public MatchMethodInfo getMethod(String methodName,List<String> paramsList){
		//まず、メソッド名で検索
		int paramNumber = paramsList.size();
		List<MatchMethodInfo> firstMatchedList = getMethodByeNameAndParamNum(methodName,paramNumber);
		int firstMatchedSize = firstMatchedList.size();
		if(firstMatchedSize == 0){
			return null;
		}
		//１個のみ
		if(firstMatchedSize == 1){
			return (MatchMethodInfo)firstMatchedList.get(0);
		}

		//同名メソッドが存在
		MatchMethodInfo goodMethod = null;
		int minRank = Integer.MAX_VALUE;
		for(int i = 0; i< firstMatchedList.size();i++){
			MatchMethodInfo mi = (MatchMethodInfo)firstMatchedList.get(i);
            int rank = mi.getParamsMatchRank(paramsList);
            if(minRank > rank){
            	minRank = rank;
            	goodMethod = mi;
            }
		}
		return goodMethod;
	}

	/**
	 * メソッド名と引数の数でメソッドを検索する
	 * @param methodName
	 * @param paramNumber
	 * @return
	 */
	private List<MatchMethodInfo> getMethodByeNameAndParamNum(String methodName,int paramNumber){
		List<MatchMethodInfo> matchedMethodList = new ArrayList<MatchMethodInfo>();
		for(int i = 0; i< methodList.size();i++){
			MatchMethodInfo mi = (MatchMethodInfo)methodList.get(i);
			if(methodName.equals(mi.getMethodName()) &&
					paramNumber== mi.getParamNumber()){
				matchedMethodList.add(mi);
			}
		}

		return matchedMethodList;
	}

	public String getSuperClassName() {
		return superClassName;
	}

	public void setSuperClassName(String superClassName) {
		this.superClassName = superClassName;
	}

	public String getFullClassName() {
		return fullClassName;
	}

	public void setFullClassName(String fullClassName) {
		this.fullClassName = fullClassName;
	}

	public String getSourceFileName() {
		return sourceFileName;
	}

	public void setSourceFileName(String sourceFileName) {
		this.sourceFileName = sourceFileName;
	}

	/**
	 * 指定した場所が所属するメソッドを取得する
	 * @param offset
	 * @param length
	 * @return
	 */
	private MatchMethodInfo getMatchedMethod(int offset,int length){

		for(int i= 0; i <  methodList.size();i++){
			MatchMethodInfo methodInfo = (MatchMethodInfo)methodList.get(i);
			if(offset >= methodInfo.getOffset() &&
					offset + length <= methodInfo.getOffset() + methodInfo.getLength()){
				return methodInfo;
			}
		}

		return null;
	}

	/**
	 * 該当変数の定義を取得
	 * @param offset
	 * @param length
	 * @param word
	 * @return
	 */
	public MatchVariableInfo getMatchedVar(int offset,int length,String word){

		//check if inside a method
		MatchMethodInfo methodInfo = getMatchedMethod(offset,length);

		if(methodInfo == null){
			return null;
		}

		//check if word is a variable
		MatchVariableInfo var = methodInfo.getVariableInfo(offset,length,word);
		return var;
	}

	/**
	 * 該当フィールドの定義情報を取得
	 * @param word
	 * @return
	 */
	public  MatchField getMatchedField(String word){

		if(word == null){
			return null;
		}
		for(int i= 0; i <  fieldList.size();i++){
			MatchField matchField = (MatchField)fieldList.get(i);
			if(word.equals(matchField.getFieldName())){
				return matchField;
			}
		}
		return null;
	}

	public int getClassValidFrom() {
		return classValidFrom;
	}

	public void setClassValidFrom(int classValidFrom) {
		this.classValidFrom = classValidFrom;
	}

	public int getClassValidLength() {
		return classValidLength;
	}

	public void setClassValidLength(int classValidLength) {
		this.classValidLength = classValidLength;
	}

	public ClassTypeInfo getFather() {
		return father;
	}

	public void setFather(ClassTypeInfo father) {
		this.father = father;
	}

	public MiddleType getSuperClassMType() {
		return superClassMType;
	}

	public void setSuperClassMType(MiddleType superClassMType) {
		this.superClassMType = superClassMType;
	}

	public List<MatchField> getFieldList() {
		return fieldList;
	}

	public List<MatchMethodInfo> getMethodList() {
		return methodList;
	}

	public List<String> getInterfaceTypeList() {
		return interfaceTypeList;
	}

	public void setInterfaceTypeList(List<String> interfaceTypeList) {
		this.interfaceTypeList = interfaceTypeList;
	}

	public List<MiddleType> getInterfaceMTypeList() {
		return interfaceMTypeList;
	}

	public void setInterfaceMTypeList(List<MiddleType> interfaceMTypeList) {
		this.interfaceMTypeList = interfaceMTypeList;
	}

	public boolean isInterfaceType() {
		return interfaceType;
	}

	public void setInterfaceType(boolean interfaceType) {
		this.interfaceType = interfaceType;
	}

	public String getClassFileFullPath() {
		return classFileFullPath;
	}

	public void setClassFileFullPath(String classFileFullPath) {
		this.classFileFullPath = classFileFullPath;
	}

	public String getJavaFileFullPath() {
		return javaFileFullPath;
	}

	public void setJavaFileFullPath(String javaFileFullPath) {
		this.javaFileFullPath = javaFileFullPath;
	}

	public String getJarPathForClassFile() {
		return jarPathForClassFile;
	}

	public void setJarPathForClassFile(String jarPathForClassFile) {
		this.jarPathForClassFile = jarPathForClassFile;
	}

	public String getJarPathForJavaFile() {
		return jarPathForJavaFile;
	}

	public void setJarPathForJavaFile(String jarPathForJavaFile) {
		this.jarPathForJavaFile = jarPathForJavaFile;
	}

	public boolean isAbstractType() {
		return abstractType;
	}

	public void setAbstractType(boolean abstractType) {
		this.abstractType = abstractType;
	}

	public List<ClassTypeInfo> getImplClassList(){
		return implClassList;
	}

	/**
	 * 指定の該当クラスを継承した全てのサブクラスの一覧を取得
	 * @version 3.0
	 * @return
	 */
	public List<String> getAllImplClassNameList(){
		ClassTypeInfo classInfo = null;

		if (implClassList == null){
			return null;
		}

		List<String> classNameList = new ArrayList<String>();

		for (int i =0; i<implClassList.size(); i++){
			classInfo = implClassList.get(i);
			if (classInfo.interfaceType){
				continue;
			}
			classNameList.add(classInfo.getFullClassName());
		}

		for (int i=0; i<implClassList.size(); i++){
			classInfo = implClassList.get(i);
			List<String> childList = classInfo.getAllImplClassNameList();
			if (childList != null){
				classNameList.addAll(childList);
			}
		}
		return classNameList;
	}

	/**
	 * thisオブジェクトを継承している他のインターフェース若しくはクラスを追加
	 * @version 3.0
	 * @since 2009/9/23
	 * @param cu
	 */
	public void addimplClass(ClassTypeInfo cu){
		if (implClassList == null){
			implClassList = new ArrayList<ClassTypeInfo>();
		}
		if (!implClassList.contains(cu)){
			implClassList.add(cu);
		}
	}

	/**
	 * インターフェースのメソッドを実装したクラスを抽出する。
	 */
	public void extraImplClassOfMethods(){
		if (interfaceType){
			for (int i=0; i<methodList.size(); i++){
				MatchMethodInfo method = methodList.get(i);
				if (!method.isStatic()){
					method.extraImplClassList();
				}
			}
		}
	}

	/**
	 * このメソッドは継承先のインターフェースのメソッドから呼ばれる。
	 * １．自分のメソッドをチェックする。
	 * ２．サブ・クラスのメソッドをチェックする。
	 * @param classMethod
	 * @return
	 */
	public void getImplClassListForThisMethod(MatchMethodInfo Method,
											List<ClassTypeInfo> result){
		//1.自分のメソッドをチェックする。
		MatchMethodInfo myMethod = this.getMethodBySignature(
											Method.getMethodName(),
											Method.getMethodSignature());
		if (myMethod != null && !this.isInterfaceType()
							&& !myMethod.isNative()
							&& !myMethod.isAbstract()
							&& !result.contains(this)){
			result.add(this);
		}
		//2.該当クラスを継承したクラスをチェック
		if (implClassList != null){
			for (int i=0; i<implClassList.size(); i++){
				ClassTypeInfo classInfo = implClassList.get(i);
				classInfo.getImplClassListForThisMethod(Method, result);
			}
		}
	}

	public void setExceptionType(boolean flag){
		this.exceptionType = flag;
		if (implClassList !=null){
			for (int i=0; i<implClassList.size(); i++){
				ClassTypeInfo classInfo = (ClassTypeInfo)implClassList.get(i);
				classInfo.setExceptionType(true);
			}
		}
	}

	public void setExceptionType() {
		if ("java.lang.Throwable".equals(className)
				|| "java.lang.Exception".equals(className)
				|| "java.lang.RuntimeException".equals(className)
				|| "java.lang.Throwable".equals(superClassName)
				|| "java.lang.Exception".equals(superClassName)
				|| "java.lang.RuntimeException".equals(superClassName)
				){
			setExceptionType(true);
		}
	}

	public boolean isExceptionType() {
		return exceptionType;
	}

	public String getPackageName(){
		int index;
		String packageName;
		index= fullClassName.lastIndexOf(".");
		if (index <0){
			packageName = "";
		}else{
			packageName = fullClassName.substring(0, index);
		}
		return packageName;
	}

}
