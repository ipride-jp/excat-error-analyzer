/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common.utility;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MethodInfo;


import org.apache.log4j.Logger;

/**
 * ユーティリティクラス
 * @author 屠偉新
 * @since 2006/9/17
 */
public class HelperFunc {

	private static HashMap<String,String> primTypes = new HashMap<String,String>();
	public static String DUMP_FILE_EXT_NAME = "dat";

	static{
		primTypes.put("Z", "boolean");
		primTypes.put("C", "char");
		primTypes.put("I", "int");
		primTypes.put("B", "byte");
		primTypes.put("S", "short");
		primTypes.put("J", "long");
		primTypes.put("F", "float");
		primTypes.put("D", "double");
	}

	public static Logger getLogger(){
		return Logger.getLogger("viewerLogger");
	}


	/**
	 * java.langパッケージにあるクラスかどうかをチェックする。
	 * @param fullClassName
	 * @return
	 */
	public static boolean isJavaLangClass(String fullClassName){

		try {
			Class.forName(fullClassName);
			return true;
		} catch (ClassNotFoundException e) {
		  return false;
		}
	}

	public static boolean isPrimitive(String type){
		if("boolean".equals(type) ||
				"char".equals(type)||
				"int".equals(type)||
				"byte".equals(type)||
				"short".equals(type)||
				"long".equals(type)||
				"float".equals(type)||
				"double".equals(type)){
			return true;
		}
		return false;
	}

	public static void logException(Throwable e) {

		StringBuffer sb = new StringBuffer();

		// スタック・ダンプ情報
		StringWriter writer = new StringWriter();
		PrintWriter pWriter = new PrintWriter(writer);
		e.printStackTrace(pWriter);
		sb.append(writer.toString());

		Logger.getLogger("viewerLogger").error(sb);
	}

	/**
	 * read the java source from a java file.
	 * @param filePath　file path
	 * @return String型内容
	 */
	public static String getFileContents(String filePath) {
		StringBuffer buf = new StringBuffer();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(filePath));
			char[] cbuf = new char[1024];
			int len = 0;
			while ((len = reader.read(cbuf)) != -1) {
				buf.append(cbuf, 0, len);
			}

