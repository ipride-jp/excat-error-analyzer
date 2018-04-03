package jp.co.ipride.excat.configeditor.model.task;

import org.w3c.dom.Node;

/**
 * this is the interface of all task.
 * @author tu-ipride
 * @version 3.0
 * @date 2009/9/26
 */
public interface ITask {

	public static final int AUT_MONITOR_EXCEPTION = 0;
	public static final int MONITOR_EXCEPTION = 1;
	public static final int MONITOR_METHOD = 2;
	public static final int MONITOR_SIGNAL = 3;

	public int getTaskType();

	public boolean isEffect();

	public void setEffect(boolean effect);

	public String getTaskName();

	public void setTaskName(String taskName);

	public String getComment();

	public void setComment(String comment);

	public String getPrefix();

	public String getIdentfyKey();

	public void setPrefix(String prefix);

	public void inputDocument(Node parentNode);

	/**
	 * Config DocumentÇ…èëÇ´çûÇﬁÅB
	 * @param configNode: [Config]
	 */
	public void outputDocument(Node configNode);

}
