package com.imooc.service;

import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.OrderVO;

public interface OrderService {

  /*
  * 创建订单相关信息
  * */
  public OrderVO createOrder(SubmitOrderBO submitOrderBO);
  /*
   * 修改订单状态
   * */
  public void updateOrderStatus(String orderId,Integer orderStatus);

  /*
   * =关闭超时未支付订单
   * */
  public void closeOrder();

}
