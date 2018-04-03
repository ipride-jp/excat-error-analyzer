package jp.co.ipride.excat.configeditor.viewer.task;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.configeditor.ExcatText;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.task.AbstractTask;
import jp.co.ipride.excat.configeditor.model.task.DumpData;
import jp.co.ipride.excat.configeditor.model.task.ITask;
import jp.co.ipride.excat.configeditor.model.task.MonitorSignalTask;
import jp.co.ipride.excat.configeditor.model.task.MonitoringSignal;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * シグナル監視タスク
 * @author wang
 * @since 2009/07/20
 */
public class MonitorSignalTaskForm extends TaskCommonForm{

	private SashForm appWindow;
	private ITaskForm taskForm;
	private Composite parent;

	protected Button threadDumpButton;
	protected Button instanceDumpButton;

	private Button selectAllThread;
	private Button selectWaitThread;
	private Button selectActiveThread;

	private Button threadPriorityCheck;
	private ExcatText threadPriority;

	private MonitoringSignal signalMonitor = null;

	public MonitorSignalTaskForm(SashForm appWindow,
			  			  Composite composite,
			  			  ITaskForm taskForm){

		this.appWindow = appWindow;
		this.taskForm = taskForm;
		this.task = taskForm.getTask();
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

	private void createLayout(){
        //シグナル監視タスクのエリア
		Group signalGroup = new Group(parent, SWT.NONE);
		signalGroup.setText(
        		ApplicationResource.getResource("Config.Task.SigMonitor"));
		GridLayout signalGrouplayout = new GridLayout();
		signalGrouplayout.marginTop = ViewerUtil.getMarginHeight(appWindow);
		signalGrouplayout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
		signalGrouplayout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
		signalGrouplayout.marginRight = ViewerUtil.getMarginWidth(appWindow);
		signalGroup.setLayout(signalGrouplayout);
//		signalGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL ));
        GridData gridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL | GridData.VERTICAL_ALIGN_FILL);
        gridData.widthHint = ViewerUtil.getConfigPlateWidth(appWindow);
        signalGroup.setLayoutData(gridData);

        //--------------------Effect  --------
        addBlankSpace(signalGroup);
        addEffectArea(signalGroup);
        addBlankSpace(signalGroup);

        //--------------------title -------------------------------
        addHeaderArea(signalGroup);
        addBlankSpace(signalGroup);
		//-------------------thread dump radio--------------------------
	    threadDumpButton = new Button(signalGroup, SWT.RADIO);
	    threadDumpButton.setText(
	    		ApplicationResource.getResource("Config.Task.SigMonitor.Thread"));

	    //--------------------radio -------------------------------
		addThreadDumpArea(signalGroup);
		addBlankSpace(signalGroup);
		//-------------------instance dump--------------------------
	    instanceDumpButton = new Button(signalGroup, SWT.RADIO);
	    instanceDumpButton.setText(
	    		ApplicationResource.getResource("Config.Task.SigMonitor.Instance"));
        addBlankSpace(signalGroup);
        addBlankSpace(signalGroup);

	    //-------------Prefix------------------------
	    addPrefixArea(signalGroup);
	    addBlankSpace(signalGroup);
	    //-------------Comment------------------------
	    addRemarkArea(signalGroup);
	    addBlankSpace(signalGroup);

	}

	private void init(){
		signalMonitor = ((MonitorSignalTask)task).getMonitoringSignal();
		updateDataToDisplay();
		if ("".equals(commentText.getText())){
			commentText.setEnabled(false);
			commentCheck.setSelection(false);
		}else{
			commentText.setEnabled(true);
			commentCheck.setSelection(true);
		}

	}

	private void addThreadDumpArea(Composite composite){
		GridData gridData;
		Composite threadDumpArea = new Composite(composite,SWT.NONE);
		GridLayout threadDumpArealayout = new GridLayout();
		threadDumpArealayout.numColumns = 1;
		threadDumpArealayout.marginHeight = 1;
		threadDumpArea.setLayout(threadDumpArealayout);
		threadDumpArea.setLayoutData(new GridData(GridData.FILL_BOTH));

		Group threadGroup = new Group(threadDumpArea, SWT.NONE);
		GridLayout trigger1Grouplayout = new GridLayout(3,false);
		trigger1Grouplayout.marginTop = ViewerUtil.getMarginHeight(appWindow);
		trigger1Grouplayout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
		trigger1Grouplayout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
		trigger1Grouplayout.marginRight = ViewerUtil.getMarginWidth(appWindow);
		threadGroup.setLayout(trigger1Grouplayout);
		threadGroup.setLayoutData(new GridData(GridData.FILL_BOTH));

	    selectAllThread = new Button(threadGroup, SWT.RADIO);
	    gridData = new GridData();
	    gridData.horizontalSpan=3;
	    selectAllThread.setLayoutData(gridData);
	    selectAllThread.setText(
	    		ApplicationResource.getResource("Config.Task.SigMonitor.Thread.All"));

	    selectWaitThread = new Button(threadGroup, SWT.RADIO);
	    gridData = new GridData();
	    gridData.horizontalSpan=3;
	    selectWaitThread.setLayoutData(gridData);
	    selectWaitThread.setText(
	    		ApplicationResource.getResource("Config.Task.SigMonitor.Thread.WaittingMonitor"));

	    selectActiveThread = new Button(threadGroup, SWT.RADIO);
	    gridData = new GridData();
	    gridData.horizontalSpan=3;
	    selectActiveThread.setLayoutData(gridData);
	    selectActiveThread.setText(
	    		ApplicationResource.getResource("Config.Task.SigMonitor.Thread.Active"));

	    threadPriorityCheck = new Button(threadGroup, SWT.CHECK);
	    threadPriorityCheck.setText(ApplicationResource.getResource("Config.Dump.Thread.ThreadPriority"));
	    threadPriority = new ExcatText(threadGroup, SWT.BORDER | SWT.RIGHT);
	    gridData = new GridData();
	    gridData.widthHint=60;
	    threadPriority.setLayoutData(gridData);
	    Label threadPriorityLabel = new Label(threadGroup, SWT.NONE);
	    threadPriorityLabel.setText(ApplicationResource.getResource("Config.Dump.Thread.Above"));

	}


