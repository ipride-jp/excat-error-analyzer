package jp.co.ipride.excat.configeditor.viewer.task.dialog;

import java.util.Vector;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.ExcatTextViewer;
import jp.co.ipride.excat.configeditor.model.task.FilterException;
import jp.co.ipride.excat.configeditor.model.task.MonitoringException;
import jp.co.ipride.excat.configeditor.model.task.Place;
import jp.co.ipride.excat.configeditor.model.task.TaskList;
import jp.co.ipride.excat.configeditor.util.ProposalUtility;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.configeditor.viewer.contentassist.ContentAssistProcessor;
import jp.co.ipride.excat.configeditor.viewer.contentassist.WordTracker;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * 画面名：例外若しくはエラーを監視する
 * @author tu
 * @since 2007/11/12
 *
 */
public class ExceptionRegisterDlg extends Dialog{

	private static int text_width    = 500;
	private static final int MAX_QUEUE_SIZE = 200;

	private ExcatTextViewer targetClassName;			//監視ターゲット


	private Table monitorPathTable;    			//監視経路テーブル
	private Button monitorPathCheck;

	private Table exclusionClassTable;   		//除外テーブル
	private Button exclusionClassCheck;

	private Button addExclusionClssButton;  	//除外対象追加
	private Button editExclusionClssButton;
	private Button deleteExclusionClssButton;

	private Button addMonitorPathButton;  		//監視経路追加
	private Button editMonitorPathButton;
	private Button deleteMonitorPathButton;

	private Button saveBtn;						//保存ボタン
	private Button cancelBtn;					//保存しないボタン
	private ExceptionRegisterDlg thisDialog;

	private Color red;
	private Color background;
	private Color white;

	private MonitoringException monitoringException = null;

	private ContentAssistant assistant;
	private WordTracker wordTracker = new WordTracker(MAX_QUEUE_SIZE);

	private String exceptionName = "";

	private Vector<MonitoringException> monitoringExceptionList = null;

	/**
	 * コンストラクタ
	 * @param parentShell
	 */
	public ExceptionRegisterDlg(Shell shell) {
		super(shell);
		thisDialog = this;
	}

	public void init(MonitoringException monitoringException, Vector<MonitoringException> monitoringExceptionList){
		this.monitoringException = monitoringException;
		this.monitoringExceptionList = monitoringExceptionList;

	}

	/**
	 * override method
	 * 1.create layout
	 * 2.add listeners
	 */
	protected Control createContents(Composite parent) {
		this.getShell().setText(
				ApplicationResource.getResource("Dialog.RegisterException.Title.Text"));
		red = this.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		white = this.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		background = this.getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

		Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		layout.numColumns = 1;
        composite.setLayout(layout);

        GridData compositeGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        composite.setLayoutData(compositeGridData);

        //監視クラス名
        addMonitorException(composite);

        //空白スペース
        addBlankSpace(composite);

        //除外クラス [監視ターゲットから除外するサブクラス名を登録する]
        addExcludeTableArea(composite);

        //空白スペース
        addBlankSpace(composite);

        //監視経路
        addMonitorArea(composite);

        //空白スペース
        addBlankSpace(composite);

        //保存＆保存しないボタン
        Composite buttonForm = new Composite(composite,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonFormlayout.numColumns=2;
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonForm.setLayoutData(gd);

		saveBtn = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Dialog.Button.Enter.Text"),
				Utility.BUTTON_WIDTH,1);
		cancelBtn = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Dialog.Button.Cancel.Text"),
				Utility.BUTTON_WIDTH,1);

		createContentAssistant();  		//for v3

		addListeners();

		updateDataToDisplay();

		cancelBtn.setFocus();

