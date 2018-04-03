package jp.co.ipride.excat.analyzer.function.printer;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.Printer;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TreeItem;

public class TreeViewPrinter {
	private static final String FONT_FAMILY = "Serif";
	private static final int FONT_SIZE = 10;
	private static final String NODE_DATA_IS_LAST = "isLast";
	private static final int BEFORE_NODE_TEXT_SPACING = 40;
	private final static int LINE_HEIGHT = 85;
	private final static int INDENT_WIDTH = 80;
	private final static int SCALE = 4;

	private TreeItem rootNode;
	private Shell shell;
	private int leftMargin;
	// private int rightMargin;
	private int topMargin;
	private int bottomMargin;
	private Printer printer = null;
	private GC gc = null;
	private Font printFont = null;
	private boolean isPrintJobStarted = false;
	private HashMap<String,Image> imageCache = new HashMap<String,Image>();
	private Image plusIcon;
	private Image minusIcon;
	private int plusIconHeight;
	private int plusIconWidth;
	private int printFontHeight;
	private int pageHeight;
	private PrinterData printerData;

	public TreeViewPrinter(TreeItem treeRoot, Shell shell) {
		rootNode = treeRoot;
		this.shell = shell;
	}

	public void print() {
		try {
			if (prepare()) {
				doPrint();
			}
		} catch (Exception e) {
			HelperFunc.logException(e);
			ExcatMessageUtilty.showMessage(
					this.shell,
					Message.get("Print.Failed"));
		} finally {
			dispose();
		}
	}

	private void dispose() {
		if (isPrintJobStarted) {
			printer.endJob();
		}

		if (gc != null) {
			gc.dispose();
		}

		if (printFont != null) {
			printFont.dispose();
		}
	}

	/**
	 * @throws PrintException
	 * @throws IOException
	 */
	private void doPrint() throws PrintException, IOException {
		// ページごとで行の内容を生成
		List<List<LinePrintContent>> printContent = createPrintContent();

		// ページごとで印刷
		for (int i = 0; i < printContent.size(); i++) {
			if (!isPageInPrintScope(i + 1)) {
				continue;
			}

			if (!printer.startPage()) {
				throw new PrintException();
			}
			doPrintPage( printContent.get(i));
			printer.endPage();
		}
	}

	private boolean isPageInPrintScope(int pageIndex) {
		if (printerData.startPage == 0 || printerData.endPage == 0) {
			return true;
		}

		return pageIndex >= printerData.startPage
				&& pageIndex <= printerData.endPage;
	}

	private void doPrintPage(List<LinePrintContent> printContentInOnePage) throws IOException {
		for (int i = 0; i < printContentInOnePage.size(); i++) {
			doPrintLine((LinePrintContent) printContentInOnePage.get(i));
		}
	}

	private void doPrintLine(LinePrintContent printContentInOneLine)
			throws IOException {
		// ライン（横および縦）の出力
		gc.drawLine(printContentInOneLine.horLineStartPos.x,
				printContentInOneLine.horLineStartPos.y,
				printContentInOneLine.horLineEndPos.x,
				printContentInOneLine.horLineEndPos.y);
		for (int i = 0; i < printContentInOneLine.vertLinesStartPos.size(); i++) {
			Point startPos = ((Point) printContentInOneLine.vertLinesStartPos
					.get(i));
			Point endPos = ((Point) printContentInOneLine.vertLinesEndPos
					.get(i));
			gc.drawLine(startPos.x, startPos.y, endPos.x, endPos.y);
		}

		// プラスまたはマイナスアイコンの出力
		if (printContentInOneLine.plusMinusIcon != null) {
			drawImage(gc, printContentInOneLine.plusMinusIcon,
					printContentInOneLine.plusMinusIconPos.x,
					printContentInOneLine.plusMinusIconPos.y);
		}

		// ノードアイコンの出力
		drawImage(gc, printContentInOneLine.nodeIcon,
				printContentInOneLine.nodeIconPos.x,
				printContentInOneLine.nodeIconPos.y);

		// ノード文字列の出力
		gc.drawText(printContentInOneLine.nodeText,
				printContentInOneLine.nodeTextPos.x,
				printContentInOneLine.nodeTextPos.y);
	}

