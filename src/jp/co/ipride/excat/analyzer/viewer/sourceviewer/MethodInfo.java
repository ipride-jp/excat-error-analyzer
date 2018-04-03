package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 *
 *
 */
public class MethodInfo {

	private String classSig=null;

	private String methodSig=null;

	private String methodName=null;

	//ファイル名のみ、パス名を含まない
	private String sourceName = null;

	private int lineNum=-1;

	private int location=-1;

	private Node node=null;
	/**
	 * ソースファイルにおけるメソッド名の開始位置
	 */
	private int startPosition = 0;
	/**
	 * メソッド名の長さ
	 */
	private int offset = 0;


	/**
	 * デフォルトコンストラクタ
	 *
	 */
	public MethodInfo(){

	}

	/**
	 * コンストラクタ
	 * @param node
	 */
	public MethodInfo(Node node){
		this.node = node;
		if (DumpFileXmlConstant.NODE_METHOD.equals(node.getNodeName())){
			processMethodNode();
		}
	}

	private void processMethodNode(){
		NamedNodeMap attrs = node.getAttributes();
		Node attr = attrs.getNamedItem(DumpFileXmlConstant.ATTR_DECL_CLASS);
		if (attr == null) {
			return;
		}else{
			classSig = attr.getNodeValue();
		}

		attr = attrs.getNamedItem(DumpFileXmlConstant.ATTR_LINE_NUMBER);
		if (attr == null) {
			return;
		}else{
			lineNum = Integer.parseInt(attr.getNodeValue());
		}

		attr = attrs.getNamedItem(DumpFileXmlConstant.ATTR_LOCATION);
		if (attr == null) {
			return;
		}else{
			location = Integer.parseInt(attr.getNodeValue());
		}

		attr = attrs.getNamedItem(DumpFileXmlConstant.ATTR_NAME);
		if (attr == null) {
			return;
		}else{
			methodName = attr.getNodeValue();
		}

		attr = attrs.getNamedItem(DumpFileXmlConstant.ATTR_SIG);
		if (attr == null) {
			return;
		}else{
			methodSig = attr.getNodeValue();
		}

		attr = attrs.getNamedItem(DumpFileXmlConstant.ATTR_SOURCE_FILE);
		if (attr != null) {
			sourceName = attr.getNodeValue();
		}
	}

	public String getClassSig() {
		return classSig;
	}

	public int getLineNum() {
		return lineNum;
	}

	public int getLocation() {
		return location;
	}

	public String getMethodName() {
		return methodName;
	}

	public String getMethodSig() {
		return methodSig;
	}

	/**
	 * タブビューのタイトルを取得する<br>
	 * ソースファイル名がある場合、ソースファイル名
	 * @return
	 */
	public String getTabTitle(){
		if(sourceName != null){
			return sourceName;
		}
		return getClassName() + ".class";
	}

	public String getClassName(){
		int index = classSig.lastIndexOf(".");
		if (index < 0){
			return classSig;
		}else{
			return classSig.substring(index+1);
		}
	}

	public Node getNode(){
		return node;
	}


	public boolean identify(Node node){
		return this.node.equals(node);
	}

	public String getSourceName() {
		return sourceName;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getMethodIdentity(){
		StringBuffer buf = new StringBuffer();
		buf.append(classSig);
		buf.append('.');
		buf.append(methodName);
		buf.append('/');
		buf.append(methodSig);
		return buf.toString();
	}

	public void setClassSig(String classSig) {
		this.classSig = classSig;
	}

	public void setLineNum(int lineNum) {
		this.lineNum = lineNum;
	}

	public void setLocation(int location) {
		this.location = location;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public void setMethodSig(String methodSig) {
		this.methodSig = methodSig;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

}
