/*
 * Error Analyzer Tool for Java
 *
 * Created on 2007/10/9
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common.setting.dialog;

import java.util.ArrayList;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.setting.Setting;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.ExcatText;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

/**
 * ダンプファイルビューの設定ビュー
 * @author tatebayashi
 * @date 2009/9/16 tu改定 for v3
 */
public class RootPathSettingView extends Composite implements
		SelectionListener, SettingViewListener {

	private static final String[] ROOT_PATH_TABLE_COLUMN_NAMES = new String[] {
			ApplicationResource
					.getResource("RootPathSettingView.RootPathTable.Column.Name"),
			ApplicationResource
					.getResource("RootPathSettingView.RootPathTable.Column.Path") };

	private TableViewer rootPathTable;
	private Button addButton;
	private Button editButton;
	private Button removeButton;

	private ExcatText extentionText;

	/**
	 * コンストラクタ
	 * @param parent
	 * @param style
	 */
	public RootPathSettingView(Composite parent, int style) {
		super(parent, style);

		createContents();

	}

	/**
	 * ダイアログのコンテンツを生成します。
	 */
	protected void createContents() {
		//set dialog area.
		GridLayout layout = new GridLayout(1, false);
		this.setLayout(layout);
		layout.marginLeft=ViewerUtil.getMarginWidth(this);
		layout.marginRight=ViewerUtil.getMarginWidth(this);
		layout.marginTop=ViewerUtil.getMarginHeight(this);
		layout.marginBottom=ViewerUtil.getMarginHeight(this);

		// Body Composite
		GridData bodyData = new GridData(GridData.FILL_VERTICAL);
		this.setLayoutData(bodyData);

		createRootPathList(this);

		initializeData();

		packColumns();

		createExtentionList(this);

	}

	/**
	 * ルートパス一覧領域を作成します。
	 * @param parent
	 */
	protected void createRootPathList(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(ApplicationResource
				.getResource("RootPathSettingView.RootPathTableGroup.Text"));
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(data);
		GridLayout layout = new GridLayout(2, false);
		group.setLayout(layout);

		//left, set file list
		Composite left_c = new Composite(group, SWT.NONE);
		GridData left_data = new GridData(GridData.FILL_HORIZONTAL);
		left_c.setLayoutData(left_data);
		GridLayout left_layout = new GridLayout(1, false);
		left_c.setLayout(left_layout);
		createRootPathListArea(left_c);

		//right, set buttons
		Composite right_c = new Composite(group, SWT.NONE);
		GridData right_data = new GridData(GridData.FILL_HORIZONTAL);
		right_c.setLayoutData(right_data);
		GridLayout right_layout = new GridLayout(1, false);
		right_c.setLayout(right_layout);
		createButtonArea(right_c);
	}

	/**
	 * 拡張子設定領域を作成します。
	 *
	 * @param parent
	 */
	protected void createExtentionList(Composite parent) {
		Group group = new Group(parent, SWT.NONE);
		group.setText(ApplicationResource
				.getResource("RootPathSettingView.ExtentionGroup.Text"));
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(data);
		GridLayout layout = new GridLayout(2, false);
		group.setLayout(layout);

		Label extentionLabel = new Label(group, SWT.NONE);
		extentionLabel.setText(ApplicationResource
				.getResource("RootPathSettingView.ExtentionLabel.Text"));
		extentionText = new ExcatText(group, SWT.SINGLE | SWT.BORDER);
		data = new GridData(GridData.BEGINNING);
		data.widthHint = 200;
		extentionText.setLayoutData(data);

		extentionText.setText(getExtentionsText());
		extentionText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				SettingManager.getSetting().setFilterExtentions(getExtentionsArray(extentionText.getText()));
			}
		});
	}

	protected String getExtentionsText() {
		String[] extentions = SettingManager.getSetting().getFilterExtentions();
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < extentions.length; i++) {
			sb.append(extentions[i]);
			if (i < extentions.length - 1) {
				sb.append(Setting.EXTENTION_TEXT_SEPARATE_STR);
			}
		}
		return sb.toString();
	}

	protected String[] getExtentionsArray(String extentionsText) {
		if ("".equals(extentionsText.trim())) {
			return Setting.EXTENTIONS_DEFAULT;
		}
		String[] extentions = extentionsText.split("\\"
				+ Setting.EXTENTION_TEXT_SEPARATE_STR);
		int len = 0;
		for (int i = 0; i < extentions.length; i++) {
			// 前後のホワイトスペースをトリム
			String newSuffix = extentions[i].trim().toLowerCase();
			if (newSuffix.length() == 0)
				continue;
			int j = 0;
			for (;j < len; j++){
				if (extentions[j].equals(newSuffix)){
					break;
				}
			}
			if (j >= len){
				extentions[len++] = newSuffix;
			}
		}
		if (len == 0){
			return Setting.EXTENTIONS_DEFAULT;
		}
		String[] retExtentions = new String[len];
		for (int i = 0; i < len; i++){
			retExtentions[i] = extentions[i];
		}
		return retExtentions;
	}

	/**
	 * ルートパス設定リスト領域を作成します。
	 * @param parent
	 */
	protected void createRootPathListArea(Composite parent) {
		rootPathTable = new TableViewer(parent, SWT.BORDER | SWT.FULL_SELECTION);
		rootPathTable.setContentProvider(new ArrayContentProvider());
		rootPathTable.setLabelProvider(new RootPathTableLabelProvider());
		rootPathTable
				.addSelectionChangedListener(new ISelectionChangedListener() {
					public void selectionChanged(SelectionChangedEvent event) {
						StructuredSelection selection = (StructuredSelection) event
								.getSelection();
						if (selection.getFirstElement() != null) {
							editButton.setEnabled(true);
							removeButton.setEnabled(true);
						} else {
							editButton.setEnabled(false);
							removeButton.setEnabled(false);
						}

					}
				});

		Table table = rootPathTable.getTable();
		table.setLinesVisible(true);

		//Tableの設定
		GridData gd = new GridData();
		gd.horizontalSpan = 2;
		gd.verticalSpan = 5;
		gd.heightHint = 200;
		gd.widthHint = 320;
		table.setLayoutData(gd);
		table.setHeaderVisible(true);

		TableColumn column = new TableColumn(table, SWT.NONE);
		column.setText(ROOT_PATH_TABLE_COLUMN_NAMES[0]);

		column = new TableColumn(table, SWT.NONE);
		column.setText(ROOT_PATH_TABLE_COLUMN_NAMES[1]);

		rootPathTable.setColumnProperties(ROOT_PATH_TABLE_COLUMN_NAMES);

		// create cell editors
		CellEditor[] editors = new CellEditor[ROOT_PATH_TABLE_COLUMN_NAMES.length];
		editors[0] = new TextCellEditor(table);
		editors[1] = new TextCellEditor(table);
		rootPathTable.setCellEditors(editors);

	}

	/**
	 * ルートパスリスト編集用のボタン領域を生成します。
	 * @param parent
	 */
	protected void createButtonArea(Composite parent) {
//		GridData data_1 = new GridData(GridData.FILL_VERTICAL);
		addButton = Utility.createButton(parent, SWT.PUSH, ApplicationResource
				.getResource("RootPathSettingView.Add.Button"), Utility.BUTTON_WIDTH,1);
		addButton.addSelectionListener(this);
		addButton.setEnabled(true);

//		GridData data_2 = new GridData(GridData.FILL_VERTICAL);
		editButton = Utility.createButton(parent, SWT.PUSH, ApplicationResource
				.getResource("RootPathSettingView.Edit.Button"), Utility.BUTTON_WIDTH,1);
		editButton.addSelectionListener(this);
		editButton.setEnabled(false);

//		GridData data_3 = new GridData(GridData.FILL_VERTICAL);
		removeButton = Utility.createButton(parent, SWT.PUSH, ApplicationResource
				.getResource("RootPathSettingView.Remove.Button"),Utility.BUTTON_WIDTH,1);
		removeButton.addSelectionListener(this);
		removeButton.setEnabled(false);
	}

	/**
	 * ボタン初期状態の設定
	 *
	 */
	public void setButtonInitialState(){

		editButton.setEnabled(false);
		removeButton.setEnabled(false);
	}

	/**
	 * 全カラムの幅を内容に合わせて設定します。
	 */
	private void packColumns() {
		Table table = rootPathTable.getTable();
		int count = table.getColumnCount();
		TableColumn[] columns = table.getColumns();
		for (int index = 0; index < count; index++) {
			columns[index].pack();
		}
	}

	/**
	 * 初期データを設定します。
	 */
	protected void initializeData() {
		rootPathTable.getTable().removeAll();
		ArrayList<RootPathItem> list = SettingManager.getSetting().getRootPathList();
		for (int i = 0; i < list.size(); i++) {
			rootPathTable.add(list.get(i));
		}
	}

	/**
	 * ボタン押下イベント処理
	 */
	public void widgetSelected(SelectionEvent event) {
		if (event.getSource() == addButton) {
			addRootPathItem();
		} else if (event.getSource() == editButton) {
			editRootPathItem();
		} else if (event.getSource() == removeButton) {
			removeRootPathItem();
		}
	}

	/**
	 * 特に処理を行いません。
	 * @param arg0
	 */
	public void widgetDefaultSelected(SelectionEvent arg0) {
	}

	/**
	 * ルートパス追加
	 */
	protected void addRootPathItem() {
		TableItem[] items = rootPathTable.getTable().getItems();
		String[] rootPathNames = new String[items.length];
		for (int i = 0; i < items.length; i++) {
			RootPathItem item = (RootPathItem) items[i].getData();
			rootPathNames[i] = item.getName();
		}

		RootPathEditDialog dialog = new RootPathEditDialog(this.getShell(),
				true, rootPathNames);
		if (dialog.open() == IDialogConstants.OK_ID) {
			RootPathItem item = dialog.getRootPathItem();
			rootPathTable.add(item);
			SettingManager.getSetting().addRootPath(item);
			packColumns();
			rootPathTable.setSelection(new StructuredSelection(item));
		}
		rootPathTable.getControl().setFocus();
	}

	/**
	 * ルートパス編集
	 */
	protected void editRootPathItem() {
		TableItem[] items = rootPathTable.getTable().getItems();
		String[] rootPathNames = new String[items.length];
		for (int i = 0; i < items.length; i++) {
			RootPathItem item = (RootPathItem) items[i].getData();
			rootPathNames[i] = item.getName();
		}
		StructuredSelection selection = (StructuredSelection) rootPathTable
				.getSelection();
		RootPathItem selectedItem = (RootPathItem) selection.getFirstElement();

		RootPathEditDialog dialog = new RootPathEditDialog(this.getShell(),
				false, selectedItem, rootPathNames);
		if (dialog.open() == IDialogConstants.OK_ID) {
			rootPathTable.update(selectedItem, null);
			packColumns();
		}
		rootPathTable.getControl().setFocus();
	}

	/**
	 * ルートパス削除
	 */
	protected void removeRootPathItem() {
		StructuredSelection selection = (StructuredSelection) rootPathTable
				.getSelection();
		RootPathItem selectedItem = (RootPathItem) selection.getFirstElement();

		rootPathTable.remove(selectedItem);
		SettingManager.getSetting().removeRootPath(selectedItem);
	}

	/**
	 * 設定をキャンセルする前の処理の処理。
	 *
	 * ルートパス設定では特に何も行いません。
	 */
	public boolean preCancelProcessed() {
		String extentionsText = extentionText.getText();
		SettingManager.getSetting().setFilterExtentions(getExtentionsArray(extentionsText));
		return true;
	}

	/**
	 * @see SettingViewListener#preOkProcessed()
	 */
	public boolean preOkProcessed() {
		String extentionsText = extentionText.getText();
		SettingManager.getSetting().setFilterExtentions(getExtentionsArray(extentionsText));
		return true;
	}

	/**
	 * 設定キャンセル後の処理を実装します。
	 *
	 * ルートパス設定では特に何も行いません。
	 */
	public void postCancelProcessed() {
	}

	/**
	 * 設定確定後の処理を実装します。
	 *
	 * ルートパス設定ではファイルツリービューの更新処理を行います。
	 */
	public void postOkProcessed() {
	}

	public void refresh() {
		initializeData();
		packColumns();
		extentionText.setText(getExtentionsText());
	}
}
