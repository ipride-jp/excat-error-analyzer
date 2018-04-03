/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2007/10/05
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.analyzer.function.pdf;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import jp.co.ipride.excat.MainViewer;
import jp.co.ipride.excat.analyzer.common.DumpFileXmlConstant;
import jp.co.ipride.excat.common.Message;
import jp.co.ipride.excat.common.icons.IconFilePathConstant;
import jp.co.ipride.excat.common.setting.SettingManager;
import jp.co.ipride.excat.common.utility.ExcatMessageUtilty;
import jp.co.ipride.excat.common.utility.HelperFunc;

import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TreeItem;
import org.w3c.dom.Node;

import com.ibm.icu.util.StringTokenizer;
import com.lowagie.text.BadElementException;
import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;

/**
 * PDFへの出力用クラス
 *
 * @version 1.0 2007/10/05
 * @author 公 続亮
 */

public class PdfGenerator {

	/**
	 * グローバル変数の宣言
	 */
	private final static String INDENX_SPACE = "   "; // 親と子ノード間の文字列のインデント

	private final static String FONT_REGULAR = "KozMinPro-Regular"; // フォント

	private final static String FONT_LANGUGE = "UniJIS-UCS2-H"; // エンコーディング名

	private final static int INDENT = 20; // 線と線の横間隔

	private final static int LINE_LENGTH = 12; // 横線の長さ

	private final static int LINE_HEIGHT = 18; // 縦線の高さ(行の高さ)

	private final static int PLUS_AND_MINUS_LOCAL_X = 5; // "+"と"-"アイコンは線との相対X軸位置

	private final static int PLUS_AND_MINUS_LOCAL_Y = 5; // "+"と"-"アイコンは線との相対Y軸位置

	private final static int START_Y = 42; // 一番の線はY軸での数値

	private final static int START_X = 45; // 一番の線はX軸での数値

	private int intstartY = 42; // 各ノードはY軸での数値

	private int newPageFlag = 1; // 改ページのかどうかのフラグ

	private int intPageMaxLine = 41; // ページの最大行数

	private PdfWriter writer = null; // Pdfの書込器

	private Document document = null; // pdf出力用のドキュメント

	private BasicStroke bs = null; // BasicStrokeの宣言

	private Font ｆontJapan = null; // 文字のフォント(日本語)

	private Paragraph paragraph = null; // パラグラフの宣言

	private Graphics2D g2d = null; // グラフィクスの宣言

	private BaseFont bf = null; // フォント

	private Rectangle recSelected = null; // ページサイズを設定用の長方形

	private ArrayList<String> alLineSize = new ArrayList<String>(); // すべての行のサイズの格納

	private MainViewer appWindow;

	/**
	 * コンストラクタ
	 *
	 * @param tiRoot
	 *            PDF出力ルートノード
	 * @param appWindow
	 *            アプリケションウィンドウ
	 * @return なし
	 * @throws Exception
	 */
	public PdfGenerator(TreeItem tiRoot, MainViewer appWindow) throws Exception {
		this.appWindow = appWindow;

		// PDF保存先フォルダパスの取得
		String strPdfPath = getPdfOutPath(appWindow);
		// PDF保存先フォルダを選択しない場合、PDFファイルを作成しない。
		if (strPdfPath == null || "".equals(strPdfPath)) {
			return;
		}
		//ファイルの存在チェック
		File file = new File(strPdfPath);
		if(file.exists()){
			//ユーザに確認
			boolean ret = ExcatMessageUtilty.showConfirmDialogBox(
					appWindow.getShell(),
					Message.getMsgWithParam("File.exist",strPdfPath));

			if(!ret ){
				return;
			}
		}

		appWindow.getShell().setCursor(new Cursor(null, SWT.CURSOR_WAIT));
		// 文字列の日本語のフォントを描画する。
		ｆontJapan = setFont(PdfGenerator.FONT_REGULAR,
				PdfGenerator.FONT_LANGUGE, BaseFont.NOT_EMBEDDED);
		document = new Document(PageSize.A4);
		setPageSize(tiRoot);
		document = new Document(recSelected);
		try {
			writer = PdfWriter.getInstance(document, new FileOutputStream(
					strPdfPath));
		} catch (Exception ex) {
			// 選択したパスのフォルダは読み取り専用の場合、
			// または、ファイルが開いた場合、エラーメッセージダイアログを表示する。
			ExcatMessageUtilty.showMessage(
					appWindow.getShell(),
					Message.get("DirectoryDialog.Pdf.Path"));
			return;
		}
		// ドキュメントをオープンする。
		document.open();

		// キャンバスを取得する。
		PdfContentByte cb = writer.getDirectContent();
		g2d = cb
				.createGraphics(recSelected.getWidth(), recSelected.getHeight());
		bs = setLineStyle(0.5f);
		// 線のスタイルを設定する。
		g2d.setStroke(bs);
		outputPdfInit(tiRoot);
		g2d.dispose();
		document.add(paragraph);
		// ドキュメントをクローズする。
		document.close();
		// PDF出力完了の確認メッセージを表示する。
		ExcatMessageUtilty.showMessage(
				appWindow.getShell(),
				Message.get("Dialog.Pdf.OK.Text"));
	}

