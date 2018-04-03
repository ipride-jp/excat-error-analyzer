package jp.co.ipride.excat.configeditor.util;

import java.net.URL;
import java.util.Vector;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.dialog.ExcatConfrim3BtnDialog;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.model.task.FilterException;
import jp.co.ipride.excat.configeditor.viewer.property.MessageProperty;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;

/**
 * ユーティリティ
 * @author tu
 * @since 2007/12/5
 */
public class ViewerUtil {

	public static int TEXT_HEIGHT = 40;

	/**
	 * 複数のカラムに結合する
	 * @param control
	 * @param n
	 */
	public static void jointHorizontalSpan(Control control, int n){
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = n;
		control.setLayoutData(gd);
	}

	/**
	 * 画面要素の幅を設定
	 * @param control
	 * @param width
	 */
	public static void setControlWidth(Control control, int width){
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.widthHint=width;
        control.setLayoutData(gd);
	}

	public static Point getLocation(Point frameSize){
		Rectangle screenRtg = Display.getDefault().getClientArea();
		Point location= new Point(
		(screenRtg.width - frameSize.x) / 2,
		(screenRtg.height - frameSize.y) / 2);
		return location;
	}

	/**
	 * コンフィグ内容を切り替える場合のチェック
	 * @param appWindow
	 * @return
	 *    -1:変更なし
	 */
	public static int reloadConfig(ApplicationWindow appWindow){
		if (ConfigModel.isChanged()){
			// 2008/12/02 begin 周
			// アイコン変更
			//URL url = ConfigEditorApp.class.getResource("icons/ConfigEditor.gif");
			URL url = MainViewer.class.getResource(IconFilePathConstant.EXCAT_TM_SMALL_16);
			// 2008/12/02 end
//			MessageDialog dialog = new MessageDialog(
//					appWindow.getShell(),
//					MessageProperty.getResource("Dialog.Title.Question"),
//					ImageDescriptor.createFromURL(url).createImage(),
//					MessageProperty.getResource("Dialog.Config.Change.Text"),
//			        MessageDialog.QUESTION,
//			        new String[]{
//						MessageProperty.getResource("Dialog.Button.Yes"),
//						MessageProperty.getResource("Dialog.Button.No"),
//						MessageProperty.getResource("Dialog.Button.Cancel")},
//			        0
//			);
			ExcatConfrim3BtnDialog dialog = new ExcatConfrim3BtnDialog(appWindow.getShell(),
																	   MessageProperty.getResource("Dialog.Config.Change.Text"));
			return dialog.open();
		}
		return -2;
	}

	/**
	 * チェック数字型の入力
	 * @param number
	 * @param min
	 * @return
	 */
	public static boolean isLargerThanMinValue(String number, int min){
		if (number == null){
			return false;
		}
		if ("".equals(number)){
			return false;
		}
		if ("0".equals(number.substring(0,1))){
			return false;
		}
		try{
			int n = Integer.parseInt(number);
			if (n > min){
				return true;
			}else{
				return false;
			}
		}catch(RuntimeException e){
			return false;
		}
	}

	/**
	 *NULLと空白である一般的なチェック
	 * @param item
	 * @return
	 */
	public static boolean checkStringItem(String item){
		if (item == null || "".equals(item.trim())){
			return false;
		}
		return true;
	}

	/**
	 * クラス名のチェック
	 * @param className
	 * @return
	 */
	public static boolean checkClassName(String className){
		if (className == null || "".equals(className.trim())){
			return false;
		}
		if (className.trim().indexOf(" ")>=0){
			return false;
		}
		return true;
	}

	/**
	 * メソッドのチェック
	 * @param methodName
	 * @return
	 */
	public static boolean checkMethodName(String methodName){
		boolean r = checkClassName(methodName);
		if (!r){
			return false;
		}
		if (methodName.indexOf(".")>=0){
			return false;
		}
		return true;
	}

	/**
	 * メソッドのシグネチャのチェック
	 * @param sig
	 * @return
	 */
	public static boolean checkSignature(String sig){
		return checkClassName(sig);
	}

	/**
	 * クラス・ロードのチェック
	 * @param loader
	 * @return
	 */
	public static boolean checkClassLoader(String loader){
		return checkClassName(loader);
	}

	/**
	 * 拡張子のチェック
	 * @param suffix
	 * @return
	 */
	public static boolean checkSuffix(String suffix){
		if (!checkStringItem(suffix)){
			return false;
		}
		if (!suffix.matches("[0-9a-zA-Z]+")){
			return false;
		}
		return true;
	}

