package jp.co.ipride.excat.configeditor.model.task;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.DocumetUtil;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 1.ŠÄŽ‹—áŠO‚ÌŒo˜H
 * 2.
 * @author tu
 * @since 2007/11/20
 */
public class Place implements IPlace{

	private String className="";

	private String methodName="";

	private String methodSignature="";

	private boolean use = true;


	/**
	 * construct
	 * @param placeNode
	 */
	public void inputDocument(Node placeNode) {
		Node attrNode;
		NamedNodeMap map = placeNode.getAttributes();
		attrNode = map.getNamedItem(ConfigContant.Field_CLASS);
		className= attrNode.getNodeValue();
		attrNode = map.getNamedItem(ConfigContant.Field_METHOD_NAME);
		methodName= attrNode.getNodeValue();
		attrNode = map.getNamedItem(ConfigContant.Field_METHOD_SIGNATURE);
		if (attrNode != null){
			methodSignature= attrNode.getNodeValue();
		}
		attrNode = map.getNamedItem(ConfigContant.Filed_VALID);
		if (attrNode != null){
			use = Boolean.parseBoolean(attrNode.getNodeValue());
		}
	}

	/**
	 * MonitorTargets
	 *    MonitorTarget
	 *        Throwable  [Class]
	 *        Place      [Class][MethodName][MethodSignature]  <= this class
	 *        Filters
	 *            Filter [ExcludeClass]
	 *                ExcludePlace [Class][MethodName][MethodSignature]
	 *
	 * @param monitoringTargetNode : [MonitoringTarget]
	 */
	public void outputDocument(Node monitoringTargetNode) {
		Node placeNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_PLACE);
		monitoringTargetNode.appendChild(placeNode);

		((Element)placeNode).setAttribute(ConfigContant.Field_CLASS, className);
		((Element)placeNode).setAttribute(ConfigContant.Field_METHOD_NAME, methodName);
		if (!"".equals(methodSignature.trim())){
			((Element)placeNode).setAttribute(ConfigContant.Field_METHOD_SIGNATURE, methodSignature);
		}
		DocumetUtil.setAttribute(placeNode, ConfigContant.Field_VALID, this.use);
	}

	public boolean checkItems(){
		if (!ViewerUtil.checkClassName(className)){
			return false;
		}
		if (!"".equals(methodName)){
			if (!ViewerUtil.checkMethodName(methodName)){
				return false;
			}
		}
		if (!"".equals(methodSignature)){
			if (!ViewerUtil.checkSignature(methodSignature)){
				return false;
			}
		}
		return true;
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
		ConfigModel.setChanged();
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
		ConfigModel.setChanged();
	}

	public String getMethodSignature() {
		return methodSignature;
	}

	public void setMethodSignature(String methodSignature) {
		this.methodSignature = methodSignature;
		ConfigModel.setChanged();
	}


	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
	}

	public void copyTo(Place clone){
		clone.className = this.className;
		clone.methodName = this.methodName;
		clone.methodSignature = this.methodSignature;
		clone.use = this.use;
	}


}
