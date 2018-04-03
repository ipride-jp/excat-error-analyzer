package jp.co.ipride.excat.analyzer.action;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.dialog.search.TextSearchDialog;
import jp.co.ipride.excat.analyzer.viewer.searchviewer.ConditionUnit;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.ImageDescriptor;

/**
 * Java僜乕僗價儏乕傾偺拞偺崁栚偵懳偡傞専嶕偡傞
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/7
 */
public class DumpDataSearchAction extends BaseAction{

	private ConditionUnit conditionFileView = null;
	private ConditionUnit conditionStackView = null;

	public DumpDataSearchAction(MainViewer appWindow){
		super(appWindow);
		String text = ApplicationResource.getResource("Menu.support.DumpDataSearch");
		setText(text);
		//modified by Qiu Song on 20091215 for ツールバーの表示改善
		String toolTipText = ApplicationResource
		.getResource("Menu.support.DumpDataSearch.ToolTip");
		this.setToolTipText(toolTipText);
        //end of modified by Qiu Song on 20091215 for ツールバーの表示改善
		URL url = MainViewer.class.getResource(IconFilePathConstant.DUMP_REF);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
		setDisabledImageDescriptor(ImageDescriptor.createFromURL(url));
	}

	public void doJob() {
		TextSearchDialog dlg = new TextSearchDialog(appWindow);
		dlg.setHistoryContiditions(conditionFileView, conditionStackView);

		if (dlg.open()==Dialog.OK){
			conditionFileView = dlg.getHistoryConditions()[0];
			conditionStackView = dlg.getHistoryConditions()[1];
			appWindow.analyzerform.searchText(dlg.getTextSearchUnit());
		}
	}
}
