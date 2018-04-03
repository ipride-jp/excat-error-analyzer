package jp.co.ipride.excat.configeditor.viewer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.configeditor.viewer.baseinfo.BaseInfoForm;
import jp.co.ipride.excat.configeditor.viewer.instance.ObjectRegisterForm;
import jp.co.ipride.excat.configeditor.viewer.task.TaskRegisterForm;
import jp.co.ipride.excat.configeditor.viewer.template.TemplateRegisterForm;
import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.task.ITask;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 主画面のフレーム
 * @author tu
 * @since 2007/11/10
 *
 */
public class ConfigMainForm{

	private MainViewer mainViewer;  //MainViewer
	private CTabFolder topTabFolder;  //「設定」、「分析」のタブ
	private CTabItem configTabItem = null; //「設定」のアイテム

	private int[] weights = new int[] { 2, 8 };

	private SashForm configForm;  	//leftForm and rightFormをマージする
	private SashForm leftForm;   	//cfgtreeのベース・フォーム
	private SashForm rightForm;  	//各タスクの編集フォーム
	public CTabFolder configEditFolder = null; //各タスクの編集フォームを管理する。


	public ConfigTree configTree = null;

	private List<TaskRegisterForm> taskFormList = new ArrayList<TaskRegisterForm>();
	private BaseInfoForm basicInfoForm = null;
	private ObjectRegisterForm objectRegisterForm = null;
	private TemplateRegisterForm templeRegisterForm = null;

	/**
	 * コンストラクタ
	 * @param appWindow
	 */
	public ConfigMainForm(MainViewer view,CTabFolder tabFolder){
		this.mainViewer = view;
		this.topTabFolder = tabFolder;
		this.configTabItem = new CTabItem(tabFolder, SWT.NULL, 0);
		this.configTabItem.setText(ApplicationResource.getResource("tab.config.Title"));
		URL url = MainViewer.class.getResource(IconFilePathConstant.TAB_CONFIG);
		configTabItem.setImage(ImageDescriptor.createFromURL(url).createImage());

		configForm = new SashForm(this.topTabFolder, SWT.NONE);
		this.configTabItem.setControl(configForm);
		leftForm = new SashForm(configForm, SWT.NONE);
		rightForm = new SashForm(configForm, SWT.NONE);
		configEditFolder = new CTabFolder(rightForm,SWT.BORDER );
		createBaseForm();
	}

