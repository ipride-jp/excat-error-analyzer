package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

import org.eclipse.jface.text.rules.IPredicateRule;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.MultiLineRule;
import org.eclipse.jface.text.rules.RuleBasedPartitionScanner;
import org.eclipse.jface.text.rules.SingleLineRule;
import org.eclipse.jface.text.rules.Token;

public class WordPartitionScanner extends RuleBasedPartitionScanner  {

	public static final String JAVA_SINGLE_LINE_COMMENT = "_CCAT_JAVA_SINGLE_LINE_COMMENT";
	
	public static final String JAVA_MULTI_LINE_COMMENT = "_CCAT_JAVA_MULTI_LINE_COMMENT";
	
	public static final String JAVA_DOC = "_CCAT_JAVA_DOC";
	
	public static final String JAVA_STRING = "_CCAT_JAVA_STRING";
	
	public static final String JAVA_CHARACTER = "_CCAT_JAVA_CHARACTER";
	
	public WordPartitionScanner(){

		IPredicateRule[] rules = new IPredicateRule[5];

		IToken string =new Token(JAVA_SINGLE_LINE_COMMENT);
		rules[0] = new SingleLineRule("//","\n",string);

		string =new Token(JAVA_DOC);
		rules[1] = new MultiLineRule("/**","*/",string);
		
		string =new Token(JAVA_MULTI_LINE_COMMENT);
		rules[2] = new MultiLineRule("/*","*/",string);

		string =new Token(JAVA_STRING);
		rules[3] = new SingleLineRule("\"","\"",string,'\\');

		string =new Token(JAVA_CHARACTER);
		rules[4] = new SingleLineRule("'","'",string);
		
		setPredicateRules(rules);
	}

}
