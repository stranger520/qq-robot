package com.zuicoding.platform.qqrobot.client;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zuicoding.platform.qqrobot.core.api.QQUrlApi;
import com.zuicoding.platform.qqrobot.core.http.HttpMethod;
import com.zuicoding.platform.qqrobot.core.http.HttpMethodBuilder;
import com.zuicoding.platform.qqrobot.core.http.HttpSender;
import com.zuicoding.platform.qqrobot.utils.LogUtil;
import com.zuicoding.platform.qqrobot.utils.UserAgent;
import org.apache.http.HttpResponse;

import java.io.File;
import java.net.URL;

/**
 * Created by Stephen.lin on 2017/6/26.<br/>
 * Description : <p></p>
 **/
public class SmartQQClient {

    private LogUtil log = LogUtil.newLogger(SmartQQClient.class);
    //客户端id，固定的
    private static final long Client_ID = 53999199;


    private String userAgent;

    private HttpSender sender;

    //QR码的Token
    private String qrsig;

    //ptwebqq
    private String ptwebqq;

    //vfwebqq
    private String vfwebqq;

    //psessionid
    private String psessionid;

    private String uin;

    private boolean loginSuccess = false;

    public SmartQQClient() {
        this.userAgent = UserAgent.getUserAgent();
        this.sender = new HttpSender();
    }

