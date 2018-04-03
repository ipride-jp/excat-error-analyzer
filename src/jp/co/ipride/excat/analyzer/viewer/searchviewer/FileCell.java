package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import java.net.URL;
import java.util.List;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

/**
 * a dump file for tree view.
 * @author tu-ipride
 *
 */
public class FileCell implements ICell{

	private MainViewer mview;

	private ConditionCell parent;

	private static Image image_file = null;

	private String path;

	private List<MethodCell> methodList;

	static{
		URL url = MainViewer.class.getResource(IconFilePathConstant.FILE);
		image_file = ImageDescriptor.createFromURL(url).createImage();
	}

	public FileCell(MainViewer mview){
		this.mview=mview;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<MethodCell> getMethodList() {
		return methodList;
	}

	public void setMethodList(List<MethodCell> methodList) {
		this.methodList = methodList;
	}

	public void setParent(ConditionCell parent){
		this.parent = parent;
	}

	public Object[] getChildren() {
		MethodCell[] methodArray = new MethodCell[methodList.size()];
		for (int i=0; i<methodList.size(); i++){
			methodArray[i] = methodList.get(i);
		}
		return methodArray;
	}

	public Image getImage() {
		return image_file;
	}

	public Object getParent() {
		return parent;
	}

	public String getText() {
		return path;
	}

	public boolean hasChildren() {
		if (methodList != null && methodList.size()>0){
			return true;
		}else{
			return false;
		}
	}

	public TreeItem selectItem(){
		try{
			String stackViewPath =mview.analyzerform.stackTree.getPath();
			if (stackViewPath == null || !path.equals(stackViewPath)){
				mview.openXmlFile(this.path);
			}
			TreeViewer treeViewer = mview.analyzerform.stackTree.getTreeViewer();
			treeViewer.expandToLevel(3);

			for (MethodCell cell: methodList){
				cell.setStackTreeViewer(treeViewer);
			}
			//[dump]tag
			TreeItem dumpItem = treeViewer.getTree().getItem(0);
			//[Thread]tag
			TreeItem threadItem = dumpItem.getItem(0);
			//return thread tag.
			return threadItem;
		}catch(Exception e){
			ExcatMessageUtilty.showErrorMessage(mview.getShell(), e);
			HelperFunc.getLogger().error("FileCell", e);
			return null;
		}
	}
}
