package jp.co.ipride.excat.configeditor;

import jp.co.ipride.excat.common.clipboard.ExcatClipboard;

import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;


/**
 * this is a proxy of Text box.
 *
 * @author tu-ipride
 * @since 2009/10/3
 * @version 3.0
 */
public class ExcatText{

  private  Text text = null;	//target

	public ExcatText(Composite composite, int style) {
		text = new Text(composite, style);
		addClipboardListener();
	}

	private void addClipboardListener(){
	    text.addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent focusevent) {
				ExcatClipboard.setPasteCandidate(text);
			}

			public void focusLost(FocusEvent focusevent) {
			}
	    });

	    text.addMouseListener(new MouseListener(){

			public void mouseDoubleClick(MouseEvent mouseevent) {
				String data = text.getSelectionText();
				ExcatClipboard.setCopyCandidate(data);
			}

			public void mouseDown(MouseEvent mouseevent) {
			}

			public void mouseUp(MouseEvent mouseevent) {
				String data = text.getSelectionText();
				ExcatClipboard.setCopyCandidate(data);
			}

	    });

	    text.addKeyListener(new KeyListener(){

			public void keyPressed(KeyEvent keyevent) {
			}

			public void keyReleased(KeyEvent keyevent) {
				String data = text.getSelectionText();
				ExcatClipboard.setCopyCandidate(data);
			}

	    });
	}

	public void addModifyListener(ModifyListener modifylistener){
		text.addModifyListener(modifylistener);
	}

	public void addVerifyListener(VerifyListener verifyListener){
		text.addVerifyListener(verifyListener);
	}

	public void addKeyListener(KeyListener keyListener){
		text.addKeyListener(keyListener);
	}

	public void setText(String data){
		text.setText(data);
	}

	public String getText(){
		return text.getText();
	}

	public void setBackground(Color color){
		text.setBackground(color);
	}

	public void setEnabled(boolean flag){
		text.setEnabled(flag);
	}

	public void setLayoutData(Object obj){
		text.setLayoutData(obj);
	}

	public void setFont(Font font){
		text.setFont(font);
	}

	public void setFocus(){
		text.setFocus();
	}

	public void setSelection(int a, int b){
		text.setSelection(a, b);
	}

	public void setEditable(boolean flag){
		text.setEditable(flag);
	}

	public boolean getEnabled(){
		return text.getEnabled();
	}

	public int getLineHeight(){
		return text.getLineHeight();
	}

	public Rectangle computeTrim(int a, int b, int c, int d){
		return text.computeTrim(a, b, c, d);
	}


	/**
	 * ï°êîÇÃÉJÉâÉÄÇ…åãçáÇ∑ÇÈ
	 * @param control
	 * @param n
	 */
	public void jointHorizontalSpan( int n){
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);
		gd.horizontalSpan = n;
		text.setLayoutData(gd);
	}

	/**
	 * âÊñ óvëfÇÃïùÇê›íË
	 * @param control
	 * @param width
	 */
	public void setControlWidth(int width){
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.widthHint=width;
        text.setLayoutData(gd);
	}

	public Text getTextBox(){
		return text;
	}

	public void setVisible(boolean flag){
		text.setVisible(flag);
	}



}
