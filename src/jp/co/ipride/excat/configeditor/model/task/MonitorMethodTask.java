package jp.co.ipride.excat.configeditor.model.task;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * this is for MonitorMethodTaskForm.
 * @author tu-ipride
 * @version 3.0
 * @date 2009/9/26
 */
public class MonitorMethodTask extends AbstractTask{

	private Vector<MonitoringMethod> monitoringMethodList = new Vector<MonitoringMethod>();

	public MonitorMethodTask(){
		super.setTaskType(MONITOR_METHOD);
		super.dumpData= new DumpData(MONITOR_METHOD);
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
			if (ConfigContant.Tag_MONITOR_METHOD.equals(name)){
				inputMonitorMethods(node);
			}
		}
	}

	/**
	 * クラス定義を読込み
	 * @param [monitorMethod] tag
	 */
	private void inputMonitorMethods(Node monitorMethodNode){
		Node node=null;
		String className="";
		String classLoaderURL="";
		String classLoader_suffix="";
		//get class infomation.
		NamedNodeMap map = monitorMethodNode.getAttributes();

		node = map.getNamedItem(ConfigContant.Field_CLASS);
		className = node.getNodeValue();

		node = map.getNamedItem(ConfigContant.Field_CLASSLOADER);
		if (node != null){
			classLoaderURL=node.getNodeValue();
		}

		node = map.getNamedItem(ConfigContant.Field_SUFFIX);
		if (node != null){
			classLoader_suffix=node.getNodeValue();
		}

		//[Method] tag
		NodeList nodeList = monitorMethodNode.getChildNodes();
		for (int i=0; i<nodeList.getLength(); i++){
			Node methodNode = nodeList.item(i);
			String name = methodNode.getNodeName();
			if (ConfigContant.Tag_METHOD.equals(name)){
				MonitoringMethod monitoring = new MonitoringMethod();
				monitoring.setClassName(className);
				monitoring.setClassLoader_suffix(classLoader_suffix);
				monitoring.setClassLoaderURL(classLoaderURL);
				monitoring.inputDocument(methodNode);
				monitoringMethodList.add(monitoring);
			}
		}

	}

	/**
	 * Config  <- input
	 *  Task
	 *    MonitoringTargets
	 *      MonitoringMethod  [Class][ClassLoaderString][Suffix]
	 *        Method   [Name][Signature][Condition][MaxDumpCount][Suffix][valid]
	 *
	 * @param configNode: [Config] tag.
	 */
	public void outputDocument(Node configNode) {
		Node taskNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_TASK);
		configNode.appendChild(taskNode);
		outputTaskAttribute(taskNode);

		//create monitoring targets
		Node monitoringTargetsNode = ConfigModel.getDocument().createElement(ConfigContant.Tag_MONITOR_TARGETS);
		taskNode.appendChild(monitoringTargetsNode);

		//create monitoring method
		//first,merge monitorMethod besed on class name and class loader url.
		Map<String,Vector<MonitoringMethod>> map = new HashMap<String,Vector<MonitoringMethod>>();
		for (int i=0; i<monitoringMethodList.size(); i++){
			MonitoringMethod method = monitoringMethodList.get(i);
			String key = method.getClassName() + method.getClassLoaderURL();
			Vector<MonitoringMethod> methods = map.get(key);
			if (methods == null){
				methods = new Vector<MonitoringMethod>();
				methods.add(method);
				map.put(key,methods);
			}else{
				methods.add(methods.size(),method);
			}
		}
		//create document of each monitoring method.
		Iterator<String> iterator = map.keySet().iterator();
		while (iterator.hasNext()){
			String key = (String)iterator.next();
			Vector<MonitoringMethod> methods = map.get(key);
			Node monitoringMethod = ConfigModel.getDocument().createElement(ConfigContant.Tag_MONITOR_METHOD);
			monitoringTargetsNode.appendChild(monitoringMethod);
			for (int i=0; i<methods.size(); i++){
				MonitoringMethod method = (MonitoringMethod)methods.get(i);
				method.outputDocument(monitoringMethod);
			}
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

	public Vector<MonitoringMethod> getMonitoringMethodList(){
		return monitoringMethodList;
	}

	public void addMonitoringMethod(MonitoringMethod monitoringMethod){
		if (!monitoringMethodList.contains(monitoringMethod)){
			monitoringMethodList.add(monitoringMethod);
		}
	}
}
