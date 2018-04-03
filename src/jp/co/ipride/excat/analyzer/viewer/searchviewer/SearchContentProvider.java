package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class SearchContentProvider implements ITreeContentProvider{

	public Object[] getChildren(Object arg) {
		return ((ICell)arg).getChildren();
	}

	public Object getParent(Object arg) {
		return ((ICell)arg).getParent();
	}

	public boolean hasChildren(Object arg) {
		return ((ICell)arg).hasChildren();
	}

	public Object[] getElements(Object arg) {
		return ((ICell)arg).getChildren();
	}

	public void dispose() {
	}

	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}


}
