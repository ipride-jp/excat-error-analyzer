package jp.co.ipride.excat.common.setting.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.ClassTypeInfo;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MatchField;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MatchMethodInfo;
import jp.co.ipride.excat.common.setting.SettingUtility;
import jp.co.ipride.excat.common.utility.Utility;

import org.apache.bcel.classfile.JavaClass;

/**
 * ソース若しくはクラス・ファイルからの以下の情報を格納する。
 * １．クラスの情報
 * ２．メソッドの情報
 * @author tu-ipride
 * @version 3.0
 * @date 2009/9/15
 */
public class Repository implements Serializable{

	private static final long serialVersionUID = 2L;

	//Key=インターフェース名
	private Map<String, ClassTypeInfo> interfaceMap = null;

	//Key=クラス名
	private Map<String, ClassTypeInfo> classMap = null;

	private List<ClassTypeInfo> exceptionList = null;

	private List<String> packageList = null;

	private List<String> javaFileList = null;

	private static Repository instance = new Repository();

	private Repository(){
	}

	public List<String> getJavaFileList() {
		return javaFileList;
	}

	public void setJavaFileList(List<String> javaFileList) {
		this.javaFileList = javaFileList;
	}

	public static Repository getInstance(){
		return instance;
	}

	/*
	 * リポジトリ再構築
	 */
	public void reset(){
		if (interfaceMap != null){
			interfaceMap.clear();
			interfaceMap=null;
		}
		if (classMap != null){
			classMap.clear();
			classMap=null;
		}
		if (exceptionList != null){
			exceptionList.clear();
			exceptionList=null;
		}
		if (packageList != null){
			packageList.clear();
			packageList=null;
		}
		if (javaFileList != null){
			javaFileList.clear();
			javaFileList=null;
		}
	}

	public Map<String, ClassTypeInfo> getInterfaceMap() {
		return interfaceMap;
	}

	public void setInterfaceMap(Map<String, ClassTypeInfo> interfaceMap) {
		this.interfaceMap = interfaceMap;
	}

	public Map<String, ClassTypeInfo> getClassMap() {
		return classMap;
	}

	public void setClassMap(Map<String, ClassTypeInfo> classMap) {
		this.classMap = classMap;
	}

	/**
	 * インターフェース一覧を取得
	 * @return
	 */
	public String[] getInterfaceList(){
		if (interfaceMap == null) {
			return null;
		}
		String[] interfaceList = new String[interfaceMap.keySet().size()];
		Iterator<String> iterator = interfaceMap.keySet().iterator();
		int i=0;
		while(iterator.hasNext()){
			interfaceList[i]= iterator.next().toString();
			i++;
		}
		return interfaceList;
	}

	/**
	 * クラス一覧（サブクラスも含む）を取得
	 * @return
	 */
	public String[] getClassList(){
		if (classMap == null) {
			return null;
		}
		String[] classList = new String[classMap.keySet().size()];
		Iterator<String> iterator = classMap.keySet().iterator();
		int i=0;
		while(iterator.hasNext()){
			classList[i]= iterator.next().toString();
			i++;
		}
		return classList;
	}

	/**
	 * インターフェースのメソッド一覧（staticも含む）を取得
	 * @param path
	 * @return
	 */
	public List<MatchMethodInfo> getInterfaceMethod(String path){
		ClassTypeInfo info = (ClassTypeInfo)interfaceMap.get(path);
		return info.getMethodList();
	}

	/**
	 * クラスの実装したメソッド（abstractを含まない）一覧を取得
	 * @param path
	 * @return
	 */
	public List<MatchMethodInfo> getClassMethod(String path){
		ClassTypeInfo info = (ClassTypeInfo)classMap.get(path);
		return info.getMethodList();
	}

	public void setExceptionList(List<ClassTypeInfo> exceptionList) {
		this.exceptionList = exceptionList;
	}


	public String[] getPackageList() {
		if (packageList == null) {
			return null;
		}
		String[] list = new String[packageList.size()];
		for (int i=0; i<packageList.size(); i++){
			list[i]=(String)packageList.get(i);
		}
		return list;
	}

	public void setPackageList(List<String> packageList) {
		this.packageList = packageList;
	}

	/**
	 * 例外一覧を取得する。
	 * @return
	 */
	public String[] getExceptionList(){
		if (exceptionList == null) {
			return null;
		}
		String[] list = new String[exceptionList.size()];
		for (int i=0; i<exceptionList.size(); i++){
			String a = ((ClassTypeInfo)exceptionList.get(i)).getFullClassName();
			if ("java.lang.Throwable".equals(a)){
				continue;
			}
			list[i]=a;
		}
		return list;
	}

