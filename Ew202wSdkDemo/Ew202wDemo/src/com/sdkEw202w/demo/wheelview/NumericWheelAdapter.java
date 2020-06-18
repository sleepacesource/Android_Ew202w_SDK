package com.sdkEw202w.demo.wheelview;

import com.sdkEw202w.demo.util.LogUtil;

import android.nfc.Tag;

/**
 * Numeric Wheel adapter.
 */
public class NumericWheelAdapter implements WheelAdapter {
	
	private static final String TAG = NumericWheelAdapter.class.getSimpleName();

	/** The default min value */
	public static final int DEFAULT_MAX_VALUE = 9;

	/** The default max value */
	private static final int DEFAULT_MIN_VALUE = 0;

	// Values
	private int minValue;
	private int maxValue;

	// format
	private String format;

	/**
	 * Default constructor
	 */
	public NumericWheelAdapter() {
		this(DEFAULT_MIN_VALUE, DEFAULT_MAX_VALUE);
	}

	/**
	 * Constructor
	 * 
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 */
	public NumericWheelAdapter(int minValue, int maxValue) {
		this.minValue = minValue;
		this.maxValue = maxValue;
	}

	private int[] data;
	private int type=1;

	public NumericWheelAdapter(int[] data, int type) {
		this.data = data;
		this.type = type;
	}

	/**
	 * Constructor
	 * 
	 * @param minValue
	 *            the wheel min value
	 * @param maxValue
	 *            the wheel max value
	 * @param format
	 *            the format string
	 */
	public NumericWheelAdapter(int minValue, int maxValue, String format) {
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.format = format;
	}

	@Override
	public Object getItem(int index) {

		if (type == 1) {
			if (index >= 0 && index < getItemsCount()) {
				int value = minValue + index;
				String str = format != null ? String.format(format, value) : Integer
						.toString(value);
//				LogUtil.log(TAG+" getItem idx:" + index+",str:" + str);
				return str;
			}
		} else {
			if (index >= 0 && index < getItemsCount()) {
				return data[index];
			}
		}
		return 0;
	}
	
	@Override
	public int getItemsCount() {
		if (type == 1) {
			return maxValue - minValue + 1;
		} else {
			if (data!=null) {
				return data.length;
			}
		}
		return 0;
	}

	@Override
	public int indexOf(Object o) {
		return Integer.parseInt(o.toString());
	}
}
