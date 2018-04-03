package jp.co.ipride.excat.analyzer.viewer.propertyviewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;

import org.dom4j.Element;
import org.dom4j.tree.DefaultAttribute;
import org.dom4j.tree.DefaultCDATA;
import org.dom4j.tree.DefaultComment;
import org.dom4j.tree.DefaultDocumentType;
import org.dom4j.tree.DefaultElement;
import org.dom4j.tree.DefaultProcessingInstruction;
import org.dom4j.tree.DefaultText;
import org.w3c.dom.Node;

/**
 * XML内容を構築するビルダークラス
 *
 * @author sai
 * @since 2009/10/15
 */
public class DomBuilder {
	private boolean isCompletely = true;
	private DomArrayBean domArray = null;
	private Node currentNode = null;
	private Map<String, org.dom4j.Document> documentMap = new HashMap<String, org.dom4j.Document>();
	private Map<String, Map<String, org.dom4j.Element>> elementMap = new HashMap<String, Map<String, org.dom4j.Element>>();

	/**
	 * Document対象のXML文書を取得するメソッド
	 *
	 * @param node Document情報が格納されているノード
	 * @return Documentオブジェクトから復元したXML文書
	 */
	public String getDocumentXml(Node node) {
		this.currentNode = node;
		try {
			if (getDocument().getRootElement() == null) {
				// キャッシュがない場合、配列やオブジェクトに格納された情報からXML文書を復元する。
				// 選択したノードをDOM4JのElementオブジェクトに変換する。
				org.dom4j.Element jNode = DomBuildHelper.convertDomObject(node);

				// ObjectPoolを整理する。
				DomBuildHelper.packObjectPool(jNode);

				// Documentオブジェクトを構築する。
				buildDocumentObject(jNode);
			}

			// 構築したDocumentオブジェクトのXML文書を返却する。
			this.isCompletely = true;
			return getDocument().asXML();

		} catch (Exception e) {
			// 例外が発生した場合、完全性フラグをFalseに設定する。
			this.isCompletely = false;
			MainViewer.win.logException(e);
		}

		return null;
	}

	/**
	 * Documentオブジェクトを構築するメソッド
	 *
	 * @param rootNode トップレベルのドキュメントオブジェクト
	 * @throws ParserConfigurationException
	 */
	private void buildDocumentObject(org.dom4j.Element rootNode)
			throws ParserConfigurationException {
		// XML情報格納用の配列の内容を取得する。
		domArray = DomBuildHelper.getDomArray(rootNode);

		// XML文書を構築する。
		buildContent(getDocument(), null, domArray, 0, true);
	}

	/**
	 * Element対象のXML文書を取得するメソッド
	 *
	 * @param node Element情報が格納されているノード
	 * @return Elementオブジェクトから復元したXML文書
	 */
	public String getElementXml(Node node) {
		org.dom4j.Element elementNode = null;

		try {
			if (getDocument().getRootElement() == null) {
				// キャッシュがない場合、配列やオブジェクトに格納された情報からXML文書を復元する。
				// 選択したノードをDOM4JのElementオブジェクトに変換する。
				org.dom4j.Element jNode = DomBuildHelper.convertDomObject(node);

				// ObjectPoolを整理する。
				DomBuildHelper.packObjectPool(null);

				// Elementが含まれるトップレベルのドキュメントオブジェクトを取得する。
				org.dom4j.Element ownerDoc = null;
				org.dom4j.Element ownerDocAttribute = DomBuildHelper.getRefObjectNodeByAttribute(
						jNode, "ParentNode", "ownerDocument");
				if (ownerDocAttribute != null) {
					ownerDoc = DomBuildHelper.getRefObjectNode(ownerDocAttribute);
				}
				if (ownerDoc != null) {
					// Documentオブジェクトを構築する。
					buildDocumentObject(ownerDoc);
				}
			}

			// ドキュメントオブジェクトから当該Element要素を取得する。
			org.dom4j.Element element = DomBuildHelper.convertDomObject(node);
			List<org.dom4j.Element> listAttr = element
					.elements(DumpFileXmlConstant.NODE_ATTRIBUTE);
			for (int j = 0; j < listAttr.size(); j++) {
				if (DomBuildHelper.FNODE_INDEX.equals(listAttr.get(j).attributeValue(
						DumpFileXmlConstant.ATTR_NAME))) {
					String index = listAttr.get(j).attributeValue(
							DumpFileXmlConstant.ATTR_VALUE);
					if (index != null) {
						elementNode = getElementByNodeIndex(index);
					}
				}
			}

			// 取得したElementのXML文書を返却する。
			if (elementNode != null) {
				this.isCompletely = true;
				return elementNode.asXML();
			}
		} catch (Exception e) {
			// 例外が発生した場合、完全性フラグをFalseに設定する。
			this.isCompletely = false;
			MainViewer.win.logException(e);
		}
		return null;
	}

