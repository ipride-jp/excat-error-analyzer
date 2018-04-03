package jp.co.ipride;

/**
 * for license error.
 * @author tu-ipride
 * @date 2009/10/24
 */
public class ExcatLicenseException extends Exception {
	private static final long serialVersionUID = 1L;

	private String path = null;

	public ExcatLicenseException(){
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