	private List<List<LinePrintContent>> createPrintContent() throws IOException {
		List<LinePrintContent> printContent = new ArrayList<LinePrintContent>();
		addPrintContentForNode(rootNode, printContent, 0, true);
		return dividePrintContentIntoPage(printContent);
	}

	private List<List<LinePrintContent>> dividePrintContentIntoPage(List<LinePrintContent> printContent) {
		int lineNumberInOnePage = pageHeight / LINE_HEIGHT;
		List<List<LinePrintContent>> result = new ArrayList<List<LinePrintContent>>();

		List<LinePrintContent> printContentInOnePage = new ArrayList<LinePrintContent>();
		int index = 0;
		int prePageIndex = 0;
		while (index < printContent.size()) {
			int pageIndex = index / lineNumberInOnePage;
			if (pageIndex != prePageIndex) {
				result.add(printContentInOnePage);
				printContentInOnePage = new ArrayList<LinePrintContent>();
				prePageIndex = pageIndex;
			}

			((LinePrintContent) printContent.get(index)).recalculatePos(pageIndex * pageHeight);
			printContentInOnePage.add(printContent.get(index));

			index++;
		}

		result.add(printContentInOnePage);
		return result;
	}

	private void addPrintContentForNode(TreeItem node, List<LinePrintContent> printContent,
			int depth, boolean isLast) throws IOException {
		LinePrintContent oneLinePrintContent = new LinePrintContent();
		TreeItem[] childNodes = node.getItems();

		// プラス、マイナスアイコンの取得
		if (childNodes.length > 0) {
			if (node.getExpanded()) {
				oneLinePrintContent.plusMinusIcon = minusIcon;
			} else {
				oneLinePrintContent.plusMinusIcon = plusIcon;
			}
		}

		int basePosX = depth * INDENT_WIDTH + leftMargin;
		int basePosY = topMargin + printContent.size() * LINE_HEIGHT;

		// プラス、マイナスアイコン位置の計算
		if (oneLinePrintContent.plusMinusIcon != null) {
			oneLinePrintContent.plusMinusIconPos = new Point(basePosX, basePosY
					+ (LINE_HEIGHT - plusIconHeight) / 2);
		}

		// 横および縦のラインの終始点位置の計算
		oneLinePrintContent.horLineStartPos = new Point(basePosX
				+ plusIconWidth / 2, basePosY + LINE_HEIGHT / 2);
		oneLinePrintContent.horLineEndPos = new Point(
				oneLinePrintContent.horLineStartPos.x + INDENT_WIDTH,
				oneLinePrintContent.horLineStartPos.y);

		node.setData(NODE_DATA_IS_LAST, Boolean.valueOf(isLast));

		TreeItem ancestorNode = node;
		int ancestorDepth = depth;
		while (ancestorDepth >= 0 && ancestorNode != null) {
			boolean isAncestorNodeLast = ((Boolean) ancestorNode
					.getData(NODE_DATA_IS_LAST)).booleanValue();

			if (node.getParentItem() != null) {
				Point vertLineStartPos = new Point(leftMargin + ancestorDepth
						* INDENT_WIDTH + plusIconWidth / 2, basePosY);

				Point vertLineEndPos = null;
				if (isAncestorNodeLast) {
					if (ancestorDepth == depth) {
						vertLineEndPos = new Point(vertLineStartPos.x,
								vertLineStartPos.y + LINE_HEIGHT / 2);
					}
				} else {
					vertLineEndPos = new Point(vertLineStartPos.x,
							vertLineStartPos.y + LINE_HEIGHT);
				}

				if (vertLineEndPos != null) {
					oneLinePrintContent.vertLinesStartPos.add(vertLineStartPos);
					oneLinePrintContent.vertLinesEndPos.add(vertLineEndPos);
				}
			}

			ancestorNode = ancestorNode.getParentItem();
			ancestorDepth--;
		}

		// ノードアイコンの取得
		oneLinePrintContent.nodeIcon = node.getImage();

		// ノードアイコン出力位置の計算
		int nodeIconWidth = oneLinePrintContent.nodeIcon.getBounds().width
				* SCALE;
		int nodeIconHeight = oneLinePrintContent.nodeIcon.getBounds().height
				* SCALE;
		oneLinePrintContent.nodeIconPos = new Point(
				oneLinePrintContent.horLineStartPos.x + INDENT_WIDTH
						- nodeIconWidth / 2, basePosY
						+ (LINE_HEIGHT - nodeIconHeight) / 2);

		// ノード文字列の取得
		oneLinePrintContent.nodeText = node.getText();

		// ノード文字列出力位置の取得
		oneLinePrintContent.nodeTextPos = new Point(basePosX + INDENT_WIDTH
				+ nodeIconWidth / 2 + BEFORE_NODE_TEXT_SPACING, basePosY
				+ (LINE_HEIGHT - printFontHeight) / 2);

		// リストに行の内容を追加
		printContent.add(oneLinePrintContent);

		depth++;
		if (node.getExpanded()) {
			for (int i = 0; i < childNodes.length; i++) {
				addPrintContentForNode(childNodes[i], printContent, depth,
						i == (childNodes.length - 1));
			}
		}
	}