	/**
	 * Document対象を取得するメソッド
	 *
	 * @return Document対象
	 */
	private org.dom4j.Document getDocument(){
		// 選択されているノードのObjectIdによってDocument対象を取得する。
		org.dom4j.Document document = documentMap.get(DomBuildHelper.getObjectId(currentNode));
		if (document == null) {
			// 取得できない場合、新規作成する。
			document = new org.dom4j.tree.DefaultDocument();
			String objectId = DomBuildHelper.getObjectId(currentNode);
			if (objectId != null) {
				documentMap.put(objectId, document);
			}
		}
		return document;
	}

	/**
	 * Element対象を取得するメソッド
	 *
	 * @param index ノードのインデックス
	 * @return Element対象
	 */
	private Element getElementByNodeIndex(String index) {
		return elementMap.get(DomBuildHelper.getObjectId(currentNode)).get(index);
	}

	/**
	 * Element対象を保存するメソッド
	 *
	 * @param index ノードのインデックス
	 * @param element 保存するElement対象
	 */
	private void putElementByNodeIndex(int index,
			org.dom4j.Element element) {
		Map<String, org.dom4j.Element> elements = elementMap.get(DomBuildHelper.getObjectId(currentNode));
		if (elements == null) {
			elements = new HashMap<String, org.dom4j.Element>();
			elementMap.put(DomBuildHelper.getObjectId(currentNode), elements);
		}
		elements.put(String.valueOf(index), element);
	}

	/**
	 * 完全性フラグを取得するメソッド
	 *
	 * @return 完全性フラグ
	 */
	public boolean isCompletely() {
		return isCompletely;
	}

	/**
	 * 完全性フラグを設定するメソッド
	 *
	 * @param isCompletely 完全性フラグ
	 */
	public void setCompletely(boolean isCompletely) {
		this.isCompletely = isCompletely;
	}

