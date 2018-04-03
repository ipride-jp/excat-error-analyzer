/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.common;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * ダンプファイルのﾄﾞｷｭﾒﾝﾄ
 *
 * @author 屠偉新
 * @since 2006/9/17
 */
public class DumpDocument {

	static String  CHECK_WORKS = "<?xml version=\"1";

	// ダンプファイルのXMLﾄﾞｷｭﾒﾝﾄ
	private static Document document = null;

	private static Node poolNode = null;

	/**
	 * create document
	 *
	 * @param dump
	 *            file path
	 */
	public static boolean  createDocument(String path) throws Exception {

		document = null;
		poolNode = null;

		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		String result = DumpFile.getDumpFileContent(path);
		if (result != null){
			document = builder.parse(new InputSource(new StringReader(result)));
			poolNode = getPoolNode();
			return true;
		}else{
			return false;
		}
	}


	/**
	 * get document
	 *
	 * @return document
	 */
	public static Document getDocument() {
		return document;
	}

	/**
	 * get node by object id
	 *
	 * @param reference
	 *            Node
	 * @return object node
	 */
	public static Node getObjectNode(Node referenceNode) {
		NamedNodeMap map = referenceNode.getAttributes();
		Node attrNode = map.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_REF);
		String objectId = attrNode.getNodeValue();

		NodeList list = poolNode.getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			NamedNodeMap attrMap = node.getAttributes();
			if (attrMap != null) {
				Node item = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_ID);
				if (item != null && item.getNodeValue().equals(objectId)) {
					return node;
				}
			}
		}
		return null;
	}

	/**
	 * Poolから該当ノードの実態を取得
	 *
	 * @param node
	 *            空ノード
	 * @return
	 */
	public static Node replaceObject(Node node) {
		NamedNodeMap map = node.getAttributes();
		if (map == null) {
			return node;
		}
		Node item;
		item = map.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_REF);
		if (item == null) {
			return node;
		}
		Node objectNode = getObjectNode(node);
		NamedNodeMap objectAttrs = objectNode.getAttributes();
		NodeList objectNodeList = objectNode.getChildNodes();

		// create a replace node
		Node newNode = node.cloneNode(false);
		Node parentNode = node.getParentNode();
		if (parentNode != null) {
			parentNode.replaceChild(newNode, node);
		}

		// add children-node of object-node
		if (objectNodeList != null) {
			for (int i = 0; i < objectNodeList.getLength(); i++) {
				newNode.appendChild(objectNodeList.item(i).cloneNode(true));
			}
		}

		// add attributes of object-node
		newNode.getAttributes().setNamedItem(
				objectAttrs.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_ID).cloneNode(
						true));

		newNode.getAttributes().setNamedItem(
				objectAttrs.getNamedItem(DumpFileXmlConstant.ATTR_REAL_TYPE).cloneNode(
						true));
		Node value = objectAttrs.getNamedItem(DumpFileXmlConstant.ATTR_VALUE);
		if (value != null) {
			newNode.getAttributes().setNamedItem(value.cloneNode(true));
		}

		//add itemnumber,added by jiang on 2007/04/09
		Node itemNumber =  objectAttrs.getNamedItem(DumpFileXmlConstant.ATTR_ITEM_NUMBER);
		if (itemNumber != null) {
			newNode.getAttributes().setNamedItem(itemNumber.cloneNode(true));
		}
		return newNode;
	}

	/**
	 * get object pool
	 *
	 * @param node
	 * @return
	 */
	public static Node getPoolNode() {
		NodeList nodeList = document.getChildNodes().item(0).getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node stackNode = nodeList.item(i);
			if (DumpFileXmlConstant.NODE_STACKTRACE.equals(stackNode.getNodeName())) {
				NodeList list = stackNode.getChildNodes();
				for (int j = 0; j < list.getLength(); j++) {
					Node item = list.item(j);
					if (DumpFileXmlConstant.NODE_OBJECT_POOL.equals(item.getNodeName())) {
						return item;
					}
				}
			}
		}
		return null;
	}

	public static void clear(){
		poolNode = null;
		document = null;
	}

}
