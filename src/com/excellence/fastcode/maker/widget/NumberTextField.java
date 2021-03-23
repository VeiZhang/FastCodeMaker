package com.excellence.fastcode.maker.widget;

import javafx.scene.control.TextField;

/**
 * <pre>
 *     author : VeiZhang
 *     blog   : http://tiimor.cn
 *     time   : 2018/5/14
 *     desc   :
 * </pre>
 */

public class NumberTextField extends TextField
{
	private static final String REGEX_NUM = "[1-9]\\d*$";

	@Override
	public void replaceText(int start, int end, String text)
	{
		String appendText = getText() + text;
		if (appendText.matches(REGEX_NUM))
		{
			super.replaceText(start, end, text);
		}
	}

}
