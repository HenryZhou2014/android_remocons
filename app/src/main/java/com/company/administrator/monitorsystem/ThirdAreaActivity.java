package com.company.administrator.monitorsystem;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;
import com.henry.store.StoreBean;
import com.henry.view.MeterTextView;
import com.henry.view.OneAreaButton;
import com.henry.view.SecondAreaButton;
import com.henry.view.SpecButton;
import com.henry.webservice.WebServiceUtils;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeConfiguration;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import std_msgs.UInt16;
import wolh_msgs.Order;
import wolh_msgs.RfidTags;
import wolh_msgs.Status;

/**
 * Created by Administrator on 2015/8/2.
 */
public class ThirdAreaActivity extends RosAppActivity implements View.OnClickListener,View.OnLongClickListener{
    /**
     *
     */
    private TopicsSetting topicsSetting = new TopicsSetting();
    private short[] agvToAndroidRfidTags = {0, 0};
    public ThirdAreaActivity() {
        super("rostester", "rostester");
    }
    private Button btn4Pop;
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
    private LinearLayout secondRootLayout ;
    private boolean isSelectedThreeBtn=false;//check three btn isn't chenked
    /**
     * Collection for three Area BUTTONS
     */
    private  SpecButton[] threeAreaButtonCollect = new SpecButton[count];
    private  SecondAreaButton[] secondButtonCollect = new SecondAreaButton[56];
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
    public final static int STATUS_INIT=0;
    public final static int STATUS_IDLE=1;
    public final static int STATUS_WAITING_FOR_CALLING=2;
    public final static int STATUS_GO_TARGET_STATION=3;
    public final static int STATUS_ARRIVE_TARGET_STATION =4;
    public final static int STATUS_DO_EMPTY_TRANSFORM=5;
    public final static int STATUS_EMERGENCY_STOP=11;
    public final static int STATUS_SYSTEM_RESET=12;
    public final static int STATUS_ERROR=13;


