package jp.co.ipride.excat.configeditor.viewer.instance.dialog;

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
import org.eclipse.swt.widgets.Shell;

/**
 * ダンプするオブジェクトのクラス名の登録
 * @author tu-ipride
 * @version 3.0
 * @since 2009/9/28
 */
public class ObjectRegisterDlg extends Dialog{

	private static int text_width    = 500;
	private static final int MAX_QUEUE_SIZE = 200;

	private ExcatTextViewer classNameText;
	private String className=null;

	private Color red;
	private Color background;
	private Color white;

	private Button saveBtn;
	private Button cancelBtn;

	private Button interfaceBtn;

	private Dialog thisDialog;

	//補助入力
	private ContentAssistant assistantForClass;
	private WordTracker wordTracker = new WordTracker(MAX_QUEUE_SIZE);

	public ObjectRegisterDlg(Shell parentShell) {
		super(parentShell);
		thisDialog = this;
	}

	public void init(String className){
		this.className=className;
	}


	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public void updateDataToDisplay(){
		classNameText.setText(className);
	}
	/**
	 * override method
	 * 1.create layout
	 */
	protected Control createContents(Composite parent) {
		this.getShell().setText(
				ApplicationResource.getResource("Dialog.Instance.Title.Text"));
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

        Group classGroup = new Group(composite, SWT.NONE);
		layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		layout.numColumns=2;
		classGroup.setLayout(layout);
		classGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

        //クラス名
        Label classNameLabel= new Label(classGroup,SWT.NONE);
		ViewerUtil.setControlWidth(classNameLabel,72);
        classNameLabel.setText(
        		ApplicationResource.getResource("Dialog.Instance.ClassName.Text"));
        classNameText = new ExcatTextViewer(classGroup, text_width, 1);

		//add select button for interface
		interfaceBtn = Utility.createButton(classGroup, SWT.PUSH,
				ApplicationResource.getResource("Dialog.RegisterMethod.Interface.Text"),
				Utility.BUTTON_WIDTH,2);

        //保存ボタンと保存しないボタン
        addDialogButtons(composite);

        createClassContentAssistant();

		addListeners();

		updateDataToDisplay();

		cancelBtn.setFocus();

		return composite;
	}

	private void addDialogButtons(Composite composite){
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
		    	  className = classNameText.getText().trim();
		    	  classNameText.setBackground(white);
			}
		});
		interfaceBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				InterfaceRegisterDlg dialog = new InterfaceRegisterDlg(thisDialog.getShell());
				int r= dialog.open();
				if (r==Dialog.OK){
					classNameText.setText(dialog.getClassName());
				}
			}
		});
		//dialog button
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

	}

	private boolean checkItems(){
		if (!ViewerUtil.checkClassName(classNameText.getText())){
			classNameText.setBackground(red);
			return false;
		}else{
			return true;
		}
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
}
