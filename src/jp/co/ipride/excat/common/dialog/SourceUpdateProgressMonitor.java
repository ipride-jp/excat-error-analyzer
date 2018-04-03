package jp.co.ipride.excat.common.dialog;

import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IProgressMonitorWithBlocking;
import org.eclipse.core.runtime.IStatus;

/**
 * リポジトリ更新用のモニター
 * @author sai-ipride
 *
 */
public class SourceUpdateProgressMonitor implements IProgressMonitorWithBlocking{

	SourceUpdateDialog dialog = null;
	IProgressMonitor superMonitor = null;
	boolean returnFlag = false;

	public SourceUpdateProgressMonitor(SourceUpdateDialog dialog, IProgressMonitor superMonitor) {
		this.dialog = dialog;
		this.superMonitor = superMonitor;
	}


	public boolean updatingConfirm(final String msg) {
		dialog.getDisplay().syncExec(new Runnable() {
			public void run() {
				returnFlag = ExcatMessageUtilty.showConfirmDialogBox(dialog.getShell(), msg);
			}
		});
		return returnFlag;
	}

	public void clearBlocked() {
		if (superMonitor instanceof IProgressMonitorWithBlocking)
			((IProgressMonitorWithBlocking) superMonitor)
					.clearBlocked();
	}

	public void setBlocked(IStatus istatus) {
		if (superMonitor instanceof IProgressMonitorWithBlocking)
			((IProgressMonitorWithBlocking) superMonitor)
					.setBlocked(istatus);
	}

	public void beginTask(String s, int i) {
		superMonitor.beginTask(s, i);
	}

	public void done() {
		superMonitor.done();
	}

	public void internalWorked(double d) {
		superMonitor.internalWorked(d);
	}

	public boolean isCanceled() {
		return superMonitor.isCanceled();
	}

	public void setCanceled(boolean flag) {
		superMonitor.setCanceled(flag);
	}

	public void setTaskName(String s) {
		superMonitor.setTaskName(s);
	}

	public void subTask(String s) {
		dialog.updatingMsgNotified(s);
	}

	public void worked(int i) {
		superMonitor.worked(i);
	}
}