	private void addListeners(Composite composite){

		addEffectListener();

		addCommentListener();

		addPreFixListener();

		addTaskNameListener();

		threadDumpButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (threadDumpButton.getSelection()){
					signalMonitor.setDumpKind(MonitoringSignal.STACK);
					taskForm.selectDumpStackBySignal();
					instanceDumpButton.setSelection(false);
					threadDumpButton.setSelection(true);
					selectAllThread.setEnabled(true);
					selectWaitThread.setEnabled(true);
					selectActiveThread.setEnabled(true);
					threadPriorityCheck.setEnabled(true);
					threadPriority.setEnabled(true);
					threadPriority.setBackground(white);
					taskForm.selectDumpStackBySignal();
				}
			}
		});

		instanceDumpButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (instanceDumpButton.getSelection()){
					signalMonitor.setDumpKind(MonitoringSignal.INSTANCE);
					instanceDumpButton.setSelection(true);
					threadDumpButton.setSelection(false);
					selectAllThread.setEnabled(false);
					selectWaitThread.setEnabled(false);
					selectActiveThread.setEnabled(false);
					threadPriorityCheck.setEnabled(false);
					threadPriority.setEnabled(false);
					threadPriority.setBackground(background);
					taskForm.selectDumpObjectBySignal();
				}
			}
		});
		selectAllThread.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (selectAllThread.getSelection()){
					selectAllThread.setSelection(true);
					selectWaitThread.setSelection(false);
					selectActiveThread.setSelection(false);
					((AbstractTask)task).getDumpData().setDumpThreadType(DumpData.DUMP_ALL_THREAD);
				}
				ConfigModel.setChanged();
			}
		});
		selectWaitThread.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (selectWaitThread.getSelection()){
					selectAllThread.setSelection(false);
					selectWaitThread.setSelection(true);
					selectActiveThread.setSelection(false);
					((AbstractTask)task).getDumpData().setDumpThreadType(DumpData.DUMP_WAITTING_THREAD);
				}
				ConfigModel.setChanged();
			}
		});
		selectActiveThread.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (selectActiveThread.getSelection()){
					selectAllThread.setSelection(false);
					selectWaitThread.setSelection(false);
					selectActiveThread.setSelection(true);
					((AbstractTask)task).getDumpData().setDumpThreadType(DumpData.DUMP_ACTIVE_THREAD);
				}
				ConfigModel.setChanged();
			}
		});
		threadPriorityCheck.addSelectionListener(new SelectionAdapter(){
			public void widgetSelected(SelectionEvent e) {
				if (threadPriorityCheck.getSelection()){
					threadPriority.setEnabled(true);
					threadPriority.setBackground(white);
				}else{
					threadPriority.setEnabled(false);
					threadPriority.setText("");
					threadPriority.setBackground(background);
				}
				ConfigModel.setChanged();
			}
		});
		threadPriority.addVerifyListener(
				new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						e.doit=ViewerUtil.verifyNumbers(e.text);
					}
				});
		threadPriority.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				((AbstractTask)task).getDumpData().setThreadPriority(threadPriority.getText());
				threadPriority.setBackground(white);
				ConfigModel.setChanged();
			}
		});
	}

	private void updateDataToDisplay(){

		updateCommonItems();

		if (task.getTaskType() == ITask.MONITOR_SIGNAL){
			int type = signalMonitor.getDumpKind();
			if (type == MonitoringSignal.STACK){
				threadDumpButton.setSelection(true);
				instanceDumpButton.setSelection(false);
				switch(((AbstractTask)task).getDumpData().getDumpThreadType()){
				case DumpData.DUMP_ALL_THREAD:
					selectAllThread.setSelection(true);
					selectActiveThread.setSelection(false);
					selectWaitThread.setSelection(false);
					break;
				case DumpData.DUMP_ACTIVE_THREAD:
					selectAllThread.setSelection(false);
					selectActiveThread.setSelection(true);
					selectWaitThread.setSelection(false);
					break;
				case DumpData.DUMP_WAITTING_THREAD:
					selectAllThread.setSelection(false);
					selectActiveThread.setSelection(false);
					selectWaitThread.setSelection(true);
					break;
				}
				threadPriority.setText(((AbstractTask)task).getDumpData().getThreadPriority());
				if ("".equals(threadPriority.getText())){
					threadPriorityCheck.setSelection(false);
					threadPriority.setEnabled(false);
					threadPriority.setBackground(background);
				}else{
					threadPriorityCheck.setSelection(true);
					threadPriority.setEnabled(true);
					threadPriority.setBackground(white);
				}
			}else if (type == MonitoringSignal.INSTANCE){
				instanceDumpButton.setSelection(true);
				threadDumpButton.setSelection(false);
			}
		}
	}

	@Override
	public boolean checkItems() {
		boolean result = true;
		if (threadPriorityCheck.getEnabled() &&
				threadPriorityCheck.getSelection() &&
				!ViewerUtil.checkStringItem(threadPriority.getText())) {
			threadPriority.setBackground(red);
			result = false;
		}

		if (!super.checkItems()) {
			result = false;
		}

		return result;
	}
}
