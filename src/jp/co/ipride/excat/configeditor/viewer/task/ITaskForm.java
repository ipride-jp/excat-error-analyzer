package jp.co.ipride.excat.configeditor.viewer.task;

import jp.co.ipride.excat.configeditor.model.task.ITask;

public interface ITaskForm {

	public ITask getTask();
	public void selectDumpStackBySignal();
	public void selectDumpObjectBySignal();

}