	/**
	 * 線の描画
	 *
	 * @param parent
	 *            ノード
	 * @param array
	 *            ノード状態
	 * @param intstartX
	 *            線のX軸数値
	 * @param intstartY
	 *            線のY軸数値
	 * @param i
	 *            ノード番号
	 * @return なし
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws DocumentException
	 */
	private void drawNodeLine(TreeItem parent, ArrayList<String> array, int intstartX,
			int intstartY, int i) {
		int intEndX = intstartX;
		int intEndY = intstartY + PdfGenerator.LINE_HEIGHT;
		// 一番下のノードの場合、"└"のような線を描画する。
		// 上記以外の場合、"├"のような線を描画する。
		if (i == parent.getItems().length - 1) {
			int intEndX2 = intstartX;
			int intEndY2 = intstartY + PdfGenerator.LINE_HEIGHT / 2;
			int intstartX3 = intstartX;
			int intstartY3 = intEndY2;
			int intEndX3 = intstartX + PdfGenerator.LINE_LENGTH;
			int intEndY3 = intstartY3;
			g2d.drawLine(intstartX, intstartY, intEndX2, intEndY2);
			g2d.drawLine(intstartX3, intstartY3, intEndX3, intEndY3);
		} else {
			int intstartX4 = intstartX;
			int intstartY4 = (intstartY + intEndY) / 2;
			int intEndX4 = intstartX + PdfGenerator.LINE_LENGTH;
			int intEndY4 = intstartY4;
			g2d.drawLine(intstartX, intstartY, intEndX, intEndY);
			g2d.drawLine(intstartX4, intstartY4, intEndX4, intEndY4);
		}
		if (array == null || array.size() == 0) {
			array.add("0");
		}
		// 該当ノードの前の一番線の以降の線"│"を描画する。
		int k = 1;
		for (int j = array.size() - 1; 0 <= j; j--) {
			if (array.get(j).equals("1")) {
				int intstartX5 = intstartX - k * PdfGenerator.INDENT;
				int intstartY5 = intstartY;
				int intEndX5 = intstartX5;
				int intEndY5 = intEndY;
				g2d.drawLine(intstartX5, intstartY5, intEndX5, intEndY5);
			}
			k++;
		}
	}

	/**
	 * "+"と"-"アイコンの描画
	 *
	 * @param parent
	 *            ノードアイテム
	 * @param intstartX
	 *            線のX軸数値
	 * @param intstartY
	 *            線のY軸数値
	 * @return なし
	 */
	private void drawPlusMinusIcon(TreeItem childTrItem, int intstartX,
			int intstartY) {
		Node targetNode = (Node) childTrItem.getData();

		if (targetNode.getChildNodes().getLength() != 0) {
			java.awt.Image imageManasuTasu = null;
			if (childTrItem.getExpanded()) {
				imageManasuTasu = Toolkit.getDefaultToolkit().getImage(
						MainViewer.class
								.getResource(IconFilePathConstant.TREE_MINUS));
				g2d.drawImage(imageManasuTasu, intstartX
						- PdfGenerator.PLUS_AND_MINUS_LOCAL_X, intstartY
						+ PdfGenerator.LINE_HEIGHT / 2
						- PdfGenerator.PLUS_AND_MINUS_LOCAL_Y, null);

			} else {
				imageManasuTasu = Toolkit.getDefaultToolkit().getImage(
						MainViewer.class
								.getResource(IconFilePathConstant.TREE_PLUS));
				g2d.drawImage(imageManasuTasu, intstartX
						- PdfGenerator.PLUS_AND_MINUS_LOCAL_X, intstartY
						+ PdfGenerator.LINE_HEIGHT / 2
						- PdfGenerator.PLUS_AND_MINUS_LOCAL_Y, null);
			}
		}
	}

