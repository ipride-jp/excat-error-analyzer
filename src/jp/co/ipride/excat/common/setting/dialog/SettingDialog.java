/*
 * Error Analyzer Tool for Java
 *
 * Created on 2007/10/9
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common.setting.dialog;



import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;

/**
 * 設定ダイアログ
 * @author tatebayashi
 *
 */
public class SettingDialog extends Dialog {

	/** タブフォルダ */
	private CTabFolder tabFolder;
	/** インポート */
	private Button importBtn;
	/** エクスポート */
	private Button exportBtn;

	private Button saveBtn;

	private Button cancelBtn;

	/** 「ソース＆クラスパス」タグのオブジェクト */
	private SourcePathSettingView sourcePathSettingView;
	/** 「ダンプファイルビューア」タグのオブジェクト */
	private RootPathSettingView rootPathSettingView;
	/** 「ソースリポジトリ」タグのオブジェクト */
	private SourceRepositorySettingView sourceRepositorySettingView;

	/** 「ソースフォルダ」のフラグ */
	public static final String FILE_SOURCE = "File.Source";
	/** 「クラスパス」のフラグ */
	public static final String FILE_CLASS = "File.Class";
	/** 「優先順」のフラグ */
	public static final String FILE_PRIORITY = "File.Priority";
	/** 「ソースコードのEnconding設定」のフラグ */
	public static final String SOURCE_ENCODING = "File.Encoding";
	/** 「ルートパス一覧の名前」のフラグ */
	public static final String DUMP_FILEVIEW_NAMEPATH = "Dump.Fileview.name";
	/** 「ルートパス一覧のルートパス」のフラグ */
	public static final String DUMP_FILEVIEW_ROOTPATH = "Dump.Fileview.rootPath";
	/** 「表示する拡張子」のフラグ */
	public static final String DUMP_FILEVIEW_EXTENSION = "Dump.Fileview.Extension";
	/** 「ソースリポジトリURL」のフラグ */
	public static final String SOURCE_REPOSITORY_URL = "Source.Repository.URL";
	/** 「アカウント」のフラグ */
	public static final String SOURCE_REPOSITORY_ACCOUNT = "Source.Repository.Account";
	/** 「パスワード」のフラグ */
	public static final String SOURCE_REPOSITORY_PASSWORD = "Source.Repository.Password";
	/** 「作業フォルダ」のフラグ */
	public static final String SOURCE_REPOSITORY_FOLDER = "Source.Repository.Folder";

	/**
	 * コンストラクタ
	 * @param parentShell 本ダイアログの親となるShell
	 */
	public SettingDialog(Shell parentShell) {
		super(parentShell);
	}

	/**
	 * ダイアログのコンテンツを生成します。
	 * override method.
	 *
	 */
	protected Control createContents(Composite parent) {
		this.getShell().setText(
				ApplicationResource.getResource("SettingDialog.ShellText"));

		//set dialog area.
		Composite area = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		area.setLayout(layout);
		layout.marginLeft=ViewerUtil.getMarginWidth(area);
		layout.marginRight=ViewerUtil.getMarginWidth(area);
		layout.marginTop=ViewerUtil.getMarginHeight(area);
		layout.marginBottom=ViewerUtil.getMarginHeight(area);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        area.setLayoutData(gridData);

        createTabFolder(area);
        createTabItems();
        createButtons(area);
        addListeners();

		return area;
	}

