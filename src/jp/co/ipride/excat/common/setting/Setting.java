/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common.setting;

import java.io.Serializable;
import java.util.ArrayList;

import jp.co.ipride.excat.common.setting.dialog.RootPathItem;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.apache.bcel.classfile.JavaClass;

/**
 * セッティング情報 該当オブジェクトはシステム終了時に保存する。
 *
 * @author 屠偉新
 * @since 2006/9/17
 */
public class Setting implements Serializable {

	private static final long serialVersionUID = 2L;

    public static final String[] FILE_PRIORITY = {
    	"Source",
    	"Class",
    	"RepositorySource"};
    public static final String DEFAULT_ENCODING = "MS932";
    public static final String[] EXTENTIONS_DEFAULT = { ".dat", ".zip"};
    public static final String EXTENTION_TEXT_SEPARATE_STR = "|";

	//画面上に指定したパス
	private String encoding = DEFAULT_ENCODING;

	//this is set by setting dialog.
	private String currentFilePath=null;

	//this is set by setting dialog's Folder Button.
	private String currentFolderPath=null;

	private ArrayList<String> priorityInfo = new ArrayList<String>();

	private String[] filterExtentions = EXTENTIONS_DEFAULT;

	//all of source path and class path, which is set by setting dialog.
	private ArrayList<String> pathList = new ArrayList<String>();
	//root path of dump data which is set by setting dialog.
	private ArrayList<RootPathItem> rootPathList = new ArrayList<RootPathItem>();
	//svn
	private SourceRepositorySetting sourceRepositorySetting = new SourceRepositorySetting();

	private String currentPDFPath = null;

	//this is set by dump file view when this file was selected.
	private String currentDumpFilePath = null;

	private String currentImportFilePath=null;

	private String currentExportFilePath=null;

	public Setting(){
		for (int i=0; i<FILE_PRIORITY.length; i++){
			priorityInfo.add(FILE_PRIORITY[i]);
		}
	}

	public String getCurrentFilePath() {
		return currentFilePath;
	}
	public void setCurrentFilePath(String currentPath){
		this.currentFilePath=currentPath;
	}

	public String getCurrentFolderPath(){
		return currentFolderPath;
	}

	public void setCurrentFolderPath(String path){
		this.currentFolderPath = path;
	}

	/**
	 * call by setting dialog
	 * @param paths
	 */
	public void addFilePaths(String[] paths){
		for (int i=0; i<paths.length; i++){
			if (!pathList.contains(paths[i])){
				pathList.add(paths[i]);
			}
		}
	}
	/**
	 * call by setting dialog.
	 * @param path
	 */
	public void addFolderPath(String path){
		if (!pathList.contains(path)){
			pathList.add(path);
		}
	}

	public ArrayList<String> getPathList(){
		return pathList;
	}

	/**
	 * this method is used for building the repository of excat.
	 * @return
	 */
	public ArrayList<String> getAllPathList(){
		ArrayList<String> allPath = new ArrayList<String>();
		String repPath = sourceRepositorySetting.getWorkingCopyFolderPath();
		if (priorityInfo.get(0).equals(FILE_PRIORITY[2])){
			if (repPath != null){
				allPath.add(repPath);
			}
			allPath.addAll(pathList);
		}else{
			allPath.addAll(pathList);
			if (repPath != null){
				allPath.add(repPath);
			}
		}
		return allPath;
	}

	public void removeFiles(int[] indices) {
		int size = indices.length;
		for (int i = size-1; i >=0; i--) {
			pathList.remove(indices[i]);
		}
	}

	public void removeAllFiles(){
		pathList.clear();
	}

	public ArrayList<String> getPriorityInfo() {
		return priorityInfo;
	}
	public void setPriorityInfo(ArrayList<String> priorityInfo) {
		this.priorityInfo = priorityInfo;
	}

	public void setEncoding(String encoding) {
		this.encoding = encoding;
	}

