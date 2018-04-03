package jp.co.ipride.excat.analyzer.viewer.threadmap;

import java.util.List;

import jp.co.ipride.excat.MainViewer;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

/**
 *
 * @author tu-ipride
 * @version 3.0
 * @since 2009/10/5
 */
public abstract class AbstractThreadMapTable extends SashForm{

	public static final String[] COLUMN_NAMES = new String[] {
		"ThreadName","Status","Priority","CPUTime","WaitReason","MonitorObject","WaitThread"
		};

	protected MainViewer mainvw;

	protected TableViewer tableViewer;

	protected Color red;  //waitting

	protected Color blue;  //running

	/**
	 * construct
	 * @param composite
	 * @param style
	 */
	public AbstractThreadMapTable(Composite composite, int style) {
		super(composite, style);
	}

	public void setMainViewer(MainViewer mainViewer){
		this.mainvw = mainViewer;
	}

	public abstract void setThreadDumpData(List<List<String>> data);

	protected void packColumns() {
		Table table = tableViewer.getTable();

		// äeÉJÉâÉÄÇÃïùÇåvéZÇ∑ÇÈ
		int count = table.getColumnCount();
		TableColumn[] columns = table.getColumns();
		for (int index = 0; index < count; index++) {
			columns[index].pack();
		}
	}
}
