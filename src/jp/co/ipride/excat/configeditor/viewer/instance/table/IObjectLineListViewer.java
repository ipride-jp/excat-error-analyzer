package jp.co.ipride.excat.configeditor.viewer.instance.table;

import jp.co.ipride.excat.configeditor.model.instance.ObjectLine;

public interface IObjectLineListViewer {

	public void addObjectLine(ObjectLine objectLine);

	public void removeObjectLine(ObjectLine objectLine);
	
	public void updateObjectLine(ObjectLine objectLine);
}
