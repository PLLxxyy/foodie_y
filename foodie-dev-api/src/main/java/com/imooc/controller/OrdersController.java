package com.imooc.controller;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.PayMethod;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.service.AddressService;
import com.imooc.service.OrderService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "订单相关", tags = "订单相关接口相关api")
@RequestMapping("orders")
@RestController
public class OrdersController extends BaseController{

  @Autowired
  private OrderService orderService;

  //用户下单

  @ApiOperation(value = "用户下单", notes = "用户下单", httpMethod = "POST")
  @PostMapping("/create")
  public IMOOCJSONResult create(
      @RequestBody SubmitOrderBO submitOrderBO,
      HttpServletRequest request, HttpServletResponse response
      ){
    System.out.println(submitOrderBO.toString());
    //1.创建订单
    String orderId=orderService.createOrder(submitOrderBO);
    //2.创建订单以后，移除购物车已经结算（提交）的商品

    // TODO 整合redis后完善购物车中一结算商品的清除，并且同步到前端的ckkie
    CookieUtils.setCookie(request,response,FOOD_SHOPCART,"",true);

    //3.0 向支付中心发送当前订单，用于保存支付中心的订单

    if (submitOrderBO.getPayMethod() != PayMethod.WEIXIN.type &&
        submitOrderBO.getPayMethod() != PayMethod.ALIPAY.type
    ){
      return IMOOCJSONResult.errorMsg("支付方式不支持!");
    }
    //前端回调获取orderId
    return IMOOCJSONResult.ok(orderId);
  }

    //支付中线返回的支付结果
    @ApiOperation(value = "根据支付中心返回修改订单状态", notes = "根据支付中心返回修改订单状态", httpMethod = "POST")
    @PostMapping("/notifyMerchantOrderPaid")
    public Integer notifyMerchantOrderPaid(
        @ApiParam(required = true,name = "merchantOrderId" ,value = "支付ID")
        @RequestBody String merchantOrderId){
        orderService.updateOrderStatus(merchantOrderId, OrderStatusEnum.WAIT_DELIVER.type);
        return HttpStatus.OK.value();
      }




}
