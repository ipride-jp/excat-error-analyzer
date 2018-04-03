package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;

import org.w3c.dom.Element;


public class SearchUtility {


	public static String getObjectRefId(Element node){
		if (node != null){
			return node.getAttribute(DumpFileXmlConstant.ATTR_OBJECT_REF);
		}else{
			return null;
		}
	}

	public static String getObjectId(Element node){
		if (node != null){
			return node.getAttribute(DumpFileXmlConstant.ATTR_OBJECT_ID);
		}else{
			return null;
		}
	}

}
