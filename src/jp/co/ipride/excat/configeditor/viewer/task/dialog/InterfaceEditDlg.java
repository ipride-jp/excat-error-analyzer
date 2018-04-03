package jp.co.ipride.excat.configeditor.viewer.task.dialog;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.HelperFunc;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.ExcatTextViewer;
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
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * メソッド監視時にインターフェースを指定する編集画面
 * @author tu-ipride
 * @date 2009/9/13
 */
public class InterfaceEditDlg extends Dialog{

	private static int text_width    = 500;
	private static final int MAX_QUEUE_SIZE = 200;

	private ExcatTextViewer interfaceText;
	private ExcatTextViewer methodNameText;
	private ExcatTextViewer methodSignatureText;
	private Table classTable;

	private String selectClassName = "";   //select class name.
	private String methodName = "";
	private String methodSig = "";

	private String interfaceName = "";

	private Color red;
	private Color background;
	private Color white;

	private Button searchBtn;
	private Button saveBtn;
	private Button cancelBtn;

	//補助入力
	private ContentAssistant assistantForInterface;
	private ContentAssistant assistantForMethod;
	private ContentAssistProcessor methodProcessor;
	private ContentAssistant assistantForMethodSig;
	private ContentAssistProcessor methodSigProcessor;

	private WordTracker wordTracker = new WordTracker(MAX_QUEUE_SIZE);


	protected InterfaceEditDlg(MethodRegisterDlg parent) {
		super(parent.getShell());
	}

	public String getClassName(){
		return selectClassName;
	}

	public String getMethodName(){
		return methodName;
	}

	public String getSignature(){
		return methodSig;
	}

	public void setInterfaceName(String interfaceName){
		this.interfaceName=interfaceName;
	}

	public void setMethodName(String methodName){
		this.methodName = methodName;
	}

	public void setMethodSig(String methodSig){
		this.methodSig = methodSig;
	}

	/**
	 * override method
	 * 1.create layout
	 */
	protected Control createContents(Composite parent) {
		this.getShell().setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.Interface.Monitor.Text"));
		red = this.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		white = this.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		background = this.getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

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

        //監視するインターフェース定義のエリア
        addInterfaceForm(composite);
        addSpaceLine(composite);

        //候補クラス・テーブル
        addClassTableArea(composite);
        addSpaceLine(composite);

		//保存ボタン＆保存しないボタン
        addButtonForm(composite);

		createInterfaceContentAssistant();
		createMethodContentAssistant();
		createMethodSigContentAssistant();

		addListeners();

		updateDataToDisplay();

		cancelBtn.setFocus();

		return composite;
	}

	private void addSpaceLine(Composite composite){
		Composite comp = new Composite(composite, SWT.NONE);
        GridData spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spaceGridData.heightHint = ViewerUtil.getMarginHeight(composite);
        comp.setLayoutData(spaceGridData);
	}

	private void addInterfaceForm(Composite composite){
        Group group = new Group(composite, SWT.NONE);
        group.setText(
        		ApplicationResource.getResource("Dialog.RegisterMethod.Interface.InterfaceName.Text"));
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		layout.numColumns=2;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		Label interfaceLabel = new Label(group, SWT.NONE);
		//ViewerUtil.setControlWidth(interfaceLabel,90);
		interfaceLabel.setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.Interface.InterfaceName.Text"));
		interfaceText = new ExcatTextViewer(group, text_width, 1);

        Label methodNameLabel = new Label(group, SWT.NONE);
		methodNameLabel.setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.Interface.MethodName.Text"));
		methodNameText = new ExcatTextViewer(group, text_width, 1);

        Label sigNameLabel = new Label(group, SWT.NONE);
        sigNameLabel.setText(
				ApplicationResource.getResource("Dialog.RegisterMethod.Interface.SigName.Text"));
        methodSignatureText = new ExcatTextViewer(group, text_width, 1);
	}

