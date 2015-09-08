package com.henry.webservice;

import java.util.Random;

/**
 * Created by Henry on 2015/8/6.
 */
public class WebServiceUtils {
    private static String[] testData = {"其他","金属","布衣","设备","日常"};
    public static String queryOrderByNo(String code){
        Random random = new Random();
        return testData[random.nextInt(testData.length)];
    }
}