	/**
	 * XML文書を構築するメソッド
	 *
	 * @param doc Documentオブジェクト
	 * @param rootNode ルートノード
	 * @param domArray XML情報が格納されている配列Beanクラス
	 * @param indexOfLastChild 最後の子要素のインデックス
	 * @param needBuildSib 兄弟要素の構築要否フラグ
	 */
	private void buildContent(org.dom4j.Document doc,
			org.dom4j.Element rootNode, DomArrayBean domArray,
			int indexOfLastChild, boolean needBuildSib) {
		try {
			DomArrayItemBean item = domArray.getItem(indexOfLastChild);
			boolean needBuildText = true;

			// ノードタイプによってDocumentオブジェクトを構築する。
			switch (item.getFNodeType()) {
			case Node.TEXT_NODE:
				// Textの場合
				String value = (String) item.getFNodeValue();
				if (value != null) {
					if (rootNode == null) {
						// Documentレベルのコメントの場合
						doc.content().add(0, new DefaultText(value));
					} else {
						// Elementレベルのコメントの場合
						rootNode.content().add(0, new DefaultText(value));
					}
				} else {
					// XML情報が配列から取得できない場合、オブジェクトから取得する。
					recoverArray(domArray, indexOfLastChild);
					buildContent(doc, rootNode, domArray, indexOfLastChild, false);
					needBuildText = false;
				}
				break;
			case Node.CDATA_SECTION_NODE:
				// CDATAの場合
				String cdataValue = (String) item.getFNodeValue();
				if (cdataValue != null) {
					if (rootNode == null) {
						// DocumentレベルのCDATAの場合
						doc.content().add(0, new DefaultCDATA(cdataValue));
					} else {
						// ElementレベルのCDATAの場合
						rootNode.content().add(0, new DefaultCDATA(cdataValue));
					}
				} else {
					// XML情報が配列から取得できない場合、オブジェクトから取得する。
					recoverArray(domArray, indexOfLastChild);
					buildContent(doc, rootNode, domArray, indexOfLastChild, false);
				}
				break;
			case Node.COMMENT_NODE:
				// Commentの場合
				String commentValue = (String) item.getFNodeValue();
				if (commentValue != null) {
					if (rootNode == null) {
						doc.content().add(0, new DefaultComment(commentValue));
					} else {
						rootNode.content().add(0, new DefaultComment(commentValue));
					}
				} else {
					recoverArray(domArray, indexOfLastChild);
					buildContent(doc, rootNode, domArray, indexOfLastChild, false);
				}
				break;
			case Node.ATTRIBUTE_NODE:
				// Attributeの場合
				rootNode.attributes().add(0, new DefaultAttribute((String) item.getFNodeName(), (String) item
						.getFNodeValue()));
				break;
			case Node.PROCESSING_INSTRUCTION_NODE:
				// ProcessingInstructionの場合
				if (rootNode == null) {
					doc.content().add(0, new DefaultProcessingInstruction((String) item.getFNodeName(),
							(String) item.getFNodeValue()));
				} else {
					rootNode.content().add(0, new DefaultProcessingInstruction((String) item.getFNodeName(),
							(String) item.getFNodeValue()));
				}
				break;
			case Node.ELEMENT_NODE:
				// Elementの場合
				org.dom4j.Element element = new DefaultElement((String) item
						.getFNodeName());

				// 子Elementを構築する。
				int index = item.getFNodeLastChild();
				if (index > DomBuildHelper.DEFAULT_VALUE) {
					buildContent(doc, element, domArray, index, true);
				}

				// Attributeを構築する。
				int extraIndexOfAttr = item.getFNodeExtra();
				if (extraIndexOfAttr > DomBuildHelper.DEFAULT_VALUE) {
					DomArrayItemBean extraBean = domArray.getItem(extraIndexOfAttr);
					if (extraBean != null) {
						do
			            {
							extraBean = domArray.getItem(extraIndexOfAttr);
							String extraName = (String)extraBean.getFNodeName();
							String extraValue = (String)extraBean.getFNodeValue();
							element.attributes().add(0, new DefaultAttribute(extraName, extraValue));
							extraIndexOfAttr = domArray.getItem(extraIndexOfAttr).getFNodePrevSib();
			            } while(extraIndexOfAttr != DomBuildHelper.DEFAULT_VALUE);
					}
				}

				if (rootNode != null) {
					// 子Elementの場合、親Elementに追加する。
					rootNode.content().add(0, element);
				} else {
					// トップElementの場合、Documentに追加する。
					doc.setRootElement(element);
				}

				// 構築したElementをキャッシュする。
				putElementByNodeIndex(indexOfLastChild, element);
				break;
			case Node.DOCUMENT_TYPE_NODE:
				// DocumentTypeの場合
				DefaultDocumentType docType = new DefaultDocumentType((String) item
						.getFNodeName(), (String) item.getFNodeValue(),
						(String) item.getFNodeURI());
				List<String> list = new ArrayList<String>();
				if (item.getInternalSubset().length() != 0) {
					list.add(item.getInternalSubset());
					docType.setInternalDeclarations(list);
				} else {
					int extraIndex = item.getFNodeExtra();
					if (extraIndex > DomBuildHelper.DEFAULT_VALUE) {
						DomArrayItemBean extraBean = domArray.getItem(extraIndex);
						if (extraBean != null) {
							String extra = (String)extraBean.getFNodeValue();
							list.add(extra);
							docType.setInternalDeclarations(list);
						}
					}
				}
				doc.setDocType(docType);
				break;
			case Node.DOCUMENT_NODE:
				// Documentの場合
				int index1 = item.getFNodeLastChild();
				if (index1 > DomBuildHelper.DEFAULT_VALUE) {
					// 子Elementを構築する。
					buildContent(doc, rootNode, domArray, index1, true);
				}

				// Encoding情報を構築する。
				org.dom4j.Element targetElement = DomBuildHelper.getDomObject(indexOfLastChild);
				String encoding = DomBuildHelper.getSuperAttributeFromDomObj(
						targetElement, DomBuildHelper.CORE_DOCUMENT_NODE_TYPE, DomBuildHelper.XML_ATTR_ENCODING);
				if (encoding != null) {
					doc.setXMLEncoding(encoding);
				}
				break;
			case DomBuildHelper.DEFAULT_VALUE:
				// ノードタイプが判断できない場合、
				// オブジェクトから情報を取得して、XML情報配列に補足する。
				if (indexOfLastChild == 0 || !item.isEmpty()) {
					recoverArray(domArray, indexOfLastChild);
					// item = domArray.getItem(indexOfLastChild);
					buildContent(doc, rootNode, domArray, indexOfLastChild, false);
				}
				break;
			default:
				break;
			}

			// 直前の兄弟ノードを構築する。
			if (needBuildSib) {
				int indexOfPrevSib = item.getFNodePrevSib();
				if (indexOfPrevSib > DomBuildHelper.DEFAULT_VALUE && indexOfPrevSib != indexOfLastChild) {
					if (!needBuildText) {
						int i = indexOfLastChild;
						// ノードタイプがTextやEntityReferenceの場合、兄弟ノードの構築する必要がない。
						while (indexOfPrevSib > DomBuildHelper.DEFAULT_VALUE
								&& (domArray.getItem(i).getFNodeType() == Node.TEXT_NODE || domArray.getItem(i).getFNodeType() == Node.ENTITY_REFERENCE_NODE)
								&& (domArray.getItem(indexOfPrevSib).getFNodeType() == Node.TEXT_NODE || domArray.getItem(indexOfPrevSib).getFNodeType() == Node.ENTITY_REFERENCE_NODE)) {
							int j = indexOfPrevSib;
							indexOfPrevSib = domArray.getItem(i).getFNodePrevSib();
							i = j;
						}
						needBuildText = true;
					}
					if (indexOfPrevSib > DomBuildHelper.DEFAULT_VALUE) {
						buildContent(doc, rootNode, domArray, indexOfPrevSib, true);
					}
				}
			}
		} catch (Exception e) {
			// 構築失敗の場合、完全性フラグをFalseに設定して、次のノードを構築する。
			this.isCompletely = false;
			MainViewer.win.logException(e);
		}
	}

