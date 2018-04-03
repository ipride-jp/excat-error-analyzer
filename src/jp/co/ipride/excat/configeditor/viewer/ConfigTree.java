package jp.co.ipride.excat.configeditor.viewer;

import java.util.regex.Pattern;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.common.DumpDocument;
import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.task.ITask;
import jp.co.ipride.excat.configeditor.viewer.action.SetActiveAction;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

/**
 * 左の編集ツリー
 * @author tu-ipride
 * @version v3
 * @date 2009/9/5
 */
public class ConfigTree {

	static {
		System.getProperties().setProperty(
				"javax.xml.parsers.DocumentBuilderFactory",
				"org.apache.xerces.jaxp.DocumentBuilderFactoryImpl");
	}

	private MainViewer appWindow = null;
	private TreeViewer treeViewer = null;

	//document of viewer.
	private Object object = null;
	private Composite parent = null;

	/**
	 * construct
	 *
	 * @param appWindow
	 * @param parent
	 * @param style
	 */
	public ConfigTree(MainViewer appWindow, Composite parent, int style) {
		this.appWindow = appWindow;
		this.parent = parent;
		treeViewer = new TreeViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		treeViewer.setContentProvider(new ConfigContentProvider());
		treeViewer.setLabelProvider(new configLabelProvider(this.appWindow));
		treeViewer.setInput("root");

		createPopupMenu();
	}


	public void refrash(){
		treeViewer.refresh();
		treeViewer.expandAll();
	}

	public Tree getTree(){
		return this.treeViewer.getTree();
	}

	public TreeItem getSelectItem(){
		return (TreeItem)this.treeViewer.getTree().getSelection()[0];
	}

	/**
	 * ダンプファイル（XML）を読み込んでTreeにセットする。
	 * @param path　ダンプファイルのパース（XML）
	 */
	public boolean  setXmlFile(String path) throws Exception{
		if (path == null) {
			return false;
		}
		boolean ret = DumpDocument.createDocument(path);
		if(ret){
			object = DumpDocument.getDocument();
			treeViewer.setInput(object);
			appWindow.setPdfPrintState(true);
		}

		return ret;
	}

	/**
	 * add a listner to tree.
	 * @param listener
	 */
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		treeViewer.addSelectionChangedListener(listener);
	}

	public void addDoubleClickListener(IDoubleClickListener listener){
		treeViewer.addDoubleClickListener(listener);
	}

	/**
	 * configTreeのポップアップメニューを生成するメソッド
	 */
	protected void createPopupMenu(){
		// ポップアップメニューを生成
		MenuManager mm = new MenuManager("#PopupMenu");
		mm.setRemoveAllWhenShown(true);
		mm.addMenuListener(new IMenuListener(){
			public void menuAboutToShow(IMenuManager manager) {
				Tree tree = ConfigTree.this.treeViewer.getTree();
				if(tree.getSelectionCount() == 1) {
					if(isTaskRootNode(tree.getSelection()[0].getData())) {
						manager.add(appWindow.addAutoMonitorExceptionTaskAction);
						manager.add(appWindow.addMonitorExceptionTaskAction);
						manager.add(appWindow.addMonitorMethodTaskAction);
						manager.add(appWindow.addMonitorSignalTaskAction);
					} else if (isTaskNode(tree.getSelection()[0].getData())) {
						manager.add(appWindow.deleteTaskAction);
					}
				}
			}
		});

		Menu menu = mm.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);
	}

	protected boolean isTaskRootNode(Object node){
		if (node != null && node.toString().equals(ConfigContant.Tree_Item_TaskRegister_Root)){
			return true;
		}
		return false;
	}

	protected boolean isTaskNode(Object node){
		if (node instanceof ITask){
			return true;
		}
		return false;
	}

	public void selectMonitorTask(ITask task){
		TreeItem[] items = treeViewer.getTree().getItem(1).getItems();
		for (int i=0; i<items.length; i++){
			Object data = items[i].getData();
			if (data instanceof ITask){
				ITask itemTask = (ITask)data;
				if (task.getIdentfyKey().equals(itemTask.getIdentfyKey())){
					treeViewer.getTree().setSelection(items[i]);
					return;
				}
			}
		}
	}

	public TreeViewer getTreeViewer() {
		return treeViewer;
	}
}
