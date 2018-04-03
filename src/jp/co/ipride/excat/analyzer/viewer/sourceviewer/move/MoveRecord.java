package jp.co.ipride.excat.analyzer.viewer.sourceviewer.move;

/**
 * ÉÜÅ[ÉUÅ[ÇÃ1å¬ÇÃëJà⁄
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/26
 */
public class MoveRecord {

	private String classPath;

	private int startPos;

	private int endPos;

	public MoveRecord(String classPath, int startPos,int endPos){
		this.classPath = classPath;
		this.startPos = startPos;
		this.endPos = endPos;
	}

	public String getClassPath() {
		return classPath;
	}

	public void setClassPath(String classPath) {
		this.classPath = classPath;
	}

	public int getStartPos() {
		return startPos;
	}

	public void setStartPos(int startPos) {
		this.startPos = startPos;
	}

	public int getEndPos() {
		return endPos;
	}

	public void setEndPos(int endPos) {
		this.endPos = endPos;
	}


}
