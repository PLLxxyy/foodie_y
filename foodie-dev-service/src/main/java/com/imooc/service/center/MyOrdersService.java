package com.imooc.service.center;

import com.imooc.pojo.Orders;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.CenterUserBO;
import com.imooc.utils.PagedGridResult;

public interface MyOrdersService {

  //判断用户是否存在
  public PagedGridResult queryMyOrders(String userId,
            Integer orderStatus,
            Integer page,
            Integer pageSize);


  /*
  * 订单状态-->商家发货
  * */
  public void updateDeliverOrderStatus(String orderId);


  /*
   * 查询我的订单
   * */
  public Orders queryMyOrder(String orderId,String userId);

  /*
  * 更新订单状态，确认收过
  * */
  public boolean updateReceiveOrderStatus(String orderId);

  /*
   * 删除订单
   * */
  public boolean deleteOrder(String orderId,String userId);
}
