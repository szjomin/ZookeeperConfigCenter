package com.study.zk.distributeConfig.service;

import com.study.zk.distributeConfig.config.SystemConfigUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PayService {

    @Autowired
    SystemConfigUtil systemConfigUtil;

    /**
     * 根据不同类型调用不同的接口
     *
     * @param type weixin | alipay
     */
    public String pay(String type) {
        // TODO 返回对应的接口~ 测试为主
        if ("alipay".equals(type)) {
            System.out.println("调用支付宝接口,当前参数配置如下：");
            System.out.println(systemConfigUtil.getProperty("pay.alipay.url"));
            return systemConfigUtil.getProperty("pay.alipay.url");
        }

        if ("weixin".equals(type)) {
            System.out.println("调用微信接口,当前参数配置如下：");
            System.out.println(systemConfigUtil.getProperty("pay.weixin.url"));
            return systemConfigUtil.getProperty("pay.weixin.url");
        }

        return "没找到支付平台";
    }

}
