package jp.co.ipride.excat.configeditor.viewer.task;

import java.util.Vector;

import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MethodInfo;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.clipboard.ExcatTemplate;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.task.MonitorMethodTask;
import jp.co.ipride.excat.configeditor.model.task.MonitoringMethod;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.configeditor.viewer.task.dialog.MethodRegisterDlg;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
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
 * メソッド監視タスク
 * @author wang
 * @since 2009/07/20
 */
public class MonitorMethodTaskForm extends TaskCommonForm{

	private SashForm appWindow;
	private ITaskForm taskForm;
	private MonitorMethodTaskForm thisForm;
	private Composite parent;

	//監視対象（メソッド）のボタン
	private Button addMethodButton;
	private Button editMethodButton;
	private Button deleteMethodButton;
	private Table methodTable;

	private Vector<MonitoringMethod> methodMonitors = null;

	public MonitorMethodTaskForm(SashForm appWindow,
						  Composite composite,
						  ITaskForm taskForm){

		this.appWindow = appWindow;
		this.task = taskForm.getTask();
		thisForm = this;
		getColors(appWindow);

		parent = new Composite(composite,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
		layout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight = ViewerUtil.getMarginWidth(appWindow);
		layout.numColumns=1;
		parent.setLayout(layout);
        GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        parent.setLayoutData(gridData);
		parent.setLayout(layout);

		createLayout();
		addListeners(parent);
		init();
	}

	private void init(){

		addMethodButton.setEnabled(true);
		editMethodButton.setEnabled(false);
		deleteMethodButton.setEnabled(false);
		methodMonitors = ((MonitorMethodTask)task).getMonitoringMethodList();
		updateCommonItems();
		updateDataToDisplay();

	}

	private void createLayout(){
        //メソッド監視タスクのエリア
		Group methodGroup = new Group(parent, SWT.NONE);
		methodGroup.setText(
        		ApplicationResource.getResource("Config.Task.MethodMonitor"));
		GridLayout methodGrouplayout = new GridLayout();
		methodGrouplayout.marginTop = ViewerUtil.getMarginHeight(appWindow);
		methodGrouplayout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
		methodGrouplayout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
		methodGrouplayout.marginRight = ViewerUtil.getMarginWidth(appWindow);
		methodGroup.setLayout(methodGrouplayout);
		methodGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL ));
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        gridData.widthHint = ViewerUtil.getConfigPlateWidth(appWindow);
        methodGroup.setLayoutData(gridData);

        //--------------------Effect  --------
        addBlankSpace(methodGroup);
        addEffectArea(methodGroup);
        addBlankSpace(methodGroup);

		//--------------------header  --------
		addHeaderArea(methodGroup);
		addBlankSpace(methodGroup);
	    //--------------------監視メソッド -------------------------------
		addMethodTable(methodGroup);
		addBlankSpace(methodGroup);
	    //-------------Prefix------------------------
        addPrefixArea(methodGroup);
        addBlankSpace(methodGroup);
	    //-------------Comment------------------------
	    addRemarkArea(methodGroup);
	    addBlankSpace(methodGroup);

	}

	public Shell getShell(){
		return appWindow.getShell();
	}

	private void addMethodTable(Composite composite){
        Group packageGroup = new Group(composite, SWT.NONE);
        packageGroup.setText(
        		ApplicationResource.getResource("Config.Task.MethodMonitor.Method")
        	);
		GridLayout layout = new GridLayout();
		layout.numColumns = 1;
		layout.marginTop = ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
		layout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight = ViewerUtil.getMarginWidth(appWindow);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.heightHint = Tabel_High;
		packageGroup.setLayout(layout);
		packageGroup.setLayoutData(gridData);

		methodTable = new Table(packageGroup, SWT.BORDER | SWT.CHECK | SWT.V_SCROLL | SWT.H_SCROLL);
        gridData = new GridData(GridData.FILL_BOTH);
		methodTable.setLayoutData(gridData);

	    //button
		Composite buttonForm = new Composite(packageGroup,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonFormlayout.numColumns = 3;
		buttonFormlayout.marginHeight = 1;
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonForm.setLayoutData(gd);

		addMethodButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.add"),
				Utility.BUTTON_WIDTH,1);

		editMethodButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.edit"),
				Utility.BUTTON_WIDTH,1);

		deleteMethodButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.delete"),
				Utility.BUTTON_WIDTH,1);
	}


	private void addListeners(Composite composite){
		//追加ボタン
		addMethodButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				MethodRegisterDlg dialog = new MethodRegisterDlg(thisForm);
				MonitoringMethod template = new MonitoringMethod();
				MethodInfo methodInfo = ExcatTemplate.getMethodInfo();
				if (methodInfo != null){
					dialog.init(template,methodInfo,methodMonitors);
				}else{
					dialog.init(template,methodMonitors);
				}
				int r = dialog.open();
				if (r==Dialog.OK){
					methodMonitors.add(methodMonitors.size(),template);
					updateDataToDisplay();
				}
			}
		});
		//メソッド編集ボタン
		editMethodButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = methodTable.getSelectionIndex();
				if (index >=0){
					MethodRegisterDlg dialog = new MethodRegisterDlg(thisForm);
					MonitoringMethod methodMonitor = (MonitoringMethod)methodMonitors.get(index);
					MonitoringMethod template = new MonitoringMethod();
					methodMonitor.copyTo(template);
					dialog.init(template,methodMonitors);
					int result =dialog.open();
					if (result == Dialog.OK){
						template.copyTo(methodMonitor);
						updateDataToDisplay();
					}
				}
			}
		});
		//メソッド削除ボタン
		deleteMethodButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = methodTable.getSelectionIndex();
				if (index >=0){
					methodTable.remove(index);
					methodMonitors.remove(index);
					ConfigModel.setChanged();
				}
				//if (methodTable.getItemCount()==0){
				editMethodButton.setEnabled(false);
				deleteMethodButton.setEnabled(false);
				//}
			}
		});
		methodTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = (TableItem)e.item;
				MonitoringMethod monitor = (MonitoringMethod)item.getData();
				monitor.setUse(item.getChecked());
				editMethodButton.setEnabled(true);
				deleteMethodButton.setEnabled(true);
			}
		});

		addEffectListener();

		addCommentListener();

		addPreFixListener();

		addTaskNameListener();

	}

	private void updateDataToDisplay(){

		methodTable.removeAll();
		methodTable.setBackground(white);

		for (int i=0; i<methodMonitors.size(); i++){
			MonitoringMethod monitor=(MonitoringMethod)methodMonitors.get(i);
			if (!"".equals(monitor.getDisplayName())){
				TableItem item =new TableItem(methodTable, SWT.NONE);
				item.setText(new String[] {monitor.getDisplayName()});
				item.setData(monitor);
				item.setChecked(monitor.isUse());
			}
		}
	}

	@Override
	public boolean checkItems() {
		boolean result = true;
		if (methodMonitors.size() == 0) {
			methodTable.setBackground(red);
			result = false;
		}

		if (!super.checkItems()) {
			result = false;
		}

		return result;
	}
}
