package jp.co.ipride.excat.analyzer.action;

import java.net.URL;

import org.eclipse.jface.resource.ImageDescriptor;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.viewer.sourceviewer.declare.IGoToDeclare;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;

/**
 * 定義へ遷移するアクション
 * @author tu-ipride
 * @version 3.0
 * @since 2009/10/27
 */
public class GoToDeclareAction extends BaseAction{

	private IGoToDeclare declare;

	public GoToDeclareAction(MainViewer appWindow) {
		super(appWindow);
		String text = ApplicationResource.getResource("Menu.Navigation.GoToDeclare.Text");
		setText(text);
		//modified by Qiu Song on 20091217 for tooltip name
		String toolTipText = ApplicationResource.getResource("Menu.Navigation.GoToDeclare.ToolTip");
		this.setToolTipText(toolTipText);
		//end of modified by Qiu Song on 20091217 for tooltip name

		URL url = MainViewer.class.getResource(IconFilePathConstant.GoToDeclare);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
		this.setEnabled(false);
	}

	@Override
	public void doJob() throws Throwable {
		declare.gotoDeclarePlace();
	}

	public void SetGoToDeclare( IGoToDeclare declare){
		this.declare = declare;
	}

}