		return composite;
	}

	private void addBlankSpace(Composite composite){
		int spaceHeight = ViewerUtil.getMarginHeight(composite);
		Composite spaceComposite = new Composite(composite, SWT.NONE);
		GridData spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spaceGridData.heightHint = spaceHeight;
        spaceComposite.setLayoutData(spaceGridData);
	}

	private void addMonitorException(Composite composite){
        Group group = new Group(composite,SWT.NONE);
        group.setText(
        		ApplicationResource.getResource("Dialog.RegisterException.MonitorClass.Text"));
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL ));

		targetClassName = new ExcatTextViewer(group, text_width, 1);
	}


	private void addExcludeTableArea(Composite composite){
        Group group = new Group(composite,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		group.setLayout(layout);
		GridData groupGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL );
		group.setLayoutData(groupGridData);

		exclusionClassCheck = new Button(group, SWT.CHECK);
		exclusionClassCheck.setText(
        		ApplicationResource.getResource("Dialog.RegisterException.Exclude.Text"));

		exclusionClassTable = new Table(group, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData exclusionClssGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        exclusionClssGridData.verticalSpan = 4;
        int listHeight = exclusionClassTable.getItemHeight() * 6;
        Rectangle trim = exclusionClassTable.computeTrim(0, 0, 0, listHeight);

        exclusionClssGridData.heightHint = trim.height-20;
        exclusionClassTable.setLayoutData(exclusionClssGridData);
        addExclusionClssButtons(group);
	}

	private void addMonitorArea(Composite composite){
        Group monitorPathGroup= new Group(composite,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		monitorPathGroup.setLayout(layout);
		GridData groupGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL );
		monitorPathGroup.setLayoutData(groupGridData);

		monitorPathCheck = new Button(monitorPathGroup, SWT.CHECK);
		monitorPathCheck.setText(
        		ApplicationResource.getResource("Dialog.RegisterException.MonitorPath.Text"));

		monitorPathTable = new Table(monitorPathGroup,
				SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);

        GridData monitorPathListGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        monitorPathListGridData.verticalSpan = 4;
        int listHeight = monitorPathTable.getItemHeight() * 6;
        Rectangle trim = monitorPathTable.computeTrim(0, 0, 0, listHeight);
        monitorPathListGridData.heightHint = trim.height-20;
        monitorPathTable.setLayoutData(monitorPathListGridData);
        addMonitorPathButtons(monitorPathGroup);
	}

	private void addExclusionClssButtons(Composite composite){
		Composite buttonForm = new Composite(composite,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonFormlayout.numColumns=3;
		buttonFormlayout.marginHeight=1;
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.widthHint=text_width;
		buttonForm.setLayoutData(gd);

		addExclusionClssButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.add"),
				Utility.BUTTON_WIDTH,1);


		editExclusionClssButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.edit"),
				Utility.BUTTON_WIDTH,1);

		deleteExclusionClssButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.delete"),
				Utility.BUTTON_WIDTH,1);
	}

	private void addMonitorPathButtons(Composite composite){
		Composite buttonForm = new Composite(composite,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonFormlayout.numColumns=3;
		buttonFormlayout.marginHeight=1;
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.widthHint=text_width;
		buttonForm.setLayoutData(gd);

		addMonitorPathButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.add"),
				Utility.BUTTON_WIDTH,1);


		editMonitorPathButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.edit"),
				Utility.BUTTON_WIDTH,1);

		deleteMonitorPathButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.delete"),
				Utility.BUTTON_WIDTH,1);
	}

	private void addListeners(){
		targetClassName.addKeyListener(new KeyAdapter() {
		      public void keyPressed(KeyEvent e) {
		        switch (e.keyCode) {
		        case SWT.F1:
		          assistant.showPossibleCompletions();
		          break;
		        default:
		        //ignore everything else
		        }
		      }
		    });
		targetClassName.addTextListener(new ITextListener() {
		      public void textChanged(TextEvent e) {
		    	  String word = e.getText();
		    	  if (ProposalUtility.isWhitespaceString(word)) {
		    		  wordTracker.add(
		    				  ProposalUtility.findMostRecentWord(
		    						  targetClassName, e.getOffset() - 1));
		    	  }
		    	  targetClassName.setBackground(white);
		    	  monitoringException.setTargetClassName(targetClassName.getText());
		      }
		    });
		targetClassName.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent arg0) {
				//targetClassName.setBackground(white);
			}

			public void focusLost(FocusEvent arg0) {
				checkExceptionType();
			}

		});

		monitorPathCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (monitorPathCheck.getSelection()){
					monitorPathTable.setEnabled(true);
					monitorPathTable.setBackground(white);
				}else{
					//TODO: 確認メッセージ
					monitorPathTable.removeAll();
//障害 #446 対応
//					monitoringException.getExcludeExceptions().removeAllElements();
					monitoringException.getPlaces().removeAllElements();
					monitorPathTable.setEnabled(false);
					monitorPathTable.setBackground(background);
				}
				updateMonitorPathButtons();
			}
		});
		monitorPathTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = (TableItem)e.item;
				Place place = (Place)item.getData();
				place.setUse(item.getChecked());
			}
		});
		exclusionClassCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (exclusionClassCheck.getSelection()){
					exclusionClassTable.setEnabled(true);
					exclusionClassTable.setBackground(white);
				}else{
					//TODO: 確認メッセージ
					exclusionClassTable.removeAll();
					monitoringException.getExcludeExceptions().removeAllElements();
					exclusionClassTable.setEnabled(false);
					exclusionClassTable.setBackground(background);
				}
				updateExclusionClassButtons();
			}
		});

		exclusionClassTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = (TableItem)e.item;
				FilterException exception = (FilterException)item.getData();
				exception.setUse(item.getChecked());
			}
		});

		//除外クラス追加ボタン
		addExclusionClssButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				Vector<FilterException> ee = monitoringException.getExcludeExceptions();
				ExceptionFilterRegisterDlg dialog = new ExceptionFilterRegisterDlg(thisDialog.getShell());
				FilterException template = new FilterException();
				dialog.init(template, monitoringException.getTargetClassName());
				dialog.setFilterExceptionList(ee);
				int r =dialog.open();
				if (r==Dialog.OK){
					ee.add(ee.size(),template);
					updateFilterExceptionListToTable();
					updateExclusionClassButtons();
				}
			}
		});

		//除外クラス編集ボタン
		editExclusionClssButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ExceptionFilterRegisterDlg dialog = new ExceptionFilterRegisterDlg(thisDialog.getShell());
				int index = exclusionClassTable.getSelectionIndex();
				if (index >=0){
					Vector<FilterException> excludeExceptions = monitoringException.getExcludeExceptions();
					FilterException ee = (FilterException)excludeExceptions.get(index);
					FilterException template = new FilterException();
					ee.copyTo(template);
					dialog.init(template, monitoringException.getTargetClassName());
					dialog.setFilterExceptionList(excludeExceptions);
					int r =dialog.open();
					if (r == Dialog.OK){
						template.copyTo(ee);
						updateFilterExceptionListToTable();
						updateExclusionClassButtons();
					}
				}
			}
		});

		deleteExclusionClssButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = exclusionClassTable.getSelectionIndex();
				if (index >=0){
					monitoringException.getExcludeExceptions().remove(index);
					updateFilterExceptionListToTable();
					updateExclusionClassButtons();
				}
			}
		});

		//監視パス追加ボタン
		addMonitorPathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MonitorPathRegisterDlg dialog = new MonitorPathRegisterDlg(thisDialog);
				dialog.title = ApplicationResource.getResource(
								"Dialog.MonitorException.Path.Title.Text");
				dialog.groupName=ApplicationResource.getResource("Dialog.MonitorException.Path.Group.Text");
				Place place = new Place();
				dialog.init(place);
				int r= dialog.open();
				if (r==Dialog.OK){
					Vector<Place> places = monitoringException.getPlaces();
					places.add(places.size(),place);
					updatePlaceListToTable();
					updateMonitorPathButtons();
				}
			}
		});

		//監視パス編集ボタン
		editMonitorPathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MonitorPathRegisterDlg dialog = new MonitorPathRegisterDlg(thisDialog);
				dialog.title = ApplicationResource.getResource(
							"Dialog.MonitorException.Path.Title.Text");
				dialog.groupName=ApplicationResource.getResource(
							"Dialog.MonitorException.Path.Group.Text");
				int index = monitorPathTable.getSelectionIndex();
				if (index >=0){
					Vector<Place> places = monitoringException.getPlaces();
					Place place = (Place)places.get(index);
					Place template = new Place();
					place.copyTo(template);
					dialog.init(template);
					int r = dialog.open();
					if (r==Dialog.OK){
						template.copyTo(place);
						updatePlaceListToTable();
						updateMonitorPathButtons();
					}
				}
			}
		});
		//監視パス削除ボタン
		deleteMonitorPathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = monitorPathTable.getSelectionIndex();
				if (index >=0){
					monitoringException.getPlaces().remove(index);
					updatePlaceListToTable();
					updateMonitorPathButtons();
				}
			}
		});

		cancelBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				thisDialog.cancelPressed();
			}
		});
		saveBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!checkItems()){
					return;
				}
