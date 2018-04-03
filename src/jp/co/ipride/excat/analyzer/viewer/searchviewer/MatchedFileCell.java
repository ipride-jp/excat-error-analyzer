package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import java.util.List;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.swt.widgets.TreeItem;

/**
 * リポジトリ検索結果セル
 * @author saisk
 *
 */
public class MatchedFileCell extends FileCell{

	private MainViewer mview;
	private List<Match> matchList;
	private String sourceName;

	public MatchedFileCell(MainViewer mview){
		super(mview);
		this.mview = mview;
	}

	public TreeItem selectItem(){
		try{
			mview.analyzerform.sourceViewerPlatform.showDelcareViewer(sourceName, matchList);
			return null;
		}catch(Exception e){
			ExcatMessageUtilty.showErrorMessage(mview.getShell(), e);
			HelperFunc.getLogger().error("FileCell", e);
			return null;
		}
	}

	public void setMatchList(List<Match> matchList) {
		this.matchList = matchList;
	}

	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}


}