	/**
	 * ツールノードのアイコンのパスの取得
	 *
	 * @param targetNode
	 *            ノード
	 * @return アイコンのパス
	 */
	private URL getIconUrl(Node targetNode) {
		URL url = null;
		// ノードはルートノードの場合、ダンプのアイコンのURLを返却する。
		if (DumpFileXmlConstant.NODE_DUMP.equalsIgnoreCase(targetNode
				.getNodeName())) {
			url = MainViewer.class.getResource(IconFilePathConstant.TREE_DUMP);
			return url;
		}
		// ノードはスタックトレースノードの場合、スタックトレースのURLを返却する。
		if (DumpFileXmlConstant.NODE_STACKTRACE.equalsIgnoreCase(targetNode
				.getNodeName())) {

			url = MainViewer.class
					.getResource(IconFilePathConstant.TREE_STACKTRACE);
			return url;
		}
		// ノードはメッソドノードの場合、メッソドのアイコンのURLを返却する。
		if (DumpFileXmlConstant.NODE_METHOD.equalsIgnoreCase(targetNode
				.getNodeName())) {
			url = MainViewer.class
					.getResource(IconFilePathConstant.TREE_METHOD);
			return url;
		}
		// ノードは変数ノードの場合、変数のアイコンのURLを返却する。
		if (DumpFileXmlConstant.NODE_VARIABLE.equalsIgnoreCase(targetNode
				.getNodeName())) {
			url = MainViewer.class
					.getResource(IconFilePathConstant.TREE_VARIABLE);
			return url;
		}
		// ノードはスーパークラスノードの場合、スーパークラスのアイコンのURLを返却する。
		if (DumpFileXmlConstant.NODE_SUPERCLASS.equalsIgnoreCase(targetNode
				.getNodeName())) {
			url = MainViewer.class
					.getResource(IconFilePathConstant.TREE_SUPERCLASS);
			return url;
		}
		// ノードは属性ノードの場合、属性のアイコンのURLを返却する。
		if (DumpFileXmlConstant.NODE_ATTRIBUTE.equalsIgnoreCase(targetNode
				.getNodeName())) {
			url = MainViewer.class
					.getResource(IconFilePathConstant.TREE_ATTRIBUTE);
			return url;
		}
		// ノードはパラメータノードの場合、パラメータのアイコンのURLを返却する。
		if (DumpFileXmlConstant.NODE_ARGUMENT.equalsIgnoreCase(targetNode
				.getNodeName())) {
			url = MainViewer.class
					.getResource(IconFilePathConstant.TREE_ARGUMENT);
			return url;
		}

         //ノードはthisの場合、thisのアイコンのURLを返却する。
		if (DumpFileXmlConstant.NODE_THIS.equalsIgnoreCase(targetNode
				.getNodeName())) {
			url = MainViewer.class
					.getResource(IconFilePathConstant.TREE_THIS);
			return url;
		}

		// ノードは項目ノードの場合、項目のアイコンのURLを返却する。
		if (DumpFileXmlConstant.NODE_ITEM.equalsIgnoreCase(targetNode
				.getNodeName())) {
			url = MainViewer.class.getResource(IconFilePathConstant.TREE_ITEM);
			return url;
		}
		//　 ノードはインスタンスノードの場合、インスタンスのアイコンのURLを返却する。
		if (DumpFileXmlConstant.NODE_INSTANCE.equalsIgnoreCase(targetNode
				.getNodeName())) {
			url = MainViewer.class.getResource(IconFilePathConstant.INSTANCE_ITEM);
			return url;
		}
		// ノードはモニターオブジェクトノードの場合、モニターオブジェクトのアイコンのURLを返却する。
		if (DumpFileXmlConstant.NODE_MONITOROBJECT.equalsIgnoreCase(targetNode
				.getNodeName())) {
			url = MainViewer.class.getResource(IconFilePathConstant.MONITOROBJECT_ITEM);
			return url;
		}

        //ノードはthisの場合、Monitor ObjectのアイコンのURLを返却する。
		if (DumpFileXmlConstant.NODE_MONITOROBJECT.equalsIgnoreCase(targetNode
				.getNodeName())) {
			url = MainViewer.class
					.getResource(IconFilePathConstant.MONITOROBJECT_ITEM);
			return url;
		}

        //ノードはthisの場合、InstanceのアイコンのURLを返却する。
		if (DumpFileXmlConstant.NODE_INSTANCE.equalsIgnoreCase(targetNode
				.getNodeName())) {
			url = MainViewer.class
					.getResource(IconFilePathConstant.INSTANCE_ITEM);
			return url;
		}
		return url;
	}

