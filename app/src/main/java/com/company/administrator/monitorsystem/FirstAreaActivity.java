package com.company.administrator.monitorsystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.henry.store.StoreBean;
import com.henry.view.MeterTextView;
import com.henry.view.OneAreaButton;
import com.henry.view.SecondAreaButton;
import com.henry.view.SpecButton;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import wolh_msgs.Order;

/**
 * Created by Administrator on 2015/8/2.
 */
public class FirstAreaActivity extends Activity implements View.OnClickListener,View.OnLongClickListener{
    private final static int SCANNIN_GREQUEST_CODE = 1;
    //Total of three area Button
    private int count = 14;
    //every pagesize
    private int pageszie=13;

    private Button btn4Pop;
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
    private LinearLayout secondRootLayout ;
    private boolean isDefaultPanel=true;//Default panel  true is 2 to 1 ,other false  is 1 to 3

    /**
     * Collection for three Area BUTTONS
     */
    private  SpecButton[] threeAreaButtonCollect = new SpecButton[17];
    private  OneAreaButton[] firstButtonCollect = new OneAreaButton[count];
    private  SecondAreaButton[] secondButtonCollect = new SecondAreaButton[56];
    private Set<MeterTextView> meterSet = new HashSet<MeterTextView>();

    //Current status
    private SpecButton currentThreeBtn;
    private SecondAreaButton currentSecondBtn;
    private OneAreaButton currentFirstBtn;
    private MeterTextView currentMeterLabel;

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
        setContentView(R.layout.first_area);

        btn4Pop=(Button)findViewById(R.id.btn4Pop);
        initThreeButton();

        fromB  =(Button)findViewById(R.id.fromtBtnId);
        toB=(Button)findViewById(R.id.tolBtnId);

        //SpecButton specButton =(SpecButton)findViewById(R.id.treeB1);

        secondRootLayout =(LinearLayout) findViewById(R.id.secLayout);
        threeMeterLayout =(LinearLayout) findViewById(R.id.thirdMeterLayout);

