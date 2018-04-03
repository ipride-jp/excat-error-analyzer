package jp.co.ipride.excat.configeditor.model.task;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.DocumetUtil;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 除外クラスの定義が有効となる経路
 *
 * @author tu
 * @since 2007/11/19
 */
public class ExcludePlace implements IPlace{

	private String className="";

	private String methodName="";

	private String methodSignature="";

	private boolean use=true;

	/**
	 * コンストラクタ
	 *
	 */
	public ExcludePlace(){
	}

	/**
	 * ＤＯＭからオブジェクトを生成
	 * @param excludePlaceNode
	 */
	public void inputDocument(Node excludePlaceNode) {
		NamedNodeMap map = excludePlaceNode.getAttributes();
		Node attrNode = map.getNamedItem(ConfigContant.Field_CLASS);
		className = attrNode.getNodeValue();
		attrNode = map.getNamedItem(ConfigContant.Field_METHOD_NAME);
		methodName = attrNode.getNodeValue();
		attrNode = map.getNamedItem(ConfigContant.Field_METHOD_SIGNATURE);
		if (attrNode != null){
			methodSignature = attrNode.getNodeValue();
		}
		attrNode = map.getNamedItem(ConfigContant.Filed_VALID);
		if (attrNode != null){
			use = Boolean.parseBoolean(attrNode.getNodeValue());
		}

	}

	/**
	 * ＤＯＭ作成
	 * MonitorTargets
	 *    MonitorTarget
	 *        Throwable  [Class]
	 *        Place      [Class][MethodName][MethodSignature]
	 *        Filters
	 *            Filter [ExcludeClass] <- this class
	 *                ExcludePlace [Class][MethodName][MethodSignature]
	 *
	 * @param filterNode : [Filter]
	 */
	public void outputDocument(Node filterNode) {
		Node excludePlaceNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_EXCLUDE_PLACE);
		filterNode.appendChild(excludePlaceNode);

//		((Element)excludePlaceNode).setAttribute(ConfigContant.Field_CLASS, className);
//		((Element)excludePlaceNode).setAttribute(ConfigContant.Field_METHOD_NAME, methodName);
		DocumetUtil.setAttribute(excludePlaceNode, ConfigContant.Field_CLASS, this.className);
		DocumetUtil.setAttribute(excludePlaceNode, ConfigContant.Field_METHOD_NAME, this.methodName);
		DocumetUtil.setAttribute(excludePlaceNode, ConfigContant.Field_METHOD_SIGNATURE, this.methodSignature);
		DocumetUtil.setAttribute(excludePlaceNode, ConfigContant.Field_VALID, this.use);
//		if (!"".equals(methodSignature.trim())){
//			((Element)excludePlaceNode).setAttribute(ConfigContant.Field_METHOD_SIGNATURE, methodSignature);
//		}
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

	/**
	 * 引数をコピー先として自身をコピーする。
	 * @param clone
	 */
	public void copyTo(ExcludePlace clone){
		clone.className = this.className;
		clone.methodName = this.methodName;
		clone.methodSignature = this.methodSignature;
		clone.use = this.use;
	}
}
