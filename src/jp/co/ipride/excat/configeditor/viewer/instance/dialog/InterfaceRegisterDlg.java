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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * インターフェースから継承クラスを選択する。
 * @author tu-ipride
 * @version 3.0
 * @since 2009/9/28
 */
public class InterfaceRegisterDlg extends Dialog{

	private static int text_width    = 500;
	private static final int MAX_QUEUE_SIZE = 200;

	private ExcatTextViewer interfaceText;

	private Table classTable;

	private String className=null;

	private Button searchBtn;
	private Button saveBtn;
	private Button cancelBtn;

	private Color red;
	private Color background;
	private Color white;

	//補助入力
	private ContentAssistant assistantForInterface;
	private WordTracker wordTracker = new WordTracker(MAX_QUEUE_SIZE);

	/**
	 *
	 * @param parentShell
	 */
	public InterfaceRegisterDlg(Shell parentShell) {
		super(parentShell);
	}

	public String getClassName() {
		return className;
	}

	/**
	 * override method
	 * 1.create layout
	 */
	protected Control createContents(Composite parent) {
		this.getShell().setText(
				ApplicationResource.getResource("Dialog.InterfaceRegsiter.Title.Text"));
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

		addListeners();

		updateDataToDisplay();

		cancelBtn.setFocus();

		return composite;
	}

	private void addInterfaceForm(Composite composite){
        Group group = new Group(composite, SWT.NONE);
        group.setText(
        		ApplicationResource.getResource("Dialog.InterfaceRegsiter.Interface.Text"));
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		layout.numColumns=2;
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		Label interfaceLabel = new Label(group, SWT.NONE);
		//ViewerUtil.setControlWidth(interfaceLabel,72);
		interfaceLabel.setText(
				ApplicationResource.getResource("Dialog.InterfaceRegsiter.Interface.InterfaceName.Text"));
		interfaceText = new ExcatTextViewer(group, text_width, 1);
	}

	private void addClassTableArea(Composite composite){
        Group group = new Group(composite, SWT.NONE);
        group.setText(
        		ApplicationResource.getResource("Dialog.InterfaceRegsiter.ClassTabel.Text")
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
				ApplicationResource.getResource("Dialog.InterfaceRegsiter.Interface.Search"),
				Utility.BUTTON_WIDTH,1);

	}
	private void addSpaceLine(Composite composite){
		Composite comp = new Composite(composite, SWT.NONE);
        GridData spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spaceGridData.heightHint = ViewerUtil.getMarginHeight(composite);
        comp.setLayoutData(spaceGridData);
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
		searchBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				classTable.removeAll();
				className = null;
				String[] classNameList = SettingManager.getRepository().
											getAllImplementClassForThisInterface(
												interfaceText.getText());
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
					className = selectItem.getText();
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
	}
	private boolean checkItems(){
		if (className != null){
			return true;
		}else{
			classTable.setBackground(red);
			return false;
		}
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

}
