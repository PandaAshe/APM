package com.jiace.apm.common.dialog;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.DatePicker;
import android.widget.DatePicker.OnDateChangedListener;

import com.jiace.apm.R;

import java.util.Calendar;
import java.util.Date;

public class DoubleDatePickerDialog extends AlertDialog implements OnDateChangedListener {
    private final DatePicker mDatePicker_start;
    private final DatePicker mDatePicker_end;

    public interface OnDateSetListener {
        void onDateSet(long start, long end);
    }

    private OnDateSetListener mOnDateSetListener;

    public void setOnDateSetListener(OnDateSetListener mCallBack) {
        this.mOnDateSetListener = mCallBack;
    }

    public DoubleDatePickerDialog(Context context) {
        super(context);
        View view = View.inflate(context, R.layout.date_picker_dialog, null);
        setView(view);

        mDatePicker_start = view.findViewById(R.id.datePickerStart);
        mDatePicker_end = view.findViewById(R.id.datePickerEnd);

        view.findViewById(R.id.oK).setOnClickListener(v -> tryNotifyDateSet());
        view.findViewById(R.id.cancel).setOnClickListener(v -> dismiss());

    }

    @Override
    public void onDateChanged(DatePicker view, int year, int month, int day) {
        if (view.getId() == R.id.datePickerStart)
            mDatePicker_start.init(year, month, day, this);

        if (view.getId() == R.id.datePickerEnd)
            mDatePicker_end.init(year, month, day, this);
    }

    /**
     * 获得开始日期的DatePicker
     *
     * @return The calendar view.
     */
    public DatePicker getDatePickerStart() {
        return mDatePicker_start;
    }

    /**
     * 获得结束日期的DatePicker
     *
     * @return The calendar view.
     */
    public DatePicker getDatePickerEnd() {
        return mDatePicker_end;
    }

    /**
     * Sets the start date.
     *
     * @param year        The date year.
     * @param monthOfYear The date month.
     * @param dayOfMonth  The date day of month.
     */
    public void setStartDate(int year, int monthOfYear, int dayOfMonth) {
        mDatePicker_start.updateDate(year, monthOfYear, dayOfMonth);
    }

    public void setStartDate(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));

        mDatePicker_start.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    /**
     * Sets the end date.
     *
     * @param year        The date year.
     * @param monthOfYear The date month.
     * @param dayOfMonth  The date day of month.
     */
    public void setEndDate(int year, int monthOfYear, int dayOfMonth) {
        mDatePicker_end.updateDate(year, monthOfYear, dayOfMonth);
    }

    public void setEndDate(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time));

        mDatePicker_end.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
    }

    private void tryNotifyDateSet() {
        if (mOnDateSetListener != null) {
            mDatePicker_start.clearFocus();
            mDatePicker_end.clearFocus();

            long start = new Date(mDatePicker_start.getYear() - 1900, mDatePicker_start.getMonth(), mDatePicker_start.getDayOfMonth(), 0, 0, 0).getTime();
            long end = new Date(mDatePicker_end.getYear() - 1900, mDatePicker_end.getMonth(), mDatePicker_end.getDayOfMonth(), 23, 59, 59).getTime();
            mOnDateSetListener.onDateSet(start, end);
        }
        dismiss();
    }
}