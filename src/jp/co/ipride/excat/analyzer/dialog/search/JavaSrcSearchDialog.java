package jp.co.ipride.excat.analyzer.dialog.search;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.viewer.searchviewer.ConditionUnit;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.ExcatTextViewer;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * リポジトリ検索ダイアログ
 * @author saisk
 * @version 3.0
 * @date 2009/10/30
 */
public class JavaSrcSearchDialog extends Dialog {

	private Button runBtn;
	private Button cancelBtn;

	private ConditionUnit conditionUnit = null;
	private ExcatTextViewer searchText;

	private static final int UI_TEXT_WIDTH = 500;

	/**
	 *
	 * @param parentShell
	 */
	public JavaSrcSearchDialog(MainViewer appWindow) {
		super(appWindow.getShell());
	}

	/**
	 * ダイアログのボタン領域内のボタンを生成します。
	 * override method.
	 */
	protected void createButtonsForButtonBar(Composite parent) {
		GridLayout layout = new GridLayout(4, false);
        parent.setLayout(layout);

		// キャンセル＆確定ボタンの作成
        runBtn = Utility.createButton(parent, SWT.PUSH,
				ApplicationResource.getResource("Search.Dialog.Run"),
				Utility.BUTTON_WIDTH,1);

        cancelBtn = Utility.createButton(parent, SWT.PUSH,
				ApplicationResource.getResource("Search.Dialog.Cancel"),
				Utility.BUTTON_WIDTH,1);

        runBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				updateToMode();
				okPressed();
			}
		});
        runBtn.setEnabled(false);

        cancelBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				cancelPressed();
			}
		});
	}

	/**
	 * ダイアログのコンテンツを生成します。
	 * override method.
	 *
	 */
	protected Control createContents(Composite parent) {

		Control contents = super.createContents(parent);

		String text = ApplicationResource.getResource("Search.Dialog.Title.Repository");
		this.getShell().setText(text);

		//set dialog area.
		Composite area = (Composite)this.getDialogArea();
		GridLayout layoutDialog = new GridLayout(1, false);
		area.setLayout(layoutDialog);

		// Body Composite
        GridData bodyData = new GridData(GridData.FILL_VERTICAL);
        area.setLayoutData(bodyData);

        GridLayout layout = new GridLayout(1, false);
		layout.marginTop = ViewerUtil.getMarginHeight(area);
		layout.marginBottom = ViewerUtil.getMarginHeight(area);
		layout.marginLeft = ViewerUtil.getMarginWidth(area);
		layout.marginRight = ViewerUtil.getMarginWidth(area);
		area.setLayout(layout);

		// set dialog area.
        Group group = new Group(area,SWT.NONE);
		layout = new GridLayout(1, false);
		layout.marginTop = ViewerUtil.getMarginHeight(area);
		layout.marginBottom = ViewerUtil.getMarginHeight(area);
		layout.marginLeft = ViewerUtil.getMarginWidth(area);
		layout.marginRight = ViewerUtil.getMarginWidth(area);
		group.setLayout(layout);

		Label label1 = new Label(group, SWT.NONE);
		label1.setText(ApplicationResource.getResource("Search.Dialog.Text"));

		searchText = new ExcatTextViewer(group,UI_TEXT_WIDTH,1);
		searchText.addTextListener(new ITextListener() {
		      public void textChanged(TextEvent e) {
		    	  checkRunStatus();
		      }
		});

		Composite composite = new Composite(area, SWT.NONE);
		layoutDialog = new GridLayout(2,false);
		layoutDialog.marginTop = ViewerUtil.getMarginHeight(area);
		layoutDialog.marginBottom = ViewerUtil.getMarginHeight(area);
		layoutDialog.marginLeft = ViewerUtil.getMarginWidth(area);
		layoutDialog.marginRight = ViewerUtil.getMarginWidth(area);
		composite.setLayout(layoutDialog);

		//add comment.
		createComment(composite);

		updateUI();

		searchText.getTextViewer().getTextWidget().selectAll();
		searchText.getTextViewer().getTextWidget().setFocus();

		return contents;
	}

	/**
	 * 各タブから呼ばれる、実行のボタンを制御する。
	 */
	public void checkRunStatus(){
		if (searchText.getText().length() != 0){
			runBtn.setEnabled(true);
		}else{
			runBtn.setEnabled(false);
		}
	}

	public void updateToMode(){
		conditionUnit = new ConditionUnit();
		conditionUnit.setText(searchText.getText().trim());
		conditionUnit.setSearchType(ConditionUnit.SEARCH_REPOSITORY);
	}

	public ConditionUnit getTextSearchUnit(){
		return conditionUnit;
	}

	private void createComment(Composite parent){
		Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(parent);
		layout.marginBottom = ViewerUtil.getMarginHeight(parent);
		layout.marginLeft = ViewerUtil.getMarginWidth(parent);
		layout.marginRight = ViewerUtil.getMarginWidth(parent);
		composite.setLayout(layout);
		GridData gd = new GridData();
		gd.widthHint=400;
		composite.setLayoutData(gd);

		Label label1 = new Label(composite, SWT.NONE);
		label1.setText(
				ApplicationResource.getResource("SearchView.Tab.Comment1"));
	}

	private void updateUI() {
		if (conditionUnit != null) {
			searchText.setText(conditionUnit.getText());
		}
	}

	public void setHistoryContidition(ConditionUnit conditionHistory) {
		conditionUnit = conditionHistory;
	}

	public ConditionUnit getHistoryCondition() {
		return conditionUnit;
	}
}
