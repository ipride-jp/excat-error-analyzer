/*
 * Error Anaylzer Tool for Java
 * 
 * Created on 2006/4/1
 * 
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.stackviewer;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.w3c.dom.Node;

import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;

/**
 * this is a filter of dump data tree view.
 * 
 * @author GuanXH
 * 
 */
public class StackViewerFilter extends ViewerFilter {

	/**
	 * process the select event.
	 * 
	 * @see org.eclipse.jface.viewers.ViewerFilter#select(org.eclipse.jface.viewers.Viewer,
	 *      java.lang.Object, java.lang.Object)
	 */
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		
		if (element == null){
			return false;
		}
		
		Node node = (Node)element;
		
		//don't dispaly node if the node is pool
		if (DumpFileXmlConstant.NODE_OBJECT_POOL.equals(node.getNodeName())){
			return false;
		}
/*commend by jiang 2007/11/09,show this node in the stack view
		//don't dispaly node if the node is this
		if (DumpFileXmlConstant.NODE_THIS.equals(node.getNodeName())){
			return false;
		}
		*/
		//don't display invalid variant when i don't like to see invalid variant. 
		if (DumpFileXmlConstant.NODE_VARIABLE.equals(node.getNodeName())){
			if ( !StackTree.displayAllLocalVar ){
				Node validNode = node.getAttributes().getNamedItem(DumpFileXmlConstant.ATTR_VALID);
				if ("false".equals(validNode.getNodeValue())){
					return false;
				}
			}
			return true;
		}

		if (node.getNodeType() == Node.ELEMENT_NODE) {
			return true;
		}
		
		//filter null or space text node.
		if (node.getNodeType() == Node.TEXT_NODE) {
			String nodeValue = node.getNodeValue();
			if (nodeValue != null && !removeNoUseChars(nodeValue).equals("")) {
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * replace word.
	 * 
	 * @param nodeValue
	 * @return
	 */
	private String removeNoUseChars(String nodeValue) {
		nodeValue = nodeValue.replaceAll("\t", "");
		nodeValue = nodeValue.replaceAll("\n", "");
		return nodeValue.trim();
	}
}