	/**
	 * すべてのノードの情報の設定
	 *
	 * @param parent
	 *            ノード
	 * @param strSpace
	 *            親と子ノードのインデント
	 * @param intstartX
	 *            線のX軸数値
	 * @return なし
	 * @throws DocumentException
	 */
	private void getLineDate(TreeItem parent, String strSpace, int intstartX)
			throws MalformedURLException, IOException, DocumentException {
		for (int i = 0; i < parent.getItems().length; i++) {
			String strIndenx = strSpace + PdfGenerator.INDENX_SPACE;
			Node targetNode = (Node) parent.getItem(i).getData();
			URL url = null;
			if (targetNode != null) {
				url = getIconUrl(targetNode);
			}
			if (url != null) {
				// PDFの行のサイズを格納する。
				saveLineSize(parent.getItem(i), intstartX);
			}
			if (parent.getItem(i).getExpanded()) {
				// 子ノードを操作する。
				getLineDate(parent.getItem(i), strIndenx
						+ PdfGenerator.INDENX_SPACE, intstartX
						+ PdfGenerator.INDENT);
			}
		}
	}

	/**
	 * 行の内容の最大サイズの取得
	 *
	 * @param なし
	 * @return なし
	 */
	private float getMaxLineSize() {
		float fMaxSize = (float) 0.0;
		String str = "";
		boolean bol = false;
		// 各ノードの最大サイズを昇順でソートし直す。
		for (int j = 0; j < alLineSize.size() - 1; j++) {
			bol = false;
			for (int i = 0; i < alLineSize.size() - 1; i++) {
				String str0 = (String) alLineSize.get(i);
				String str1 = (String) alLineSize.get(i + 1);
				if (Float.parseFloat(str0) > Float.parseFloat(str1)) {
					str = alLineSize.get(i).toString();
					alLineSize.set(i, alLineSize.get(i + 1));
					alLineSize.set(i + 1, str);
					bol = true;
				}
			}
			if (!bol) {
				break;
			}
		}
		// ノードの最大サイズにソートした配列の最後の元素を設定する。
		fMaxSize = Float.parseFloat(alLineSize.get(alLineSize.size() - 1)
				.toString());
		return fMaxSize;
	}

