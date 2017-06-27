package com.zuicoding.platform.qqrobot.core.http;

import org.apache.commons.collections.CollectionUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Stephen.lin on 2017/6/26.<br/>
 * Description : <p></p>
 **/
public class HttpMethodBuilder {

    private String url;
    private HttpMethod method = HttpMethod.GET;
    private String charset = "UTF-8";
    private List<NameValuePair> params = new ArrayList<>(0);
    private List<Header> headers = new ArrayList<Header>();

    public String getUrl() {
        return url;
    }

    public HttpMethodBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public HttpMethod getMethod() {
        return method;
    }

    public HttpMethodBuilder setMethod(HttpMethod method) {
        this.method = method;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public HttpMethodBuilder setCharset(String charset) {
        this.charset = charset;
        return this;
    }

    public HttpMethodBuilder addParam(String name, String value){
        this.params.add(new BasicNameValuePair(name, value));
        return this;
    }

    public HttpMethodBuilder addParam(String name,int value){
        this.params.add(new BasicNameValuePair(name,String.valueOf(value)));
        return this;
    }

    public HttpMethodBuilder addHead(String name,String value){

        this.headers.add(new BasicHeader(name, value));
        return this;

    }

    public HttpRequestBase build()throws Exception{
        HttpRequestBase http = null;
        HttpEntity entity = null;
        switch (method){
            case GET:
                http = new HttpGet();
                if (CollectionUtils.isNotEmpty(params)){
                    entity = new UrlEncodedFormEntity(params);
                    url += "?" + EntityUtils.toString(entity,this.charset);
                }
                break;
            case POST:
                http = new HttpPost();
                if (CollectionUtils.isNotEmpty(params)){
                    entity = new UrlEncodedFormEntity(params,charset);
                    ((HttpPost)http).setEntity(entity);
                }
                break;
            default:
                throw new IllegalArgumentException("unknow http method :" + method);
        }
        http.setURI(URI.create(url));
        if (CollectionUtils.isNotEmpty(headers)){
            for (Header header : headers) {
                http.addHeader(header);
            }
        }
        return http;
    }

}
