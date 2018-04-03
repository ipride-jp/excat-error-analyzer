package jp.co.ipride.excat.configeditor.viewer.task;

import java.util.Vector;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.task.AutoMonitorExceptionTask;
import jp.co.ipride.excat.configeditor.model.task.FilterException;
import jp.co.ipride.excat.configeditor.model.task.Place;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.configeditor.viewer.task.dialog.AutoMonitorPathRegisterDlg;
import jp.co.ipride.excat.configeditor.viewer.task.dialog.ExceptionFilterRegisterDlg;

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
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * 例外自動監視タスク
 * @author wang
 * @since 2009/07/20
 */
public class AutoMonitorExceptionTaskForm extends TaskCommonForm{

	//監視対象（パッケージ）のボタン
	private Button addPlaceButton;
	private Button editPlaceButton;
	private Button deletePlaceButton;

	//除外する例外のボタン
	private Button addFilterButton;
	private Button editFilterButton;
	private Button deleteFilterButton;

	//監視パッケージ
	private Table placeTable;
	//除外例外
	private Table filterTable;
	private Button filterCheck;

	private Vector<Place> packageList = null;
	private Vector<FilterException> filters = null;

	/**
	 * コンストラクタ
	 * @param appWindow MainView
	 * @param form form
	 * @param taskForm
	 */
	public AutoMonitorExceptionTaskForm(SashForm parentForm, Composite form, ITaskForm taskForm){
		this.parentForm = parentForm;
		this.task = taskForm.getTask();
		this.red = parentForm.getShell().getDisplay().getSystemColor(SWT.COLOR_RED);
		this.white = parentForm.getShell().getDisplay().getSystemColor(SWT.COLOR_WHITE);
		this.background = parentForm.getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND);
		this.taskForm = taskForm;
		getColors(parentForm);

		parent = new Composite(form,SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(parentForm);
		layout.marginBottom = ViewerUtil.getMarginHeight(parentForm);
		layout.marginLeft = ViewerUtil.getMarginWidth(parentForm);
		layout.marginRight = ViewerUtil.getMarginWidth(parentForm);
		layout.numColumns = 1;
        GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
        parent.setLayoutData(gridData);
		parent.setLayout(layout);

		createLayout();
		addListeners(parent);
		init();
	}

	private void init(){
		addPlaceButton.setEnabled(true);
		editPlaceButton.setEnabled(false);
		deletePlaceButton.setEnabled(false);
		addFilterButton.setEnabled(false);
		editFilterButton.setEnabled(false);
		deleteFilterButton.setEnabled(false);
		filterCheck.setEnabled(true);

		packageList = ((AutoMonitorExceptionTask)task).getMonitoringPackage();
		filters = ((AutoMonitorExceptionTask)task).getFilterExceptions();

		updateDataToDisplay();
	}

	private void createLayout(){

        //監視タスクのエリア
		Group group = new Group(parent, SWT.NONE);
		group.setText(
        		ApplicationResource.getResource("Config.Task.AutoMonitor"));
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(parentForm);
		layout.marginBottom = ViewerUtil.getMarginHeight(parentForm);
		layout.marginLeft = ViewerUtil.getMarginWidth(parentForm);
		layout.marginRight = ViewerUtil.getMarginWidth(parentForm);
		group.setLayout(layout);
        GridData gridData = new GridData();
		gridData.horizontalAlignment=SWT.BEGINNING;
        gridData.widthHint = ViewerUtil.getConfigPlateWidth(parentForm);
        group.setLayoutData(gridData);

        //--------------------Effect  --------
        addBlankSpace(group);
        addEffectArea(group);
        addBlankSpace(group);

        //--------------------header  --------
        addHeaderArea(group);
        addBlankSpace(group);
        //--------------------table 1 --------
		addPackageTable(group);
        addBlankSpace(group);
	    //--------------------table 2 --------
        addFilterExceptionTable(group);
        addBlankSpace(group);
        //-------------Prefix-----------------
        addPrefixArea(group);
        addBlankSpace(group);
        //-------------Comment----------------
	    addRemarkArea(group);
        addBlankSpace(group);
	}



