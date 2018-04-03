/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common.dialog;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

/**
 * ÉwÉãÉvâÊñ 
 *
 * @author ê∑ê\
 * @since 2006/12/4 èCê≥ ìj 2006/12/10
 */
public class AboutDialog extends Dialog {

	private static Image imgExcatIcon;

	static{
		imgExcatIcon = new Image(Display.getDefault(), MainViewer.class
				.getResourceAsStream(IconFilePathConstant.EXCAT_TM));
	}
	/**
	 * @param parentShell
	 */
	public AboutDialog(Shell parentShell) {
		super(parentShell);

	}

	/**
	 * Creates the dialog's contents
	 *
	 * @param parent
	 *            the parent composite
	 * @return Control
	 */
	protected Control createContents(Composite parent) {
		getShell().setText(
				ApplicationResource.getResource("AboutDialog.ShellText"));
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		composite.setLayout(layout);
		createUpperPart(composite);
		addSpaceLine(composite, 20);
		createLowerPart(composite);
		return composite;
	}

	/**
	 * This method initializes composite
	 *
	 */
	private void createUpperPart(Composite parent) {
		GridLayout layout;
		Composite composite = new Composite(parent, SWT.BORDER);
		layout = new GridLayout(2, false);
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		composite.setLayout(layout);

		//ÉçÉS
		Composite logoPlate = new Composite(composite, SWT.NONE);
		layout = new GridLayout(1, false);
		layout.marginTop = ViewerUtil.getMarginHeight(logoPlate);
		layout.marginBottom = ViewerUtil.getMarginHeight(logoPlate);
		layout.marginLeft = ViewerUtil.getMarginWidth(logoPlate);
		layout.marginRight = ViewerUtil.getMarginWidth(logoPlate);
		logoPlate.setLayout(layout);
		logoPlate.setBounds(new Rectangle(0, 0, 530, 110));
		Label logo = new Label(logoPlate, SWT.NONE);
		logo.setImage(imgExcatIcon);

		//ÉcÅ[ÉãèÓïÒ
		Composite toolPlate = new Composite(composite, SWT.NONE);
		layout = new GridLayout(1, false);
		layout.marginTop = ViewerUtil.getMarginHeight(toolPlate);
		layout.marginBottom = ViewerUtil.getMarginHeight(toolPlate);
		layout.marginLeft = ViewerUtil.getMarginWidth(toolPlate);
		layout.marginRight = ViewerUtil.getMarginWidth(toolPlate);
		toolPlate.setLayout(layout);
		GridData gridData = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
		gridData.widthHint = 400;
		toolPlate.setLayoutData(gridData);
		Label toolName = new Label(toolPlate, SWT.NONE);
		toolName.setText(ApplicationResource.getResource("AboutDialog.Title"));
		Label versin = new Label(toolPlate, SWT.NONE);
		versin.setText(ApplicationResource.getResource("AboutDialog.Version"));
		Label copyright = new Label(toolPlate, SWT.NONE);
		copyright.setText(ApplicationResource.getResource("AboutDialog.copyright"));

	}

	private void addSpaceLine(Composite composite,int spaceHeight){
		Composite spaceComposite = new Composite(composite, SWT.NONE);
        GridData spaceGridData = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
        spaceGridData.heightHint = spaceHeight;
        spaceComposite.setLayoutData(spaceGridData);
	}

	/**
	 * This method initializes composite1
	 *
	 */
	private void createLowerPart(Composite parent) {
		Composite infoPlate = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = ViewerUtil.getMarginHeight(infoPlate);
		layout.marginBottom = ViewerUtil.getMarginHeight(infoPlate);
		layout.marginLeft = ViewerUtil.getMarginWidth(infoPlate);
		layout.marginRight = ViewerUtil.getMarginWidth(infoPlate);
		infoPlate.setLayout(layout);

		Label company = new Label(infoPlate, SWT.NONE);
		company.setText(ApplicationResource.getResource("AboutDialog.iPride"));
		Label homepage = new Label(infoPlate, SWT.NONE);
		homepage.setText(ApplicationResource.getResource("AboutDialog.homepage"));
		Label mail = new Label(infoPlate, SWT.NONE);
		mail.setText(ApplicationResource.getResource("AboutDialog.email"));
		addSpaceLine(infoPlate,10);
	}


}