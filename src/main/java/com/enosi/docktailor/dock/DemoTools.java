
package com.enosi.docktailor.dock;
import com.enosi.docktailor.common.util.CKit;
import com.enosi.docktailor.docktailor.fx.HPane;

import java.text.DecimalFormat;

/**
 * Demo Tools.
 */
public class DemoTools {
	private static final DecimalFormat format = new DecimalFormat("#0.##");

	/**
	 * formats double value
	 */
	public static String f(double x) {
		int n = CKit.round(x);
		if (x == n) {
			return String.valueOf(n);
		} else {
			return format.format(x);
		}
	}


	/**
	 * spec description
	 */
	public static String spec(double x) {
		if (x == HPane.FILL) {
			return "FILL";
		} else if (x == HPane.PREF) {
			return "PREF";
		} else {
			return f(x);
		}
	}
}