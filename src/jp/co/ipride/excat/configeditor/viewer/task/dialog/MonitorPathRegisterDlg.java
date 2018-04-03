package jp.co.ipride.excat.configeditor.viewer.task.dialog;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.HelperFunc;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.ExcatTextViewer;
import jp.co.ipride.excat.configeditor.model.task.IPlace;
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

/**
 * 以下の機能がある。
 * １．監視例外のルートの設定
 * ２．除外例外のルートの設定
 *
 * 画面のタイトル等の文言は設定時に切り替え
 *
 * @author tu
 * @since 2007/11/14
 * @date 2009/9/24 modify for v3
 *
 */
public class MonitorPathRegisterDlg  extends Dialog {

	private static int text_width    = 500;
	private static final int MAX_QUEUE_SIZE = 200;

	private ExcatTextViewer classNameText;
	private ExcatTextViewer methodNameText;
	private ExcatTextViewer methodSignatureText;

	private Button methodNameCheck;
	private Button methodSignatureCheck;
	private Button saveBtn;
	private Button cancelBtn;

	private Label classNameLabel;
	private Label methodNameLabel;

	private Color red;
	private Color white;
	private Color background;

	private IPlace place=null;
	private IPlace editPlace = null;
	private MonitorPathRegisterDlg thisDialog;

	protected String title =null;
	protected String groupName = null;

	//補助入力
	private ContentAssistant assistantForClass;
	private ContentAssistant assistantForMethod;
	private ContentAssistProcessor methodProcessor;
	private ContentAssistant assistantForMethodSig;
	private ContentAssistProcessor methodSigProcessor;

	private WordTracker wordTracker = new WordTracker(MAX_QUEUE_SIZE);
	/**
	 * コンストラクタ
	 * @param parentShell
	 */
	public MonitorPathRegisterDlg(Dialog parent) {
		super(parent.getShell());
		thisDialog=this;
	}

	/**
	 * 既存編集
	 * @param place
	 */
	public void init(IPlace place){
		this.place = place;
	}

	private void updateDataToDisplay(){
		classNameText.setText(place.getClassName());
		methodNameText.setText(place.getMethodName());
		methodSignatureText.setText(place.getMethodSignature());

		if ("".equals(methodNameText.getText())){
			methodNameCheck.setSelection(false);
			methodNameText.setEditable(false);
			methodNameText.setBackground(background);
		}else{
			methodNameCheck.setSelection(true);
			methodNameText.setEditable(true);
			methodNameText.setBackground(white);
		}
		if ("".equals(methodSignatureText.getText())){
			methodSignatureCheck.setSelection(false);
			methodSignatureText.setEditable(false);
			methodSignatureText.setBackground(background);
		}else{
			methodSignatureCheck.setSelection(true);
			methodSignatureText.setEditable(true);
			methodSignatureText.setBackground(white);
		}
	}

	/**
	 * override method
	 * 1.create layout
	 */
	protected Control createContents(Composite parent) {
		this.getShell().setText(title);
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

        //監視するメソッド定義のエリア
        Group methodGroup = new Group(composite, SWT.NONE);
        methodGroup.setText(groupName);
		GridLayout methodGrouplayout = new GridLayout();
		methodGrouplayout.numColumns=3;
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		methodGroup.setLayout(methodGrouplayout);

		classNameLabel = new Label(methodGroup, SWT.NONE);
		classNameLabel.setText(
				ApplicationResource.getResource("Dialog.ExcludeException.Path.Class.Text"));
		classNameText = new ExcatTextViewer(methodGroup, text_width, 2);

		methodNameLabel = new Label(methodGroup, SWT.NONE);
		methodNameLabel. setText(
				ApplicationResource.getResource("Dialog.ExcludeException.Path.Method.Text"));
		methodNameCheck = new Button(methodGroup, SWT.CHECK);
		methodNameText = new ExcatTextViewer(methodGroup, text_width, 1);

		Label sigLabel = new Label(methodGroup, SWT.NONE);
		sigLabel.setText(
				ApplicationResource.getResource("Dialog.ExcludeException.Path.Signature.Text"));
		methodSignatureCheck = new Button(methodGroup, SWT.CHECK);
		methodSignatureText = new ExcatTextViewer(methodGroup, text_width, 1);

        //空白スペース
		Composite spaceComposite = new Composite(composite, SWT.NONE);
		GridData spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
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

		createClassContentAssistant();
		createMethodContentAssistant();
		createMethodSigContentAssistant();

		addListeners();

		updateDataToDisplay();

		cancelBtn.setFocus();

		return composite;
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
		methodNameCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (methodNameCheck.getSelection()){
					methodNameText.setEditable(true);
					methodNameText.setBackground(white);
				}else{
					methodNameText.setText("");
					methodNameText.setEditable(false);
					methodNameText.setBackground(background);
				}
			}
		});
		methodSignatureCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (methodSignatureCheck.getSelection()){
					methodSignatureText.setEditable(true);
					methodSignatureText.setBackground(white);
				}else{
					methodSignatureText.setText("");
					methodSignatureText.setEditable(false);
					methodSignatureText.setBackground(background);
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
				if (checkItems()){
			    	  updateMode();
			    	  thisDialog.okPressed();
				}
			}
		});
	}

	private boolean checkItems(){
		boolean result = true;
		if (!ViewerUtil.checkClassName(classNameText.getText())){
			classNameText.setBackground(red);
			result= false;
		}
		if (methodNameCheck.getSelection()){
			if (!ViewerUtil.checkMethodName(methodNameText.getText())){
				methodNameText.setBackground(red);
				result=false;
			}
		}
		if (methodSignatureCheck.getSelection()){
			if (!ViewerUtil.checkSignature(methodSignatureText.getText())){
				methodSignatureText.setBackground(red);
				result = false;
			}
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
		place.setClassName(classNameText.getText());
		place.setMethodName(methodNameText.getText());
		place.setMethodSignature(methodSignatureText.getText());
	}

}
