/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.propertyviewer;

import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XMLツリーの要素を作成するファクトリ
 *
 * @author 屠偉新
 *
 */
public class PropertyItemFactory {

	//修飾子
	private static long ACC_PUBLIC = 0x0001;
	private static long ACC_PRIVATE = 0x0002;
	private static long ACC_PROTECTED = 0x0004;
	private static long ACC_STATIC  = 0x0008;
	private static long ACC_FINAL = 0x0010;
	private static long ACC_SYNCHRONIZED = 0x0020;
	private static long ACC_NATIVE = 0x0100;
	private static long ACC_ABSTRACT = 0x0400;
	private static long ACC_VOLATILE  = 0x0040;
	private static long ACC_TRANSIENT  = 0x0080;

	/**
	 * XMLツリー上のノードの属性ビューアの内容を作成
	 *
	 * @param nodeValue
	 * @param attrMap
	 * @return
	 */
	public static List<PropertyItem> createPropertyItems(String nodeValue,
			NamedNodeMap attrMap,Node nodeSelected) {
		if (DumpFileXmlConstant.NODE_DUMP.equals(nodeValue)) {
			List<PropertyItem> dumpList =  createDump(attrMap);
			//get info from StackTrace node
			NamedNodeMap stackNodeAttrMap = getStackNodeAttrMap(nodeSelected);
			List<PropertyItem> dumpInfoList = createDumpInfo(stackNodeAttrMap);
			dumpList.addAll(dumpInfoList);
			return dumpList;
		} else if (DumpFileXmlConstant.NODE_STACKTRACE.equals(nodeValue)) {
			return createStackTrace(attrMap);
		} else if (DumpFileXmlConstant.NODE_METHOD.equals(nodeValue)) {
			return createMethod(attrMap);
		} else if (DumpFileXmlConstant.NODE_ARGUMENT.equals(nodeValue)) {
			return createArgument(attrMap);
		} else if (DumpFileXmlConstant.NODE_VARIABLE.equals(nodeValue)) {
			return createVariable(attrMap);
		} else if (DumpFileXmlConstant.NODE_ITEM.equals(nodeValue)) {
			return createItem(attrMap);
		} else if (DumpFileXmlConstant.NODE_THIS.equals(nodeValue)) {
			return createThis(attrMap);
		} else if (DumpFileXmlConstant.NODE_ATTRIBUTE.equals(nodeValue)) {
			return createAttribute(attrMap);
		} else if (DumpFileXmlConstant.NODE_SUPERCLASS.equals(nodeValue)) {
			return createSuperClass(attrMap);
		} else if (DumpFileXmlConstant.NODE_OBJECT.equals(nodeValue)) {
			return createAttribute(attrMap);
		} else if (DumpFileXmlConstant.NODE_MONITOROBJECT.equals(nodeValue)) {
			return createMonitorObject(attrMap);
		}else if (DumpFileXmlConstant.NODE_INSTANCE.equals(nodeValue)) {
			return createInstance(attrMap);
		}else if (DumpFileXmlConstant.NODE_EXCEPTION_OBJECT.equals(nodeValue)) {
			return createException(attrMap);
		}else if (DumpFileXmlConstant.NODE_CONTEND_MONITOR_OBJECT.equals(nodeValue)) {
			return createContentMonitor(attrMap);
		}else if (DumpFileXmlConstant.NODE_RETURN.equals(nodeValue)){
			return createReturn(attrMap);
		}else {
			return null;
		}
	}

	/**
	 * StackTraceのNamedNodeMapを取得
	 * @param node dump_node
	 * @return
	 */
	private static NamedNodeMap getStackNodeAttrMap(Node node){

		NodeList sonList = node.getChildNodes();
		if(sonList == null || sonList.getLength() == 0){
			return null;
		}
		for(int i = 0; i < sonList.getLength();i++){
			Node sonNode = sonList.item(i);
			String nodeName = sonNode.getNodeName();
			if (DumpFileXmlConstant.NODE_STACKTRACE.equals(nodeName)){
				return sonNode.getAttributes();
			}
		}

		return null;
	}

