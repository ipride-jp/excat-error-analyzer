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
import jp.co.ipride.excat.configeditor.model.task.ExcludePlace;
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
 * 除外クラスの編集画面
 *
 * @author tu
 * @since 2007/11/12
 *
 */
public class ExceptionFilterRegisterDlg extends Dialog{

	private static int text_width    = 500;
	private static final int MAX_QUEUE_SIZE = 200;

	private ExcatTextViewer filterExceptionText;	//除外例外

	private Table applyPlaceTable;			//除外適用経路
	private Button applyPlaceCheck;
	private Button addPathButton;
	private Button editPathButton;
	private Button deletePathButton;

	private Button saveBtn;
	private Button cancelBtn;

	private ExceptionFilterRegisterDlg thisDialog;

	private Color red;
	private Color background;
	private Color white;

	private ContentAssistant assistant;
	private WordTracker wordTracker = new WordTracker(MAX_QUEUE_SIZE);

	private FilterException exceptionFilterMode = null;
	private String monitorException=null;

	private Vector<FilterException> filterExceptionList;

	private String exceptionName = "";


	public ExceptionFilterRegisterDlg(Shell shell){
		super(shell);
		thisDialog = this;
	}

	public void init(FilterException exclude, String monitorException){
		this.exceptionFilterMode=exclude;
		this.monitorException = monitorException;
	}

	public void setFilterExceptionList(Vector<FilterException> list){
		this.filterExceptionList = list;
	}

	/**
	 * 画面を起動時に初回のみ
	 */
	private void updateDataToDisplay(){
		//初期状態
		applyPlaceTable.setEnabled(false);
		applyPlaceTable.setBackground(background);
		addPathButton.setEnabled(false);
		editPathButton.setEnabled(false);
		deletePathButton.setEnabled(false);

		exceptionName = exceptionFilterMode.getExcludeClassName();
		filterExceptionText.setText(exceptionFilterMode.getExcludeClassName());

		updatePlaceTable();

	}

	/**
	 * 各タイミングで呼ばれる。
	 */
	private void updatePlaceTable(){
		applyPlaceTable.removeAll();
		Vector<ExcludePlace> applyPlaces = exceptionFilterMode.getApplyPlaces();
		if (applyPlaces.size()>0){
			applyPlaceCheck.setSelection(true);
			applyPlaceTable.setEnabled(true);
			applyPlaceTable.setBackground(white);
			for (int i=0; i<applyPlaces.size(); i++){
				ExcludePlace place = (ExcludePlace)applyPlaces.get(i);

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
				if (!"".equals(place.getMethodSignature())){
					root.append("    "+ place.getMethodSignature());
				}
				TableItem item =new TableItem(applyPlaceTable, SWT.NONE);
				item.setText(new String[] {root.toString()});
				item.setData(place);
				item.setChecked(place.isUse());
			}
			checkPathButtons();
		}
	}

	/**
	 * override method
	 * 1.create layout
	 */
	protected Control createContents(Composite parent) {
		Composite spaceComposite;
		GridData spaceGridData;

		this.getShell().setText(
				ApplicationResource.getResource("Dialog.ExcludeException.Title.Text"));
		red = this.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		white = this.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);

		Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		layout.numColumns=1;
        composite.setLayout(layout);
        GridData compositeGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        composite.setLayoutData(compositeGridData);

