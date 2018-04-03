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
 * 前に遷移するアクション
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/26
 */
public class ForwordAction extends BaseAction{

	private SourceViewerPlatform sourceViewerPlatform;

	public ForwordAction(MainViewer appWindow) {
		super(appWindow);
		try{
			String text = ApplicationResource.getResource("Menu.Move.Forword.Text");
			setText(text);
			this.setToolTipText(text);

			URL url = MainViewer.class.getResource(IconFilePathConstant.FORWORD);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
			this.setEnabled(false);
		}catch(Exception e){
			HelperFunc.getLogger().error("ForwordAction", e);
		}
	}

	public void setSourceViewerPlatform(SourceViewerPlatform s){
		sourceViewerPlatform = s;
	}

	@Override
	public void doJob() throws Throwable {
		MoveRecord moveRecord = MoveMgr.forword();
		if (moveRecord != null){
			sourceViewerPlatform.moveTo(moveRecord);
		}
	}

}