	public static boolean verifyNumbers(String s){
		int l=s.length();
		for (int i=0; i<l; i++){
			String atom = s.substring(i,i+1);
			if ("0123456789".indexOf(atom) < 0){
				return false;
			}
		}
		return true;
	}

//	public static void showErrorMessageDialog(String msg_id){
//		// 2008/12/02 begin 周
//		// アイコン変更
//		//URL url = ConfigEditorApp.class.getResource("icons/ConfigEditor.gif");
//		URL url = MainViewer.class.getResource("/jp/co/ipride/excat/common/icons/config_tool_icon16.png");
//		// 2008/12/02 end
//		MessageDialog dialog = new MessageDialog(
//				null,
//				MessageProperty.getResource("Dialog.Title.Error"),
//				ImageDescriptor.createFromURL(url).createImage(),
//				MessageProperty.getResource(msg_id),
//		        MessageDialog.ERROR,
//		        new String[]{MessageProperty.getResource("Dialog.Button.Yes")},
//		        0
//		);
//		dialog.open();
//	}
//
//	public static void showWarningMessageDialog(String msg_id){
//		URL url = MainViewer.class.getResource(IconFilePathConstant.EXCAT_TM_SMALL_16);
//		MessageDialog dialog = new MessageDialog(
//				null,
//				MessageProperty.getResource("Dialog.Title.Warning"),
//				ImageDescriptor.createFromURL(url).createImage(),
//				MessageProperty.getResource(msg_id),
//		        MessageDialog.WARNING,
//		        new String[]{MessageProperty.getResource("Dialog.Button.Yes")},
//		        0
//		);
//		dialog.open();
//	}
//
//	public static void showInfoMessageDialog(String msg_id){
//		URL url = MainViewer.class.getResource(IconFilePathConstant.EXCAT_TM_SMALL_16);
//		MessageDialog dialog = new MessageDialog(
//				Display.getCurrent().getActiveShell(),
//				MessageProperty.getResource("Dialog.Title.Information"),
//				ImageDescriptor.createFromURL(url).createImage(),
//				MessageProperty.getResource(msg_id),
//		        MessageDialog.INFORMATION,
//		        new String[]{MessageProperty.getResource("Dialog.Button.Yes")},
//		        0
//		);
////		dialog.getShell().setMinimumSize(400, 300);
//		dialog.open();
//	}
//
//	public static int showQuestionMessageDialog(Shell shell, String msg_id){
//		URL url = MainViewer.class.getResource(IconFilePathConstant.EXCAT_TM_SMALL_16);
//		MessageDialog dialog = new MessageDialog(
//				shell,
//				MessageProperty.getResource("Dialog.Title.Question"),
//				ImageDescriptor.createFromURL(url).createImage(),
//				MessageProperty.getResource(msg_id),
//		        MessageDialog.QUESTION,
//		        new String[]{MessageProperty.getResource("Dialog.Button.Yes"),
//					MessageProperty.getResource("Dialog.Button.No")},
//		        1
//		);
////		dialog.getShell().setMinimumSize(400, 300);
//		return dialog.open();
//	}

	//編集部分：ツリー部分＝80:20
	static int configWinPercent = 80;

	public static int getConfigPlateWidth(Composite composite){
		Rectangle trim= composite.getShell().getDisplay().getPrimaryMonitor().getClientArea();
		int width = trim.width;
		if (width >1600){
			return width*configWinPercent/100/2-100;
		}else if (width > 1200){
			return width*configWinPercent/100/2-70;
		}else{
			return width*configWinPercent/100/2-50;
		}
	}

	public static int getMarginWidth(Composite composite){
		Rectangle trim= composite.getShell().getDisplay().getPrimaryMonitor().getClientArea();
		int width = trim.width;
		if (width >1600){
			return 10;
		}else if (width > 1200){
			return 7;
		}else{
			return 5;
		}
	}

	public static int getMarginHeight(Composite composite){
		Rectangle trim= composite.getShell().getDisplay().getPrimaryMonitor().getClientArea();
		int height = trim.height;
		if (height >1200){
			return 5;
		}else if (height > 700){
			return 3;
		}else{
			return 2;
		}
	}

	public static Rectangle getScrollMinRectangle(Composite composite){
		Rectangle trim= composite.getShell().getDisplay().getPrimaryMonitor().getClientArea();
		trim.height = trim.height*100/80;
		return trim;
	}

	public static TextViewer createTextViewer(Composite composite, int witdth, int span){
		TextViewer textViewer;
		SashForm textSite = new SashForm(composite, SWT.NONE);
		GridLayout textlayout = new GridLayout();
		textSite.setLayout(textlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END | GridData.VERTICAL_ALIGN_FILL);
        gd.widthHint = witdth;
		gd.horizontalSpan = span;
        textSite.setLayoutData(gd);
        textViewer = new TextViewer(textSite, SWT.LEFT | SWT.SINGLE | SWT.BORDER
        		| GridData.HORIZONTAL_ALIGN_END);
        textViewer.setDocument(new Document());
        return textViewer;
	}

}