	/**
	 * XML情報を配列に補足するメソッド
	 *
	 * @param domArray XML情報が格納されている配列Beanクラス
	 * @param index 構築している要素のインデックス
	 * @throws ParserConfigurationException
	 */
	private static void recoverArray(DomArrayBean domArray, int index) throws ParserConfigurationException {
		int currentIndex = index;
		DomArrayItemBean itemBeanCurrent = domArray.getItem(currentIndex);
		if (itemBeanCurrent.getFNodeLastChild() == DomBuildHelper.DEFAULT_VALUE) {
			int childIndex = currentIndex + 1;
			boolean continueFlg = true;

			// fNodeParent配列の内容によって、fNodeLastChildとfNodePrevSib配列の内容を補足する。
			while (continueFlg) {
				DomArrayItemBean itemChildBean = domArray.getItem(childIndex);
				DomArrayItemBean itemChildBeanNext = domArray
						.getItem(childIndex + 1);
				if (itemChildBean == null
						|| (itemChildBean.isEmpty()
								&& (itemChildBeanNext == null || itemChildBeanNext.isEmpty()))) {
					// 空白が二つ連続である場合、中止する。
					continueFlg = false;
				} else {
					if (itemChildBean.getFNodeParent() != DomBuildHelper.DEFAULT_VALUE) {
						DomArrayItemBean itemBeanParent = domArray
								.getItem(itemChildBean.getFNodeParent());
						if (itemBeanParent.getFNodeLastChild() < childIndex) {
							itemChildBean.setFNodePrevSib(itemBeanParent
									.getFNodeLastChild());
							itemBeanParent.setFNodeLastChild(childIndex);
						}
					}
				}
				childIndex++;
			}
		}

		if (index == 0) {
			// インデックスがゼロの場合、ノードタイプをDocumentに設定する。
			itemBeanCurrent.setFNodeType(Node.DOCUMENT_NODE);
			return;
		}

		// オブジェクトのクラス名によって、各情報を補足する。
		org.dom4j.Element targetElement = DomBuildHelper.getDomObject(currentIndex);
		String className = DomBuildHelper.getAttributeValue(targetElement,
				DumpFileXmlConstant.ATTR_REAL_TYPE);
		className = className != null ? className : DomBuildHelper.getAttributeValue(
				targetElement, DumpFileXmlConstant.ATTR_DEF_TYPE);
		if (className != null) {
			if (DomBuildHelper.checkEquals(className, DomBuildHelper.ELEMENT_NODE_TYPE)) {
				// タグ名の構築
				itemBeanCurrent.setFNodeType(Node.ELEMENT_NODE);
				if (itemBeanCurrent.getFNodeName() == null) {
					itemBeanCurrent.setFNodeName(DomBuildHelper.getSuperAttributeFromDomObj(
							targetElement, DomBuildHelper.ELEMENT_SUPER_NODE_TYPE, DomBuildHelper.DOCUMENTTYPE_ATTR_NAME));
				}
			} else if (DomBuildHelper.checkEquals(className, DomBuildHelper.ATTR_NODE_TYPE)) {
				itemBeanCurrent.setFNodeType(Node.ATTRIBUTE_NODE);
				// Attributeの構築
				if (itemBeanCurrent.getFNodeName() == null) {
					String[] attibute = DomBuildHelper.getElementAttibute(targetElement);
					if (attibute != null) {
						itemBeanCurrent.setFNodeName(attibute[0]);
						itemBeanCurrent.setFNodeValue(attibute[1]);
					}
				}
			} else if (DomBuildHelper.checkEquals(className, DomBuildHelper.TEXT_NODE_TYPE)
					|| DomBuildHelper.checkEquals(className, DomBuildHelper.CDATA_NODE_TYPE)) {
				// Textの構築
				if (itemBeanCurrent.getFNodeValue() == null) {
					itemBeanCurrent
							.setFNodeValue(DomBuildHelper.getElementText(targetElement));
				}
			} else if (DomBuildHelper.checkEquals(className, DomBuildHelper.COMMENT_NODE_TYPE)) {
				// Commentの構築
				itemBeanCurrent.setFNodeType(Node.COMMENT_NODE);
				if (itemBeanCurrent.getFNodeValue() == null) {
					itemBeanCurrent
							.setFNodeValue(DomBuildHelper.getElementText(targetElement));
				}
			} else if (DomBuildHelper.checkEquals(className, DomBuildHelper.DOCUMENTTYPE_NODE_TYPE)) {
				// DocumentTypeの構築
				itemBeanCurrent.setFNodeType(Node.DOCUMENT_TYPE_NODE);
				if (itemBeanCurrent.getFNodeName() == null) {
					itemBeanCurrent.setFNodeName(DomBuildHelper.getSuperAttributeFromDomObj(
							targetElement,DomBuildHelper.DOCUMENTTYPE_SUPER_NODE_TYPE, DomBuildHelper.DOCUMENTTYPE_ATTR_NAME));
				}
				if (itemBeanCurrent.getFNodeValue() == null) {
					itemBeanCurrent.setFNodeValue(DomBuildHelper.getSuperAttributeFromDomObj(
							targetElement, DomBuildHelper.DOCUMENTTYPE_SUPER_NODE_TYPE, DomBuildHelper.DOCUMENTTYPE_ATTR_PUBLIC_ID));
				}
				if (itemBeanCurrent.getFNodeURI() == null) {
					itemBeanCurrent.setFNodeURI(DomBuildHelper.getSuperAttributeFromDomObj(
							targetElement, DomBuildHelper.DOCUMENTTYPE_SUPER_NODE_TYPE, DomBuildHelper.DOCUMENTTYPE_ATTR_SYSTEM_ID));
				}
				if (itemBeanCurrent.getInternalSubset().length() == 0) {//Modified by Qiu Song on 2010.02.16
					itemBeanCurrent.setInternalSubset(DomBuildHelper.getSuperAttributeFromDomObj(
							targetElement, DomBuildHelper.DOCUMENTTYPE_SUPER_NODE_TYPE, DomBuildHelper.DOCUMENTTYPE_ATTR_INTERNAL_SUBSET));
				}
			} else if (DomBuildHelper.checkEquals(className, DomBuildHelper.ENTITY_NODE_TYPE)) {
				// Entityの構築
				itemBeanCurrent.setFNodeType(Node.ENTITY_NODE);
			} else if (DomBuildHelper.checkEquals(className, DomBuildHelper.ENTITY_REFERENCE_NODE_TYPE)) {
				// EntityReferenceの構築
				itemBeanCurrent.setFNodeType(Node.ENTITY_REFERENCE_NODE);
			} else if (DomBuildHelper.checkEquals(className, DomBuildHelper.PROCESSING_INSTRUCTION_NODE_TYPE)) {
				// ProcessingInstructionの構築
				itemBeanCurrent.setFNodeType(Node.PROCESSING_INSTRUCTION_NODE);
				if (itemBeanCurrent.getFNodeName() == null) {
					itemBeanCurrent.setFNodeName(DomBuildHelper.getSuperAttributeFromDomObj(
							targetElement, DomBuildHelper.PROCESSING_INSTRUCTION_SUPER_NODE_TYPE, DomBuildHelper.PROCESSING_INSTRUCTION_ATTR_TARGET));
				}
				if (itemBeanCurrent.getFNodeValue() == null) {
					itemBeanCurrent.setFNodeValue(DomBuildHelper.getSuperAttributeFromDomObj(
							targetElement, DomBuildHelper.CHARACTER_DATA_NODE_TYPE, DomBuildHelper.PROCESSING_INSTRUCTION_ATTR_DATA));
				}
			} else if (DomBuildHelper.checkEquals(className, DomBuildHelper.NOTATION_NODE_TYPE)) {
				// Notationの構築
				itemBeanCurrent.setFNodeType(Node.NOTATION_NODE);
			} else {
				itemBeanCurrent.setFNodeType(DomBuildHelper.INVALID_NODE_TYPE);
			}
		}
	}
}
