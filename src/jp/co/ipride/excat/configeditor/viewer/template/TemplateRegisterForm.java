package jp.co.ipride.excat.configeditor.viewer.template;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.model.ConfigContant;
import jp.co.ipride.excat.configeditor.model.ConfigModel;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;
import jp.co.ipride.excat.configeditor.viewer.template.table.TemplateTableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

public class TemplateRegisterForm {

	private SashForm appWindow;
	private CTabFolder tabFolder;
	private Composite form;

	private TemplateTableViewer templateTableViewer;
	private Button deleteButton;
	private Button addButton;

	/**
	 * コンストラクト
	 * @param appWindow
	 * @param tabFolder
	 */
	public TemplateRegisterForm(SashForm appWindow, CTabFolder tabFolder){
		this.appWindow=appWindow;
		this.tabFolder=tabFolder;
		CTabItem tabItem = new CTabItem(tabFolder, SWT.NULL);

		//add by v3
		tabItem.setData(ConfigContant.Tree_Item_Template_Register);

		form = new Composite(tabFolder, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginTop = ViewerUtil.getMarginHeight(appWindow);
		layout.marginBottom = ViewerUtil.getMarginHeight(appWindow);
		layout.marginLeft = ViewerUtil.getMarginWidth(appWindow);
		layout.marginRight = ViewerUtil.getMarginWidth(appWindow);
		layout.numColumns=1;
		form.setLayout(layout);
		tabItem.setControl(form);
		createLayout();
		addListeners();
	}

	private void createLayout(){
		Label templateLabel = new Label(form, SWT.NONE);
		templateLabel.setText(
				ApplicationResource.getResource("Tab.Template.Title.Text"));

		templateTableViewer = new TemplateTableViewer(form);

		Composite buttonForm = new Composite(form,SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout();
		buttonFormlayout.numColumns=2;
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonForm.setLayoutData(gd);

		addButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Button.Add.Text"),
				Utility.BUTTON_WIDTH,1);

		deleteButton = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Tab.Button.Delete.Text"),
				Utility.BUTTON_WIDTH,1);
	}

	private void addListeners(){

		addButton.addSelectionListener(new SelectionAdapter() {
 			public void widgetSelected(SelectionEvent e) {
 				templateTableViewer.addNewTemplate();
			}
		});
		deleteButton.addSelectionListener(new SelectionAdapter() {
 			public void widgetSelected(SelectionEvent e) {
 				templateTableViewer.deleteTemplate();
			}
		});
	}

	public boolean checkItems(){
		return templateTableViewer.checkItems();
	}
}

