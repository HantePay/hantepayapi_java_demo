package com.hantepay.demo.common.util;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * 描述
 *
 * @author :         wt
 * @version :        2.0
 * @date :     2019/9/9 17:02
 */
@Slf4j
public class HttpClientUtil {

    private static CloseableHttpClient closeableHttpClient = HttpClientBuilder.create().build();

    public static JSONObject get(String url, Map<String, Object> headerParams) {
        HttpGet httpGet = new HttpGet(HantePayConfig.BASE_URL + url);
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("请求链接", url);
        return response(httpGet, headerParams, logMap);
    }

    public static JSONObject post(String url, Map<String, Object> headerParams, String jsonParam){
        HttpPost httpPost = new HttpPost(HantePayConfig.BASE_URL + url);
        httpPost.addHeader("Content-Type","application/json;charset=UTF-8");
        StringEntity entity = new StringEntity(jsonParam.toString(),"UTF-8");
        entity.setContentEncoding("UTF-8");
        httpPost.setEntity(entity);
        Map<String, Object> logMap = new HashMap<>();
        logMap.put("请求链接", url);
        logMap.put("请求参数", jsonParam);
        return response(httpPost, headerParams, logMap);
    }

    private static JSONObject response(HttpRequestBase httpRequestBase, Map<String, Object> headerParams, Map<String, Object> logMap) {
        JSONObject respParam = new JSONObject();
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000)
                .setSocketTimeout(5000).build();
        httpRequestBase.setConfig(config);
        // 拼装请求头
        if (!StringUtil.isEmpty(headerParams)) {
            for (Map.Entry<String, Object> entry : headerParams.entrySet()) {
                httpRequestBase.addHeader(entry.getKey(), entry.getValue().toString());
            }
        }

        try {
            HttpResponse httpResponse = closeableHttpClient.execute(httpRequestBase);
            // 状态码
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            logMap.put("请求头", headerParams);
            logMap.put("状态码", statusCode);
            logMap.put("请求方法", httpRequestBase.getMethod());
            log.debug("请求信息:{}",logMap);

            // 返回响应body数据
            HttpEntity entity = httpResponse.getEntity();
            String resBody = EntityUtils.toString(entity, "utf-8");
            // 响应头
            Header[] headers = httpResponse.getAllHeaders();

            // 组装响应
            respParam.put("body", resBody);
            respParam.put("headers", headers);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return respParam;
    }

    public static InputStream getAuthCode(String url, Map<String, Object> headerParams) {
        RequestConfig config = RequestConfig.custom().setConnectionRequestTimeout(5000).setConnectTimeout(5000)
                .setSocketTimeout(5000).build();
        HttpGet httpGet = new HttpGet(url);
        httpGet.setConfig(config);
        // 拼装请求头
        if (!headerParams.isEmpty()) {
            for (Map.Entry<String, Object> entry : headerParams.entrySet()) {
                httpGet.addHeader(entry.getKey(), entry.getValue().toString());
            }
        }
        HttpResponse httpResponse = null;
        try {
            httpResponse = closeableHttpClient.execute(httpGet);
            int statusCode = httpResponse.getStatusLine().getStatusCode();
            Map<String, Object> logMap = new HashMap<>();
            logMap.put("请求链接", url);
            logMap.put("请求头", headerParams);
            logMap.put("请求方法", httpGet.getMethod());
            logMap.put("请求状态", statusCode);

            if (statusCode == HttpStatus.SC_OK) {
                HttpEntity entity = httpResponse.getEntity();
                return entity.getContent();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
