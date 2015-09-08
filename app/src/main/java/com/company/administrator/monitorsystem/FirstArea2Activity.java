package com.company.administrator.monitorsystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.henry.view.MeterTextView;
import com.henry.view.SecondAreaButton;
import com.henry.view.SpecButton;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Administrator on 2015/8/2.
 */
public class FirstArea2Activity extends Activity implements View.OnClickListener,View.OnLongClickListener{
    private final static int SCANNIN_GREQUEST_CODE = 1;
    //Total of three area Button
    private int count = 17;
    //every pagesize
    private int pageszie=6;
    //Default index
    private int currentIndex = 1;
    private int tatalPageSize = 1;
    private int lastPageSize = count%pageszie;
    private ImageButton left ;
    private ImageButton right ;
    private Button reset ;
    private LinearLayout threeMeterLayout;
    private Button fromB;
    private Button toB;
    private Button fromBtnFlag=null; // is not has select Button;
    private Button toBtnFlag = null;


    /**
     * Collection for three Area BUTTONS
     */
    private  Button[] threeAreaButtonCollect = new Button[count];
    private Set<MeterTextView> meterSet = new HashSet<MeterTextView>();

    //Current status
    private SpecButton currentThreeBtn;
    private SecondAreaButton currentSecondBtn;
    private MeterTextView currentMeterLabel;
    //prev status
    private SpecButton prevThreeBtn;
    private SecondAreaButton prevSecondBtn;
    private MeterTextView prevMeterLabel;
    private int model=MODEL_NORMAL;
    public final static int MODEL_NORMAL=0;
    public final static int MODEL_KONGPAN=1;


    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.first_area2);


        tatalPageSize = lastPageSize==0?count/pageszie:(count/pageszie+1);
//        initSubArea();
        initThreeButton();

        fromB  =(Button)findViewById(R.id.fromtBtnId);
        toB=(Button)findViewById(R.id.tolBtnId);

        //SpecButton specButton =(SpecButton)findViewById(R.id.treeB1);


        threeMeterLayout =(LinearLayout) findViewById(R.id.thirdMeterLayout);


        left =(ImageButton)findViewById(R.id.thirdAreaLft);
        right =(ImageButton)findViewById(R.id.thirdAreaRig);
        reset =(Button)findViewById(R.id.thirdAreaReset);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentIndex == 1) {
                    Toast.makeText(FirstArea2Activity.this, "前面没有按钮", Toast.LENGTH_LONG).show();
                    return;
                }
                currentIndex--;
                setAllThreeBtnGone();
                for (int j = pageszie * (currentIndex - 1); j < (pageszie * currentIndex); j++) {
                    threeAreaButtonCollect[j].setVisibility(View.VISIBLE);
                }
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("THREE AREA", "reset");
                if (currentIndex == tatalPageSize) {
                    Toast.makeText(FirstArea2Activity.this, "后面没有按钮", Toast.LENGTH_LONG).show();
                    return;
                }
                currentIndex++;
                setAllThreeBtnGone();
                for (int j = pageszie * (currentIndex - 1); j < (pageszie * currentIndex); j++) {
                    try {
                        threeAreaButtonCollect[j].setVisibility(View.VISIBLE);
                    } catch (IndexOutOfBoundsException e) {
                        break;
                    }
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentThreeBtn != null) {
                    currentThreeBtn.initState();
                    currentThreeBtn = null;
                }
                Toast.makeText(FirstArea2Activity.this, "成功", Toast.LENGTH_SHORT).show();
            }
        });

