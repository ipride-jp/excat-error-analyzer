package jp.co.ipride.excat.configeditor.model.task;

import java.util.Vector;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * this for ExceptionRegisterForm
 * @author tu-ipride
 * @version 3.0
 * @date 2009/9/26
 */
public class ExceptionRegisterTask extends AbstractTask{

	private Vector<MonitoringException> monitoringExceptionList = new Vector<MonitoringException>();

	public ExceptionRegisterTask(){
		super.setTaskType(MONITOR_EXCEPTION);
		super.dumpData= new DumpData(MONITOR_EXCEPTION);
		setIdentfyKey();
	}

	/**
	 * @input: [Task] tag.
	 */
	public void inputDocument(Node taskNode) {
		inputTaskAttribute(taskNode);
		NodeList nodeList = taskNode.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			String name = node.getNodeName();
			if (ConfigContant.Tag_MONITOR_TARGETS.equals(name)){
				inputMonitorTargets(node);
			}else if (ConfigContant.Tag_DUMP_DATA.equals(name)){
				dumpData.inputDocument(node);
			}
		}
	}

	/**
	 * [MonitorTargets]タグのデータを読込む。
	 * @param targetsNode
	 */
	private void inputMonitorTargets(Node targetsNode){
		MonitoringException monitoringException=null;
		NodeList nodeList = targetsNode.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			String name = node.getNodeName();
			//[MonitorTarget] tag
			if (ConfigContant.Tag_MONITOR_TARGET.equals(name)){
				monitoringException = new MonitoringException();
				monitoringException.inputDocument(node);
				monitoringExceptionList.add(monitoringException);
			}
		}
	}

	public void outputDocument(Node configNode) {
		Node taskNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_TASK);
		configNode.appendChild(taskNode);
		outputTaskAttribute(taskNode);

		//add target
		Node monitoringTargetsNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_MONITOR_TARGETS);
		taskNode.appendChild(monitoringTargetsNode);
		for(int i=0; i<monitoringExceptionList.size(); i++){
			monitoringExceptionList.get(i).outputDocument(monitoringTargetsNode);
		}

		//add dump data
		Node dumpDataNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_DUMP_DATA);
		taskNode.appendChild(dumpDataNode);
		dumpData.outputThreadDef(dumpDataNode);
		dumpData.outputDataType(dumpDataNode);
		dumpData.outputInstance(dumpDataNode);
		dumpData.outputObjectAttribute(dumpDataNode);
		dumpData.outputOthers(dumpDataNode);
	}

	public Vector<MonitoringException> getMonitoringExceptionList(){
		return monitoringExceptionList;
	}

	public void addMonitoringException(MonitoringException monitoringException){
		if (!monitoringExceptionList.contains(monitoringException)){
			monitoringExceptionList.add(monitoringException);
		}
	}

}
