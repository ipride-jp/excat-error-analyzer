package jp.co.ipride.excat.analyzer.action;

import java.net.URL;
import java.util.Vector;

import org.eclipse.jface.resource.ImageDescriptor;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.MethodInfo;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.MethodDeclarePlace;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.clipboard.ExcatTemplate;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.task.ITask;

/**
 * ÉÅÉ\ÉbÉhÇÃí«â¡
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/26
 */
public class AddMonitorMethodTask extends BaseAction{

	private MethodDeclarePlace methodDeclarePlace = null;

	public AddMonitorMethodTask(MainViewer appWindow) {
		super(appWindow);
		String Icon = IconFilePathConstant.CONFIG_ADD_METHOD_TASK;
		String text = ApplicationResource.getResource("Menu.Task.MonitorMethod");
		setText(text);
        //modified by Qiu Song on 20091215
		String toolTipText = ApplicationResource.getResource("Menu.Task.MonitorMethod.ToolTip");
		setToolTipText(toolTipText);
        //end of modified by Qiu Song on 20091215
		URL url = MainViewer.class.getResource(Icon);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
		this.setEnabled(false);
	}

	public void setMethodDeclarePlace(MethodDeclarePlace methodDeclarePlace){
		this.methodDeclarePlace = methodDeclarePlace;
	}

	@Override
	public void doJob() throws Throwable {
		MethodInfo methodInfo = methodDeclarePlace.getMethodInfo();
		String msg = Message.get("Analyzer.AddTask.Confirm.Text");
		boolean ret = ExcatMessageUtilty.showConfirmDialogBox(appWindow.getShell(), msg);
		if (ret){
			ExcatTemplate.setMethodInfo(methodInfo);
			appWindow.selectConfig();
		}
	}

}