	private void addPackageTable(Composite composite){
        Group packageGroup = new Group(composite, SWT.NONE);
        packageGroup.setText(
        		ApplicationResource.getResource("Config.Task.AutoMonitor.MonitorApp"));
		GridLayout packageGrouplayout = new GridLayout();
		packageGrouplayout.numColumns = 1;
		packageGrouplayout.marginTop = ViewerUtil.getMarginHeight(composite);
		packageGrouplayout.marginBottom = ViewerUtil.getMarginHeight(composite);
		packageGrouplayout.marginLeft = ViewerUtil.getMarginWidth(composite);
		packageGrouplayout.marginRight = ViewerUtil.getMarginWidth(composite);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.heightHint = Tabel_High;
		packageGroup.setLayout(packageGrouplayout);
		packageGroup.setLayoutData(gridData);

		placeTable = new Table(packageGroup, SWT.BORDER | SWT.CHECK | SWT.V_SCROLL | SWT.H_SCROLL);
        gridData = new GridData(GridData.FILL_BOTH);
		placeTable.setLayoutData(gridData);

	    //button
		Composite buttonForm = new Composite(packageGroup,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonFormlayout.numColumns = 3;
		buttonFormlayout.marginHeight = 1;
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonForm.setLayoutData(gd);

		addPlaceButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Config.Button.Add"),
				Utility.BUTTON_WIDTH,1);

		editPlaceButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Config.Button.Edit"),
				Utility.BUTTON_WIDTH,1);

		deletePlaceButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Config.Button.Delete"),
				Utility.BUTTON_WIDTH,1);
	}

	private void addFilterExceptionTable(Composite composite){
        Group filterGroup = new Group(composite, SWT.NONE);
        filterGroup.setText(
        		ApplicationResource.getResource("Tab.Task.SelectException.ExcludeException")
        	);
		GridLayout filterGrouplayout = new GridLayout();
		filterGrouplayout.numColumns = 1;
		filterGrouplayout.marginTop = ViewerUtil.getMarginHeight(composite);
		filterGrouplayout.marginBottom = ViewerUtil.getMarginHeight(composite);
		filterGrouplayout.marginLeft = ViewerUtil.getMarginWidth(composite);
		filterGrouplayout.marginRight = ViewerUtil.getMarginWidth(composite);
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.heightHint = Tabel_High;
		filterGroup.setLayout(filterGrouplayout);
		filterGroup.setLayoutData(gridData);

		filterCheck = new Button(filterGroup, SWT.CHECK);
		filterTable = new Table(filterGroup, SWT.BORDER | SWT.CHECK);
        gridData = new GridData(GridData.FILL_BOTH);
		filterTable.setLayoutData(gridData);

		//button
		Composite buttonForm = new Composite(filterGroup,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonFormlayout.numColumns=3;
		buttonFormlayout.marginHeight=1;
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonForm.setLayoutData(gd);

		addFilterButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.add"),
				Utility.BUTTON_WIDTH,1);

		editFilterButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.edit"),
				Utility.BUTTON_WIDTH,1);

		deleteFilterButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Task.Button.delete"),
				Utility.BUTTON_WIDTH,1);

	}

	private void addListeners(Composite composite){

		//監視エリアの「追加」ボタン
		addPlaceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				AutoMonitorPathRegisterDlg dialog = new AutoMonitorPathRegisterDlg(parentForm.getShell());
				Place place = new Place();
				dialog.init(place, packageList);
				int r= dialog.open();
				if (r==Dialog.OK){
					packageList.add(place);
					updatePlaces();
				}
			}
		});

		//監視エリアの「編集」ボタン
		editPlaceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index =placeTable.getSelectionIndex();
				if (index>=0){
					AutoMonitorPathRegisterDlg dialog = new AutoMonitorPathRegisterDlg(parentForm.getShell());
					Place place = packageList.get(index);
					Place temp = new Place();
					place.copyTo(temp);
					dialog.init(temp, packageList);
					int r= dialog.open();
					if (r==Dialog.OK){
						temp.copyTo(place);
						updatePlaces();
					}
				}
			}
		});
		//監視エリアの削除ボタン
		deletePlaceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = placeTable.getSelectionIndex();
				if (index >=0){
					placeTable.remove(index);
					packageList.remove(index);
					ConfigModel.setChanged();
				}
				//if (placeTable.getItemCount()==0){
				editPlaceButton.setEnabled(false);
				deletePlaceButton.setEnabled(false);
				//}
			}
		});

		//除外対象編集の「許可」を選択する
		filterCheck.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (filterCheck.getSelection()){
					filterTable.setBackground(white);
					filterTable.setEnabled(true);
					addFilterButton.setEnabled(true);
				}else{
					filterTable.setBackground(background);
					filterTable.setEnabled(false);
					addFilterButton.setEnabled(false);
					editFilterButton.setEnabled(false);
					deleteFilterButton.setEnabled(false);
					filters.clear();
					updateFilters();
				}
				ConfigModel.setChanged();
			}
		});

		//除外対象編集Tableの対象を選択する
		filterTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = (TableItem)e.item;
				FilterException monitor = (FilterException)item.getData();
				monitor.setUse(item.getChecked());
				editFilterButton.setEnabled(true);
				deleteFilterButton.setEnabled(true);
			}
		});

		//除外対象の「追加」ボタン
		addFilterButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				ExceptionFilterRegisterDlg dialog = new ExceptionFilterRegisterDlg(parent.getShell());
				FilterException template = new FilterException();
				dialog.setFilterExceptionList(filters);
				dialog.init(template, "java.lang.Throwable");
				int r = dialog.open();
				if (r==Dialog.OK){
					filters.add(filters.size(), template);
					//updateDataToDisplay();
					updateFilters();
				}
			}
		});
		//除外対象の「編集」ボタン
		editFilterButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index =filterTable.getSelectionIndex();
				if (index>=0){
					ExceptionFilterRegisterDlg dialog = new ExceptionFilterRegisterDlg(parent.getShell());
					FilterException filter = (FilterException)filters.get(index);
					FilterException template = new FilterException();
					filter.copyTo(template);
					dialog.init(template,"java.lang.Throwable");
					dialog.setFilterExceptionList(filters);
					int r= dialog.open();
					if (r==Dialog.OK){
						template.copyTo(filter);
						//updateDataToDisplay();
						updateFilters();
					}
				}
			}
		});

		//除外対象の削除ボタン
		deleteFilterButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				int index = filterTable.getSelectionIndex();
				if (index >=0){
					filterTable.remove(index);
					filters.remove(index);
					ConfigModel.setChanged();
				}
				//if (filterTable.getItemCount()==0){
					editFilterButton.setEnabled(false);
					deleteFilterButton.setEnabled(false);
				//}
			}
		});

		placeTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				TableItem item = (TableItem)e.item;
				Place place = (Place)item.getData();
				place.setUse(item.getChecked());
				editPlaceButton.setEnabled(true);
				deletePlaceButton.setEnabled(true);
			}
		});

		addEffectListener();

		addCommentListener();

		addPreFixListener();

		addTaskNameListener();

	}

	private void updateDataToDisplay(){
		//初期化
		updatePlaces();

		updateCommonItems();

		updateFilters();

	}

	private void updatePlaces() {
		placeTable.removeAll();
		placeTable.setBackground(white);

		//データの反映

		for (int i=0; i<packageList.size(); i++){
			Place place=packageList.get(i);
			if (!"".equals(place.getClassName())){
				TableItem item = new TableItem(placeTable, SWT.NONE);
				item.setText(new String[] {place.getClassName()});
				item.setData(place);
				item.setChecked(place.isUse());
			}
		}
	}

	private void updateFilters() {
		filterTable.removeAll();
		filterTable.setBackground(background);
		addFilterButton.setEnabled(false);
		editFilterButton.setEnabled(false);
		deleteFilterButton.setEnabled(false);

		if (filters.size()>0){
			filterTable.setBackground(white);
			filterCheck.setSelection(true);
			addFilterButton.setEnabled(true);
			for (int i=0; i<filters.size(); i++){
				FilterException monitor=(FilterException)filters.get(i);
				if (!"".equals(monitor.getExcludeClassName())){
					TableItem item = new TableItem(filterTable, SWT.NONE);
					item.setText(new String[] {monitor.getExcludeClassName()});
					item.setData(monitor);
					item.setChecked(monitor.isUse());
				}
			}
		}
	}

	@Override
	public boolean checkItems() {
		boolean result = true;
		if (packageList.size() == 0) {
			placeTable.setBackground(red);
			result = false;
		}
		if (filterCheck.getSelection() && filters.size() == 0) {
			filterTable.setBackground(red);
			result = false;
		}

		if (!super.checkItems()) {
			result = false;
		}

		return result;
	}
}