        reset =(Button)findViewById(R.id.thirdAreaReset);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDefaultPanel) {
                    toB.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
                    toB.setText("");
                    toBtnFlag = null;
                } else {
                    fromBtnFlag = null;
                    fromB.setText("");
                    fromB.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
                }

                if (currentFirstBtn != null) currentFirstBtn.blurState();
                currentFirstBtn = null;

                Toast.makeText(FirstAreaActivity.this, "成功", Toast.LENGTH_SHORT).show();
            }
        });

        initOneButton();
        ininSecondButton();
        initThreeButton();


    }


    public void initOneButton(){
        LinearLayout threeBtnLayout= (LinearLayout)findViewById(R.id.layout1AreaSub);
        int childCount = threeBtnLayout.getChildCount();
        if(childCount!=count){
            Log.i("THREE AREA","chlid count is not corroct!");
            return;
        }
        for(int i = 0 ; i<childCount;i++){
            firstButtonCollect[i]=(OneAreaButton)threeBtnLayout.getChildAt(i);

//FOR TEST
if(i%2==0){
    firstButtonCollect[i].changeState(OneAreaButton.STATE_FULL_1);
}else{
    firstButtonCollect[i].changeState(OneAreaButton.STATE_INIT_1);
}

        }
    }



   public void initThreeButton(){
       LinearLayout threeBtnLayout= (LinearLayout)findViewById(R.id.subThreeAreaLay1);
       int childCount = threeBtnLayout.getChildCount();
       if(childCount!=10){
           Log.i("ONE AREA","three chlid count is not corroct!");
           return;
       }
       for(int i = 0 ; i<10;i++){
           threeAreaButtonCollect[i]=(SpecButton)threeBtnLayout.getChildAt(i);
   //FOR　TEST
   if(i%2==0){
       threeAreaButtonCollect[i].changeState(SpecButton.STATE_INIT_3);
   }else{
       threeAreaButtonCollect[i].changeState(SpecButton.STATE_NOTHING_3);
   }
       }

       threeBtnLayout= (LinearLayout)findViewById(R.id.subThreeAreaLay2);
        childCount = threeBtnLayout.getChildCount();
       if(childCount!=7){
           Log.i("ONE AREA","three chlid count is not corroct!");
           return;
       }
       for(int i = 10 ; i<17;i++){
           threeAreaButtonCollect[i]=(SpecButton)threeBtnLayout.getChildAt(i);
       }
   }

    public void ininSecondButton(){

        LinearLayout secBtnLayout=(LinearLayout)findViewById(R.id.secLayout);
        ;
        Log.i("TEST",secBtnLayout.getChildCount()+"");
        int childCount = secBtnLayout.getChildCount();
        int j=0;
        for(int i = 0 ; i<childCount;i++){
            LinearLayout subLayout = (LinearLayout)secBtnLayout.getChildAt(i);
            for(int h = 0;h<subLayout.getChildCount();h++){
                Button tmpView = (Button)subLayout.getChildAt(h);
                secondButtonCollect[j]=(SecondAreaButton)tmpView;
                secondButtonCollect[j].changeState(SecondAreaButton.STATE_FULL_2);
                j++;
            }
        }
    }

    public void showSecondAreaPage1(){
        int count = secondRootLayout.getChildCount();
        for(int i =0;i<count;i++){
            View child = secondRootLayout.getChildAt(i);
            if(child.getTag().equals("page1")){
                child.setVisibility(View.VISIBLE);
            }else{
                child.setVisibility(View.GONE);
            }
        }
    }

    public void showSecondAreaPage2(){
        int count = secondRootLayout.getChildCount();
        for(int i =0;i<count;i++){
            View child = secondRootLayout.getChildAt(i);
            if(child.getTag().equals("page2")){
                child.setVisibility(View.VISIBLE);
            }else{
                child.setVisibility(View.GONE);
            }
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

    public MeterTextView selectMeter(Set<MeterTextView> meterSet,String meterStr){
        MeterTextView res = null;
        Iterator<MeterTextView> iterator= meterSet.iterator();
        while(iterator.hasNext()){
            MeterTextView tmp = iterator.next();
            if(tmp.getMeterName().equals(meterStr)){
                res = tmp;
                break;
            }
        }
        if(res!=null){
            reconvertMeterViewTextColor();
            res.setTextColor(Color.WHITE);
        }
        return res;
    }

    @Override
    public void onClick(View v) {
        clearBtnAnimate();
        if(v instanceof SpecButton){

            if(isDefaultPanel){
                Toast.makeText(FirstAreaActivity.this,"此模式无法选中三区信息！",Toast.LENGTH_SHORT).show();
                return;
            }

            if(((SpecButton)v).getState() != SpecButton.STATE_NOTHING_3){
                Toast.makeText(FirstAreaActivity.this,"此状态无法选择！",Toast.LENGTH_SHORT).show();
                return;
            }
            if(currentThreeBtn==null){
                if(fromBtnFlag==null){
                    Toast.makeText(FirstAreaActivity.this,"请先选择一区信息！",Toast.LENGTH_SHORT).show();
                    return;
                }
                SpecButton tmp=((SpecButton) v);
                tmp.drawableWithFocus();
                currentThreeBtn=tmp;
                toB.setText(tmp.getText());
                toB.setBackgroundDrawable(tmp.getBackground());
                toBtnFlag=tmp;

            }else{
                Toast.makeText(FirstAreaActivity.this,"已经选中三区信息！",Toast.LENGTH_SHORT).show();
                return;
            }

        }else if(v instanceof  MeterTextView){
            if(isDefaultPanel){
                reconvertMeterViewTextColor();
                MeterTextView meterV = (MeterTextView)v;
                meterV.setTextColor(Color.WHITE);

                for(OneAreaButton tmp:firstButtonCollect){
                    if(tmp.getMeterCls()!=null && tmp.getMeterCls().equals(meterV.getMeterName())){
                        tmp.spark();
                    }
                }

                for(SecondAreaButton tmp:secondButtonCollect){
                    if(tmp.getMeterCls()!=null && tmp.getMeterCls().equals(meterV.getMeterName())){
                        tmp.spark();
                    }
                }
            }
        }else if(v instanceof  SecondAreaButton){
            if(!isDefaultPanel){
                    Toast.makeText(FirstAreaActivity.this,"此模式无法选中二区信息！",Toast.LENGTH_SHORT).show();
                    return;
            }
            if(((SecondAreaButton)v).getState() != SecondAreaButton.STATE_FULL_2){
                Toast.makeText(FirstAreaActivity.this,"此状态无法选择！",Toast.LENGTH_SHORT).show();
                return;
            }
            if(currentSecondBtn==null){
                SecondAreaButton tmp=((SecondAreaButton) v);
                tmp.drawableWithFocus();
                currentSecondBtn=tmp;

                fromB.setText(tmp.getText());
                fromB.setBackgroundDrawable(tmp.getBackground());
                fromBtnFlag =tmp;

            }else{
                Toast.makeText(FirstAreaActivity.this,"已经选中二区信息！",Toast.LENGTH_SHORT).show();
                return;
            }

        }else if(v instanceof  OneAreaButton){

            if(currentFirstBtn !=null){
                Toast.makeText(FirstAreaActivity.this,"已经选择一区信息！",Toast.LENGTH_SHORT).show();
                return;
            }

            if(isDefaultPanel){
                if(((OneAreaButton)v).getState() != OneAreaButton.STATE_INIT_1){
                    Toast.makeText(FirstAreaActivity.this,"此状态无法选择！",Toast.LENGTH_SHORT).show();
                    return;
                }
                if(fromBtnFlag==null){
                    Toast.makeText(FirstAreaActivity.this,"请先选择二区信息！",Toast.LENGTH_SHORT).show();
                    return;
                }
                OneAreaButton tmp=((OneAreaButton) v);
                tmp.drawableWithFocus();
                currentFirstBtn= tmp;
                toB.setText(tmp.getText());
                toB.setBackgroundDrawable(tmp.getBackground());
                toBtnFlag=tmp;
            }else{
                if(((OneAreaButton)v).getState() != OneAreaButton.STATE_FULL_1){
                    Toast.makeText(FirstAreaActivity.this,"此状态无法选择！",Toast.LENGTH_SHORT).show();
                    return;
                }

                OneAreaButton tmp=((OneAreaButton) v);
                tmp.drawableWithFocus();
                currentFirstBtn= tmp;
                fromB.setText(tmp.getText());
                fromB.setBackgroundDrawable(tmp.getBackground());
                fromBtnFlag=tmp;
            }
        }

    }

    public void clearBtnAnimate(){
        for(SpecButton tmp:threeAreaButtonCollect){
           if(tmp!=null) tmp.stopSpark();
        }

        for(SecondAreaButton tmp:secondButtonCollect){
            if(tmp!=null) tmp.stopSpark();
        }
        for(OneAreaButton tmp:firstButtonCollect){
            if(tmp!=null) tmp.stopSpark();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        reconverData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storeData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        storeData();
    }

    public void storeData(){
        try {
            // 步骤2:创建一个FileOutputStream对象,MODE_APPEND追加模式
            FileOutputStream fos = openFileOutput("message2.dat",
                    MODE_PRIVATE);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fos);
            // 步骤3：将获取过来的值放入文件
//            fos.write(msg.getBytes());
            StoreBean storeBean = new StoreBean();


            Set<String> meterStrSet = new HashSet<String>();

            int secondLen = secondButtonCollect.length;
            String[] secondMeter = new String[secondLen];
            int[] secondStatus = new int[secondLen];
            for(int i=0;i<secondLen;i++){
                secondMeter[i] = secondButtonCollect[i].getMeterCls();
                if(secondMeter[i]!=null && !secondMeter[i].equals("")){
                    meterStrSet.add(secondMeter[i]);
                }
                int tmpState = secondButtonCollect[i].getState();
                if(tmpState%2==0 && tmpState>0){
                    tmpState--;
                }
                secondStatus[i] = tmpState;
            }
            storeBean.setTwoMeter(secondMeter);
            storeBean.setTwoStatus(secondStatus);


            int threeLen = threeAreaButtonCollect.length;
            String[] threeMeter = new String[threeLen];
            int[] threeStatus = new int[threeLen];
            for(int i=0;i<threeLen;i++){
                threeMeter[i] = threeAreaButtonCollect[i].getMeterCls();
                if(threeMeter[i]!=null && !threeMeter[i].equals("")){
                    meterStrSet.add(threeMeter[i]);
                }

                int tmpState = threeAreaButtonCollect[i].getState();
                if(tmpState%2==0 && tmpState>0){
                    tmpState--;
                }
                threeStatus[i] = tmpState;
            }
            storeBean.setThreeMeter(threeMeter);
            storeBean.setThreeStatus(threeStatus);


            int oneLen = firstButtonCollect.length;
            String[] oneMeter = new String[oneLen];
            int[] oneStatus = new int[oneLen];
            for(int i=0;i<oneLen;i++){
                oneMeter[i] = firstButtonCollect[i].getMeterCls();
                if(oneMeter[i]!=null && !oneMeter[i].equals("")){
                    meterStrSet.add(oneMeter[i]);
                }

                int tmpState = firstButtonCollect[i].getState();
                if(tmpState%2==0 && tmpState>0){
                    tmpState--;
                }
                oneStatus[i] = tmpState;
            }
            storeBean.setOneMeter(oneMeter);
            storeBean.setOneStatus(oneStatus);

            int meterLen = meterStrSet.size();
            String[] meters =new String[meterLen];
            Iterator<String>  iterator = meterStrSet.iterator();
            int tmpCount =0;
            while(iterator.hasNext())
            {
                meters[tmpCount]=iterator.next();
                tmpCount++;
            }

            storeBean.setMeters(meters);

            objectOutputStream.writeObject(storeBean);
            objectOutputStream.flush();
            // 步骤4：关闭数据流
            fos.close();
            objectOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void reconverData(){
        try {
            FileInputStream fout =  openFileInput("message2.dat");
            ObjectInputStream objectInputStream = new ObjectInputStream(fout);
            Object obj = objectInputStream.readObject();
            if(obj!=null){
                StoreBean storeBean = (StoreBean)obj;
                String[] threeMeter =storeBean.getThreeMeter();
                int[] threeStatus = storeBean.getThreeStatus();
                String[] secondMeter = storeBean.getTwoMeter();
                int[] secondStatus = storeBean.getTwoStatus();
                String[] meters= storeBean.getMeters();

                int[] oneStatus = storeBean.getOneStatus();
                String[] oneMeter = storeBean.getOneMeter();


                for(int j = 0 ; j<firstButtonCollect.length && oneMeter.length==oneStatus.length&&
                        oneMeter.length==firstButtonCollect.length;j++){
                    firstButtonCollect[j].changeState(oneStatus[j]);
                    firstButtonCollect[j].setMeterCls(oneMeter[j]);
                }

                for(int j = 0 ; j<threeAreaButtonCollect.length && threeMeter.length==threeStatus.length&&
                        threeMeter.length==threeAreaButtonCollect.length;j++){
                    threeAreaButtonCollect[j].changeState(threeStatus[j]);
                    threeAreaButtonCollect[j].setMeterCls(threeMeter[j]);
                }

                for(int j = 0 ; j<secondButtonCollect.length && secondMeter.length==secondStatus.length&&
                        secondMeter.length==secondButtonCollect.length;j++){
                    secondButtonCollect[j].changeState(secondStatus[j]);
                    secondButtonCollect[j].setMeterCls(secondMeter[j]);
                }

                for(int j =0;j<meters.length;j++) {
                    if( isHasMeterStr(meterSet, meters[j])==null){
                        MeterTextView meterLal = new MeterTextView(this);
                        meterLal.setText(meters[j]);
                        meterLal.setMeterName(meters[j]);
                        meterLal.setPadding(10, 0, 2, 0);
                        //meterLal.setTextColor(Color.WHITE);
                        threeMeterLayout.addView(meterLal);
                        meterSet.add(meterLal);
                    }

                }
            }

            if(fout!=null)fout.close();
            if(objectInputStream!=null)objectInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /** change panel **/
    public void changeArea(View v){

//        Intent intent = new Intent(FirstAreaActivity.this,FirstArea2Activity.class);
//        startActivity(intent);
//        this.finish();
        FrameLayout frameLayout1 = (FrameLayout)findViewById(R.id.fragmentContinerid);
        FrameLayout frameLayout2 = (FrameLayout)findViewById(R.id.fragmentContinerid2);
        TextView textView = (TextView)findViewById(R.id.subareatitle);
        if(isDefaultPanel){
            frameLayout1.setVisibility(View.GONE);
            frameLayout2.setVisibility(View.VISIBLE);
            isDefaultPanel=false;
            ((Button)v).setText(getString(R.string.back));
            textView.setText(getString(R.string.thirdAreaTitle));
        }else{
            frameLayout1.setVisibility(View.VISIBLE);
            frameLayout2.setVisibility(View.GONE);
            isDefaultPanel=true;
            ((Button)v).setText(getString(R.string.changeToTherrArea));
            textView.setText(getString(R.string.secondAreaTitle));
        }
        popAlert("电量低警告！");
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
        showSecondAreaPage2();
        if(fromBtnFlag==null ||toBtnFlag==null){
            Log.i("TEST","请选择请求和目的地址");
            Toast.makeText(FirstAreaActivity.this, "请选择请求和目的地址", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            int fromId = Integer.valueOf(fromBtnFlag.getTag().toString().substring(1));
            int toId = Integer.valueOf(toBtnFlag.getTag().toString().substring(1));

            //TODO  add send msg method
            Log.i("TEST","请求:"+fromId +" 目的:"+toId);
            Toast.makeText(FirstAreaActivity.this, "请求:"+fromId +" 目的:"+toId, Toast.LENGTH_LONG).show();




//            Order orderTalker = topicsSetting.orderTalkerPublisher.newMessage();
//            orderTalker.setRobotName(robot_name);
//            orderTalker.setStartId(fromId);
//            orderTalker.setTargetId(toId);
//            orderTalker.setStatus(status);
            // 定义orderTalker消息类型为Order，Order消息类型中包含了四个信息，
            // 其中包括String格式的RobotName，Int格式的start_id，target_id和status；
            // 定义之后对orderTalker内的信息分别进行赋值。

//            topicsSetting.orderTalkerPublisher.publish(orderTalker);
            //将消息通过Publisher发送出去。
        }catch (NullPointerException e){
            Log.i("TEST",e.getLocalizedMessage());
        }catch (NumberFormatException e){
            Log.i("TEST",e.getLocalizedMessage());
        }finally {

        }
        fromB.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
        toB.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
        fromBtnFlag=null;
        toBtnFlag = null;
        fromB.setText("");
        toB.setText("");

        if(isDefaultPanel){
            currentSecondBtn.changeState(SecondAreaButton.STATE_INIT_2);
            currentFirstBtn.changeState(OneAreaButton.STATE_FULL_1);
        }else{
            currentFirstBtn.changeState(OneAreaButton.STATE_INIT_1);
            currentThreeBtn.changeState(SpecButton.STATE_INIT_3);
        }

        //private MeterTextView currentMeterLabel;
        currentThreeBtn =null;
        currentSecondBtn=null;
        currentFirstBtn=null;
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
        fromB.setText("");
        toB.setText("");
        fromBtnFlag=null;
        toBtnFlag = null;
        if(currentFirstBtn!=null)currentFirstBtn.blurState();
        if(currentSecondBtn!=null)currentSecondBtn.blurState();
        if(currentThreeBtn!=null)currentThreeBtn.blurState();

        currentFirstBtn=null;
        currentSecondBtn=null;
        currentThreeBtn=null;
    }


    public void popAlert(String mgs){
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.cosmtor_popwindow, null);

            final PopupWindow popupWindow = new PopupWindow(contentView,ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, true);

//        popupWindow.setTouchable(true);
//        popupWindow.setTouchInterceptor(new OnTouchListener() {
//
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                Log.i("mengdd", "onTouch : ");
//
//                return false;
//                // 这里如果返回true的话，touch事件将被拦截
//                // 拦截后 PopupWindow的onTouchEvent不被调用，这样点击外部区域无法dismiss
//            }
//        });
//        popupWindow.setBackgroundDrawable(getResources().getDrawable(Color.RED));
        Button hiddenBtn = (Button)popupWindow.getContentView().findViewById(R.id.hiddenPopBtn);
        TextView messagePop = (TextView)popupWindow.getContentView().findViewById(R.id.messagePop);
        hiddenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });
        popupWindow.showAtLocation(btn4Pop, Gravity.CENTER, 0, 0);
        messagePop.setText(mgs);
    }

}
