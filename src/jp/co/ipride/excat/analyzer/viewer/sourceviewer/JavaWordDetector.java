package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

public class JavaWordDetector implements org.eclipse.jface.text.rules.IWordDetector{

	/**
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordIdentifierPart
	 */
	public boolean isWordPart(char c) {
		return Character.isJavaIdentifierPart(c);
	}

	/**
	 * @see org.eclipse.jface.text.rules.IWordDetector#isWordIdentifierStart
	 */
	public boolean isWordStart(char c) {
		return Character.isJavaIdentifierStart(c);
	}
	
}
