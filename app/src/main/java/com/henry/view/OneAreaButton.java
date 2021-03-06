package com.henry.view;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.company.administrator.monitorsystem.FirstAreaActivity;
import com.company.administrator.monitorsystem.R;

/**
 * Created by Administrator on 2015/8/2.
 */

public class OneAreaButton extends Button {
    private int state = 1;
    public static final int STATE_INIT_1=1;//GREEN
    public static final int STATE_INIT_1_FOCUS=2;//GREEN_FOCUS
    public static final int STATE_FULL_1=3; //YELLOW
    public static final int STATE_FULL_1_FOCUS=4; //YELLOW_FOCUS
    private CharSequence defaultText ;
    private String meterCls;

    public String getMeterCls() {
        return meterCls;
    }

    public void setMeterCls(String meterCls) {
        this.meterCls = meterCls;
    }




    public OneAreaButton(Context context) {
        super(context);
        changeState(STATE_INIT_1);
        defaultText = this.getText();
        FirstAreaActivity activity = (FirstAreaActivity)context;
        this.setOnClickListener(activity);
        this.setOnLongClickListener(activity);
    }

    public OneAreaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultText = this.getText();
        FirstAreaActivity activity = (FirstAreaActivity)context;
        this.setOnClickListener(activity);
        this.setOnLongClickListener(activity);
    }

    public OneAreaButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        defaultText = this.getText();
        FirstAreaActivity activity = (FirstAreaActivity)context;
        this.setOnClickListener(activity);
        this.setOnLongClickListener(activity);
    }

//    public SpecButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    public void drawableWithFocus(){
        switch (this.state){
            case STATE_INIT_1:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_focus));
                this.state = STATE_INIT_1_FOCUS;
                break;
            case STATE_FULL_1:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_yellow_focus));
                this.state = STATE_FULL_1_FOCUS;
                break;
            default:
                break;
        }
    }
    public void drawableWithBlur(){
        switch (this.state){
            case STATE_INIT_1_FOCUS:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
                this.state = STATE_INIT_1;
                break;
            case STATE_FULL_1_FOCUS:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_yellow));
                this.state = STATE_FULL_1;
                break;
            default:
                break;
        }
    }
    public void changeState(int state){
        this.state = state;
        switch (this.state){
            case STATE_INIT_1:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
                break;
            case STATE_INIT_1_FOCUS:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_focus));
                break;
            case STATE_FULL_1:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_yellow));
                break;
            case STATE_FULL_1_FOCUS:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_yellow_focus));
                break;
            default:
                break;
        }

    }

    public void initState(){
        changeState(STATE_INIT_1);
    }

    public void blurState(){ //lose focus state
        if(this.state%2 ==0 && this.state>0){
            int tmpState = --this.state;
            changeState(tmpState);
        }
    }

    public void nextState(){
        switch (this.state) {
            case STATE_INIT_1_FOCUS:
                changeState(STATE_FULL_1);
                break;
            case STATE_FULL_1_FOCUS:
                changeState(STATE_INIT_1);
                break;
            default:
                break;
        }
    }

    public void spark(){
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(500); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        this.startAnimation(animation);
    }

    public void stopSpark(){
        this.clearAnimation();
    }

    public int getState(){
        return this.state;
    }
}
