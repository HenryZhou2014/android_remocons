package com.henry.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;

import com.company.administrator.monitorsystem.FirstAreaActivity;
import com.company.administrator.monitorsystem.R;
import com.company.administrator.monitorsystem.ThirdAreaActivity;

/**
 * Created by Administrator on 2015/8/2.
 */

public class SecondAreaButton extends Button {
    private int state = 1;
    public static final int STATE_INIT_2=1;//GREEN
    public static final int STATE_INIT_2_FOCUS=2;//GREEN_FOCUS
    public static final int STATE_FULL_2=3; //YELLOW
    public static final int STATE_FULL_2_FOCUS=4; //YELLOW_FOCUS
    public static final int STATE_PAN_2=5;//HAS PAN
    public static final int STATE_PAN_2_FOCUS=6;//HAS PAN_FOCUS

    private CharSequence defaultText ;

    public String getMeterCls() {
        return meterCls;
    }

    public void setMeterCls(String meterCls) {
        this.meterCls = meterCls;
    }

    private String meterCls;
    public void setDefaultText(){
        this.setText(defaultText);
    }

    public int getState(){
        return this.state;
    }
    public SecondAreaButton(Context context) {
        super(context);
        //this.setWidth(60);
        //this.setHeight(60);
        changeState(STATE_INIT_2);

        if(context instanceof ThirdAreaActivity){
            ThirdAreaActivity listener = (ThirdAreaActivity)context;
            this.setOnLongClickListener(listener);
            this.setOnClickListener(listener);
        }else if(context instanceof FirstAreaActivity){
            FirstAreaActivity listener = (FirstAreaActivity)context;
            this.setOnClickListener(listener);
        }
        defaultText = this.getText();
        ;
    }
    public void blurState(){ //lose focus state
        if(this.state%2 ==0 && this.state>0){
            int tmpState = --this.state;
            changeState(tmpState);
        }
    }
    public SecondAreaButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        changeState(STATE_INIT_2);
        if(context instanceof ThirdAreaActivity){
            ThirdAreaActivity listener = (ThirdAreaActivity)context;
            this.setOnLongClickListener(listener);
            this.setOnClickListener(listener);
        }else if(context instanceof FirstAreaActivity){
            FirstAreaActivity listener = (FirstAreaActivity)context;
            this.setOnClickListener(listener);
        }
        defaultText = this.getText();
    }

    public SecondAreaButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        changeState(STATE_INIT_2);
        if(context instanceof ThirdAreaActivity){
            ThirdAreaActivity listener = (ThirdAreaActivity)context;
            this.setOnLongClickListener(listener);
            this.setOnClickListener(listener);
        }else if(context instanceof FirstAreaActivity){
            FirstAreaActivity listener = (FirstAreaActivity)context;
            this.setOnClickListener(listener);
        };
        defaultText = this.getText();
    }

//    public SpecButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    public void changeState(int state){
        this.state = state;
        switch (this.state){
            case STATE_INIT_2:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
                break;
            case STATE_FULL_2:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_yellow));
                break;
            case STATE_PAN_2:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_white));
                break;
            default:
                break;
        }
    }


    public void drawableWithFocus(){
        switch (this.state){
            case STATE_INIT_2:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_focus));
                this.state = STATE_INIT_2_FOCUS;
                break;
            case STATE_FULL_2:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_yellow_focus));
                this.state = STATE_FULL_2_FOCUS;
                break;
            case STATE_PAN_2:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_white_focus));
                this.state = STATE_PAN_2_FOCUS;
                break;
            default:
                break;
        }
    }

    public void drawableWithBlur(){
        switch (this.state){
            case STATE_INIT_2_FOCUS:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
                this.state = STATE_INIT_2;
                break;
            case STATE_FULL_2_FOCUS:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_yellow));
                this.state = STATE_FULL_2;
                break;
            case STATE_PAN_2_FOCUS:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_white));
                this.state = STATE_PAN_2;
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
}
