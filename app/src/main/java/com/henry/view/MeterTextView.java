package com.henry.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

import com.company.administrator.monitorsystem.ThirdAreaActivity;

/**
 * Created by Henry on 2015/8/6.
 */
public class MeterTextView extends TextView {

    private String meterName;
    private int defaultTextColor;
    public String getMeterName() {
        return meterName;
    }

    public void setMeterName(String meterName) {
        this.meterName = meterName;
    }

    public MeterTextView(Context context) {
        super(context);
        if(context instanceof ThirdAreaActivity){
            ThirdAreaActivity activity = (ThirdAreaActivity)context;
            this.setOnClickListener(activity);
        }
        this.defaultTextColor = this.getCurrentTextColor();
        this.setClickable(true);
    }

    public MeterTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setClickable(true);
        this.defaultTextColor = this.getCurrentTextColor();
        if(context instanceof ThirdAreaActivity){
            ThirdAreaActivity activity = (ThirdAreaActivity)context;
            this.setOnClickListener(activity);
        }
    }

    public MeterTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setClickable(true);
        this.defaultTextColor = this.getCurrentTextColor();
        if(context instanceof ThirdAreaActivity){
            ThirdAreaActivity activity = (ThirdAreaActivity)context;
            this.setOnClickListener(activity);
        }

    }

//    public MeterTextView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    public void setDefaultTextColor(){
        this.setTextColor(this.defaultTextColor);
    }
}
