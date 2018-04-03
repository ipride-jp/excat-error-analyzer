package jp.co.ipride.excat.configeditor.model.task;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.util.DocumetUtil;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;


/**
 * this is a mode for DumpDataForm
 * @author tu-ipride
 * @version 3.0
 * @date 2009/9/26
 */
public class DumpData {

	public static final int  DUMP_CURRENT_THREAD =0;
	public static final int  DUMP_ALL_THREAD =1;
	public static final int  DUMP_ACTIVE_THREAD =2;
	public static final int  DUMP_WAITTING_THREAD =3;

	public static final String  DUMP_CURRENT_THREAD_STR ="current";
	public static final String  DUMP_ALL_THREAD_STR ="all";
	public static final String  DUMP_ACTIVE_THREAD_STR ="runnable";
	public static final String  DUMP_WAITTING_THREAD_STR ="wait";

	public static final String STACK_TRACE_DEPTH = "10";
	public static final String ATTRIBUTE_NEST_DEPTH = "5";
	public static final String MAX_OBJECT_ARRAY_ELEMENT_NUMBER = "100";
	public static final String MAX_PRIMITIVE_ARRAY_ELEMENT_NUMBER = "100";

	//mail
	protected boolean noticeMail = false;
	protected boolean attachDumpFile = false;

	//auto delete dump files
	protected boolean autoDelete = false;
	protected String saveDays = null;

	//data
	protected boolean dumpThis = true;
	protected boolean dumpVariable = true;
	protected boolean dumpArgument = true;
	protected boolean dumpAttribute = true;
	protected boolean dumpPublic = true;
	protected boolean dumpPackage = true;
	protected boolean dumpProtected = true;
	protected boolean dumpPrivate = true;
	protected boolean dumpOtherInstance = true;
	protected String stackTraceDepth = "";
	protected String attributeNestDepth = "";

	protected String maxArrayElementForObject = "";
	protected String maxArrayElementForPrimitive = "";
	protected boolean dumpInstance = false;

	protected int dumpThreadType = DUMP_CURRENT_THREAD;
	protected String threadPriority = "";

	/**
	 *
	 * @param taskType
	 */
	public DumpData(int taskType){
		noticeMail = false;
		attachDumpFile = false;
		autoDelete = false;
		stackTraceDepth = STACK_TRACE_DEPTH;
		attributeNestDepth = ATTRIBUTE_NEST_DEPTH;
		dumpVariable = true;
		dumpArgument = true;
		dumpAttribute = true;
		dumpPublic = true;
		dumpPackage = true;
		dumpProtected = true;
		dumpPrivate = true;
		dumpThis = true;
		maxArrayElementForObject = MAX_OBJECT_ARRAY_ELEMENT_NUMBER;
		maxArrayElementForPrimitive = MAX_PRIMITIVE_ARRAY_ELEMENT_NUMBER;
		dumpInstance = true;
		saveDays = "";
		if (taskType == ITask.MONITOR_SIGNAL){
			dumpThreadType = DUMP_ALL_THREAD;
		}
	}

	public boolean isNoticeMail() {
		return noticeMail;
	}

	public void setNoticeMail(boolean noticeMail) {
		this.noticeMail = noticeMail;
	}

	public boolean isAttachDumpFile() {
		return attachDumpFile;
	}

	public void setAttachDumpFile(boolean attachDumpFile) {
		this.attachDumpFile = attachDumpFile;
	}

	public boolean isAutoDelete() {
		return autoDelete;
	}

	public void setAutoDelete(boolean autoDelete) {
		this.autoDelete = autoDelete;
	}

	public String getSaveDays() {
		return saveDays;
	}

	public void setSaveDays(String saveDays) {
		this.saveDays = saveDays;
	}

	public boolean isDumpThis() {
		return dumpThis;
	}

	public void setDumpThis(boolean dumpThis) {
		this.dumpThis = dumpThis;
	}

	public boolean isDumpVariable() {
		return dumpVariable;
	}

	public void setDumpVariable(boolean dumpVariable) {
		this.dumpVariable = dumpVariable;
	}

	public boolean isDumpArgument() {
		return dumpArgument;
	}

	public void setDumpArgument(boolean dumpArgument) {
		this.dumpArgument = dumpArgument;
	}

	public boolean isDumpAttribute() {
		return dumpAttribute;
	}

