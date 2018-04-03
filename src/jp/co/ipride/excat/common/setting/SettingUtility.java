/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common.setting;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.setting.repository.FileInfo;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.apache.bcel.classfile.ClassFormatException;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

/**
 * セッティングのユーティリティ
 *
 * @author 屠偉新
 * @since 2006/9/17
 */
public class SettingUtility {
	// 一時ファイルの保存フォルダ
	private static final String TEMPORARY_FOLDER_PATH = System
			.getProperty("user.dir")
			+ File.separator + "temp";

	/**
	 * get full path
	 *
	 * @param folder
	 * @param classPath
	 * @return contains
	 */
	public static String getFullPath(String folder, String classSig) {

		String classPath = classSig.replace('.', File.separatorChar);
		String fullPath = folder + File.separator + classPath + ".java";
		return fullPath;
	}

	/**
	 * get contents of full path.
	 *
	 * @param fullPath
	 * @return
	 */
	public static String getContents(String fullPath) {
		File file = new File(fullPath);
		if (file.exists()) {
			FileInputStream infile = null;
			try {
				infile = new FileInputStream(file);

				return getStringFromIs(infile);

			} catch (IOException e) {
				ExcatMessageUtilty.showMessage(
						null,
						Message.get("Encoding.Error.CannotRead"));
				HelperFunc.logException(e);
				return null;
			}finally{
				if(infile != null){
					try{
						infile.close();
						infile=null;
						file=null;
					}catch(IOException ex){
						HelperFunc.logException(ex);
					}

				}
			}
		} else {
			return null;
		}
	}

	/**
	 * 設定されたエンコーディングでInputStreamから文字列を読み込み
	 * @param infile
	 * @return
	 * @throws IOException
	 */
	private static String getStringFromIs(InputStream infile)
	    throws IOException{

		//エンコーディング設定の取得
		String encoding = SettingManager.getSetting().getEncoding();
		if (encoding == null) {
			encoding = Setting.DEFAULT_ENCODING; //デフォルトエンコーディングがMS932
		}

		InputStreamReader in = new InputStreamReader(infile, encoding);
		BufferedReader reader = new BufferedReader(in);
		char[] cbuf = new char[1024];
		int len = 0;
		StringBuffer buf = new StringBuffer();
		while ((len = reader.read(cbuf)) != -1) {
			buf.append(cbuf, 0, len);
		}
		reader.close();
		return buf.toString();
	}

	/**
	 * jarファイルからクラス・ファイルを取得
	 *
	 * @param jarPath
	 * @param classSig
	 * @param isClass
	 * @return
	 */
	public static Object getFileFromJarOrZip(String jarPath,
			String classSig,boolean isClass) {
		if(isClass){
			//class file
			classSig = classSig.replaceAll("\\.", "/") + ".class";
		}else{
			//java file
			classSig = classSig.replaceAll("\\.", "/") + ".java";
		}

		ZipFile zip = null;
		try {
			zip = new ZipFile(jarPath);
			ZipEntry entry = zip.getEntry(classSig);
			if (entry == null)
				return null;
			if(isClass){
				ClassParser parser = new ClassParser(zip.getInputStream(entry),
						classSig);
				return parser.parse();
			}else{
				InputStream is = zip.getInputStream(entry);
				return getStringFromIs(is);
			}

		} catch (ClassFormatException e) {
			HelperFunc.logException(e);
			return null;
		} catch (IOException e) {
			HelperFunc.logException(e);
			return null;
		} catch (NullPointerException e) {
			HelperFunc.logException(e);
			return null;
		} finally {
			if (zip != null)
				try {
					zip.close();
					zip = null;
				} catch (IOException e) {
					HelperFunc.logException(e);
				}
		}
	}
	/**
	 * jarファイルからクラス・ファイルを取得
	 *
	 * @param jarPath
	 * @param fullPath
	 * @date 2009/9/19
	 * @return
	 */
	public static JavaClass getJavaClassFromJarOrZipUsingFullPath(String jarPath,
			String fullPath) {

		ZipFile zip = null;
		try {
			zip = new ZipFile(jarPath);
			ZipEntry entry = zip.getEntry(fullPath);
			if (entry == null){
				return null;
			}
			ClassParser parser = new ClassParser(zip.getInputStream(entry),	fullPath);
			return parser.parse();
		} catch (ClassFormatException e) {
			HelperFunc.logException(e);
			return null;
		} catch (IOException e) {
			HelperFunc.logException(e);
			return null;
		} catch (NullPointerException e) {
			HelperFunc.logException(e);
			return null;
		} finally {
			if (zip != null)
				try {
					zip.close();
					zip = null;
				} catch (IOException e) {
					HelperFunc.logException(e);
				}
		}
	}

