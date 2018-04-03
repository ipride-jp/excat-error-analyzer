package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import jp.co.ipride.excat.MainViewer;

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeItem;

/**
 * tree viewer for search text and object.
 * @author tu-ipride
 *
 */
public class SearchTree {

	private MainViewer appWindow;

	private TreeViewer treeViewer;

	public SearchTree(MainViewer appWindow, Composite parent, int style) {
		this.appWindow=appWindow;
		treeViewer = new TreeViewer(parent, style);
		treeViewer.setContentProvider(new SearchContentProvider());
		treeViewer.setLabelProvider(new SearchLabelProvider(this.appWindow));
		addSelectionListener();
	}

	public void addSelectionListener(){
		treeViewer.addDoubleClickListener(new IDoubleClickListener(){
		public void doubleClick(DoubleClickEvent event) {
			TreeItem item = treeViewer.getTree().getSelection()[0];
			ICell data = (ICell)item.getData();
			data.selectItem();
		}
		});
	}

	/**
	 * input data.
	 * @param data
	 */
	public void inputData(Object data){
		treeViewer.setInput(data);
	}

}
