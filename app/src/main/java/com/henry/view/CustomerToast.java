package com.henry.view;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.company.administrator.monitorsystem.R;

/**
 * Created by Administrator on 2015/8/10.
 */
public class CustomerToast extends Toast {




    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link Application}
     *                or {@link Activity} object.
     */
    public CustomerToast(Context context) {
        super(context);
    }



    public static CustomerToast makeText2(Context context, CharSequence text,int duration) {
        CustomerToast result = new CustomerToast(context);

        LayoutInflater inflate = (LayoutInflater)
                context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflate.inflate(R.layout.cosmtor_toast, null);
        TextView tv = (TextView)v.findViewById(R.id.messageToast);
        tv.setText(text);
        result.setView(v);
        result.setGravity(Gravity.CENTER_VERTICAL,0,0);
        result.setDuration(duration);
        return result;
    }

    @Override
    public void show(){
      super.show();
    }
}
