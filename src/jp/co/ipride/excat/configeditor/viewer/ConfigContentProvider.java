package jp.co.ipride.excat.configeditor.viewer;

import java.util.Vector;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.task.ITask;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

/**
 *
 * @author tu-ipride
 *
 */
public class ConfigContentProvider implements ITreeContentProvider{

	/**
	 * 子ノードの配列を返す
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
		if (ConfigContant.Tree_Item_TaskRegister_Root.equals(parentElement)){
			Vector tasks = ConfigModel.getTaskList().getTasks();
			return tasks.toArray();
		}else{
			return new String[]{};
		}
	}

	/**
	 * 親ノードを返す。
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
	    return null;
	}

	/**
	 * 子ノードを持つかどうか
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
		if (element instanceof ITask){
			return false;
		}
		if (ConfigContant.Tree_Item_TaskRegister_Root.equals(element)){
			int count =ConfigModel.getTaskList().getTasks().size();
			if (count ==0){
				return false;
			}else{
				return true;
			}
		}
		return false;
	}

	/**
	 * 子ノードの要素を取得
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		String[] children = null;
		if ("root".equals(inputElement)){
			children = new String[]{
					ConfigContant.Tree_Item_BaseInfo,
					ConfigContant.Tree_Item_TaskRegister_Root,
					ConfigContant.Tree_Item_Object_Register,
					ConfigContant.Tree_Item_Template_Register
			};
			return children;
		}else if (ConfigContant.Tree_Item_TaskRegister_Root.equals(inputElement)){
			Vector<ITask> tasks = ConfigModel.getTaskList().getTasks();
			return tasks.toArray();
		}

         return new String[]{};
	}

	/**
	 * 実装しない。
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}

	/**
	 * 実装しない。
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}

}
