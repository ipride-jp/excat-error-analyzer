package jp.co.ipride.excat.common.setting.repository;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.bcel.classfile.JavaClass;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.ClassTypeInfo;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.setting.SettingUtility;
import jp.co.ipride.excat.common.sourceupdate.SourceCodeUpdateCancelledException;

/**
 * create a repository
 * @author tu-ipride
 * @version 3.0
 * @date 2009/9/19
 */
public class RepositoryFactory {

	static String[] FILE_EXTENSION = new String[]{
		"java",
		"jar",
		"zip",
		"class"
	};

	private static Repository repository = null;

	//extract files from user's setting.
	private static List<String> javaFileList = new ArrayList<String>();
	private static List<String> jarFileList = new ArrayList<String>();
	private static List<String> zipFileList = new ArrayList<String>();
	private static List<String> classFileList = new ArrayList<String>();

//	private static List<JavaSrcVisitor> visitorList = new ArrayList<JavaSrcVisitor>();

	//class name -> ClassTypeInfo
	private static Map<String,ClassTypeInfo> definedClassMap = new HashMap<String,ClassTypeInfo>();

	//class name -> ClassTypeInfo
	private static Map<String,ClassTypeInfo> interfaceMap = new HashMap<String,ClassTypeInfo>();

	//class name -> ClassTypeInfo
	private static Map<String,ClassTypeInfo> classMap = new HashMap<String,ClassTypeInfo>();
	//例外クラス(ClassTypeInfo)一覧
	private static List<ClassTypeInfo> exceptionList = new ArrayList<ClassTypeInfo>();

	private static List<String> packageList = new ArrayList<String>();
	/**
	 * call by visitor
	 * @return definedClassMap
	 */
	public static Map<String,ClassTypeInfo> getDefinedClassMap(){
		return definedClassMap;
	}

	/**
	 * リポジトリ作成を呼出す。
	 * @return
	 */
//	public static void createRepository(){
//		Thread thread = new Thread(){
//			public void run(){
//				createMyRepository();
//			}
//		};
//		thread.start();
//	}
//
	public static  synchronized void createRepository(IProgressMonitor monitor){
		repository = Repository.getInstance();
		repository.reset();

		try {
			//1.指定のパスand repositoryから「java」、「class」、「jar＆zip」一覧を抽出
			long t1 = System.currentTimeMillis();
			analyzeInputPath(monitor);
			long t2 = System.currentTimeMillis();
			//System.out.println("analyzeInputPath：" + (t2-t1));

			analyzeZipJarFile(jarFileList, monitor);
			long t3 = System.currentTimeMillis();
			//System.out.println("analyzeInputPath(jar)：" + (t3-t2));

			analyzeZipJarFile(zipFileList, monitor);
			long t4 = System.currentTimeMillis();
			//System.out.println("analyzeInputPath(Zip)：" + (t4-t3));

			//2.「java」、「class」、「」jar/zip」ファイル毎を解析する。
			analyzeJavaFile(monitor);
			long t5 = System.currentTimeMillis();
			//System.out.println("analyzeJavaFile：" + (t5-t4));

			analyzeClassFile(monitor);
			long t6 = System.currentTimeMillis();
			//System.out.println("analyzeClassFile：" + (t6-t5));

			classRelationAnalyze(monitor);
			long t7 = System.currentTimeMillis();
			//System.out.println("classRelationAnalyze：" + (t7-t6));

			methodRelationAnalyze(monitor);
			long t8 = System.currentTimeMillis();
			//System.out.println("methodRelationAnalyze：" + (t8-t7));

			//4．例外クラスを抽出する
			extraExceptionClass(monitor);
			long t9 = System.currentTimeMillis();
			//System.out.println("extraExceptionClass：" + (t9-t8));
		} catch (SourceCodeUpdateCancelledException e) {
			// キャンセルされた場合
			monitor.subTask(Message.get("Repository.Update.Cancelled"));
		}

		repository.setInterfaceMap(interfaceMap);
		repository.setClassMap(classMap);
		repository.setExceptionList(exceptionList);
		repository.setPackageList(packageList);
		repository.setJavaFileList(javaFileList);

		//javaFileList.clear();
		classFileList.clear();
		jarFileList.clear();
		zipFileList.clear();
//		visitorList.clear();
		definedClassMap.clear();
	//	interfaceMap.clear();
	//	classMap.clear();
	//	packageList.clear();
	//	exceptionList.clear();
		System.gc();
	}