	/**
	 * タブフォルダーを生成します。
	 * @param parent
	 */
	protected void createTabFolder(Composite parent) {
		tabFolder = new CTabFolder(parent, SWT.BORDER );
		tabFolder.setTabHeight(24);
		tabFolder.setSelectionBackground(
		  new Color[]{
				  parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND),
				  parent.getDisplay().getSystemColor(SWT.COLOR_TITLE_BACKGROUND_GRADIENT)},
		  new int[] {90},
		  true
		);
		tabFolder.setSelectionForeground(parent.getDisplay().getSystemColor(SWT.COLOR_WHITE));
	}

	/**
	 * タブアイテムを生成します。
	 *
	 * タブアイテムに設定するコントロールクラスは必ず、ISettingView実装クラスである必要があります。
	 */
	protected void createTabItems() {
		CTabItem item;

		// ソースパス設定ビューのアイテム追加
		item = new CTabItem(tabFolder, SWT.NONE);
		item.setText(
				ApplicationResource.getResource("SourcePathSettingView.Tab.Text"));
		sourcePathSettingView= new SourcePathSettingView(tabFolder, SWT.None);
		item.setControl(sourcePathSettingView);

		// 保管ルートパス設定ビューのアイテム追加
		item = new CTabItem(tabFolder, SWT.NONE);
		item.setText(
				ApplicationResource.getResource("RootPathSettingView.Tab.Text"));
		rootPathSettingView = new RootPathSettingView(tabFolder, SWT.NONE);
		item.setControl(rootPathSettingView);
		// ソースリポジトリ設定ビューのアイテム追加
		item = new CTabItem(tabFolder, SWT.NONE);
		item.setText(
				ApplicationResource.getResource("SourceRepositorySettingView.Tab.Text"));
		sourceRepositorySettingView = new SourceRepositorySettingView(tabFolder, SWT.NONE);
		item.setControl(sourceRepositorySettingView);
	}

	private void createButtons(Composite composite){
		Composite buttonSite = new Composite(composite, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		buttonSite.setLayout(layout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonSite.setLayoutData(gd);

		importBtn = Utility.createButton(buttonSite, SWT.PUSH,
				ApplicationResource.getResource("SettingDialog.ImportButton"),
				Utility.BUTTON_WIDTH,1);
		exportBtn = Utility.createButton(buttonSite, SWT.PUSH,
				ApplicationResource.getResource("SettingDialog.ExportButton"),
				Utility.BUTTON_WIDTH,1);

		saveBtn = Utility.createButton(buttonSite, SWT.PUSH,
				ApplicationResource.getResource("Dialog.Button.Enter.Text"),
				Utility.BUTTON_WIDTH,1);

		cancelBtn = Utility.createButton(buttonSite, SWT.PUSH,
				ApplicationResource.getResource("Dialog.Button.Cancel.Text"),
				Utility.BUTTON_WIDTH,1);
	}

	private void addListeners(){
		importBtn.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog dialog = new FileDialog(getShell(), SWT.OPEN);
				dialog.setFilterExtensions(new String[] { "*.setting" });
				String path = SettingManager.getSetting().getCurrentImportFilePath();
				if (path != null) {
					dialog.setFilterPath(path);
				}
				String inputPath = dialog.open();
				if (inputPath != null) {
					SettingManager.inputSetting(inputPath);
					sourcePathSettingView.refresh();
					rootPathSettingView.refresh();
					sourceRepositorySettingView.refresh();
				}
			}
		});
		exportBtn.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SAVE);
				dialog.setFilterExtensions(new String[] { "*.setting" });
				String path = SettingManager.getSetting().getCurrentImportFilePath();
				if (path != null) {
					dialog.setFilterPath(path);
				}
				String outputPath = dialog.open();
				if (outputPath != null) {
					SettingManager.outputSetting(outputPath);
				}
			}
		});
		cancelBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cancelProcess();
			}
		});
		saveBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				okProcess();
			}
		});
	}


	/**
	 * 確定ボタン選択時の処理
	 *
	 * 設定を反映します。
	 */
	protected void okProcess() {
		// 各ビュー個別の確定前処理を行います。
		for(int i=0;i<tabFolder.getItemCount();i++) {
			SettingViewListener view = (SettingViewListener)tabFolder.getItem(i).getControl();
			if(!view.preOkProcessed()) {
				// 対象のビューを選択
				tabFolder.setSelection(i);
			}
		}

		super.okPressed();
	}

	/**
	 * キャンセルボタン選択時の処理
	 *
	 * 設定を確定するかどうかの問い合わせを行い、設定の確定またはキャンセルをを行います。
	 */
	protected void cancelProcess(){
//		// 各ビュー個別のキャンセル前処理を行います。
//		for(int i=0;i<tabFolder.getItemCount();i++) {
//			SettingViewListener view = (SettingViewListener)tabFolder.getItem(i).getControl();
//			if(!view.preCancelProcessed()) {
//				// falseが返却された場合は、そのタブを選択し、確定処理を中断する。
//				tabFolder.setSelection(i);
//				return;
//			}
//		}

		// キャンセルボタン押下にて設定の保存を確認する
//		if (SettingManager.isChanged()){
//			MessageBox msgBox = new MessageBox(this.getShell(),SWT.YES|SWT.NO);
//			msgBox.setMessage(ApplicationResource.getResource("SettingDialog.ConfirmToSaveSetting"));
//			msgBox.setText(ApplicationResource.getResource("SettingDialog.ConfirmMessage"));
//			int result = msgBox.open();
//			if (result == SWT.YES){
//				SettingManager.update(true);
//			}else{
//				SettingManager.update(false);
//			}
//		}
		SettingManager.cancelChanges();
		super.cancelPressed();
	}

	/**
	 * ダイアログを閉じる際の処理
	 *
	 * 設定を確定するかどうかの問い合わせを行い、設定の確定またはキャンセルをを行います。
	 */
	public boolean close(){
//		if (SettingManager.isChanged()){
//			boolean result = ExcatMessageUtilty.showConfirmDialogBox(
//					this.getShell(),
//					Message.get("SettingDialog.ConfirmToSaveSetting"));
//
//			if (result){
//				SettingManager.update(this.getShell(),true);
//			}else{
//				SettingManager.update(this.getShell(),false);
//			}
//		}
		//add by Qiu Song 20091113 for バグ#499
		if(getReturnCode() == IDialogConstants.CANCEL_ID){
			SettingManager.cancelChanges();
		}
		//end of add by Qiu Song 20091113 for バグ#499
		return super.close();
	}
}