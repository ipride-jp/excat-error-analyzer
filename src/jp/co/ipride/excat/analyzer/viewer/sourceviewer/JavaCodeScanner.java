package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.text.rules.EndOfLineRule;
import org.eclipse.jface.text.rules.IRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.text.rules.WordRule;

public class JavaCodeScanner extends org.eclipse.jface.text.rules.RuleBasedScanner {
	private IToken fKeywordToken;
	private IToken fTypeToken;
	private IToken fJavaDocToken;
	private IToken fStringToken;
	private IToken fSingleLineCommentToken;
	private IToken fDefaultToken;

	private static String[] fgKeywords = {"abstract", //$NON-NLS-1$
				"break", //$NON-NLS-1$
				"case", "catch", "class", "continue", //$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				"default", "do", //$NON-NLS-2$//$NON-NLS-1$
				"else", "extends", //$NON-NLS-2$//$NON-NLS-1$
				"final", "finally", "for", //$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				"if", "implements", "import", "instanceof", "interface", //$NON-NLS-5$//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				"native", "new", //$NON-NLS-2$//$NON-NLS-1$
				"package", "private", "protected", "public", //$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				"return", //$NON-NLS-1$
				"static", "super", "switch", "synchronized", //$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				"this", "throw", "throws", "transient", "try", //$NON-NLS-5$//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
				"volatile", //$NON-NLS-1$
				"while", //$NON-NLS-1$
				"strictfp",//$NON-NLS-1$
	};
	private static String[] fgTypes = {"void", "boolean", "char", "byte", "short", "int", "long", "float", "double"};//$NON-NLS-9$//$NON-NLS-8$//$NON-NLS-7$//$NON-NLS-6$//$NON-NLS-5$//$NON-NLS-4$//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$
	private static String[] fgConstants = {"false", "null", "true"};//$NON-NLS-3$//$NON-NLS-2$//$NON-NLS-1$

	/**
	 * Creates a Java code scanner
	 */
	public JavaCodeScanner() {
		super();
	}

	public void initializeRules() {
		List<IRule> rules = new ArrayList<IRule>();

		//Add rule for java doc
		rules.add(new MultiLineRule("/**", "*/", fJavaDocToken));

		// Add rule for multiple line comments.
		rules.add(new MultiLineRule("/*", "*/", fSingleLineCommentToken));//$NON-NLS-1$ //$NON-NLS-2$

		// Add rule for single line comments.
		rules.add(new EndOfLineRule("//", fSingleLineCommentToken));//$NON-NLS-1$

		// Add rule for strings and character constants.
		rules.add(new SingleLineRule("\"", "\"", fStringToken, '\\'));//$NON-NLS-2$//$NON-NLS-1$
		rules.add(new SingleLineRule("'", "'", fStringToken, '\\'));//$NON-NLS-2$//$NON-NLS-1$

		// Add generic whitespace rule.
		//rules.add(new WhitespaceRule(new JavaWhitespaceDetector()));

		// Add word rule for keywords, types, and constants.
		WordRule wordRule = new WordRule(
				new JavaWordDetector(), fDefaultToken);
		for (int i = 0; i < fgKeywords.length; i++)
			wordRule.addWord(fgKeywords[i], fKeywordToken);
		for (int i = 0; i < fgTypes.length; i++)
			wordRule.addWord(fgTypes[i], fTypeToken);
		for (int i = 0; i < fgConstants.length; i++)
			wordRule.addWord(fgConstants[i], fTypeToken);
		rules.add(wordRule);


		IRule[] result = new IRule[rules.size()];
		rules.toArray(result);
		setRules(result);
	}

	public void setTokenData(String tokenKey, Object data) {
		if (IStyleConstantsJSPJava.JAVA_KEYWORD.equals(tokenKey)) {
			fKeywordToken = new Token(data);
			fTypeToken = new Token(data);
		} else if (IStyleConstantsJSPJava.JAVA_STRING.equals(tokenKey)) {
			fStringToken = new Token(data);
		} else if (IStyleConstantsJSPJava.JAVA_SINGLE_LINE_COMMENT.equals(tokenKey)) {
			fSingleLineCommentToken = new Token(data);
		} else if (IStyleConstantsJSPJava.JAVA_DEFAULT.equals(tokenKey)) {
			fDefaultToken = new Token(data);
		}else if (IStyleConstantsJSPJava.JAVA_DOC.equals(tokenKey)) {
			fJavaDocToken = new Token(data);
		}
	}
}
