package jp.co.ipride.excat.configeditor.viewer.task.dialog;

import java.util.Vector;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MethodInfo;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.ExcatText;
import jp.co.ipride.excat.configeditor.ExcatTextViewer;
import jp.co.ipride.excat.configeditor.model.task.MonitoringMethod;
import jp.co.ipride.excat.configeditor.model.task.Place;
import jp.co.ipride.excat.configeditor.util.ProposalUtility;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.configeditor.viewer.contentassist.ContentAssistProcessor;
import jp.co.ipride.excat.configeditor.viewer.contentassist.WordTracker;
import jp.co.ipride.excat.configeditor.viewer.task.MonitorMethodTaskForm;

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
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * メソッドを監視する画面
 * @author tu
 * @since 2007/11/12
 *
 */
public class MethodRegisterDlg extends Dialog{

	private static int text_width    = 500;
	private static final int MAX_QUEUE_SIZE = 200;

	private ExcatTextViewer classNameText;      //class name
	private ExcatTextViewer methodNameText;     //method name
	private ExcatText classSuffixText;    //suffix name for this class url
	private Button classSuffixCheck; //select suffix name for this method
	private ExcatTextViewer methodSignatureText;  //signature of this method
	private Button methodSignatureCheck;  //select signature of this method
	private Button classLoaderUrlCheck;  //select class loader for this class
	private ExcatText classLoaderUrlText;     //class loader for this class
	private Button conditionCheck;       //select condition
	private ExcatText conditionText;          //condition
	private ExcatText maxDumpCountText;       //max count of dump
	private Button maxDumpCountCheck;    //select max count of dump

	private Button interfaceSelect;      //select interface

	private Label classNameLabel;
	private Label methodNameLabel;
	private Label maxDumpCountLabel;

	private Button startPositionCheck;
	private Button finishPositionCheck;

	private Button saveBtn;
	private Button cancelBtn;

	private Color red;
	private Color background;
	private Color white;

	private MethodRegisterDlg thisDialog;
	private MonitoringMethod monitoringMethod = null;

	//補助入力
	private ContentAssistant assistantForClass;
	private ContentAssistant assistantForMethod;
	private ContentAssistProcessor methodProcessor;
	private ContentAssistant assistantForMethodSig;
	private ContentAssistProcessor methodSigProcessor;

	private WordTracker wordTracker = new WordTracker(MAX_QUEUE_SIZE);

	//チェックした結果、該当クラスはインターフェースである場合、Trueにする。
	//インターフェース・ボタンを押すと該当の内容をインターフェース登録画面に転送する。
	private boolean isInterface = false;

	private Vector<MonitoringMethod> methodMonitors = null;
	private String oldClassName = "";
	private String oldMethodName = "";
	private String oldMethodSignature = "";
	private boolean isSpecified = false;

	/**
	 * construct
	 * @param parent
	 */
	public MethodRegisterDlg(MonitorMethodTaskForm parent){
		super(parent.getShell());
		thisDialog = this;
	}

	/**
	 * 初期条件の場合
	 * @param monitoringMethod
	 */
	public void init(MonitoringMethod monitoringMethod, Vector<MonitoringMethod> methodMonitors){
		this.monitoringMethod = monitoringMethod;
		this.methodMonitors = methodMonitors;
	}

	/**
	 * 分析画面から指定メソッドがある場合
	 * @param monitoringMethod
	 * @param methodInfo
	 */
	public void init(MonitoringMethod monitoringMethod, MethodInfo methodInfo, Vector<MonitoringMethod> methodMonitors){
		monitoringMethod.setClassName(methodInfo.getClassSig());
		monitoringMethod.setMethodName(methodInfo.getMethodName());
		monitoringMethod.setMethodSignature(methodInfo.getMethodSig());
		this.monitoringMethod = monitoringMethod;
		this.methodMonitors = methodMonitors;
		this.isSpecified = true;
	}

