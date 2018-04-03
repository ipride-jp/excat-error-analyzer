package jp.co.ipride.excat.configeditor.model.instance;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.DocumetUtil;

import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * 特定オブジェクト
 * @author tu
 * @since 2007/11/14
 */
public class ObjectLine {
	
	static final String MAX_SIZE="32";

	private String className=null;
	private boolean hasClassLoadName=false;
	private String classLoadName =null;
	private boolean limitMaxSize=false;
	private String maxSize=null;
	private boolean use=false;
	
	/**
	 * コンストラクタ
	 *
	 */
	public ObjectLine(){
		init();
	}
	
	/**
	 * 初期化
	 *
	 */
	public void init(){
		className="";
		hasClassLoadName=false;
		classLoadName="";
		limitMaxSize = true;
		maxSize=MAX_SIZE;
		use=true;
	}

	/**
	 * [Instance]タグを読込む。
	 * @param instanceNode
	 */
	public void inputDocument(Node instanceNode) {
		NamedNodeMap map = instanceNode.getAttributes();
		Node attrNode = map.getNamedItem(ConfigContant.Field_CLASS);
		className = attrNode.getNodeValue();
		
		attrNode = map.getNamedItem(ConfigContant.Field_CLASSLOADER);
		if (attrNode == null){
			hasClassLoadName=false;
			classLoadName = "";
		}else{
			hasClassLoadName=true;
			classLoadName = attrNode.getNodeValue();
		}
		
		attrNode = map.getNamedItem(ConfigContant.Field_MAX_INSTANCE_COUNT);
		if (attrNode == null){
			limitMaxSize=false;
			maxSize="";
		}else{
			limitMaxSize=true;
			maxSize = attrNode.getNodeValue();
		}

		attrNode = map.getNamedItem(ConfigContant.Field_VALID);
		if (attrNode == null){
			use=true;
		}else{
			use = DocumetUtil.parseBoolean(attrNode.getNodeValue());
		}
	}
	
	/**
	 * ＤＯＭ作成
	 * Config
	 *    DumpInstance
	 *        Instance [Class][ClassLoaderString][MaxInstanceCount][Valid]
	 * @param dumpInstanceNode：　[DumpInstance]
	 */
	public void outputDocument(Node dumpInstanceNode){
		Node instanceNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_INSTANCE);
		dumpInstanceNode.appendChild(instanceNode);

		((Element)instanceNode).setAttribute(ConfigContant.Field_CLASS, className);
		
		if (hasClassLoadName){
			((Element)instanceNode).setAttribute(ConfigContant.Field_CLASSLOADER, classLoadName);
		}		
		if (limitMaxSize){
			((Element)instanceNode).setAttribute(ConfigContant.Field_MAX_INSTANCE_COUNT, maxSize);
		}

		((Element)instanceNode).setAttribute(ConfigContant.Field_VALID, Boolean.toString(use));
	}

	public String getClassName() {
		return className;
	}

	public void setClassName(String className) {
		this.className = className;
		ConfigModel.setChanged();
	}
	
	public String getClassLoadName(){
		return classLoadName;
	}
	
	public void setClassLoadName(String classLoadName){
		if ("".equals(classLoadName)){
			hasClassLoadName=false;
		}else{
			hasClassLoadName=true;
		}
		this.classLoadName=classLoadName;
		ConfigModel.setChanged();
	}

	public boolean isUse() {
		return use;
	}

	public void setUse(boolean use) {
		this.use = use;
		ConfigModel.setChanged();
	}

	public String getMaxSize() {
		return maxSize;
	}

	public void setMaxSize(String maxSize) {
		if ("".equals(maxSize)){
			limitMaxSize=false;
		}else{
			limitMaxSize=true;
		}
		this.maxSize = maxSize;
		ConfigModel.setChanged();
	}
}
