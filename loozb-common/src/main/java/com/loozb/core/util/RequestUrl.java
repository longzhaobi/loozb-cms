package com.loozb.core.util;

import org.apache.commons.httpclient.NameValuePair;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chuan on 2017-5-25.
 */
public class RequestUrl {
    public static void main(String[] args) {
        String url = "http://clouderp.skystorechain.com:20080/clouderp-shop-rest/api/shop/prodAutoStorage";
        ArrayList<NameValuePair> param = new ArrayList<NameValuePair>();
        param.add(new NameValuePair("debugSkipSignValidate", "1"));
        param.add(new NameValuePair("orderNo", "XSM95003245436"));
        param.add(new NameValuePair("userId", "6274"));
        param.add(new NameValuePair("shopId", "5026"));
        String result = HttpUtil.httpClientPost(url, param);
        System.out.println(result);
    }
}
