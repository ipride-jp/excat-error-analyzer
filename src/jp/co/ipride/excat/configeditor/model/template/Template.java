package jp.co.ipride.excat.configeditor.model.template;

import java.util.Vector;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.DocumetUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * テンプレート・クラス
 * @author tu
 * @since 2007/12/5
 */
public class Template {

	private String className=null;

	private boolean use=false;

	private MemberList members = new MemberList();

	/**
	 * コンストラクタ
	 *
	 */
	public Template(){
		init();
	}

	/**
	 * 初期化
	 *
	 */
	public void init(){
		className="";
		use = true;
		members.init();
	}

	/**
	 * [Object]タグを読込む
	 * @param objectNode
	 */
	public void inputDocument(Node objectNode) {
		Node attrNode;
		NamedNodeMap map = objectNode.getAttributes();
		attrNode = map.getNamedItem(ConfigContant.Field_CLASS);
		className= attrNode.getNodeValue();
		attrNode = map.getNamedItem(ConfigContant.Field_VALID);
		if (attrNode == null){
			use=true;
		}else{
			use = DocumetUtil.parseBoolean(attrNode.getNodeValue());
		}
		members.init();
		members.inputDocument(objectNode);
	}

	/**
	 * Config
	 *     ObjectElement
	 *          Object   [Class][Valid]
	 *          	Field [Name][Valid]
	 * @param objectElementNode: [ObjectElement]
	 */
	public void outputDocument(Node objectElementNode){
		Node objectNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_OBJECT);
		objectElementNode.appendChild(objectNode);
		((Element)objectNode).setAttribute(ConfigContant.Field_CLASS, className);
		((Element)objectNode).setAttribute(ConfigContant.Field_VALID, Boolean.toString(use));
		members.outputDocument(objectNode);
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
		ConfigModel.setChanged();
	}

	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
		ConfigModel.setChanged();
	}

	public MemberList getMembers(){
		return members;
	}

	public void copyTo(Template clone){
		clone.className = this.className;

		Vector<Member> cloneMembers = clone.members.getMembers();
		cloneMembers.removeAllElements();

		Vector<Member> thisMembers = members.getMembers();

		for (int i=0; i<thisMembers.size(); i++){
			Member thisMember = thisMembers.get(i);
			Member cloneMember = new Member();
			thisMember.copyTo(cloneMember);
			cloneMembers.add(cloneMembers.size(), cloneMember);
		}

	}
}