	public void setDumpAttribute(boolean dumpAttribute) {
		this.dumpAttribute = dumpAttribute;
	}

	public boolean isDumpPublic() {
		return dumpPublic;
	}

	public void setDumpPublic(boolean dumpPublic) {
		this.dumpPublic = dumpPublic;
	}

	public boolean isDumpPackage() {
		return dumpPackage;
	}

	public void setDumpPackage(boolean dumpPackage) {
		this.dumpPackage = dumpPackage;
	}

	public boolean isDumpProtected() {
		return dumpProtected;
	}

	public void setDumpProtected(boolean dumpProtected) {
		this.dumpProtected = dumpProtected;
	}

	public boolean isDumpPrivate() {
		return dumpPrivate;
	}

	public void setDumpPrivate(boolean dumpPrivate) {
		this.dumpPrivate = dumpPrivate;
	}

	public boolean isDumpOtherInstance() {
		return dumpOtherInstance;
	}

	public void setDumpOtherInstance(boolean dumpOtherInstance) {
		this.dumpOtherInstance = dumpOtherInstance;
	}

	public String getStackTraceDepth() {
		return stackTraceDepth;
	}

	public void setStackTraceDepth(String stackTraceDepth) {
		this.stackTraceDepth = stackTraceDepth;
	}

	public String getAttributeNestDepth() {
		return attributeNestDepth;
	}

	public void setAttributeNestDepth(String attributeNestDepth) {
		this.attributeNestDepth = attributeNestDepth;
	}

	public String getMaxArrayElementForObject() {
		return maxArrayElementForObject;
	}

	public void setMaxArrayElementForObject(String maxArrayElementForObject) {
		this.maxArrayElementForObject = maxArrayElementForObject;
	}

	public String getMaxArrayElementForPrimitive() {
		return maxArrayElementForPrimitive;
	}

	public void setMaxArrayElementForPrimitive(String maxArrayElementForPrimitive) {
		this.maxArrayElementForPrimitive = maxArrayElementForPrimitive;
	}

	public boolean isDumpInstance() {
		return dumpInstance;
	}

	public void setDumpInstance(boolean dumpInstance) {
		this.dumpInstance = dumpInstance;
	}

	public int getDumpThreadType() {
		return dumpThreadType;
	}

	public void setDumpThreadType(int dumpThreadType) {
		this.dumpThreadType = dumpThreadType;
	}

	public String getThreadPriority(){
		return this.threadPriority;
	}

	public void setThreadPriority(String threadPriority){
		this.threadPriority = threadPriority;
	}
	protected String getDumpThreadTypeString(){
		switch(dumpThreadType){
		case DUMP_CURRENT_THREAD:
			return DUMP_CURRENT_THREAD_STR;
		case DUMP_ALL_THREAD:
			return DUMP_ALL_THREAD_STR;
		case DUMP_ACTIVE_THREAD:
			return DUMP_ACTIVE_THREAD_STR;
		case DUMP_WAITTING_THREAD:
			return DUMP_WAITTING_THREAD_STR;
		}
		return "current";
	}

