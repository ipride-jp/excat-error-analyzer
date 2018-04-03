package jp.co.ipride.excat.configeditor.viewer.task;

import java.util.Vector;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.task.ExceptionRegisterTask;
import jp.co.ipride.excat.configeditor.model.task.MonitoringException;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.configeditor.viewer.task.dialog.ExceptionRegisterDlg;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * 指定例外監視タスク
 * @author wang
 * @since 2009/07/20
 */
public class MonitorExceptionTaskForm extends TaskCommonForm{

	private Button addMonitorExceptionBtn;
	private Button editMonitorExceptionBtn;
	private Button deleteMonitorExceptionBtn;

	private Table exceptionTable;
	private Vector<MonitoringException> exceptionMonitors = null;

	/**
	 * construct.
	 * @param appWindow
	 * @param composite
	 * @param taskForm
	 */
	public MonitorExceptionTaskForm(SashForm appWindow,
							Composite composite,
							ITaskForm taskForm){
		parent = new Composite(composite,SWT.NONE);
		this.parentForm = appWindow;
		this.taskForm = taskForm;
		this.task = taskForm.getTask();
		getColors(appWindow);

		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
		layout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight = ViewerUtil.getMarginWidth(appWindow);
		layout.numColumns=1;
        GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        parent.setLayoutData(gridData);
		parent.setLayout(layout);

		createLayout();
		addListeners(parent);
		init();
	}

	private void init(){
		addMonitorExceptionBtn.setEnabled(true);
		editMonitorExceptionBtn.setEnabled(false);
		deleteMonitorExceptionBtn.setEnabled(false);
		exceptionMonitors = ((ExceptionRegisterTask)task).getMonitoringExceptionList();
		updateCommonItems();
		updateDataToDisplay();

	}

	public Shell getShell(){
		return parentForm.getShell();
	}