	/**
	 * ノードはフィールドである場合
	 *
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem> createAttribute(NamedNodeMap attrMap) {
		String type = null;
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.Attribute"));
		list.add(topItem);
		Node node;
		PropertyItem item;
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_NAME);
		item = new PropertyItem(ApplicationResource
				.getResource("Property.AttributeName"), node.getNodeValue());
		list.add(item);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_DEF_TYPE);
		type = node.getNodeValue();
		item = new PropertyItem(ApplicationResource
				.getResource("Property.Type"), type);
		list.add(item);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_ID);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectId"), node.getNodeValue());
			list.add(item);
			node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_REAL_TYPE);
			if (node != null) {
				item = new PropertyItem(ApplicationResource
						.getResource("Property.ObjectType"), node
						.getNodeValue());
				list.add(item);
			}
		}
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_VALUE);
		addNodeValueToList(type, list, node);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_ITEM_NUMBER);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ItemNumber"), node.getNodeValue());
			list.add(item);
		}

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_MODIFIERS);
		if(node != null){
			String flags = getNodeValue(node);
			addModifiersItem(list,flags);
		}
		return list;
	}

	/**
	 * ノードはSuperクラスである場合
	 *
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem> createSuperClass(NamedNodeMap attrMap) {
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.SuperClass"));
		list.add(topItem);
		return list;
	}

	/**
	 * ノードはInstanceである場合
	 *
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem> createMonitorObject(NamedNodeMap attrMap) {
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.MonitorObject"));
		list.add(topItem);

		Node node;
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_CLASSNAME);
		PropertyItem item = new PropertyItem(ApplicationResource
				.getResource("Property.ClassName"), getNodeValue(node));
		list.add(item);

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_CLASSURL);
		item = new PropertyItem(ApplicationResource
				.getResource("Property.ClassURL"), getNodeValue(node));
		list.add(item);
		return list;
	}

	/**
	 * ノードは「Dump」である場合
	 *
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem> createDump(NamedNodeMap attrMap) {
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.DumpFile"));
		list.add(topItem);

		PropertyItem fileNameItem = new PropertyItem(ApplicationResource
				.getResource("Property.DumpFileName"), SettingManager
				.getSetting().getCurrentDumpFilePath());
		list.add(fileNameItem);
		return list;
	}

	private static String getNodeValue(Node node) {
		if (node == null)
			return "";
		return node.getNodeValue();
	}

	/**
	 * ノードは「Stack」である場合
	 * ダンプファイルの情報を取得する
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem> createDumpInfo(NamedNodeMap attrMap) {
		List<PropertyItem> list = new ArrayList<PropertyItem>();

		Node node;
		node = attrMap.getNamedItem("DumpTime");
		PropertyItem item = new PropertyItem(ApplicationResource
				.getResource("Property.DumpTime"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem("SystemID");
		item = new PropertyItem(ApplicationResource
				.getResource("Property.SystemID"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem("JVMID");
		item = new PropertyItem(ApplicationResource
				.getResource("Property.JVMID"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem("JVMVersion");
		item = new PropertyItem(ApplicationResource
				.getResource("Property.JVMVersion"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem("JVMName");
		item = new PropertyItem(ApplicationResource
				.getResource("Property.JVMName"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem("JVMVender");
		item = new PropertyItem(ApplicationResource
				.getResource("Property.JVMVender"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem("DumpCompleted");
		item = new PropertyItem(ApplicationResource
				.getResource("Property.DumpCompleted"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem("ExceptionName");
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ExceptionName"), getNodeValue(node));

			list.add(item);
		}

		return list;
	}

	/**
	 * ノードは「Stack」である場合
	 *
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem> createStackTrace(NamedNodeMap attrMap) {
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.Stack"));
		list.add(topItem);
		Node node;

		PropertyItem item = null;
		node = attrMap.getNamedItem("ThreadName");
		if (node != null) {
			 item = new PropertyItem(ApplicationResource
					.getResource("Property.ThreadName"), getNodeValue(node));

			list.add(item);
		}

		node = attrMap.getNamedItem("ThreadId");
		if (node != null) {
			 item = new PropertyItem(ApplicationResource
					.getResource("Property.ThreadId"), getNodeValue(node));

			list.add(item);
		}
		//v3
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_STATUS);
		if (node != null){
			 item = new PropertyItem(ApplicationResource
						.getResource("Property.StackTrace.Status"), getNodeValue(node));
			 list.add(item);
		}
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_PRIORITY);
		if (node != null){
			 item = new PropertyItem(ApplicationResource
						.getResource("Property.StackTrace.ThreadPriority"), getNodeValue(node));
			 list.add(item);
		}
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_CPUTIME);
		if (node != null){
			 item = new PropertyItem(ApplicationResource
						.getResource("Property.StackTrace.CPUTime"), getNodeValue(node));
			 list.add(item);
		}
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_WAIT_REASON);
		if (node != null){
			 item = new PropertyItem(ApplicationResource
						.getResource("Property.StackTrace.WaitReason"), getNodeValue(node));
			 list.add(item);
		}


		return list;
	}

	/**
	 * ノードはメソッドである場合
	 *
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem> createMethod(NamedNodeMap attrMap) {
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.Method"));
		list.add(topItem);
		Node node;
		PropertyItem item;
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_DECL_CLASS);
		item = new PropertyItem(ApplicationResource
				.getResource("Property.Class"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_NAME);
		item = new PropertyItem(ApplicationResource
				.getResource("Property.MethodName"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_SIG);
		String signature = getNodeValue(node);
		int index = signature.indexOf(")");
		String returnType = signature.substring(index + 1);
		String argType = signature.substring(1, index);
		String[] argTypes = argType.split(",");
		for (int i = 0; i < argTypes.length; i++) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.Argument"), argTypes[i]);
			list.add(item);
		}
		item = new PropertyItem(ApplicationResource
				.getResource("Property.ReturnType"), returnType);
		list.add(item);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_LINE_NUMBER);
		item = new PropertyItem(ApplicationResource
				.getResource("Property.JavaSrcLine"), getNodeValue(node));
		list.add(item);
		addLocationNum(list, attrMap);

		//add access flags
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_MODIFIERS);
		if(node != null){
			String flags = getNodeValue(node);
			addModifiersItem(list,flags);
		}

		return list;
	}

	private static void addModifiersItem(List<PropertyItem> list,String flags){
		long flagValue = Long.valueOf(flags).longValue();
		StringBuffer buf = new StringBuffer();
		if((flagValue & ACC_PUBLIC) == ACC_PUBLIC){
			buf.append("public ");
		}
		if((flagValue & ACC_PRIVATE) == ACC_PRIVATE){
			buf.append("private ");
		}
		if((flagValue & ACC_PROTECTED) == ACC_PROTECTED){
			buf.append("protected ");
		}
		if((flagValue & ACC_STATIC) == ACC_STATIC){
			buf.append("static ");
		}
		if((flagValue & ACC_FINAL) == ACC_FINAL){
			buf.append("final ");
		}
		if((flagValue & ACC_SYNCHRONIZED) == ACC_SYNCHRONIZED){
			buf.append("synchronized ");
		}
		if((flagValue & ACC_NATIVE) == ACC_NATIVE){
			buf.append("native ");
		}
		if((flagValue & ACC_ABSTRACT) == ACC_ABSTRACT){
			buf.append("abstract ");
		}

		if((flagValue & ACC_VOLATILE) == ACC_VOLATILE){
			buf.append("volatile ");
		}

		if((flagValue & ACC_TRANSIENT ) == ACC_TRANSIENT){
			buf.append("transient ");
		}
		PropertyItem item = new PropertyItem(ApplicationResource
				.getResource("Property.Modifiers"), buf.toString());
		list.add(item);
	}

	/**
	 * ノードは引数である場合
	 *
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem> createArgument(NamedNodeMap attrMap) {
		String type = null;
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.MethodArgument"));
		list.add(topItem);
		Node node;
		PropertyItem item;
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_NAME);
		item = new PropertyItem(ApplicationResource
				.getResource("Property.VariantName"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_DEF_TYPE);
		if (node != null) {
			type = node.getNodeValue();
			item = new PropertyItem(ApplicationResource
					.getResource("Property.DefinedType"), type);
			list.add(item);
		}
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_ID);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectId"), node.getNodeValue());
			list.add(item);
			node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_REAL_TYPE);
			if (node != null) {
				item = new PropertyItem(ApplicationResource
						.getResource("Property.ObjectType"), node
						.getNodeValue());
				list.add(item);
			}
		}
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_VALUE);
		addNodeValueToList(type, list, node);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_ITEM_NUMBER);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ItemNumber"), node.getNodeValue());
			list.add(item);
		}
		return list;
	}

	/**
	 * ノードはローカル変数である場合
	 *
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem> createVariable(NamedNodeMap attrMap) {
		String type;
		String unsure = "";
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.LocalVariant"));
		list.add(topItem);
		Node node;
		PropertyItem item;
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_NAME);
		item = new PropertyItem(ApplicationResource
				.getResource("Property.VariantName"), getNodeValue(node));
		list.add(item);
		// added by tu 2006/9/29
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_UNSURE);
		if (node != null && "true".equals(getNodeValue(node))) {
			unsure = "?";
		}
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_DEF_TYPE);
		type = getNodeValue(node);
		item = new PropertyItem(ApplicationResource
				.getResource("Property.DefinedType"), type + unsure);
		list.add(item);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_VALID);
		if ("false".equals(getNodeValue(node))) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.Effective"), ApplicationResource
					.getResource("Property.Effective_False"));
			list.add(item);
			return list;
		} else {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.Effective"), ApplicationResource
					.getResource("Property.Effective_True"));
			list.add(item);
		}

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_ID);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectId"), node.getNodeValue());
			list.add(item);
			node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_REAL_TYPE);
			if (node != null) {
				item = new PropertyItem(ApplicationResource
						.getResource("Property.ObjectType"), node
						.getNodeValue());
				list.add(item);
			}
		}
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_VALUE);
		addNodeValueToList(type, list, node);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_ITEM_NUMBER);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ItemNumber"), node.getNodeValue());
			list.add(item);
		}
		return list;
	}

	private static void addNodeValueToList(String type, List<PropertyItem> list, Node node) {
		PropertyItem item;
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.Value"), convertValue(node
					.getNodeValue(), type, false));
			list.add(item);

			if (isChar(type) || isString(type)) {
				item = new PropertyItem(ApplicationResource
						.getResource("Property.ValueInHex"), convertValue(node
						.getNodeValue(), type, true));
				list.add(item);
			}
		}
	}

	/**
	 * ノードは配列の要素である場合
	 *
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem> createItem(NamedNodeMap attrMap) {
		String type;
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.Item"));
		list.add(topItem);
		Node node;
		PropertyItem item;
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_INDEX);
		item = new PropertyItem(ApplicationResource
				.getResource("Property.IndexNo"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_NAME);
		item = new PropertyItem(ApplicationResource
				.getResource("Property.VariantName"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_DEF_TYPE);
		type = getNodeValue(node);
		item = new PropertyItem(ApplicationResource
				.getResource("Property.DefinedType"), type);
		list.add(item);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_ID);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectId"), node.getNodeValue());
			list.add(item);
			node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_REAL_TYPE);
			if (node != null) {
				item = new PropertyItem(ApplicationResource
						.getResource("Property.ObjectType"), node
						.getNodeValue());
				list.add(item);
			}
		}
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_VALUE);
		addNodeValueToList(type, list, node);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_ITEM_NUMBER);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ItemNumber"), node.getNodeValue());
			list.add(item);
		}
		return list;
	}

	/**
	 * ノードはThisオブジェクトである場合
	 *
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem> createThis(NamedNodeMap attrMap) {
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.ThisObject"));
		list.add(topItem);
		Node node;
		PropertyItem item;
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_NAME);
		item = new PropertyItem(ApplicationResource
				.getResource("Property.ObjectName"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_DEF_TYPE);
		item = new PropertyItem(ApplicationResource
				.getResource("Property.DefinedType"), getNodeValue(node));
		list.add(item);
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_ID);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectId"), node.getNodeValue());
			list.add(item);
		}
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_REAL_TYPE);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectType"), node.getNodeValue());
			list.add(item);
		}
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_ITEM_NUMBER);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ItemNumber"), node.getNodeValue());
			list.add(item);
		}
		return list;
	}

	/**
	 * ノードはInstanceオブジェクトである場合
	 *
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem> createInstance(NamedNodeMap attrMap) {
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.Instance"));
		list.add(topItem);
		Node node;
		PropertyItem item;

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_ID);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectId"), node.getNodeValue());
			list.add(item);
		}
		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_REAL_TYPE);
		String type = node.getNodeValue();
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectType"), type);
			list.add(item);
		}

		if("java.lang.String".equals(type)){
			node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_VALUE);
			if (node != null) {
				String hexValue =  node.getNodeValue();
				item = new PropertyItem(ApplicationResource
						.getResource("Property.ValueInHex"), hexValue);
				list.add(item);
				String utfValue = HelperFunc.getStringFromNumber(hexValue);
				item = new PropertyItem(ApplicationResource
						.getResource("Property.Value"), utfValue);
				list.add(item);
			}
		}

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_OBJECTSIZE);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectSize"), node.getNodeValue());
			list.add(item);
		}


		return list;
	}

	/**
	 * 例外オブジェクトの属性
	 * @version 3.0
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem>  createException(NamedNodeMap attrMap){
//		String type = null;
		Node node;
		PropertyItem item;
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.StackTrace.ExceptionName"));
		list.add(topItem);

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_DEF_TYPE);
		if(node != null){
			item = new PropertyItem(ApplicationResource
					.getResource("Property.DefinedType"), getNodeValue(node));
			list.add(item);
		}

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_REAL_TYPE);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectType"), node.getNodeValue());
			list.add(item);
		}

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_ID);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectId"), node.getNodeValue());
			list.add(item);
		}
		return list;
	}

	/**
	 * スレッド・モニター
	 * @version 3.0
	 * @param attrMap
	 * @return
	 */
	private static List<PropertyItem>  createContentMonitor(NamedNodeMap attrMap){
		String type = null;
		Node node;
		PropertyItem item;
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.ThreadMonitor"));
		list.add(topItem);

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_NAME);
		if (node != null){
			item = new PropertyItem(ApplicationResource
					.getResource("Property.VariantName"), getNodeValue(node));
			list.add(item);
		}

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_USETHREADNAME);
		if (node != null){
			item = new PropertyItem(ApplicationResource
					.getResource("Property.UseThreadName"), getNodeValue(node));
			list.add(item);
		}

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_DEF_TYPE);
		if (node != null){
			item = new PropertyItem(ApplicationResource
					.getResource("Property.DefinedType"), getNodeValue(node));
			list.add(item);
		}

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_REAL_TYPE);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectType"), node.getNodeValue());
			list.add(item);
		}

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_VALUE);
		type = getNodeValue(node);
		addNodeValueToList(type, list, node);

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_ID);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectId"), node.getNodeValue());
			list.add(item);
		}


		return list;
	}

	private static List<PropertyItem>  createReturn(NamedNodeMap attrMap){
		String type = null;
		Node node;
		PropertyItem item;
		List<PropertyItem> list = new ArrayList<PropertyItem>();
		PropertyItem topItem = new PropertyItem(ApplicationResource
				.getResource("Property.Kind"), ApplicationResource
				.getResource("Property.ReturnName"));
		list.add(topItem);

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_NAME);
		if (node != null){
			item = new PropertyItem(ApplicationResource
					.getResource("Property.VariantName"), getNodeValue(node));
			list.add(item);
		}

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_DEF_TYPE);
		type = getNodeValue(node);			//障害 #456
		item = new PropertyItem(ApplicationResource
				.getResource("Property.DefinedType"), getNodeValue(node));
		list.add(item);

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_REAL_TYPE);
		if (node != null) {
			type = getNodeValue(node);	  //障害 #456
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectType"), node.getNodeValue());
			list.add(item);
		}

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_ID);
		if (node != null) {
			item = new PropertyItem(ApplicationResource
					.getResource("Property.ObjectId"), node.getNodeValue());
			list.add(item);
		}

		node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_VALUE);
		addNodeValueToList(type, list, node);
		return list;
	}

	/**
	 * バイトコードのライン情報を追加
	 *
	 * @param list
	 * @param attrMap
	 */
	private static void addLocationNum(List<PropertyItem> list, NamedNodeMap attrMap) {
		Node node = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_LOCATION);
		PropertyItem item = new PropertyItem(ApplicationResource
				.getResource("Property.bytecodeLine"), node.getNodeValue());
		list.add(item);
	}

	/**
	 * 表示用の値に変換
	 *
	 * @param value
	 * @param type
	 * @return
	 */
	public static String convertValue(String value, String type, boolean inHex) {
		if (value == null) {
			return "null";
		}
		if ("null".equals(value)) {
			return value;
		}

		if (isChar(type)) {
			if (inHex) {
				return value;
			} else {
				return "\'"	+ HelperFunc.getStringFromNumber(value) + "\'";
			}
		}

		if (isPrimitiveTypeOtherThanChar(type)) {
			return value;
		} else {
			if (inHex) {
				return value;
			} else {
				return "\""	+ HelperFunc.getStringFromNumber(value) + "\"";
			}
		}
	}

	private static boolean isPrimitiveTypeOtherThanChar(String type) {
		return "int".equals(type) || "long".equals(type)
				|| "boolean".equals(type) || "short".equals(type)
				|| "float".equals(type) || "double".equals(type)
				|| "byte".equals(type);
	}

	private static boolean isChar(String type) {
		return "char".equals(type);
	}

	private static boolean isString(String type) {
		return !isChar(type) && !isPrimitiveTypeOtherThanChar(type);
	}
}