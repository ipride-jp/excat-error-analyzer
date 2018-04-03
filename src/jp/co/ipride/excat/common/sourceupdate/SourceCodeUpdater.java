package jp.co.ipride.excat.common.sourceupdate;

import org.eclipse.core.runtime.IProgressMonitor;

/**
 * 実行側の実装インターフェース
 * @author tu-ipride
 * @version 2.0
 * @date 2009/10/17
 */
public interface SourceCodeUpdater {

	void update() throws SouceCodeUpdateException;

	//changed by V3
	//void addUpdateListener(SourceCodeUpdateListener listener);

	void setMonitor(IProgressMonitor monitor);
}
