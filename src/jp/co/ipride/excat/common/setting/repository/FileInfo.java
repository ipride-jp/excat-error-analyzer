package jp.co.ipride.excat.common.setting.repository;

/**
 * jar or zip‚É‚ ‚éƒtƒ@ƒCƒ‹‚ðŠi”[‚·‚é
 * @author tu-ipride
 * @version 3.0
 * @date 2009/9/23
 *
 */
public class FileInfo {

	public final static int CLASS =0;
	public final static int SOURCE =1;
	private String path = null;
	private int type = 0;
	public Object contents = null;   //source:String, class: JavaClass.

	public FileInfo(int type){
		this.type=type;
	}

	public String getPath() {
		return path;
	}


	public void setPath(String path) {
		this.path = path;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public Object getContents() {
		return contents;
	}
	public void setContents(Object contents) {
		this.contents = contents;
	}

	public void clear(){
		path=null;
		contents=null;
	}

}
