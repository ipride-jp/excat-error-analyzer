package jp.co.ipride.excat.configeditor;

import jp.co.ipride.excat.common.clipboard.ExcatClipboard;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

/**
 * this is a proxy of TextViewer.
 * @author tu-ipride
 * @since 2009/10/3
 * @version 3.0
 */
public class ExcatTextViewer {

	private TextViewer textViewer = null;

	public ExcatTextViewer(Composite composite, int witdth, int span){
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
        addClipboardListener();
	}

	private void addClipboardListener(){
		textViewer.getTextWidget().addFocusListener(new FocusListener(){
			public void focusGained(FocusEvent focusevent) {
				ExcatClipboard.setPasteCandidate(textViewer);
			}

			public void focusLost(FocusEvent focusevent) {
			}
	    });
		textViewer.addPostSelectionChangedListener(new ISelectionChangedListener(){
			public void selectionChanged(
					SelectionChangedEvent selectionchangedevent) {
				String data = textViewer.getTextWidget().getSelectionText();
				if (!"".equals(data)){
					ExcatClipboard.setCopyCandidate(data);
				}
			}

		});

	}

	public void setText(String text){
		textViewer.getDocument().set(text);
	}

	public String getText(){
		return textViewer.getDocument().get();
	}

	public void addKeyListener(KeyListener keyListener){
		textViewer.getControl().addKeyListener(keyListener);
	}

	public void addTextListener(ITextListener textListener){
		textViewer.addTextListener(textListener);
	}

	public void addFocusListener(FocusListener focusListener){
		textViewer.getControl().addFocusListener(focusListener);
	}

	public char getChar(int index) throws BadLocationException{
		return textViewer.getDocument().getChar(index);
	}

	public void setBackground(Color color){
		textViewer.getTextWidget().setBackground(color);
	}

	public TextViewer getTextViewer(){
		return textViewer;
	}

	public void setEditable(boolean flag){
		textViewer.setEditable(flag);
	}

	public boolean hasFocus(){
		return textViewer.getControl().isFocusControl();
	}

	public void setEnabled(boolean flag){
		textViewer.getTextWidget().setEnabled(flag);
	}

	public boolean getEnabled(){
		return textViewer.getTextWidget().getEnabled();
	}
}