	/**
	 * jarファイルからJavaファイルを取得
	 *
	 * @param jarPath
	 * @param fullPath
	 * @return
	 */
	public static String getJavaFileFromJarOrZipUsingFullPath(String jarPath,
			String fullPath) {
		ZipFile zip = null;
		try {
			zip = new ZipFile(jarPath);
			ZipEntry entry = zip.getEntry(fullPath);
			if (entry == null){
				return null;
			}
			InputStream is = zip.getInputStream(entry);
			return getStringFromIs(is);
		} catch (ClassFormatException e) {
			HelperFunc.logException(e);
			return null;
		} catch (IOException e) {
			HelperFunc.logException(e);
			return null;
		} catch (NullPointerException e) {
			HelperFunc.logException(e);
			return null;
		} finally {
			if (zip != null)
				try {
					zip.close();
					zip=null;
				} catch (IOException e) {
					HelperFunc.logException(e);
					zip=null;
				}
		}
	}

	/**
	 * 該当Jar/zipファイルから、java/classのファイルを取得する。
	 * @param jarPath
	 * @return 全てのjava/classのファイル
	 */
	public static List<FileInfo> getAllContentsFromJarOrZip(String jarPath){
		List<FileInfo> result = new ArrayList<FileInfo>();
		ZipFile zip = null;
		FileInfo fileInfo;
		try {
			zip = new ZipFile(jarPath);
			//1.Zip/jarにあるファイル一覧を取得する。
			Enumeration enumZf = zip.entries();
			while (enumZf.hasMoreElements()) {
				ZipEntry entry = zip.getEntry(enumZf.nextElement().toString());
				if (!entry.isDirectory()){
					String path = entry.getName();
					String ext = getExtensinName(path);
					if ("java".equals(ext)){
						fileInfo = new FileInfo(FileInfo.SOURCE);
						fileInfo.setPath(path);
						InputStream is = zip.getInputStream(entry);
						fileInfo.setContents(getStringFromIs(is));
						result.add(fileInfo);
					}else if ("class".equals(ext)){
						fileInfo = new FileInfo(FileInfo.CLASS);
						fileInfo.setPath(path);
						ClassParser parser = new ClassParser(zip.getInputStream(entry),	path);
						fileInfo.setContents(parser.parse());
						result.add(fileInfo);
					}
				}
			}
			return result;

		} catch (ClassFormatException e) {
			HelperFunc.logException(e);
			return null;
		} catch (IOException e) {
			HelperFunc.logException(e);
			return null;
		} catch (NullPointerException e) {
			HelperFunc.logException(e);
			return null;
		} finally {
			if (zip != null){
				try {
					zip.close();
					zip=null;
				} catch (IOException e) {
					HelperFunc.logException(e);
					zip=null;
				}
			}
		}
	}



	public static JavaClass getJavaClassFromClassFile(String fullPath){
		try {
			ClassParser parser;
			parser = new ClassParser(fullPath);
			JavaClass javaClass = parser.parse();
			parser = null;
			return javaClass;
		} catch (IOException e) {
			HelperFunc.logException(e);
			return null;
		}
	}

	/**
	 * classファイルからクラス・ファイルを取得
	 *
	 * @param clazz
	 * @param classSig
	 * @return
	 */
	public static JavaClass getJavaClassFromClazz(String clazz, String classSig) {
		try {
			ClassParser parser = new ClassParser(clazz);
			JavaClass javaClass = parser.parse();
			String name = javaClass.getClassName();
			if (classSig.equals(name)) {
				return javaClass;
			} else {
				return null;
			}
		} catch (IOException e) {
			HelperFunc.logException(e);
			return null;
		} catch (NullPointerException e) {
			HelperFunc.logException(e);
			return null;
		}
	}