	private void createBaseForm(){
		leftForm.setOrientation(SWT.VERTICAL);
		configForm.setWeights(weights);
		configEditFolder.setTabHeight(0);
		configEditFolder.setSimple(false);
		configEditFolder.marginHeight = 0;
		configEditFolder.marginWidth = 0;

		configTree = new ConfigTree(this.mainViewer, leftForm, SWT.BORDER);

		configTree.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				String treeNodeIdentfy="";
				mainViewer.deleteTaskAction.setEnabled(false);

				if(event.getSelection().isEmpty()){
					mainViewer.deleteTaskAction.setEnabled(false);
					return;
				}
				TreeItem item = (TreeItem)configTree.getTree().getSelection()[0];
				if (item.getData() instanceof String){
					treeNodeIdentfy = (String)item.getData();
				}else if(item.getData() instanceof ITask){
					//障害 #440 対応 begin
					mainViewer.deleteTaskAction.setEnabled(true);
					//障害 #440 対応 end
					treeNodeIdentfy=((ITask)item.getData()).getIdentfyKey();
				}
				CTabItem[] items = configEditFolder.getItems();
				//look out edit form using identfy.
				for (int j = 0; j < items.length; j++) {
					if (treeNodeIdentfy.equals(items[j].getData().toString())){
						configEditFolder.setSelection(items[j]);
						break;
					}
				}
			}
		});
	}

	/**
	 * タブ・フォルダーにリスナーを追加
	 * @param listener
	 */
	public void addSelectionListenerToTabFolder(SelectionListener listener){
		topTabFolder.addSelectionListener(listener);
	}

	/**
	 * 新規コンフィグを作成。
	 * (メニューアクションから読み出す)
	 */
	public void createNewConfig(){
		moveAll();
		basicInfoForm = new BaseInfoForm(rightForm, configEditFolder);
		templeRegisterForm = new TemplateRegisterForm(rightForm, configEditFolder);
		objectRegisterForm = new ObjectRegisterForm(rightForm, configEditFolder);
		topTabFolder.setSelection(0);
	}

	/**
	 * 新規タスクを追加
	 * (メニューアクションから読み出す)
	 */
	public void addNewTask(int taskType){
		//タスク・フォームの追加
		TaskRegisterForm newTaskForm =
			new TaskRegisterForm(rightForm, configEditFolder, taskType);
		taskFormList.add(newTaskForm);
		configTree.refrash();
		configTree.selectMonitorTask(newTaskForm.getTask());
		configEditFolder.setSelection(newTaskForm.getTabItem());
	}



	/**
	 * 既存コンフィグを編集
	 * (メニューアクションから読み出す)
	 */
	public void createOldConfig(){
		moveAll();
		basicInfoForm = new BaseInfoForm(rightForm, configEditFolder);
		templeRegisterForm = new TemplateRegisterForm(rightForm, configEditFolder);
		objectRegisterForm = new ObjectRegisterForm(rightForm, configEditFolder);

		Vector<ITask> tasks = ConfigModel.getTaskList().getTasks();
		for (int i=0; i<tasks.size(); i++){
			TaskRegisterForm taskForm = new TaskRegisterForm(rightForm, configEditFolder, tasks.get(i));
			taskFormList.add(taskForm);
		}
		configTree.refrash();
	}

	/**
	 * 現在のタスクを削除する。
	 * @data 2009/9/13
	 */
	public void deleteCurrentTask(){
		TreeItem item = configTree.getSelectItem();
		Object data = item.getData();
		if (data instanceof ITask){
			ITask task = (ITask)data;
			ConfigModel.getTaskList().removeTask(task);
			String key = task.getIdentfyKey();
			for (int index = 0; index < taskFormList.size(); index++){
				TaskRegisterForm taskForm = (TaskRegisterForm)taskFormList.get(index);
				if (taskForm.isThis(key)){
					taskFormList.remove(index);
					configEditFolder.setSelection(0);
					configTree.refrash();
					ConfigModel.setChanged();
					break;
				}
			}
		}
	}

	public boolean hasTask(){
		if (taskFormList.isEmpty()){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 現時点表示されているTabはタスクかを確認する。
	 * @return
	 */
	public boolean canDeleteThisTask(){
		Object data = configTree.getSelectItem().getData();
		if (data instanceof ITask){
			return false;
		}else{
			return true;
		}
	}

	/**
	 * 基本設定からのクローズ通知
	 *
	 */
	public void closeMailNotice(){
		for (int i=0; i<taskFormList.size(); i++){
			TaskRegisterForm task = (TaskRegisterForm)taskFormList.get(i);
			task.closeMailNotice();
		}
	}

	/**
	 * 保存前の項目チェック
	 * 注意：ターゲットの項目チェックは登録時にチェックしているため、
	 * ここでは、チェックしていない。
	 * @return
	 */
	public boolean checkItems(){
		boolean result= true;
		//単画面のチェック
		result = basicInfoForm.checkItems();
		if (!result){
			//topTabFolder.setSelection(0);
			configTree.getTreeViewer().setSelection(
					new StructuredSelection(ConfigContant.Tree_Item_BaseInfo));
			return result;
		}

		for (int i=0; i<taskFormList.size(); i++){
			TaskRegisterForm taskForm = (TaskRegisterForm)taskFormList.get(i);
			result = taskForm.checkItems();
			if (!result){
				//topTabFolder.setSelection(i+1);
				configTree.selectMonitorTask(taskForm.getTask());
				configEditFolder.setSelection(taskForm.getTabItem());
				if (taskFormList.size()>1){
					//appWindow.setDeleteTaskAction(true);
				}
				return result;
			}
		}

		result = objectRegisterForm.checkItems();
		if (!result){
			//topTabFolder.setSelection(taskFormList.size() + 2);
			configTree.getTreeViewer().setSelection(
					new StructuredSelection(ConfigContant.Tree_Item_Object_Register));
			return result;
		}

		result=templeRegisterForm.checkItems();
		if (!result){
			//topTabFolder.setSelection(taskFormList.size() + 1);
			configTree.getTreeViewer().setSelection(
					new StructuredSelection(ConfigContant.Tree_Item_Template_Register));
			return result;
		}

		//画面関連チェック（タスク間）
		//既に登録時にチェックできた。

		return result;
	}

	public void moveAll(){

//IT-00-001対応		ConfigModel.getTaskList().removeAll();
		CTabItem[] items = configEditFolder.getItems();
		for (int i=0; i<items.length; i++){
			items[i].dispose();
		}
		taskFormList.clear();
		refrash();
	}

	/**
	 * タスクの有効フラグを切り替える処理
	 * @date 2009/9/12
	 */
	public void refrash(){
		configTree.refrash();
	}

	public void selectConfig(){
		topTabFolder.setSelection(configTabItem);
	}
}