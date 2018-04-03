package jp.co.ipride.excat.analyzer.viewer.propertyviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import jp.co.ipride.excat.analyzer.common.DumpDocument;
import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.w3c.dom.Node;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.NodeList;
import org.dom4j.io.DOMReader;

/**
 * XML内容を構築するヘルパークラス
 *
 * @author sai
 * @since 2009/10/15
 */
public class DomBuildHelper {
	// DOMのパッケージ名
	private static final String PACKAGE_PRE = "org.apache.xerces.dom";
	private static final String PACKAGE_PRE_SUN = "com.sun.org.apache.xerces.internal.dom";
	private static final String DIVISION = ".";

	// DOMの各実装クラス名
	public static final String NOTATION_NODE_TYPE = "DeferredNotationImpl";
	public static final String PROCESSING_INSTRUCTION_NODE_TYPE = "DeferredProcessingInstructionImpl";
	public static final String PROCESSING_INSTRUCTION_SUPER_NODE_TYPE = "ProcessingInstructionImpl";
	public static final String ENTITY_REFERENCE_NODE_TYPE = "DeferredEntityReferenceImpl";
	public static final String ENTITY_NODE_TYPE = "DeferredEntityImpl";
	public static final String DOCUMENT_NODE_TYPE = "DeferredDocumentImpl";
	public static final String DOCUMENTTYPE_NODE_TYPE = "DeferredDocumentTypeImpl";
	public static final String DOCUMENTTYPE_SUPER_NODE_TYPE = "DocumentTypeImpl";
	public static final String CORE_DOCUMENT_NODE_TYPE = "CoreDocumentImpl";
	public static final String COMMENT_NODE_TYPE = "DeferredCommentImpl";
	public static final String CDATA_NODE_TYPE = "DeferredCDATASectionImpl";
	public static final String TEXT_NODE_TYPE = "DeferredTextImpl";
	public static final String CHARACTER_DATA_NODE_TYPE = "CharacterDataImpl";
	public static final String ATTR_NODE_TYPE = "DeferredAttrImpl";
	public static final String ELEMENT_NODE_TYPE = "DeferredElementImpl";
	public static final String ELEMENT_SUPER_NODE_TYPE = "ElementImpl";

	// 各情報が含まれる属性名
	public static final String PROCESSING_INSTRUCTION_ATTR_TARGET = "target";
	public static final String DOCUMENTTYPE_ATTR_NAME = "name";
	public static final String DOCUMENTTYPE_ATTR_INTERNAL_SUBSET = "internalSubset";
	public static final String DOCUMENTTYPE_ATTR_SYSTEM_ID = "systemID";
	public static final String DOCUMENTTYPE_ATTR_PUBLIC_ID = "publicID";
	public static final String XML_ATTR_ENCODING = "encoding";
	public static final String ELEMENT_ATTR_VALUE = "value";
	public static final String PROCESSING_INSTRUCTION_ATTR_DATA = "data";

	// XML情報配列のフィールド名
	public static final String FNODE_EXTRA = "fNodeExtra";
	public static final String FNODE_URI = "fNodeURI";
	public static final String FNODE_PARENT = "fNodeParent";
	public static final String FNODE_PREV_SIB = "fNodePrevSib";
	public static final String FNODE_LAST_CHILD = "fNodeLastChild";
	public static final String FNODE_VALUE = "fNodeValue";
	public static final String FNODE_NAME = "fNodeName";
	public static final String FNODE_TYPE = "fNodeType";
	public static final String FNODE_INDEX = "fNodeIndex";

	public static final int DEFAULT_VALUE = -1;
	public static final int INVALID_NODE_TYPE = -2;

	// クラスフール名コレクション
	public static Map<String, String> CLASS_NAMES = new HashMap<String, String>();
	public static Map<String, String> CLASS_NAMES_SUN = new HashMap<String, String>();

	private static final int FLG_INT = 0;
	private static final int FLG_OBJ = 1;
	private static Map<String, Node> objPoolById = new HashMap<String, Node>();
	private static Map<String, org.dom4j.Element> objPoolByNodeIndex = new HashMap<String, org.dom4j.Element>();

