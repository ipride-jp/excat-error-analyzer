package jp.co.ipride.excat.configeditor.viewer.task;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.configeditor.ExcatText;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.task.DumpData;
import jp.co.ipride.excat.configeditor.model.task.ITask;
import jp.co.ipride.excat.configeditor.model.task.AbstractTask;
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
 * ダンプデータの定義アリア
 * @author tu
 * @since 2007/11/10
 */
public class DumpDataForm{
	static int Text_Width = 100;
	private SashForm appWindow;
	private Composite parent;

	private Button curThreadSelect;
	private Button runningThreadSelect;
	private Button allThreadSelect;
	private ExcatText stackDepth;
	private ExcatText runningThreadPriority;
	private Button runningThreadPrioritySelect;
	private Label runningThreadPriorityLabel;
	private ExcatText allThreadPriority;
	private Button allThreadPrioritySelect;
	private Label allThreadPriorityLabel;

	private Button methodArgumentSelect;
	private Button thisObjectSelect;
	private Button otherInstanceSelect;

	private Button methodLoaclVarSelect;
	private Button publicSelect;
	private Button privateSelect;
	private Button packageSelect;
	private Button protectedSelect;
	private ExcatText objectNestDepth;
	private ExcatText  maxObjectElement;
	private ExcatText  maxPrimitiveElement;

	private Label stackDepthLabel;
	private Label objectNestDepthLabel;
	private Label objectElementLabel;
	private Label primitiveElementLabel;

	private Color red;
	private Color white;
	private Color background;

	private Group topGroup;
	private Group threadGroup;
	private Group stackGroup;
	private Group methodGroup;
	private Group objectElementGroup;
	private Group nestDepthGroup;
	private Group listLimitGroup;
	private Group[] groups;
	private Composite spaceComposite;

	private int style;
	private AbstractTask task;

	/**
	 * コンストラクタ
	 * @param appWindow
	 * @param composite
	 */
	public DumpDataForm(SashForm appWindow, Composite composite, ITaskForm taskForm, int style){
		this.appWindow=appWindow;
		this.style=style;
		this.task = (AbstractTask)taskForm.getTask();
		this.red = appWindow.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		this.white = appWindow.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		this.background = appWindow.getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);