//				if (!relationCheck()){
//					return;
//				}
				if ((!targetClassName.getText().equals(exceptionName)) && !checkDuplicate()){
					return;
				}
				thisDialog.okPressed();
			}
		});

		monitorPathTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editMonitorPathButton.setEnabled(true);
				deleteMonitorPathButton.setEnabled(true);
			}
		});

		exclusionClassTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editExclusionClssButton.setEnabled(true);
				deleteExclusionClssButton.setEnabled(true);
			}
		});
	}

	private void updateExclusionClassButtons(){
		if ( !exclusionClassCheck.getSelection()){
			addExclusionClssButton.setEnabled(false);
			editExclusionClssButton.setEnabled(false);
			deleteExclusionClssButton.setEnabled(false);
		}else{
//			if (exclusionClassTable.getItemCount()>0){
//				addExclusionClssButton.setEnabled(true);
//				editExclusionClssButton.setEnabled(true);
//				deleteExclusionClssButton.setEnabled(true);
//			}else{
//				editExclusionClssButton.setEnabled(false);
//				deleteExclusionClssButton.setEnabled(false);
//				addExclusionClssButton.setEnabled(true);
//			}
			editExclusionClssButton.setEnabled(false);
			deleteExclusionClssButton.setEnabled(false);
			addExclusionClssButton.setEnabled(true);
		}
	}

	private void updateMonitorPathButtons(){
		if ( !monitorPathCheck.getSelection()){
			addMonitorPathButton.setEnabled(false);
			editMonitorPathButton.setEnabled(false);
			deleteMonitorPathButton.setEnabled(false);
		}else{
//			int index = monitorPathTable.getItemCount();
//			if (index == 0){
//				addMonitorPathButton.setEnabled(true);
//				editMonitorPathButton.setEnabled(false);
//				deleteMonitorPathButton.setEnabled(false);
//			}else{
//				editMonitorPathButton.setEnabled(true);
//				deleteMonitorPathButton.setEnabled(true);
//				addMonitorPathButton.setEnabled(true);
//			}
			addMonitorPathButton.setEnabled(true);
			editMonitorPathButton.setEnabled(false);
			deleteMonitorPathButton.setEnabled(false);
		}
	}

	/**
	 * Modeのデータを画面に更新する場合に呼ばれる。
	 * 他のタイミングに呼ばないこと！
	 */
	private void updateDataToDisplay(){
		//初期状態
		exclusionClassCheck.setSelection(false);
		monitorPathTable.setEnabled(false);
		exclusionClassTable.setBackground(background);

		monitorPathCheck.setSelection(false);
		monitorPathTable.setEnabled(false);
		monitorPathTable.setBackground(background);

		addMonitorPathButton.setEnabled(false);
		editMonitorPathButton.setEnabled(false);
		deleteMonitorPathButton.setEnabled(false);

		addExclusionClssButton.setEnabled(false);
		editExclusionClssButton.setEnabled(false);
		deleteExclusionClssButton.setEnabled(false);

		exceptionName = monitoringException.getTargetClassName();
		targetClassName.setText(monitoringException.getTargetClassName());

		Vector<Place> places = monitoringException.getPlaces();
		if (places.size()>0){
			monitorPathCheck.setSelection(true);
			monitorPathTable.setEnabled(true);		//障害 #447 add by tu
			monitorPathTable.setBackground(white);
			updatePlaceListToTable();
			updateMonitorPathButtons();
		}
		Vector<FilterException> excludes =monitoringException.getExcludeExceptions();
		if (excludes.size()>0){
			exclusionClassCheck.setSelection(true);
			exclusionClassTable.setBackground(white);
			updateFilterExceptionListToTable();
			updateExclusionClassButtons();
		}
	}

	/**
	 * 監視経路のデータをテーブルにアップして表示する。
	 * 　１．初期
	 * 　２．監視経路の追加、削除に呼ばれる。
	 * @author tu
	 * @since 2007/11/23
	 */
	private void updatePlaceListToTable(){
		Vector<Place> places = monitoringException.getPlaces();
		monitorPathTable.removeAll();
		for(int i=0; i<places.size(); i++){
			Place place = (Place)places.get(i);
			StringBuffer root = new StringBuffer();
			if ("".equals(place.getClassName())){
				root.append("\"\"->");
			}else{
				root.append(place.getClassName());
				root.append("->");
			}
			if ("".equals(place.getMethodName())){
				root.append("\"\"");
			}else{
				root.append(place.getMethodName());
			}
			TableItem item = new TableItem(monitorPathTable, SWT.NONE);
			item.setText(root.toString());
			item.setData(place);
			item.setChecked(place.isUse());
		}
		if (monitorPathCheck.getSelection()) {
			monitorPathTable.setBackground(white);
		}
	}

	/**
	 * 除外対象データをテーブルにアップして表示させる。
	 *　１．初期
	 *　２．監視対象の追加、削除に呼ばれる。
	 */
	private void updateFilterExceptionListToTable(){
		exclusionClassTable.removeAll();
		Vector<FilterException> excludes =monitoringException.getExcludeExceptions();
		for(int i=0; i<excludes.size(); i++){
			FilterException exclude = (FilterException)excludes.get(i);
			String excludeClassName = exclude.getExcludeClassName();
			TableItem item = new TableItem(exclusionClassTable, SWT.NONE);
			item.setText(excludeClassName);
			item.setData(exclude);
			item.setChecked(exclude.isUse());
		}
		if (exclusionClassCheck.getSelection()) {
			exclusionClassTable.setBackground(white);
		}
	}

	/**
	 * 項目チェック
	 * @return
	 */
	private boolean checkItems(){
		boolean result = true;
		if (!ViewerUtil.checkClassName(targetClassName.getText())){
			targetClassName.setBackground(red);
			result = false;
		}
		Vector<FilterException> excludes =monitoringException.getExcludeExceptions();
		for(int i=0; i<excludes.size(); i++){
			FilterException exclude = (FilterException)excludes.get(i);
			if (!exclude.checkItems()){
				exclusionClassTable.setBackground(red);
				result = false;
			}
			for (int j=i+1; j<excludes.size();j++){
				FilterException another = (FilterException)excludes.get(j);
				if (exclude.getExcludeClassName().equals(another.getExcludeClassName())){
					exclusionClassTable.setBackground(red);
					result = false;
				}
			}
		}
		Vector<Place> places = monitoringException.getPlaces();
		for(int i=0; i<places.size(); i++){
			Place place = (Place)places.get(i);
			if (!place.checkItems()){
				monitorPathTable.setBackground(red);
				result = false;
			}
		}

		if (monitorPathCheck.getSelection() &&
				monitoringException.getPlaces().size() == 0) {
			monitorPathTable.setBackground(red);
			result = false;
		}
		if (exclusionClassCheck.getSelection() &&
				monitoringException.getExcludeExceptions().size() == 0) {
			exclusionClassTable.setBackground(red);
			result = false;
		}

		return result;
	}

	/**
	 * 関連チェック
	 * @return
	 */
