/*
 * Error Analyzer Tool for Java
 *
 * Created on 2007/10/9
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.action.finder;

import jp.co.ipride.excat.analyzer.action.finder.FindCondition;
import jp.co.ipride.excat.analyzer.action.finder.IFindProvider;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.Utility;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * 検索ダイアログ
 *
 * 本ダイアログはモードレスなダイアログとして表示されます。
 *
 * @author tatebayashi
 */
public class FindDialog extends Dialog {

	private IFindProvider findProvider;
	private final static int MAX_HISTORY = 10;

	private Combo findStringCombo;
	private Button dirNextButton;
	private Button dirPrevButton;
	private Button optCaseSensitive;
	private Button optCircularSearch;
	private Button optWordSearch;
	private Button findButton;
	private Button closeButton;

	private String[] _findStringHistory = {};
	private FindCondition _condition = new FindCondition("", true, false,
			false, false, false);

	/**
	 * コンストラクタ
	 *
	 * @param arg0
	 */
	public FindDialog(Shell arg0) {
		super(arg0);
		// モードレスダイアログとして指定
		this.setShellStyle(SWT.DIALOG_TRIM | SWT.MODELESS);
	}

	/**
	 * ダイアログの内容を生成します。
	 */
	protected Control createContents(Composite parent) {
		Control contents = super.createContents(parent);

		// ダイアログのタイトルバーを設定
		String text = ApplicationResource.getResource("FindDialog.ShellText");
		this.getShell().setText(text);

		// ダイアログの領域のレイアウトマネージャを指定。
		Composite area = (Composite) this.getDialogArea();
		GridLayout layout = new GridLayout(1, false);
		area.setLayout(layout);

		GridData bodyData = new GridData(GridData.FILL_VERTICAL);
		area.setLayoutData(bodyData);

		// 文字列検索部を指定
		createSearchArea(area);

		// 検索方向
		createDirectionArea(area);

		// 検索オプション
		createOptionArea(area);

		return contents;
	}

