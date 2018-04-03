package jp.co.ipride.excat.configeditor.viewer.task;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.configeditor.ExcatText;
import jp.co.ipride.excat.configeditor.model.task.AbstractTask;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * 他の定義アリア
 * @author tu
 * @since 2007/11/10
 */
public class OtherForm{

	static int Text_Width = 100;

	private SashForm appWindow;
	private Composite parent;
	private Button selectMail;
	private Button mailAttach;
	private Button saveDaysCheck;
	private ExcatText saveDaysText;

	private AbstractTask task;
	private ITaskForm taskForm;

	private Color red;
	private Color white;
	private Color background;

	private Group mailGroup;
	private Group saveDaysGroup;
	private Group[] groups;
	/**
	 * construct
	 */
	public OtherForm(SashForm appWindow, Composite composite, ITaskForm taskForm){
		this.appWindow = appWindow;
		this.taskForm = taskForm;
		this.task = (AbstractTask)taskForm.getTask();
		this.red = appWindow.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		this.white = appWindow.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		this.background = appWindow.getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

		parent = new Composite(composite,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginLeft=5;
		layout.marginRight=5;
		layout.marginTop=2;
		layout.marginBottom=2;
		layout.numColumns=1;
		parent.setLayout(layout);
		createLayout();
		init();
        addListeners();
	}

	/**
	 * 基本設定からのクローズ通知
	 *
	 */
	public void closeMailNotice(){
		selectMail.setSelection(false);
		mailAttach.setSelection(false);
		mailAttach.setEnabled(false);
		task.getDumpData().setNoticeMail(false);
		task.getDumpData().setAttachDumpFile(false);
	}

	/**
	 * input data
	 *
	 */
	private void init(){
		groups = new Group[]{saveDaysGroup,mailGroup};
		selectMail.setSelection(task.getDumpData().isNoticeMail());
		mailAttach.setSelection(task.getDumpData().isAttachDumpFile());
		saveDaysCheck.setSelection(task.getDumpData().isAutoDelete());
		saveDaysText.setText(task.getDumpData().getSaveDays());

		if (selectMail.getSelection()){
			mailAttach.setEnabled(true);
		}else{
			mailAttach.setSelection(false);
			mailAttach.setEnabled(false);
		}

		if (saveDaysCheck.getSelection()){
			saveDaysText.setEnabled(true);
		}else{
			saveDaysText.setEnabled(false);
		}
	}

	/**
	 * 右のフォーム設定
	 */
	private void createLayout(){
		//メール通知の設定
        mailGroup = new Group(parent, SWT.NONE);
        mailGroup.setText(
        		ApplicationResource.getResource("Tab.Task.Other.Mail.Text"));
		GridLayout mailGroupLayout = new GridLayout();
		mailGroupLayout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
		mailGroupLayout.marginRight = ViewerUtil.getMarginWidth(appWindow);
		mailGroupLayout.marginTop = ViewerUtil.getMarginHeight(appWindow);
		mailGroupLayout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
        mailGroup.setLayout(mailGroupLayout);
        GridData mailGroupGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        mailGroupGridData.widthHint =ViewerUtil.getConfigPlateWidth(appWindow);
        mailGroup.setLayoutData(mailGroupGridData);

        selectMail =new Button(mailGroup, SWT.CHECK);
        selectMail.setText(
        		ApplicationResource.getResource("Tab.Task.Other.Mail.Select.Text"));
		//ダンプファイル添付有無
		mailAttach = new Button(mailGroup, SWT.CHECK);
		mailAttach.setText(
				ApplicationResource.getResource("Tab.BasicInfo.Mail.Attach.Text"));

        //保存期間の設定
        saveDaysGroup = new Group(parent, SWT.NONE);
        saveDaysGroup.setText(
        		ApplicationResource.getResource("Tab.Task.Other.Delecte.Text"));
        GridLayout saveDaysGroupLayout = new GridLayout(3,false);
        saveDaysGroupLayout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
        saveDaysGroupLayout.marginRight = ViewerUtil.getMarginWidth(appWindow);
        saveDaysGroupLayout.marginTop = ViewerUtil.getMarginHeight(appWindow);
        saveDaysGroupLayout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
        saveDaysGroup.setLayout(saveDaysGroupLayout);

        GridData saveDaysGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        saveDaysGridData.widthHint =ViewerUtil.getConfigPlateWidth(appWindow);
        saveDaysGroup.setLayoutData(saveDaysGridData);

        //自動削除選択
        saveDaysCheck = new Button(saveDaysGroup,SWT.CHECK);
        saveDaysCheck.setText(ApplicationResource.getResource("Config.Dump.AutDelete.Check"));
		saveDaysGridData = new GridData(GridData.BEGINNING);
		saveDaysGridData.horizontalSpan = 3;
		saveDaysCheck.setLayoutData(saveDaysGridData);

		Label saveDaysLabel = new Label(saveDaysGroup, SWT.NONE);
		saveDaysLabel.setText(
				ApplicationResource.getResource("Tab.Task.Other.Delecte.KeepTime.Text"));

        saveDaysText = new ExcatText(saveDaysGroup, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
        saveDaysText.setControlWidth(Text_Width);
		Label dayLabel = new Label(saveDaysGroup, SWT.NONE);
		dayLabel.setText(
				ApplicationResource.getResource("Tab.Task.Other.Delecte.KeepTime.Day.Text"));
	}

	private void addListeners(){
		saveDaysText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				if (checkSaveDaysText()){
					task.getDumpData().setSaveDays(saveDaysText.getText());
					saveDaysText.setBackground(white);
					ConfigModel.setChanged();
				}
			}
		});

