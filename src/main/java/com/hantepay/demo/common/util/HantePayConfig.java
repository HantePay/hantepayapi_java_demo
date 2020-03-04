package com.hantepay.demo.common.util;

/**
 * @author wt
 * <p>
 * 描述: 支付配置 详情信息可查看商户后台
 *
 * 商户后台地址 http://www.hantepay.com/merchant/loginPage
 * </p>
 * @date : 2019-12-05
 */
public class HantePayConfig {

    /**
     * 商户密钥
     */
    public static final String KEY = "API TOKEN";

    /**
     * 商户编号
     */
    public static final String MERCHANT_NO = "MERCHANT NUMBER";

    /**
     * 商户门店编号
     */
    public static final String STORE_NO = "STORE NUMBER";

    /**
     * HantePay网关请求地址
     */
    public static final String BASE_URL = "https://gateway.hantepay.com/v2/gateway";
}
