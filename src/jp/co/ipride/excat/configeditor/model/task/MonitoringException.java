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
 * 例外やエラーを監視する定義
 *
 * @author tu
 * @since 2007/11/19
 */
public class MonitoringException extends Monitoring{

	private String targetClassName="";
	private Vector<FilterException> filterExceptions = new Vector<FilterException>();
	private Vector<Place> places = new Vector<Place>();


	/**
	 * [MonitorTarget]タグを読み込む
	 */
	public void inputDocument(Node monitorTargetNode) {
		NodeList nodeList = monitorTargetNode.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			String name = node.getNodeName();
			if (ConfigContant.Tag_THROWABLE.equals(name)){
				inputThrowable(node);
			}else if (ConfigContant.Tag_PLACE.equals(name)){
				inputPlace(node);
			}else if (ConfigContant.Tag_FILTERS.equals(name)){
				inputFilters(node);
			}
		}
	}
	/**
	 * 監視クラスを読込み
	 * @param ThrowableNode
	 */
	private void inputThrowable(Node ThrowableNode){
		Node attrNode;
		NamedNodeMap map = ThrowableNode.getAttributes();
		attrNode = map.getNamedItem(ConfigContant.Field_CLASS);
		targetClassName = attrNode.getNodeValue();
		inputValidAttribute(ThrowableNode);
	}

	/**
	 * 監視経路を読込み
	 * @param placeNode
	 */
	private void inputPlace(Node placeNode){
		Place place = new Place();
		place.inputDocument(placeNode);
		places.add(places.size(),place);
	}

	/**
	 * 除外クラスを読込み
	 * @param filtersNode
	 */
	private void inputFilters(Node filtersNode){
		NodeList nodeList = filtersNode.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			String name = node.getNodeName();
			if (ConfigContant.Tag_FILTER.equals(name)){
				FilterException exclude = new FilterException();
				exclude.inputDocument(node);
				filterExceptions.add(filterExceptions.size(),exclude);
			}
		}
	}

	/**
	 * ＤＯＭを作成する。
	 * MonitorTargets
	 *    MonitorTarget
	 *        Throwable  [Class]
	 *        Place      [Class][MethodName][MethodSignature]
	 *        Filters
	 *            Filter [ExcludeClass]
	 *                ExcludePlace [Class][MethodName][MethodSignature]
	 *
	 * @param monitoringTargetsNode : [MonitoringTargets]
	 */
	public void outputDocument(Node monitoringTargetsNode) {
		Node monitoringTargetNode = ConfigModel.getDocument().
						createElement(ConfigContant.Tag_MONITOR_TARGET);
		monitoringTargetsNode.appendChild(monitoringTargetNode);

		Node throwableNode = ConfigModel.getDocument().
						createElement(ConfigContant.Tag_THROWABLE);
		monitoringTargetNode.appendChild(throwableNode);

		DocumetUtil.setAttribute(throwableNode, ConfigContant.Field_CLASS, targetClassName);
		DocumetUtil.setAttribute(throwableNode, ConfigContant.Field_VALID, use);

		for (int i=0; i<places.size(); i++){
			Place place = (Place)places.get(i);
			place.outputDocument(monitoringTargetNode);
		}
		if (filterExceptions.size()>0){
			Node filtersNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_FILTERS);
			monitoringTargetNode.appendChild(filtersNode);
			for (int i=0; i<filterExceptions.size(); i++){
				FilterException ee = (FilterException)filterExceptions.get(i);
				ee.outputDocument(filtersNode);
			}
		}
	}

	public boolean checkItems(){
		if (!ViewerUtil.checkClassName(targetClassName)){
			return false;
		}
		for (int i=0; i<filterExceptions.size(); i++){
			if (!((FilterException)filterExceptions.get(i)).checkItems()){
				return false;
			}
		}
		for (int i=0; i<places.size(); i++){
			if (!((Place)places.get(i)).checkItems()){
				return false;
			}
		}
		return true;
	}

	public String getTargetClassName() {
		return targetClassName;
	}

	public void setTargetClassName(String targetClassName) {
		this.targetClassName = targetClassName;
		ConfigModel.setChanged();
	}

	public Vector<FilterException> getExcludeExceptions() {
		return filterExceptions;
	}

	public Vector<Place> getPlaces() {
		return places;
	}

	/**
	 * 自身を引数オブジェクトにコピーする。
	 * @param clone
	 */
	public void copyTo(Monitoring clone){
		((MonitoringException)clone).targetClassName = this.targetClassName;
		((MonitoringException)clone).filterExceptions.removeAllElements();
		((MonitoringException)clone).places.removeAllElements();

		for (int i=0; i<filterExceptions.size(); i++){
			FilterException excludeException = new FilterException();
			((FilterException)filterExceptions.get(i)).copyTo(excludeException);
			((MonitoringException)clone).filterExceptions.add(excludeException);
		}
		for (int i=0; i<places.size(); i++){
			Place place = new Place();
			((Place)places.get(i)).copyTo(place);
			((MonitoringException)clone).places.add(place);
		}
	}
}
