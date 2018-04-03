package jp.co.ipride.excat.analyzer.viewer.propertyviewer;

/**
 * DomBuilderを作成するファクトリ
 *
 * @author sai
 * @since 2009/10/15
 */
public class DomBuilderFactory {

	private static DomBuilderFactory instance;

	protected DomBuilderFactory() {
	}

	/**
	 * ファクトリのインスタンスを取得するメソッド
	 *
	 * @return ファクトリのインスタンス
	 */
	public static synchronized DomBuilderFactory getInstance() {
		if (instance == null) {
			instance = new DomBuilderFactory();
		}
		return instance;
	}

	/**
	 * DomBuilderを作成するメソッド
	 *
	 * @return DomBuilder
	 */
	public DomBuilder newDomBuilder() {
		return new DomBuilder();
	}
}
