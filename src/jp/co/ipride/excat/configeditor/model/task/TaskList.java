package jp.co.ipride.excat.configeditor.model.task;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.configeditor.model.ConfigContant;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

/**
 * タスクを管理するマネージャ
 * @author tu
 * @since 2007/11/19
 */
public class TaskList {

	public static String THROWABLE = "java.lang.Throwable";

	Vector<ITask> tasks= new Vector<ITask>();

	/**
	 * construct
	 *
	 */
	public TaskList(){
	}

	/**
	 * 初期化
	 *
	 */
	public void init(){
		tasks.removeAllElements();
	}

	public ITask createNewTask(int taskType){
		ITask task=null;
		switch(taskType){
		case ITask.AUT_MONITOR_EXCEPTION:
			task = new AutoMonitorExceptionTask();
			break;
		case ITask.MONITOR_EXCEPTION:
			task = new ExceptionRegisterTask();
			break;
		case ITask.MONITOR_METHOD:
			task = new MonitorMethodTask();
			break;
		case ITask.MONITOR_SIGNAL:
			task = new MonitorSignalTask();
		}
		tasks.add(task);
		return task;
	}

	public ITask getTaskByIdentfyKey(String key){
		for (int i = 0; i < tasks.size(); i++){
			ITask task = tasks.get(i);
			if (key.equals(task.getIdentfyKey())){
				return task;
			}
		}
		return null;
	}

	/**
	 * 「Task」タグを読み込む
	 * @param parentNode
	 */
	public void inputDocument(Node taskNode){
		NamedNodeMap map = taskNode.getAttributes();
		Node typeNode = map.getNamedItem(ConfigContant.Field_TYPE);
		if (typeNode != null){
			int type = Integer.parseInt(typeNode.getNodeValue());
			ITask task = createNewTask(type);
			task.inputDocument(taskNode);
		}
	}

	/**
	 * Config Documentに書き込む。
	 * @param root [Config]
	 */
	public void outputDocument(Node root){
		for (int i = 0; i < tasks.size(); i++){
			ITask task = tasks.get(i);
			task.outputDocument(root);
		}
	}

	public void removeTask(ITask task){
		for (int i=0; i<tasks.size(); i++){
			ITask item = tasks.get(i);
			if (item.equals(task)){
				tasks.remove(i);
			}
		}
	}
	public void removeAll(){
		tasks.removeAllElements();
	}
	public Vector<ITask> getTasks(){
		return tasks;
	}

	private int getEffectTaskCount(int type){
		int count =0;
		for (ITask task: tasks){
			if (task.getTaskType() == type	&& task.isEffect()){
				count++;
			}
		}
		return count;
	}

	private boolean checkSameException(){
		List<String> exceptionList = new ArrayList<String>();
		for (ITask task: tasks){
			if (task.getTaskType() == ITask.MONITOR_EXCEPTION	&& task.isEffect()){
				ExceptionRegisterTask eTask = (ExceptionRegisterTask)task;
				for (MonitoringException monitor: eTask.getMonitoringExceptionList()){
					String name = monitor.getTargetClassName();
					if (exceptionList.contains(name)){
						exceptionList.clear();
						ExcatMessageUtilty.showMessage(
								null,
								name + Message.get("Dialog.Task.Monitor.Exception.Same.Text"));
						return false;
					}else{
						exceptionList.add(name);
					}

				}
			}
		}
		return true;
	}

	private boolean checkSameMethodName(){
		List<MonitoringMethod> methodList = new ArrayList<MonitoringMethod>();
		for (ITask task: tasks){
			if (task.getTaskType() == ITask.MONITOR_METHOD	&& task.isEffect()){
				MonitorMethodTask mTask = (MonitorMethodTask)task;
				for (MonitoringMethod method : mTask.getMonitoringMethodList()){
					for (MonitoringMethod oMethod: methodList){
						if (method.isSameMethod(oMethod)){
							methodList.clear();
							String msg = method.getClassName()+"."
										+ method.getMethodName()
										+Message.get("Dialog.Task.Monitor.Method.Check.1.Text");
							ExcatMessageUtilty.showMessage(
									null,
									msg);
							return false;
						}
					}
					//障害 #437 add
					methodList.add(method);
				}
			}
		}
		methodList.clear();
		return true;
	}

	public boolean hasMethodMonitorTask(){
		for (ITask task: tasks){
			if (task.getTaskType()== ITask.MONITOR_METHOD){
				return true;
			}
		}
		return false;
	}

	public boolean checkTasks(){

		//aut-monitor have to only one.
		int autMonitorCount = getEffectTaskCount(ITask.AUT_MONITOR_EXCEPTION);
		if (autMonitorCount>1){
			ExcatMessageUtilty.showMessage(
					null,
					Message.get("Config.Task.Check.MultiAutMonitorTask.Text"));
			return false;
		}
		//check aut-monitor and custom-monitor
		int exceptionCount = getEffectTaskCount(ITask.MONITOR_EXCEPTION);
		if (autMonitorCount>0 && exceptionCount>0){
			ExcatMessageUtilty.showMessage(
					null,
					Message.get("Config.Task.Check.AutoAndExcepTask.Text"));
			return false;
		}
		//check custome-monitor contents.
		if (!checkSameException()){
			return false;
		}

		//check method contents
		if (!checkSameMethodName()){
			return false;
		}

		//check instance-dump's contents.

		//check singal task
		int signalCount = getEffectTaskCount(ITask.MONITOR_SIGNAL);
		if (signalCount>1){
			ExcatMessageUtilty.showMessage(
					null,
					Message.get("Config.Task.Check.MultiSignalTask.Text"));
			return false;
		}

		return true;
	}
}
