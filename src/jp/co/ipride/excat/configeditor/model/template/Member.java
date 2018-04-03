package jp.co.ipride.excat.configeditor.model.template;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.DocumetUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * テンプレート・クラスのメンバー
 * @author tu
 * @since 2007/12/5
 */
public class Member {	
	
	private String name=null;
	
	private boolean use=false;
	
	/**
	 * コンストラクタ
	 *
	 */
	public Member(){
		init();
	}
	
	/**
	 * 初期化
	 *
	 */
	public void init(){
		name="";
		use=true;
	}

	/**
	 * [Member]タグを読込む
	 * @param memberNode
	 */
	public void inputDocument(Node memberNode) {
		NamedNodeMap map = memberNode.getAttributes();
		Node attrNode = map.getNamedItem(ConfigContant.Field_NAME);
		name = attrNode.getNodeValue();
		attrNode = map.getNamedItem(ConfigContant.Field_VALID);
		if (attrNode == null){
			use=true;
		}else{
			use = DocumetUtil.parseBoolean(attrNode.getNodeValue());
		}
	}

	/**
	 * Config
	 *     ObjectElement
	 *          Object   [Class][Valid]
	 *          	Field [Name][Valid]
	 * @param objectNode: [Object]
	 */
	public void outputDocument(Node objectNode){
		Node fieldNode = ConfigModel.getDocument().createElement(ConfigContant.TAG_FIELD);
		objectNode.appendChild(fieldNode);
		((Element)fieldNode).setAttribute(ConfigContant.Field_NAME, name);
		((Element)fieldNode).setAttribute(ConfigContant.Field_VALID, Boolean.toString(use));
	}
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
		ConfigModel.setChanged();
	}

	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
		ConfigModel.setChanged();
	}
	
	public void copyTo(Member clone){
		clone.name = this.name;
		clone.use = this.use;
	}

}
