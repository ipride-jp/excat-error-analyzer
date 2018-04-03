package jp.co.ipride.excat.configeditor.model.task;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.util.DocumetUtil;

public abstract class AbstractTask implements ITask{

	public static int task_count = 0;

	protected String taskName = ApplicationResource.getResource("Config.Task.InitName");

	protected boolean effect = true;

	protected int taskType;

	protected String prefix = "";

	protected String comment = "";

	protected DumpData dumpData = null;

	protected String identfyKey = null;

	public String getTaskName() {
		return taskName;
	}

	public int getTaskType() {
		return taskType;
	}

	public boolean isEffect() {
		return effect;
	}

	public void setEffect(boolean effect) {
		this.effect = effect;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getPrefix(){
		return prefix;
	}

	public void setPrefix(String prefix){
		this.prefix = prefix;
	}

	public String getIdentfyKey(){
		return identfyKey;
	}

	protected void setIdentfyKey(){
		this.identfyKey=ConfigContant.Tree_Item_TaskRegister_Root + "." + task_count;
		task_count++;
	}

	public void setTaskType(int taskType) {
		this.taskType = taskType;
	}

	public DumpData getDumpData() {
		return dumpData;
	}

	public void setDumpData(DumpData dumpData) {
		this.dumpData = dumpData;
	}

	protected void outputTaskAttribute(Node taskNode){
		DocumetUtil.setAttribute(taskNode,ConfigContant.Filed_VALID, this.effect);
		DocumetUtil.setAttribute(taskNode,ConfigContant.Field_NAME, this.taskName);
		DocumetUtil.setAttribute(taskNode,ConfigContant.Field_TYPE, this.taskType);
		DocumetUtil.setAttribute(taskNode,ConfigContant.Field_COMMENT, this.comment);
		DocumetUtil.setAttribute(taskNode,ConfigContant.Field_SUFFIX, this.prefix);
	}

	protected void inputTaskAttribute(Node taskNode){
		Node attrNode;
		NamedNodeMap map = taskNode.getAttributes();

		attrNode = map.getNamedItem(ConfigContant.Field_COMMENT);
		if (attrNode != null){
			comment = attrNode.getNodeValue();
		}

		attrNode = map.getNamedItem(ConfigContant.Field_SUFFIX);
		if (attrNode != null){
			prefix = attrNode.getNodeValue();
		}

		attrNode = map.getNamedItem(ConfigContant.Filed_VALID);
		if (attrNode != null){
			effect = Boolean.parseBoolean(attrNode.getNodeValue());
		}

		attrNode = map.getNamedItem(ConfigContant.Field_NAME);
		if (attrNode != null){
			taskName = attrNode.getNodeValue();
		}
	}

}
