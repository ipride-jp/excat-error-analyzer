/*
 * Error Anaylzer Tool for Java
 * 
 * Created on 2006/4/1
 * 
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.viewer.stackviewer;

import jp.co.ipride.excat.analyzer.common.DumpDocument;
import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Tree構造の各ノードの表示をカスタマイズする。
 * @version 	1.0 2004/12/01
 * @author 	管 暁華
 */
public class StackContentProvider implements ITreeContentProvider {
	
	/**
	 * 子ノードの配列を返す
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.Object)
	 */
	public Object[] getChildren(Object parentElement) {
	    return getChildrenNodeArray((Node)parentElement);
	}
	
	/**
	 * 親ノードを返す。
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object)
	 */
	public Object getParent(Object element) {
	    return ((Node)element).getParentNode();
	}
	
	/**
	 * 子ノードを持つかどうか
	 * @see org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.Object)
	 */
	public boolean hasChildren(Object element) {
	    return ((Node)element).hasChildNodes();
	}
	
	/**
	 * 子ノードの要素を取得
	 * @see org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java.lang.Object)
	 */
	public Object[] getElements(Object inputElement) {
		return getChildrenNodeArray((Node)inputElement);
	}
	
	/**
	 * 実装しない。
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	public void dispose() {
	}
	
	/**
	 * 実装しない。
	 * @see org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface.viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	
	/**
	 * 子ノード配列を取得
	 * もし子ノードの名前は”ObjectPool”の場合、Poolから取得
	 * @param node 親ノード
	 * @return
	 */
	private Node[] getChildrenNodeArray(Node node) {
		NodeList nodeList;
		nodeList = node.getChildNodes();
		Node[] nodeArray = new Node[nodeList.getLength()];
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node item = nodeList.item(i);
			if (!DumpFileXmlConstant.NODE_OBJECT_POOL.equals(item.getNodeName())){
				nodeArray[i] = DumpDocument.replaceObject(item);
			}
		}
		return nodeArray;
	}
	
}
