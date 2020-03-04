package com.hantepay.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author wt
 * <p>
 * 描述: 接口页面控制器
 * http://www.hante.com
 * @date : 2019-12-05
 */
@Controller
public class ApiPageController {

    @GetMapping("/")
    public String index(){
        return "pages/index";
    }

    /**
     * 跳转到标准支付页面
     */
    @GetMapping("securePay")
    public String securePay(Model model){
        return "pages/securePay";
    }

    /**
     * 跳转到二维码支付页面
     */
    @GetMapping("qrCodePay")
    public String qrCodePay(Model model){
        return "pages/qrCodePay";
    }

    /**
     * 跳转到线下扫码支付页面
     */
    @GetMapping("posPay")
    public String posPay(Model model){
        return "pages/posPay";
    }

    /**
     * 跳转到小程序支付页面
     */
    @GetMapping("microPay")
    public String microPay(Model model){
        return "pages/microPay";
    }

    /**
     * 跳转到退款页面
     */
    @GetMapping("refund")
    public String refund(Model model){
        return "pages/refund";
    }

    /**
     * 跳转到查询页面
     */
    @GetMapping("orderQuery")
    public String orderQuery(Model model){
        return "pages/orderQuery";
    }

}