	private void updateDataToDisplay(){
		oldClassName = monitoringMethod.getClassName();
		oldMethodName = monitoringMethod.getMethodName();
		oldMethodSignature = monitoringMethod.getMethodSignature();
		classNameText.setText(monitoringMethod.getClassName());
		methodNameText.setText(monitoringMethod.getMethodName());
		classSuffixText.setText(monitoringMethod.getClassLoader_suffix());
		methodSignatureText.setText(monitoringMethod.getMethodSignature());
		classLoaderUrlText.setText(monitoringMethod.getClassLoaderURL());
		conditionText.setText(monitoringMethod.getCondition());

		if (!"".equals(methodSignatureText.getText())){
			methodSignatureCheck.setSelection(true);
			methodSignatureText.setEnabled(true);
			methodSignatureText.setBackground(white);
		}else{
			methodSignatureCheck.setSelection(false);
			methodSignatureText.setEnabled(false);
			methodSignatureText.setBackground(background);
		}
		if (!"".equals(classLoaderUrlText.getText())){
			classLoaderUrlCheck.setSelection(true);
			classLoaderUrlText.setEnabled(true);
			classLoaderUrlText.setBackground(white);
		}else{
			classLoaderUrlCheck.setSelection(false);
			classLoaderUrlText.setEnabled(false);
			classLoaderUrlText.setBackground(background);
		}
		if (!"".equals(classSuffixText.getText())){
			classSuffixCheck.setSelection(true);
			classSuffixText.setEnabled(true);
			classSuffixText.setBackground(white);
		}else{
			classSuffixCheck.setSelection(false);
			classSuffixText.setEnabled(false);
			classSuffixText.setBackground(background);
		}

		if (!"".equals(conditionText.getText())){
			conditionCheck.setSelection(true);
			conditionText.setEnabled(true);
			conditionText.setBackground(white);
		}else{
			conditionCheck.setSelection(false);
			conditionText.setEnabled(false);
			conditionText.setBackground(background);
		}

		if (monitoringMethod.getMaxDumpCount()==0){
			maxDumpCountCheck.setSelection(false);
			maxDumpCountText.setBackground(background);
		}else{
			maxDumpCountCheck.setSelection(true);
			maxDumpCountText.setText(Integer.toString(monitoringMethod.getMaxDumpCount()));
			maxDumpCountText.setBackground(white);
		}
		if (MonitoringMethod.BOTH_POSITION.equals(monitoringMethod.getPosition())){
			startPositionCheck.setSelection(true);
			finishPositionCheck.setSelection(true);
		}else if (MonitoringMethod.START_POSITION.equals(monitoringMethod.getPosition())){
			startPositionCheck.setSelection(true);
			finishPositionCheck.setSelection(false);
		}else if (MonitoringMethod.FINISH_POSITION.equals(monitoringMethod.getPosition())){
			startPositionCheck.setSelection(false);
			finishPositionCheck.setSelection(true);
		}
	}


	/**
	 * override method
	 * 1.create layout
	 */
	protected Control createContents(Composite parent) {
		this.getShell().setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.Title.Text"));
		red = this.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		white = this.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		background = this.getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

		//この画面のレイアウト
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

        //監視するメソッド定義のエリア
        addMethodForm(composite);
        addSpaceLine(composite);

	    //--------------------監視箇所 -------------------------------
        addPositionArea(composite);
        addSpaceLine(composite);

		//監視条件の定義
        addConditionForm(composite);

        //class loader group
        addClassLoaderForm(composite);

        //最大ダンプ回数
        addMaxDumpCount(composite);

        //空白スペース
        addSpaceLine(composite);

		//保存ボタン＆保存しないボタン
        addButtonForm(composite);

		createClassContentAssistant();
		createMethodContentAssistant();
		createMethodSigContentAssistant();

		addListeners();

		updateDataToDisplay();

		cancelBtn.setFocus();

		return composite;
	}


