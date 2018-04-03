package jp.co.ipride.excat.analyzer.viewer.threadmap;

import java.util.ArrayList;
import java.util.List;

/**
 * スレッドダウン２組以上に適用する。
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/6
 */
public class ThreadMapItemTypeB {

	public static int listSize = 10;

	// スレッド名
	private String threadName;

	//ファイルのパス
	private List<String> filePathList = null;

	// 状態
	private List<String> statusList = null;

	public ThreadMapItemTypeB(){
		filePathList = new ArrayList<String>();
		statusList = new ArrayList<String>();
		for (int i=0; i<listSize; i++){
			filePathList.add("");
			statusList.add("");
		}
	}

	public String getFilePath(int index) {
		return filePathList.get(index);
	}
	public void setFilePathList(int index, String filePath) {
		this.filePathList.set(index,filePath);
	}
	public String getThreadName() {
		return threadName;
	}
	public void setThreadName(String threadName) {
		this.threadName = threadName;
	}
	public String getStatus(int index) {
		return statusList.get(index);
	}
	public void setStatus(int index, String status) {
		this.statusList.set(index,status);
	}

}
