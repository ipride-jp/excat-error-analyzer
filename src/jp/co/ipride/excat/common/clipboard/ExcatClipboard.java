package jp.co.ipride.excat.common.clipboard;

import jp.co.ipride.excat.common.action.CopyAction;
import jp.co.ipride.excat.common.action.PasteAction;

import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;
import org.eclipse.jface.text.TextViewer;

/**
 * クリップポートに文字列をコピー
 * またはクリップポートの文字列をテキスト・ボックスにコピー
 * @author tu-ipride
 * @since 2009/10/3
 * @version 3.0
 */
public class ExcatClipboard {

	private static CopyAction copyAction=null;

	private static PasteAction pasteAction = null;

	//can be copied data
	private static String data = null;
	//for paste
	private static Object currentTextBox = null;

	//clip board data
	private static Clipboard clipboard = new Clipboard(Display.getCurrent());

	public static String getText(){
		return data;
	}

	public static void setCopyAction(CopyAction action){
		ExcatClipboard.copyAction = action;
	}

	public static void setPasteAction(PasteAction action){
		ExcatClipboard.pasteAction = action;
	}

	public static void setCopyCandidate(String str){
		data=str;
		if (data == null || "".equals(data)){
			copyAction.setEnabled(false);
		}else{
			copyAction.setEnabled(true);
		}
	}
	public static void setPasteCandidate(Object obj){
		if (obj instanceof Text){
			currentTextBox = obj;
		}else if (obj instanceof TextViewer){
			currentTextBox = obj;
		}else{
			currentTextBox=null;
		}
		//reset copy paste buttons
		if (currentTextBox == null){
			pasteAction.setEnabled(false);
		}else{
			pasteAction.setEnabled(true);
		}
	}

	public static void copy(){
		if (data != null){
			clipboard.setContents(new Object[] { data },
		              new Transfer[] { TextTransfer.getInstance() });
		}
	}

	public static void paste(){
		data = (String) clipboard.getContents(TextTransfer.getInstance());
		if (currentTextBox instanceof Text){
			((Text)currentTextBox).setText(data);
		}else if (currentTextBox instanceof TextViewer){
			((TextViewer)currentTextBox).getDocument().set(data);
		}
	}

	public static void dispose(){
		clipboard.dispose();
	}
}
