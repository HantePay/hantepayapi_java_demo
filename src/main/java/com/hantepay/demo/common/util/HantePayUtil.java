package com.hantepay.demo.common.util;

import com.alibaba.fastjson.JSONObject;

import javax.servlet.http.HttpServletRequest;
import java.beans.BeanInfo;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * @author wt
 * <p>
 * 描述: 汉特支付工具类
 * @date : 2019-12-05
 */
public class HantePayUtil {


    /**
     * 对象类转map
     * @param object 需要转换类
     * @return 返回转换后Map
     */
    public static SortedMap<String, Object> toMap(Object object) throws Exception {
        SortedMap<String, Object> targetParamMap = new TreeMap<>();
        Class<?> cls = object.getClass();
        BeanInfo beanInfo = Introspector.getBeanInfo(cls);
        PropertyDescriptor[] propertyDescriptors = beanInfo.getPropertyDescriptors();
        for (PropertyDescriptor descriptor : propertyDescriptors) {
            String propertyName = descriptor.getName();
            if (!"class".equals(propertyName)) {
                Method readMethod = descriptor.getReadMethod();
                Object result = readMethod.invoke(object);
                if (!StringUtil.isEmpty(result)) {
                    targetParamMap.put(propertyName, result);
                }
            }
        }
        return targetParamMap;
    }

    public static JSONObject hanteBuildRequestParam(SortedMap<String, Object> paramMap, String key){
        JSONObject requestParam = new JSONObject(paramMap);
        requestParam.put("signature", getSign(paramMap,key));
        requestParam.put("sign_type", "MD5");
        return requestParam;
    }

    /**
     * 获取签名字符串
     * @param paramMap 待签名参数
     * @param key 商户密钥
     * @return 签名字符串
     */
    private static String getSign(SortedMap<String, Object> paramMap, String key) {
        StringBuilder signBuilder = new StringBuilder();
        for (Map.Entry<String, Object> entry : paramMap.entrySet()) {
            if (!"signature".equals(entry.getKey())
                    && !"sign_type".equals(entry.getKey())
                    && !StringUtil.isEmpty(entry.getValue())) {
                signBuilder.append(entry.getKey()).append("=")
                        .append(entry.getValue()).append("&");
            }
        }
        return MD5.sign(signBuilder.toString() , key, "UTF-8");
    }

    public static JSONObject getRequestParam(HttpServletRequest request) {
        Map<String, String[]> params = request.getParameterMap();
        SortedMap<String, Object> req=new TreeMap<>();
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (String value : values) {
                req.put(key, value);
            }
        }
        req.put("merchant_no", HantePayConfig.MERCHANT_NO);
        req.put("store_no",HantePayConfig.STORE_NO);
        return HantePayUtil.hanteBuildRequestParam(req,HantePayConfig.KEY);
    }


}