	/**
	 * 指定のクラスのメソッドを取得
	 * @param className
	 * @return
	 */
	public String[] getClassMethodList(String className){
		if (classMap == null) {
			return null;
		}
		ClassTypeInfo classInfo = (ClassTypeInfo)classMap.get(className);
		if (classInfo != null){
			List<MatchMethodInfo> methodList = classInfo.getMethodList();
			List<String> methodCandidates = new ArrayList<String>();
			for (int i=0; i<methodList.size(); i++){
				MatchMethodInfo method = (MatchMethodInfo)methodList.get(i);
				String mehod = method.getMethodName();
				if (!methodCandidates.contains(mehod)){
					methodCandidates.add(mehod);
				}
			}
			return Utility.transferToStrings(methodCandidates);
		}else{
			return null;
		}
	}
	/**
	 * 指定のインターフェースのメソッドを取得
	 * @param interfaceName
	 * @return
	 */
	public String[] getInterfaceMethodList(String interfaceName){
		if (interfaceMap == null) {
			return null;
		}
		ClassTypeInfo classInfo = (ClassTypeInfo)interfaceMap.get(interfaceName);
		if (classInfo != null){
			List<MatchMethodInfo> methodList = classInfo.getMethodList();
			List<String> methodCandidates = new ArrayList<String>();
			for (int i=0; i<methodList.size(); i++){
				MatchMethodInfo method = (MatchMethodInfo)methodList.get(i);
				String mehod = method.getMethodName();
				if (!methodCandidates.contains(mehod)){
					methodCandidates.add(mehod);
				}
			}
			return Utility.transferToStrings(methodCandidates);
		}else{
			return null;
		}
	}

	/**
	 * 指定のメソッド名のシグネチャの一覧を取得
	 * @param className
	 * @param methodName
	 * @return　シグネチャの一覧
	 */
	public String[] getClassMethosSigList(String className, String methodName){
		if (classMap == null) {
			return null;
		}
		ClassTypeInfo classInfo = (ClassTypeInfo)classMap.get(className);
		if (classInfo != null) {
			List<MatchMethodInfo> methodList = classInfo.getMethodList();
			List<String> sigList = new ArrayList<String>();
			for (int i=0; i<methodList.size(); i++){
				MatchMethodInfo method = (MatchMethodInfo)methodList.get(i);
				if (method.getMethodName().equals(methodName)){
					sigList.add(method.getMethodSignature());
				}
			}
			return Utility.transferToStrings(sigList);
		}
		return null;
	}
	/**
	 * 指定のメソッド名のシグネチャの一覧を取得
	 * @param interfaceName
	 * @param methodName
	 * @return　シグネチャの一覧
	 */
	public String[] getInterfaceMethosSigList(String interfaceName, String methodName){
		if (interfaceMap == null) {
			return null;
		}
		ClassTypeInfo classInfo = (ClassTypeInfo)interfaceMap.get(interfaceName);
		if (classInfo != null) {
			List<MatchMethodInfo> methodList = classInfo.getMethodList();
			List<String> sigList = new ArrayList<String>();
			for (int i=0; i<methodList.size(); i++){
				MatchMethodInfo method = (MatchMethodInfo)methodList.get(i);
				if (method.getMethodName().equals(methodName)){
					sigList.add(method.getMethodSignature());
				}
			}
			return Utility.transferToStrings(sigList);
		}
		return null;
	}

	/**
	 * 指定のクラスを継承したクラスの一覧を取得する。
	 * 孫以降も含む。
	 * @param className
	 * @return　継承したクラスの一覧
	 */
	public String[] getAllImplementClassList(String className){
		if (classMap == null) {
			return null;
		}
		ClassTypeInfo classInfo=null;
		classInfo= (ClassTypeInfo)classMap.get(className);
		if (classInfo != null){
			List<String> list = classInfo.getAllImplClassNameList();
			return Utility.transferToStrings(list);
		}else if ("java.lang.Throwable".equals(className) ||
				"java.lang.Exception".equals(className)){
			return getExceptionList();
		}else{
			return null;
		}
	}

	public String[] getAllImplementClassForThisInterfaceMethod(String interfaceName,
			String methodName, String sig){
		if (interfaceMap == null) {
			return null;
		}
		ClassTypeInfo interfaceInfo = (ClassTypeInfo)interfaceMap.get(interfaceName);
		if (interfaceInfo == null){
			return null;
		}
		MatchMethodInfo methodInfo = interfaceInfo.getMethodBySignature(methodName, sig);
		if (methodInfo == null){
			return null;
		}
		List<ClassTypeInfo> list = methodInfo.getImplClassList();
		if (list == null){
			return null;
		}
		String[] classList = new String[list.size()];
		for (int i=0; i<list.size(); i++){
			classList[i]=list.get(i).getFullClassName();
		}
		return classList;
	}

