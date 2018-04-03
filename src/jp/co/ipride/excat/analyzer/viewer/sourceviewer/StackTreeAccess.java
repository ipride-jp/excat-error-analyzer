package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import jp.co.ipride.excat.analyzer.common.DumpDocument;
import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;
import jp.co.ipride.excat.analyzer.viewer.stackviewer.CommonNodeLabel;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * stack treeをアクセスして、該当情報を取得する
 * @author iPride_Demo
 *
 */
public class StackTreeAccess {

	/**
	 * StackTree情報をアクセスして、該当Fieldの情報を取得する。
	 * @param matchField
	 * @return
	 */
	public static String getFieldInfo(MatchField matchField){

		//get xml document
		Document doc = DumpDocument.getDocument();
		if(doc == null){
			return null;

		}

		//get method list
		NodeList methodList = doc.getElementsByTagName(
				DumpFileXmlConstant.NODE_METHOD);
		if(methodList == null || methodList.getLength()==0){
			return null;
		}

		for (int i = 0; i < methodList.getLength(); i++) {
			Node node = methodList.item(i);
			if(!isMatchField(node,matchField)){
				continue;
			}

			String fieldInfo = getFieldFromThisNode(node,
					matchField.getFieldName());
			if(fieldInfo != null){
				return fieldInfo;
			}
		}
		return null;
	}

	/**
	 * get information for a field
	 * @param fieldName
	 * @return
	 */
	private static String getFieldFromThisNode(Node methodNode,
			String fieldName){

		if(fieldName == null)
			return null;

		NodeList list = methodNode.getChildNodes();
		for (int i=0; i<list.getLength(); i++){
			Node item = list.item(i);
			//find this node
			if (DumpFileXmlConstant.NODE_THIS.equals(item.getNodeName())){
				Node thisNode = item;

				NodeList listField = thisNode.getChildNodes();
				if(listField != null){
					int fieldNumber = listField.getLength();
					//the method node is not expanded
					if(fieldNumber == 0){
						Node replaceItem = DumpDocument.replaceObject(thisNode.cloneNode(true));
						if(replaceItem == null){
							return null;
						}
						listField = replaceItem.getChildNodes();
						if(listField == null){
							return null;
						}
						fieldNumber = listField.getLength();
					}
					for(int j= 0; j < fieldNumber;j++ ){
						Node itemField = listField.item(j);
						NamedNodeMap attrs = itemField.getAttributes();
						if(attrs != null){
							Node attr = attrs.getNamedItem(DumpFileXmlConstant.ATTR_NAME);
							if (attr != null) {
								String name = attr.getNodeValue();
								if(fieldName.equals(name)){
									//if this is an object,replace it with node in object pool
									if (!DumpFileXmlConstant.NODE_OBJECT_POOL.equals(itemField.getNodeName())){
										itemField = DumpDocument.replaceObject(itemField.cloneNode(true));
									}
									return CommonNodeLabel.getText(itemField);
								}
							}
						}

					}
				}
				return null;
			}
		}

		return null;
	}

	/**
	 * StackTree情報をアクセスして、該当変数の情報を取得する。
	 * @param methodInfo　メソッド情報
	 * @param varInfo　変数情報
	 * @return　StackTreeにある変数ノートの文字列情報、該当がなければ、
	 * nullを返す
	 */
	public static String getVariableInfo(MatchMethodInfo methodInfo,
			MatchVariableInfo varInfo){

		//get xml document
		Document doc = DumpDocument.getDocument();
		if(doc == null){
			return null;

		}

		//get method list
		NodeList methodList = doc.getElementsByTagName(
				DumpFileXmlConstant.NODE_METHOD);
		if(methodList == null || methodList.getLength()==0){
			return null;
		}

		for (int i = 0; i < methodList.getLength(); i++) {
			Node node = methodList.item(i);
			if(!isMatchMethod(node,methodInfo)){
				continue;
			}

			//該当メソッド
			NodeList varList = node.getChildNodes();
			if(varList == null || varList.getLength() == 0){
				continue;
			}

			for (int j = 0; j < varList.getLength(); j++) {
				Node varNode = varList.item(j);
				if(isMatchVar(varNode,varInfo)){
					Node realVarNode = DumpDocument.replaceObject(
							varNode.cloneNode(true));
					return CommonNodeLabel.getText(realVarNode);
				}
			}

		}

		return null;
	}