        //除外クラス
        Group filterExceptionGroup= new Group(composite,SWT.NONE);
        filterExceptionGroup.setText(
        		ApplicationResource.getResource("Dialog.ExcludeException.Class.Text"));
		GridLayout filterExceptionGrouplayout = new GridLayout();
		filterExceptionGrouplayout.marginTop = ViewerUtil.getMarginHeight(filterExceptionGroup);
		filterExceptionGrouplayout.marginBottom = ViewerUtil.getMarginHeight(filterExceptionGroup);
		filterExceptionGrouplayout.marginLeft = ViewerUtil.getMarginWidth(filterExceptionGroup);
		filterExceptionGrouplayout.marginRight = ViewerUtil.getMarginWidth(filterExceptionGroup);
		filterExceptionGroup.setLayout(filterExceptionGrouplayout);
		filterExceptionGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL ));
        filterExceptionText = new ExcatTextViewer(filterExceptionGroup, text_width, 1);

        //空白スペース
		spaceComposite = new Composite(composite, SWT.NONE);
		spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spaceGridData.heightHint=10;
        spaceComposite.setLayoutData(spaceGridData);

        //経路
        Group pathGroup= new Group(composite,SWT.NONE);
        pathGroup.setText(
        		ApplicationResource.getResource("Dialog.ExcludeException.Path.Text"));
		GridLayout pathGrouplayout = new GridLayout();
		pathGrouplayout.marginTop = ViewerUtil.getMarginHeight(pathGroup);
		pathGrouplayout.marginBottom = ViewerUtil.getMarginHeight(pathGroup);
		pathGrouplayout.marginLeft = ViewerUtil.getMarginWidth(pathGroup);
		pathGrouplayout.marginRight = ViewerUtil.getMarginWidth(pathGroup);
		pathGroup.setLayout(pathGrouplayout);
		GridData pathGridData=new GridData(GridData.HORIZONTAL_ALIGN_FILL );
		pathGroup.setLayoutData(pathGridData);

		applyPlaceCheck = new Button(pathGroup, SWT.CHECK);
		applyPlaceCheck.setText(
        		ApplicationResource.getResource("Dialog.ExcludeException.Path.Check.Text"));
		applyPlaceTable = new Table(pathGroup, SWT.BORDER | SWT.CHECK | SWT.V_SCROLL | SWT.H_SCROLL);
        GridData pathListGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        pathListGridData.verticalSpan = 4;
        int listHeight = applyPlaceTable.getItemHeight() * 5;
        Rectangle trim = applyPlaceTable.computeTrim(0, 0, 0, listHeight);
        pathListGridData.heightHint = trim.height-20;
        applyPlaceTable.setLayoutData(pathListGridData);

        addPathButtons(pathGroup);

        //空白スペース
		spaceComposite = new Composite(composite, SWT.NONE);
		spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spaceGridData.heightHint=10;
        spaceComposite.setLayoutData(spaceGridData);

		//保存ボタン＆保存しないボタン
		Composite buttonForm = new Composite(composite,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonFormlayout.numColumns=3;
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonForm.setLayoutData(gd);

		saveBtn = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Dialog.Button.Enter.Text"),
				Utility.BUTTON_WIDTH,1);

		cancelBtn = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Dialog.Button.Cancel.Text"),
				Utility.BUTTON_WIDTH,1);

		createContentAssistant();

		addListeners();

		updateDataToDisplay();

		cancelBtn.setFocus();

		return composite;
	}

	private void addPathButtons(Composite composite){
		Composite buttonForm = new Composite(composite,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonFormlayout.numColumns=3;
		buttonFormlayout.marginHeight=1;
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		gd.widthHint=text_width;
		buttonForm.setLayoutData(gd);

		addPathButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.add"),
				Utility.BUTTON_WIDTH,1);

		editPathButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.edit"),
				Utility.BUTTON_WIDTH,1);

		deletePathButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.delete"),
				Utility.BUTTON_WIDTH,1);

	}

	private void addListeners(){
		filterExceptionText.addKeyListener(new KeyAdapter() {
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

		filterExceptionText.addTextListener(new ITextListener() {
		      public void textChanged(TextEvent e) {
		    	  filterExceptionText.setBackground(white);
		    	  String word = e.getText();
		    	  if (ProposalUtility.isWhitespaceString(word)) {
		    		  wordTracker.add(
		    				  ProposalUtility.findMostRecentWord(
		    						  filterExceptionText, e.getOffset() - 1));
		    	  }
		  		exceptionFilterMode.setExcludeClassName(filterExceptionText.getText());
		      }
		    });

		applyPlaceCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (applyPlaceCheck.getSelection()){
					applyPlaceTable.setEnabled(true);
					applyPlaceTable.setBackground(white);
				}else{
					//TODO 確認メッセージ
					exceptionFilterMode.getApplyPlaces().removeAllElements();
					applyPlaceTable.removeAll();
					applyPlaceTable.setEnabled(false);
					applyPlaceTable.setBackground(background);
				}
				checkPathButtons();
			}
		});

		applyPlaceTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = (TableItem)e.item;
				ExcludePlace place = (ExcludePlace)item.getData();
				place.setUse(item.getChecked());
			}
		});

		//パス追加ボタン
		addPathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MonitorPathRegisterDlg dialog = new MonitorPathRegisterDlg(thisDialog);
				dialog.title = ApplicationResource.getResource(
										"Dialog.ExcludeException.Path.Title.Text");
				dialog.groupName = ApplicationResource.getResource(
										"Dialog.ExcludeException.Path.Group.Text");
				ExcludePlace template = new ExcludePlace();
				dialog.init(template);
				int r = dialog.open();
				if (r==Dialog.OK){
					Vector<ExcludePlace> places = exceptionFilterMode.getApplyPlaces();
					places.add(places.size(), template);
					updatePlaceTable();
				}
			}
		});

		//パス編集ボタン
		editPathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MonitorPathRegisterDlg dialog = new MonitorPathRegisterDlg(thisDialog);
				dialog.title = ApplicationResource.getResource(
							"Dialog.ExcludeException.Path.Title.Text");
				dialog.groupName = ApplicationResource.getResource(
						"Dialog.ExcludeException.Path.Group.Text");
				int index = applyPlaceTable.getSelectionIndex();
				if (index >=0){
					Vector<ExcludePlace> places = exceptionFilterMode.getApplyPlaces();
					ExcludePlace place = (ExcludePlace)places.get(index);
					ExcludePlace template = new ExcludePlace();
					place.copyTo(template);
					dialog.init(template);
					int r = dialog.open();
					if (r== Dialog.OK){
						template.copyTo(place);
						updatePlaceTable();
					}
				}
			}
		});

		//パス削除ボタン
		deletePathButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = applyPlaceTable.getSelectionIndex();
				if (index >=0){
					applyPlaceTable.remove(index);
					exceptionFilterMode.getApplyPlaces().remove(index);
				}
				if (applyPlaceTable.getItemCount() == 0) {
					editPathButton.setEnabled(false);
					deletePathButton.setEnabled(false);
				}
				updatePlaceTable();
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
				if ((!filterExceptionText.getText().equals(exceptionName)) && !checkDuplicateFilterException()){
					return;
				}
				thisDialog.okPressed();
			}
		});

		applyPlaceTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				editPathButton.setEnabled(true);
				deletePathButton.setEnabled(true);
			}
		});
	}

	private void checkPathButtons(){
		if (applyPlaceCheck.getSelection()){
			int index = applyPlaceTable.getItemCount();
			if (index <= 0){
				addPathButton.setEnabled(true);
				editPathButton.setEnabled(false);
				deletePathButton.setEnabled(false);
			}
			else{
				addPathButton.setEnabled(true);
//				editPathButton.setEnabled(true);
//				deletePathButton.setEnabled(true);
				editPathButton.setEnabled(false);
				deletePathButton.setEnabled(false);
			}
		}else{
			addPathButton.setEnabled(false);
			editPathButton.setEnabled(false);
			deletePathButton.setEnabled(false);
		}
	}

	private boolean checkItems(){
		boolean result = true;
		if (!ViewerUtil.checkClassName(filterExceptionText.getText())){
			filterExceptionText.setBackground(red);
			result = false;
		}
		Vector<ExcludePlace> applyPlaces = exceptionFilterMode.getApplyPlaces();
		for (int i=0; i<applyPlaces.size(); i++){
			ExcludePlace place = (ExcludePlace)applyPlaces.get(i);
			if (!place.checkItems()){
				applyPlaceTable.setBackground(red);
				result = false;
			}
		}
		if (applyPlaceCheck.getSelection() &&
				exceptionFilterMode.getApplyPlaces().size() == 0) {
			applyPlaceTable.setBackground(red);
			result = false;
		}
		return result;
	}

	private void createContentAssistant(){

		String[] exceptionList = SettingManager.getRepository().
									getAllImplementClassList(this.monitorException);
		assistant = new ContentAssistant();
		IContentAssistProcessor processor = new ContentAssistProcessor(
				exceptionList);
		assistant.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
		assistant.install(filterExceptionText.getTextViewer());

		filterExceptionText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (HelperFunc.isLetterOrDot(e.character)) {
					assistant.showPossibleCompletions();
				}
			}
		});
	}

	public boolean checkDuplicateFilterException(){

		for (FilterException exception : filterExceptionList){
			if (exception.getExcludeClassName().equals(
					exceptionFilterMode.getExcludeClassName())){
				filterExceptionText.setBackground(red);
				String msg = exceptionFilterMode.getExcludeClassName()
				+ Message.get("Dialog.Task.Monitor.Same.Text");
				ExcatMessageUtilty.showMessage(thisDialog.getShell(), msg);
				return false;
			}
		}
		return true;

	}
}

