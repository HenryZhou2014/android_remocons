package com.company.administrator.monitorsystem;

import android.app.Activity;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.github.rosjava.android_remocons.common_tools.apps.RosAppActivity;

import org.ros.message.MessageListener;
import org.ros.namespace.GraphName;
import org.ros.node.AbstractNodeMain;
import org.ros.node.NodeConfiguration;
import org.ros.node.ConnectedNode;
import org.ros.node.NodeMainExecutor;
import org.ros.node.topic.Publisher;
import org.ros.node.topic.Subscriber;

import java.io.IOException;

import wolh_msgs.*;
import std_msgs.*;
import std_msgs.String;

public class MainActivity extends RosAppActivity implements View.OnClickListener,View.OnLongClickListener{

    private TopicsSetting topicsSetting = new TopicsSetting();
    private ImageButton mImageButtonTalker;
    private short[] agvToAndroidRfidTags = {0, 0};
    private TextView textViewListener;

    private java.lang.String robot_name = "walle";
    private int start_id = 301;
    private int target_id = 201;
    private int status = 51;

    public MainActivity() {
        super("rostester", "rostester");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        mImageButtonTalker = (ImageButton)findViewById(R.id.imageButtonTalker);
        textViewListener = (TextView)findViewById(R.id.textViewListener);

        /******************************************************************************
         该函数为Android的ImageButton，我设置为按一下该按钮就会发送一次消息。
         ******************************************************************************/

        mImageButtonTalker.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Order orderTalker = topicsSetting.orderTalkerPublisher.newMessage();
                orderTalker.setRobotName(robot_name);
                orderTalker.setStartId(start_id);
                orderTalker.setTargetId(target_id);
                orderTalker.setStatus(status);
                // 定义orderTalker消息类型为Order，Order消息类型中包含了四个信息，
                // 其中包括String格式的RobotName，Int格式的start_id，target_id和status；
                // 定义之后对orderTalker内的信息分别进行赋值。

                topicsSetting.orderTalkerPublisher.publish(orderTalker);
                //将消息通过Publisher发送出去。
            }
        });
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    public boolean onLongClick(View v) {
        return false;
    }

    /******************************************************************************
     在这里定义需要发送的消息类型及消息路径，其中Publisher自Android端发送消息至ROS系统，
     Subscriber接收由ROS系统发送过来的消息。
     ******************************************************************************/
    public class TopicsSetting extends AbstractNodeMain {

        public Publisher<Order> orderTalkerPublisher;
        public Subscriber<RfidTags> rfidTagsListenerSubscriber;
        //分别定义了一个Publisher(向ROS系统发布消息的单元)和一个Subscriber(接受自ROS系统发来的消息的单元)

        public GraphName getDefaultNodeName()
        {
            return GraphName.of("rostester");
        }
        public void onStart(final ConnectedNode connectedNode)
        {
            orderTalkerPublisher = connectedNode.newPublisher(getString(R.string.topic_talker), Order._TYPE);
            rfidTagsListenerSubscriber = connectedNode.newSubscriber(getString(R.string.topic_listener), RfidTags._TYPE);
            //分别为Publisher 和Subscriber定义了消息发布及接收的路径(R.string.topic_talker / R.string.topic_listener)，
            //每个消息都有对应的路径，路径设置相同的Publisher和Subscriber就可以互相收发消息。

            /************* 建立一个消息接收器，并且将通过回调Handler执行相关动作 **********/
            rfidTagsListenerSubscriber.addMessageListener(new MessageListener<RfidTags>()
            {
                @Override
                public void onNewMessage(RfidTags rfidTags)
                {
                    agvToAndroidRfidTags = rfidTags.getRfidTags();
                    // 获得地面上的rfid卡号数值并赋值给agvToAndroidRfidTags。

                    Message msgRfidTags = new Message();
                    msgRfidTags.what = agvToAndroidRfidTags[0];
                    textHandlerListener.sendMessage(msgRfidTags);
                    // 通过调用Handler执行相关动作
                }
            });
        }
    };

    /***********************************************************************************
        Android的Handler，我用来作为消息接收的回调函数，当接收到消息后在屏幕上显示出来相应的信息。
     ***********************************************************************************/
    Handler textHandlerListener = new Handler()
    {
        public void handleMessage(Message msg)
        {
            switch (msg.what)
            {
                case 0:
                    textViewListener.setText("000000");
                    break;
                default:
                    textViewListener.setText("I'm Listening");
            }
        }
    };

    protected void init(NodeMainExecutor nodeMainExecutor)
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
}
