package jp.co.ipride.excat.analyzer.action;

import java.net.URL;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.action.BaseAction;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.resource.ImageDescriptor;

public class SetFileViewerVisibleAction extends BaseAction {

	// 表示/非表示状態
	private boolean visibleState = true;

	/**
	 * コンストラクタ
	 *
	 * @param appWindow
	 */
	public SetFileViewerVisibleAction(MainViewer appWindow) {
		super(appWindow);
		try {
			// 規定値は表示する状態とする
			setDisplayMenuInvisible();
		} catch (Exception e) {
			HelperFunc.getLogger().debug(e);
		}
	}

	/**
	 * 切り替え
	 *
	 * @see org.eclipse.jface.action.IAction#run()
	 */
	public void doJob() {
		try {
			if (visibleState) {
				// 非表示処理を実行
				((MainViewer) appWindow).setFileViewerVisible(false);
				visibleState = false;
				// メニューを「表示する」に切り換える
				setDisplayMenuVisible();
			} else {
				// 表示処理を実行
				((MainViewer) appWindow).setFileViewerVisible(true);
				// メニューを「表示しない」に切り換える
				visibleState = true;
				setDisplayMenuInvisible();
			}
		} catch (Exception e) {
			HelperFunc.getLogger().error("SetFileViewerVisibleAction", e);
			ExcatMessageUtilty.showErrorMessage(this.appWindow.getShell(),e);
		}
	}

	/**
	 * 「ファイルビューを表示するメニュー」を設定する
	 *
	 * @throws Exception
	 */
	private void setDisplayMenuVisible() throws Exception {
		String text = ApplicationResource
				.getResource("Menu.View.showFileViewer.Text");
		setText(text);
		this.setToolTipText(text);

		URL url = MainViewer.class
				.getResource(IconFilePathConstant.FILE_VIEWER_VISIBLE);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
	}

	/**
	 * 「ファイルビューを表示しない」メニューを設定する
	 *
	 * @throws Exception
	 */
	private void setDisplayMenuInvisible() throws Exception {
		String text = ApplicationResource
				.getResource("Menu.View.hideFileViewer.Text");
		setText(text);
		this.setToolTipText(text);

		URL url = MainViewer.class
				.getResource(IconFilePathConstant.FILE_VIEWER_INVISIBLE);
		setImageDescriptor(ImageDescriptor.createFromURL(url));
	}

}