	private void addMethodForm(Composite composite){
        Group methodGroup = new Group(composite, SWT.NONE);
        methodGroup.setText(
        		ApplicationResource.getResource("Dialog.RegisterMethod.Monitor.Text"));
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		layout.numColumns=3;
		methodGroup.setLayout(layout);
		methodGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

        //add elements on methodSite
		classNameLabel = new Label(methodGroup, SWT.NONE);
		ViewerUtil.setControlWidth(classNameLabel,72);
		classNameLabel.setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.ClassName.Text"));
		classNameText = new ExcatTextViewer(methodGroup,text_width ,2);

        methodNameLabel = new Label(methodGroup, SWT.NONE);
		methodNameLabel.setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.MethodName.Text"));
		methodNameText = new ExcatTextViewer(methodGroup,text_width,2);
		Label sigLabel = new Label(methodGroup, SWT.NONE);
		sigLabel.setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.SigName.Text"));
		methodSignatureCheck = new Button(methodGroup, SWT.CHECK);
		methodSignatureText=new ExcatTextViewer(methodGroup,text_width,	1);

		//add select button for interface
		interfaceSelect = Utility.createButton(methodGroup, SWT.PUSH,
				ApplicationResource.getResource("Dialog.RegisterMethod.Interface.Text"),
				Utility.BUTTON_WIDTH,3);

	}