	/**
	 * 検索文字列領域を生成します。
	 *
	 * @param parent
	 */
	private void createSearchArea(Composite parent) {
		Composite searchArea = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(2, false);
		searchArea.setLayout(layout);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		searchArea.setLayoutData(data);

		Label label = new Label(searchArea, SWT.NONE);
		label.setText(ApplicationResource
				.getResource("FindDialog.FindLabelText"));
		findStringCombo = new Combo(searchArea, SWT.DROP_DOWN);
		GridData searchComboData = new GridData();
		searchComboData.widthHint = 260;
		findStringCombo.setLayoutData(searchComboData);
		findStringCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				Combo source = (Combo) arg0.getSource();
				if (source.getText() == null || "".equals(source.getText())) {
					findButton.setEnabled(false);
				} else {
					findButton.setEnabled(true);
				}
			}
		});
		findStringCombo.setItems(_findStringHistory);
		findStringCombo.setText(_condition.getTargetString());
	}

	/**
	 * 検索方向領域を生成します。
	 *
	 * @param parent
	 */
	private void createDirectionArea(Composite parent) {
		GridLayout layout = new GridLayout(1, false);
		Group group = new Group(parent, SWT.NONE);
		group.setText(ApplicationResource
				.getResource("FindDialog.DirectionGroup.Text"));
		GridData data = new GridData(GridData.FILL_VERTICAL);
		group.setLayoutData(data);
		group.setLayout(layout);

		dirNextButton = new Button(group, SWT.RADIO);
		dirNextButton.setText(ApplicationResource
				.getResource("FindDialog.DirectionNext.Text"));
		dirNextButton.setSelection(_condition.isForwardSearch());

		dirPrevButton = new Button(group, SWT.RADIO);
		dirPrevButton.setText(ApplicationResource
				.getResource("FindDialog.DirectionPrev.Text"));
		dirPrevButton.setSelection(!_condition.isForwardSearch());
	}

	/**
	 * オプション設定領域を生成します。
	 *
	 * @param parent
	 */
	private void createOptionArea(Composite parent) {
		GridLayout layout = new GridLayout(2, false);
		Group group = new Group(parent, SWT.NONE);
		group.setText(ApplicationResource
				.getResource("FindDialog.OptionGroup.Text"));
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		group.setLayoutData(data);
		group.setLayout(layout);

		optCaseSensitive = new Button(group, SWT.CHECK);
		optCaseSensitive.setText(ApplicationResource
				.getResource("FindDialog.Option.CaseSensitive.Text"));
		optCaseSensitive.setSelection(_condition.isCaseSensitive());

		optCircularSearch = new Button(group, SWT.CHECK);
		optCircularSearch.setText(ApplicationResource
				.getResource("FindDialog.Option.CircularSearch.Text"));
		optCircularSearch.setSelection(_condition.isCircularSearch());

		optWordSearch = new Button(group, SWT.CHECK);
		optWordSearch.setText(ApplicationResource
				.getResource("FindDialog.Option.WordSearch.Text"));
		optWordSearch.setSelection(_condition.isWordSearch());
	}

	/**
	 * ボタンバー領域を生成します。
	 */
	protected Control createButtonBar(Composite arg0) {
		Composite buttonsArea = new Composite(arg0, SWT.NONE);
		GridLayout layout = new GridLayout(4, false);
		buttonsArea.setLayout(layout);

		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonsArea.setLayoutData(data);

		findButton = Utility.createButton(buttonsArea, SWT.NULL,
				ApplicationResource.getResource("FindDialog.Find.Text"), 100,1);
		findButton.addSelectionListener(new FindButtonSelectinListener());
		closeButton = Utility.createButton(buttonsArea, SWT.NULL,
				ApplicationResource.getResource("FindDialog.Close.Text"), 100,1);
		closeButton
				.addSelectionListener(new CloseButtonSelectionListener(this));
		getShell().setDefaultButton(findButton);
		return buttonsArea;
	}

	/**
	 * 入力された検索条件を取得します。
	 */
	public FindCondition getFindCondition() {
		// 表示中の場合一度フィールドへ保存
		if (this.getShell() != null) {
			saveFindData();
		}
		return _condition;
	}

	/**
	 * 指定された検索条件をUIに設定します。
	 *
	 * @param condition
	 */
	public void setFindCondition(FindCondition condition) {
		findStringCombo.setText(condition.getTargetString());
		dirNextButton.setSelection(condition.isForwardSearch());
		optCaseSensitive.setSelection(condition.isCaseSensitive());
		optCircularSearch.setSelection(condition.isCircularSearch());
		optWordSearch.setSelection(condition.isWordSearch());
	}

	/**
	 * ダイアログ表示中に検索文字列を設定します。
	 *
	 * @param targetStr
	 */
	public void setFindString(String targetStr) {
		if (this.getShell() != null) {
			// 表示中は直接設定する。
			findStringCombo.setText(targetStr);

		}
		_condition.setTargetString(targetStr);
	}

	/**
	 * 検索設定内容を保持します。
	 *
	 * 次回表示時に復元されるように、内容を<code>_initializeCondition</code>に保持します。
	 */
	private void saveFindData() {
		String findStr = findStringCombo.getText();
		_condition.setTargetString(findStr);
		if (!("".equals(findStr))) {
			// 履歴に同一の文字列が存在する場合は削除
			for (int i = 0; i < findStringCombo.getItemCount(); i++) {
				if (findStringCombo.getItem(i).equals(findStr)) {
					findStringCombo.remove(i);
					break;
				}
			}

			// 登録されている履歴が上限を超えている場合は末尾を削除する
			if (findStringCombo.getItemCount() >= MAX_HISTORY) {
				findStringCombo.remove(MAX_HISTORY - 1);
			}
			// 履歴の先頭に追加
			findStringCombo.add(findStr, 0);
			// 履歴から選択した文字列であった場合、リストから削除されると空となってしまうため、
			// テキストに差し戻す。
			findStringCombo.setText(findStr);
		}

		// 次回表示時に設定が反映されるよう初期値保持にフィールドを更新する
		_findStringHistory = findStringCombo.getItems();
		_condition.setForwardSearch(dirNextButton.getSelection());
		_condition.setCaseSensitive(optCaseSensitive.getSelection());
		_condition.setCircularSearch(optCircularSearch.getSelection());
		_condition.setWordSearch(optWordSearch.getSelection());
	}

	/**
	 * 検索処理の提供元オブジェクトを取得します。
	 *
	 * @param provider
	 */
	public void setFindProvider(IFindProvider provider) {
		findProvider = provider;
	}

	/**
	 * 検索処理の提供元オブジェクトを設定します。
	 *
	 * @param provider
	 */
	public IFindProvider getFindProvider() {
		return findProvider;
	}

	/**
	 * 検索ボタンの利用可否を設定します。
	 *
	 * @param enable
	 */
	public void setFindButtonEnable(boolean enable) {
		if (this.getShell() != null) {
			findButton.setEnabled(enable);
		}
	}

	/**
	 * 検索ボタン選択リスナー
	 *
	 * @author tatebayashi
	 *
	 */
	private class FindButtonSelectinListener extends SelectionAdapter {
		public void widgetSelected(SelectionEvent arg0) {
			saveFindData();
			// 検索処理を行います。
			if (findProvider != null) {
				boolean found = findProvider.find(getFindCondition());
				if (!found) {
//					ExcatMessageUtilty.showMessage(this.getShell(),Message.get("FindString.Info.NotFound"));
				}
			}
		}
	}

	/**
	 * 閉じるボタン選択リスナー
	 *
	 * @author tatebayashi
	 *
	 */
	private class CloseButtonSelectionListener extends SelectionAdapter {
		private Dialog dialog;

		public CloseButtonSelectionListener(Dialog d) {
			dialog = d;
		}

		public void widgetSelected(SelectionEvent arg0) {
			// ダイアログを閉じます。
			saveFindData();
			dialog.close();
		}
	}

	/**
	 * ダイアログが閉じられる際のイベント処理
	 *
	 * @author tatebayashi
	 *
	 */
	protected void handleShellCloseEvent() {
		saveFindData();
		super.handleShellCloseEvent();
	}

}