	/**
	 * Folderからクラス・ファイルを取得
	 *
	 * @param folder
	 * @param classSig
	 * @return
	 */
	public static JavaClass getJavaClassFromFolder(String folder,
			String classSig) {

		if(classSig == null){
			return null;
		}
		classSig = classSig.replaceAll("\\.", "/") + ".class";
		StringBuffer buf = new StringBuffer();
		buf.append(folder);
		buf.append(File.separator);
		buf.append(classSig);
		File file = new File(buf.toString());
		if(!file.exists() || !file.isFile()){
			return null;
		}

		try {
			ClassParser parser = new ClassParser(buf.toString());
			JavaClass javaClass = parser.parse();

			return javaClass;
		} catch (IOException e) {
			HelperFunc.logException(e);
			return null;
		} catch (NullPointerException e) {
			HelperFunc.logException(e);
			return null;
		}
	}

	private static int serial = 0;

	/**
	 * war,earファイルからクラスを取得する。
	 *
	 * @param zipFile
	 * @param classSig
	 * @return
	 */
	public static Object getFileFromWarEar(String zipFile,
			String classSig,boolean isClass) {
		File tempFolder = new File(TEMPORARY_FOLDER_PATH);
		if (!tempFolder.exists()) {
			tempFolder.mkdirs();
		}

		String tempFile = TEMPORARY_FOLDER_PATH + File.separator + "jartemp"
				+ (++serial) + ".jar";
		ZipFile zip = null;
		try {
			zip = new ZipFile(zipFile);
			Enumeration enumeration = zip.entries();
			while (enumeration.hasMoreElements()) {
				ZipEntry entry = (ZipEntry) enumeration.nextElement();
				if (entry == null) {
					continue;
				}
				if (entry.isDirectory()) {
					continue;
				}
				String name = entry.getName();
				int lastPoint = name.lastIndexOf(".");
				if (lastPoint < 0) {
					continue;
				}
				String ext = name.substring(lastPoint + 1);
				if ("war".equals(ext)) {
					File file = new File(tempFile);
					if (file.exists()) {
						file.delete();
					}
					// 一時テンプレートに保存し、解析
					InputStream in = zip.getInputStream(entry);
					OutputStream out = new BufferedOutputStream(
							new FileOutputStream(tempFile));
					byte[] buffer = new byte[1024];
					int len;
					while ((len = in.read(buffer)) >= 0) {
						out.write(buffer, 0, len);
					}
					in.close();
					out.close();
					Object javaClass = getFileFromWarEar(tempFile,
							classSig,isClass);
					file.delete();
					if (javaClass != null) {
						return javaClass;
					}
				} else if ("jar".equals(ext)) {
					File file = new File(tempFile);
					if (file.exists()) {
						file.delete();
					}
					// 一時テンプレートに保存し、解析
					InputStream in = zip.getInputStream(entry);
					OutputStream out = new BufferedOutputStream(
							new FileOutputStream(tempFile));
					byte[] buffer = new byte[1024];
					int len;
					while ((len = in.read(buffer)) >= 0) {
						out.write(buffer, 0, len);
					}
					in.close();
					out.close();
					Object javaClass = getFileFromJarOrZip(tempFile,
							classSig,isClass);
					file.delete();
					if (javaClass != null) {
						return javaClass;
					}
				} else if ("class".equals(ext) && isClass) {

					InputStream is = zip.getInputStream(entry);
					ClassParser parser = new ClassParser(is, null);
					JavaClass javaClass = parser.parse();
					if (javaClass == null) {
						continue;
					}
					String clazz = javaClass.getClassName();
					if (classSig.equals(clazz)) {
						return javaClass;
					}
				}
			}
			return null;
		} catch (IOException e) {
			HelperFunc.logException(e);
			//e.printStackTrace();
			return null;
		} catch (NullPointerException e) {
			HelperFunc.logException(e);
			//e.printStackTrace();
			return null;
		} finally {
			if (zip != null)
				try {
					zip.close();
				} catch (IOException e) {
					HelperFunc.logException(e);
				}
		}
	}

	public static String getExtensinName(String filePath){
		String[] words = filePath.split("\\.");
		if (words.length>0){
			return words[words.length-1];
		}else{
			return "";
		}
	}

}
