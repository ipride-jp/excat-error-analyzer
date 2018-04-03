package jp.co.ipride.excat.analyzer.viewer.searchviewer;

import java.util.ArrayList;
import java.util.List;

import jp.co.ipride.ExcatLicenseException;
import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;

/**
 * テキスト検索を検索し、結果を表示する。
 *
 * @author tu-ipride
 * @version 3.0
 * @date 2009/10/9
 */
public class TextSearchViewer extends SashForm {

	private MainViewer mview;

	private SearchTree searchTree;

	/**
	 * construct
	 *
	 * @param view
	 * @param parent
	 * @param style
	 */
	public TextSearchViewer(MainViewer view, Composite parent, int style) {
		super(parent, style);
		this.mview = view;
		searchTree = new SearchTree(mview, this, SWT.BORDER | SWT.V_SCROLL
				| SWT.H_SCROLL);
	}

	/**
	 * 検索を実施する。
	 *
	 * @param conditionUnit
	 * @throws ExcatLicenseException
	 */
	public void searchText(ConditionUnit conditionUnit)
			throws ExcatLicenseException {

		try {
			this.getShell().setCursor(new Cursor(null, SWT.CURSOR_WAIT));

			conditionUnit.parseRule();

			ConditionCell conditionCell = new ConditionCell();

			conditionCell.setConditionUnit(conditionUnit);

			TopCell topCell = new TopCell(conditionCell);

			conditionCell.setParent(topCell);

			List<FileCell> fileCellList = new ArrayList<FileCell>();

			conditionCell.setFileList(fileCellList);

			TextSearchEngine engine = new TextSearchEngine(conditionUnit);

			if (conditionUnit.getSearchType() == ConditionUnit.SEARCH_STACK
					|| conditionUnit.getSearchType() == ConditionUnit.SEARCH_OBJECT) {

				String path = mview.analyzerform.stackTree.getPath();

				if (path != null) {

					FileCell fileCell = new FileCell(mview);
					fileCell.setPath(path);
					List<MethodCell> methodList = null;
					methodList = engine.searchOneDumpData(path);
					if (methodList != null && methodList.size() > 0) {
						fileCell.setMethodList(methodList);
						for (MethodCell methodCell : methodList) {
							methodCell.setParent(fileCell);
						}
						fileCell.setParent(conditionCell);
						fileCellList.add(fileCell);
					}

					searchTree.inputData(topCell);

				} else {

					ExcatMessageUtilty.showMessage(mview.getShell(), Message
							.get("Dialog.Search.Stack.NoDocument.Text"));

				}

			} else if (conditionUnit.getSearchType() == ConditionUnit.SEARCH_FILE) {

				List<String> pathList = conditionUnit.getFileList();

				for (String path : pathList) {

					List<MethodCell> methodList;

					methodList = engine.searchOneDumpData(path);

					if (methodList != null && methodList.size() > 0) {
						FileCell fileCell = new FileCell(mview);
						fileCell.setPath(path);
						fileCell.setParent(conditionCell);
						fileCell.setMethodList(methodList);
						for (MethodCell methodCell : methodList) {
							methodCell.setParent(fileCell);
						}
						fileCellList.add(fileCell);
					}
				}

				searchTree.inputData(topCell);
			} else if (conditionUnit.getSearchType() == ConditionUnit.SEARCH_REPOSITORY) {

				List<MatchedFileUnit> matchList = engine
						.searchRepository(conditionUnit.getText());

				for (MatchedFileUnit match : matchList) {

					MatchedFileCell fileCell = new MatchedFileCell(mview);
					fileCell.setMatchList(match.getMatchList());
					fileCell.setSourceName(match.getPath());
					fileCell.setPath(match.getDisplayedPath());
					fileCell.setParent(conditionCell);
					fileCellList.add(fileCell);
				}
				searchTree.inputData(topCell);
			}
		} finally {
			this.getShell().setCursor(null);
		}
	}
}
