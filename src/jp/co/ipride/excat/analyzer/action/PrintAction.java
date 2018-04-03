package jp.co.ipride.excat.analyzer.action;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.resource.ImageDescriptor;

/**
 * àÛç¸ÉAÉNÉVÉáÉì
 * @author Ê‚Ç¶ÇÒ
 * @since 2007/10/02
 */
public class PrintAction extends BaseAction {
	public PrintAction(MainViewer appWindow) {
		super(appWindow);
		try {
			String text = ApplicationResource.getResource("Menu.Print.Text");
			setText(text);
			setEnabled(false);
			this.setToolTipText(text);
			URL url = MainViewer.class.getResource(IconFilePathConstant.PRINT);
			setImageDescriptor(ImageDescriptor.createFromURL(url));
			setDisabledImageDescriptor(ImageDescriptor.createFromURL(url));
		} catch (Exception e) {
			HelperFunc.getLogger().error("PrintAction", e);
		}
	}

	public void doJob() throws Exception {
		MainViewer viewer = (MainViewer)this.appWindow;
		viewer.print();
	}
}