	/**
	 * 該当変数かどうかをチェックする
	 * @param varNode
	 * @param varInfo
	 * @return
	 */
	private static boolean isMatchVar(Node varNode,
			MatchVariableInfo varInfo){

		String varName =getAttributeValue(varNode,
				DumpFileXmlConstant.ATTR_NAME);
		if(varName == null){
			return false;
		}

		//parameter is valid and need not to be checked for validity
		if(!DumpFileXmlConstant.NODE_ARGUMENT.equals(varNode.getNodeName())){
			String isValid = getAttributeValue(varNode,
					DumpFileXmlConstant.ATTR_VALID);
			if(isValid == null || isValid.equals("false")){
				return false;
			}
		}


		//same var name?
		//実行時に、同名の変数があっても、一つのみが有効である。
		if(varName.equals(varInfo.getName())){
			return true;
		}

		return false;
	}

	/**
	 * 該当fieldを含むかどうかをチェックする<br>
	 * @param methodNode
	 * @param methodInfo
	 * @return
	 */
	private static boolean isMatchField(Node methodNode,
			MatchField matchField){

	    String delcaringClass = getAttributeValue(methodNode,
	    		DumpFileXmlConstant.ATTR_DECL_CLASS);
	    String methodName = getAttributeValue(methodNode,
	    		DumpFileXmlConstant.ATTR_NAME);
	    if(delcaringClass == null || methodName == null ){
	    	return false;
	    }

	    String packageName = matchField.getPackageName();
	    StringBuffer buf = new StringBuffer();
	    if(packageName != null){
	    	buf.append(packageName);
	    	buf.append('.');
	    }
	    buf.append(matchField.getClassName());
	    if(delcaringClass.equals(buf.toString())){
            return true;
	    }

	    return false;

	}

	/**
	 * 該当メソッドであるかどうかをチェックする<br>
	 * @param methodNode
	 * @param methodInfo
	 * @return
	 */
	private static boolean isMatchMethod(Node methodNode,
			MatchMethodInfo methodInfo){

	    String delcaringClass = getAttributeValue(methodNode,
	    		DumpFileXmlConstant.ATTR_DECL_CLASS);
	    String methodName = getAttributeValue(methodNode,
	    		DumpFileXmlConstant.ATTR_NAME);
	    if(delcaringClass == null || methodName == null ){
	    	return false;
	    }

	    if(!methodName.equals(methodInfo.getMethodName())){
	    	return false;
	    }

	    String packageName = methodInfo.getPackageName();
	    StringBuffer buf = new StringBuffer();
	    if(packageName != null){
	    	buf.append(packageName);
	    	buf.append('.');
	    }
	    buf.append(methodInfo.getClassName());
	    if(delcaringClass.equals(buf.toString())){
		    //to do:method name duplicate?
	    	String signature =  getAttributeValue(methodNode,
		    		DumpFileXmlConstant.ATTR_SIG);
	    	//if(methodInfo.isSameSignature(signature)){
	    	if (methodInfo.getMethodSignature().equals(signature)) {
	    		return true;
	    	}
	    }

	    return false;

	}

	/**
	 * 指定したノートの属性の値を取得する。
	 * @param node
	 * @param attr
	 * @return
	 */
	private static String getAttributeValue(Node node,String attr){

		if(node == null || attr == null){
			return null;
		}
		NamedNodeMap map = node.getAttributes();
		if(map == null){
			return null;
		}
		Node attrNode = map.getNamedItem(attr);
		if(attrNode == null){
			return null;
		}
		return attrNode.getNodeValue();
	}
}