	private boolean prepare() throws IOException {
		// 印刷ダイアログを表示する
		PrintDialog printDialog = new PrintDialog(shell);
		printerData = printDialog.open();
		if (printerData == null) {
			return false;
		}

		printer = new Printer(printerData);
		if (!printer.startJob("")) {
			return false;
		}

		isPrintJobStarted = true;
		gc = new GC(printer);

		// フォント
		printFont = new Font(printer, FONT_FAMILY, FONT_SIZE, SWT.NORMAL);
		gc.setFont(printFont);
		printFontHeight = gc.getFontMetrics().getHeight();

		// ラインスタイル
		gc.setLineStyle(SWT.LINE_DOT);

		// Marginの計算
		Rectangle clientArea = printer.getClientArea();
		Rectangle trim = printer.computeTrim(0, 0, 0, 0);
		Point dpi = printer.getDPI();
		leftMargin = dpi.x + trim.x;
		// rightMargin = clientArea.width - dpi.x + trim.x + trim.width;
		topMargin = dpi.y + trim.y;
		bottomMargin = clientArea.height - dpi.y + trim.y + trim.height;

		// ページ高さの計算
		pageHeight = (bottomMargin - topMargin);

		// ロードアイコン
		plusIcon = loadImage(IconFilePathConstant.TREE_PLUS);
		minusIcon = loadImage(IconFilePathConstant.TREE_MINUS);

		// アイコンサイズ
		plusIconHeight = plusIcon.getBounds().height * SCALE;
		plusIconWidth = plusIcon.getBounds().width * SCALE;

		return true;
	}

	private void drawImage(GC gc, Image image, int x, int y) throws IOException {
		int width = image.getBounds().width;
		int height = image.getBounds().height;
		gc.drawImage(image, 0, 0, width, height, x, y, width * SCALE, height
				* SCALE);
	}

	private Image loadImage(String iconFilePath) throws IOException {
		if (imageCache.containsKey(iconFilePath)) {
			return (Image) imageCache.get(iconFilePath);
		}

		InputStream stream = MainViewer.class.getResourceAsStream(iconFilePath);
		Image image = new Image(shell.getDisplay(), stream);

		imageCache.put(iconFilePath, image);
		return image;
	}
}