		parent = new Composite(composite,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
		layout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight = ViewerUtil.getMarginWidth(appWindow);
		layout.numColumns=1;
		parent.setLayout(layout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        parent.setLayoutData(gridData);

		createLayout();
		init();
		addListeners();
	}

	/**
	 * 初期化及びデータセット
	 * @param task
	 */
	private void init(){
		groups = new Group[]{
			 topGroup,
			 threadGroup,
			 stackGroup,
			 methodGroup,
			 objectElementGroup,
			 nestDepthGroup,
			 listLimitGroup};

		stackDepth.setText(task.getDumpData().getStackTraceDepth());
		methodArgumentSelect.setSelection(task.getDumpData().isDumpArgument());
		methodLoaclVarSelect.setSelection(task.getDumpData().isDumpVariable());
		thisObjectSelect.setSelection(task.getDumpData().isDumpThis());
		objectNestDepth.setText(task.getDumpData().getAttributeNestDepth());
		publicSelect.setSelection(task.getDumpData().isDumpPublic());
		privateSelect.setSelection(task.getDumpData().isDumpPrivate());
		protectedSelect.setSelection(task.getDumpData().isDumpProtected());
		packageSelect.setSelection(task.getDumpData().isDumpPackage());
		maxObjectElement.setText(task.getDumpData().getMaxArrayElementForObject());
		maxPrimitiveElement.setText(task.getDumpData().getMaxArrayElementForPrimitive());

		if (task.getTaskType()!=ITask.MONITOR_SIGNAL){
			//otherInstanceSelect.setSelection(task.getDumpData().isDumpOtherInstance());
			otherInstanceSelect.setSelection(task.getDumpData().isDumpInstance());
			otherInstanceSelect.setEnabled(true);
			//IT-00-002対応
			int dumpThread = task.getDumpData().getDumpThreadType();
			switch(dumpThread){
			case DumpData.DUMP_ACTIVE_THREAD:
				this.curThreadSelect.setSelection(false);
				this.allThreadSelect.setSelection(false);
				this.setAllThreadPriority(false, null);
				this.runningThreadSelect.setSelection(true);
				this.setRunninThreadPriority(true, task.getDumpData().getThreadPriority());
				break;
			case DumpData.DUMP_ALL_THREAD:
				this.curThreadSelect.setSelection(false);
				this.allThreadSelect.setSelection(true);
				this.setAllThreadPriority(true, task.getDumpData().getThreadPriority());
				this.runningThreadSelect.setSelection(false);
				this.setRunninThreadPriority(false, null);
				break;
			case DumpData.DUMP_CURRENT_THREAD:
				this.curThreadSelect.setSelection(true);
				this.allThreadSelect.setSelection(false);
				this.setAllThreadPriority(false, null);
				this.runningThreadSelect.setSelection(false);
				this.setRunninThreadPriority(false, null);
				break;
			}
		}

	}

	/**
	 * レイアウトを生成
	 */
	private void createLayout(){

        //define Top group.
		topGroup = new Group(parent, SWT.NONE);
		GridLayout topGroupLayout = new GridLayout();
		topGroupLayout.marginTop = ViewerUtil.getMarginHeight(appWindow);
		topGroupLayout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
		topGroupLayout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
		topGroupLayout.marginRight = ViewerUtil.getMarginWidth(appWindow);
		topGroup.setLayout(topGroupLayout);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        gridData.widthHint = ViewerUtil.getConfigPlateWidth(parent);
        topGroup.setLayoutData(gridData);

        if (task.getTaskType()!=ITask.MONITOR_SIGNAL){
        	addThreadDumpArea();
        }

		//スタック階層定義のエリア
        addStackDeepInfoArea();
        addSpaceLine();

        //メソッド定義のエリア
        addStackMethodInfoArea();
        addSpaceLine();

        //ダンプするオブジェクト内容
        addDumpObjectInfo();
        addSpaceLine();

        //配列制限定義のエリア
        addLimitArrayListArea();

	}

	private void addSpaceLine(){
        spaceComposite = new Composite(topGroup, SWT.NONE);
        GridData spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spaceGridData.heightHint = ViewerUtil.getMarginHeight(appWindow);
        spaceComposite.setLayoutData(spaceGridData);
	}

	private void addThreadDumpArea(){
	    GridLayout layout;
	    GridData gridData;
		threadGroup = new Group(topGroup, SWT.NONE);
		threadGroup.setText(ApplicationResource.getResource("Config.Dump.Thread.Head"));
		layout = new GridLayout(4,false);
		threadGroup.setLayout(layout);
		gridData = new GridData(GridData.FILL_BOTH);
		threadGroup.setLayoutData(gridData);

		//カレンド・スレッド
	    curThreadSelect = new Button(threadGroup, SWT.RADIO);
	    curThreadSelect.setText(ApplicationResource.getResource("Config.Dump.Thread.CurThread"));
	    gridData = new GridData();
	    gridData.horizontalSpan=4;
	    curThreadSelect.setLayoutData(gridData);
	    curThreadSelect.setSelection(true);
	    //稼働中スレッド
	    runningThreadSelect = new Button(threadGroup, SWT.RADIO);
	    runningThreadSelect.setText(ApplicationResource.getResource("Config.Dump.Thread.RunningThread"));
	    runningThreadPrioritySelect = new Button(threadGroup, SWT.CHECK);
	    runningThreadPrioritySelect.setText(ApplicationResource.getResource("Config.Dump.Thread.ThreadPriority"));
	    runningThreadPriority = new ExcatText(threadGroup, SWT.BORDER | SWT.RIGHT);
	    gridData = new GridData();
	    gridData.widthHint=60;
	    runningThreadPriority.setLayoutData(gridData);
	    runningThreadPriorityLabel = new Label(threadGroup, SWT.NONE);
	    runningThreadPriorityLabel.setText(ApplicationResource.getResource("Config.Dump.Thread.Above"));

	    //全スレッド
	    allThreadSelect = new Button(threadGroup, SWT.RADIO);
	    allThreadSelect.setText(ApplicationResource.getResource("Config.Dump.Thread.AllThread"));
	    allThreadPrioritySelect = new Button(threadGroup, SWT.CHECK);
	    allThreadPrioritySelect.setText(ApplicationResource.getResource("Config.Dump.Thread.ThreadPriority"));
	    allThreadPriority = new ExcatText(threadGroup, SWT.BORDER | SWT.RIGHT);
	    gridData = new GridData();
	    gridData.widthHint=60;
	    allThreadPriority.setLayoutData(gridData);
	    allThreadPriorityLabel = new Label(threadGroup, SWT.NONE);
	    allThreadPriorityLabel.setText(ApplicationResource.getResource("Config.Dump.Thread.Above"));
	}

	private void addStackDeepInfoArea(){
		stackGroup = new Group(topGroup, SWT.NONE);
		stackGroup.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.Stack.Text"));
		GridLayout layout = new GridLayout();
		layout.marginLeft = ViewerUtil.getMarginWidth(parent);
		layout.marginRight = ViewerUtil.getMarginWidth(parent);
		layout.numColumns=2;
		stackGroup.setLayout(layout);
		stackGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		stackDepthLabel = new Label(stackGroup, SWT.NONE);
        stackDepthLabel.setText(
        		ApplicationResource.getResource("Tab.Task.DumpData.Stack.Max.Text"));
		stackDepth = new ExcatText(stackGroup, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		stackDepth.setLayoutData(new GridData(GridData.FILL_BOTH));
		stackDepth.setControlWidth(Text_Width);
	}

	private void addStackMethodInfoArea(){
        methodGroup = new Group(topGroup, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginLeft = ViewerUtil.getMarginWidth(parent);
		layout.marginRight = ViewerUtil.getMarginWidth(parent);
		methodGroup.setLayout(layout);
		methodGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		methodGroup.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.Method.Text")
				);

		//メソッドのthisオブジェクト
		thisObjectSelect = new Button(methodGroup, SWT.CHECK);
		thisObjectSelect.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.Method.ThisObject"));

		//メソッド引数
		methodArgumentSelect = new Button(methodGroup, SWT.CHECK);
		methodArgumentSelect.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.Method.Argument.Text"));

		//ローカル変数
		methodLoaclVarSelect = new Button(methodGroup, SWT.CHECK);
		methodLoaclVarSelect.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.Method.LocalVar.Text"));

		//他のインスタンス
		if (task.getTaskType()!=ITask.MONITOR_SIGNAL){
			otherInstanceSelect = new Button(methodGroup, SWT.CHECK);
			otherInstanceSelect.setText(
					ApplicationResource.getResource("Tab.Task.DumpData.instance.Text"));
		}
	}

	private void addDumpObjectInfo(){
		objectElementGroup = new Group(topGroup, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginLeft = ViewerUtil.getMarginWidth(parent);
		layout.marginRight = ViewerUtil.getMarginWidth(parent);
		objectElementGroup.setLayout(layout);
		objectElementGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		objectElementGroup.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.Object.Text"));
		publicSelect = new Button(objectElementGroup, SWT.CHECK);
		publicSelect.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.Object.Public.Text"));
		protectedSelect = new Button(objectElementGroup, SWT.CHECK);
		protectedSelect.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.Object.Protected.Text"));
		packageSelect = new Button(objectElementGroup, SWT.CHECK);
		packageSelect.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.Object.Package.Text"));
		privateSelect = new Button(objectElementGroup, SWT.CHECK);
		privateSelect.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.Object.Private.Text"));

		nestDepthGroup = new Group(objectElementGroup, SWT.NONE);
		GridLayout nestDepthGrouplayout = new GridLayout();
		nestDepthGrouplayout.numColumns = 2;
		layout.marginRight = ViewerUtil.getMarginWidth(parent);
		nestDepthGroup.setLayout(nestDepthGrouplayout);
		nestDepthGroup.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL ));
		objectNestDepthLabel = new Label(nestDepthGroup, SWT.NONE);
		objectNestDepthLabel.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.Object.NestDepth.Text"));
		objectNestDepth = new ExcatText(nestDepthGroup, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		objectNestDepth.setControlWidth(Text_Width);
	}

	private void addLimitArrayListArea(){
		listLimitGroup = new Group(topGroup, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginLeft = ViewerUtil.getMarginWidth(parent);
		layout.marginRight = ViewerUtil.getMarginWidth(parent);
		listLimitGroup.setLayout(layout);
		listLimitGroup.setLayoutData(new GridData(GridData.FILL_BOTH));
		listLimitGroup.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.ListLimit.Text"));

		objectElementLabel = new Label(listLimitGroup, SWT.NONE);
		objectElementLabel.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.ListLimit.Object.Text"));
		maxObjectElement = new ExcatText(listLimitGroup, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		maxObjectElement.setControlWidth(Text_Width);
		Label objectUnitLabel = new Label(listLimitGroup, SWT.NONE);
		objectUnitLabel.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.ListLimit.Unit.Text"));

		primitiveElementLabel = new Label(listLimitGroup, SWT.NONE);
		primitiveElementLabel.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.ListLimit.Primitive.Text"));
		maxPrimitiveElement = new ExcatText(listLimitGroup, SWT.RIGHT | SWT.SINGLE | SWT.BORDER);
		maxPrimitiveElement.setControlWidth(Text_Width);
		Label primitiveUnitLabel = new Label(listLimitGroup, SWT.NONE);
		primitiveUnitLabel.setText(
				ApplicationResource.getResource("Tab.Task.DumpData.ListLimit.Unit.Text"));
	}


	public void selectDumpStackBySignal(){
		stackDepth.setEnabled(true);
		methodArgumentSelect.setEnabled(true);
		methodLoaclVarSelect.setEnabled(true);
		thisObjectSelect.setEnabled(true);

		task.getDumpData().setDumpInstance(false);

		task.getDumpData().setDumpPublic(true);
		publicSelect.setEnabled(true);

		task.getDumpData().setDumpPrivate(true);
		privateSelect.setEnabled(true);

		task.getDumpData().setDumpProtected(true);
		protectedSelect.setEnabled(true);

		task.getDumpData().setDumpPackage(true);
		packageSelect.setEnabled(true);

		stackDepth.setBackground(white);
	}

	public void selectDumpObjectBySignal(){

		stackDepth.setEnabled(false);
		objectNestDepth.setEnabled(true);
		methodArgumentSelect.setEnabled(false);
		methodLoaclVarSelect.setEnabled(false);
		thisObjectSelect.setEnabled(false);

		task.getDumpData().setDumpInstance(true);

		task.getDumpData().setDumpPrivate(true);
		privateSelect.setEnabled(true);

		task.getDumpData().setDumpProtected(true);
		protectedSelect.setEnabled(true);

		task.getDumpData().setDumpPackage(true);
		packageSelect.setEnabled(true);

		stackDepth.setBackground(background);
	}

	public void selectDumpByException(){
		stackDepth.setEnabled(true);
		methodArgumentSelect.setEnabled(true);
		methodLoaclVarSelect.setEnabled(true);
		publicSelect.setEnabled(true);
		privateSelect.setEnabled(true);
		protectedSelect.setEnabled(true);
		packageSelect.setEnabled(true);

		stackDepth.setBackground(white);
	}

	/**
	 * add listeners
	 *
	 */
	private void addListeners(){
		if (task.getTaskType()!=ITask.MONITOR_SIGNAL){
			otherInstanceSelect.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					task.getDumpData().setDumpInstance(otherInstanceSelect.getSelection());
					ConfigModel.setChanged();
				}
			});
			curThreadSelect.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (curThreadSelect.getSelection()){
						task.getDumpData().setDumpThreadType(DumpData.DUMP_CURRENT_THREAD);
						task.getDumpData().setThreadPriority("");
						runningThreadSelect.setSelection(false);
						setRunninThreadPriority(false, null);
						allThreadSelect.setSelection(false);
						setAllThreadPriority(false,null);
					}
					ConfigModel.setChanged();
				}
			});
			runningThreadSelect.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (runningThreadSelect.getSelection()){
						setRunninThreadPriority(true, "");
						task.getDumpData().setThreadPriority("");
						task.getDumpData().setDumpThreadType(DumpData.DUMP_ACTIVE_THREAD);
						curThreadSelect.setSelection(false);
						allThreadSelect.setSelection(false);
						setAllThreadPriority(false,null);
					}
					ConfigModel.setChanged();
				}
			});
			allThreadSelect.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent e) {
					if (allThreadSelect.getSelection()){
						setAllThreadPriority(true,"");
						task.getDumpData().setThreadPriority("");
						task.getDumpData().setDumpThreadType(DumpData.DUMP_ALL_THREAD);
						curThreadSelect.setSelection(false);
						runningThreadSelect.setSelection(false);
						setRunninThreadPriority(false, null);
					}
					ConfigModel.setChanged();
				}
			});
			runningThreadPrioritySelect.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					if (runningThreadPrioritySelect.getSelection()){
						runningThreadPriority.setEnabled(true);
						runningThreadPriority.setBackground(white);
					}else{
						runningThreadPriority.setEnabled(false);
						runningThreadPriority.setText("");
						runningThreadPriority.setBackground(background);
					}
					ConfigModel.setChanged();
				}
			});
			allThreadPrioritySelect.addSelectionListener(new SelectionAdapter(){
				public void widgetSelected(SelectionEvent e) {
					if (allThreadPrioritySelect.getSelection()){
						allThreadPriority.setEnabled(true);
						allThreadPriority.setBackground(white);
					}else{
						allThreadPriority.setEnabled(false);
						allThreadPriority.setText("");
						allThreadPriority.setBackground(background);
					}
					ConfigModel.setChanged();
				}
			});
			allThreadPriority.addVerifyListener(
					new VerifyListener() {
						public void verifyText(VerifyEvent e) {
							e.doit=ViewerUtil.verifyNumbers(e.text);
						}
					});
			runningThreadPriority.addVerifyListener(
					new VerifyListener() {
						public void verifyText(VerifyEvent e) {
							e.doit=ViewerUtil.verifyNumbers(e.text);
						}
					});
			runningThreadPriority.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent arg0) {
					task.getDumpData().setThreadPriority(runningThreadPriority.getText());
					runningThreadPriority.setBackground(white);
					ConfigModel.setChanged();
				}
			});
			allThreadPriority.addModifyListener(new ModifyListener(){
				public void modifyText(ModifyEvent arg0) {
					task.getDumpData().setThreadPriority(allThreadPriority.getText());
					allThreadPriority.setBackground(white);
					ConfigModel.setChanged();
				}
			});
		}

		stackDepth.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				task.getDumpData().setStackTraceDepth(stackDepth.getText());
				stackDepth.setBackground(white);
				ConfigModel.setChanged();
			}
		});
		//障害 #443 this保存漏れ
		thisObjectSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				task.getDumpData().setDumpThis(thisObjectSelect.getSelection());
				ConfigModel.setChanged();
			}
		});
		methodArgumentSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				task.getDumpData().setDumpArgument(methodArgumentSelect.getSelection());
				ConfigModel.setChanged();
			}
		});
		methodLoaclVarSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				task.getDumpData().setDumpVariable(methodLoaclVarSelect.getSelection());
				ConfigModel.setChanged();
			}
		});

		stackDepth.addVerifyListener(
				new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						e.doit=ViewerUtil.verifyNumbers(e.text);
					}
				});

		objectNestDepth.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				task.getDumpData().setAttributeNestDepth(objectNestDepth.getText());
				objectNestDepth.setBackground(white);
				ConfigModel.setChanged();
			}
		});
		maxObjectElement.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				task.getDumpData().setMaxArrayElementForObject(maxObjectElement.getText());
				maxObjectElement.setBackground(white);
				ConfigModel.setChanged();
			}
		});
		maxPrimitiveElement.addModifyListener(new ModifyListener(){
			public void modifyText(ModifyEvent arg0) {
				task.getDumpData().setMaxArrayElementForPrimitive(maxPrimitiveElement.getText());
				maxPrimitiveElement.setBackground(white);
				ConfigModel.setChanged();
			}
		});

		publicSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				task.getDumpData().setDumpPublic(publicSelect.getSelection());
				ConfigModel.setChanged();
			}
		});
		privateSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				task.getDumpData().setDumpPrivate(privateSelect.getSelection());
				ConfigModel.setChanged();
			}
		});
		packageSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				task.getDumpData().setDumpPackage(packageSelect.getSelection());
				ConfigModel.setChanged();
			}
		});
		protectedSelect.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				task.getDumpData().setDumpProtected(protectedSelect.getSelection());
				ConfigModel.setChanged();
			}
		});

		objectNestDepth.addVerifyListener(
				new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						e.doit=ViewerUtil.verifyNumbers(e.text);
					}
				});
		maxObjectElement.addVerifyListener(
				new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						e.doit=ViewerUtil.verifyNumbers(e.text);
					}
				});
		maxPrimitiveElement.addVerifyListener(
				new VerifyListener() {
					public void verifyText(VerifyEvent e) {
						e.doit=ViewerUtil.verifyNumbers(e.text);
					}
				});
	}

	public boolean checkItems(){
		boolean result= true;
		if (runningThreadPrioritySelect != null &&
				runningThreadPrioritySelect.getVisible() &&
				runningThreadPrioritySelect.getSelection() &&
				!ViewerUtil.checkStringItem(runningThreadPriority.getText())){
			runningThreadPriority.setBackground(red);
			result=false;
		}
		if (allThreadPrioritySelect != null &&
				allThreadPrioritySelect.getVisible() &&
				allThreadPrioritySelect.getSelection() &&
				!ViewerUtil.checkStringItem(allThreadPriority.getText())){
			allThreadPriority.setBackground(red);
			result=false;
		}
		if (stackDepth.getEnabled()){
			if (!ViewerUtil.isLargerThanMinValue(stackDepth.getText(),0)){
				result=false;
				stackDepth.setBackground(red);
			}
		}
		if (!ViewerUtil.isLargerThanMinValue(objectNestDepth.getText(),0)){
			result=false;
			objectNestDepth.setBackground(red);
		}
		if (!ViewerUtil.isLargerThanMinValue(maxObjectElement.getText(),0)){
			result=false;
			maxObjectElement.setBackground(red);
		}
		if (!ViewerUtil.isLargerThanMinValue(maxPrimitiveElement.getText(),0)){
			result=false;
			maxPrimitiveElement.setBackground(red);
		}
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

	private void setAllThreadPriority(boolean flag, String priority){
		this.allThreadPrioritySelect.setVisible(flag);
		this.allThreadPriority.setVisible(flag);
		this.allThreadPriorityLabel.setVisible(flag);
		if (flag){
			this.allThreadPriority.setText(priority);
			if (priority == null || "".equals(priority)){
				this.allThreadPrioritySelect.setSelection(false);
				this.allThreadPriority.setBackground(background);
				this.allThreadPriority.setEnabled(false);
			}else{
				this.allThreadPrioritySelect.setSelection(true);
				this.allThreadPriority.setBackground(white);
				this.allThreadPriority.setEnabled(true);
			}
		}
	}

	private void setRunninThreadPriority(boolean flag, String priority){
		this.runningThreadPrioritySelect.setVisible(flag);
		this.runningThreadPriority.setVisible(flag);
		this.runningThreadPriorityLabel.setVisible(flag);
		if (flag){
			this.runningThreadPriority.setText(priority);
			if (priority == null || "".equals(priority)){
				this.runningThreadPrioritySelect.setSelection(false);
				this.runningThreadPriority.setBackground(background);
				this.runningThreadPriority.setEnabled(false);
			}else{
				this.runningThreadPrioritySelect.setSelection(true);
				this.runningThreadPriority.setBackground(white);
				this.runningThreadPriority.setEnabled(true);
			}
		}
	}
}
