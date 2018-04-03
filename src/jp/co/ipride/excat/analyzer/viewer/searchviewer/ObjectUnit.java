package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;
import jp.co.ipride.excat.analyzer.viewer.propertyviewer.PropertyItemFactory;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * use to search stack view when using text keyword.
 * @author tu-ipride
 * @version 3.0
 * @since 2009/10/9
 */
public class ObjectUnit {

	public static final String DEFINIT_TYPE = "definiteType";

	public static final String REAL_TYPE = "realType";

	public static final String VAR_NAME = "varName";

	public static int MAX_DEPTH = 6;


	//親ｵﾌﾞｼﾞｪｸﾄ
	private ObjectUnit parent;

	private int sequence = -1;

	/**
	 * 定義するオブジェクト
	 * このオブジェクトは、以下のものが必ずある。
	 * １．変数名
	 * ２．定義タイプ
	 *　３．値（reference objectがある場合にはない）
	 * 該当オブジェクトはObjectRefがある場合、この値はReaｌObjectにあるかもしれない。
	 */
	private Node definiteNode = null;

	/**
	 * 参照するオブジェクト
	 * １．該当オブジェクトは変数名はない
	 *　２．オブジェクトの要素（フィールド、スーパー・オブジェクト）が可能
	 */
	private Node realNode = null;

	//this object directly reference object(argument,this, variant,super-object)
	private List<ObjectUnit> referenceList = new ArrayList<ObjectUnit>();

	//this is mark. when was searched, it will be set by true.
	private boolean searched = false;

	//this is mark. if this unit fit the search condition, will be changed to true.
	private boolean matched = false;

	/**
	 * construct
	 */
	public ObjectUnit(){
	}

	public void setParent(ObjectUnit unit){
		this.parent = unit;
	}

	public void setDefiniteNode(Node node){
		this.definiteNode = node;
	}

	public int getSequence(){
		return sequence;
	}

	public void setSequence(int sequence){
		this.sequence = sequence;
	}

	public String getObjectId(){
		return SearchUtility.getObjectId((Element)realNode);
	}

	public void addRelationObjectList(List<ObjectUnit> nodeList){
		this.referenceList.addAll(nodeList);
	}

	public void addRelationObject(ObjectUnit node){
		this.referenceList.add(node);
	}

	public List<ObjectUnit> getRelationObjectList(){
		return referenceList;
	}

	public Node getDefiniteNode(){
		return definiteNode;
	}

	public Node getRealNode(){
		return realNode;
	}

	public String getObjectReferenceId(){
		return SearchUtility.getObjectRefId((Element)definiteNode);
	}

	public void setRealObject(Node realObject){
		this.realNode = realObject;
	}

	public String getValue(){
		String value;
		if (realNode == null){
			Element node =(Element)definiteNode;
			String data = node.getAttribute(DumpFileXmlConstant.ATTR_VALUE);
			value = PropertyItemFactory.convertValue(
					data,
					getDefType(),
					false);
		}else{
			Element node =(Element)realNode;
			String data = node.getAttribute(DumpFileXmlConstant.ATTR_VALUE);
			value = PropertyItemFactory.convertValue(
					data,
					getRealType(),
					false);
		}

		return Utility.filterString(value);
	}

	public String getVarName(){
		String name = null;
		String nodeName = definiteNode.getNodeName();
		if (DumpFileXmlConstant.NODE_ITEM.equals(nodeName)) {
			Node indexAttr = definiteNode.getAttributes().getNamedItem(DumpFileXmlConstant.ATTR_INDEX);
			if (indexAttr != null) {
				name = "[" + indexAttr.getNodeValue() + "]";
			}
		}else if (DumpFileXmlConstant.NODE_EXCEPTION_OBJECT.equals(nodeName)){
			name = ApplicationResource.getResource("Property.StackTrace.ExceptionName");
		}else if (DumpFileXmlConstant.NODE_CONTEND_MONITOR_OBJECT.equals(nodeName)){
			name = ApplicationResource.getResource("Property.ThreadMonitor");
		}else{
			Element node =(Element)definiteNode;
			name = node.getAttribute(DumpFileXmlConstant.ATTR_NAME);
		}
		return name;
	}

	public String getDefType(){
		Element node =(Element)definiteNode;
		String type = node.getAttribute(DumpFileXmlConstant.ATTR_DEF_TYPE);
		return type;
	}

	public String getRealType(){
		if (realNode == null){
			return null;
		}else{
			Element node =(Element)realNode;
			String type = node.getAttribute(DumpFileXmlConstant.ATTR_REAL_TYPE);
			return type;
		}
	}

	/**
	 * run a search
	 * @return
	 */
	public ObjectCell extracRoot(int depth){
		int myDepth = depth+1;
		if (this.matched && referenceList.size() <= 0){
			return this.transferObjectCell();
		}else if (this.searched){
			return null;
		}else if (myDepth > MAX_DEPTH){
			return null;
		}else{
			//参照している変数を検索する。
			ObjectCell me = null;
			if (this.matched) {
				// 障害#546
				me = this.transferObjectCell();
			}
			for (int i=0; i<referenceList.size(); i++){
				ObjectUnit child =referenceList.get(i);
				ObjectCell ret = child.extracRoot(myDepth);
				if (ret != null){
					if (me==null){
						me = this.transferObjectCell();
					}
					me.addChild(ret);
					ret.setParent(me);
				}
			}
			return me;
		}
	}

	private ObjectCell transferObjectCell(){
		ObjectCell me = new ObjectCell(this);
		return me;
	}

	public boolean isSearched() {
		return searched;
	}

	public void setSearched(boolean searched) {
		this.searched = searched;
	}

	public boolean isMatched() {
		return matched;
	}

	public void setMatched(boolean matched) {
		this.matched = matched;
	}

	public String getDefTagName(){
		return ((Element)definiteNode).getNodeName();
	}

	public void clear(){
		definiteNode = null;
		realNode = null;
		referenceList.clear();
		parent = null;
	}
}
