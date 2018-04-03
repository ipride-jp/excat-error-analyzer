package jp.co.ipride.excat.analyzer.viewer.propertyviewer;

/**
 * XMLèÓïÒîzóÒÇäiî[Ç∑ÇÈBeanÉNÉâÉX
 *
 * @author sai
 * @since 2009/10/15
 */
public class DomArrayItemBean {

	private int fNodeType = DomBuildHelper.DEFAULT_VALUE;

	private Object fNodeName = null;

	private Object fNodeValue = null;

	private int fNodeLastChild = DomBuildHelper.DEFAULT_VALUE;

	private int fNodePrevSib = DomBuildHelper.DEFAULT_VALUE;

	private int fNodeParent = DomBuildHelper.DEFAULT_VALUE;

	private Object fNodeURI = DomBuildHelper.DEFAULT_VALUE;

	private int fNodeExtra = DomBuildHelper.DEFAULT_VALUE;

	private String internalSubset = "";

	public DomArrayItemBean(int fNodeType, Object fNodeName, Object fNodeValue,
			int fNodeLastChild, int fNodePrevSib, int fNodeParent, Object fNodeURI, int fNodeExtra) {
		this.fNodeType = fNodeType;
		this.fNodeName = fNodeName;
		this.fNodeValue = fNodeValue;
		this.fNodeLastChild = fNodeLastChild;
		this.fNodePrevSib = fNodePrevSib;
		this.fNodeParent = fNodeParent;
		this.fNodeURI = fNodeURI;
		this.fNodeExtra = fNodeExtra;
	}

	public int getFNodeType() {
		return fNodeType;
	}

	public void setFNodeType(int nodeType) {
		fNodeType = nodeType;
	}

	public Object getFNodeName() {
		return fNodeName;
	}

	public void setFNodeName(Object nodeName) {
		fNodeName = nodeName;
	}

	public Object getFNodeValue() {
		return fNodeValue;
	}

	public void setFNodeValue(Object nodeValue) {
		fNodeValue = nodeValue;
	}

	public int getFNodeLastChild() {
		return fNodeLastChild;
	}

	public void setFNodeLastChild(int nodeLastChild) {
		fNodeLastChild = nodeLastChild;
	}

	public int getFNodePrevSib() {
		return fNodePrevSib;
	}

	public void setFNodePrevSib(int nodePrevSib) {
		fNodePrevSib = nodePrevSib;
	}

	public int getFNodeParent() {
		return fNodeParent;
	}

	public void setFNodeParent(int nodeParent) {
		fNodeParent = nodeParent;
	}

	public Object getFNodeURI() {
		return fNodeURI;
	}

	public void setFNodeURI(Object nodeURI) {
		fNodeURI = nodeURI;
	}

	public int getFNodeExtra() {
		return fNodeExtra;
	}

	public void setFNodeExtra(int nodeExtra) {
		fNodeExtra = nodeExtra;
	}

	public String getInternalSubset() {
		return internalSubset;
	}

	public void setInternalSubset(String internalSubset) {
		this.internalSubset = internalSubset;
	}

	public boolean isEmpty() {
		return (fNodeType == DomBuildHelper.DEFAULT_VALUE && fNodeName == null && fNodeValue == null
				&& fNodeLastChild == DomBuildHelper.DEFAULT_VALUE && fNodePrevSib == DomBuildHelper.DEFAULT_VALUE
				&& fNodeParent == DomBuildHelper.DEFAULT_VALUE && fNodeURI == null && fNodeExtra == DomBuildHelper.DEFAULT_VALUE);
	}

	public String toString() {
		StringBuilder s = new StringBuilder();
		s.append("NodeType:");
		s.append(fNodeType + "\n");
		s.append("fNodeName:");
		s.append((String) fNodeName + "\n");
		s.append("fNodeValue:");
		s.append((String) fNodeValue + "\n");
		s.append("fNodeLastChild:");
		s.append(fNodeLastChild + "\n");
		s.append("fNodePrevSib:");
		s.append(fNodePrevSib + "\n");
		s.append("fNodeParent:");
		s.append(fNodeParent + "\n");
		s.append("fNodeURI:");
		s.append(fNodeURI + "\n");
		s.append("fNodeExtra:");
		s.append(fNodeExtra + "\n");
		s.append("internalSubset:");
		s.append(internalSubset + "\n");
		return s.toString();
	}
}
