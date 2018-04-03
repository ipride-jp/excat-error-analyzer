package jp.co.ipride.excat.configeditor.model.task;

import java.util.Vector;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * this is for AutoMonitorExceptionTaskForm
 * @author tu-ipride
 * @version 3.0
 * @date 2009/9/26
 */
public class AutoMonitorExceptionTask extends AbstractTask{

	private static String monitorException = "java.lang.Throwable";

	private MonitoringException monitoringException = null;

	public AutoMonitorExceptionTask(){
		super.setTaskType(AUT_MONITOR_EXCEPTION);
		super.dumpData= new DumpData(AUT_MONITOR_EXCEPTION);
		setIdentfyKey();
		monitoringException = new MonitoringException();
		monitoringException.setTargetClassName(monitorException);
	}

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
			//[MonitorTarget] tag
			if (ConfigContant.Tag_MONITOR_TARGET.equals(name)){
				monitoringException = new MonitoringException();
				monitoringException.inputDocument(node);
			}
		}
	}

	/**
	 * output a [MonitorTargets] tag under [Config] tag.
	 * the [MonitorTargets] must have a [MonitorTarget] monitoring java.lang.Throwabe.
	 */
	public void outputDocument(Node configNode) {
		Node taskNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_TASK);
		configNode.appendChild(taskNode);
		outputTaskAttribute(taskNode);

		//add target
		Node monitoringTargetsNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_MONITOR_TARGETS);
		taskNode.appendChild(monitoringTargetsNode);
		monitoringException.outputDocument(monitoringTargetsNode);

		//add dump data
		Node dumpDataNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_DUMP_DATA);
		taskNode.appendChild(dumpDataNode);
		dumpData.outputThreadDef(dumpDataNode);
		dumpData.outputDataType(dumpDataNode);
		dumpData.outputInstance(dumpDataNode);
		dumpData.outputObjectAttribute(dumpDataNode);
		dumpData.outputOthers(dumpDataNode);
	}

	public Vector<FilterException> getFilterExceptions() {
		return monitoringException.getExcludeExceptions();
	}

	public Vector<Place> getMonitoringPackage() {
		return monitoringException.getPlaces();
	}

	public void setMonitoringPackage(Place place) {
		if (monitoringException.getPlaces().contains(place)){
			monitoringException.getPlaces().add(place);
		}
	}



}