	/**
	 * 1.指定のパスから「java」、「class」、「jar」、「zip」ファイル一覧を抽出
	 * @throws SourceCodeUpdateCancelledException
	 */
	private static void analyzeInputPath(IProgressMonitor monitor) throws SourceCodeUpdateCancelledException{
		ArrayList<String> pathList = SettingManager.getSetting().getAllPathList();
		for (int i=0; i<pathList.size(); i++){
			String path = pathList.get(i);
			File file = new File(path);
			analyzeFile(file, monitor);
		}
	}

	/**
	 * 「java」一覧を解析する。
	 * @throws SourceCodeUpdateCancelledException
	 */
	private static void analyzeJavaFile(IProgressMonitor monitor) throws SourceCodeUpdateCancelledException{
		for (int i=0; i<javaFileList.size(); i++){
			if (monitor.isCanceled()) {
				throw new SourceCodeUpdateCancelledException();
			}
			String filePath = (String)javaFileList.get(i);
			extraInfoFromJavaSource(
					null,
					filePath,
					SettingUtility.getContents(filePath));
		}
	}
	/**
	 * 「zip」と「jar」一覧を解析する。
	 * @throws SouceCodeUpdateCancelledException
	 */
	private static void analyzeZipJarFile(List<String> list, IProgressMonitor monitor)
		throws SourceCodeUpdateCancelledException{
		for (int i=0; i<list.size(); i++){
			String jarPath = (String)list.get(i);
			List<FileInfo> fileList = SettingUtility.getAllContentsFromJarOrZip(jarPath);
			for (int j=0; j<fileList.size(); j++){
				if (monitor.isCanceled()) {
					throw new SourceCodeUpdateCancelledException();
				}
				FileInfo fileInfo = (FileInfo)fileList.get(j);
				String filePath = fileInfo.getPath();
				if (fileInfo.getType()==FileInfo.SOURCE){
					Object javaFile = fileInfo.getContents();
					if (javaFile != null){
						extraInfoFromJavaSource(
								jarPath,
								filePath,
								(String)javaFile
								);
					}
				}else if (fileInfo.getType()==FileInfo.CLASS){
					Object javaClass = fileInfo.getContents();
					if (javaClass != null){
						extraInfoFromJavaClass(
								jarPath,
								filePath,
								(JavaClass)javaClass);
					}
				}
				fileInfo.clear();
				fileInfo=null;
			}
			fileList.clear();
		}
	}
	/**
	 * 「class」一覧を解析する。
	 * @throws SourceCodeUpdateCancelledException
	 */
	private static void analyzeClassFile(IProgressMonitor monitor) throws SourceCodeUpdateCancelledException{
		for (int i=0; i<classFileList.size(); i++){
			if (monitor.isCanceled()) {
				throw new SourceCodeUpdateCancelledException();
			}
			String classFilePath = (String)classFileList.get(i);
			JavaClass javaClass = SettingUtility.getJavaClassFromClassFile(classFilePath);
			if (javaClass != null){
				extraInfoFromJavaClass(
						null,
						classFilePath,
						javaClass);
			}
		}
	}

