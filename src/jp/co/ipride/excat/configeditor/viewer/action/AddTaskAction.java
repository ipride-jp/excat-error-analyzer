package jp.co.ipride.excat.configeditor.viewer.action;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.configeditor.model.task.ITask;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * 僞僗僋捛壛
 * @author tu
 * @since 2007/11/10
 * @date 2009/9/5 update for v3
 */
public class AddTaskAction extends BaseAction
								implements SelectionListener{

	private int taskType = 0;

	public AddTaskAction(MainViewer appWindow, int taskType){
		super(appWindow);
		this.taskType = taskType;

		String Icon = "";
		String text = "";

		//modifide by Qiu Song on 20091215 for ツールバー表示不正
		String toolTipText = "";
		switch(taskType){
			case ITask.AUT_MONITOR_EXCEPTION:
			Icon = IconFilePathConstant.CONFIG_ADD_AUTO_TASK;
			text = ApplicationResource.getResource("Menu.Task.AutoMonitorException");
			toolTipText = ApplicationResource.getResource("Menu.Task.AutoMonitorException.ToolTip");
			break;
			case ITask.MONITOR_EXCEPTION:
			Icon = IconFilePathConstant.CONFIG_ADD_EXCEPTION_TASK;
			text = ApplicationResource.getResource("Menu.Task.MonitorException");
			toolTipText = ApplicationResource.getResource("Menu.Task.MonitorException.ToolTip");
			break;
			case ITask.MONITOR_METHOD:
			Icon = IconFilePathConstant.CONFIG_ADD_METHOD_TASK;
			text = ApplicationResource.getResource("Menu.Task.MonitorMethod");
			toolTipText = ApplicationResource.getResource("Menu.Task.MonitorMethod.ToolTip");
			break;
			case ITask.MONITOR_SIGNAL:
			Icon = IconFilePathConstant.CONFIG_ADD_SIGNAL_TASK;
			text = ApplicationResource.getResource("Menu.Task.MonitorSig");
			toolTipText = ApplicationResource.getResource("Menu.Task.MonitorSig.ToolTip");
			break;
		}

		setToolTipText(toolTipText);
		//end of modified by Qiu Song on 20091215 for ツールバー表示不正
		setText(text);
		URL url = MainViewer.class.getResource(Icon);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
//		URL url1 = MainViewer.class.getResource(IconFilePathConstant.CONFIG_ADD_TASK_DISABLE);
//		this.setDisabledImageDescriptor(ImageDescriptor.createFromURL(url1));
	}

	public void doJob(){
		appWindow.addNewTask(this.taskType);

	}

	/**
	 * implement SelectionListener
	 * @param event
	 */
	public void widgetSelected(SelectionEvent event) {
		this.setEnabled(true);


	}
	/**
	 * no implement SelectionListener
	 * @param arg0
	 */
	public void widgetDefaultSelected(SelectionEvent arg0) {}
}
