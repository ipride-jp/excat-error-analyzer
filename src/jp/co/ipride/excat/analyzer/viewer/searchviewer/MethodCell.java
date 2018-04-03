package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

/**
 * XMLを持たない軽量オブジェクト
 * @author tu-ipride
 *
 */
public class MethodCell implements ICell{

	private static Image methodImage;

	private TreeViewer stackTreeViewer = null;

	private FileCell parent;

	private int sequence;

	private String methodName;

	private List<ObjectCell> localVarArray = new ArrayList<ObjectCell>();

	static{
		URL url = MainViewer.class.getResource(IconFilePathConstant.TREE_METHOD);
		methodImage = ImageDescriptor.createFromURL(url).createImage();
	}

	/**
	 * construct
	 */
	public MethodCell( MethodUnit methodUnit){
		this.sequence = methodUnit.getSequence();
		this.methodName = methodUnit.methodName();
	}

	public void setStackTreeViewer(TreeViewer stackTreeViewer){
		this.stackTreeViewer=stackTreeViewer;
		for (ObjectCell cell: localVarArray){
			cell.setStackTreeViewer(stackTreeViewer);
		}
	}

	public int getSequence() {
		return sequence;
	}

	public void setSequence(int sequence) {
		this.sequence = sequence;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public List<ObjectCell> getLocalVarArray() {
		return localVarArray;
	}

	public void setLocalVarArray(List<ObjectCell> localVarArray) {
		this.localVarArray = localVarArray;
		for (ObjectCell cell: localVarArray){
			cell.setParent(this);
		}
	}

	public void addChild(ObjectCell objectCell){
		objectCell.setParent(this);
		localVarArray.add(objectCell);
	}

	public void setParent(FileCell parent){
		this.parent=parent;
	}

	public Object[] getChildren() {
		ObjectCell[] objectCellArray = new ObjectCell[localVarArray.size()];
		for (int i=0; i<localVarArray.size(); i++){
			objectCellArray[i] = localVarArray.get(i);
		}
		return objectCellArray;
	}

	public Image getImage() {
		return methodImage;
	}

	public Object getParent() {
		return parent;
	}

	public String getText() {
		return methodName;
	}

	public boolean hasChildren() {
		if (localVarArray.size()>0){
			return true;
		}else{
			return false;
		}
	}

	public TreeItem selectItem(){
		TreeItem threadItem = parent.selectItem();
		if (threadItem == null){
			HelperFunc.getLogger().debug("failure: threadItem is null");
			return null;
		}
		stackTreeViewer.expandToLevel(threadItem.getData(),1);
		TreeItem methodItem = threadItem.getItem(sequence);
		//stackTreeViewer.getTree().setSelection(methodItem);
		stackTreeViewer.setSelection(new StructuredSelection(methodItem.getData()));
		return methodItem;
	}

}