		/*
		commentText.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				task.setComment(commentText.getText());
				commentText.setBackground(white);
			}
		});
		*/

		selectMail.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (selectMail.getSelection()){
					if (ConfigModel.checkMail()){
						task.getDumpData().setNoticeMail(true);
						mailAttach.setEnabled(true);
					}else{
						ExcatMessageUtilty.showMessage(
								parent.getShell(),
								Message.get("Dialog.Task.Mail.Attach.Check.Text"));
						mailAttach.setEnabled(false);
						selectMail.setSelection(false);
						task.getDumpData().setNoticeMail(false);
						task.getDumpData().setAttachDumpFile(false);
					}
				}else{
					mailAttach.setSelection(false);
					mailAttach.setEnabled(false);
					task.getDumpData().setNoticeMail(false);
					task.getDumpData().setAttachDumpFile(false);
				}
				ConfigModel.setChanged();
			}
		});
		mailAttach.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				task.getDumpData().setAttachDumpFile(mailAttach.getSelection());
				ConfigModel.setChanged();
			}
		});

		saveDaysText.addVerifyListener(
				new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						e.doit=ViewerUtil.verifyNumbers(e.text);
					}
				});
		saveDaysCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				task.getDumpData().setAutoDelete(saveDaysCheck.getSelection());
				if (saveDaysCheck.getSelection()){
					saveDaysText.setBackground(white);
					saveDaysText.setEnabled(true);
				}else{
					saveDaysText.setBackground(background);
					saveDaysText.setEnabled(false);
				}
				ConfigModel.setChanged();
			}
		});

		/*
		commentCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (commentCheck.getSelection()){
					commentText.setBackground(white);
					commentText.setEnabled(true);
				}else{
					commentText.setBackground(background);
					commentText.setEnabled(false);
				}
			}
		});
		 */

	}

	private boolean checkSaveDaysText(){
		int num = Integer.parseInt(saveDaysText.getText().trim());
		if (num > 365){
			saveDaysText.setBackground(red);
			ExcatMessageUtilty.showMessage(
					parent.getShell(),
					Message.get("Dialog.Error.SaveDays.Text"));
			return false;
		}else{
			return true;
		}
	}


	public boolean checkItems(){
		boolean result=true;
		if (saveDaysCheck.getSelection()){
			if (!ViewerUtil.isLargerThanMinValue(saveDaysText.getText(),0)){
				saveDaysText.setBackground(red);
				result = false;
			} else if (!checkSaveDaysText()){
				result = false;
			}
		}

		/*
		if (commentCheck.getSelection()){
			if (!ViewerUtil.checkStringItem(commentText.getText())){
				commentText.setBackground(red);
				result=false;
			}
		}
		*/
		return result;
	}

	public void setEnabled(boolean flg){
		for (int i = 0; i<groups.length; i++){
			if (groups[i] != null){
				for (int j = 0 ; j<groups[i].getChildren().length; j++){
					groups[i].getChildren()[j].setEnabled(flg);
				}
			}
		}
	}
}
