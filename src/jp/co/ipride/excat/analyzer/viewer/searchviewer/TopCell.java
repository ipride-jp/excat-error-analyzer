package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

public class TopCell implements ICell{

	private ConditionCell conditionCell;

	private ConditionCell[] array;

	public TopCell(ConditionCell conditionCell){
		this.conditionCell=conditionCell;
		array = new ConditionCell[1];
		array[0] = conditionCell;
	}

	public Object[] getChildren() {
		return array;
	}

	public Image getImage() {
		return null;
	}

	public Object getParent() {
		return null;
	}

	public String getText() {
		return null;
	}

	public boolean hasChildren() {
		return true;
	}

	public TreeItem selectItem() {
		return null;
	}

}
