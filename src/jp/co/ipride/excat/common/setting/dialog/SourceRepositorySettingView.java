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
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.setting.SourceRepositorySetting;
import jp.co.ipride.excat.configeditor.ExcatText;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;

/**
 * SVN編集
 * @author tu-ipride
 * @version 2.0
 * @date 2009/10/18 update by tu
 */
public class SourceRepositorySettingView extends Composite implements
		SelectionListener, SettingViewListener {

	private static final int UI_TEXT_WIDTH = 280;

	public static final int BUTTON_WIDTH = 80;

	private ExcatText passwordText;

	private ExcatText sourceRepositoryUrlText;

	private ExcatText accountText;

	private ExcatText workingCopyFolderPathText;

	private Button workingCopyFolderPathSelectionButton;

	/**
	 * コンストラクタ
	 *
	 * @param parent
	 * @param style
	 */
	public SourceRepositorySettingView(Composite parent, int style) {
		super(parent, style);
		createContents();
		addListeners();
		synchronizeWithSetting(true);
	}

	/**
	 * ダイアログのコンテンツを生成します。
	 */
	protected void createContents() {
		// set dialog area.
		GridLayout layout = new GridLayout(3, false);
		layout.marginLeft=ViewerUtil.getMarginWidth(this);
		layout.marginRight=ViewerUtil.getMarginWidth(this);
		layout.marginTop=ViewerUtil.getMarginHeight(this);
		layout.marginBottom=ViewerUtil.getMarginHeight(this);
		this.setLayout(layout);

		// ソースリポジトリURL
		Label repositoryUrlLabel = new Label(this, SWT.NONE);
		repositoryUrlLabel
				.setText(ApplicationResource
						.getResource("SourceRepositorySettingView.RepositoryUrlLabel.Text"));

		sourceRepositoryUrlText = new ExcatText(this, SWT.SINGLE | SWT.BORDER);
		GridData data = new GridData(GridData.BEGINNING);
		data.horizontalSpan = 2;
		data.widthHint = UI_TEXT_WIDTH;
		sourceRepositoryUrlText.setLayoutData(data);

		Label accountLabel = new Label(this, SWT.NONE);
		accountLabel.setText(ApplicationResource
				.getResource("SourceRepositorySettingView.AccountLabel.Text"));

		accountText = new ExcatText(this, SWT.SINGLE | SWT.BORDER);
		data = new GridData(GridData.BEGINNING);
		data.horizontalSpan = 2;
		data.widthHint = UI_TEXT_WIDTH;
		accountText.setLayoutData(data);

		// パスワード
		Label passwordLabel = new Label(this, SWT.NONE);
		passwordLabel.setText(ApplicationResource
				.getResource("SourceRepositorySettingView.PasswordLabel.Text"));

		passwordText = new ExcatText(this, SWT.SINGLE | SWT.BORDER | SWT.PASSWORD);
		data = new GridData(GridData.BEGINNING);
		data.horizontalSpan = 2;
		data.widthHint = UI_TEXT_WIDTH;
		passwordText.setLayoutData(data);

		// ローカル保存先フォルダ
		Label workingCopyFolderLabel = new Label(this, SWT.NONE);
		workingCopyFolderLabel
				.setText(ApplicationResource
						.getResource("SourceRepositorySettingView.WorkingCopyFolderLabel.Text"));

		workingCopyFolderPathText = new ExcatText(this, SWT.SINGLE | SWT.BORDER);
		data = new GridData(GridData.BEGINNING);
		data.widthHint = UI_TEXT_WIDTH;
		workingCopyFolderPathText.setLayoutData(data);

		workingCopyFolderPathSelectionButton = new Button(this, SWT.PUSH);
		workingCopyFolderPathSelectionButton
				.setText(ApplicationResource
						.getResource("SourceRepositorySettingView.WorkingCopyFolderSelectionButton.Text"));
		data = new GridData();
		data.widthHint = BUTTON_WIDTH;
		workingCopyFolderPathSelectionButton.setLayoutData(data);

		workingCopyFolderPathSelectionButton.addSelectionListener(this);

		//クリアボタン
		Button clearButton = new Button(this, SWT.PUSH);
		clearButton.setText(ApplicationResource
				.getResource("SourceRepositorySettingView.ClearButton.Text"));
		data = new GridData();
		data.widthHint = BUTTON_WIDTH;
		clearButton.setLayoutData(data);

		clearButton.addSelectionListener(new SelectionListener() {
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}

			public void widgetSelected(SelectionEvent arg0) {
				accountText.setText("");
				passwordText.setText("");
				sourceRepositoryUrlText.setText("");
				workingCopyFolderPathText.setText("");
			}
		});
	}

	private void addListeners() {
		accountText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				SettingManager.getSetting()
				.getSourceRepositorySetting().setAccount(accountText.getText());
			}
		});
		passwordText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				SettingManager.getSetting()
				.getSourceRepositorySetting().setPassword(passwordText.getText());
			}
		});
		sourceRepositoryUrlText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				SettingManager.getSetting()
				.getSourceRepositorySetting().setSourceRepositoryUrl(sourceRepositoryUrlText.getText());
			}
		});
		workingCopyFolderPathText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				SettingManager.getSetting()
				.getSourceRepositorySetting().setWorkingCopyFolderPath(workingCopyFolderPathText
						.getText());
			}
		});
	}

	/**
	 * @see SettingViewListener#preCancelProcessed()
	 */
	public boolean preCancelProcessed() {
		synchronizeWithSetting(false);
		return true;
	}

	/**
	 * @see SettingViewListener#preOkProcessed()
	 */
	public boolean preOkProcessed() {
		//何も設定されない場合、何もしない。
		if ("".equals(accountText.getText().trim())
				&& "".equals(passwordText.getText().trim())
				&& "".equals(sourceRepositoryUrlText.getText().trim())
				&& "".equals(workingCopyFolderPathText.getText().trim())) {
			synchronizeWithSetting(false);
			return true;
		}

		// 設定を保存する
		synchronizeWithSetting(false);

		return true;
	}

	/**
	 * 設定と同期する。
	 *
	 * @param fromSetting
	 *            true: 設定から読込む、false: 設定に書き込む
	 */
	private void synchronizeWithSetting(boolean fromSetting) {
		SourceRepositorySetting setting = SettingManager.getSetting()
				.getSourceRepositorySetting();
		if (fromSetting) {
			if (setting.getAccount() != null) {
				accountText.setText(setting.getAccount());
			}
			if (setting.getPassword() != null) {
				passwordText.setText(setting.getPassword());
			}
			if (setting.getSourceRepositoryUrl() != null) {
				sourceRepositoryUrlText.setText(setting
						.getSourceRepositoryUrl());
			}
			if (setting.getWorkingCopyFolderPath() != null) {
				workingCopyFolderPathText.setText(setting
						.getWorkingCopyFolderPath());
			}
		} else {
			setting.setAccount(accountText.getText());
			setting.setPassword(passwordText.getText());
			setting.setSourceRepositoryUrl(sourceRepositoryUrlText.getText());
			setting.setWorkingCopyFolderPath(workingCopyFolderPathText
					.getText());
		}
	}

	/**
	 * @see SettingViewListener#postCancelProcessed()
	 */
	public void postCancelProcessed() {
	}

	/**
	 * @see SettingViewListener#postOkProcessed()
	 */
	public void postOkProcessed() {

	}

	/**
	 * @see SelectionListener#widgetDefaultSelected(SelectionEvent)
	 */
	public void widgetDefaultSelected(SelectionEvent event) {
	}

	/**
	 * @see SelectionListener#widgetSelected(SelectionEvent)
	 */
	public void widgetSelected(SelectionEvent event) {
		DirectoryDialog dialog = new DirectoryDialog(this.getShell());
		String path = dialog.open();
		if (path != null) {
			workingCopyFolderPathText.setText(path);
		}
	}

	public void updatingMsgNotified(String msg) {
		System.out.println(msg);
	}

	public void refresh() {
		synchronizeWithSetting(true);
	}
}