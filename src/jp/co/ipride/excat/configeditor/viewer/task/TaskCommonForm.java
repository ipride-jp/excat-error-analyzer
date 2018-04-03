package jp.co.ipride.excat.configeditor.viewer.task;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.configeditor.ExcatText;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.task.ITask;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;

/**
 * 各タスクの共通部品を実装する。
 * @author tu-ipride
 *
 */
public class TaskCommonForm {

	protected static int Tabel_High = 150;

	protected SashForm parentForm;
	protected ITaskForm taskForm;
	protected Composite parent;

	protected ITask task;
	protected ExcatText taskName;

	protected Button effectSelect;
	protected ExcatText prefix;
	protected Button prefixCheck;

	//備考欄
	protected Button commentCheck;
	protected ExcatText commentText;

	protected Color red;
	protected Color white;
	protected Color background;


	protected void getColors(Composite parent){
		this.red = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		this.white = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		this.background = parent.getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
	}
	/**
	 * 以下の要素を表示
	 * 	・タスク名
	 * @param composite
	 */
	protected void addHeaderArea(Composite composite){
        Group headerGroup = new Group(composite, SWT.NONE);
        headerGroup.setText(
	    		ApplicationResource.getResource("Config.Task.Name")
				);
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		layout.marginBottom = 3;
		headerGroup.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		headerGroup.setLayoutData(gd);

	    //--------------------task name -------------------------------
	    taskName = new ExcatText(headerGroup,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
	    taskName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL ));

	}

	/**
	 * 以下の要素を表示
	 * 	・有効フラグ
	 * @param composite
	 */
	protected void addEffectArea(Composite composite){
		effectSelect = new Button(composite, SWT.NONE|SWT.CHECK);
        effectSelect.setText(
	    		ApplicationResource.getResource("Config.Effect"));
        effectSelect.setSelection(true);
	}


	protected void addBlankSpace(Composite composite){
		int spaceHeight = ViewerUtil.getMarginHeight(composite);
		Composite spaceComposite = new Composite(composite, SWT.NONE);
		GridData spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spaceGridData.heightHint = spaceHeight;
        spaceComposite.setLayoutData(spaceGridData);
	}

	protected void addPrefixArea(Composite composite){
        Group prefixGroup = new Group(composite, SWT.NONE);
        prefixGroup.setText(
        		ApplicationResource.getResource("Config.Task.Prefix"));
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		prefixGroup.setLayout(layout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		prefixGroup.setLayoutData(gd);

	    prefixCheck = new Button(prefixGroup, SWT.CHECK);
		prefix = new ExcatText(prefixGroup,SWT.LEFT | SWT.SINGLE | SWT.BORDER);
	    gd = new GridData(GridData.FILL_HORIZONTAL);
        prefix.setLayoutData(gd);
        prefix.setEnabled(false);
        prefix.setBackground(background);
	}

	protected void addRemarkArea(Composite composite){
        Group remarkGroup = new Group(composite, SWT.NONE);
        remarkGroup.setText(
        		ApplicationResource.getResource("Config.Task.Note"));
		GridLayout remarkGrouplayout = new GridLayout();
		remarkGrouplayout.marginTop = ViewerUtil.getMarginHeight(composite);
		remarkGrouplayout.marginBottom = ViewerUtil.getMarginHeight(composite);
		remarkGrouplayout.marginLeft = ViewerUtil.getMarginWidth(composite);
		remarkGrouplayout.marginRight = ViewerUtil.getMarginWidth(composite);
		remarkGroup.setLayout(remarkGrouplayout);
		GridData gd = new GridData(GridData.FILL_BOTH);
		remarkGroup.setLayoutData(gd);

	    commentCheck = new Button(remarkGroup, SWT.CHECK);
	    commentText = new ExcatText(remarkGroup, SWT.BORDER |SWT.MULTI | SWT.V_SCROLL | SWT.WRAP);
	    gd = new GridData(GridData.FILL_BOTH);
        gd.verticalSpan = 20;
        commentText.setLayoutData(gd);
        commentText.setEnabled(false);
	}

	protected void addCommentListener(){
		commentCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (commentCheck.getSelection()){
					commentText.setBackground(white);
					commentText.setEnabled(true);
				}else{
					commentText.setText("");
					commentText.setBackground(background);
					commentText.setEnabled(false);
				}
				ConfigModel.setChanged();
			}
		});

		commentText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
	        	  task.setComment(commentText.getText());
	        	  commentText.setBackground(white);
	        	  MainViewer.win.configMainForm.refrash();
	        	  ConfigModel.setChanged();
			}
		});
	}
	protected void addPreFixListener(){
		prefixCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (prefixCheck.getSelection()){
					prefix.setBackground(white);
					prefix.setEnabled(true);
				}else{
					prefix.setText("");
					prefix.setBackground(background);
					prefix.setEnabled(false);
				}
				ConfigModel.setChanged();
			}
		});

		prefix.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
	        	  task.setPrefix(prefix.getText());
	        	  prefix.setBackground(white);
	        	  MainViewer.win.configMainForm.refrash();
	        	  ConfigModel.setChanged();
			}
		});
	}
	protected void addEffectListener(){
		effectSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (effectSelect.getSelection()){
					task.setEffect(true);
				}else{
					task.setEffect(false);
				}
				MainViewer.win.configMainForm.refrash();
				ConfigModel.setChanged();
			}
		});
	}

	protected void addTaskNameListener(){
		// フォーカスが外れたときの処理
//		taskName.addKeyListener(new KeyListener(){
//			@Override
//			public void keyPressed(KeyEvent arg0) {
//			}
//			@Override
//			public void keyReleased(KeyEvent arg0) {
//	        	  task.setTaskName(taskName.getText());
//	        	  MainViewer.win.configMainForm.refrash();
//			}
//		});
		taskName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent modifyevent) {
	        	  task.setTaskName(taskName.getText());
	        	  MainViewer.win.configMainForm.refrash();
	        	  ConfigModel.setChanged();
			}
		});
	}

	protected void updateCommonItems(){
		//prefix.setText("");
		//prefix.setBackground(background);

		if ("".equals(commentText.getText())){
			commentText.setEnabled(false);
			commentCheck.setSelection(false);
		}else{
			commentText.setEnabled(true);
			commentCheck.setSelection(true);
		}

		taskName.setText(task.getTaskName());
		effectSelect.setSelection(task.isEffect());
		String comment = task.getComment();
		if (comment != null && !"".equals(comment)){
			commentCheck.setSelection(true);
			commentText.setText(comment);
			commentText.setEnabled(true);
		}
		String p = task.getPrefix();
		if (p != null && !"".equals(p)){
			prefixCheck.setSelection(true);
			prefix.setText(p);
			prefix.setEnabled(true);
			prefix.setBackground(white);
		}
	}

	protected boolean checkItems() {
		boolean result=true;
		if (prefixCheck.getSelection() &&
			!ViewerUtil.checkStringItem(prefix.getText())){
			prefix.setBackground(red);
			result=false;
		}
		if (commentCheck.getSelection() &&
			!ViewerUtil.checkStringItem(commentText.getText())){
			commentText.setBackground(red);
			result=false;
		}

		return result;
	}
}
