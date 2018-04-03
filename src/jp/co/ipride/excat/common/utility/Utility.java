/*
 * Error Anaylzer Tool for Java
 *
 * Created on 2006/4/1
 *
 * Copyright (c) 2006-2009 iPride Co.,Ltd.
 * All rights reserved.
 */
package jp.co.ipride.excat.common.utility;

import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;


/**
 * ダイアローグ画面設定のユーティリティ
 * @author 屠偉新
 * @since 2006/9/17
 */
public class Utility {

	public static final int BUTTON_WIDTH = 80;

	public static final char[] separate = new char[]{
		'.',
		' ',
		'/',
		'(',
		')',
		'{',
		'}',
		'[',
		']',
		';',
		',',
		'=',
		'>',
		'<',
		'!',
		'~',
		'?',
		':',
		'+',
		'-',
		'&',
		'^',
		'*',
		'%',
		'\n',
		'\t',
		'\b',
		'\f',
		'\r'
	};
	/**
	 * ボタン設定
	 *
	 * @param c
	 * @param style
	 * @param name
	 * @param minWidth
	 * @param d
	 * @return
	 */
	public static Button createButton(Composite c, int style,
		String name, int minWidth, int span) {
		GridData d = new GridData(GridData.FILL_VERTICAL | GridData.HORIZONTAL_ALIGN_END);
		Button b = new Button(c, style);
		b.setText(name);

		int w = b.computeSize(SWT.DEFAULT, SWT.DEFAULT).x;
		if (w < minWidth) {
		    d.widthHint = minWidth;
		} else {
		    d.widthHint = w;
		}
		d.horizontalSpan = span;
		b.setLayoutData(d);

		return b;
	}


	/**
	 * 分離棒を設定
	 * @param c
	 * @return
	 */
	public static Label createHorizotalLine(Composite c) {
        Label line = new Label(c, SWT.SEPARATOR | SWT.HORIZONTAL);
        GridData data = new GridData(GridData.FILL_VERTICAL);
        line.setLayoutData(data);
        return line;
    }

	public static String[] transferToStrings(List<String> list){
		if (list == null){
			return new String[]{};
		}else{
			String[] array = new String[list.size()];
			for (int i=0; i<list.size(); i++){
				array[i]=list.get(i);
			}
			return array;
		}
	}

	public static String filterString(String value){
		if (value == null){
			return null;
		}
		char[]  data = value.toCharArray();
		if (data.length >= 2 && data[0]=='"' && data[data.length-1] == '"'){
			String str ="";
			for (int i=1; i<data.length-1; i++){
				str += data[i];
			}
			return str;
		}else{
			return value;
		}
	}

	public static boolean isSeparate(char word){
		for (char w: separate){
			if (w == word){
				return true;
			}
		}
		return false;
	}

	public static boolean isArray(String word){
		String text = word.trim();
		if (text.charAt(text.length()-1) == ']'){
			return true;
		}else{
			return false;
		}
	}

}
