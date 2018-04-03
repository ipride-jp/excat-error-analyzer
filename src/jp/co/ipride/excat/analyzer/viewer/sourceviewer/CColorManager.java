package jp.co.ipride.excat.analyzer.viewer.sourceviewer;

/*******************************************************************************
 * Copyright (c) 2000 2005 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - initial API and implementation
 *     QNX Software System
 *******************************************************************************/


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Java color manager.
 */
public class CColorManager {

	protected Map<String, RGB> fKeyTable = new HashMap<String, RGB>(10);

	protected Map<Display,Map<RGB,Color>> fDisplayTable = new HashMap<Display,Map<RGB,Color>>(2);

	public CColorManager() {
		bindColor(IStyleConstantsJSPJava.JAVA_KEYWORD, new RGB(64, 0, 64));
		bindColor(IStyleConstantsJSPJava.JAVA_SINGLE_LINE_COMMENT, new RGB(63, 127, 95));
		bindColor(IStyleConstantsJSPJava.JAVA_DOC, new RGB(128, 128, 255));
		bindColor(IStyleConstantsJSPJava.JAVA_STRING, new RGB(0,0, 255));
		bindColor(IStyleConstantsJSPJava.JAVA_DEFAULT, new RGB(0, 0, 0));
		bindColor(IStyleConstantsJSPJava.BACKGROUND, new RGB(255, 255, 255));
	}

	protected void dispose(Display display) {
		Map<RGB,Color> colorTable = fDisplayTable.get(display);
		if (colorTable != null) {
			Iterator<Color> e = colorTable.values().iterator();
			while (e.hasNext())
				e.next().dispose();
		}
	}

	/*
	 * @see IColorManager#getColor(RGB)
	 */
	public Color getColor(RGB rgb) {

		if (rgb == null)
			return null;

		final Display display = Display.getCurrent();
		Map<RGB,Color> colorTable = fDisplayTable.get(display);
		if (colorTable == null) {
			colorTable = new HashMap<RGB,Color>(10);
			fDisplayTable.put(display, colorTable);
			display.disposeExec(new Runnable() {
				public void run() {
					dispose(display);
				}
			});
		}

		Color color = (Color) colorTable.get(rgb);
		if (color == null) {
			color = new Color(Display.getCurrent(), rgb);
			colorTable.put(rgb, color);
		}

		return color;
	}

	/*
	 * @see IColorManager#dispose
	 */
	public void dispose() {
		dispose(Display.getCurrent());
	}

	/*
	 * @see IColorManager#getColor(String)
	 */
	public Color getColor(String key) {

		if (key == null)
			return null;

		RGB rgb = (RGB) fKeyTable.get(key);
		return getColor(rgb);
	}

	/*
	 * @see IColorManagerExtension#bindColor(String, RGB)
	 */
	public void bindColor(String key, RGB rgb) {
		Object value = fKeyTable.get(key);
		if (value != null)
			throw new UnsupportedOperationException();

		fKeyTable.put(key, rgb);
	}

	/*
	 * @see IColorManagerExtension#unbindColor(String)
	 */
	public void unbindColor(String key) {
		fKeyTable.remove(key);
	}
}