//        FragmentTransaction transaction = getFragmentManager().beginTransaction();
//        SecondAreaFragment secondAreaFragment = new SecondAreaFragment();
//        transaction.add(R.id.fragmentContinerid, secondAreaFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
    }




    public void setAllThreeBtnGone(){
        for(Button tmpBtn :threeAreaButtonCollect){
            tmpBtn.setVisibility(View.GONE);
        }
    }

   public void initThreeButton(){
       LinearLayout threeBtnLayout= (LinearLayout)findViewById(R.id.layout3AreaSub);
       int childCount = threeBtnLayout.getChildCount();
       if(childCount!=count){
           Log.i("THREE AREA","chlid count is not corroct!");
           return;
       }
       for(int i = 0 ; i<childCount;i++){
           threeAreaButtonCollect[i]=(Button)threeBtnLayout.getChildAt(i);
       }
   }



    /**
     * Utils for reconvert All meterView to default text color
     */
   public void reconvertMeterViewTextColor(){
       Iterator<MeterTextView> iterator= meterSet.iterator();
       while(iterator.hasNext()){
           MeterTextView tmp = iterator.next();
           tmp.setDefaultTextColor();
       }
   }
    /**
     * @see This method for meterView
     * @param meterSet
     * @param meterStr
     * @return
     */
    public MeterTextView isHasMeterStr(Set<MeterTextView> meterSet,String meterStr){
        MeterTextView res = null;
        Iterator<MeterTextView> iterator= meterSet.iterator();
        while(iterator.hasNext()){
            MeterTextView tmp = iterator.next();
            if(tmp.getMeterName().equals(meterStr)){
                res = tmp;
                break;
            }
        }
        return res;
    }
    /**
     * @ Dynamic subButton for three Area
     */
    public void initSubArea(){
        LinearLayout linearLayoutThrea =(LinearLayout)findViewById(R.id.layout3AreaSub);
        for(int i =1;i<=count;i++){
            SpecButton b = new SpecButton(this);
//            b.setBackground(getResources().getDrawable(R.drawable.cicle_button,null));
            //b.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
            b.setText(i + "");
            b.setPadding(10,2,2,2);
            b.setId(i);
            if(i>3){
                b.setVisibility(View.INVISIBLE);
            }
            linearLayoutThrea.addView(b);
        }
    }

    @Override
    public void onClick(View v) {
        if(v instanceof SpecButton){
            if(currentThreeBtn==null){
                SpecButton tmp=((SpecButton) v);
                tmp.drawableWithFocus();
                currentThreeBtn=tmp;

                if(fromBtnFlag==null){
                    fromB.setText(tmp.getText());
                    fromB.setBackgroundDrawable(tmp.getBackground());

                    fromBtnFlag =tmp;
                }else if(toBtnFlag==null){
                    toB.setText(tmp.getText());
                    toB.setBackgroundDrawable(tmp.getBackground());
                    toBtnFlag=tmp;
                }
            }else{
                Toast.makeText(FirstArea2Activity.this,"已经选中三区信息！",Toast.LENGTH_SHORT).show();
                return;
            }

        }else if(v instanceof MeterTextView){
            reconvertMeterViewTextColor();
            MeterTextView meterV = (MeterTextView)v;
            meterV.setTextColor(Color.WHITE);
        }else if(v instanceof SecondAreaButton){
            if(model == MODEL_NORMAL){
                if(currentThreeBtn==null){
                    Toast.makeText(FirstArea2Activity.this,"请先选择三区信息！",Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            if(currentSecondBtn==null){
                SecondAreaButton tmp=((SecondAreaButton) v);
                tmp.drawableWithFocus();
                currentSecondBtn=tmp;

                if(fromBtnFlag==null){
                    fromB.setText(tmp.getText());
                    fromB.setBackgroundDrawable(tmp.getBackground());
                    fromBtnFlag =tmp;
                }else if(toBtnFlag==null){
                    toB.setText(tmp.getText());
                    toB.setBackgroundDrawable(tmp.getBackground());
                    toBtnFlag=tmp;
                }
            }else{
                Toast.makeText(FirstArea2Activity.this,"已经选中二区信息！",Toast.LENGTH_SHORT).show();
                return;
            }

        }

    }

    public void changeArea(View v){

        Intent intent = new Intent(FirstArea2Activity.this,FirstAreaActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    /**
     * SUBMIT REQUEST
     * @param v
     */
    public void submit(View v){
        fromB.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
        toB.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
        fromBtnFlag=null;
        toBtnFlag = null;


        currentThreeBtn.changeState(SpecButton.STATE_INIT_3);
        currentSecondBtn.changeState(SecondAreaButton.STATE_FULL_2);
        //private MeterTextView currentMeterLabel;
        currentThreeBtn =null;
        currentSecondBtn=null;
    }

    /**
     * SUBMIT REQUEST
     * @param v
     */
    public void concel(View v){
        fromB.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
        toB.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
    }

    /**
     * SUBMIT REQUEST
     * @param v
     */
    public void reselect(View v){
        fromB.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
        toB.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
        fromBtnFlag=null;
        toBtnFlag = null;
    }

    public void kongpanModel(View v){
        Button btn =(Button)v;
        if(model==MODEL_NORMAL){
            model = MODEL_KONGPAN;
            btn.setBackgroundColor(Color.YELLOW);
        }else if(model == MODEL_KONGPAN){
            model=MODEL_NORMAL;
            btn.setBackgroundColor(Color.BLUE);
        }

    }
}