//	private boolean relationCheck(){
//		boolean result = ConfigModel.hasSameMonitoringClassName(
//							monitoringException,
//							orgMonitoringException);
//		if (result){
//			targetClassName.setBackground(red);
//			ExcatMessageUtilty.showMessage(
//					this.getShell(),
//					Message.get("Dialog.Task.ExceptionMonitor.ClassName.Text"));
//			return false;
//		}else{
//			return true;
//		}
//	}

	private void createContentAssistant(){
		String[] exceptionList = SettingManager.getRepository().getExceptionList();
		assistant = new ContentAssistant();
		IContentAssistProcessor processor = new ContentAssistProcessor(
				exceptionList);
		assistant.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
		assistant.install(targetClassName.getTextViewer());

		targetClassName.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (HelperFunc.isLetterOrDot(e.character)) {
					assistant.showPossibleCompletions();
				}
			}
		});
	}
	private void checkExceptionType(){
		String name = targetClassName.getText();
		if (TaskList.THROWABLE.equals(name)){
			targetClassName.setBackground(red);
			String msg = Message.get("Dialog.Task.Monitor.Exception.Throwable.Text");
			ExcatMessageUtilty.showMessage(this.getShell(), msg);
		}
	}

	private boolean checkDuplicate(){
		for (MonitoringException m : monitoringExceptionList){
			if (m.getTargetClassName().equals(monitoringException.getTargetClassName())){
				targetClassName.setBackground(red);
				String msg = monitoringException.getTargetClassName()
				+ Message.get("Dialog.Task.Monitor.Same.Text");
				ExcatMessageUtilty.showMessage(thisDialog.getShell(), msg);
				return false;
			}
		}
		return true;
	}
}