    public void login(){

        getQRcode();
        String url = verifyQRCode();
        getPtwebqq(url);
        getVfwebqq();
        getUinAndPsessionid();

        if (loginSuccess){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while (true){
                        pullMessage();
                    }
                }
            }).start();
        }
    }

    //接受消息
    private void pullMessage(){
        log.i("拉取消息");
        try {
            JSONObject r = new JSONObject();
            r.put("ptwebqq", ptwebqq);
            r.put("clientid", Client_ID);
            r.put("psessionid", psessionid);
            r.put("key", "");
            String body = sender.send(new HttpMethodBuilder()
                    .setMethod(HttpMethod.POST)
                    .setUrl(QQUrlApi.POLL_MESSAGE.getUrl())
                    .addHead(UserAgent.USER_AGENT_KEY,userAgent)
                    .addHead("Referer",QQUrlApi.POLL_MESSAGE.getReferer())
                    .addHead("Origin",QQUrlApi.POLL_MESSAGE.getOrigin())
                    .addParam("r",r.toJSONString())
            );
            if(log.isDebugEnabled()){
                log.d("pull message result:\n{}",body);
            }
        }catch (Exception e){
            log.e("pull message error",e);
        }

    }

    //1、获取二维码
    public void getQRcode(){
        log.i("获取二维码...");
        try {
            URL url = SmartQQClient.class.getClassLoader().getResource("qrcode.png");
            File file = new File(url.toURI());
            if (!file.exists()){
                file.createNewFile();
            }
            HttpResponse response = sender.save2File(new HttpMethodBuilder()
                    .addHead(UserAgent.USER_AGENT_KEY,this.userAgent)
                    .setUrl(QQUrlApi.GET_QR_CODE.getUrl()),file);
            qrsig = sender.getCookies().get("qrsig");
            log.i("二维码 已生成：{}, token：{}",file.getAbsolutePath(),qrsig);

        }catch (Exception e){
            log.e("获取二维码失败",e);
        }


    }
    //用于生成ptqrtoken的哈希函数
    private static int hash33(String s) {
        int e = 0, i = 0, n  = s.length();
        for (; n > i; ++i)
            e += (e << 5) + s.charAt(i);
        return 2147483647 & e;
    }

    //登录流程2：校验二维码
    public String verifyQRCode(){
        try {
            while (true){
                Thread.sleep(1000);
                String body = sender.send(new HttpMethodBuilder()
                        .setUrl(QQUrlApi.VERIFY_QR_CODE.buildUrl(hash33(qrsig)))
                        .addHead("Referer",QQUrlApi.VERIFY_QR_CODE.getReferer())
                        .addHead(UserAgent.USER_AGENT_KEY,userAgent)
                );
                if(log.isDebugEnabled()){
                    log.d("校验二维码结果:{}",body);
                }
                if (body.contains("成功")){
                    for (String content : body.split("','")) {
                        if (content.startsWith("http")) {
                            log.i("正在登录，请稍后");

                            return content;
                        }
                    }
                    continue;
                }
                if (body.contains("二维码已失效")){
                    log.i("二维码已失效，尝试重新获取二维码");
                    getQRcode();
                }

            }
        }catch (Exception e){
            log.e("二维码认证失败!",e);
        }
        return null;
    }

    //登录流程3：获取ptwebqq
    public void  getPtwebqq(String url){
        log.i("开始获取 ptwebqq ,by url is :\n{}",url);
        try {
            String body = sender.send(new HttpMethodBuilder()
                    .setUrl(QQUrlApi.GET_PTWEBQQ.buildUrl(url))
                    .addHead(UserAgent.USER_AGENT_KEY,userAgent)

            );
            ptwebqq = sender.getCookies().get("ptwebqq");
            if (log.isDebugEnabled()){
                log.d("ptwebqq is :{}",ptwebqq);
            }
        }catch (Exception e){
            log.e("获取 ptwebqq error",e);
        }

    }

    //登录流程4：获取vfwebqq
    public void getVfwebqq(){
        log.i("开始获取vfwebqq");
        try {
            String body = sender.send(new HttpMethodBuilder()
                    .addHead(UserAgent.USER_AGENT_KEY,userAgent)
                    .addHead("Referer",QQUrlApi.GET_VFWEBQQ.getReferer())
                    .setUrl(QQUrlApi.GET_VFWEBQQ.buildUrl(ptwebqq)));

            if (log.isDebugEnabled()){
                log.d("vfwebqq sender result:{}",body);
            }
            JSONObject jo = JSON.parseObject(body);


            if (jo.getInteger("retcode") == 0){
                vfwebqq = jo.getString("vfwebqq");
                return;
            }
            if (jo.getInteger("retcode") == null || jo.getInteger("retcode") != 0){
                log.e("获取vfwebqq失败!");
                return;
            }
        }catch (Exception e){
            log.e("获取vfwebqq error",e);
        }

    }

    //登录流程5：获取uin和psessionid
    private void getUinAndPsessionid() {
        log.i("开始获取uin和psessionid");
        try {
            JSONObject r = new JSONObject();
            r.put("ptwebqq", ptwebqq);
            r.put("clientid", Client_ID);
            r.put("psessionid", "");
            r.put("status", "online");

            String body = sender.send(new HttpMethodBuilder()
                    .setUrl(QQUrlApi.GET_UIN_AND_PSESSIONID.getUrl())
                    .setMethod(HttpMethod.POST)
                    .addHead(UserAgent.USER_AGENT_KEY,userAgent)
                    .addHead("Referer",QQUrlApi.GET_UIN_AND_PSESSIONID.getReferer())
                    .addHead("Origin",QQUrlApi.GET_UIN_AND_PSESSIONID.getOrigin())
                    .addParam("r",r.toJSONString())
            );
            if (log.isDebugEnabled()){
                log.d("getUinAndPsessionid result :{}",body);
            }
            JSONObject jo = JSON.parseObject(body);
            if (jo.getInteger("retcode") == 0){
                log.i("登录成功...");
                loginSuccess = true;
                this.psessionid = jo.getJSONObject("result").getString("psessionid");
                this.uin = jo.getJSONObject("result").getString("uin");
                return;
            }
            log.e("登录失败!");
        }catch (Exception e){
            log.e(e);
        }


    }


    public static void main(String[] args) {
        SmartQQClient client = new SmartQQClient();
        client.login();
    }
}
