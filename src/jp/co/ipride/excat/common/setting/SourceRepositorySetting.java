package jp.co.ipride.excat.common.setting;

import java.io.Serializable;

import jp.co.ipride.excat.common.utility.HelperFunc;

/**
 * SVN“o˜^
 * @author tu-ipride
 * @version 2.0
 */
public class SourceRepositorySetting implements Serializable {
	private static final long serialVersionUID = 1L;
	private String sourceRepositoryUrl;
	private String account;
	private String password;
	private String workingCopyFolderPath;

	public String getSourceRepositoryUrl() {
		return sourceRepositoryUrl;
	}

	public void setSourceRepositoryUrl(String sourceRepositoryUrl) {
		this.sourceRepositoryUrl = sourceRepositoryUrl;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getWorkingCopyFolderPath() {
		return workingCopyFolderPath;
	}

	public void setWorkingCopyFolderPath(String workingCopyFolderPath) {
		this.workingCopyFolderPath = workingCopyFolderPath;
	}

	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}

		if (!(obj instanceof SourceRepositorySetting)) {
			return false;
		}

		SourceRepositorySetting other = (SourceRepositorySetting) obj;
		return HelperFunc.compareObject(sourceRepositoryUrl,
				other.sourceRepositoryUrl)
				&& HelperFunc.compareObject(account, other.account)
				&& HelperFunc.compareObject(password, other.password)
				&& HelperFunc.compareObject(workingCopyFolderPath,
						other.workingCopyFolderPath);
	}
}
