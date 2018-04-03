package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.excat.common.ApplicationResource;

/**
 * リポジトリ検索の結果を格納するBeanクラス。
 *
 * @author saisk
 * @version 3.0
 * @date 2009/10/30
 */
public class MatchedFileUnit {
	private String path = null;
	private List<Match> matchList = new ArrayList<Match>();

	public MatchedFileUnit(String path) {
		this.path = path;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public List<Match> getMatchList() {
		return matchList;
	}

	public void setMatchList(List<Match> list) {
		this.matchList = list;
	}

	public void addMatch(Match match) {
		this.matchList.add(match);
	}

	public String getDisplayedPath() {
		return this.path
				+ String.format(ApplicationResource
						.getResource("SearchView.Tab.Result"), this.matchList
						.size());
	}
}
