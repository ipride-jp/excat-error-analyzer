package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

/**
 * メソッドにある変数の情報
 * @author jiang
 *
 */
public class MatchVariableInfo {

	/**
	 * 変数名
	 */
	private String name = null;

	/**
	 * 変数タイプ
	 */
	private String type = null;
	
	/**
	 * 変数の中間タイプ
	 */
	private MiddleType varMType= null;
	
	/**
	 * 変数タイプの完全修飾名（パッケージ名を含む）
	 */
	private String fullType = null;
	
	/**
	 * 変数の有効範囲の開始位置
	 */
	private int validFrom = 0;
	
	/**
	 * 変数の有効範囲の終了位置
	 */
	private int validTo = 0;
	
	/**
	 * 変数の宣言の開始位置
	 */
	private int startPosition = 0;
	
	/**
	 * 変数の宣言の長さ
	 */
	private int length = 0;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(int validFrom) {
		this.validFrom = validFrom;
	}

	public int getValidTo() {
		return validTo;
	}

	public void setValidTo(int validTo) {
		this.validTo = validTo;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getLength() {
		return length;
	}

	public void setLength(int length) {
		this.length = length;
	}

	public int getStartPosition() {
		return startPosition;
	}

	public void setStartPosition(int startPosition) {
		this.startPosition = startPosition;
	}

	public String getFullType() {
		return fullType;
	}

	public void setFullType(String fullType) {
		this.fullType = fullType;
	}

	public MiddleType getVarMType() {
		return varMType;
	}

	public void setVarMType(MiddleType varMType) {
		this.varMType = varMType;
	}
	
	
}
