package jp.co.ipride.excat.configeditor.viewer.action;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.action.BaseAction;
import org.eclipse.jface.window.ApplicationWindow;

public class SetActiveAction extends BaseAction{

	private int index = 0 ;
	public SetActiveAction(MainViewer appWindow){
		super(appWindow);
		//setToolTipText(ApplicationResource.getResource("Menu.File.Exit.Text"));
		setText(
			"\u4E2D\u6B62\u0020"
		);
	}

	public void doJob() {
		//TODO
//		ConfigModel.getTaskList().getTaskByTaskTagId(index).setIsActive(false);
	}

	public void setTaskIndex(int index) {
		this.index = index;
	}

	public void setActionText(String text) {
		this.setText(text);
	}

}