package com.zuicoding.platform.qqrobot.core.api;

/**
 * Created by Stephen.lin on 2017/6/26.<br/>
 * Description : <p></p>
 **/
public enum QQUrlApi {

    GET_QR_CODE(
            "https://ssl.ptlogin2.qq.com/ptqrshow?appid=501004106&e=0&l=M&s=5&d=72&v=4&t=0.1",
            ""
    ),

    /**
     * 二维码认证
     */
    VERIFY_QR_CODE(
            "https://ssl.ptlogin2.qq.com/ptqrlogin?ptqrtoken={1}&webqq_type=10&remember_uin=1&login2qq=1&aid=501004106&u1=http%3A%2F%2Fw.qq.com%2Fproxy.html%3Flogin2qq%3D1%26webqq_type%3D10&ptredirect=0&ptlang=2052&daid=164&from_ui=1&pttype=1&dumy=&fp=loginerroralert&0-0-157510&mibao_css=m_webqq&t=undefined&g=1&js_type=0&js_ver=10184&login_sig=&pt_randsalt=3",
                    "https://ui.ptlogin2.qq.com/cgi-bin/login?daid=164&target=self&style=16&mibao_css=m_webqq&appid=501004106&enable_qlogin=0&no_verifyimg=1&s_url=http%3A%2F%2Fw.qq.com%2Fproxy.html&f_url=loginerroralert&strong_login=1&login_state=10&t=20131024001"
    ),

    GET_PTWEBQQ(
            "{1}",
                    null
    ),

    GET_VFWEBQQ(
            "http://s.web2.qq.com/api/getvfwebqq?ptwebqq={1}&clientid=53999199&psessionid=&t=0.1",
            "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1"
    ),
    GET_UIN_AND_PSESSIONID(
            "http://d1.web2.qq.com/channel/login2",
            "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2"
    ),
    GET_ACCOUNT_INFO(
            "http://s.web2.qq.com/api/get_self_info2?t=0.1",
            "http://s.web2.qq.com/proxy.html?v=20130916001&callback=1&id=1"
    ),
    POLL_MESSAGE(
            "http://d1.web2.qq.com/channel/poll2",
            "http://d1.web2.qq.com/proxy.html?v=20151105001&callback=1&id=2"
    );
    private String url;

    private String referer;

    QQUrlApi(String url, String referer) {
        this.url = url;
        this.referer = referer;
    }

    public  String buildUrl(Object... args){
        String url = this.url;
        for (int i = 0; i < args.length; i++) {
            url = url.replace("{"+(i+1)+"}",args[i].toString());
        }
        return url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getReferer() {
        return referer;
    }

    public void setReferer(String referer) {
        this.referer = referer;
    }

    public String getOrigin() {
        return this.url.substring(0, url.lastIndexOf("/"));
    }
}
