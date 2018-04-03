package jp.co.ipride.excat.common.dialog;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * メッセージ画面
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/17
 */
public class ExcatMessageDialog extends Dialog{

	private static Image imgExcatIcon;

	private String message;

	private Button okBtn;

	static{
		imgExcatIcon = new Image(Display.getDefault(), MainViewer.class
				.getResourceAsStream(IconFilePathConstant.EXCAT_TM));
	}

	public ExcatMessageDialog(Shell parentShell, String message) {
		super(parentShell);
		this.message = message;
	}

	protected Control createContents(Composite parent) {
		getShell().setText(
				ApplicationResource.getResource("Dialog.Message.Title"));
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		composite.setLayout(layout);
		createUpperPart(composite);
		createHorizotalLine(composite);
		createLowerPart(composite);
		okBtn.setFocus();
		return composite;
	}

	/**
	 * This is the composite of message
	 */
	private void createUpperPart(Composite parent) {
		GridLayout layout;
		Composite composite = new Composite(parent, SWT.NONE);
		layout = new GridLayout(2, false);
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		composite.setLayout(layout);

		//ロゴ
		Composite logoPlate = new Composite(composite, SWT.NONE);
		layout = new GridLayout(1, false);
		layout.marginTop = ViewerUtil.getMarginHeight(logoPlate);
		layout.marginBottom = ViewerUtil.getMarginHeight(logoPlate);
		layout.marginLeft = ViewerUtil.getMarginWidth(logoPlate);
		layout.marginRight = ViewerUtil.getMarginWidth(logoPlate);
		logoPlate.setLayout(layout);
		logoPlate.setBounds(new Rectangle(0, 0, 530, 55));
		Label logo = new Label(logoPlate, SWT.NONE);
		logo.setImage(imgExcatIcon);

		//メッセージ情報
		Composite msgPlate = new Composite(composite, SWT.NONE);
		layout = new GridLayout(1, false);
		layout.marginTop = ViewerUtil.getMarginHeight(msgPlate);
		layout.marginBottom = ViewerUtil.getMarginHeight(msgPlate);
		layout.marginLeft = ViewerUtil.getMarginWidth(msgPlate);
		layout.marginRight = ViewerUtil.getMarginWidth(msgPlate);
		msgPlate.setLayout(layout);
		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.widthHint = 400;
		gridData.heightHint = 70;
		msgPlate.setLayoutData(gridData);
		Text messageText = new Text(msgPlate, SWT.MULTI | SWT.WRAP);
		messageText.setLayoutData(new GridData(GridData.FILL_BOTH));
		messageText.setBackground(getShell().getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));
		messageText.setText(message);
		messageText.setEditable(false);
	}

	/**
	 * 分割線を作成
	 * @param c
	 * @return
	 */
	private void createHorizotalLine(Composite composite) {
		Label line = new Label(composite, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		line.setLayoutData(data);
	}

	private void createLowerPart(Composite composite){
		Composite buttonForm = new Composite(composite,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonForm.setLayoutData(gd);

		okBtn = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Dialog.Message.OK"),
				Utility.BUTTON_WIDTH,1);
		okBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				okPressed();
			}
		});
	}

}
