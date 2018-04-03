package jp.co.ipride.excat.configeditor.model.task;
import java.util.Vector;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.DocumetUtil;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * 除外クラスを保持する。
 * @author tu
 * @since 2007/11/20
 */
public class FilterException {

	private String excludeClassName="";

	private Vector<ExcludePlace> applyPlaces = new Vector<ExcludePlace>();

	private boolean use=true;

	/**
	 * コンストラクタ
	 *
	 */
	public FilterException(){
	}

	/**
	 * ＤＯＭからオブジェクトを生成する。
	 * @param filterNode
	 */
	public void inputDocument(Node filterNode) {
		NamedNodeMap map = filterNode.getAttributes();
		Node attrNode = map.getNamedItem(ConfigContant.Field_EXCLUDE_CLASSS);
		excludeClassName = attrNode.getNodeValue();
		attrNode = map.getNamedItem(ConfigContant.Filed_VALID);
		if (attrNode != null){
			use = Boolean.parseBoolean(attrNode.getNodeValue());
		}

		NodeList nodeList = filterNode.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			String name = node.getNodeName();
			if (ConfigContant.Tag_EXCLUDE_PLACE.equals(name)){
				ExcludePlace place = new ExcludePlace();
				place.inputDocument(node);
				applyPlaces.add(applyPlaces.size(),place);
			}
		}
	}

	/**
	 * ＤＯＭ生成
	 * MonitorTargets
	 *    MonitorTarget
	 *        Throwable  [Class]
	 *        Place      [Class][MethodName][MethodSignature]
	 *        Filters
	 *            Filter [FilterException]  <= this class
	 *                ExcludePlace [Class][MethodName][MethodSignature]
	 *
	 * @param filtersNode : [Filters]
	 */
	public void outputDocument(Node filtersNode) {
		Node filterNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_FILTER);
		filtersNode.appendChild(filterNode);
//		((Element)filterNode).setAttribute(ConfigContant.Field_EXCLUDE_CLASSS, excludeClassName);
		DocumetUtil.setAttribute(filterNode, ConfigContant.Field_EXCLUDE_CLASSS, this.excludeClassName);
		DocumetUtil.setAttribute(filterNode, ConfigContant.Field_VALID, this.use);

		for (int i=0; i<applyPlaces.size(); i++){
			ExcludePlace place = (ExcludePlace)applyPlaces.get(i);
			place.outputDocument(filterNode);
		}
	}

	public boolean checkItems(){
		if (!ViewerUtil.checkClassName(excludeClassName)){
			return false;
		}
		for (int i=0; i<applyPlaces.size(); i++){
			if (!((ExcludePlace)applyPlaces.get(i)).checkItems()){
				return false;
			}
		}
		return true;
	}

	public String getExcludeClassName() {
		return excludeClassName;
	}

	public void setExcludeClassName(String excludeClassName) {
		this.excludeClassName = excludeClassName;
		ConfigModel.setChanged();
	}

	public Vector<ExcludePlace> getApplyPlaces(){
		return applyPlaces;
	}

	public void addApplyPlace(ExcludePlace applyPlace){
		applyPlaces.add(applyPlace);
	}

	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	/**
	 * 引数に自身をコピーする。
	 * @param clone　コピー先
	 */
	public void copyTo(FilterException clone){
		clone.use = this.use;
		clone.excludeClassName = this.excludeClassName;
		clone.applyPlaces.removeAllElements();
		for (int i=0; i<applyPlaces.size(); i++){
			ExcludePlace excludePlace = new ExcludePlace();
			((ExcludePlace)applyPlaces.get(i)).copyTo(excludePlace);
			clone.applyPlaces.add(excludePlace);
		}
	}

}
