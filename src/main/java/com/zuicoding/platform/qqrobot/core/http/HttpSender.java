package com.zuicoding.platform.qqrobot.core.http;

import com.zuicoding.platform.qqrobot.utils.LogUtil;
import com.zuicoding.platform.qqrobot.utils.ResourceUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Stephen.lin on 2017/6/26.<br/>
 * Description : <p></p>
 **/
public class HttpSender {


    private LogUtil log = LogUtil.newLogger(HttpSender.class);

    private CookieStore cookieStore = new BasicCookieStore();

    private  HttpClient httpClient = HttpClients
            .custom()
            .setDefaultCookieStore(cookieStore)
            .setDefaultRequestConfig(HttpClientBuilder.requestConfig)
            .build();




    public  String send(HttpMethodBuilder builder) throws Exception {
        if (log.isDebugEnabled()){
            log.d(builder.getUrl());
        }
        HttpResponse response = httpClient.execute(builder.build());
        if (response == null){
            throw new NullPointerException("can't access response by url is :"+builder.getUrl());
        }

        return EntityUtils.toString(response.getEntity(),builder.getCharset());
    }

    public  HttpResponse save2File(HttpMethodBuilder builder, File file) throws Exception {
        if (log.isDebugEnabled()){
            log.d(builder.getUrl());
        }
        HttpResponse response = httpClient.execute(builder.build());
        if (response == null){
            throw new NullPointerException("can't access response by url is :"+builder.getUrl());
        }

        InputStream stream = response.getEntity().getContent();
        FileUtils.copyInputStreamToFile(stream,file);

        return  response;
    }

    public Map<String,String> getCookies(){
        List<Cookie> cookies = this.cookieStore.getCookies();
        Map<String,String> map = new LinkedHashMap<>(cookies.size());

        if (CollectionUtils.isNotEmpty(cookies)){
            for (Cookie cookie : cookies) {
                map.put(cookie.getName(),cookie.getValue());
            }
        }
        return map;
    }



    private static class HttpClientBuilder {


        private static RequestConfig requestConfig = RequestConfig
                .custom()
                .setConnectionRequestTimeout(ResourceUtils.getIntValue("http.RequestConnectionTimeout"))
                .setConnectTimeout(ResourceUtils.getIntValue("http.connectionTimeout"))
                .setSocketTimeout(ResourceUtils.getIntValue("http.socketTimeout"))
                //.setCookieSpec(CookieSpecs.IGNORE_COOKIES)
                .build();

    }
}
