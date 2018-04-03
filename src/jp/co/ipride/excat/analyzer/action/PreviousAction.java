package jp.co.ipride.excat.analyzer.action;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.SourceViewerPlatform;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.move.MoveMgr;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.move.MoveRecord;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.HelperFunc;

/**
 * ソースビューアの前の跡にバックする
 * @author tu-ipride
 *
 */
public class PreviousAction extends BaseAction{

	private SourceViewerPlatform sourceViewerPlatform;

	public PreviousAction(MainViewer appWindow) {
		super(appWindow);
		try{
			String text = ApplicationResource.getResource("Menu.Move.Previous.Text");
			setText(text);
			this.setToolTipText(text);

			URL url = MainViewer.class.getResource(IconFilePathConstant.PREVIOUS);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
			this.setEnabled(false);
		}catch(Exception e){
			HelperFunc.getLogger().error("PreviousAction", e);
		}
	}
	public void setSourceViewerPlatform(SourceViewerPlatform s){
		sourceViewerPlatform = s;
	}

	@Override
	public void doJob() throws Throwable {
		MoveRecord moveRecord = MoveMgr.back();
		if (moveRecord != null){
			sourceViewerPlatform.moveTo(moveRecord);
		}
	}


}
