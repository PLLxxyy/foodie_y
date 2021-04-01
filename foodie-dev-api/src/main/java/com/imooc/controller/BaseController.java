package com.imooc.controller;


import com.imooc.pojo.Orders;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.IMOOCJSONResult;
import java.io.File;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

@Controller
public class BaseController {

  public static final Integer COMMEN_PAGE_SIZE=10;
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
  @Autowired
  public MyOrdersService myOrdersService;

  /*
   *用于验证用户和订单是否有关联关系，避免非法用户调用
   * */
  public IMOOCJSONResult checkUserOrder(String orderId,String userId){
    Orders orders=myOrdersService.queryMyOrder(orderId,userId);
    if(orders ==null){
      return IMOOCJSONResult.errorMsg("订单不存在");
    }
    return IMOOCJSONResult.ok();
  }
}
