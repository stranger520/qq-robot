package com.zuicoding.platform.qqrobot.utils;

import java.util.Properties;

/**
 * Created by Stephen.lin on 2017/6/26.<br/>
 * Description : <p></p>
 **/
public class ResourceUtils {

    private static LogUtil log = LogUtil.newLogger(ResourceUtils.class);
    private static final Properties PROPERTIES = new Properties();



    private  ResourceUtils(){}


    static {
        try {

            PROPERTIES.load(ResourceUtils.class.getClassLoader().getResourceAsStream("config.properties"));
        }catch (Exception e){
            log.e("load propeties file error",e);
        }
    }

    public static String getValue(String key){
        return PROPERTIES.getProperty(key);
    }
    public static String getValue(String key,String defaultValue){
        return PROPERTIES.getProperty(key,defaultValue);
    }

    public static int getIntValue(String key){

        return Integer.valueOf(PROPERTIES.getProperty(key));
    }
}
