package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

/**
 * data interface for search tree
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/15
 */
public interface ICell {

	public Object[] getChildren();

	public Object getParent();

	public boolean hasChildren();

	public Image getImage();

	public String getText();

	public TreeItem selectItem();

}
