package jp.co.ipride.excat.configeditor.viewer.template.dialog;

import java.util.List;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.ClassTypeInfo;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MatchField;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.ExcatTextViewer;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.template.Template;
import jp.co.ipride.excat.configeditor.util.ProposalUtility;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.configeditor.viewer.contentassist.ContentAssistProcessor;
import jp.co.ipride.excat.configeditor.viewer.contentassist.WordTracker;
import jp.co.ipride.excat.configeditor.viewer.template.dialog.table.MemberTableViewer;

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
 * テンプレートを定義する画面
 * @author tu
 * @since 2007/11/13
 *
 */
public class TemplateRegisterDlg  extends Dialog{

	private static int text_width    = 500;
	private static final int MAX_QUEUE_SIZE = 200;

	private ExcatTextViewer classNameText;

	private MemberTableViewer memberTableViewer;

	private Button deleteButton;
	private Button addButton;

	private Button saveBtn;
	private Button cancelBtn;

	private Label classNameLabel;

	private Color red;
	private Color background;
	private Color white;

	private TemplateRegisterDlg thisDialog;

	private Template template;
	private Template orgTemplate;

	//補助入力
	private ContentAssistant assistantForClass;
	private WordTracker wordTracker = new WordTracker(MAX_QUEUE_SIZE);
	private ClassTypeInfo classTypeInfo = null;

	/**
	 * construct
	 * @param parentShell
	 */
	public TemplateRegisterDlg(Shell parentShell){
		super(parentShell);
		thisDialog=this;
	}

	public void init(Template template, Template orgTemplate){
		this.template = template;
		this.orgTemplate = orgTemplate;
	}

	public void updateDataToDisplay(){
		classNameText.setText(template.getClassName());
	}

	/**
	 * override method
	 * 1.create layout
	 */
	protected Control createContents(Composite parent) {
		this.getShell().setText(
				ApplicationResource.getResource("Dialog.Template.Title.Text"));
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

        //クラス名
        classNameLabel= new Label(composite,SWT.NONE);
        classNameLabel.setText(
        		ApplicationResource.getResource("Dialog.Template.ClassName.Text"));
        classNameText = new ExcatTextViewer(composite, text_width, 1);

        //メンバー名
		Group templateGroup = new Group(composite, SWT.NONE);
		templateGroup.setText(
				ApplicationResource.getResource("Dialog.Template.ClassMember.Text"));
		GridLayout templateGrouplayout = new GridLayout();
		templateGroup.setLayout(templateGrouplayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_FILL );
		gd.heightHint = 200;
		gd.widthHint = text_width;
		templateGroup.setLayoutData(gd);

		memberTableViewer = new MemberTableViewer(
								templateGroup,
								template.getMembers());

        addTableButton(templateGroup);

        //空白スペース
		Composite spaceComposite = new Composite(composite, SWT.NONE);
		GridData spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spaceGridData.heightHint=10;
        spaceComposite.setLayoutData(spaceGridData);

        //保存ボタンと保存しないボタン
        addDialogButtons(composite);

        createClassContentAssistant();

		addListeners();

		updateDataToDisplay();

		cancelBtn.setFocus();

		return composite;
	}

	private void addTableButton(Composite composite){
		Composite buttonForm = new Composite(composite,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonFormlayout.numColumns=2;
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonForm.setLayoutData(gd);

		addButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Button.Add.Text"),
				Utility.BUTTON_WIDTH,1);

		deleteButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Button.Delete.Text"),
				Utility.BUTTON_WIDTH,1);
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
		    	  //障害 #428 by tu
		    	  if (classNameText.hasFocus()){
		    		  classNameText.setBackground(white);
			    	  String className = classNameText.getText().trim();
					  template.setClassName(className);
			    	  classTypeInfo = SettingManager.getRepository().getClassTypeInfo(className);
			    	  memberTableViewer.removeAll();
			    	  if (classTypeInfo != null){
			    		  List<MatchField> fieldList = classTypeInfo.getFieldList();
			    		  for (int i=0; i<fieldList.size(); i++){
			    			  String field = fieldList.get(i).getFieldName();
			    			  memberTableViewer.addNewMember(field);
			    		  }
			    	  }
		    	  }
		      }
		    });

		//table button
		addButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				memberTableViewer.addNewMember();
			}
		});

		deleteButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				memberTableViewer.deleteMember();
			}
		});

		//dialog button
		cancelBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				thisDialog.cancelPressed();
			}
		});
		saveBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (checkItems()){
					thisDialog.okPressed();
				}
			}
		});
	}

	private boolean checkItems(){
		if (!ViewerUtil.checkClassName(classNameText.getText())){
			classNameText.setBackground(red);
			return false;
		}
		if (!memberTableViewer.checkItems()){
			return false;
		}
		if (!ConfigModel.hasSameClassForTemplateDef(template, orgTemplate)){
			classNameText.setBackground(red);
			ExcatMessageUtilty.showMessage(
					this.getShell(),
					Message.get("Dialog.Template.Class.Name.Text"));
			return false;
		}
		return true;
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

