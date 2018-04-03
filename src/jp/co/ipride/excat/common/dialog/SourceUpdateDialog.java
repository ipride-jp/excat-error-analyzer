/*
 * Error Analyzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common.dialog;

import java.lang.reflect.InvocationTargetException;

import jp.co.ipride.excat.common.ApplicationResource;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.sourceupdate.SourceCodeUpdateListener;
import jp.co.ipride.excat.common.sourceupdate.SourceCodeUpdater;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;
import jp.co.ipride.excat.common.utility.Utility;
import jp.co.ipride.excat.configeditor.util.ViewerUtil;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;

/**
 * SVN更新からリポジトリ更新にする。
 * @author tu-ipride
 * @date 2009/10/17 update
 * @date 2009/11/20 updated for cancelable
 */
public class SourceUpdateDialog extends ProgressMonitorDialog implements SourceCodeUpdateListener{

	private List msgList;

	private SourceCodeUpdater sourceCodeUpdater;

	private Button closeBtn;
	private Button cancelBtn;

	private String title;

	private IProgressMonitor wrapperedMonitor;

	/**
	 * construct
	 * @param parentShell
	 * @param sourceCodeUpdater
	 */
	public SourceUpdateDialog(Shell parentShell, String title,
			SourceCodeUpdater sourceCodeUpdater) {
		super(parentShell);
		this.title = title;
		this.sourceCodeUpdater = sourceCodeUpdater;
	}

	/**
	 * create layout of this dialog.
	 */
	protected Control createContents(Composite parent) {
		getShell().setText(title);
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(1, false);
		layout.marginTop = ViewerUtil.getMarginHeight(composite);
		layout.marginBottom = ViewerUtil.getMarginHeight(composite);
		layout.marginLeft = ViewerUtil.getMarginWidth(composite);
		layout.marginRight = ViewerUtil.getMarginWidth(composite);
		composite.setLayout(layout);

		GridData data = new GridData();
		data.grabExcessHorizontalSpace = true;
		data.grabExcessVerticalSpace = true;
		data.widthHint = 500;
		data.heightHint = 250;
		composite.setLayoutData(data);

		msgList = new List(composite, SWT.SINGLE | SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
		msgList.setLayoutData(new GridData(GridData.FILL_BOTH));

		createLowerPart(composite);

		closeBtn.setFocus();

		return composite;
	}

	private void createLowerPart(Composite composite) {
		Composite buttonForm = new Composite(composite, SWT.NONE);
		GridLayout buttonFormlayout = new GridLayout(2, true);
		buttonForm.setLayout(buttonFormlayout);
		GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_END);
		buttonForm.setLayoutData(gd);

		closeBtn = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Dialog.Message.OK"),
				Utility.BUTTON_WIDTH, 1);

		closeBtn.setEnabled(false);

		closeBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				okPressed();
			}
		});

		cancelBtn = Utility.createButton(buttonForm, SWT.PUSH,
				ApplicationResource.getResource("Search.Dialog.Cancel"),
				Utility.BUTTON_WIDTH, 1);

		cancelBtn.setEnabled(true);

		cancelBtn.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent arg0) {
				setCanceled();
			}
		});
	}

	public boolean close() {

		return super.close();
	}

	public void openDialog() {
		// キャンセルできるように変更した。11/20
		try {
			run(true, true, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
						throws InvocationTargetException, InterruptedException {
					try {
						sourceCodeUpdater.setMonitor(monitor);
						sourceCodeUpdater.update();
					} catch (Exception e) {
						HelperFunc.logException(e);
					}
				}

			});
			closeBtn.setEnabled(true);
			cancelBtn.setEnabled(false);
		} catch (InvocationTargetException e) {
			HelperFunc.logException(e);
		} catch (InterruptedException e) {
			HelperFunc.logException(e);
		}
	}

	public void updatingMsgNotified(String msg) {
		msgList.add(msg);
		msgList.select(msgList.getItemCount() - 1);
		msgList.showSelection();
		getParentShell().getDisplay().readAndDispatch();
	}

	public void updateCloseButton(boolean flag) {
		closeBtn.setEnabled(flag);
		closeBtn.setFocus();
	}

	public boolean updatingConfirm(String msg) {
		boolean ret = ExcatMessageUtilty.showConfirmDialogBox(getParentShell(),
				msg);

		return ret;
	}

	public void setCanceled() {
		if (ExcatMessageUtilty.showConfirmDialogBox(getShell(), Message.get("Repository.Cancel.Confirm"))) {
			getProgressMonitor().setCanceled(true);
		}
	}

	protected void configureShell(final Shell shell) {
		super.configureShell(shell);
		shell.setCursor(new Cursor(null, SWT.CURSOR_ARROW));
	}

	protected void finishedRun() {
		decrementNestingDepth();
	}

	public IProgressMonitor getProgressMonitor() {
		if (wrapperedMonitor == null)
			createWrapperedMonitor();
		return wrapperedMonitor;
	}

	public void createWrapperedMonitor() {
		wrapperedMonitor = new SourceUpdateProgressMonitor(this, SourceUpdateDialog.super.getProgressMonitor());
	}

	public Display getDisplay() {
		return getParentShell().getDisplay();
	}
}