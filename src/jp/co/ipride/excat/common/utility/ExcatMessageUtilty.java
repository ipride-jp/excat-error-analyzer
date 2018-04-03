package jp.co.ipride.excat.common.utility;

import jp.co.ipride.excat.common.dialog.ExcatConfirmDialog;
import jp.co.ipride.excat.common.dialog.ExcatMessageDialog;

import org.eclipse.swt.widgets.Shell;

/**
 * 全てのメッセージ・ダイアローグ
 * @author tu-ipride
 * @version 2.0
 * @date 2009/10/17 update
 */
public class ExcatMessageUtilty {

	/**
	 * 例外時のメッセージ表示
	 * @param e
	 */
	public static void showErrorMessage(Shell shell,Throwable e) {
		HelperFunc.logException(e);
//		showMessage(shell, e.toString());
		ExcatMessageDialog dialog = new ExcatMessageDialog(shell,e.toString());
		dialog.open();
	}

	/**
	 * 一般のメッセージ表示
	 * @param msg
	 */
	public static void showMessage(Shell shell, String msg) {
//		try{
//			URL url = MainViewer.class.getResource(IconFilePathConstant.EXCAT_TM_SMALL_16);
//			org.eclipse.jface.dialogs.MessageDialog message = new org.eclipse.jface.dialogs.MessageDialog(
//					null, Message.get("MessageDialog.Title"),
//					ImageDescriptor.createFromURL(url).createImage(), msg, 0,
//					new String[] { Message.get("MessageDialog.Button") }, 1);
//			message.open();
//		}catch(Exception e){
//			e.printStackTrace();
//		}
		ExcatMessageDialog dialog = new ExcatMessageDialog(shell,msg);
		dialog.open();
	}


	/**
	 * ユーザーに確認ダイアログボックスを表示する
	 * @param title　タイトル
	 * @param msg　メッセージ内容
	 * @return ユーザの選択
	 */
	public static boolean showConfirmDialogBox(Shell shell,String msg){
//		MessageBox msgBox = new MessageBox(parent,SWT.YES|SWT.NO);
//		msgBox.setMessage(msg);
//		msgBox.setText(title);
//		int result = msgBox.open();

		ExcatConfirmDialog dialog = new ExcatConfirmDialog(shell, msg);
		dialog.open();
		return dialog.result;
	}
}