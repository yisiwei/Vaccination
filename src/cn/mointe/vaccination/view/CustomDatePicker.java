package cn.mointe.vaccination.view;

import java.util.Calendar;

import android.content.Context;
import android.widget.DatePicker;

public class CustomDatePicker extends DatePicker {

	private int maxYear = 0;
	private int maxMonth = 0;
	private int maxDay = 0;

	private int minYear = 0;
	private int minMonth = 0;
	private int minDay = 0;
	private Calendar m_minDateCalendar, m_maxDateCalendar, m_calendar;

	private boolean m_fired = false;

	public CustomDatePicker(Context context) {
		super(context);
	}

}
