package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

import org.w3c.dom.Element;

/**
 * XMLを持たない軽量オブジェクト
 * @author tu-ipride
 *
 */
public class ObjectCell implements ICell{

	private static final String BRACKET_RIGHT = "]";

	private static final String BRACKET_LEFT = "[";

	private TreeViewer stackTreeViewer = null;

	private ICell parent=null;

	private String varName=null;

	private String definteType=null;

	private String realType=null;

	private String value=null;

	private String objectId=null;

	private List<ObjectCell> referenceArray = new ArrayList<ObjectCell>();

	private String tagName=null;

	private static Image ArgementImage=null;

	private static Image VariantImage=null;

	private static Image SuperClassImage=null;

	private static Image ThisImage=null;

	private static Image AttributeImage=null;

	private static Image ReturnImage=null;

	private static Image ExceptionImage=null;

	private static Image MonitorImage=null;

	private static Image ItemImage= null;

	static{
		URL url;
		url= MainViewer.class.getResource(IconFilePathConstant.TREE_ATTRIBUTE);
		ArgementImage = ImageDescriptor.createFromURL(url).createImage();
		url= MainViewer.class.getResource(IconFilePathConstant.TREE_VARIABLE);
		VariantImage = ImageDescriptor.createFromURL(url).createImage();
		url= MainViewer.class.getResource(IconFilePathConstant.TREE_SUPERCLASS);
		SuperClassImage = ImageDescriptor.createFromURL(url).createImage();
		url= MainViewer.class.getResource(IconFilePathConstant.TREE_THIS);
		ThisImage = ImageDescriptor.createFromURL(url).createImage();
		url= MainViewer.class.getResource(IconFilePathConstant.TREE_ATTRIBUTE);
		AttributeImage = ImageDescriptor.createFromURL(url).createImage();
		url= MainViewer.class.getResource(IconFilePathConstant.TREE_RETURN);
		ReturnImage = ImageDescriptor.createFromURL(url).createImage();
		url= MainViewer.class.getResource(IconFilePathConstant.TREE_EXCEPTION);
		ExceptionImage = ImageDescriptor.createFromURL(url).createImage();
		url= MainViewer.class.getResource(IconFilePathConstant.TREE_CONTEND_MONITOR_OBJECT);
		MonitorImage = ImageDescriptor.createFromURL(url).createImage();
		url= MainViewer.class.getResource(IconFilePathConstant.TREE_ITEM);
		ItemImage = ImageDescriptor.createFromURL(url).createImage();
	}
	/**
	 * construct
	 * @param objectUnit
	 */
	public ObjectCell(ObjectUnit objectUnit){
		this.varName = objectUnit.getVarName();
		this.definteType = objectUnit.getDefType();
		this.realType = objectUnit.getRealType();
		this.value = objectUnit.getValue();
		this.objectId = objectUnit.getObjectId();
		this.tagName = objectUnit.getDefTagName();
	}

	public void setStackTreeViewer(TreeViewer stackTreeViewer){
		this.stackTreeViewer=stackTreeViewer;
		for (ObjectCell cell: referenceArray){
			cell.setStackTreeViewer(stackTreeViewer);
		}
	}

	public Object getParent() {
		return parent;
	}

	public void setParent(ICell parent) {
		this.parent = parent;
	}


	public String getObjectId() {
		return objectId;
	}

	public List<ObjectCell> getReferenceArray() {
		return referenceArray;
	}

	public void setReferenceArray(List<ObjectCell> referenceArray) {
		for (ObjectCell cell: referenceArray){
			cell.setParent(this);
		}
		this.referenceArray = referenceArray;

	}

	public void addChild(ObjectCell objectCell){
		objectCell.setParent(this);
		this.referenceArray.add(objectCell);
	}

	public Object[] getChildren() {
		ObjectCell[] objectCellArray = new ObjectCell[referenceArray.size()];
		for (int i=0; i<referenceArray.size(); i++){
			objectCellArray[i] = referenceArray.get(i);
		}
		return objectCellArray;
	}

