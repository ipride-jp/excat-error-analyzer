package jp.co.ipride.excat.analyzer.dialog.search;

import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.viewer.fileviewer.DumpFileViewerFilter;
import jp.co.ipride.excat.analyzer.viewer.fileviewer.FileTreeContentProvider;
import jp.co.ipride.excat.analyzer.viewer.fileviewer.FileTreeFileItem;
import jp.co.ipride.excat.analyzer.viewer.fileviewer.FileTreeItemComparer;
import jp.co.ipride.excat.analyzer.viewer.fileviewer.FileTreeLabelProvider;
import jp.co.ipride.excat.analyzer.viewer.fileviewer.FileTreeTopItem;
import jp.co.ipride.excat.analyzer.viewer.searchviewer.ConditionUnit;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.HelperFunc;
import jp.co.ipride.excat.configeditor.ExcatTextViewer;
import jp.co.ipride.excat.configeditor.util.ProposalUtility;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.configeditor.viewer.contentassist.ContentAssistProcessor;
import jp.co.ipride.excat.configeditor.viewer.contentassist.WordTracker;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.dialogs.ContainerCheckedTreeViewer;

/**
 * ダンプ・ファイルの検索
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/9
 */
public class FileViewSearchTab extends Composite {

	private static final int MAX_QUEUE_SIZE = 200;
	private static final int UI_TEXT_WIDTH = 500;

	private MainViewer appWindow;

	private Button classTypeSelect;
	private Button varNameSelect;
	private Button valueSelect;
	private ExcatTextViewer typeNameText;
	private ExcatTextViewer varNameText;
	private ExcatTextViewer valueText;
	private Combo depthCombo;

	private ContainerCheckedTreeViewer treeViewer;

	private Color red;
	private Color background;
	private Color white;

	private TextSearchDialog dialog;


	//補助入力
	private ContentAssistant assistantForClass;
	private WordTracker wordTracker = new WordTracker(MAX_QUEUE_SIZE);

	/**
	 * コンストラクタ
	 *
	 * @param parent
	 * @param style
	 */
	public FileViewSearchTab(MainViewer appWindow, TextSearchDialog dialog, Composite parent, int style) {
		super(parent, style);
		this.appWindow = appWindow;
		this.dialog = dialog;
		red = this.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		white = this.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		background = this.getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		createContents();
		init();
	}

