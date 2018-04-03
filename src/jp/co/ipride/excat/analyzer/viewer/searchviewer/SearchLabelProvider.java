package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import jp.co.ipride.excat.MainViewer;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.graphics.Image;

public class SearchLabelProvider implements ILabelProvider{

	private MainViewer mainViewer;

	public SearchLabelProvider(MainViewer mainViewer){
		this.mainViewer=mainViewer;
	}

	public Image getImage(Object arg) {
		return ((ICell)arg).getImage();
	}

	public String getText(Object arg) {
		return ((ICell)arg).getText();
	}

	public void addListener(ILabelProviderListener arg) {
	}

	public void dispose() {
	}

	public boolean isLabelProperty(Object arg0, String arg1) {
		return false;
	}

	public void removeListener(ILabelProviderListener arg) {
	}

}