	static {
		// クラスフール名コレクションの初期化
		CLASS_NAMES.put(DOCUMENT_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(DOCUMENT_NODE_TYPE));
		CLASS_NAMES.put(NOTATION_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(NOTATION_NODE_TYPE));
		CLASS_NAMES.put(PROCESSING_INSTRUCTION_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(PROCESSING_INSTRUCTION_NODE_TYPE));
		CLASS_NAMES.put(PROCESSING_INSTRUCTION_SUPER_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(PROCESSING_INSTRUCTION_SUPER_NODE_TYPE));
		CLASS_NAMES.put(ENTITY_REFERENCE_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(ENTITY_REFERENCE_NODE_TYPE));
		CLASS_NAMES.put(ENTITY_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(ENTITY_NODE_TYPE));
		CLASS_NAMES.put(DOCUMENTTYPE_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(DOCUMENTTYPE_NODE_TYPE));
		CLASS_NAMES.put(DOCUMENTTYPE_SUPER_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(DOCUMENTTYPE_SUPER_NODE_TYPE));
		CLASS_NAMES.put(CORE_DOCUMENT_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(CORE_DOCUMENT_NODE_TYPE));
		CLASS_NAMES.put(COMMENT_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(COMMENT_NODE_TYPE));
		CLASS_NAMES.put(CDATA_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(CDATA_NODE_TYPE));
		CLASS_NAMES.put(TEXT_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(TEXT_NODE_TYPE));
		CLASS_NAMES.put(CHARACTER_DATA_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(CHARACTER_DATA_NODE_TYPE));
		CLASS_NAMES.put(ATTR_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(ATTR_NODE_TYPE));
		CLASS_NAMES.put(ELEMENT_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(ELEMENT_NODE_TYPE));
		CLASS_NAMES.put(ELEMENT_SUPER_NODE_TYPE, PACKAGE_PRE.concat(DIVISION).concat(ELEMENT_SUPER_NODE_TYPE));
		CLASS_NAMES_SUN.put(DOCUMENT_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(DOCUMENT_NODE_TYPE));
		CLASS_NAMES_SUN.put(NOTATION_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(NOTATION_NODE_TYPE));
		CLASS_NAMES_SUN.put(PROCESSING_INSTRUCTION_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(PROCESSING_INSTRUCTION_NODE_TYPE));
		CLASS_NAMES_SUN.put(PROCESSING_INSTRUCTION_SUPER_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(PROCESSING_INSTRUCTION_SUPER_NODE_TYPE));
		CLASS_NAMES_SUN.put(ENTITY_REFERENCE_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(ENTITY_REFERENCE_NODE_TYPE));
		CLASS_NAMES_SUN.put(ENTITY_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(ENTITY_NODE_TYPE));
		CLASS_NAMES_SUN.put(DOCUMENTTYPE_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(DOCUMENTTYPE_NODE_TYPE));
		CLASS_NAMES_SUN.put(DOCUMENTTYPE_SUPER_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(DOCUMENTTYPE_SUPER_NODE_TYPE));
		CLASS_NAMES_SUN.put(CORE_DOCUMENT_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(CORE_DOCUMENT_NODE_TYPE));
		CLASS_NAMES_SUN.put(COMMENT_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(COMMENT_NODE_TYPE));
		CLASS_NAMES_SUN.put(CDATA_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(CDATA_NODE_TYPE));
		CLASS_NAMES_SUN.put(TEXT_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(TEXT_NODE_TYPE));
		CLASS_NAMES_SUN.put(CHARACTER_DATA_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(CHARACTER_DATA_NODE_TYPE));
		CLASS_NAMES_SUN.put(ATTR_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(ATTR_NODE_TYPE));
		CLASS_NAMES_SUN.put(ELEMENT_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(ELEMENT_NODE_TYPE));
		CLASS_NAMES_SUN.put(ELEMENT_SUPER_NODE_TYPE, PACKAGE_PRE_SUN.concat(DIVISION).concat(ELEMENT_SUPER_NODE_TYPE));
	}

