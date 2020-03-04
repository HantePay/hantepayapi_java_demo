package com.hantepay.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.hantepay.demo.common.util.HantePayUtil;
import com.hantepay.demo.common.util.HttpClientUtil;
import com.hantepay.demo.common.util.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

/**
 * @author wt
 * <p>
 * 描述: 接口控制器
 * @date : 2019-12-05
 */
@Slf4j
@Controller
public class ApiController {

    @PostMapping("securePay")
    public void securePay(HttpServletRequest request,HttpServletResponse response) throws IOException {
        JSONObject requestParam = HantePayUtil.getRequestParam(request);
        String result = HttpClientUtil.post("/securepay",null,requestParam.toJSONString()).getString("body");
        log.info("===<<<请求结果:{}",result);
        if(!StringUtil.isEmpty(result)){
            JSONObject responseBody = JSONObject.parseObject(result);
            if("ok".equals(responseBody.getString("return_code")) && "SUCCESS".equals(responseBody.getString("result_code"))){
                log.info("付款地址:{}",responseBody.getJSONObject("data").getString("pay_url"));
                response.sendRedirect(responseBody.getJSONObject("data").getString("pay_url"));
            }
        }
        response.setHeader("Content-type", "application/json;charset=utf-8");
        response.getWriter().write(result);
    }



    @PostMapping("qrCodePay")
    public String qrCodePay(HttpServletRequest request,Model model) throws IOException {
        JSONObject requestParam = HantePayUtil.getRequestParam(request);
        String result = HttpClientUtil.post("/qrcode",null,requestParam.toJSONString()).getString("body");
        log.info("===<<<请求结果:{}",result);
        if(!StringUtil.isEmpty(result)){
            JSONObject responseBody = JSONObject.parseObject(result);
            if("ok".equals(responseBody.getString("return_code")) && "SUCCESS".equals(responseBody.getString("result_code"))){
                JSONObject resultData  = responseBody.getJSONObject("data");
                model.addAttribute("code_url",resultData.getString("code_url"));
                model.addAttribute("amount",resultData.getInteger("amount"));
                model.addAttribute("trade_no",resultData.getString("trade_no"));
                model.addAttribute("out_trade_no",resultData.getString("out_trade_no"));
            }
        }
        return "pages/showQrCode";
    }


    @PostMapping("posPay")
    public void posPay(HttpServletRequest request,HttpServletResponse response) throws IOException {
        JSONObject requestParam = HantePayUtil.getRequestParam(request);
        String result = HttpClientUtil.post("/pospay",null,requestParam.toJSONString()).getString("body");
        log.info("===<<<请求结果:{}",result);
        response.setHeader("Content-type", "application/json;charset=utf-8");
        response.getWriter().write(result);
    }

    @PostMapping("microPay")
    public void microPay(HttpServletRequest request,HttpServletResponse response) throws IOException {
        JSONObject requestParam = HantePayUtil.getRequestParam(request);
        String result = HttpClientUtil.post("/micropay",null,requestParam.toJSONString()).getString("body");
        log.info("===<<<请求结果:{}",result);
        response.setHeader("Content-type", "application/json;charset=utf-8");
        response.getWriter().write(result);
    }

    @PostMapping("refund")
    public void refund(HttpServletRequest request,HttpServletResponse response) throws IOException {
        JSONObject requestParam = HantePayUtil.getRequestParam(request);
        String result = HttpClientUtil.post("/refund",null,requestParam.toJSONString()).getString("body");
        log.info("===<<<请求结果:{}",result);
        response.setHeader("Content-type", "application/json;charset=utf-8");
        response.getWriter().write(result);
    }

    @PostMapping("orderQuery")
    public void orderQuery(HttpServletRequest request,HttpServletResponse response) throws IOException {
        JSONObject requestParam = HantePayUtil.getRequestParam(request);
        Iterator<String> keys = requestParam.keySet().iterator();
        StringBuilder builder = new StringBuilder();
        while (keys.hasNext()){
            String key = keys.next();
            builder.append(key).append("=").append(requestParam.get(key)).append("&");
        }
        String result = HttpClientUtil.get("/orderquery?" + builder.substring(0,builder.length() -1),null).getString("body");
        log.info("===<<<请求结果:{}",result);
        response.setHeader("Content-type", "application/json;charset=utf-8");
        response.getWriter().write(result);
    }


    @PostMapping("notify")
    public void notify(HttpServletRequest request,HttpServletResponse response) throws IOException {
        Map<String, String[]> params = request.getParameterMap();
        Map<String, Object> resultMap=new HashMap<>();
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (String value : values) {
                resultMap.put(key, value);
            }
        }
        log.info("异步接收参数:{}",resultMap.toString());
        response.getWriter().print("SUCCESS");
    }

    @GetMapping("callback")
    public String callback(HttpServletRequest request,Model model){
        Map<String, String[]> params = request.getParameterMap();
        Map<String, Object> resultMap=new HashMap<>();
        for (String key : params.keySet()) {
            String[] values = params.get(key);
            for (String value : values) {
                resultMap.put(key, value);
            }
        }
        log.info("同步接收参数接收参数:{}",resultMap.toString());
        model.addAttribute("resultMap",resultMap);
        return "pages/callback";
    }


}
