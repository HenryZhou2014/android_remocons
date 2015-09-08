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

public class SpecButton extends Button{
    private int state = 1;
    public static final int STATE_INIT_3=1;//GREEN
    public static final int STATE_INIT_3_FOCUS=2;//GREEN_FOCUS
    public static final int STATE_NOTHING_3=3;//GRAY
    public static final int STATE_NOTHING_3_FOCUS=4;//GRAY_FOCUS
    public static final int STATE_FULL_3=5; //YELLOW
    public static final int SSTATE_FULL_3_FOCUS=6;//YELLOW_FOCUS
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

    public SpecButton(Context context) {
        super(context);
        //this.setWidth(60);
        //this.setHeight(60);
        changeState(STATE_INIT_3);
        if(context instanceof ThirdAreaActivity){
            ThirdAreaActivity activity = (ThirdAreaActivity)context;
            this.setOnClickListener(activity);
            this.setOnLongClickListener(activity);
        }else if(context instanceof FirstAreaActivity){
            FirstAreaActivity activity = (FirstAreaActivity)context;
            this.setOnClickListener(activity);
            this.setOnLongClickListener(activity);
        }
        defaultText=this.getText();
    }


    public SpecButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        changeState(STATE_INIT_3);

        if(context instanceof ThirdAreaActivity){
            ThirdAreaActivity activity = (ThirdAreaActivity)context;
            this.setOnClickListener(activity);
            this.setOnLongClickListener(activity);
        }else if(context instanceof FirstAreaActivity){
            FirstAreaActivity activity = (FirstAreaActivity)context;
            this.setOnClickListener(activity);
            this.setOnLongClickListener(activity);
        }

        defaultText=this.getText();
    }

    public SpecButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        changeState(STATE_INIT_3);
        if(context instanceof ThirdAreaActivity){
            ThirdAreaActivity activity = (ThirdAreaActivity)context;
            this.setOnClickListener(activity);
            this.setOnLongClickListener(activity);
        }else if(context instanceof FirstAreaActivity){
            FirstAreaActivity activity = (FirstAreaActivity)context;
            this.setOnClickListener(activity);
            this.setOnLongClickListener(activity);
        }
        defaultText=this.getText();
    }

//    public SpecButton(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }

    public void drawableWithFocus(){
        switch (this.state){
            case STATE_INIT_3:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_focus));
                this.state = STATE_INIT_3_FOCUS;
                break;
            case STATE_NOTHING_3:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_gray_focus));
                this.state = STATE_NOTHING_3_FOCUS;
                break;
            case STATE_FULL_3:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_yellow_focus));
                this.state = SSTATE_FULL_3_FOCUS;
                break;
            default:
                break;
        }
    }
    public void drawableWithBlur(){
        switch (this.state){
            case STATE_INIT_3_FOCUS:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
                this.state = STATE_INIT_3;
                break;
            case STATE_NOTHING_3_FOCUS:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_gray));
                this.state = STATE_NOTHING_3;
                break;
            case SSTATE_FULL_3_FOCUS:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_yellow));
                this.state = STATE_FULL_3;
                break;
            default:
                break;
        }
    }
    public void changeState(int state){
        this.state = state;
        switch (this.state){
            case STATE_INIT_3:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
                break;
            case STATE_NOTHING_3:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_gray));
                break;
            case STATE_FULL_3:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_yellow));
                break;
            case STATE_INIT_3_FOCUS:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_focus));
                break;
            case STATE_NOTHING_3_FOCUS:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_gray_focus));
                break;
            case SSTATE_FULL_3_FOCUS:
                this.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button_yellow_focus));
                break;
            default:
                break;
        }
        //if(state == STATE_FULL_3){
            //this.setBackgroundColor();
           // this.setBackground(getResources().getDrawable(R.drawable.cicle_button_yellow));

        //}
    }

    public void initState(){
        changeState(STATE_INIT_3);
    }

    public void blurState(){ //lose focus state
        if(this.state%2 ==0 && this.state>0){
            int tmpState = --this.state;
            changeState(tmpState);
        }
    }

    public void nextState(){
        switch (this.state) {
            case STATE_INIT_3_FOCUS:
                changeState(STATE_FULL_3);
                break;
            case SSTATE_FULL_3_FOCUS:
                changeState(STATE_NOTHING_3);
                break;
            case STATE_NOTHING_3_FOCUS:
                changeState(STATE_INIT_3);
                break;
            default:
                break;
        }
    }

    public int getState(){
        return this.state;
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
