package jp.co.ipride.excat.configeditor.model.task;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * this is for MonitorSignalTaskForm
 * @author tu-ipride
 * @version 3.0
 * @date 2009/9/26
 */
public class MonitorSignalTask extends AbstractTask{

	private MonitoringSignal monitoringSignal= new MonitoringSignal();

	public static final int DUMP_INSTANCE=0;

	public static final int DUMP_THREAD = 1;

	private int dumpKind = DUMP_THREAD;

	public MonitorSignalTask(){
		super.setTaskType(MONITOR_SIGNAL);
		super.dumpData= new DumpData(MONITOR_SIGNAL);
		setIdentfyKey();
	}

	/**
	 * @input [Task] tag.
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
		NodeList nodeList = targetsNode.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++){
			Node node = nodeList.item(i);
			String name = node.getNodeName();
			if (ConfigContant.Tag_MONITOR_SIGNAL.equals(name)){
				monitoringSignal.inputDocument(node);
			}
		}
	}

	/**
	 * create [MonitoringSignal] tag under [Config] tag.
	 * @param [config] tag.
	 */
	public void outputDocument(Node configNode) {
		Node taskNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_TASK);
		configNode.appendChild(taskNode);
		outputTaskAttribute(taskNode);

		//add target
		Node monitoringTargetsNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_MONITOR_TARGETS);
		taskNode.appendChild(monitoringTargetsNode);
		monitoringSignal.outputDocument(monitoringTargetsNode);

		//add dump data
		Node dumpDataNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_DUMP_DATA);
		taskNode.appendChild(dumpDataNode);
		if (dumpKind == DUMP_THREAD){
			dumpData.outputThreadDef(dumpDataNode);
		}
		dumpData.outputDataType(dumpDataNode);
		dumpData.outputObjectAttribute(dumpDataNode);
		dumpData.outputOthers(dumpDataNode);
	}

	public MonitoringSignal getMonitoringSignal(){
		return monitoringSignal;
	}

	public int getDumpKind() {
		return dumpKind;
	}

	public void setDumpKind(int dumpKind) {
		this.dumpKind = dumpKind;
	}

	public void setMonitoringSignal(MonitoringSignal monitoringSignal) {
		this.monitoringSignal = monitoringSignal;
	}


}
