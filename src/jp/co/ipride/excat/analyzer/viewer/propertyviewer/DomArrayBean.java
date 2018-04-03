package jp.co.ipride.excat.analyzer.viewer.propertyviewer;

import java.util.HashMap;
import java.util.Map;

/**
 * XMLèÓïÒîzóÒÇäiî[Ç∑ÇÈBeanÉNÉâÉX
 *
 * @author sai
 * @since 2009/10/15
 */
public class DomArrayBean {
	protected static final int CHUNK_SHIFT = 11;
	protected static final int CHUNK_SIZE = 2048;
	protected static final int CHUNK_MASK = 2047;

	private int[][] fNodeType = null;

	private Object[][] fNodeName = null;

	private Object[][] fNodeValue = null;

	private int[][] fNodeLastChild = null;

	private int[][] fNodePrevSib = null;

	private int[][] fNodeParent = null;

	private Object[][] fNodeURI = null;

	private int[][] fNodeExtra = null;

	private Map<Integer, DomArrayItemBean> cache = new HashMap<Integer, DomArrayItemBean>();

	public void setFNodeType(int[][] nodeType) {
		fNodeType = nodeType;
	}

	public void setFNodeName(Object[][] nodeName) {
		fNodeName = nodeName;
	}

	public void setFNodeValue(Object[][] nodeValue) {
		fNodeValue = nodeValue;
	}

	public void setFNodeLastChild(int[][] nodeLastChild) {
		fNodeLastChild = nodeLastChild;
	}

	public void setFNodePrevSib(int[][] nodePrevSib) {
		fNodePrevSib = nodePrevSib;
	}

	public void setFNodeParent(int[][] nodeParent) {
		fNodeParent = nodeParent;
	}

	public void setFNodeURI(Object[][] nodeURI) {
		fNodeURI = nodeURI;
	}

	public void setFNodeExtra(int[][] nodeExtra) {
		fNodeExtra = nodeExtra;
	}

	public DomArrayItemBean getItem(int index) {

		DomArrayItemBean item = cache.get(index);
		if (item != null) {
			return item;
		}

		int i = index >> 11;
		int j = index & 2047;

		if (i > fNodeType.length) {
			return null;
		}

		int nodeType = (fNodeType == null || fNodeType[i] == null) ? DomBuildHelper.DEFAULT_VALUE
				: fNodeType[i][j];

		Object nodeName = (fNodeName == null || fNodeName[i] == null) ? null
				: fNodeName[i][j];

		Object nodeValue = (fNodeValue == null || fNodeValue[i] == null) ? null
				: fNodeValue[i][j];

		int nodeLastChild = (fNodeLastChild == null || fNodeLastChild[i] == null) ? DomBuildHelper.DEFAULT_VALUE
				: fNodeLastChild[i][j];

		int nodePrevSib = (fNodePrevSib == null || fNodePrevSib[i] == null) ? DomBuildHelper.DEFAULT_VALUE
				: fNodePrevSib[i][j];

		int nodeParent = (fNodeParent == null || fNodeParent[i] == null) ? DomBuildHelper.DEFAULT_VALUE
				: fNodeParent[i][j];

		Object nodeURI = (fNodeURI == null || fNodeURI[i] == null) ? null
				: fNodeURI[i][j];

		int nodeExtra = (fNodeExtra == null || fNodeExtra[i] == null) ? DomBuildHelper.DEFAULT_VALUE
				: fNodeExtra[i][j];

		item = new DomArrayItemBean(nodeType, nodeName, nodeValue, nodeLastChild,
				nodePrevSib, nodeParent, nodeURI, nodeExtra);
		cache.put(index, item);
		return item;
	}
}
