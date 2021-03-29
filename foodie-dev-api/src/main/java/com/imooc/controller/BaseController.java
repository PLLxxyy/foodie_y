package com.imooc.controller;


import java.io.File;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController {

  public static final Integer COMMENT_PAGE_SIZE=10;
  public static final Integer PAGE_SIZE=20;
  public static final String FOOD_SHOPCART="shopcart";
  //支付中心调用的地址：
  String paymentUrl="http://payment.t.mukewang.com/foodie-payment/payment/createMerchantOrder";

  //微信支付成功->支付中心->天天吃货平台
  //回调通知url
  String payReturnUrl="http://localhost:8089/orders/notifyMerchantOrderPaid";

  public static final String IMAGE_USER_FACE_LOCATION = File.separator + "workspaces" +
      File.separator + "images" +
      File.separator + "foodie" +
      File.separator + "faces";
}
