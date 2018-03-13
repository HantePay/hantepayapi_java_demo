package com.aurfy.haze.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSONArray;
import com.aurfy.haze.utils.HttpRequest;
import com.aurfy.haze.utils.SecurityUtils;
import com.aurfy.haze.utils.StringUtils;
import com.aurfy.haze.web.vo.API.APIRefundReqVO;
import com.aurfy.haze.web.vo.API.APISecurePayReqVO;
import com.aurfy.haze.web.vo.API.APISecurePayRespVO;

@Controller
public class APIController {
	private static final Logger logger = LoggerFactory.getLogger(APIController.class);
	
	private static final String token = "123456";

	@RequestMapping(value = "/APISecurePay", method = RequestMethod.POST)
	public @ResponseBody void APISecurePay(HttpServletRequest request,
			@ModelAttribute("payReq") @Validated APISecurePayReqVO payReq, BindingResult errors,
			HttpServletResponse resp) {
		try {
			if (errors.hasErrors()) {
				resp.getWriter().print(errors.getAllErrors());
			}
			logger.info("Do API SecurePay: " + payReq.getInput());
			String result = HttpRequest.sendAuthPost(payReq.getUrl(), payReq.getInput(), "Bearer " + payReq.getToken());
			logger.info("get response from php_API: " + result);
			resp.setCharacterEncoding("UTF-8");
			resp.setHeader("Content-type", "text/html");// jquery post must return json type
			resp.getWriter().print(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/APIRefund", method = RequestMethod.POST)
	public @ResponseBody void APIRefund(HttpServletRequest request, @Validated APIRefundReqVO payReq,
			BindingResult errors, HttpServletResponse resp) {
		try {
			if (errors.hasErrors()) {
				resp.getWriter().print(errors.getAllErrors());
			}
			logger.info("Do API Refund: " + payReq.getInput());
			String url = payReq.getUrl().replace("{transaction_id}", payReq.getTransaction_id());
			System.out.println(payReq.getInput());
			String result = HttpRequest.sendAuthPost(url, payReq.getInput(), "Bearer " + payReq.getToken());
			logger.info("get response from php_API: " + result);
			resp.setCharacterEncoding("UTF-8");
			resp.setHeader("Content-type", "application/json");// jquery post must return json type
			resp.getWriter().print(result);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/ipn")
	public @ResponseBody void receiveIPN(HttpServletResponse response, HttpServletRequest request,
			APISecurePayRespVO respVO) {
		try {
			logger.info("ipn request:");
			logger.info(getClientInfo(request));
			logger.info(respVO.toString());

			BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
			String line = null;
			StringBuilder sb = new StringBuilder();
			while ((line = br.readLine()) != null) {
				sb.append(line);
			}
			logger.info("getInputStream to String: " + sb.toString());

			Map<String, String[]> params = request.getParameterMap();
			if (params.size() < 1) {
				response.setStatus(HttpServletResponse.SC_NO_CONTENT);
			}
			logger.info(request.getParameter("id"));
			StringBuilder str = new StringBuilder();
			str.append("Form Data:");
			for (String key : params.keySet()) {
				String[] values = params.get(key);
				for (int i = 0; i < values.length; i++) {
					String value = values[i];
					str.append("\r\n " + key + " -> " + value);
				}
			}
			Set<String> keySet = params.keySet();
			List<String> keyList = new ArrayList<String>(keySet);
			Collections.sort(keyList);
			StringBuffer mdStr = new StringBuffer();
			for (String key : keyList) {
				String value = params.get(key)[0];
				if(!StringUtils.isEmpty(value)){
					if (!key.equals("verify_sign") && params.get(key) != null && value != null && !value.equals("null")) {
						mdStr.append(key + "=" + value + "&");
					}
				}
			}
			logger.info("sign prams string:" + mdStr.toString());
			mdStr.append(SecurityUtils.MD5(token).toLowerCase());
			logger.info("prams and token sign string:" + mdStr.toString());
			String verify_sign = params.get("verify_sign")[0];
			String sign = SecurityUtils.MD5(mdStr.toString()).toLowerCase();
			logger.info("sign string:" + sign);
			logger.info("verify_sign string:" + verify_sign);
			if (sign.equals(verify_sign)) {
				str.append("\r\n sign_vlidated -> true");
			} else {
				str.append("\r\n sign_vlidated -> false");
			}
			logger.info("IPN: " + str.toString());
			response.setHeader("Content-type", "application/json");// jquery post must return json type
			response.getWriter().print(str);

		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
	}

	@RequestMapping(value = "/callback")
	public ModelAndView callback(HttpServletRequest request) {
		try {
			logger.info("callback request:");
			logger.info(getClientInfo(request));

			Map<String, String[]> params = request.getParameterMap();
			if (params.size() < 1) {
				return new ModelAndView("callback", "result", "Response status:" + HttpServletResponse.SC_NO_CONTENT);
			}
			StringBuilder str = new StringBuilder();
			str.append("Form Data:");
			for (String key : params.keySet()) {
				String[] values = params.get(key);
				for (int i = 0; i < values.length; i++) {
					String value = values[i];
					str.append("<br/>" + key + "=" + value);
				}
			}
			return new ModelAndView("callback", "result", str.toString());
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
			return new ModelAndView("callback", "result", e.getMessage());
		}
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/APIQrcode", method = RequestMethod.POST)
	public ModelAndView APIQrcode(HttpServletRequest request,
			@ModelAttribute("payReq") @Validated APISecurePayReqVO payReq, BindingResult errors,
			HttpServletResponse resp) {
		ModelAndView mv = new ModelAndView("qrCodePay");
		try {
			if (errors.hasErrors()) {
				resp.getWriter().print(errors.getAllErrors());
			}
			logger.info("Do API Qrcode: " + payReq.getInput());
			System.out.println(payReq.getUrl() + "================");
			String result = HttpRequest.sendAuthPost(payReq.getUrl(), payReq.getInput(), "Bearer " + payReq.getToken());
			logger.info("get response from php_API: " + result);
			Map<Object, Object> map = (Map<Object, Object>) JSONArray.parse(result);
			mv.addObject("resultMap", map);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage(), e);
		}
		return mv;
	}

	public static String getClientInfo(HttpServletRequest request) {
		StringBuilder str = new StringBuilder();
		str.append("\r\n ----------New Request---------------");
		str.append("\r\n RequestURL:" + request.getRequestURL());
		str.append("\r\n Request Header");
		str.append("\r\n Origin:" + request.getHeader("Origin"));
		str.append("\r\n Referer:" + request.getHeader("Referer"));
		str.append("\r\n Accept:" + request.getHeader("Accept"));
		str.append("\r\n Host:" + request.getHeader("Host"));
		str.append("\r\n User-Agent:" + request.getHeader("User-Agent"));
		str.append("\r\n Locale:" + request.getLocale());
		str.append("\r\n Protocol:" + request.getProtocol());
		str.append("\r\n Scheme:" + request.getScheme());
		str.append("\r\n Connection:" + request.getHeader("Connection"));
		str.append("\r\n Character Encoding:" + request.getCharacterEncoding());
		str.append("\r\n Content Type:" + request.getContentType());
		str.append("\r\n Content Length:" + request.getContentLength());
		str.append("\r\n Http Method:" + request.getMethod());
		str.append("\r\n Remote Addr: " + request.getRemoteAddr());
		str.append("\r\n Remote Host: " + request.getRemoteHost());
		str.append("\r\n Remote Port: " + request.getRemotePort());
		str.append("\r\n Remote User: " + request.getRemoteUser());
		str.append("\r\n Query String: " + request.getQueryString());
		str.append("\r\n Form Data:");
		Map<String, String[]> params = request.getParameterMap();
		for (String key : params.keySet()) {
			String[] values = params.get(key);
			for (int i = 0; i < values.length; i++) {
				String value = values[i];
				str.append("\r\n " + key + " -> " + value);
			}
		}
		str.append("\r\n ----------End Request---------------");
		return str.toString();
	}
	
}
