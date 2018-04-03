package jp.co.ipride.excat.analyzer.viewer.stackviewer;


import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * nodeのレベル情報を提供する
 * @author jiang
 *
 */
public class CommonNodeLabel {

	/**
	 * implement interface to supply text of element.
	 *
	 * @see org.eclipse.jface.viewers.ILabelProvider#getText(java.lang.Object)
	 */
	public static String getText(Node node) {
		String nodeName;

		short nodeType = node.getNodeType();

		if (nodeType == Node.ELEMENT_NODE) {
			nodeName = node.getNodeName();

			////  get method, dump stacktrace and superclass  ////

			if (DumpFileXmlConstant.NODE_DUMP.equals(nodeName)) {

				return nodeName + "(" + getDumpDateTime(node) + ")";

			} else if (DumpFileXmlConstant.NODE_STACKTRACE.equals(nodeName)) {

				return "Thread";  //need to be changed

			} else if (DumpFileXmlConstant.NODE_METHOD.equals(nodeName)) {

				String clazzName = getAttrValue(node, DumpFileXmlConstant.ATTR_DECL_CLASS);
				String name = getAttrValue(node, DumpFileXmlConstant.ATTR_NAME);
				return clazzName + "." + name;

			} else if (DumpFileXmlConstant.NODE_SUPERCLASS.equals(nodeName)) {

				return getAttrValue(node, DumpFileXmlConstant.ATTR_SIG);

			}else if (DumpFileXmlConstant.NODE_MONITOROBJECT.equals(nodeName)) {

				return "Class:" + getAttrValue(node, DumpFileXmlConstant.ATTR_CLASSNAME);

			} if (DumpFileXmlConstant.NODE_INSTANCE.equals(nodeName)) {

				return "Instance:" + getAttrValue(node, DumpFileXmlConstant.ATTR_SEQID);

			}

			////  get label of object  ////

			//get name of node.
			Node nameAttr = node.getAttributes().getNamedItem(DumpFileXmlConstant.ATTR_NAME);
			String nameAttrValue = "";

			// if it is item node
			if (DumpFileXmlConstant.NODE_ITEM.equals(nodeName)) {
				Node indexAttr = node.getAttributes().getNamedItem(DumpFileXmlConstant.ATTR_INDEX);
				if (indexAttr != null) {
					nameAttrValue += "[" + indexAttr.getNodeValue() + "]";
				}
			}else if (DumpFileXmlConstant.NODE_EXCEPTION_OBJECT.equals(nodeName)){
				//v3
				nameAttrValue = ApplicationResource.getResource("Property.StackTrace.ExceptionName");

			}else if (DumpFileXmlConstant.NODE_CONTEND_MONITOR_OBJECT.equals(nodeName)){

				nameAttrValue = ApplicationResource.getResource("Property.ThreadMonitor");
				//障害 #452
			}else if (DumpFileXmlConstant.NODE_RETURN.equals(nodeName)){

				nameAttrValue = ApplicationResource.getResource("Property.ReturnName");

			}else{

				nameAttrValue = nameAttr.getNodeValue();
			}

			//get real type of object
			Node realTypeAttr = node.getAttributes().getNamedItem(DumpFileXmlConstant.ATTR_REAL_TYPE);
			String realTypeAttrValue = null;
			if (realTypeAttr != null) {
				realTypeAttrValue = realTypeAttr.getNodeValue();
			}

			//get real type of object
			Node defTypeAttr = node.getAttributes().getNamedItem(DumpFileXmlConstant.ATTR_DEF_TYPE);
			String defTypeAttrValue = null;
			if (defTypeAttr != null) {
				defTypeAttrValue = defTypeAttr.getNodeValue();
			}

			//get value of node.
			Node valueAttr = node.getAttributes().getNamedItem(DumpFileXmlConstant.ATTR_VALUE);
			String valueAttrValue = null;
			String value=null;
			if (valueAttr != null) {
				value = valueAttr.getNodeValue();
			}
			valueAttrValue = convertValue(value, realTypeAttrValue, defTypeAttrValue);

			return nameAttrValue + "=" + valueAttrValue;
		}

		return node.getNodeValue();
	}


	/**
	 * ダンプの日時を取得
	 * @param node dump_node
	 * @return
	 */
	private static String getDumpDateTime(Node node){

		NodeList sonList = node.getChildNodes();
		if(sonList == null || sonList.getLength() == 0){
			return null;
		}
		for(int i = 0; i < sonList.getLength();i++){
			Node sonNode = sonList.item(i);
			String nodeName = sonNode.getNodeName();
			if (DumpFileXmlConstant.NODE_STACKTRACE.equals(nodeName)){
				return getAttrValue(sonNode, DumpFileXmlConstant.ATTR_DUMPTIME);
			}
		}

		return null;
	}

	/**
	 * 表示用の値に変換
	 * @param value
	 * @param type
	 * @return
	 */
	public static String convertValue(String value, String realType, String defType){

		String type;
		if (realType != null){
			type = realType;
		}else{
			type = defType;
		}

		if ("null".equals(value)){
			return value;
		}

		if ("char".equals(type)){
			return "\'" + HelperFunc.getStringFromNumber(value) + "\'";
		}

		if ("int".equals(type) || "long".equals(type) || "boolean".equals(type) ||
			"short".equals(type) || "long".equals(type) || "float".equals(type) ||
			"double".equals(type) || "byte".equals(type)){
			if(value == null){
				return type; //for invalid variable
			}
			return value;
		}else if ("java.lang.String".equals(type)){
			if(value == null){
				return type; //for invalid variable
			}

			return "\"" + HelperFunc.getStringFromNumber(value) + "\"";
		}else{
			return type;
		}
	}

	/**
	 *
	 * @param node
	 * @param attrName
	 * @return
	 */
	private static String getAttrValue(Node node, String attrName) {
		Node nameAttr = node.getAttributes().getNamedItem(attrName);
		String nameAttrValue = "";
		if (nameAttr != null) {
			nameAttrValue = nameAttr.getNodeValue();
		}

		return nameAttrValue;
	}
}
