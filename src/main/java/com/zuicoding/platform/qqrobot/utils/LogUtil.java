package com.zuicoding.platform.qqrobot.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Stephen.lin on 2017/6/26.<br/>
 * Description : <p></p>
 **/
public class LogUtil {

    private  Logger logger ;
    private LogUtil(Class klass){

        logger = LoggerFactory.getLogger(klass);
    }
    private LogUtil(String  name){

        logger = LoggerFactory.getLogger(name);
    }
    public static LogUtil newLogger(Class klass){

        return new LogUtil(klass);
    }
    public static LogUtil newLogger(String name){

        return new LogUtil(name);
    }
    public boolean isDebugEnabled(){
        return logger.isDebugEnabled();
    }
    public boolean isWarnEnabled(){
        return logger.isWarnEnabled();
    }
    public boolean isInfoEnabled(){
        return logger.isInfoEnabled();
    }

    public void d(String log){
        logger.debug(log);
    }
    public void d(String log,Object... args){

        logger.debug(log, args);
    }

    public void d(String log,Throwable thr){
        logger.debug(log,thr);
    }

    public void i(String log){
        logger.info(log);
    }

    public void i(String log,Object... args){
        logger.info(log, args);
    }
    public void i(String log,Throwable thr){
        logger.info(log, thr);
    }

    public void w(String log){
        logger.warn(log);
    }
    public void w(String log,Object... args){
        logger.warn(log, args);
    }
    public void w(String log,Throwable thr){
        logger.warn(log, thr);
    }
    public void e(String log){
        logger.error(log);
    }
    public void e(String log,Object... args){
        logger.error(log,args);
    }
    public void e(String log,Throwable thr){
        logger.error(log, thr);
    }
    public void e(Throwable thr){
        logger.error("",thr);
    }
}