	public Image getImage() {
		if (DumpFileXmlConstant.NODE_ARGUMENT.equals(tagName)){
			return ArgementImage;
		}else if (DumpFileXmlConstant.NODE_VARIABLE.equals(tagName)){
			return VariantImage;
		}else if (DumpFileXmlConstant.NODE_SUPERCLASS.equals(tagName)){
			return SuperClassImage;
		}else if (DumpFileXmlConstant.NODE_THIS.equals(tagName)){
			return ThisImage;
		}else if (DumpFileXmlConstant.NODE_ATTRIBUTE.equals(tagName)){
			return AttributeImage;
		}else if (DumpFileXmlConstant.NODE_RETURN.equals(tagName)){
			return ReturnImage;
		}else if (DumpFileXmlConstant.NODE_EXCEPTION_OBJECT.equals(tagName)){
			return ExceptionImage;
		}else if (DumpFileXmlConstant.NODE_CONTEND_MONITOR_OBJECT.equals(tagName)){
			return MonitorImage;
		}else if (DumpFileXmlConstant.NODE_ITEM.equals(tagName)){
			return ItemImage;
		}
		return null;
	}

	public String getText() {
		String text = "";
		if (!"".equals(this.varName) && this.varName != null){
			text = text + this.varName;
		}
		if (!"".equals(this.value) && this.value != null){
			text = text + "=" + "\"" + this.value + "\"";
		}
		if (!"".equals(this.realType) && this.realType != null){
			text = text + " [ type= " + this.realType + " ] ";
		}else{
			text = text + " [ type= " + this.definteType + " ] ";
		}
		if (!"".equals(this.objectId) && this.objectId != null){
			text = text + " [ id= " + this.getObjectId() + " ]";
		}
		return text;
	}

	public boolean hasChildren() {
		if (referenceArray.size()>0){
			return true;
		}else{
			return false;
		}
	}

	public TreeItem selectItem(){
		TreeItem parentItem = parent.selectItem();

		if (parentItem == null){
			HelperFunc.getLogger().debug("failure: parentItem is null");
			return null;
		}
		return selectItem(parentItem);
	}

	private TreeItem selectItem(TreeItem item) {
		Element node = ((Element)item.getData());
		stackTreeViewer.expandToLevel(node,1);
		TreeItem[] temList = item.getItems();
		for (TreeItem myItem : temList){
			Element nodeSuper = ((Element)myItem.getData());
			String name = nodeSuper.getAttribute(DumpFileXmlConstant.ATTR_NAME);
			if (varName.equals(name)){
				//stackTreeViewer.getTree().setSelection(myItem);
				stackTreeViewer.setSelection(new StructuredSelection(myItem.getData()));
				return myItem;
			} else if (name.length() == 0) {
				// 配列の場合、Indexを利用して比較する。
				String type = ((Element)nodeSuper.getParentNode()).getAttribute(DumpFileXmlConstant.ATTR_REAL_TYPE);
				if (type == null || type.length() == 0) {
					type = ((Element)nodeSuper.getParentNode()).getAttribute(DumpFileXmlConstant.ATTR_DEF_TYPE);
				}
				if (type.endsWith(BRACKET_LEFT + BRACKET_RIGHT)) {
					String index = nodeSuper.getAttribute(DumpFileXmlConstant.ATTR_INDEX);
					if (varName.equals(BRACKET_LEFT + index + BRACKET_RIGHT)) {
						//stackTreeViewer.getTree().setSelection(myItem);
						stackTreeViewer.setSelection(new StructuredSelection(myItem.getData()));
						return myItem;
					}
				}
			}
			// SuperClassの変数を再帰的に検索する。
			if (DumpFileXmlConstant.NODE_SUPERCLASS.equals(nodeSuper.getNodeName())) {
				 return selectItem(myItem);
			}
		}
		return null;
	}
}