	private void addClassLoaderForm(Composite composite){
		Group urlGroup = new Group(composite, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		layout.numColumns=3;
		urlGroup.setLayout(layout);
		urlGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		Label urlLabel = new Label(urlGroup, SWT.NONE);
		ViewerUtil.setControlWidth(urlLabel,55);
		urlLabel.setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.URL.Text"));
		classLoaderUrlCheck = new Button(urlGroup,SWT.CHECK);
		classLoaderUrlText = new ExcatText(urlGroup,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		classLoaderUrlText.setControlWidth(text_width);

		Label clsSuffixLabel = new Label(urlGroup, SWT.NONE);
		clsSuffixLabel.setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.Suffix.Text"));
		classSuffixCheck = new Button(urlGroup, SWT.CHECK);
		classSuffixText = new ExcatText(urlGroup,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
		classSuffixText.setControlWidth(text_width);
	}

	private void addConditionForm(Composite composite){
		Group conditionGroup = new Group(composite, SWT.NONE);
		conditionGroup.setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.Condition.Text"));
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		conditionGroup.setLayout(layout);
		conditionGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		conditionCheck = new Button(conditionGroup, SWT.CHECK);
		conditionCheck.setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.Condition.Check.Text"));
		conditionText = new ExcatText(conditionGroup,SWT.BORDER |SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        gd.verticalSpan = 4;
        int listHeight = conditionText.getLineHeight()*5;
        Rectangle trim = conditionText.computeTrim(0, 0, 0, listHeight);
        gd.heightHint = trim.height;
        gd.widthHint = text_width + 60;
        conditionText.setLayoutData(gd);
	}
	private void addPositionArea(Composite composite){
        Group watchGroup = new Group(composite, SWT.NONE);
        watchGroup.setText(
        		ApplicationResource.getResource("Dialog.RegisterMethod.Point")
        	);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		watchGroup.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		watchGroup.setLayoutData(gd);

		Composite prefixForm = new Composite(watchGroup,SWT.NONE);
		GridLayout prefixFormlayout = new GridLayout();
		prefixFormlayout.numColumns = 2;
		prefixFormlayout.marginHeight = 1;
		prefixForm.setLayout(prefixFormlayout);
		gd = new GridData(GridData.FILL_BOTH);
		prefixForm.setLayoutData(gd);

	    startPositionCheck = new Button(watchGroup, SWT.CHECK);
	    startPositionCheck.setText(
	    		ApplicationResource.getResource("Dialog.RegisterMethod.Point.Call"));
	    finishPositionCheck = new Button(watchGroup, SWT.CHECK);
	    finishPositionCheck.setText(
	    		ApplicationResource.getResource("Dialog.RegisterMethod.Point.Return"));

	}

	private void addMaxDumpCount(Composite composite){
		Group maxDumpCountForm = new Group(composite,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		maxDumpCountForm.setLayout(layout);
		maxDumpCountForm.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		maxDumpCountCheck = new Button(maxDumpCountForm, SWT.CHECK);
		maxDumpCountCheck.setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.Runtime.Check"));

		Group group = new Group(maxDumpCountForm, SWT.NONE);
		layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		layout.numColumns=3;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		maxDumpCountLabel = new Label(group, SWT.NONE);
		maxDumpCountLabel.setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.Runtime.Text"));
		maxDumpCountText = new ExcatText(group,SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		maxDumpCountText.setControlWidth(50);
		Label unitLabel = new Label(group, SWT.NONE);
		unitLabel.setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.Runtime.Unit.Text"));
	}

	private void addButtonForm(Composite composite){
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

	}

	private void addSpaceLine(Composite composite){
		Composite comp = new Composite(composite, SWT.NONE);
        GridData spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spaceGridData.heightHint = ViewerUtil.getMarginHeight(composite);
        comp.setLayoutData(spaceGridData);
	}

	private void addListeners(){
		classNameText.addKeyListener(new KeyAdapter() {
		      public void keyPressed(KeyEvent e) {
		        switch (e.keyCode) {
		        case SWT.F1:
		          assistantForClass.showPossibleCompletions();
		          break;
		        default:
		        //ignore everything else
		        }
		      }
		    });

		classNameText.addTextListener(new ITextListener() {
			public void textChanged(TextEvent e) {
				String word = e.getText();
				if (ProposalUtility.isWhitespaceString(word)) {
					wordTracker.add(
						ProposalUtility.findMostRecentWord(
								classNameText, e.getOffset() - 1));
				}
				classNameText.setBackground(white);
			}
		});
		methodNameText.addKeyListener(new KeyAdapter() {
		      public void keyPressed(KeyEvent e) {
		        switch (e.keyCode) {
		        case SWT.F1:
		          assistantForMethod.showPossibleCompletions();
		          break;
		        default:
		        //ignore everything else
		        }
		      }
		    });

		methodNameText.addTextListener(new ITextListener() {
		      public void textChanged(TextEvent e) {
		    	  String word = e.getText();
		    	  if (ProposalUtility.isWhitespaceString(word)) {
		    		  wordTracker.add(
		    				  ProposalUtility.findMostRecentWord(
		    						  methodNameText, e.getOffset() - 1));
		    	  }
		    	  methodNameText.setBackground(white);
			}
		});
		methodNameText.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				String className = classNameText.getText().trim();
				if (!"".equals(className)){
					String[] classList = SettingManager.getRepository().getClassMethodList(className);
					methodProcessor.setCandidates(classList);
				}
			}
			public void focusLost(FocusEvent e) {
			}
		});
		methodSignatureText.addKeyListener(new KeyAdapter() {
		      public void keyPressed(KeyEvent e) {
		        switch (e.keyCode) {
		        case SWT.F1:
		          assistantForMethodSig.showPossibleCompletions();
		          break;
		        default:
		        //ignore everything else
		        }
		      }
		    });

		methodSignatureText.addTextListener(new ITextListener() {
		      public void textChanged(TextEvent e) {
		    	  String word = e.getText();
		    	  if (ProposalUtility.isWhitespaceString(word)) {
		    		  wordTracker.add(
		    				  ProposalUtility.findMostRecentWord(
		    						  methodSignatureText, e.getOffset() - 1));
		    	  }
		    	  methodSignatureText.setBackground(white);
		      }
		});
		methodSignatureText.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				String className = classNameText.getText().trim();
				String methodName = methodNameText.getText().trim();
				if ( !"".equals(className) && !"".equals(methodName)){
					String[] methodSigList = SettingManager.getRepository().
										getClassMethosSigList(className, methodName);
					methodSigProcessor.setCandidates(methodSigList);
				}
			}
			public void focusLost(FocusEvent e) {
			}
		});
		classSuffixText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
		    	  classSuffixText.setBackground(white);
			}
		});
		classLoaderUrlText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
		    	  classLoaderUrlText.setBackground(white);
			}
		});
		conditionText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
		    	  conditionText.setBackground(white);
			}
		});
		maxDumpCountText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
		    	  maxDumpCountText.setBackground(white);
		    	  maxDumpCountText.setFocus();
			}
		});

		maxDumpCountText.addVerifyListener(
				new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						e.doit=ViewerUtil.verifyNumbers(e.text);
					}
				});
		maxDumpCountCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (maxDumpCountCheck.getSelection()){
					maxDumpCountText.setBackground(white);
					maxDumpCountText.setEnabled(true);			//障害 #433 add
					maxDumpCountText.setFocus();
				}else{
					maxDumpCountText.setText("");
					maxDumpCountText.setBackground(background);
					maxDumpCountText.setEnabled(false);			//障害 #433 add
				}
			}
		});
		classLoaderUrlCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (classLoaderUrlCheck.getSelection()){
					classLoaderUrlText.setEnabled(true);
					classLoaderUrlText.setBackground(white);
					classLoaderUrlText.setFocus();
				}else{
					classLoaderUrlText.setEnabled(false);
					classLoaderUrlText.setText("");
					classLoaderUrlText.setBackground(background);
				}
			}
		});
		methodSignatureCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (methodSignatureCheck.getSelection()){
					methodSignatureText.setEnabled(true);
					methodSignatureText.setBackground(white);
				}else{
					methodSignatureText.setEnabled(false);
					methodSignatureText.setText("");
					methodSignatureText.setBackground(background);
					conditionCheck.setSelection(false);
					conditionText.setText("");
					conditionText.setBackground(background);
				}
			}
		});
		classSuffixCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (classSuffixCheck.getSelection()){
					classSuffixText.setEnabled(true);
					classSuffixText.setBackground(white);
					classSuffixText.setFocus();
				}else{
					classSuffixText.setText("");
					classSuffixText.setBackground(background);
					classSuffixText.setEnabled(false);
				}
			}
		});
		conditionCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (conditionCheck.getSelection()){
					if ("".equals(classNameText.getText().trim())
						|| "".equals(methodNameText.getText().trim())
						|| "".equals(methodSignatureText.getText().trim())){
						conditionCheck.setSelection(false);
						ExcatMessageUtilty.showMessage(
								thisDialog.getShell(),
								Message.get("Dialog.Task.Monitor.Method.Check.3.Text"));
					}else{
						conditionText.setEnabled(true);
						conditionText.setBackground(white);
						conditionText.setFocus();
					}
				}else{
					conditionText.setText("");
					conditionText.setBackground(background);
					conditionText.setEnabled(false);
				}
			}
		});
		interfaceSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				InterfaceEditDlg dialog = new InterfaceEditDlg(thisDialog);
				if (isInterface){
					dialog.setInterfaceName(classNameText.getText());
					dialog.setMethodName(methodNameText.getText());
					dialog.setMethodSig(methodSignatureText.getText());
				}
				int r= dialog.open();
				if (r==Dialog.OK){
					classNameText.setText(dialog.getClassName());
					methodNameText.setText(dialog.getMethodName());
					methodSignatureText.setText(dialog.getSignature());
					methodSignatureCheck.setSelection(true);
					methodSignatureText.setBackground(white);
				}
			}
		});
		startPositionCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!startPositionCheck.getSelection()){
					finishPositionCheck.setSelection(true);
				}
			}
		});
		finishPositionCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (!finishPositionCheck.getSelection()){
					startPositionCheck.setSelection(true);
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
				updateMode();

				if ((isSpecified ||
						!classNameText.getText().equals(oldClassName) ||
						!methodNameText.getText().equals(oldMethodName) ||
						!methodSignatureText.getText().equals(oldMethodSignature)) && !checkDuplicate()){
					return;
				}
				thisDialog.okPressed();
			}
		});
	}

	/**
	 * 単項目チェック
	 * @return
	 */
	private boolean checkItems(){
		boolean result = true;
		if (!ViewerUtil.checkClassName(classNameText.getText())){
			classNameText.setBackground(red);
			result=false;
		}
		if (!ViewerUtil.checkMethodName(methodNameText.getText())){
			methodNameText.setBackground(red);
			result = false;
		}
		if (classLoaderUrlCheck.getSelection()){
			if (!ViewerUtil.checkClassLoader(classLoaderUrlText.getText())){
				classLoaderUrlText.setBackground(red);
				result=false;
			}
		}
		if (classSuffixCheck.getSelection()){
			if (!ViewerUtil.checkSuffix(classSuffixText.getText())){
				classSuffixText.setBackground(red);
				result=false;
			}
		}
		if (conditionCheck.getSelection()){
			if (!ViewerUtil.checkStringItem(conditionText.getText())){
				conditionText.setBackground(red);
				result=false;
			}
		}
		if (methodSignatureCheck.getSelection()){
			if (!ViewerUtil.checkSignature(methodSignatureText.getText())){
				methodSignatureText.setBackground(red);
				result = false;
			}
		}
		if (maxDumpCountCheck.getSelection()){
			if (!ViewerUtil.isLargerThanMinValue(maxDumpCountText.getText(),0)){
				maxDumpCountText.setBackground(red);
				result=false;
			}
		}
		String className = classNameText.getText();
		isInterface = SettingManager.getRepository().isInterface(className);
		if (isInterface){
			classNameText.setBackground(red);
			String msg = classNameText.getText() + Message.get("Dialog.Task.Monitor.Method.Check.4.Text");
			ExcatMessageUtilty.showMessage(getShell(), msg);
			result=false;
		}
		return result;
	}

	private void createClassContentAssistant(){
		String[] classList = SettingManager.getRepository().getClassList();
		assistantForClass = new ContentAssistant();
		IContentAssistProcessor processor = new ContentAssistProcessor(
				classList);
		assistantForClass.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
		assistantForClass.install(classNameText.getTextViewer());

		classNameText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (HelperFunc.isLetterOrDot(e.character)) {
					assistantForClass.showPossibleCompletions();
				}
			}
		});
	}
	private void createMethodContentAssistant(){
		assistantForMethod = new ContentAssistant();
		methodProcessor = new ContentAssistProcessor(
				new String[]{});
		assistantForMethod.setContentAssistProcessor(methodProcessor, IDocument.DEFAULT_CONTENT_TYPE);
		assistantForMethod.install(methodNameText.getTextViewer());

		methodNameText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (HelperFunc.isLetterOrDot(e.character)) {
					assistantForMethod.showPossibleCompletions();
				}
			}
		});
	}
	private void createMethodSigContentAssistant(){
		assistantForMethodSig = new ContentAssistant();
		methodSigProcessor = new ContentAssistProcessor(
				new String[]{});
		assistantForMethodSig.setContentAssistProcessor(methodSigProcessor, IDocument.DEFAULT_CONTENT_TYPE);
		assistantForMethodSig.install(methodSignatureText.getTextViewer());

		methodSignatureText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (HelperFunc.isLetterOrDot(e.character)) {
					assistantForMethodSig.showPossibleCompletions();
				}
			}
		});
	}
	private void updateMode(){
  	  monitoringMethod.setClassName(classNameText.getText().trim());
	  monitoringMethod.setMethodName(methodNameText.getText().trim());
	  monitoringMethod.setMethodSignature(methodSignatureText.getText().trim());
	  monitoringMethod.setCondition(conditionText.getText().trim());
	  if ("".equals(maxDumpCountText.getText().trim())){
		  //無制限
		  monitoringMethod.setMaxDumpCount(0);
	  }else{
		  monitoringMethod.setMaxDumpCount(Integer.parseInt(maxDumpCountText.getText().trim()));
	  }
	  monitoringMethod.setClassLoaderURL(classLoaderUrlText.getText().trim());
	  monitoringMethod.setClassLoader_suffix(classSuffixCheck.getText().trim());
	  if (startPositionCheck.getSelection() && finishPositionCheck.getSelection()){
		  monitoringMethod.setPosition(MonitoringMethod.BOTH_POSITION);
	  }else if (startPositionCheck.getSelection()){
		  monitoringMethod.setPosition(MonitoringMethod.START_POSITION);
	  }else if (finishPositionCheck.getSelection()){
		  monitoringMethod.setPosition(MonitoringMethod.FINISH_POSITION);
	  }
	  //障害 #432 add
	  monitoringMethod.setClassLoader_suffix(this.classSuffixText.getText());
	}

	private boolean checkDuplicate(){
		for (MonitoringMethod m : methodMonitors){
			if (m.getClassName().equals(monitoringMethod.getClassName()) &&
					m.getMethodName().equals(monitoringMethod.getMethodName()) &&
					m.getMethodSignature().equals(monitoringMethod.getMethodSignature())){
				classNameText.setBackground(red);
				methodNameText.setBackground(red);
				if (methodSignatureText.getEnabled()) {
					methodSignatureText.setBackground(red);
				}

				String msg = monitoringMethod.getDisplayName()
				+ Message.get("Dialog.Task.Monitor.Same.Text");
				ExcatMessageUtilty.showMessage(thisDialog.getShell(), msg);
				return false;
			}
		}
		return true;
	}
}

