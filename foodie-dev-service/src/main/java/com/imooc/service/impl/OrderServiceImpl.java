package com.imooc.service.impl;

import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Orders;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.SubmitOrderBO;
import com.imooc.pojo.vo.MerchantOrdersVO;
import com.imooc.pojo.vo.OrderVO;
import com.imooc.service.AddressService;
import com.imooc.service.ItemService;
import com.imooc.service.OrderService;
import com.imooc.utils.DateUtil;
import java.util.Date;
import java.util.List;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderServiceImpl implements OrderService {

  @Autowired
  private OrdersMapper ordersMapper;
  @Autowired
  private Sid sid;

  @Autowired
  private AddressService addressService;
  @Autowired
  private ItemService itemService;

  @Autowired
  private OrderItemsMapper orderItemsMapper;
  @Autowired
  private OrderStatusMapper orderStatusMapper;

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public OrderVO createOrder(SubmitOrderBO submitOrderBO) {
    String userId=submitOrderBO.getUserId();
    String addressId=submitOrderBO.getAddressId();
    Integer payMethod=submitOrderBO.getPayMethod();
    String leftMsg=submitOrderBO.getLeftMsg();
    //规格id
    String itemSpecIds =submitOrderBO.getItemSpecIds();


    String orderId=sid.nextShort();
    UserAddress address=addressService.queryUserAddress(userId,addressId);
    //订单数据保存
    //包邮费用设置为0
    Integer postAmount=0;
    Orders newOrder=new Orders();
    newOrder.setId(orderId);
    newOrder.setUserId(userId);
    newOrder.setReceiverName(address.getReceiver());
    newOrder.setReceiverMobile(address.getMobile());
    newOrder.setReceiverAddress(
        address.getProvince()+""+address.getCity()+""+
            address.getDistrict()+""+address.getDetail());
    newOrder.setPostAmount(postAmount);

    newOrder.setPayMethod(payMethod);
    newOrder.setLeftMsg(leftMsg);
    newOrder.setIsComment(YesOrNo.NO.type);
    newOrder.setIsDelete(YesOrNo.NO.type);
    newOrder.setCreatedTime(new Date());
    newOrder.setUpdatedTime(new Date());

    //循环itemSpecIds子订单信息
    String itemSpecIdArr[]=itemSpecIds.split(",");
    Integer totalAmount=0;//商品价格累计，单位、分
    Integer realPayAmount=0;//实际支付价格，单位、分
    for (String itemSpecId:itemSpecIdArr){
      //根据规格id查询具体信息，主要获取价格
      ItemsSpec itemsSpec=itemService.queryItemsBySpecId(itemSpecId);
      // TODO 整合redis后，购买数量重新从redis获取,先暂定1
      int byCounts=1;
      totalAmount +=itemsSpec.getPriceNormal()*byCounts;//普通价格*购买数量
      realPayAmount +=itemsSpec.getPriceDiscount()*byCounts;

      //根据商品id获取商品信息及图片
      String itemId= itemsSpec.getItemId();
      Items item=itemService.queryItemById(itemId);
      String imgUrl=itemService.queryItemMainImgById(itemId);
      //循环保存子订单数据到数据库
      OrderItems subOrderItem=new OrderItems();
      String subOrderId=sid.nextShort();
      subOrderItem.setId(subOrderId);
      subOrderItem.setOrderId(orderId);
      subOrderItem.setItemId(itemId);
      subOrderItem.setItemName(item.getItemName());
      subOrderItem.setItemImg(imgUrl);
      subOrderItem.setBuyCounts(byCounts);
      subOrderItem.setItemSpecId(itemSpecId);
      subOrderItem.setItemSpecName(itemsSpec.getName());
      subOrderItem.setPrice(itemsSpec.getPriceDiscount());
      //1.1子订单数据循环保存
      orderItemsMapper.insert(subOrderItem);
      //1.1.1用户提交订单后规格表需要减少库存
      //如果抛出异常回滚
      itemService.decreaseItemSpecStock(itemSpecId,byCounts);

    }
    //价格要计算
    newOrder.setTotalAmount(totalAmount);
    //真实付款价格要
    newOrder.setRealPayAmount(realPayAmount);
    //1.2保存订单基本信息
    ordersMapper.insert(newOrder);

    //1.3保存订单状态(待支付)
    OrderStatus waitPayOrderStatus=new OrderStatus();
    waitPayOrderStatus.setOrderId(orderId);
    waitPayOrderStatus.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
    waitPayOrderStatus.setCloseTime(new Date());
    orderStatusMapper.insert(waitPayOrderStatus);

    //构建商户订单，用于传给支付中心
    MerchantOrdersVO merchantOrdersVO=new MerchantOrdersVO();
    merchantOrdersVO.setMerchantOrderId(orderId);
    merchantOrdersVO.setMerchantUserId(userId);
    merchantOrdersVO.setAmount(realPayAmount+totalAmount);
    merchantOrdersVO.setPayMethod(payMethod);

    //构建自定义订单VO
    OrderVO orderVO=new OrderVO();
    orderVO.setOrderId(orderId);
    orderVO.setMerchantOrdersVO(merchantOrdersVO);


    return orderVO;
}
  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public void updateOrderStatus(String orderId, Integer orderStatus) {
    OrderStatus paidStatus =new OrderStatus();
    paidStatus.setOrderId(orderId);
    paidStatus.setOrderStatus(orderStatus);
    paidStatus.setPayTime(new Date());
    orderStatusMapper.updateByPrimaryKeySelective(paidStatus);

  }
  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public void closeOrder() {
    //查询所有未付款订单，判断时间是否超时，1天，超时关闭交易
    OrderStatus queryOrder=new OrderStatus();
    queryOrder.setOrderStatus(OrderStatusEnum.WAIT_PAY.type);
    List<OrderStatus> list=orderStatusMapper.select(queryOrder);
    for(OrderStatus os:list){
      //获得订单创建时间
      Date createTime=os.getCreatedTime();
      //和当前时间进行对比
      int days=DateUtil.daysBetween(createTime,new Date());
      if(days>=1){
        //超过1天，关闭订单
        doCloseOrder(os.getOrderId());
      }
    }

  }

  @Transactional(propagation = Propagation.REQUIRED)
  void doCloseOrder(String orderId){
    OrderStatus close=new OrderStatus();
    close.setOrderId(orderId);
    close.setOrderStatus(OrderStatusEnum.CLOSE.type);
    close.setCloseTime(new Date());
    orderStatusMapper.updateByPrimaryKeySelective(close);
  }

}