	private void init(){
		classTypeSelect.setSelection(false);
		varNameSelect.setSelection(false);
		valueSelect.setSelection(true);
		typeNameText.setBackground(background);
		typeNameText.setEnabled(false);
		varNameText.setBackground(background);
		varNameText.setEnabled(false);
		valueText.setBackground(white);
		valueText.setEnabled(true);
		valueText.getTextViewer().getTextWidget().setFocus();

		//add by Qiu Song on 20091113 for バグ#506
		depthCombo.addVerifyListener(
				new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						e.doit=ViewerUtil.verifyNumbers(e.text);
					}
				});
		//end of add by Qiu Song on 20091113 for バグ#506
	}

	public void updateFocus(){
		if (classTypeSelect.getSelection()) {
			typeNameText.getTextViewer().getTextWidget().setFocus();
		} else if (varNameSelect.getSelection()) {
			varNameText.getTextViewer().getTextWidget().setFocus();
		} else if (valueSelect.getSelection()) {
			valueText.getTextViewer().getTextWidget().setFocus();
		}
	}

	/**
	 * ダイアログのコンテンツを生成します。
	 */
	protected void createContents() {
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = ViewerUtil.getMarginHeight(this);
		layout.marginBottom = ViewerUtil.getMarginHeight(this);
		layout.marginLeft = ViewerUtil.getMarginWidth(this);
		layout.marginRight = ViewerUtil.getMarginWidth(this);
		this.setLayout(layout);

		// set dialog area.
        Group group = new Group(this,SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginTop = ViewerUtil.getMarginHeight(this);
		layout.marginBottom = ViewerUtil.getMarginHeight(this);
		layout.marginLeft = ViewerUtil.getMarginWidth(this);
		layout.marginRight = ViewerUtil.getMarginWidth(this);
		group.setLayout(layout);

		// 変数のタイプ
		classTypeSelect = new Button(group, SWT.CHECK);
		classTypeSelect.setText(
				ApplicationResource.getResource("Search.Dialog.ClassName"));
		classTypeSelect.setSelection(true);
		classTypeSelect.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				if (classTypeSelect.getSelection()){
					typeNameText.setBackground(white);
					typeNameText.setEnabled(true);
				}else{
					typeNameText.setText("");
					typeNameText.setBackground(background);
					typeNameText.setEnabled(false);
				}
			}
		});
		typeNameText = new ExcatTextViewer(group,UI_TEXT_WIDTH,1);
		typeNameText.addKeyListener(new KeyAdapter() {
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

		typeNameText.addTextListener(new ITextListener() {
		      public void textChanged(TextEvent e) {
		    	  String word = e.getText();
		    	  if (ProposalUtility.isWhitespaceString(word)) {
		    		  wordTracker.add(
		    				  ProposalUtility.findMostRecentWord(
		    						  typeNameText, e.getOffset() - 1));
		    	  }
		    	  dialog.checkRunStatus();
		      }
		    });
		typeNameText.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent e) {
				createClassContentAssistant();
			}
			public void focusLost(FocusEvent e) {
			}
		});

		//変数名
		varNameSelect = new Button(group, SWT.CHECK);
		varNameSelect.setText(
				ApplicationResource.getResource("Search.Dialog.VarName"));
		varNameSelect.setSelection(true);
		varNameText = new ExcatTextViewer(group,UI_TEXT_WIDTH,1);
		varNameSelect.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				if (varNameSelect.getSelection()){
					varNameText.setBackground(white);
					varNameText.setEnabled(true);
				}else{
					varNameText.setText("");
					varNameText.setBackground(background);
					varNameText.setEnabled(false);
				}
			}
		});
		varNameText.addTextListener(new ITextListener() {
		      public void textChanged(TextEvent e) {
		    	  dialog.checkRunStatus();
		      }
		    });

		// 変数値
		valueSelect = new Button(group, SWT.CHECK);
		valueSelect.setText(
				ApplicationResource.getResource("Search.Dialog.Value"));
		valueSelect.setSelection(true);
		valueText = new ExcatTextViewer(group, UI_TEXT_WIDTH,1);
		valueSelect.addSelectionListener(new SelectionListener(){
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
			public void widgetSelected(SelectionEvent arg0) {
				if (valueSelect.getSelection()){
					valueText.setBackground(white);
					valueText.setEnabled(true);
				}else{
					valueText.setText("");
					valueText.setBackground(background);
					valueText.setEnabled(false);
				}
			}
		});
		valueText.addTextListener(new ITextListener() {
		      public void textChanged(TextEvent e) {
		    	  dialog.checkRunStatus();
		      }
		    });

		Composite composite = new Composite(this, SWT.NONE);
        layout = new GridLayout(2,false);
		layout.marginTop = ViewerUtil.getMarginHeight(this);
		layout.marginBottom = ViewerUtil.getMarginHeight(this);
		layout.marginLeft = ViewerUtil.getMarginWidth(this);
		layout.marginRight = ViewerUtil.getMarginWidth(this);
		composite.setLayout(layout);

		//add comment.
		createComment(composite);

		//depth area
		createDepthArea(composite);

		//ファイル・ツリー
		createTable(this);
	}

	private void createComment(Composite parent){
		Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(this);
		layout.marginBottom = ViewerUtil.getMarginHeight(this);
		layout.marginLeft = ViewerUtil.getMarginWidth(this);
		layout.marginRight = ViewerUtil.getMarginWidth(this);
		composite.setLayout(layout);
		GridData gd = new GridData();
		gd.widthHint=400;
		composite.setLayoutData(gd);

		Label label1 = new Label(composite, SWT.NONE);
		label1.setText(
				ApplicationResource.getResource("SearchView.Tab.Comment1"));
	}

	private void createDepthArea(Composite parent){
        Group depthgroup = new Group(parent,SWT.NONE);
        GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(this);
		layout.marginBottom = ViewerUtil.getMarginHeight(this);
		layout.marginLeft = ViewerUtil.getMarginWidth(this);
		layout.marginRight = ViewerUtil.getMarginWidth(this);
		depthgroup.setLayout(layout);
		depthgroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_END));
		depthgroup.setText(
				ApplicationResource.getResource("Search.Dialog.Depth"));

		depthCombo = new Combo(depthgroup,SWT.DROP_DOWN);
		depthCombo.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		depthCombo.setLayoutData(gd);
		for (int i=0; i<ConditionUnit.DEPTH.length; i++){
			depthCombo.add(ConditionUnit.DEPTH[i]);
		}
		depthCombo.select(3);
		// リスナ追加
		depthCombo.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent arg0) {
				dialog.checkRunStatus();

			  }
		    });
	}

	/**
	 * テーブルを作成
	 * @param composite
	 */
	private void createTable(Composite composite) {

        Group tableGroup = new Group(composite, SWT.NONE);
        GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		tableGroup.setLayout(layout);
		tableGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		tableGroup.setText(
				ApplicationResource.getResource("Search.Dialog.Area"));

		GridData gridData = new GridData(GridData.FILL_BOTH);
		gridData.heightHint = 210;

		// 作業 #504
		treeViewer = new ContainerCheckedTreeViewer(tableGroup, SWT.BORDER);
		treeViewer.getControl().setLayoutData(gridData);
		treeViewer.setContentProvider(new FileTreeContentProvider());
		treeViewer.setLabelProvider(new FileTreeLabelProvider());
		treeViewer.setComparer(new FileTreeItemComparer());
		treeViewer.addFilter(new DumpFileViewerFilter());

		//modofied by Qiu Song on 20091126 for バグ:527
		/*treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
		@Override
		public void selectionChanged(
				SelectionChangedEvent selectionchangedevent) {
			System.out.println("addSelectionChangedListener");
			dialog.checkRunStatus();
		}
	    });*/
		treeViewer.addCheckStateListener(new ICheckStateListener() {
		      public void checkStateChanged(CheckStateChangedEvent event) {
		    	// If the item is checked . . .
		        if (event.getChecked()) {
		          // . . . check all its children
		        	treeViewer.setSubtreeChecked(event.getElement(), true);
		        }else{
		        	treeViewer.setSubtreeChecked(event.getElement(), false);
		        }
		        dialog.checkRunStatus();
		      }
		    });
		//end modofied by Qiu Song on 20091126 for バグ:527

		// ルートを設定
		treeViewer.setInput(new FileTreeTopItem());
	}

	public boolean checkRunStatus(){
		String value = valueText.getText().trim();
		String varName = varNameText.getText().trim();
		String typeName = typeNameText.getText().trim();
		String depth = depthCombo.getText().trim();
		if (!checkFileTree()){
			return false;
		}
		if ((!"".equals(value) || !varName.equals("") || !typeName.equals("")) && !depth.equals("")){
			return true;
		}else{
			return false;
		}
	}

	private boolean checkFileTree(){
		return getSelectFileList().size() > 0;
	}

	public void updateToMode(ConditionUnit textSearchUnit){
		textSearchUnit.setTypeName(typeNameText.getText().trim());
		textSearchUnit.setVarName(varNameText.getText().trim());
		textSearchUnit.setValue(valueText.getText().trim());
		textSearchUnit.setSearchType(ConditionUnit.SEARCH_FILE);
		textSearchUnit.setDepth(depthCombo.getText());
		textSearchUnit.setFileList(getSelectFileList());
	}

	public List<String> getSelectFileList(){
		List<String> result = new ArrayList<String>();
		Object[] elements = treeViewer.getCheckedElements();

		for (int i = 0; i < elements.length; i++) {
			Object object = elements[i];
			if (object instanceof FileTreeFileItem) {
				FileTreeFileItem item = (FileTreeFileItem)object;
				if (item.getFile().isFile() && HelperFunc.isDumpFile(item.getFile())) {
					result.add(item.getFilePath());
				}
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
		assistantForClass.install(typeNameText.getTextViewer());

		typeNameText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				if (HelperFunc.isLetterOrDot(e.character)) {
					assistantForClass.showPossibleCompletions();
				}
			}
		});
	}

	public void updateUI(ConditionUnit conditionUnit) {
		typeNameText.setText(conditionUnit.getTypeName());
		boolean isFocusSetted = false;
		if (typeNameText.getText().length() != 0) {
			typeNameText.setBackground(white);
			typeNameText.setEnabled(true);
			classTypeSelect.setSelection(true);
			if (!isFocusSetted) {
				typeNameText.getTextViewer().getTextWidget().setFocus();
				isFocusSetted = true;
			}
		} else {
			typeNameText.setBackground(background);
			typeNameText.setEnabled(false);
			classTypeSelect.setSelection(false);
		}
		varNameText.setText(conditionUnit.getVarName());
		if (varNameText.getText().length() != 0) {
			varNameText.setBackground(white);
			varNameText.setEnabled(true);
			varNameSelect.setSelection(true);
			if (!isFocusSetted) {
				varNameText.getTextViewer().getTextWidget().setFocus();
				isFocusSetted = true;
			}
		} else {
			varNameText.setBackground(background);
			varNameText.setEnabled(false);
			varNameSelect.setSelection(false);
		}
		valueText.setText(conditionUnit.getValue());
		if (valueText.getText().length() != 0) {
			valueText.setBackground(white);
			valueText.setEnabled(true);
			valueSelect.setSelection(true);
			if (!isFocusSetted) {
				valueText.getTextViewer().getTextWidget().setFocus();
				isFocusSetted = true;
			}
		} else {
			valueText.setBackground(background);
			valueText.setEnabled(false);
			valueSelect.setSelection(false);
			if (!isFocusSetted) {
				valueText.getTextViewer().getTextWidget().setFocus();
				isFocusSetted = true;
			}
		}
		depthCombo.setText(String.valueOf(conditionUnit.getDepth()));
	}
}
