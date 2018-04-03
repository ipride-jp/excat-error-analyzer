package jp.co.ipride.excat.analyzer.function.printer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

public class LinePrintContent {
	public final static int NOTHING = 0;
	public final static int MINUS = 1;
	public final static int PLUS = 2;

	public Image plusMinusIcon = null;
	public Image nodeIcon;
	public String nodeText;
	public Point plusMinusIconPos = null;
	public Point horLineStartPos;
	public Point horLineEndPos;
	public List<Point> vertLinesStartPos = new ArrayList<Point>();
	public List<Point> vertLinesEndPos = new ArrayList<Point>();
	public Point nodeIconPos;
	public Point nodeTextPos;

	public void recalculatePos(int offsetY) {
		if (plusMinusIconPos != null) {
			plusMinusIconPos.y -= offsetY;
		}

		horLineStartPos.y -= offsetY;
		horLineEndPos.y -= offsetY;

		for (int i = 0; i < vertLinesStartPos.size(); i++) {
			((Point)vertLinesStartPos.get(i)).y -= offsetY;
			((Point)vertLinesEndPos.get(i)).y -= offsetY;
		}

		nodeIconPos.y -= offsetY;
		nodeTextPos.y -= offsetY;
	}
}