	/**
	 * オブジェクトプールを整理するメソッド
	 *
	 * @param rootElement Documentオブジェクト
	 * @throws ParserConfigurationException
	 */
	public static void packObjectPool(org.dom4j.Element rootElement)
		throws ParserConfigurationException {

		// 子ノードのリストを取得する。
		NodeList list = DumpDocument.getPoolNode().getChildNodes();
		for (int i = 0; i < list.getLength(); i++) {
			Node node = list.item(i);
			NamedNodeMap attrMap = node.getAttributes();
			if (attrMap != null) {
				Node item = attrMap
						.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_ID);
				if (item != null && item.getNodeValue().length() != 0) {
					// ID属性の値をキーとしてノードをマップに保存する。
					objPoolById.put(item.getNodeValue(), node);
				}
				item = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_REAL_TYPE);
				if (item != null && item.getNodeValue() != null
						&& (isDomClass(item.getNodeValue()))) {
					org.dom4j.Element element = convertDomObject(node);
					List<org.dom4j.Element> listAttr = element
							.elements(DumpFileXmlConstant.NODE_ATTRIBUTE);
					for (int j = 0; j < listAttr.size(); j++) {
						if (FNODE_INDEX.equals(listAttr.get(j).attributeValue(
								DumpFileXmlConstant.ATTR_NAME))) {
							String index = listAttr.get(j).attributeValue(
									DumpFileXmlConstant.ATTR_VALUE);
							if (index != null) {
								// fNodeIndex属性の値をキーとしてノードをマップに保存する。
								objPoolByNodeIndex.put(index, element);
							}
						}
					}
				}
			}
		}
		if (rootElement != null) {
			// Documentノードを保存する。
			objPoolByNodeIndex.put(String.valueOf(0), rootElement);
		}
	}

	/**
	 * クラス名はDOMの実装クラスであるかを判断するメソッド
	 *
	 * @param className クラス名
	 * @return DOMの実装クラスの場合：True、以外の場合：Flase
	 */
	private static boolean isDomClass(String className) {
		return CLASS_NAMES.values().contains(className) || CLASS_NAMES_SUN.values().contains(className);
	}

	/**
	 * org.w3c.dom.Elementからorg.dom4j.Elementへ変換するメソッド
	 *
	 * @param node org.w3c.dom.Elementオブジェクト
	 * @return org.dom4j.Elementオブジェクト
	 * @throws ParserConfigurationException
	 */
	public static org.dom4j.Element convertDomObject(Node node) throws ParserConfigurationException {
		// org.w3c.dom.Elementからorg.dom4j.Elementへ変換する。
		if (node instanceof Element) {
			DocumentBuilderFactory factory = DocumentBuilderFactory
					.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();

			org.w3c.dom.Document doc = builder.newDocument();
			Node newNode = doc.importNode(node, true);
			doc.appendChild(newNode);
			DOMReader reader = new DOMReader();
			org.dom4j.Document doc2 = reader.read(doc);

			return doc2.getRootElement();
		}
		return null;
	}

	/**
	 * XML情報が格納されている配列の内容を取得するメソッド
	 *
	 * @param node Documentオブジェクト
	 * @return 配列内容を保持するBeanクラス
	 * @throws ParserConfigurationException
	 */
	public static DomArrayBean getDomArray(org.dom4j.Element node) throws ParserConfigurationException {
		List<org.dom4j.Element> attrNodeList = getChildren(node,
				DumpFileXmlConstant.NODE_ATTRIBUTE);
		// XML情報配列の内容を取得して、Beanクラスに保存する。
		DomArrayBean bean = new DomArrayBean();
		for (int i = 0; i < attrNodeList.size(); i++) {
			org.dom4j.Element attrNode = attrNodeList.get(i);
			String attrValue = getAttributeValue(attrNode,
					DumpFileXmlConstant.ATTR_NAME);
			if (attrValue == null)
				continue;

			if (attrValue.equals(FNODE_TYPE)) {
				bean.setFNodeType((int[][]) buildArray(attrNode,
						DomBuildHelper.FLG_INT));
			} else if (attrValue.equals(FNODE_NAME)) {
				bean.setFNodeName((Object[][]) buildArray(attrNode,
						DomBuildHelper.FLG_OBJ));
			} else if (attrValue.equals(FNODE_VALUE)) {
				bean.setFNodeValue((Object[][]) buildArray(attrNode,
						DomBuildHelper.FLG_OBJ));
			} else if (attrValue.equals(FNODE_LAST_CHILD)) {
				bean.setFNodeLastChild((int[][]) buildArray(attrNode,
						DomBuildHelper.FLG_INT));
			} else if (attrValue.equals(FNODE_PREV_SIB)) {
				bean.setFNodePrevSib((int[][]) buildArray(attrNode,
						DomBuildHelper.FLG_INT));
			} else if (attrValue.equals(FNODE_PARENT)) {
				bean.setFNodeParent((int[][]) buildArray(attrNode,
						DomBuildHelper.FLG_INT));
			} else if (attrValue.equals(FNODE_URI)) {
				bean.setFNodeURI((Object[][]) buildArray(attrNode,
						DomBuildHelper.FLG_OBJ));
			} else if (attrValue.equals(FNODE_EXTRA)) {
				bean.setFNodeExtra((int[][]) buildArray(attrNode,
						DomBuildHelper.FLG_INT));
			}
		}
		return bean;
	}

	/**
	 * Domオブジェクトから配列の内容を復元するメソッド
	 *
	 * @param attrNode Domオブジェクト
	 * @param flag 配列内容のタイプ
	 * @return 復元した配列
	 * @throws ParserConfigurationException
	 */
	private static Object[] buildArray(org.dom4j.Element attrNode, int flag)
		throws ParserConfigurationException {

		// Dumpファイルから各XML情報配列のフィールド名によって、内容を抽出して、配列を復元する。
		Object[] typeArray = null;
		if (getAttributeValue(attrNode, DumpFileXmlConstant.ATTR_OBJECT_REF) != null) {
			// 参照先Elementを取得する。
			org.dom4j.Element refNode = getRefObjectNode(attrNode);
			if (refNode == null)
				return null;

			// サイズ情報を取得して、配列を作成する。
			int size = Integer.parseInt(getAttributeValue(refNode,
					DumpFileXmlConstant.ATTR_ITEM_NUMBER));
			if (flag == DomBuildHelper.FLG_INT) {
				typeArray = (Object[]) new int[size][];
			} else {
				typeArray = new Object[size][];
			}

			// 作成した配列に情報を埋め込む。
			List<org.dom4j.Element> itemList = getChildren(refNode,
					DumpFileXmlConstant.NODE_ITEM);
			for (int j = 0; j < itemList.size(); j++) {
				org.dom4j.Element itemNode = itemList.get(j);
				if (getAttributeValue(itemNode,
						DumpFileXmlConstant.ATTR_OBJECT_REF) != null) {
					// 参照先Elementから内容を取得する。
					org.dom4j.Element refNodeArray = getRefObjectNode(itemNode);
					if (refNodeArray != null) {
						int sizeArray = Integer.parseInt(getAttributeValue(
								refNodeArray,
								DumpFileXmlConstant.ATTR_ITEM_NUMBER));
						Object typeArrayItem = null;
						if (flag == DomBuildHelper.FLG_INT) {
							typeArrayItem = (Object) new int[sizeArray];
						} else {
							typeArrayItem = new Object[sizeArray];
						}

						List<org.dom4j.Element> itemList2 = getChildren(
								refNodeArray, DumpFileXmlConstant.NODE_ITEM);
						int nullIndex = 0;
						for (int k = 0; k < itemList2.size(); k++) {
							org.dom4j.Element itemNode2 = itemList2.get(k);
							if (flag == DomBuildHelper.FLG_INT) {
								int itemValue = Integer
										.parseInt(getAttributeValue(itemNode2,
												DumpFileXmlConstant.ATTR_VALUE));
								((int[]) typeArrayItem)[k] = itemValue;
							} else {
								String itemValue = null;
								if (getAttributeValue(itemNode2,
										DumpFileXmlConstant.ATTR_OBJECT_REF) != null) {
									// 参照先Elementから内容を取得する。
									itemValue = getValueFromRefObj(itemNode2);
								}
								((Object[]) typeArrayItem)[k] = itemValue;
							}
							nullIndex = k;
						}

						// 空の要素に対して、デフォルト値を設定する。
						for (int k = nullIndex; k < sizeArray; k++) {
							if (flag == DomBuildHelper.FLG_INT) {
								((int[]) typeArrayItem)[k] = DEFAULT_VALUE;
							} else {
								((Object[]) typeArrayItem)[k] = null;
							}
						}

						typeArray[j] = typeArrayItem;
					}
				}
			}

			return typeArray;
		}

		return null;
	}

	/**
	 * 指定されるElementの親クラスの属性を取得するメソッド
	 *
	 * @param targetElement 指定されるElement
	 * @param superClassName 指定される親クラス名
	 * @param attrName 取得する属性名
	 * @return 属性値
	 * @throws ParserConfigurationException
	 */
	public static String getSuperAttributeFromDomObj(
			org.dom4j.Element targetElement, String superClassName,
			String attrName) throws ParserConfigurationException {
		// 親クラスの属性を表すノードを取得する。
		org.dom4j.Element elementAttr =
			getRefObjectNodeByAttribute(targetElement, superClassName, attrName);
		if (elementAttr != null) {
			// 属性値を取得する。
			return getValueFromRefObj(elementAttr);
		}
		return null;
	}

	/**
	 * 指定されるElementの親クラスの属性に参照されるElementを取得するメソッド
	 *
	 * @param targetElement 指定されるElement
	 * @param superClassName 指定される親クラス名
	 * @param attrName 指定される属性名
	 * @return 参照されるElement
	 */
	public static org.dom4j.Element getRefObjectNodeByAttribute(
			org.dom4j.Element targetElement, String superClassName,
			String attrName) {
		// 子Elementを取得する。
		List<org.dom4j.Element> list = getChildren(targetElement,
				DumpFileXmlConstant.NODE_SUPERCLASS);
		for (int i = 0; i < list.size(); i++) {
			org.dom4j.Element array_element = list.get(i);
			String sig = getAttributeValue(array_element,
					DumpFileXmlConstant.ATTR_SIG);
			if (sig != null && checkEquals(sig, superClassName)) {
				// 親クラスの属性を表すElementのリストを取得する。
				List<org.dom4j.Element> listAttr = getChildren(array_element,
						DumpFileXmlConstant.NODE_ATTRIBUTE);
				for (int j = 0; j < listAttr.size(); j++) {
					org.dom4j.Element elementAttr = listAttr.get(j);
					String name = getAttributeValue(elementAttr,
							DumpFileXmlConstant.ATTR_NAME);
					if (name != null && name.equals(attrName)) {
						// 属性名が指定された属性名と一致する場合、当該Elementを返却する。
						return elementAttr;
					}
				}
			} else {
				// 親クラスの親クラスを再帰的に探す。
				return getRefObjectNodeByAttribute(array_element, superClassName, attrName);
			}
		}
		return null;
	}

	/**
	 * クラス名の一致判定用メソッド
	 *
	 * @param className 実際のクラス名
	 * @param classNameTag 指定したクラス名
	 * @return 一致の場合：True、不一致の場合、False
	 */
	public static boolean checkEquals(String className, String classNameTag) {
		return className.equals(DomBuildHelper.CLASS_NAMES.get(classNameTag))
				|| className.equals(DomBuildHelper.CLASS_NAMES_SUN.get(classNameTag));
	}

	/**
	 * 指定されるノード順番からElementを取得するメソッド
	 *
	 * @param currentIndex ノード順番
	 * @return Elementオブジェクト
	 */
	public static org.dom4j.Element getDomObject(int currentIndex) {
		return objPoolByNodeIndex.get(String.valueOf(currentIndex));
	}


	/**
	 * Element対象のテキスト（data属性）を取得するメソッド
	 *
	 * @param element 指定されるElementオブジェクト
	 * @return 属性「data」の値
	 * @throws ParserConfigurationException
	 */
	public static String getElementText(org.dom4j.Element element) throws ParserConfigurationException {
		List<org.dom4j.Element> list = getElementAttrList(element);
		for (int i = 0; i < list.size(); i++) {
			org.dom4j.Element attrElement = list.get(i);
			String name = getAttributeValue(attrElement,
					DumpFileXmlConstant.ATTR_NAME);
			if (name != null && name.equals(PROCESSING_INSTRUCTION_ATTR_DATA)) {
				return getValueFromRefObj(attrElement);
			}
		}
		return null;
	}


	/**
	 * 指定されるElementの参照先Elementから属性「Value」の値を取得するメソッド
	 *
	 * @param attrElement 指定されるElement
	 * @return 属性「Value」の値
	 * @throws ParserConfigurationException
	 */
	private static String getValueFromRefObj(org.dom4j.Element attrElement) throws ParserConfigurationException {
		org.dom4j.Element elementAttrRef = getRefObjectNode(attrElement);
		if (elementAttrRef != null) {
			String itemValue = getAttributeValue(elementAttrRef,
					DumpFileXmlConstant.ATTR_VALUE);
			if (itemValue != null) {
				return HelperFunc.getStringFromNumber(itemValue);
			}
		}
		return null;
	}


	/**
	 * Elementオブジェクトの属性ノードを取得するメソッド
	 *
	 * @param element Elementオブジェクト
	 * @return 属性ノードのリスト
	 */
	private static List<org.dom4j.Element> getElementAttrList(
			org.dom4j.Element element) {
		ArrayList<org.dom4j.Element> list = new ArrayList<org.dom4j.Element>();
		list.addAll(getChildren(element, DumpFileXmlConstant.NODE_ATTRIBUTE));
		List<org.dom4j.Element> elementSupers = getChildren(element,
				DumpFileXmlConstant.NODE_SUPERCLASS);
		for (int i = 0; i < elementSupers.size(); i++) {
			org.dom4j.Element elementSuper = elementSupers.get(i);
			list.addAll(getElementAttrList(elementSuper));
		}

		return list;
	}

	/**
	 * 属性情報を取得するメソッド
	 *
	 * @param element 属性情報が格納されているElementオブジェクト
	 * @return 属性内容の配列　[0]:属性名、[1]:属性値
	 * @throws ParserConfigurationException
	 */
	public static String[] getElementAttibute(org.dom4j.Element element) throws ParserConfigurationException {
		String attrValue = null;
		String attrName = null;
		List<org.dom4j.Element> list = getElementAttrList(element);
		for (int i = 0; i < list.size(); i++) {
			org.dom4j.Element attrElement = list.get(i);
			String name = getAttributeValue(attrElement,
					DumpFileXmlConstant.ATTR_NAME);
			if (name != null && name.equals(DOCUMENTTYPE_ATTR_NAME)) {
				attrName = getValueFromRefObj(attrElement);
			}
			name = getAttributeValue(attrElement, DumpFileXmlConstant.ATTR_NAME);
			if (name != null && name.equals(ELEMENT_ATTR_VALUE)) {
				attrValue = getValueFromRefObj(attrElement);
			}
		}

		if (attrName != null && attrValue != null) {
			return new String[] { attrName, attrValue };
		}
		return null;
	}

	/**
	 * 参照先Elementオブジェクトを取得するメソッド
	 *
	 * @param referenceNode 参照元Elementオブジェクト
	 * @return 参照先Elementオブジェクト
	 * @throws ParserConfigurationException
	 */
	public static org.dom4j.Element getRefObjectNode(
			org.dom4j.Element referenceNode) throws ParserConfigurationException {
		String objectId = getAttributeValue(referenceNode,
				DumpFileXmlConstant.ATTR_OBJECT_REF);

		return convertDomObject(objPoolById.get(objectId));
	}

	/**
	 * 指定名前で子Elementオブジェクトを取得するメソッド
	 *
	 * @param node 親Elementオブジェクト
	 * @param name 子Elementの名前
	 * @return 子Elementオブジェクトのリスト
	 */
	private static List<org.dom4j.Element> getChildren(org.dom4j.Element node,
			String name) {
		List<org.dom4j.Element> attrNodeList = node.elements(name);
		return attrNodeList;
	}

	/**
	 * すべての子Elementオブジェクトを取得するメソッド
	 *
	 * @param node 親Elementオブジェクト
	 * @return 子Elementオブジェクトのリスト
	 */
	private static List<org.dom4j.Element> getChildren(org.dom4j.Element node) {
		List<org.dom4j.Element> list = node.elements();
		return list;
	}

	/**
	 * 属性値を取得するメソッド
	 *
	 * @param objNode Elementオブジェクト
	 * @param name 属性名
	 * @return 属性値
	 */
	public static String getAttributeValue(org.dom4j.Element objNode,
			String name) {
		String value = objNode.attributeValue(name);
		return value;
	}

	/**
	 * ObjectIdを取得するメソッド
	 *
	 * @param node Objectノード
	 * @return ObjectIdの値
	 */
	public static String getObjectId(Node node) {
		if (node == null) {
			return null;
		}
		NamedNodeMap attrMap = node.getAttributes();
		if (attrMap != null) {
			Node item = attrMap.getNamedItem(DumpFileXmlConstant.ATTR_OBJECT_ID);
			if (item != null && item.getNodeValue().length() != 0) {
				return item.getNodeValue();
			}
		}
		return null;
	}
}