	private void createLayout(){

        //例外自動監視タスクのエリア
		Group specificGroup = new Group(parent, SWT.NONE);
		specificGroup.setText(
        		ApplicationResource.getResource("Config.Task.ExceptionMonitor"));
		GridLayout specificGrouplayout = new GridLayout();
		specificGrouplayout.marginTop = ViewerUtil.getMarginHeight(parentForm);
		specificGrouplayout.marginBottom = ViewerUtil.getMarginHeight(parentForm);
		specificGrouplayout.marginLeft = ViewerUtil.getMarginWidth(parentForm);
		specificGrouplayout.marginRight = ViewerUtil.getMarginWidth(parentForm);
		specificGroup.setLayout(specificGrouplayout);
		specificGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL ));
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        gridData.widthHint =ViewerUtil.getConfigPlateWidth(parentForm);
        specificGroup.setLayoutData(gridData);

        //--------------------Effect  --------
        addBlankSpace(specificGroup);
        addEffectArea(specificGroup);
        addBlankSpace(specificGroup);

	    //--------------------header -------------------------------
        addHeaderArea(specificGroup);
        addBlankSpace(specificGroup);
	    //--------------------table 1 -------------------------------
		addPackageTable(specificGroup);
		addBlankSpace(specificGroup);
	    //-------------Prefix------------------------
		addPrefixArea(specificGroup);
		addBlankSpace(specificGroup);
	    //-------------Comment------------------------
	    addRemarkArea(specificGroup);
	    addBlankSpace(specificGroup);

	}


	private void addPackageTable(Composite composite){
        Group packageGroup = new Group(composite, SWT.NONE);
        packageGroup.setText(
        		ApplicationResource.getResource("Config.Task.ExceptionMonitor.MonitorException")
        	);
		GridLayout packageGrouplayout = new GridLayout();
		packageGrouplayout.numColumns = 1;
		packageGrouplayout.marginTop = ViewerUtil.getMarginHeight(parentForm);
		packageGrouplayout.marginBottom = ViewerUtil.getMarginHeight(parentForm);
		packageGrouplayout.marginLeft = ViewerUtil.getMarginWidth(parentForm);
		packageGrouplayout.marginRight = ViewerUtil.getMarginWidth(parentForm);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.heightHint = Tabel_High;
		packageGroup.setLayout(packageGrouplayout);
		packageGroup.setLayoutData(gridData);

		exceptionTable = new Table(packageGroup, SWT.BORDER | SWT.CHECK | SWT.V_SCROLL | SWT.H_SCROLL);
        gridData = new GridData(GridData.FILL_BOTH);
		exceptionTable.setLayoutData(gridData);

	    //button
		Composite buttonForm = new Composite(packageGroup,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonFormlayout.numColumns = 3;
		buttonFormlayout.marginHeight = 1;
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonForm.setLayoutData(gd);

		addMonitorExceptionBtn = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.add"),
				Utility.BUTTON_WIDTH,1);

		editMonitorExceptionBtn = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.edit"),
				Utility.BUTTON_WIDTH,1);

		deleteMonitorExceptionBtn = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.delete"),
				Utility.BUTTON_WIDTH,1);

	}


	private void addListeners(Composite composite){
		//追加ボタン
		addMonitorExceptionBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ExceptionRegisterDlg dialog = new ExceptionRegisterDlg(parentForm.getShell());
				MonitoringException template = new MonitoringException();
				dialog.init(template, exceptionMonitors);
				int result = dialog.open();
				if (result == Dialog.OK){
					exceptionMonitors.add(exceptionMonitors.size(), template);
					updateDataToDisplay();
				}
			}
		});
		//編集ボタン
		editMonitorExceptionBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = exceptionTable.getSelectionIndex();
				if (index >=0){
					ExceptionRegisterDlg dialog = new ExceptionRegisterDlg(parentForm.getShell());
					MonitoringException monitor =(MonitoringException)exceptionMonitors.get(index);
					MonitoringException template = new MonitoringException();
					monitor.copyTo(template);
					dialog.init(template, exceptionMonitors);
					int result = dialog.open();
					if (result == Dialog.OK){
						template.copyTo(monitor);
						updateDataToDisplay();
					}
				}
			}
		});
		//例外削除ボタン
		deleteMonitorExceptionBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = exceptionTable.getSelectionIndex();
				if (index >=0){
					exceptionTable.remove(index);
					exceptionMonitors.remove(index);
					ConfigModel.setChanged();
				}
				//if (exceptionTable.getItemCount()==0){
				editMonitorExceptionBtn.setEnabled(false);
				deleteMonitorExceptionBtn.setEnabled(false);
				//}
			}
		});
		exceptionTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = (TableItem)e.item;
				MonitoringException monitor = (MonitoringException)item.getData();
				monitor.setUse(item.getChecked());
				editMonitorExceptionBtn.setEnabled(true);
				deleteMonitorExceptionBtn.setEnabled(true);
			}
		});
		exceptionTable.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent arg0) {
			}
			public void focusLost(FocusEvent arg0) {
			}
		});
		addEffectListener();

		addCommentListener();

		addPreFixListener();

		addTaskNameListener();

	}

	/**
	 * 例外を監視するチェックを入れた場合
	 *
	 */
	private void updateDataToDisplay(){

		exceptionTable.removeAll();
		exceptionTable.setBackground(white);

		for (int i=0; i<exceptionMonitors.size(); i++){
			MonitoringException monitor=(MonitoringException)exceptionMonitors.get(i);
			TableItem item = new TableItem(exceptionTable, SWT.NONE);
			item.setText(new String[] {monitor.getTargetClassName()});
			item.setData(monitor);
			item.setChecked(monitor.isUse());
		}
	}

	@Override
	public boolean checkItems() {
		boolean result = true;
		if (exceptionMonitors.size() == 0) {
			exceptionTable.setBackground(red);
			result = false;
		}

		if (!super.checkItems()) {
			result = false;
		}

		return result;
	}

}
