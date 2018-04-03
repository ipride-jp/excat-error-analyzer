package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

/**
 * インポートクラスの情報
 * @author jiang
 *
 */
public class ImportClassInfo {

	/**
	 * import class name
	 */
	private String name = null;
	
	/**
	 * whether this import declaration is an on-demand or a single-type import
	 */
	private boolean isOnDemand = false;

	public boolean isOnDemand() {
		return isOnDemand;
	}

	public void setOnDemand(boolean isOnDemand) {
		this.isOnDemand = isOnDemand;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