    private String robot_name;
    private int status = STATUS_GO_TARGET_STATION;

//    @Override
//    protected void onStart() {
//        super.onStart();
//
//    }


    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.third_area);

        robot_name = getString(R.string.THREE_2_TWO_NAME);

        tatalPageSize = lastPageSize==0?count/pageszie:(count/pageszie+1);

        initThreeButton();
        ininSecondButton();
        btn4Pop=(Button)findViewById(R.id.btn4Pop);
        fromB  =(Button)findViewById(R.id.fromtBtnId);
        toB=(Button)findViewById(R.id.tolBtnId);
        secondRootLayout =(LinearLayout) findViewById(R.id.secLayout);
        threeMeterLayout =(LinearLayout) findViewById(R.id.thirdMeterLayout);


        left =(ImageButton)findViewById(R.id.thirdAreaLft);
        right =(ImageButton)findViewById(R.id.thirdAreaRig);
        reset =(Button)findViewById(R.id.thirdAreaReset);
        left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentIndex==1 ){
                    Toast.makeText(ThirdAreaActivity.this,"前面没有按钮",Toast.LENGTH_LONG).show();
                    return;
                }
                currentIndex--;
                setAllThreeBtnGone();
                for(int j =pageszie*(currentIndex-1); j<(pageszie*currentIndex);j++ ){
                    threeAreaButtonCollect[j].setVisibility(View.VISIBLE);
                }
            }
        });

        right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("THREE AREA", "reset");
                if(currentIndex==tatalPageSize ){
                    Toast.makeText(ThirdAreaActivity.this,"后面没有按钮",Toast.LENGTH_LONG).show();
                    return;
                }
                currentIndex++;
                setAllThreeBtnGone();
                for(int j =pageszie*(currentIndex-1); j<(pageszie*currentIndex);j++ ){
                    try {
                        threeAreaButtonCollect[j].setVisibility(View.VISIBLE);
                    }catch (IndexOutOfBoundsException e){
                        break;
                    }
                }
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(currentThreeBtn!=null){
                    currentThreeBtn.blurState();
                    currentThreeBtn=null;
                    isSelectedThreeBtn=false;
                }
                Toast.makeText(ThirdAreaActivity.this,"成功",Toast.LENGTH_SHORT).show();
            }
        });
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
                j++;
            }
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        switch (requestCode) {
            case SCANNIN_GREQUEST_CODE:
                if(resultCode == RESULT_OK){

                    Bundle bundle = data.getExtras();
                    //显示扫描到的内容
                    //mTextView.setText(bundle.getString("result"));
                    String tmpMeter = bundle.getString("result");
                    Toast.makeText(ThirdAreaActivity.this,tmpMeter,Toast.LENGTH_LONG).show();
                    //显示
                    //mImageView.setImageBitmap((Bitmap) data.getParcelableExtra("bitmap"));

                    //QUERY CODE FROM WEBSERVICE
                    String classMeter = WebServiceUtils.queryOrderByNo(tmpMeter);
                    reconvertMeterViewTextColor();
                    MeterTextView meterLal = isHasMeterStr(meterSet, classMeter);

                    if(meterLal!=null){
                        meterLal.setTextColor(Color.WHITE);
                    }else{
                        meterLal = new MeterTextView(this);
                        meterLal.setText(classMeter);
                        meterLal.setMeterName(classMeter);
                        meterLal.setPadding(10, 0, 2, 0);
                        meterLal.setTextColor(Color.WHITE);
                        threeMeterLayout.addView(meterLal);
                        meterSet.add(meterLal);

                    }

                    currentMeterLabel=meterLal;
                    currentThreeBtn.setMeterCls(currentMeterLabel.getMeterName());

                    currentThreeBtn.changeState(SpecButton.STATE_FULL_3);
                    currentThreeBtn=null;
                    storeData();
                }else{
                    currentThreeBtn.initState();
                    currentThreeBtn=null;
                }
                break;

            default:
                super.onActivityResult(requestCode,resultCode,data);
                break;
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
        clearBtnAnimate();
        if(v instanceof SpecButton){
            if(model == MODEL_NORMAL) {
                if (!isSelectedThreeBtn) {
                    SpecButton tmp = ((SpecButton) v);
                    if (tmp.getState() != tmp.STATE_FULL_3) {
                        Toast.makeText(ThirdAreaActivity.this, "没有选择对应的物料信息，请长按次处扫描物料信息！", Toast.LENGTH_LONG).show();
                        return;
                    }

                    tmp.drawableWithFocus();
                    currentThreeBtn = tmp;

                    if (fromBtnFlag == null) {
                        fromB.setText(tmp.getText());
                        fromB.setBackgroundDrawable(tmp.getBackground());

                        fromBtnFlag = tmp;
                    } else if (toBtnFlag == null) {
                        toB.setText(tmp.getText());
                        toB.setBackgroundDrawable(tmp.getBackground());
                        toBtnFlag = tmp;
                    }
                    isSelectedThreeBtn = true;

                    currentMeterLabel = selectMeter(meterSet, tmp.getMeterCls());

                } else {
                    Toast.makeText(ThirdAreaActivity.this, "已经选中三区信息！", Toast.LENGTH_SHORT).show();
                    return;
                }
            }else{ //EMPTY TRANSFORM
                if (!isSelectedThreeBtn) {
                    SpecButton tmp = ((SpecButton) v);
                    if(fromBtnFlag==null){ // transform 3 to 2 empty
                        if(tmp.getState() != tmp.STATE_INIT_3){
                            Toast.makeText(ThirdAreaActivity.this, "请选择正确状态空盘运输！", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            tmp.drawableWithFocus();
                            currentThreeBtn = tmp;
                            fromBtnFlag = tmp;
                            isSelectedThreeBtn = true;
                            fromB.setText(tmp.getText());
                            fromB.setBackgroundDrawable(tmp.getBackground());
                        }
                    }else if(toBtnFlag == null){
                        if(tmp.getState() != tmp.STATE_NOTHING_3){
                            Toast.makeText(ThirdAreaActivity.this, "请选择正确状态空盘运输！", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            tmp.drawableWithFocus();
                            currentThreeBtn = tmp;
                            toBtnFlag = tmp;
                            isSelectedThreeBtn = true;
                            toB.setText(tmp.getText());
                            toB.setBackgroundDrawable(tmp.getBackground());
                        }
                    }else{
                        Toast.makeText(ThirdAreaActivity.this, "起始位置都已经选择！", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }else if(v instanceof  MeterTextView){
            if(model == MODEL_NORMAL){
                reconvertMeterViewTextColor();
                MeterTextView meterV = (MeterTextView)v;
                meterV.setTextColor(Color.WHITE);

                for(SpecButton tmp:threeAreaButtonCollect){
                    if(tmp.getMeterCls()!=null && tmp.getMeterCls().equals(meterV.getMeterName())){
//                        tmp.setText(tmp.getText()+"\r\n"+meterV.getMeterName());
                        tmp.spark();
                    }else{
                        tmp.setDefaultText();
                    }
                }

                for(SecondAreaButton tmp:secondButtonCollect){
                    if(tmp.getMeterCls()!=null && tmp.getMeterCls().equals(meterV.getMeterName())){
//                        tmp.setText(tmp.getText()+"\r\n"+meterV.getMeterName());
                          tmp.spark();
                    }else{
                        tmp.setDefaultText();
                    }
                }
            }


        }else if(v instanceof  SecondAreaButton){
            if(model == MODEL_NORMAL){
                if(currentThreeBtn==null){
                    Toast.makeText(ThirdAreaActivity.this,"请先选择三区信息！",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            if(model == MODEL_NORMAL) {
                if(currentSecondBtn==null){
                    SecondAreaButton tmp=((SecondAreaButton) v);
                    if(tmp.getState() == tmp.STATE_FULL_2){
                        Toast.makeText(ThirdAreaActivity.this,"此地方已经满，不能选择！",Toast.LENGTH_LONG).show();
                        return;
                    }

                    if(currentMeterLabel==null){
                        Toast.makeText(ThirdAreaActivity.this,"没有选择对应的物料信息，不能选择！",Toast.LENGTH_LONG).show();
                        return;
                    }
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
                    tmp.setMeterCls(currentMeterLabel.getMeterName());
                }else{
                    Toast.makeText(ThirdAreaActivity.this,"已经选中二区信息！",Toast.LENGTH_SHORT).show();
                    return;
                }
            }
            else{
                if (currentSecondBtn==null) {
                    SecondAreaButton tmp = ((SecondAreaButton) v);
                    if(fromBtnFlag==null){ // transform 3 to 2 empty
                        if(tmp.getState() != tmp.STATE_PAN_2){
                            Toast.makeText(ThirdAreaActivity.this, "请选择正确状态空盘运输！", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            tmp.drawableWithFocus();
                            currentSecondBtn = tmp;
                            fromBtnFlag = tmp;
                            fromB.setText(tmp.getText());
                            fromB.setBackgroundDrawable(tmp.getBackground());
                        }
                    }else if(toBtnFlag == null){
                        if(tmp.getState() != tmp.STATE_INIT_2){
                            Toast.makeText(ThirdAreaActivity.this, "请选择正确状态空盘运输！", Toast.LENGTH_SHORT).show();
                            return;
                        }else{
                            tmp.drawableWithFocus();
                            currentSecondBtn = tmp;
                            toBtnFlag = tmp;
                            toB.setText(tmp.getText());
                            toB.setBackgroundDrawable(tmp.getBackground());
                        }
                    }else{
                        Toast.makeText(ThirdAreaActivity.this, "起始位置都已经选择！", Toast.LENGTH_SHORT).show();
                    }
                }
            }

        }

    }

    @Override
    public boolean onLongClick(View v) {
        if(v instanceof SpecButton) {
//            if(currentThreeBtn!=null){  //keep one buton can scan code  onetime
//                Toast.makeText(ThirdAreaActivity.this,"三区已经有一个选择",Toast.LENGTH_SHORT).show();
//                return false;
//            }
            SpecButton tmp=((SpecButton) v);
            //tmp.drawableWithFocus();
            if(tmp.getState() != tmp.STATE_INIT_3){
                Toast.makeText(ThirdAreaActivity.this,"此状态下无法扫描二维码",Toast.LENGTH_SHORT).show();
                return false;
            }

            Intent intent = new Intent();
            intent.setClass(ThirdAreaActivity.this, MipcaActivityCapture.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivityForResult(intent, SCANNIN_GREQUEST_CODE);

           currentThreeBtn=tmp;
        }
        return false;
    }

    public void secondLft(View v){
        showSecondAreaPage1();
        findViewById(R.id.secondRigArrowid).setVisibility(View.VISIBLE);
        findViewById(R.id.secondLftArrowid).setVisibility(View.GONE);
    }

    public void secondRig(View v){
        showSecondAreaPage2();
        findViewById(R.id.secondRigArrowid).setVisibility(View.GONE);
        findViewById(R.id.secondLftArrowid).setVisibility(View.VISIBLE);
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
     * SUBMIT REQUEST
     * @param v
     */
    public void submit(View v){
        heartBreat();
//        changeFromRev("AGV_1",20101,30101,"Meter",3);
        if(fromBtnFlag==null ||toBtnFlag==null){
            Log.i("TEST","请选择请求和目的地址");
            Toast.makeText(ThirdAreaActivity.this, "请选择请求和目的地址", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            int fromId = Integer.valueOf(fromBtnFlag.getTag().toString().substring(1));
            int toId = Integer.valueOf(toBtnFlag.getTag().toString().substring(1));

            //TODO  add send msg method
            Log.i("TEST","请求:"+fromId +" 目的:"+toId);
            Toast.makeText(ThirdAreaActivity.this, "请求:"+fromId +" 目的:"+toId, Toast.LENGTH_LONG).show();




            Order orderTalker = topicsSetting.orderTalkerPublisher.newMessage();
            orderTalker.setRobotName(robot_name);
            orderTalker.setStartId(fromId);
            orderTalker.setTargetId(toId);
            orderTalker.setStatus(status);
            // 定义orderTalker消息类型为Order，Order消息类型中包含了四个信息，
            // 其中包括String格式的RobotName，Int格式的start_id，target_id和status；
            // 定义之后对orderTalker内的信息分别进行赋值。

            topicsSetting.orderTalkerPublisher.publish(orderTalker);
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


        if(model == MODEL_NORMAL){
            //currentSecondBtn.changeState(SecondAreaButton.STATE_FULL_2);
            //currentThreeBtn.nextState();
            //currentThreeBtn.setMeterCls("");
        }else{
            if(fromBtnFlag instanceof SecondAreaButton){
                currentSecondBtn.changeState(SecondAreaButton.STATE_INIT_2);
            }else if(toBtnFlag instanceof SecondAreaButton){
                currentSecondBtn.changeState(SecondAreaButton.STATE_PAN_2);
            }

            if(fromBtnFlag instanceof  SpecButton){
                currentThreeBtn.changeState(SpecButton.STATE_NOTHING_3);
            }else if(toBtnFlag instanceof SpecButton){
                currentThreeBtn.changeState(SpecButton.STATE_INIT_3);
            }
        }

        //currentSecondBtn.setMeterCls("");
        //private MeterTextView currentMeterLabel;
        currentThreeBtn =null;
        currentSecondBtn=null;
        isSelectedThreeBtn=false;









    }

    /**
     * SUBMIT REQUEST
     * @param v
     */
    public void reset(View v){
        Status statusTalker = topicsSetting.statusTalkerPublisher.newMessage();
        statusTalker.setSysStatus(10);
        topicsSetting.statusTalkerPublisher.publish(statusTalker);
        //将消息通过Publisher发送出去。
        Toast.makeText(ThirdAreaActivity.this,"重置平板消息已发出",Toast.LENGTH_LONG).show();
    }

    /**
     * SUBMIT REQUEST
     * @param v
     */
    public void reselect(View v){
        fromB.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));
        toB.setBackgroundDrawable(getResources().getDrawable(R.drawable.cicle_button));

        isSelectedThreeBtn=false;
        fromB.setText("");
        toB.setText("");
        fromBtnFlag=null;
        toBtnFlag = null;
        if(currentSecondBtn!=null)currentSecondBtn.drawableWithBlur();
        if(currentThreeBtn!=null)currentThreeBtn.drawableWithBlur();
        currentSecondBtn=null;
        currentThreeBtn=null;
    }

    public void kongpanModel(View v){
        Button btn =(Button)v;
        if(model==MODEL_NORMAL){
            model = MODEL_KONGPAN;
            btn.setBackgroundColor(Color.YELLOW);
            robot_name=getString(R.string.THREE_2_TWO_EMPTY_NAME);
            status=STATUS_DO_EMPTY_TRANSFORM;
        }else if(model == MODEL_KONGPAN){
            model=MODEL_NORMAL;
            btn.setBackgroundColor(Color.BLUE);
            robot_name=getString(R.string.THREE_2_TWO_NAME);
            status=STATUS_GO_TARGET_STATION;
        }

    }




    public class TopicsSetting extends AbstractNodeMain {

        public Publisher<Order> orderTalkerPublisher; //发送消息
        public Subscriber<Order> orderListenerSubscriber;//接收消息

        public Publisher<Status> statusTalkerPublisher; //发送消息
        //分别定义了一个Publisher(向ROS系统发布消息的单元)和一个Subscriber(接受自ROS系统发来的消息的单元)
        public Subscriber<RfidTags> rfidTagsListenerSubscriber;//接收消息
        public Subscriber<Status> statusListenerSubscriber;//接收消息
        public Subscriber<UInt16> heartBeatListenerSubscriber;//接收消息

        public GraphName getDefaultNodeName()
        {
            return GraphName.of("rostester");
        }
        public void onStart(final ConnectedNode connectedNode)
        {
            statusTalkerPublisher = connectedNode.newPublisher(getString(R.string.path_status_pub), Status._TYPE);
            orderTalkerPublisher = connectedNode.newPublisher(getString(R.string.path_sender), Order._TYPE);
            orderListenerSubscriber = connectedNode.newSubscriber(getString(R.string.path_reader), Order._TYPE);
            rfidTagsListenerSubscriber  = connectedNode.newSubscriber(getString(R.string.path_tags), RfidTags._TYPE);
            statusListenerSubscriber  = connectedNode.newSubscriber(getString(R.string.path_status), Status._TYPE);
            heartBeatListenerSubscriber  = connectedNode.newSubscriber(getString(R.string.path_heartbeat), UInt16._TYPE);
            //分别为Publisher 和Subscriber定义了消息发布及接收的路径(R.string.topic_talker / R.string.topic_listener)，
            //每个消息都有对应的路径，路径设置相同的Publisher和Subscriber就可以互相收发消息。

            /************* 建立一个消息接收器，并且将通过回调Handler执行相关动作 **********/
            orderListenerSubscriber.addMessageListener(new MessageListener<Order>()
            {
                @Override
                public void onNewMessage(Order order)
                {
                    Log.i("TTTTTTTTTTTTTTT", order.getRobotName());
                    upUIHandle.post(new UpdateUI(order));
                }
            });

            rfidTagsListenerSubscriber.addMessageListener(new MessageListener<RfidTags>()
            {
                @Override
                public void onNewMessage(RfidTags rfidTags)
                {
                    agvToAndroidRfidTags = rfidTags.getRfidTags();
//                    Toast.makeText(ThirdAreaActivity.this, agvToAndroidRfidTags[0] + " RfidTags", Toast.LENGTH_LONG).show();
                }
            });

            statusListenerSubscriber.addMessageListener(new MessageListener<Status>()
            {
                @Override
                public void onNewMessage(Status status)
                {
                    if(status.getSysStatus() == 10){ //10 重置所有平板
                        resetAllUIHandle.post(new ResetAllUI(status));
                    }
//
                }
            });

            heartBeatListenerSubscriber.addMessageListener(new MessageListener<UInt16>()
            {
                @Override
                public void onNewMessage(UInt16 status)
                {
                        heartUIHandle.post(new HeartBeatUI());
                }
            });
        }
    };

   final Handler upUIHandle=new Handler();
    final Handler heartUIHandle=new Handler();
    final Handler resetAllUIHandle=new Handler();

    class HeartBeatUI implements Runnable{
        public HeartBeatUI(){};
        UInt16 status;
        public HeartBeatUI(UInt16 status){
            this.status = status;
        };
        public void run(){
//            Looper.prepare();
//            Toast.makeText(ThirdAreaActivity.this," heartBeat:"+status.getData(),Toast.LENGTH_SHORT).show();
//            Looper.loop();;
//            if(status.getData()==99)
            Log.i("hearBeat......" ,status==null?"status empty":status.getData()+"");
            heartBreat();
        }
    }
    class ResetAllUI implements Runnable{
        Status status;
        public ResetAllUI(){};
        public ResetAllUI(Status status){
            this.status = status;
        }
        public void run(){
            for(SpecButton threeB:threeAreaButtonCollect){
                if(threeB!=null){
                    threeB.changeState(threeB.STATE_INIT_3);
                    threeB.setMeterCls("");
                }
            }
            for(SecondAreaButton secondB :secondButtonCollect ){
                if(secondB!=null){
                    secondB.changeState(secondB.STATE_INIT_2);
                    secondB.setMeterCls("");
                }
            }
           Iterator it =  meterSet.iterator();
            while(it.hasNext()){

            }
        }
    }

   class UpdateUI implements Runnable{
       Order order;
       public UpdateUI(){};
       public UpdateUI(Order order){
           this.order = order;
       }
       public void run(){
           changeFromRev(this.order.getRobotName(), this.order.getTargetId(), this.order.getStartId(), this.order.getMaterial(), this.order.getStatus());

       }
   }

    public void changeFromRev(String type,int targetid,int startid,String meter,int status){
        LinearLayout rootLay= (LinearLayout)findViewById(R.id.thirdRootid);
        String start = "b0"+startid;
        String target= "b0"+targetid;
        if("AGV_1".equals(type)){  //3 to 2
            if(status == 3){ //normal transform
                if(start.substring(2,3).equals("3") && target.substring(2,3).equals("2")){
                    SpecButton thireB= (SpecButton)rootLay.findViewWithTag(start);
                    SecondAreaButton secondB= (SecondAreaButton)rootLay.findViewWithTag(target);
                    thireB.setMeterCls(meter);
                    thireB.changeState(thireB.STATE_NOTHING_3);
                    secondB.setMeterCls(meter);
                    secondB.changeState(secondB.STATE_FULL_2);
                }else{
                    Toast.makeText(ThirdAreaActivity.this, "接收广播AGV_1起始点信息不正确 targetid："+targetid +" startid:"+startid, Toast.LENGTH_SHORT).show();
                }
            }else if(status == 5) {//kong pan
                if(start.substring(2,3).equals("3") && target.substring(2,3).equals("2")){
                    SpecButton thireB= (SpecButton)rootLay.findViewWithTag(start);
                    SecondAreaButton secondB= (SecondAreaButton)rootLay.findViewWithTag(target);
                    thireB.changeState(thireB.STATE_NOTHING_3);
                    secondB.changeState(secondB.STATE_PAN_2);
                }else if(start.substring(2,3).equals("2") && target.substring(2,3).equals("3")){
                    SpecButton thireB= (SpecButton)rootLay.findViewWithTag(target);
                    SecondAreaButton secondB= (SecondAreaButton)rootLay.findViewWithTag(start);
                    thireB.changeState(thireB.STATE_INIT_3);
                    secondB.changeState(secondB.STATE_INIT_2);
                }
                else{
                    Toast.makeText(ThirdAreaActivity.this, "接收空盘广播AGV_1起始点信息不正确 targetid："+targetid +" startid:"+startid, Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(ThirdAreaActivity.this, "接收广播AGV_1状态不正确 status："+status, Toast.LENGTH_SHORT).show();
            }

        }else if("AGV_2".equals(type)){ //2 to 1
            if(start.substring(2,3).equals("2") && target.substring(2,3).equals("1")){
                OneAreaButton oneB= (OneAreaButton)rootLay.findViewWithTag(target);
                SecondAreaButton secondB= (SecondAreaButton)rootLay.findViewWithTag(start);
                oneB.changeState(oneB.STATE_FULL_1);
                secondB.changeState(secondB.STATE_PAN_2);
            }else if(start.substring(2,3).equals("1") && target.substring(2,3).equals("3")){
                SpecButton thireB= (SpecButton)rootLay.findViewWithTag(target);
                OneAreaButton oneB= (OneAreaButton)rootLay.findViewWithTag(start);
                thireB.changeState(thireB.STATE_FULL_3);
                oneB.changeState(oneB.STATE_INIT_1);
            }
            else{
                Toast.makeText(ThirdAreaActivity.this, "接收空盘广播AGV_2起始点信息不正确 targetid："+targetid +" startid:"+startid, Toast.LENGTH_SHORT).show();
            }
        }
        else{

        }
    }

    protected void init(NodeMainExecutor nodeMainExecutor)                                                   // talker
    {
        try
        {
            java.net.Socket socket = new java.net.Socket(getMasterUri().getHost(), getMasterUri().getPort());
            java.net.InetAddress local_network_address = socket.getLocalAddress();
            socket.close();
            NodeConfiguration nodeConfiguration =
                    NodeConfiguration.newPublic(local_network_address.getHostAddress(), getMasterUri());
            nodeConfiguration.setNodeName("rostester");
            Log.e("Talker", "master uri [" + getMasterUri() + "]");
            nodeMainExecutor.execute(topicsSetting, nodeConfiguration);
        }
        catch (IOException e) {
        }
    }

    public void clearBtnAnimate(){
        for(SpecButton tmp:threeAreaButtonCollect){
            tmp.stopSpark();
        }

        for(SecondAreaButton tmp:secondButtonCollect){
            tmp.stopSpark();
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
            FileOutputStream fos = openFileOutput("message.dat",
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
            FileInputStream fout =  openFileInput("message.dat");
            ObjectInputStream objectInputStream = new ObjectInputStream(fout);
            Object obj = objectInputStream.readObject();
            if(obj!=null){
                StoreBean storeBean = (StoreBean)obj;
                String[] threeMeter =storeBean.getThreeMeter();
                int[] threeStatus = storeBean.getThreeStatus();
                String[] secondMeter = storeBean.getTwoMeter();
                int[] secondStatus = storeBean.getTwoStatus();
                String[] meters= storeBean.getMeters();
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

    public void heartBreat(){
        Button heart= (Button)findViewById(R.id.heatbeatid);
        final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
        animation.setDuration(300); // duration - half a second
        animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
        animation.setRepeatCount(0); // Repeat animation infinitely
        animation.setRepeatMode(Animation.REVERSE); // Reverse animation at the end so the button will fade back in
        heart.startAnimation(animation);
    }
    public void popAlert(String mgs){
        View contentView = LayoutInflater.from(this).inflate(
                R.layout.cosmtor_popwindow, null);

        final PopupWindow popupWindow = new PopupWindow(contentView, ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT, true);

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
