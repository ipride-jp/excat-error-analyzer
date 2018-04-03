package jp.co.ipride.excat.configeditor.model.task;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.DocumetUtil;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * メソッド指定の任意ダンプ
 * 指定するメソッド
 * @author tu
 * @since 2007/11/19
 */
public class MonitoringMethod extends Monitoring{
	public static final String START_POSITION = "start";
	public static final String FINISH_POSITION = "finish";
	public static final String BOTH_POSITION = "both";

	private String className = "";
	private String classLoaderURL = "";
	private String classLoader_suffix = "";

	private String methodName = "";
	private String signature = "";
	private String signature_suffix = "";
	private String condition = "";
	private String position = FINISH_POSITION;
	private int maxDumpCount = 1;

	/**
	 * [Method]タグを読込み
	 */
	public void inputDocument(Node methodNode) {
		Node node;
		NamedNodeMap map = methodNode.getAttributes();

		node = map.getNamedItem(ConfigContant.Field_NAME);
		methodName = node.getNodeValue();

		node = map.getNamedItem(ConfigContant.Field_SIGNATURE);
		if (node !=null){
			signature=node.getNodeValue();
		}

		node = map.getNamedItem(ConfigContant.Field_CONDITION);
		if (node != null){
			condition=node.getNodeValue();
		}

		node = map.getNamedItem(ConfigContant.Field_MAX_DUMP_COUNT);
		if (node != null){
			String value = node.getNodeValue();
			maxDumpCount = Integer.parseInt(value);
		}

		node = map.getNamedItem(ConfigContant.Field_SUFFIX);
		if (node != null){
			signature_suffix=node.getNodeValue();
		}
		node = map.getNamedItem(ConfigContant.Field_POSITION);
		if (node != null){
			position = node.getNodeValue();
		}
		inputValidAttribute(methodNode);
	}

	/**
	 * MonitoringTargets
	 *    MonitoringMethod  [Class][ClassLoaderString][Suffix]  <- now
	 *        Method   [Name][Signature][Condition][MaxDumpCount][Suffix][valid]
	 *
	 * @param monitoringTargetsNode: [MonitoringMethod]
	 */
	public void outputDocument(Node monitorMethodNode) {
		//既存の同一のclass有無をチェックする。なければ、新規作成
		if (monitorMethodNode.getAttributes().getNamedItem(ConfigContant.Field_CLASS) == null){
			((Element)monitorMethodNode).setAttribute(ConfigContant.Field_CLASS, className);
			if (!"".equals(classLoaderURL.trim())){
				((Element)monitorMethodNode).setAttribute(ConfigContant.Field_CLASSLOADER, classLoaderURL);
			}
			if (!"".equals(classLoader_suffix.trim())){
				((Element)monitorMethodNode).setAttribute(ConfigContant.Field_SUFFIX, classLoader_suffix);
			}
		}

		//method
		Node methodNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_METHOD);
		monitorMethodNode.appendChild(methodNode);

		DocumetUtil.setAttribute(methodNode,ConfigContant.Field_NAME, methodName);
		DocumetUtil.setAttribute(methodNode,ConfigContant.Field_SIGNATURE, signature);
		DocumetUtil.setAttribute(methodNode,ConfigContant.Field_SUFFIX, signature_suffix);
		DocumetUtil.setAttribute(methodNode,ConfigContant.Field_MAX_DUMP_COUNT, Integer.toString(maxDumpCount));
		DocumetUtil.setAttribute(methodNode,ConfigContant.Field_CONDITION, condition);
		DocumetUtil.setAttribute(methodNode,ConfigContant.Field_POSITION, position);
		DocumetUtil.setAttribute(methodNode, ConfigContant.Field_VALID, this.use);
	}

	public boolean checkItems(){
		if (!ViewerUtil.checkClassName(className)){
			return false;
		}
		if (!ViewerUtil.checkMethodName(methodName)){
			return false;
		}
		if (!"".equals(classLoaderURL)){
			if (!ViewerUtil.checkClassLoader(classLoaderURL)){
				return false;
			}
		}
		if (!"".equals(classLoader_suffix)){
			if (!ViewerUtil.checkSuffix(classLoader_suffix)){
				return false;
			}
		}
		if (!"".equals(condition)){
			if (!ViewerUtil.checkStringItem(condition)){
				return false;
			}
		}
		if (!"".equals(signature)){
			if (!ViewerUtil.checkSignature(signature)){
				return false;
			}
		}
		if (!"".equals(signature_suffix)){
			if (!ViewerUtil.checkSuffix(signature_suffix)){
				return false;
			}
		}
		if (maxDumpCount <=0){
			return false;
		}
		return true;
	}

	public String getDisplayName(){
		return className +"->" + methodName + signature;
	}

	public String getClassLoader_suffix() {
		return classLoader_suffix;
	}

	public void setClassLoader_suffix(String classLoader_suffix) {
		this.classLoader_suffix = classLoader_suffix;
		ConfigModel.setChanged();
	}

	public String getClassLoaderURL() {
		return classLoaderURL;
	}

	public void setClassLoaderURL(String classLoaderURL) {
		this.classLoaderURL = classLoaderURL;
		ConfigModel.setChanged();
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
		ConfigModel.setChanged();
	}


	public String getCondition() {
		return condition;
	}

	public void setCondition(String condition) {
		this.condition = condition;
		ConfigModel.setChanged();
	}

	public int getMaxDumpCount() {
		return maxDumpCount;
	}

	public void setMaxDumpCount(int maxDumpCount) {
		this.maxDumpCount = maxDumpCount;
		ConfigModel.setChanged();
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
		ConfigModel.setChanged();
	}

	public String getMethodSig_suffix() {
		return signature_suffix;
	}

	public void setMethodSig_suffix(String methodSig_suffix) {
		this.signature_suffix = methodSig_suffix;
		ConfigModel.setChanged();
	}

	public String getMethodSignature() {
		return signature;
	}

	public void setMethodSignature(String methodSignature) {
		signature = methodSignature;
		ConfigModel.setChanged();
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}

	public void copyTo(Monitoring clone){
		((MonitoringMethod)clone).className = this.className;
		((MonitoringMethod)clone).methodName= this.methodName;
		((MonitoringMethod)clone).classLoader_suffix = this.classLoader_suffix;
		((MonitoringMethod)clone).classLoaderURL = this.classLoaderURL;
		((MonitoringMethod)clone).condition = this.condition;
		((MonitoringMethod)clone).maxDumpCount = this.maxDumpCount;
		((MonitoringMethod)clone).signature_suffix = this.signature_suffix;
		((MonitoringMethod)clone).signature = this.signature;
		((MonitoringMethod)clone).position = this.position;
		((MonitoringMethod)clone).use = this.use;
	}

	public boolean isSameMethod(MonitoringMethod method){
		if (!className.equals(method.getClassName())){
			return false;
		}
		if (!this.classLoaderURL.equals(method.getClassLoaderURL())){
			return false;
		}
		if (!this.methodName.equals(method.getMethodName())){
			return false;
		}
		if (!"".equals(this.signature) && !"".equals(method.getMethodSignature())
				&& !this.signature.equals(method.getMethodSignature())){
			return false;
		}
		return true;
	}
}