	/**
	 *
	 * @param filePath
	 * @throws SourceCodeUpdateCancelledException
	 */
	private static void analyzeFile(File rootFile, IProgressMonitor monitor) throws SourceCodeUpdateCancelledException{
		if (!rootFile.exists()){
			return;
		}
		if (monitor.isCanceled()) {
			throw new SourceCodeUpdateCancelledException();
		}
		if (rootFile.isDirectory()){
			File[] subPaths= rootFile.listFiles();
			for (int i=0; i<subPaths.length; i++){
				analyzeFile(subPaths[i], monitor);
			}
		}else if (rootFile.isFile()){
			String extension = SettingUtility.getExtensinName(rootFile.getPath());
			if (FILE_EXTENSION[0].equals(extension)){
				javaFileList.add(rootFile.getPath());
			}else if (FILE_EXTENSION[1].equals(extension)){
				jarFileList.add(rootFile.getPath());
			}else if (FILE_EXTENSION[2].equals(extension)){
//				jarFileList.add(rootFile.getPath());
				zipFileList.add(rootFile.getPath());
			}else if (FILE_EXTENSION[3].equals(extension)){
				classFileList.add(rootFile.getPath());
			}

		}
	}

	/**
	 * analyze java source
	 * @param fullPath
	 */
	private static void extraInfoFromJavaSource(
			String jarPath,
			String fullpath,
			String javaSourefile){

		char[] source = javaSourefile.toCharArray();
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource(source);
		CompilationUnit compileUnit = (CompilationUnit) parser.createAST(null);
		JavaSrcVisitor visitor = new JavaSrcVisitor();
		compileUnit.accept(visitor);
		visitor.resolveType();
		List<ClassTypeInfo> classList = visitor.getClassList();

		for (int i=0; i<classList.size(); i++){

			ClassTypeInfo classTypeInfo = (ClassTypeInfo)classList.get(i);
			classTypeInfo.setJarPathForJavaFile(jarPath);
			classTypeInfo.setJavaFileFullPath(fullpath);

			String className = classTypeInfo.getFullClassName();
			ClassTypeInfo old = definedClassMap.get(className);
			if (old == null){
				definedClassMap.put(className, classTypeInfo);
			}else{
				old.setJavaFileFullPath(classTypeInfo.getJavaFileFullPath());
				old.setJarPathForJavaFile(classTypeInfo.getJarPathForJavaFile());
			}
		}

		String packageName = visitor.getPackageName();
		if (!packageList.contains(packageName)){
			packageList.add(packageName);
		}
		visitor.clear();
//		visitorList.add(visitor);
	}
	/**
	 * クラスファイルより、クラスの情報を登録する
	 * @param clazz
	 * @return
	 */
	private static void extraInfoFromJavaClass(
			String jarPath,
			String fullpath,
			JavaClass clazz){

		ClassTypeInfo cu=null;

		//クラスの完全修飾名
		String className = clazz.getClassName();

		cu = (ClassTypeInfo)definedClassMap.get(className);
		if (cu != null){
			if (cu.getClassFileFullPath()==null){
				cu.setJarPathForClassFile(jarPath);
				cu.setClassFileFullPath(fullpath);
			}
		}else{
			cu = JavaClassVisitor.extraInfo(clazz);
			cu.setJarPathForClassFile(jarPath);
			cu.setClassFileFullPath(fullpath);
			cu.setFullClassName(className);
			definedClassMap.put(className, cu);
			String packageName = cu.getPackageName();
			if (!packageList.contains(packageName)){
				packageList.add(packageName);
			}
		}
	}

	/**
	 * create definedClassMap using visitorList.
	 */
//	private static void createDefinedClassMap(){
//		for (int j=0; j<visitorList.size(); j++){
//			JavaSrcVisitor visitor = (JavaSrcVisitor)visitorList.get(j);
//			List<ClassTypeInfo> classList = visitor.getClassList();
//			for (int i=0; i<classList.size(); i++){
//				ClassTypeInfo classTypeInfo = classList.get(i);
//				String className = classTypeInfo.getFullClassName();
//				ClassTypeInfo old = definedClassMap.get(className);
//				if (old == null){
//					definedClassMap.put(className, classTypeInfo);
//				}else{
//					old.setJavaFileFullPath(classTypeInfo.getJavaFileFullPath());
//					old.setJarPathForJavaFile(classTypeInfo.getJarPathForJavaFile());
//				}
//			}
//		}
//	}

