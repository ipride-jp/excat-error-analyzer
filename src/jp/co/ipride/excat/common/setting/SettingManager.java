/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common.setting;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.dialog.SourceUpdateDialog;
import jp.co.ipride.excat.common.setting.repository.Repository;
import jp.co.ipride.excat.common.sourceupdate.SourceCodeUpdater;
import jp.co.ipride.excat.common.sourceupdate.SvnSourceCodeUpdater;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.apache.log4j.Logger;
import org.eclipse.swt.widgets.Shell;

/**
 * セッティングを管理する。
 *
 * @author 屠偉新
 * @since 2006/9/17
 */
public class SettingManager {
	// Settingオブジェクトを保存する場所
	private static final String SETTING_FILE_PATH = System
			.getProperty("user.dir")
			+ File.separator + "Setting";

	// セッティング情報を持つオブジェクト
	private static Setting setting = null;
	private static Setting oldSetting = null;

	/**
	 * ツール起動時、セッティング情報をロードする。
	 */
	public static void load() {
		setting = createSettingFromFile(SETTING_FILE_PATH);
		oldSetting = createSettingFromFile(SETTING_FILE_PATH);
	}

	/**
	 * セッティング情報をファイルに保存する。 タイミングとしては、変更された場合SourceManagerに呼ばれる。
	 * 終了時にviewerに呼ばれる。
	 */
	public static void save() {
		saveSettingToFile(SETTING_FILE_PATH);
		oldSetting = createSettingFromFile(SETTING_FILE_PATH);
	}

	public static void outputSetting(String filePath){
		saveSettingToFile(filePath);
	}

	public static void inputSetting(String filePath){
		setting = createSettingFromFile(filePath);
		// 障害489
		//oldSetting = createSettingFromFile(filePath);
		//saveSettingToFile(SETTING_FILE_PATH);
	}

	private static Setting createSettingFromFile(String filePath) {
		Setting setting = null;
		ObjectInputStream in = null;

		try {
			in = new ObjectInputStream(new FileInputStream(filePath));
			setting = (Setting) in.readObject();
		} catch (Exception e) {
			Logger.getLogger("viewerLogger").debug(e);
			setting = new Setting();
		} finally {
			if (in != null) {
				try {
					in.close();
				} catch (IOException e) {
				}
			}
		}

		return setting;
	}

	private static void saveSettingToFile(String filePath) {
		if (setting == null) {
			return;
		}

		ObjectOutputStream out = null;
		try {
			File file = new File(filePath);
			if (!file.exists()) {
				file.createNewFile();
			}

			out = new ObjectOutputStream(new FileOutputStream(file));
			out.writeObject(setting);
		} catch (Exception e) {
			HelperFunc.getLogger().error("SettingManager", e);
			ExcatMessageUtilty.showErrorMessage(null,e);
		} finally {
			if (out != null) {
				try {
					out.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public static Setting getSetting() {
		return setting;
	}

	public static Repository getRepository(){
		return Repository.getInstance();
	}

	public static void creatRepository(Shell shell, String title){
		SourceCodeUpdater sourceCodeUpdater = new SvnSourceCodeUpdater();
		SourceUpdateDialog dialog = new SourceUpdateDialog(
				shell, title, sourceCodeUpdater);
		dialog.openDialog();
		return;
	}

	/**
	 * セッティング情報を同期する。
	 *
	 * @param flag
	 *            Trueの場合、ファイルを更新。Falseの場合、メモリを更新
	 * @date tu add for v3
	 */
	public static void update(Shell shell,boolean flag) {
		if (flag) {
			save();
		} else {
			load();
		}
		creatRepository(
				shell,
				ApplicationResource.getResource("SourceUpdateProgressDialog.Update.Text")
				);  //v3
	}

	/**
	 * セッティング情報は変更有無か
	 *
	 * @return
	 */
	public static boolean isChanged() {
		return !HelperFunc.compareObject(setting, oldSetting);
	}

	/**
	 * ソースパス、クラスパスの設定が変更されているかどうか
	 * @return true for changed.
	 */
	public static boolean isPathChanged(){
		ArrayList<String> sourcePathList = setting.getPathList();
		ArrayList<String> oldSourcePathList = oldSetting.getPathList();
		SourceRepositorySetting repo = setting.getSourceRepositorySetting();
		SourceRepositorySetting oldRepo = oldSetting.getSourceRepositorySetting();
		ArrayList<String>  priority = setting.getPriorityInfo();
		ArrayList<String>  oldPriority = oldSetting.getPriorityInfo();
		if (!HelperFunc.compareList(sourcePathList,oldSourcePathList)
				|| HelperFunc.compareList(priority,oldPriority)
				|| HelperFunc.compareObject(repo, oldRepo)){
			return true;
		}else{
			return false;
		}

//		return !HelperFunc.compareList(sourcePathList,oldSourcePathList);
	}


	/**
	 * 変更をキャンセルするメソッド
	 */
	public static void cancelChanges() {
		setting = oldSetting;
		//add by Qiu Song 20091113 for バグ#499
		oldSetting = createSettingFromFile(SETTING_FILE_PATH);
		//end of add by Qiu Song 20091113 for バグ#499
	}
}