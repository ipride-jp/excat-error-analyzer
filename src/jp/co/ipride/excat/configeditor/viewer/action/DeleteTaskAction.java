package jp.co.ipride.excat.configeditor.viewer.action;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 *
 * @author tu
 * @since 2007/11/10
 *
 */
public class DeleteTaskAction extends BaseAction
								implements SelectionListener{

	public DeleteTaskAction(MainViewer appWindow){
		super(appWindow);
		setToolTipText(ApplicationResource.getResource("Menu.Edit.DeleteTask.ToolTip"));
		setText(ApplicationResource.getResource("Menu.Edit.DeleteTask"));
		URL url = MainViewer.class.getResource(IconFilePathConstant.CONFIG_REMOVE_TASK);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
		URL url1 = MainViewer.class.getResource(IconFilePathConstant.CONFIG_REMOVE_TASK_DISABLE);
		this.setDisabledImageDescriptor(ImageDescriptor.createFromURL(url1));
	}

	public void doJob(){
		boolean ret = ExcatMessageUtilty.showConfirmDialogBox(
				this.appWindow.getShell(),
				Message.get("Dialog.Task.Delete.Text"));
		if (ret){
			appWindow.deleteCurrentTask();
		}
	}

	/**
	 * implement SelectionListener
	 * @param event
	 */
	public void widgetSelected(SelectionEvent event) {

		if (((MainViewer)appWindow).canDeleteThisTask()){
			this.setEnabled(true);
		}else{
			this.setEnabled(false);
		}

	}
	/**
	 * no implement SelectionListener
	 * @param arg0
	 */
	public void widgetDefaultSelected(SelectionEvent arg0) {}
}