	public String[] getAllImplementClassForThisInterface(String interfaceName){
		if (interfaceMap == null) {
			return null;
		}
		ClassTypeInfo interfaceInfo = (ClassTypeInfo)interfaceMap.get(interfaceName);
		if (interfaceInfo == null){
			return null;
		}
		List<String> classList = interfaceInfo.getAllImplClassNameList();
		return Utility.transferToStrings(classList);
	}

	public ClassTypeInfo getClassTypeInfo(String className){
		if (classMap == null) {
			return null;
		}
		return classMap.get(className);
	}

	public JavaClass getByteCodeContents(String name){
		if (classMap == null) {
			return null;
		}
		ClassTypeInfo classInfo = (ClassTypeInfo)classMap.get(name);
		if (classInfo == null){
			//haven't this file.
			return null;
		}
		String fullPath = classInfo.getClassFileFullPath();
		String jarPath = classInfo.getJarPathForClassFile();
		//case1:  the file is in jar file.
		if (jarPath != null && fullPath != null){
			return SettingUtility.getJavaClassFromJarOrZipUsingFullPath(jarPath, fullPath);
		//case2: the file is a single class file.
		}else if (jarPath == null && fullPath != null){
			return SettingUtility.getJavaClassFromClassFile(fullPath);
		}
		return null;  //no problem.
	}

	public String getClassFileFullPath(String className){
		if (classMap == null) {
			return null;
		}
		ClassTypeInfo classInfo = (ClassTypeInfo)classMap.get(className);
		if (classInfo == null){
			//haven't this file.
			return null;
		}
		String fullPath = classInfo.getClassFileFullPath();
		String jarPath = classInfo.getJarPathForClassFile();
		if (jarPath != null){
			return jarPath;
		}else{
			return fullPath;
		}
	}

	public String getJavaSourceContent(String name){
		if (classMap == null) {
			return null;
		}
		ClassTypeInfo classInfo = (ClassTypeInfo)classMap.get(name);
		if (classInfo == null){
			return null;  //no information
		}
		String fullPath = classInfo.getJavaFileFullPath();
		String jarPath = classInfo.getJarPathForJavaFile();

		//case1:this file is in jar file.
		if (jarPath != null && fullPath != null){
			return SettingUtility.getJavaFileFromJarOrZipUsingFullPath(jarPath, fullPath);
		//case2: this file is a single java file.
		}else if (jarPath == null && fullPath != null){
			return SettingUtility.getContents(fullPath);
		}
		return null;
	}

	public String getJavaFileFullPath(String className){
		if (classMap == null) {
			return null;
		}
		ClassTypeInfo classInfo = (ClassTypeInfo)classMap.get(className);
		if (classInfo == null){
			return null;  //no information
		}
		String fullPath = classInfo.getJavaFileFullPath();
		String jarPath = classInfo.getJarPathForJavaFile();
		if (jarPath != null){
			return jarPath;
		}else{
			return fullPath;
		}
	}

	public String getJavaFileName(String className){
		if (classMap == null) {
			return null;
		}
		ClassTypeInfo classInfo = (ClassTypeInfo)classMap.get(className);
		if (classInfo == null){
			return null;  //no information
		}
		String fullPath = classInfo.getJavaFileFullPath();
		int index = fullPath.lastIndexOf("\\");
		if (index >=0){
			return fullPath.substring(index+1);
		}else{
			return fullPath;
		}
	}

	public boolean isInterface(String typeName){
		if (interfaceMap == null) {
			return false;
		}
		ClassTypeInfo classInfo = interfaceMap.get(typeName);
		if (classInfo == null){
			return false;
		}else{
			return true;
		}
	}

	public String getHasFieldClassName(String className, String fieldName){
		if (classMap == null) {
			return null;
		}
		ClassTypeInfo classInfo = (ClassTypeInfo)classMap.get(className);
		if (classInfo == null){
			return null;  //no information
		}
		List<MatchField> matchFieldList = classInfo.getFieldList();
		for (MatchField matchField : matchFieldList){
			if (fieldName.equals(matchField.getFieldName())){
				return className;
			}
		}
		String superClassName = classInfo.getSuperClassName();
		return getHasFieldClassName(superClassName, fieldName);
	}

	public String getClassNameByFileName(String fileName) {
		for(Object o : classMap.keySet()){
			ClassTypeInfo classInfo = (ClassTypeInfo)classMap.get(o);
			if (fileName.equals(classInfo.getJavaFileFullPath())) {
				return classInfo.getFullClassName();
			}
		}
		return "";
	}

}