	protected void outputThreadDef(Node dumpDataNode){
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_DUMP_ALLTHREADS, getDumpThreadTypeString());
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_Dump_ThreadPriority, this.threadPriority);
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_STACK_DEPTH, stackTraceDepth);
	}
	protected void outputDataType(Node dumpDataNode){
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_THIS, dumpThis);
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_ARGUMENT, dumpArgument);
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_VARIABLE, dumpVariable);
	}

	protected void outputInstance(Node dumpDataNode){
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_DUMP_INSTANCE, dumpInstance);
	}
	protected void outputObjectAttribute(Node dumpDataNode){
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_PRIVATE, dumpPrivate);
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_PROTECTED, dumpProtected);
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_PUBLIC, dumpPublic);
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_PACKAGE, dumpPackage);
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_ATTRIBUTE, dumpAttribute);
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_ATTRIBUTE_DEPTH, attributeNestDepth);
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_MAX_ELEMENT_FOR_OBJECT, maxArrayElementForObject);
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_MAX_ELEMENT_FOR_PRIMITIVE, maxArrayElementForPrimitive);
	}

	protected void outputOthers(Node dumpDataNode){
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_MAIL, noticeMail);
		DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_ATTACH_FILE, attachDumpFile);
		if (autoDelete){
			DocumetUtil.setAttribute(dumpDataNode,ConfigContant.Field_SAVE_DAYS, saveDays);
		}
	}

	protected void inputDocument(Node dumpDataNode) {
		Node item=null;
		NamedNodeMap attrMap = dumpDataNode.getAttributes();

		item = attrMap.getNamedItem(ConfigContant.Field_THIS);
		if (item != null){
			dumpThis = Boolean.parseBoolean(item.getNodeValue());
		}

		item = attrMap.getNamedItem(ConfigContant.Field_STACK_DEPTH);
		if (item != null){
			stackTraceDepth = item.getNodeValue();
		}

		item = attrMap.getNamedItem(ConfigContant.Field_ATTRIBUTE_DEPTH);
		if (item != null){
			attributeNestDepth=item.getNodeValue();
		}

		item = attrMap.getNamedItem(ConfigContant.Field_VARIABLE);
		if (item != null){
			dumpVariable = DocumetUtil.parseBoolean(item.getNodeValue());
		}

		item = attrMap.getNamedItem(ConfigContant.Field_ARGUMENT);
		if (item != null){
			dumpArgument = DocumetUtil.parseBoolean(item.getNodeValue());
		}

		item = attrMap.getNamedItem(ConfigContant.Field_ATTRIBUTE);
		if (item != null){
			dumpAttribute = DocumetUtil.parseBoolean(item.getNodeValue());
		}

		item = attrMap.getNamedItem(ConfigContant.Field_PUBLIC);
		if (item != null){
			dumpPublic = DocumetUtil.parseBoolean(item.getNodeValue());
		}

		item = attrMap.getNamedItem(ConfigContant.Field_PACKAGE);
		if (item != null){
			dumpPackage = DocumetUtil.parseBoolean(item.getNodeValue());
		}

		item = attrMap.getNamedItem(ConfigContant.Field_PRIVATE);
		if (item != null){
			dumpPrivate = DocumetUtil.parseBoolean(item.getNodeValue());
		}

		item = attrMap.getNamedItem(ConfigContant.Field_PROTECTED);
		if (item != null){
			dumpProtected = DocumetUtil.parseBoolean(item.getNodeValue());
		}

		item = attrMap.getNamedItem(ConfigContant.Field_MAX_ELEMENT_FOR_OBJECT);
		if (item != null){
			maxArrayElementForObject=item.getNodeValue();
		}

		item = attrMap.getNamedItem(ConfigContant.Field_MAX_ELEMENT_FOR_PRIMITIVE);
		if (item != null){
			maxArrayElementForPrimitive=item.getNodeValue();
		}

		item = attrMap.getNamedItem(ConfigContant.Field_MAIL);
		if (item != null){
			noticeMail = DocumetUtil.parseBoolean(item.getNodeValue());
		}

		item = attrMap.getNamedItem(ConfigContant.Field_ATTACH_FILE);
		if (item != null){
			attachDumpFile = DocumetUtil.parseBoolean(item.getNodeValue());
		}

		item = attrMap.getNamedItem(ConfigContant.Field_SAVE_DAYS);
		if (item == null){
			autoDelete=false;
		}else{
			saveDays = item.getNodeValue();
			if ("0".equals(saveDays.trim())){
				autoDelete=false;
			}else{
				autoDelete=true;
			}
		}

		item = attrMap.getNamedItem(ConfigContant.Field_DUMP_INSTANCE);
		if (item != null){
			dumpInstance = DocumetUtil.parseBoolean(item.getNodeValue());
		}

		item = attrMap.getNamedItem(ConfigContant.Field_DUMP_ALLTHREADS);
		if (item != null){
			setDumpThreadType(item.getNodeValue());
		}

		item = attrMap.getNamedItem(ConfigContant.Field_Dump_ThreadPriority);
		if (item != null){
			threadPriority = item.getNodeValue();
		}
	}
	private void setDumpThreadType(String type){
		if (DUMP_CURRENT_THREAD_STR.equals(type)){
			dumpThreadType = DUMP_CURRENT_THREAD;
		}else if(DUMP_ALL_THREAD_STR.equals(type)){
			dumpThreadType = DUMP_ALL_THREAD;
		}else if(DUMP_ACTIVE_THREAD_STR.equals(type)){
			dumpThreadType = DUMP_ACTIVE_THREAD;
		}else if(DUMP_WAITTING_THREAD_STR.equals(type)){
			dumpThreadType = DUMP_WAITTING_THREAD;
		}
	}

}