			reader.close();
		} catch (Exception e) {
			ExcatMessageUtilty.showErrorMessage(null,e);
			return null;
		}

		return buf.toString();
	}

	/**
	 * convert a bytecode method Sig into a java method Sig.
	 * @param methodSig：BCELより取得したメソッドSignature
	 * @return
	 */
	public static String convertMethodSig(String methodSig) {

		//find the position of the char '('
		int parenBegin = methodSig.indexOf('(');

		//find the position of the char ')'
		int parenEnd = methodSig.indexOf(')');

		StringBuffer buf = new StringBuffer();
		buf.append('(');
		String classSigs = methodSig.substring(parenBegin + 1, parenEnd);

		if (classSigs.length()>0){
			//String[] params = classSigs.split(";");
			String[] params =  spliteClassSigs(classSigs);
			for (int i=0; i<params.length; i++){

				buf.append(convertClassSig(params[i]));
				buf.append(',');
			}
			buf.deleteCharAt(buf.length() - 1);
		}


		buf.append(')');

		String retType = methodSig.substring(parenEnd + 1);
		//retType = retType.replaceAll(";","");
		if (retType.length()==1) {
			if ("V".equals(retType)) {
				buf.append("void");
			}else{
				buf.append(getPrimitiveType(retType));
			}
		} else {
			buf.append(convertClassSig(retType));
		}
		return buf.toString();
	}

	/**
	 * メソッドのSignatureから引数のリストを取得する
	 * @param methodSig：BCELより取得したメソッドSignature
	 * @return
	 */
	public static List<String> getParamsListFromSignature(String methodSig) {

		List<String> paramsList = new ArrayList<String>();
		//find the position of the char '('
		int parenBegin = methodSig.indexOf('(');

		//find the position of the char ')'
		int parenEnd = methodSig.indexOf(')');

		String classSigs = methodSig.substring(parenBegin + 1, parenEnd);

		if (classSigs.length()>0){
			//String[] params = classSigs.split(";");
			String[] params =  spliteClassSigs(classSigs);
			for (int i=0; i<params.length; i++){
			    String typeParam = convertClassSig(params[i]);
			    paramsList.add(typeParam);

			}
		}
		return paramsList;
	}

	/**
	 * splite classSigs to array.
	 * @param classSigs:BCELから取得した引数のSignature文字列
	 * @return
	 */
	private static String[] spliteClassSigs(String classSigs) {

		ArrayList<String> arr = new ArrayList<String>();
		int index1 = -1;
		int index2 = -1;
		char ch;
		for (int i=0; i<classSigs.length();)
		{
			String sig = null;
			ch = classSigs.charAt(i);
			if (ch == '[')
			{
				index1 = i;
				while (ch == '[')
				{
					index1 += 1;
					ch = classSigs.charAt(index1);
				}
				if (ch == 'L')
				{
					index2 = classSigs.indexOf(';', index1);
					//include ;
					sig = classSigs.substring(i, index2 + 1);
					arr.add(sig);
					i = index2 + 1;
				}
				else
				{
					sig = classSigs.substring(i, index1+1);
					arr.add(sig);
					i = index1 + 1;
				}
			}
			else if (ch == 'L')
			{
				index1 = classSigs.indexOf(';', i);
				sig = classSigs.substring(i, index1 + 1);
				arr.add(sig);
				i = index1 + 1;
			}
			else
			{
				sig = classSigs.substring(i,i+1);
				i++;
				arr.add(sig);
			}
		}

		String[] params = new String[arr.size()];
		for (int i = 0; i < arr.size(); i++)
		{
			params[i] = (String)arr.get(i);
		}

		return params;
	}

	/**
	 * convert classSig of bytecode to java classSig
	 * @param classSig
	 * @return
	 */
	public static String convertClassSig(String classSig) {
		if (classSig == null || "".equals(classSig))
			return classSig;
		if (classSig.charAt(0) == '['){
			return getArrayType(classSig);
		}
		if ("L".equals(classSig.substring(0,1))){
			return getObjectType(classSig);
		}else{
			return getPrimitiveType(classSig);
		}

	}

	/**
	 * get java name of method from bytecode method name.
	 * @param methodName
	 * @return
	 */
	public static String filterInnerClassName(String methodName){
		int pos = methodName.indexOf("$");
		if (pos <0){
			return methodName;
		}else{
			return methodName.substring(0, pos);
		}
	}

	/**
	 * クラスの完全修飾名からクラス名を取得
	 * @param fullClassName
	 * @return
	 */
	public static String getPureClassName(String fullClassName){

		if(fullClassName == null){
			return null;
		}

		int index = -1;
		//inner class
		index = fullClassName.lastIndexOf('$');
		if(index < 0){
			index = fullClassName.lastIndexOf('.');
		}

		if(index < 0){
			return fullClassName;
		}else{
			return fullClassName.substring(index + 1);
		}
	}

	/**
	 * convert primitive type of bytecode to java primitive type.
	 * @param classSig
	 * @return
	 */
	private static String getPrimitiveType(String primitiveSig) {
		int length=primitiveSig.length();
		if (length==1){
			return (String)primTypes.get(primitiveSig);
		}else{
			StringBuffer buf = new StringBuffer();
			buf.append((String)primTypes.get(primitiveSig.substring(0,1)));
			for (int i=1; i<length; i++){
				buf.append(",");
				buf.append((String)primTypes.get(primitiveSig.substring(i,i+1)));
			}
			return buf.toString();
		}
	}

	/**
	 * convert array of bytecode to []
	 * @param classSig
	 * @return
	 */
	private static String getArrayType(String classSig) {
		return convertClassSig(classSig.substring(1)) + "[]";
	}

	/**
	 * convert a class or primitive type of bytecode to a java type.
	 * @param classSig:例 Ljava/lang/Object;
	 * @return
	 */
	private static String getObjectType(String classSig) {
		String word = classSig.replace('/', '.');

		//process head word
		StringBuffer buf = new StringBuffer();
		int indexL = word.indexOf("L");
		if (indexL>0){
			for (int i=0; i<indexL; i++){
				String primitive = getPrimitiveType(word.substring(i,1));
				buf.append(primitive);
				buf.append(',');
			}
		}
		//remove last ;
		buf.append(word.substring(indexL+1,word.length() - 1));
		return buf.toString();

	}

	/**
	 * 数字から該当数字が文字セットUTF-16で表す文字列に変換する。
	 * @param  数字
	 * @return 対応する文字列
	 */
	public static String getStringFromNumber(String number) {
		String ret = null;

		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try{
			for(int i = 0; i < number.length(); i += 2){
				int b = Integer.parseInt(number.substring(i, i + 2), 16);
				baos.write(b);
			}
		}catch(Exception e){
			return number;//need to be changed:jiang
		}

		byte[] result = baos.toByteArray();
		try {
			ret = new String(result, "UTF-16");
		} catch (UnsupportedEncodingException e) {
			HelperFunc.logException(e);
			//e.printStackTrace();
		}

		return ret;
	}

	public static boolean compareObject(Object obj1, Object obj2) {
		if (obj1 == null && obj2 == null) {
			return true;
		}

		return obj1 != null && obj1.equals(obj2);
	}

	public static boolean compareStringArray(String[] array1, String[] array2) {
		if (array1 == null && array2 == null) {
			return true;
		}

		if (array1 != null && array2 != null) {
			return HelperFunc.compareObject(Arrays.asList(array1), Arrays
					.asList(array2));
		}

		return false;
	}

	public static boolean compareList(List list1, List list2) {
		if (list1 == null && list2 == null) {
			return true;
		}

		if (list1 == null && list2.size() == 0) {
			return true;
		}

		if (list2 == null && list1.size() == 0) {
			return true;
		}

		return HelperFunc.compareObject(list1, list2);
	}

	public static String ucFirst(String string) {
		if (string == null) {
			return null;
		}

		if ("".equals(string)) {
			return string;
		}

		return string.substring(0, 1).toUpperCase() + string.substring(1);
	}

	/**
	 * フルクラス名を取得する。
	 * @param packageName
	 * @param className
	 * @return packageName.className
	 */
	public static String getFullClassName(String packageName,String className){
		StringBuffer buf = new StringBuffer();

		if(packageName != null){
			buf.append(packageName);
			buf.append('.');
		}
		buf.append(className);

		return buf.toString();
	}

	/**
	 * ファイル名のみを取得
	 * @param filePath
	 * @return
	 */
	public static String tripSourcePath(String filePath){

		if(filePath == null){
			return null;
		}

		int pos = filePath.lastIndexOf(File.separatorChar);
		if(pos > 0){
			return filePath.substring(pos + 1);
		}else{
			return filePath;
		}
	}

	public static String getFileExtName(String fileName){
		String extName=null;
		int index = fileName.lastIndexOf(".");
		if (index>=0){
			extName = fileName.substring(index+1);
		}
		return extName;
	}

	public static String getFileNameWithoutPath(String fileFullName){
		String fillName=null;
		int index = fileFullName.lastIndexOf("\\");
		if (index>=0){
			fillName = fileFullName.substring(index+1);
		}
		return fillName;
	}

	public static boolean isDumpFile(File file){
		 String ext = HelperFunc.getFileExtName(file.getName());
		 if (DUMP_FILE_EXT_NAME.equals(ext)){
			 return true;
		 }else{
			 return false;
		 }
	}

	public static List<String> getPathList(List<String> folderList){
		List<String> pathList = new ArrayList<String>();
		for (String folder: folderList){
			File dir = new File(folder);
			File[] fileList = dir.listFiles();
			for (File file : fileList){
				if (file.isDirectory()){
					getPathList(file,pathList);
				}else{
					if (!isDumpFile(file)){
						continue;
					}
					String path = file.getPath();
					if (pathList.contains(path)){
						continue;
					}
					pathList.add(path);
				}

			}
		}
		return pathList;
	}

	public static void getPathList(File folder, List<String> pathList){
		File[] fileArray = folder.listFiles();
		for (File file : fileArray){
			if (file.isDirectory()){
				getPathList(file, pathList);
			}else{
				if (!isDumpFile(file)){
					continue;
				}
				String path = file.getPath();
				if (pathList.contains(path)){
					continue;
				}
				pathList.add(path);
			}
		}
	}

	/**
	 * メソッドを宣言するクラスが所在するソースファイルのメインクラスの
	 * 完全修飾名を取得する。.javaを含まない
	 * @param methodInfo StackTreeにあるメソッドノードの情報
	 * @return
	 */
	public static String getJavaFileFullName(MethodInfo methodInfo) {
		String classSig = methodInfo.getClassSig();
//		String classWithoutInner = HelperFunc.filterInnerClassName(classSig);
//		String nameForJavaSrc = classWithoutInner;
//		// get source file name
		String sourceFile = methodInfo.getSourceName();
//		if (sourceFile != null && !"".equals(sourceFile)) {
//			// remove .java from source file
//			int pos = sourceFile.lastIndexOf('.');
//			String lastClassName = sourceFile;
//			if (pos > 0) {
//				lastClassName = sourceFile.substring(0, pos);
//			}
//
//			pos = classWithoutInner.lastIndexOf('.');
//			if (pos > 0) {
//				// has package
//				classWithoutInner = classWithoutInner.substring(0, pos);
//				nameForJavaSrc = classWithoutInner + "." + lastClassName;
//			} else {
//				// no package
//				nameForJavaSrc = lastClassName;
//			}
//		}
//		return nameForJavaSrc;
		return getJavaFileFullName(classSig, sourceFile);
	}
	
	public static String getJavaFileFullName(String classSig, String sourceFile) {
		String classWithoutInner = HelperFunc.filterInnerClassName(classSig);
		String nameForJavaSrc = classWithoutInner;
		// get source file name
		if (sourceFile != null && !"".equals(sourceFile)) {
			// remove .java from source file
			int pos = sourceFile.lastIndexOf('.');
			String lastClassName = sourceFile;
			if (pos > 0) {
				lastClassName = sourceFile.substring(0, pos);
			}

			pos = classWithoutInner.lastIndexOf('.');
			if (pos > 0) {
				// has package
				classWithoutInner = classWithoutInner.substring(0, pos);
				nameForJavaSrc = classWithoutInner + "." + lastClassName;
			} else {
				// no package
				nameForJavaSrc = lastClassName;
			}
		}
		return nameForJavaSrc;
	}

	public static String escapeRegex(String tag) {
		String[] s1 = (tag + "1").split("\\*\\*");
		int lastIndex = s1.length - 1;
		s1[lastIndex] = s1[lastIndex].substring(0, s1[lastIndex].length() - 1);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < s1.length; i++) {
			String string = s1[i];
			String[] s2 = (string + "1").split("\\*");
			int lastIndex2 = s2.length - 1;
			s2[lastIndex2] = s2[lastIndex2].substring(0, s2[lastIndex2].length() - 1);
			for (int j = 0; j < s2.length; j++) {
				String string2 = s2[j];
				sb.append(Pattern.quote(string2));
				if (j < s2.length - 1) {
					sb.append(".*");
				}
			}
			if (i < s1.length - 1) {
				sb.append("\\*");
			}
		}
		return sb.toString();
	}

	public static boolean isLetterOrDot(char c) {
		return Character.isLetter(c) || ".".equals(String.valueOf(c));
	}
}