	public String getEncoding() {
		return encoding;
	}

	public void setPriority(ArrayList<String> priorities) {
		this.priorityInfo = priorities;
	}

	public String[] getFilterExtentions() {
		return this.filterExtentions;
	}

	public void setFilterExtentions(String[] extentions) {
		filterExtentions = extentions;
	}

	public ArrayList<RootPathItem> getRootPathList() {
		return rootPathList;
	}

	public void addRootPath(RootPathItem path) {
		this.rootPathList.add(path);
	}

	public void removeRootPath(RootPathItem path) {
		this.rootPathList.remove(path);
	}

	public SourceRepositorySetting getSourceRepositorySetting() {
		return sourceRepositorySetting;
	}

	public void setSourceRepositorySetting(
			SourceRepositorySetting sourceRepositorySetting) {
		this.sourceRepositorySetting = sourceRepositorySetting;
	}

	public String getCurrentPdfPath() {
		return currentPDFPath;
	}
	public void setCurrentPDFPath(String currentPDFPath) {
		this.currentPDFPath = currentPDFPath;
	}

	/**
	 * call when open xml file.
	 * call when display dump property.
	 */
	public String getCurrentDumpFilePath() {
		return currentDumpFilePath;
	}

	/**
	 * call when open xml file.
	 * @param path
	 */
	public void setCurrentDumpFilePath(String path) {
		this.currentDumpFilePath = path;
	}

	/**
	 * this method is used for building excat repository and source viewer.
	 * @param name
	 * @return
	 */
	public JavaClass getByteCodeContents(String name){
		return SettingManager.getRepository().getByteCodeContents(name);
	}

	public String getClassFileFullPath(String className){
		return SettingManager.getRepository().getClassFileFullPath(className);
	}

	/**
	 * this method is used for building excat repository and source viewer.
	 * @param name
	 * @return
	 */
	public String getJavaSourceContent(String name){
		return SettingManager.getRepository().getJavaSourceContent(name);
	}

	public String getJavaFileFullPath(String className){
		return SettingManager.getRepository().getJavaFileFullPath(className);
	}

	public String getJavaFileName(String className){
		return SettingManager.getRepository().getJavaFileName(className);
	}

	public String getRepositorySourceContent(String className) {
		String folderPath = sourceRepositorySetting.getWorkingCopyFolderPath();
		String currentPath = SettingUtility.getFullPath(folderPath, className);
		return SettingUtility.getContents(currentPath);
	}

	public String getCurrentImportFilePath() {
		return currentImportFilePath;
	}

	/**
	 * エクスポートファイルのパスを取得する。
	 *
	 * @param なし
	 * @return
	 */
	public String getCurrentExportFilePath() {
		return currentExportFilePath;
	}
	/**
	 * エクスポートファイルのパスを設定する。
	 *
	 * @param currentExportFilePath
	 */
	public void setCurrentExportFilePath(String currentExportFilePath) {
		this.currentExportFilePath = currentExportFilePath;
	}

	public String getFirstPriority(){
		return priorityInfo.get(0);
	}

	public boolean equals(Object obj) {
		if (!(obj instanceof Setting)) {
			return false;
		}

		Setting other = (Setting) obj;
		return HelperFunc.compareList(pathList, other.pathList)
				&& HelperFunc.compareObject(currentFilePath,other.currentFilePath)
				&& HelperFunc.compareObject(currentFolderPath,other.currentFolderPath)
				&& HelperFunc.compareObject(encoding,other.encoding)
				&& HelperFunc.compareList(rootPathList, other.rootPathList)
				&& HelperFunc.compareStringArray(filterExtentions, other.filterExtentions)
				&& HelperFunc.compareList(priorityInfo,
						other.priorityInfo)
				&& HelperFunc.compareObject(sourceRepositorySetting,
						other.sourceRepositorySetting);
	}

}