	private void addClassTableArea(Composite composite){
        Group group = new Group(composite, SWT.NONE);
        group.setText(
        		ApplicationResource.getResource("Dialog.RegisterMethod.Interface.ClassTabel.Text")
        	);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
        GridData gridData = new GridData(GridData.FILL_BOTH );
		group.setLayout(layout);
		group.setLayoutData(gridData);

		classTable = new Table(group, SWT.BORDER | SWT.CHECK);
		classTable.setLayoutData(gridData);

		searchBtn = Utility.createButton(group, SWT.PUSH,
				ApplicationResource.getResource("Dialog.RegisterMethod.Interface.Search"),
				Utility.BUTTON_WIDTH,1);

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

	private void addListeners(){
		interfaceText.addKeyListener(new KeyAdapter() {
		      public void keyPressed(KeyEvent e) {
		        switch (e.keyCode) {
		        case SWT.F1:
		          assistantForInterface.showPossibleCompletions();
		          break;
		        default:
		        //ignore everything else
		        }
		      }
		    });

		interfaceText.addTextListener(new ITextListener() {
		      public void textChanged(TextEvent e) {
		    	  String word = e.getText();
		    	  if (ProposalUtility.isWhitespaceString(word)) {
		    		  wordTracker.add(
		    				  ProposalUtility.findMostRecentWord(
		    						  interfaceText, e.getOffset() - 1));
		    	  }
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
		    	  methodName = methodNameText.getText();
		    	  methodNameText.setBackground(white);
		      }
		    });
		methodNameText.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				String className = interfaceText.getText().trim();
				if (!"".equals(className)){
					String[] classList = SettingManager.getRepository().
												getInterfaceMethodList(className);
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
		    	  methodSig = methodSignatureText.getText();
		    	  methodSignatureText.setBackground(white);
		      }
		    });
		methodSignatureText.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				String className = interfaceText.getText().trim();
				String methodName = methodNameText.getText().trim();
				if ( !"".equals(className) && !"".equals(methodName)){
					String[] methodSigList = SettingManager.getRepository().
										getInterfaceMethosSigList(className, methodName);
					methodSigProcessor.setCandidates(methodSigList);
				}
			}
			public void focusLost(FocusEvent e) {
			}
		});
		searchBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				classTable.removeAll();
				selectClassName = null;
				String[] classNameList = SettingManager.getRepository().
											getAllImplementClassForThisInterfaceMethod(
												interfaceText.getText(),
												methodNameText.getText(),
												methodSignatureText.getText());
				if (classNameList != null ){
					for (int i=0; i<classNameList.length; i++){
						TableItem item = new TableItem(classTable, SWT.RADIO);
						item.setText(classNameList[i]);
					}
				}
			}
		});
		cancelBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				cancelPressed();
			}
		});
		saveBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (checkItems()){
					okPressed();
				}
			}
		});
		classTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem selectItem = (TableItem)e.item;
				if (selectItem.getChecked()){
					selectClassName = selectItem.getText();
					classTable.setBackground(white);
					for (int i=0; i<classTable.getItemCount(); i++){
						TableItem item = classTable.getItem(i);
						if (!selectItem.equals(item)){
							item.setChecked(false);
						}
					}
				}
			}
		});

	}

	private void updateDataToDisplay(){
		interfaceText.setText(this.interfaceName);
		methodNameText.setText(this.methodName);
		methodSignatureText.setText(this.methodSig);
	}

	private boolean checkItems(){
//		if (selectClassName != null && !selectClassName.isEmpty()){
//			return true;
//		}else{
//			classTable.setBackground(red);
//			return false;
//		}

		boolean flg = false;
		if (methodName == null || methodName.length() == 0) {
			methodNameText.setBackground(red);
		}
		if (methodSig == null || methodSig.length() == 0) {
			methodSignatureText.setBackground(red);
		}
		if (selectClassName == null || selectClassName.length() == 0) {
			classTable.setBackground(red);
		}
		if ((methodName != null && methodName.length() != 0)
				&& (methodSig != null && methodSig.length() != 0)
				&& (selectClassName != null && selectClassName.length() != 0)) {
			flg = true;
		}

		return flg;
	}

	private void createInterfaceContentAssistant(){
		String[] classList = SettingManager.getRepository().getInterfaceList();
		assistantForInterface = new ContentAssistant();
		IContentAssistProcessor processor = new ContentAssistProcessor(
				classList);
		assistantForInterface.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
		assistantForInterface.install(interfaceText.getTextViewer());

		interfaceText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (HelperFunc.isLetterOrDot(e.character)) {
					assistantForInterface.showPossibleCompletions();
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

}
