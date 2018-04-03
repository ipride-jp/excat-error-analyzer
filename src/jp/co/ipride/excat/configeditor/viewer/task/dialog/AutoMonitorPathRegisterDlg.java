package jp.co.ipride.excat.configeditor.viewer.task.dialog;

import java.util.Vector;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.ExcatTextViewer;
import jp.co.ipride.excat.configeditor.model.task.Place;
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
 * タスク：例外自動監視
 * 機能：監視するパッケージの登録画面
 *
 * @author tu-ipride
 * @date 2009/9/8
 */
public class AutoMonitorPathRegisterDlg extends Dialog {

	private static int text_width    = 500;
	private static final int MAX_QUEUE_SIZE = 200;

	private ExcatTextViewer packageNameText;

	private Button saveBtn;
	private Button cancelBtn;
	private AutoMonitorPathRegisterDlg thisDialog;

	private Color red;
	private Color white;

	private Place place;

	private Vector<Place> packageList;

	private ContentAssistant assistant;
	private WordTracker wordTracker = new WordTracker(MAX_QUEUE_SIZE);

	private String packageName = "";
	/**
	 * コンストラクタ
	 * @param parentShell
	 */
	public AutoMonitorPathRegisterDlg(Shell parent) {
		super(parent);
		thisDialog = this;
	}

	public void init(Place place, Vector<Place> packageList){
		this.place = place;
		this.packageList = packageList;
	}

	/**
	 * override method
	 * 1.create layout
	 */
	protected Control createContents(Composite parent) {
		this.getShell().setText(
        		ApplicationResource.getResource("Dialog.AutoMonitoring.Monitor.Title")
				);

		red = this.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		white = this.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);

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

        //監視するパッケージ定義のエリア
        Group packageGroup = new Group(composite, SWT.NONE);
        packageGroup.setText(
        		ApplicationResource.getResource("Dialog.AutoMonitoring.Monitor")
        		);
		GridLayout methodGrouplayout = new GridLayout();
		methodGrouplayout.numColumns=3;
		methodGrouplayout.marginTop=10;
		methodGrouplayout.marginBottom=10;
		packageGroup.setLayout(methodGrouplayout);

		Label packageLabel = new Label(packageGroup, SWT.NONE);
		packageLabel.setText(
				ApplicationResource.getResource("Dialog.AutoMonitoring.Monitor.Name")
				);

        packageNameText = new ExcatTextViewer(packageGroup, text_width, 1);

        //空白スペース
		Composite spaceComposite = new Composite(composite, SWT.NONE);
		GridData spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spaceGridData.heightHint=ViewerUtil.getMarginHeight(composite);
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

		updateDataToDispaly();

		cancelBtn.setFocus();

		return composite;
	}

	private void addListeners(){
		packageNameText.addKeyListener(new KeyAdapter() {
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
		packageNameText.addTextListener(new ITextListener() {
		      public void textChanged(TextEvent e) {
		    	  packageNameText.setBackground(white);
		    	  String word = e.getText();
		    	  if (ProposalUtility.isWhitespaceString(word)) {
		    		  wordTracker.add(
		    				  ProposalUtility.findMostRecentWord(
		    						  packageNameText, e.getOffset() - 1));
		    	  }
		    	  updateMode();
				  packageNameText.setBackground(white);
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
				if ((!packageNameText.getText().equals(packageName)) && !checkDuplicate()){
					return;
				}
				thisDialog.okPressed();
			}
		});
	}

	private void createContentAssistant(){
		String[] packageList = SettingManager.getRepository().getPackageList();
		assistant = new ContentAssistant();
		IContentAssistProcessor processor = new ContentAssistProcessor(
				packageList);
		assistant.setContentAssistProcessor(processor, IDocument.DEFAULT_CONTENT_TYPE);
		assistant.install(packageNameText.getTextViewer());

		packageNameText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (HelperFunc.isLetterOrDot(e.character)) {
					assistant.showPossibleCompletions();
				}
			}
		});
	}

	/**
	 * 項目のチェック
	 */
	private boolean checkItems(){
		if (!ViewerUtil.checkClassName(packageNameText.getText())){
			packageNameText.setBackground(red);
			return false;
		}
		return true;
	}

	private void updateDataToDispaly(){
		packageName = place.getClassName();
		packageNameText.setText(place.getClassName());
	}

	private void updateMode(){
		place.setClassName(packageNameText.getText());
	}

	private boolean checkDuplicate(){
		for (Place p : packageList){
			if (p.getClassName().equals(place.getClassName())){
				packageNameText.setBackground(red);
				String msg = place.getClassName()
				+ Message.get("Dialog.Task.Monitor.Same.Text");
				ExcatMessageUtilty.showMessage(thisDialog.getShell(), msg);
				return false;
			}
		}
		return true;
	}

}