	/**
	 * call resolveType method of all of visitor to modify types.
	 */
//	private static void resolveType(){
//		for (int j=0; j<visitorList.size(); j++){
//			JavaSrcVisitor visitor = (JavaSrcVisitor)visitorList.get(j);
//			visitor.resolveType();
//			String packageName = visitor.getPackageName();
//			if (!packageList.contains(packageName)){
//				packageList.add(packageName);
//			}
//		}
//	}

	/**
	 * extract interface to interfaceMap.
	 * extract class to classMap
	 * @throws SourceCodeUpdateCancelledException
	 * @since 2009/9/21 create by tu
	 * @date 2009/9/23 modify by tu for performace.
	 */
	private static void classRelationAnalyze(IProgressMonitor monitor) throws SourceCodeUpdateCancelledException{
		Iterator<String> iterator;
		String superClassName;
		ClassTypeInfo superClass;
		String interfaceName;
		ClassTypeInfo interfaceObject;

		iterator = definedClassMap.keySet().iterator();
		while(iterator.hasNext()){
			if (monitor.isCanceled()) {
				throw new SourceCodeUpdateCancelledException();
			}
			String name = iterator.next();
			ClassTypeInfo cu = definedClassMap.get(name);

			superClassName = cu.getSuperClassName();
			superClass = definedClassMap.get(superClassName);
			if (superClass != null){
					superClass.addimplClass(cu);
			}
			List<String> list = cu.getInterfaceTypeList();
			if (list != null){
				for (int i=0; i<list.size(); i++){
					interfaceName = list.get(i);
					interfaceObject = definedClassMap.get(interfaceName);
					if (interfaceObject != null){
						interfaceObject.addimplClass(cu);
					}
				}
			}

			// 障害487
			Map<String, List<String>> interfaceTypeMap = cu.getPossibleInterfaceType();
			if (interfaceTypeMap != null) {
				for(String key : interfaceTypeMap.keySet()) {
					List<String> possibleTypeList = interfaceTypeMap.get(key);
					if (possibleTypeList != null) {
						for (int i = 0; i < possibleTypeList.size(); i++) {
							interfaceObject = definedClassMap.get(possibleTypeList.get(i));
							if (interfaceObject != null){
								interfaceObject.addimplClass(cu);
								break;
							}
						}
					}
				}
			}
			if (cu.isInterfaceType()){
				interfaceMap.put(name, cu);
			}else{
				classMap.put(name, cu);
			}
		}

		definedClassMap.clear();
	}


	/**
	 * インターフェースのメソッドを実装したメソッドを抽出する。
	 * @throws SourceCodeUpdateCancelledException
	 */
	private static void methodRelationAnalyze(IProgressMonitor monitor) throws SourceCodeUpdateCancelledException{
		Iterator<String> iterator = interfaceMap.keySet().iterator();
		while(iterator.hasNext()){
			if (monitor.isCanceled()) {
				throw new SourceCodeUpdateCancelledException();
			}
			String key = (String)iterator.next();
			ClassTypeInfo cu = (ClassTypeInfo)interfaceMap.get(key);
			cu.extraImplClassOfMethods();
		}

	}

	/**
	 * 例外クラスを抽出
	 * @throws SourceCodeUpdateCancelledException
	 */
	private static void extraExceptionClass(IProgressMonitor monitor) throws SourceCodeUpdateCancelledException{
		//1.例外クラスを識別する。
		Iterator<String> iterator = classMap.keySet().iterator();
		while(iterator.hasNext()){
			if (monitor.isCanceled()) {
				throw new SourceCodeUpdateCancelledException();
			}
			String key = iterator.next();
			ClassTypeInfo classInfo = classMap.get(key);
			classInfo.setExceptionType();
		}
		//2.例外クラスを抽出する。
		iterator = classMap.keySet().iterator();
		while(iterator.hasNext()){
			if (monitor.isCanceled()) {
				throw new SourceCodeUpdateCancelledException();
			}
			String key = (String)iterator.next();
			ClassTypeInfo classInfo = classMap.get(key);
			if (classInfo.isExceptionType()){
				exceptionList.add(classInfo);
			}
		}
	}
}
