package com.study.zk.distributeConfig.web;

import com.study.zk.distributeConfig.service.PayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class PayController {

    @Autowired
    PayService payService;

    @RequestMapping("/pay")
    public String doPay(String type) {
        String testInfo = payService.pay(type);// 调用支付的具体实现
        return testInfo;
    }
}
