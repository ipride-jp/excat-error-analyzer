package jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.ClassTypeInfo;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.JavaSourceVisitor;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MatchField;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MatchMethodInfo;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.apache.bcel.classfile.Field;
import org.apache.bcel.classfile.JavaClass;
import org.apache.bcel.classfile.Method;
import org.apache.bcel.generic.Type;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

/**
 * クラスの情報を管理するクラス
 * @author iPride_Demo
 *
 */
public class ClassRepository {

	/**
	 * 未定義クラスのマップ
	 */
	private Map<String, ClassTypeInfo> unDefinedClassMap = new HashMap<String, ClassTypeInfo>();

	/**
	 * 定義クラスのマップ
	 */
	private Map<String, ClassTypeInfo> definedClassMap  = new HashMap<String, ClassTypeInfo>();

	/**
	 * プリミティブタイプ間変換可能のタイプのset
	 */
	private Set<String> wideCastSet = new HashSet<String>();

	/**
	 * Siglenton
	 */
	private static ClassRepository instance = null;


	/**
	 * 当クラスの唯一のインスタンスを取得する
	 * @return
	 */
	public static synchronized ClassRepository getInstance(){
		if(instance == null){
			instance = new ClassRepository();
			instance.initWideCastMap();
		}

		return instance;
	}

	/**
	 * クラスパス、ソースパスが変更された場合、reset
	 *
	 */
	public void reset(){
		definedClassMap.clear();
		unDefinedClassMap.clear();

	}

	private void initWideCastMap(){
		wideCastSet.add("byte,short");
		wideCastSet.add("byte,int");
		wideCastSet.add("byte,long");
		wideCastSet.add("byte,float");
		wideCastSet.add("byte,double");
		wideCastSet.add("short,int");
		wideCastSet.add("short,long");
		wideCastSet.add("short,float");
		wideCastSet.add("short,double");
		wideCastSet.add("int,long");
		wideCastSet.add("int,float");
		wideCastSet.add("int,double");
		wideCastSet.add("long,float");
		wideCastSet.add("long,double");
		wideCastSet.add("float,double");
	}

	/**
	 * プリミティブタイプの変換がWidenかどうか
	 * @param invokerType 呼び出しのタイプ
	 * @param definedType 宣言タイプ
	 * @return
	 */
	public boolean isWidenPrimitiveConversion(String invokerType,
			String definedType){
		StringBuffer buf = new StringBuffer(invokerType);
		buf.append(',');
		buf.append(definedType);
		return wideCastSet.contains(buf.toString());
	}

	/**
	 * サブタイプであるかどうか
	 * @param invokerType 呼び出しのタイプ
	 * @param definedType 宣言タイプ
	 * @return
	 */
	public boolean isSubType(String invokerType,
			String definedType){

		if(invokerType == null || definedType == null){
			return false;
		}

		if(unDefinedClassMap.get(invokerType) != null ||
				unDefinedClassMap.get(definedType) != null){
			//解決不能のタイプ
			return false;
		}
		ClassTypeInfo invokderCu = getClassUnit(invokerType);
		if(invokderCu == null){
			return false;
		}
		ClassTypeInfo definedCu = getClassUnit(definedType);
		if(definedCu == null){
			return false;
		}
		if(!definedCu.isInterfaceType()){
			String superClassType = invokderCu.getSuperClassName();
			if(definedType.equals(superClassType)){
				return true;
			}else{
				return isSubType(superClassType,definedType);
			}
		}else{
			//インターフェースである
			List<String> interfaceList = invokderCu.getInterfaceTypeList();
			if(interfaceList == null || interfaceList.size() == 0){
				return false;
			}
			boolean found = false;
			for(int i= 0; i <interfaceList.size();i++){
				String interfaceType = interfaceList.get(i);
				if(definedType.equals(interfaceType)){
					found = true;
					break;
				}
			}

			if(found){
				return true;
			}else{
				for(int i= 0; i <interfaceList.size();i++){
					String interfaceType = interfaceList.get(i);
					if(isSubType(interfaceType,definedType)){
						found = true;
						break;
					}
				}

				return found;
			}
		}

	}

