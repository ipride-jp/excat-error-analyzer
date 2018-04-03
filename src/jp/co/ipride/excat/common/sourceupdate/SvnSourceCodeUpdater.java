package jp.co.ipride.excat.common.sourceupdate;

import java.io.File;

import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.dialog.SourceUpdateProgressMonitor;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.setting.SourceRepositorySetting;
import jp.co.ipride.excat.common.utility.HelperFunc;
import jp.co.ipride.excat.common.setting.repository.RepositoryFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ProgressMonitorWrapper;
import org.tmatesoft.svn.core.SVNCancelException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.internal.io.dav.DAVRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.fs.FSRepositoryFactory;
import org.tmatesoft.svn.core.internal.io.svn.SVNRepositoryFactoryImpl;
import org.tmatesoft.svn.core.wc.ISVNEventHandler;
import org.tmatesoft.svn.core.wc.ISVNOptions;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNEvent;
import org.tmatesoft.svn.core.wc.SVNEventAction;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNStatus;
import org.tmatesoft.svn.core.wc.SVNStatusClient;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

/**
 * svnからソースを取得する処理
 * @author tu-ipride
 * @date 2009/11/20 updated for cancelable
 *
 */
public class SvnSourceCodeUpdater implements SourceCodeUpdater,
		ISVNEventHandler {
	private SVNClientManager clientManager;
	private String name;
	private String password;
	private String repositoryUrl;
	private String workingCopyFolderPath;
	//private SourceCodeUpdateListener listener = null;
	private IProgressMonitor monitor = null;
	private SourceUpdateProgressMonitor monitorWrapped = null;

	/**
	 * construct
	 * @param name
	 * @param password
	 * @param repositoryUrl
	 * @param workingCopyFolderPath
	 */
//	public SvnSourceCodeUpdater(String name, String password,
//			String repositoryUrl, String workingCopyFolderPath) {
//		this.name = name;
//		this.password = password;
//		this.repositoryUrl = repositoryUrl;
//		this.workingCopyFolderPath = workingCopyFolderPath;
	public SvnSourceCodeUpdater(){
		SourceRepositorySetting setting = SettingManager.getSetting()
		.getSourceRepositorySetting();

		this.name = setting.getAccount();
		this.password = setting.getPassword();
		this.repositoryUrl = setting.getSourceRepositoryUrl();
		this.workingCopyFolderPath = setting.getWorkingCopyFolderPath();
	}

	/**
	 * will be called by the dialog.
	 */
	public void update() throws SouceCodeUpdateException {
		String msg;
		initClientManager();

		try {
			//1. download file from svn.
			msg = Message.get("SvnSourceCodeUpdater.Update_start");
			showMessage(msg);
			if (checkSVNLocal() && monitorWrapped.updatingConfirm(Message.get("SvnSourceCodeUpdater.ConfirmUpdate"))){
				if (isRepositoryChanged()) {
					svnCheckOut();
				} else {
					svnUpdate();
				}
			}
			msg = Message.get("SvnSourceCodeUpdater.Update_completed");
			showMessage(msg);

		} catch (SVNException e) {
			if (e instanceof SVNCancelException) {
				// キャンセルされた場合
				showMessage("Cancelled!");
				try {
					clientManager.getWCClient().doCleanup(new File(workingCopyFolderPath));
					clientManager.shutdownConnections(true);
				} catch (SVNException exc) {
					HelperFunc.logException(exc);
				}
				return;
			} else {
				msg = Message.get("SvnSourceCodeUpdater.FailedtoGetSource");
				showMessage(msg);
				HelperFunc.logException(e);

				//2.update repository.
				updateRepository();
				return;
			}
		}
		//2.update repository.
		updateRepository();

	}

	private void updateRepository() {
		//update repository.
		String msg;
		msg = Message.get("Repository.Update.Start");
		showMessage(msg);
		RepositoryFactory.createRepository(monitor);
		msg = Message.get("Repository.Update.Finish");
		showMessage(msg);
	}

	private void showMessage(String msg) {
		monitor.subTask(msg);
		try {
			Thread.sleep(50);
		} catch (InterruptedException e) {
		}
	}

	private boolean isRepositoryChanged() {
		SVNStatusClient client = clientManager.getStatusClient();
		SVNStatus status = null;
		try {
			status = client.doStatus(new File(workingCopyFolderPath), false);
		} catch (SVNException e) {
		}

		if (status == null) {
			return true;
		}
		return !removeLastSlash(repositoryUrl).equals(status.getURL().toString());
	}

	private String removeLastSlash(String string) {
		if (string == null || "".equals(string)) {
			return string;
		}

		if (string.charAt(string.length() - 1) == '/') {
			return string.substring(0, string.length() - 1);
		}
		return string;
	}

	private void initClientManager() {
		setupLibrary();
		ISVNOptions options = SVNWCUtil.createDefaultOptions(true);
		clientManager = SVNClientManager.newInstance(options, name, password);
	}

	private void svnCheckOut() throws SVNException {
		File workingCopyFolder = new File(workingCopyFolderPath);
		if (workingCopyFolder.exists()) {
			if (monitorWrapped.updatingConfirm(Message.get("SvnSourceCodeUpdater.ConfirmDelete"))){
				deleteFolder(workingCopyFolder);
				workingCopyFolder.mkdirs();
			}
		}

		SVNUpdateClient client = clientManager.getUpdateClient();
		client.setEventHandler(this);
		client.doCheckout(SVNURL.parseURIDecoded(repositoryUrl),
				workingCopyFolder, SVNRevision.HEAD, SVNRevision.HEAD, true);
	}

	private void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				deleteFolder(file);
			}

			if (file.isFile()) {
				file.delete();
			}
		}
		folder.delete();
	}

	private void svnUpdate() throws SVNException {
		SVNUpdateClient client = clientManager.getUpdateClient();
		client.setEventHandler(this);
		client.doUpdate(new File(workingCopyFolderPath), SVNRevision.HEAD,true);
	}

	private void setupLibrary() {
		// 「http」と「https」の対応
		DAVRepositoryFactory.setup();

		// 「svn」と「svn+xxx」の対応
		SVNRepositoryFactoryImpl.setup();

		// 「file」の対応
		FSRepositoryFactory.setup();
	}

	public void handleEvent(SVNEvent event, double arg1) throws SVNException {
		if (event.getAction().getID() == SVNEventAction.UPDATE_NONE.getID()) {
			return;
		}

		// 更新が終わる場合、バージョン番号を通知する。
		if (event.getAction().getID() == SVNEventAction.UPDATE_COMPLETED
				.getID()) {
			showMessage(Message
					.get("SvnSourceCodeUpdater.Revision")
					+ ": " + event.getRevision());
		} else {
			String actionName = null;
			try {
				actionName = Message.get("SvnSourceCodeUpdater."
						+ HelperFunc.ucFirst(event.getAction().toString()));
			} catch (Exception e) {
			}
			if (actionName == null) {
				actionName = event.getAction().toString();
			}

			String msg = actionName + "    " + event.getPath();
			showMessage(msg);
		}
	}

	public void checkCancelled() throws SVNCancelException {
		if (monitor.isCanceled()) {
			throw new SVNCancelException();
		}
	}

	private boolean checkSVNLocal(){
		// ソースをチェックアウトまたは更新することを試す。
		SourceRepositorySetting setting = SettingManager.getSetting()
				.getSourceRepositorySetting();

		//error check
		String url = setting.getSourceRepositoryUrl();
		if(url == null || url.trim().length() == 0){
			String msg = Message.get("SvnSourceCodeUpdater.NoRepositoryUrl");
			showMessage(msg);
			return false;
		}

		String local = setting.getWorkingCopyFolderPath();
		if(local == null || local.trim().length() == 0){
			String msg = Message.get("SvnSourceCodeUpdater.NoLocalFolder");
			showMessage(msg);
			return false;
		}
		File workingCopyFolder = new File(local);
		if(workingCopyFolder.exists()){
			if(!workingCopyFolder.isDirectory()){
				String msg = Message.get("SvnSourceCodeUpdater.NotDirectory");
				showMessage(msg);
				return false;
			}else if(!workingCopyFolder.canWrite()){
				String msg = Message.get("SvnSourceCodeUpdater.ReadOnly");
				showMessage(msg);
				return false;
			}
		}else{
			if(!workingCopyFolder.mkdirs()){
				String msg = Message.get("SvnSourceCodeUpdater.MakeDirsFailed");
				showMessage(msg);
				return false;
			}
		}
		this.name = setting.getAccount();
		this.password = setting.getPassword();
		this.repositoryUrl = setting.getSourceRepositoryUrl();
		this.workingCopyFolderPath = setting.getWorkingCopyFolderPath();
		return true;
	}

	public void setMonitor(IProgressMonitor monitor) {
		this.monitor = monitor;
		this.monitorWrapped = (SourceUpdateProgressMonitor)((ProgressMonitorWrapper)monitor).getWrappedProgressMonitor();
	}
}