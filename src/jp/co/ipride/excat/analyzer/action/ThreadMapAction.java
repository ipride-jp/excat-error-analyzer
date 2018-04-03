package jp.co.ipride.excat.analyzer.action;

import java.io.File;
import java.net.URL;
import java.util.List;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.dialog.ThreadMapDialog;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.events.SelectionEvent;

/**
 * É^ÉXÉNí«â¡
 * @author wang
 * @since 2009/07/15
 *
 */
public class ThreadMapAction extends BaseAction{

	private MainViewer mainviewer;

	public ThreadMapAction(MainViewer appWindow){
		super(appWindow);
		this.mainviewer = (MainViewer)appWindow;
		setToolTipText(ApplicationResource.getResource("Menu.support.analyzeThreadMap.ToolTip"));
		setText(ApplicationResource.getResource("Menu.support.analyzeThreadMap"));
		URL url = MainViewer.class.getResource(IconFilePathConstant.OPEN_THREAD_MAP_DIALOG);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
		URL url1 = MainViewer.class.getResource(IconFilePathConstant.OPEN_THREAD_MAP_DIALOG);
		this.setDisabledImageDescriptor(ImageDescriptor.createFromURL(url1));
	}

	public void doJob(){
		File rootFile = mainviewer.analyzerform.fileViewer.getSelectPath();
		ThreadMapDialog dialog = new ThreadMapDialog(this.mainviewer, rootFile);
		if (dialog.open() == Dialog.OK){
			List<List<String>> data = dialog.getData();
			mainviewer.analyzerform.setThreadPathMap(data);
		}
	}

	/**
	 * implement SelectionListener
	 * @param event
	 */
	public void widgetSelected(SelectionEvent event) {
		this.setEnabled(true);


	}
	/**
	 * no implement SelectionListener
	 * @param arg0
	 */
	public void widgetDefaultSelected(SelectionEvent arg0) {}
}