	/**
	 * クラスの定義を取得
	 * @param fullClassName
	 * @return
	 */
	public synchronized ClassTypeInfo getClassUnit(String fullClassName){

		if(unDefinedClassMap.get(fullClassName) != null){
			return null;
		}

		ClassTypeInfo cu = (ClassTypeInfo)definedClassMap.get(fullClassName);
		if(cu == null){
			//get definition of this class
			JavaClass clazz = SettingManager.getSetting(
			    ).getByteCodeContents(fullClassName);
			if(clazz != null){
				registerClassInfoFromByteCode(fullClassName,clazz);
				cu = (ClassTypeInfo)definedClassMap.get(fullClassName);
			}else{
				//get definition from source
				String fileContents = SettingManager.getSetting()
			        .getJavaSourceContent(fullClassName);
				if(fileContents != null){
					registerClassInfoFromSource(fileContents);
					cu = (ClassTypeInfo)definedClassMap.get(fullClassName);
				}else{
					//this class can't be resolved
//					unDefinedClassMap.put(fullClassName, new Object());
					unDefinedClassMap.put(fullClassName, new ClassTypeInfo());   //modify by tu
				}
			}
		}
		return cu;
	}

	/**
	 * Javaソースコードより、クラスの情報を登録する
	 * @param fileContents
	 */
	private void registerClassInfoFromSource(String fileContents){

		//build ast tree
		char[] source = fileContents.toCharArray();
		ASTParser parser = ASTParser.newParser(AST.JLS3);  // handles JDK 1.0, 1.1, 1.2, 1.3, 1.4, 1.5
		parser.setSource(source);
		CompilationUnit compileUnit = (CompilationUnit) parser.createAST(null);

		//visit the tree
		JavaSourceVisitor sourceVisitor = new JavaSourceVisitor();
		compileUnit.accept(sourceVisitor);
		sourceVisitor.resolveType();
	}

	/**
	 * クラスファイルより、クラスの情報を登録する
	 * @param clazz
	 * @return
	 */
	private void registerClassInfoFromByteCode(String fullClassName,JavaClass clazz){

		ClassTypeInfo cu = new ClassTypeInfo();

		//クラスの完全修飾名
		String className = clazz.getClassName();
		cu.setFullClassName(className);
		String superClassName = clazz.getSuperclassName();
		cu.setSuperClassName(superClassName);

		//when class is Object,bcel return Object for it's super class
		if("java.lang.Object".equals(className)){
			cu.setSuperClassName(null);
		}

		//register interfaces
		String[] interfaceNames = clazz.getInterfaceNames();
		List<String> interfaceList = new ArrayList<String>();
		for(int i = 0; i < interfaceNames.length;i++){
			interfaceList.add(interfaceNames[i]);
		}
		cu.setInterfaceTypeList(interfaceList);
		cu.setInterfaceType(clazz.isInterface());

		//register methods
		Method[] methods = clazz.getMethods();
		if(methods != null && methods.length > 0){
			for(int i= 0;i < methods.length;i++){
				MatchMethodInfo mu = new MatchMethodInfo();
				String methodName = methods[i].getName();
				String signature = methods[i].getSignature();
				if("<init>".equals(methodName)){
					methodName = HelperFunc.getPureClassName(
							className);
				}
				mu.setMethodName(methodName);
				String mConvertedSig = HelperFunc.convertMethodSig(signature);
				mu.setMethodSignature(mConvertedSig);
				//引数のタイプのリストを取得
				List<String> paramTypeList = HelperFunc.getParamsListFromSignature(signature);
				mu.setParamTypeList(paramTypeList);
				mu.setParamNumber(paramTypeList.size());
				Type returnType = methods[i].getReturnType();
				mu.setReturnType(HelperFunc.convertClassSig(
						returnType.getSignature()));
				cu.addMethod(mu);
				mu.setMyClassTypeInfo(cu);  //tu add for v3
			}
		}

		//register field
		Field[] fields = clazz.getFields();
		if(fields != null && fields.length > 0){
			for(int i = 0; i < fields.length; i++){
				MatchField fu = new MatchField();
				fu.setFieldName(fields[i].getName());
				fu.setFullFieldType(HelperFunc.convertClassSig(
						fields[i].getSignature()));
				cu.addField(fu);
			}
		}
		definedClassMap.put(fullClassName, cu);
	}

	/**
	 * ソースコードより、クラスの定義を追加する
	 * @param classTypeInfo
	 */
	public synchronized void addClassUnitFromSource(ClassTypeInfo classTypeInfo){

		String fullClassName = classTypeInfo.getFullClassName();
		//ソースコードに変数情報があるので、ソースコードのほうが優先
		ClassTypeInfo cu = (ClassTypeInfo)definedClassMap.get(fullClassName);
		if(cu != null){
			//既に登録されている,remove it
			definedClassMap.remove(fullClassName);
		}

		definedClassMap.put(fullClassName, classTypeInfo);
		unDefinedClassMap.remove(fullClassName);
	}
}