	/**
	 * DialogでPDFファイル生成パスの取得
	 *
	 * @param appWindow
	 *            アプレケションのウインドウ
	 * @return ファイル保存のパス
	 */
	private String getPdfOutPath(ApplicationWindow appWindow) {
		FileDialog dialog = new FileDialog(appWindow.getShell(), SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.pdf" });
		String currentPdfPath = SettingManager.getSetting().getCurrentPdfPath();
		if (currentPdfPath != null) {
			File file = new File(currentPdfPath);
			if (file.isFile()) {
				currentPdfPath = currentPdfPath.substring(0, currentPdfPath.indexOf(file.getName()));
			}
		}
		if (currentPdfPath != null) {
			dialog.setFilterPath(currentPdfPath);
		}
		String path = dialog.open();
		if (path == null) {
			return null;
		}
		SettingManager.getSetting().setCurrentPDFPath(path);
		return path;
	}

	/**
	 * PDFファイルへの出力
	 *
	 * @param parent
	 *            ノード
	 * @param strSpace
	 *            親と子ノードのインデント
	 * @param intstartX
	 *            線のX軸数値
	 * @param array
	 *            ノード状態
	 * @return なし
	 * @throws MalformedURLException
	 * @throws IOException
	 * @throws DocumentException
	 */
	private void outputPdfContents(TreeItem parent, String strSpace,
			int intstartX, ArrayList<String> array) throws MalformedURLException,
			IOException, DocumentException {
		for (int i = 0; i < parent.getItems().length; i++) {
			// ページに文字列は41行を超えた場合、改ページを行う。
			SetNewPage(newPageFlag);
			String strIndenx = strSpace + PdfGenerator.INDENX_SPACE;
			intstartY = intstartY + PdfGenerator.LINE_HEIGHT;
			Node targetNode = (Node) parent.getItem(i).getData();
			URL url = null;
			if (targetNode != null) {
				url = getIconUrl(targetNode);
			}
			if (url != null) {
				// 文字列とアイコンを出力する。
				outputCharacter(url, strIndenx, parent.getItem(i), intstartX);
				// 線を描画する。
				drawNodeLine(parent, array, intstartX, intstartY, i);
				// "+"と"-"アイコンを描画する。
				drawPlusMinusIcon(parent.getItem(i), intstartX, intstartY);
				newPageFlag++;
			}
			if (parent.getItem(i).getExpanded()) {

				ArrayList<String> cloneAl = (ArrayList<String>) array.clone();
				// ノードの状態をcloneAlに格納する。
				if (i == parent.getItems().length - 1) {
					cloneAl.add("0");
				} else {
					cloneAl.add("1");
				}
				// 子ノードを描画する。
				outputPdfContents(parent.getItem(i), strIndenx
						+ PdfGenerator.INDENX_SPACE, intstartX
						+ PdfGenerator.INDENT, cloneAl);
			}
		}
	}

	/**
	 * 文字列とアイコンの出力
	 *
	 * @param url
	 *            アイコンのURL
	 * @param strIndenx
	 *            文字列の左側の余白
	 * @param childTi
	 *            子ノード
	 * @param intstartX
	 *            線のX軸数値
	 * @return なし
	 * @throws BadElementException
	 * @throws MalformedURLException
	 * @throws IOException
	 */
	private void outputCharacter(URL url, String strIndenx, TreeItem childTi,
			int intstartX) throws BadElementException, MalformedURLException,
			IOException {
		Paragraph objParag = new Paragraph();
		Image image1 = Image.getInstance(url);
		Chunk ch1 = new Chunk(strIndenx);
		Chunk ch2 = new Chunk(image1, 1, -5, true);
		Chunk ch3 = new Chunk(" " + childTi.getText().replaceAll("\r\n", ""), ｆontJapan);
		objParag.add(ch1);
		objParag.add(ch2);
		objParag.add(ch3);
		paragraph.add(objParag);
	}

	/**
	 * PDFファイルへの出力の初期化設定
	 *
	 * @param parent
	 *            ノード
	 * @return なし
	 * @throws IOException
	 * @throws Exception
	 */
	private void outputPdfInit(TreeItem parent) throws Exception {
		//アイコンURLの取得
		Node targetNode = (Node) parent.getData();
		URL url = getIconUrl(targetNode);

		//アイコンと文字列の出力
		paragraph = new Paragraph("");
		Paragraph childParagraph = new Paragraph();
		Image image = Image.getInstance(url);
		childParagraph.add(new Chunk(image, 1, -5, true));
		childParagraph.add(new Chunk(" " + parent.getText().replaceAll("\r\n", ""), ｆontJapan));
		paragraph.add(childParagraph);

		// ルートノードの線を描画する。
		int intstartRootX = PdfGenerator.START_X - PdfGenerator.INDENT;
		int intstartRootY = intstartY;
		int intEndRootX = intstartRootX + PdfGenerator.LINE_LENGTH;
		int intEndRootY = intstartRootY;
		g2d.drawLine(intstartRootX, intstartRootY + PdfGenerator.LINE_HEIGHT
				/ 2, intEndRootX, intEndRootY + PdfGenerator.LINE_HEIGHT / 2);

		// ルートノードの"+"と"-"アイコンを描画する。
		drawPlusMinusIcon(parent, intstartRootX, intstartRootY);
		if (parent.getExpanded()) {
			// pdfOutTemp(parent, 1);
			ArrayList<String> array = new ArrayList<String>();
			array.add("0");
			outputPdfContents(parent, PdfGenerator.INDENX_SPACE,
					PdfGenerator.START_X, array);
		}
	}

	/**
	 * PDFの行のサイズの格納
	 *
	 * @param childTi
	 *            ノード
	 * @param intstartX
	 *            線のX軸数値
	 */
	private void saveLineSize(TreeItem childTi, int intstartX) {
		float leftMargin = document.leftMargin();
		float rightMargin = document.rightMargin();
		float spaceWidth = bf.getWidthPoint(" ", 12);
		float leftWidth = (float) (intstartX + PdfGenerator.LINE_LENGTH
				+ PdfGenerator.LINE_HEIGHT + spaceWidth);
		float rigthWidth = bf.getWidthPoint(childTi.getText(), 12);
		float maxLength = leftMargin + leftWidth + rigthWidth
				+ rightMargin;
		alLineSize.add(Float.toString(maxLength));
	}

	/**
	 * 国によって、文字化けを防ぐフォント設定
	 *
	 * @param strRegu
	 *            フォント
	 * @param strLanguType
	 *            エンコーディング名
	 * @param bol
	 *            有効
	 * @return font フォント
	 * @throws IOException
	 * @throws DocumentException
	 */
	private Font setFont(String strRegu, String strLanguType, boolean bol)
			throws DocumentException, IOException {
		bf = BaseFont.createFont(strRegu, strLanguType, bol);
		Font font = new Font(bf, 12, Font.NORMAL);

		return font;
	}

	/**
	 * 線のスタイルの設定
	 *
	 * @param width
	 *            線の幅
	 * @return bs 線のスタイルのBasicStrokeオブジェクト
	 */
	private BasicStroke setLineStyle(float width) {
		float dash[] = { 1.0f };
		BasicStroke bs = new BasicStroke(width, BasicStroke.CAP_BUTT,
				BasicStroke.JOIN_MITER, 10.0f, dash, 0.0f);
		return bs;
	}

	/**
	 * PDFの改ページの操作
	 *
	 * @param newPageFlag
	 *            改ページフラグ
	 * @return なし
	 * @throws DocumentException
	 */
	private void SetNewPage(int newPageFlag) throws DocumentException {
		if (newPageFlag % intPageMaxLine == 0) {
			g2d.dispose();
			document.add(paragraph);
			document.newPage();
			PdfContentByte cb = writer.getDirectContent();
			g2d = cb.createGraphics(recSelected.getWidth(), recSelected
					.getHeight());
			g2d.setStroke(bs);
			paragraph = new Paragraph("");
			intstartY = PdfGenerator.START_Y - PdfGenerator.LINE_HEIGHT;
		}
	}

	/**
	 * ページサイズの設定
	 *
	 * @param tiRoot
	 *            ノード
	 * @return なし
	 * @throws DocumentException
	 * @throws IOException
	 * @throws MalformedURLException
	 */
	private void setPageSize(TreeItem tiRoot) throws MalformedURLException,
			IOException, DocumentException {
		getLineDate(tiRoot, PdfGenerator.INDENX_SPACE, PdfGenerator.START_X);
		float fMaxSize = getMaxLineSize();
		// ページサイズの種類
		Rectangle[] rec = { PageSize.A4, PageSize.B4, PageSize.A3, PageSize.B3,
				PageSize.A2, PageSize.B2, PageSize.A1, PageSize.B1,
				PageSize.A0, PageSize.B0 };
		// ページサイズの種類によって、行数の表示
		int intPageMaxLineArr[] = { 41, 50, 61, 73, 88, 106, 127, 152, 182, 217 };

		// ツリーのノードの最大サイズによって、ページサイズを設定する。
		for (int i = 0; i < rec.length; i++) {
			// ノードの最大サイズはPageSize.B0を超えなかった場合、
			// ページの標準種類を設定する。
			if (fMaxSize < rec[i].getWidth()) {
				recSelected = rec[i];
				intPageMaxLine = intPageMaxLineArr[i];
				break;
			}
			// ノードの最大サイズはPageSize.B0を超えた場合、
			// 最大サイズによって、自定義でページのサイズを設定する。
			if (i == rec.length - 1) {
				if (rec[i].getWidth() < fMaxSize) {
					float heigth = fMaxSize * rec[0].getHeight()
							/ rec[0].getWidth();
					recSelected = new Rectangle(fMaxSize, heigth);
					intPageMaxLine = (int) (Math.floor((heigth
							- document.topMargin() - document.bottomMargin())
							/ PdfGenerator.LINE_HEIGHT)) - 1;
				}
			}
		}
	}
}
