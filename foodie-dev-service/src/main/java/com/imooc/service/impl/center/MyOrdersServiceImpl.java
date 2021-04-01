package com.imooc.service.impl.center;


import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.OrderStatusEnum;
import com.imooc.enums.YesOrNo;
import com.imooc.mapper.OrderStatusMapper;
import com.imooc.mapper.OrdersMapper;
import com.imooc.mapper.OrdersMapperCustom;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.OrderStatus;
import com.imooc.pojo.Orders;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.CenterUserBO;
import com.imooc.pojo.vo.MyOrdersVO;
import com.imooc.service.center.CenterUserService;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.PagedGridResult;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;
import tk.mybatis.mapper.entity.Example.Criteria;


@Service
public class MyOrdersServiceImpl implements MyOrdersService {

  @Autowired
  public OrdersMapperCustom ordersMapperCustom;
  @Autowired
  public OrderStatusMapper orderStatusMapper;

  @Autowired
  public OrdersMapper ordersMapper;
  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public PagedGridResult queryMyOrders(String userId, Integer orderStatus,
      Integer page,
      Integer pageSize) {

    Map<String,Object> map=new HashMap<>();
    map.put("userId",userId);
    if(orderStatus !=null){
      map.put("orderStatus",orderStatus);
    }

    PageHelper.startPage(page,pageSize);
    List<MyOrdersVO> list=ordersMapperCustom.queryMyOrders(map);
    return setterPageGrid(list,page);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public void updateDeliverOrderStatus(String orderId) {
    OrderStatus updateOrder=new OrderStatus();
    updateOrder.setOrderStatus(OrderStatusEnum.WAIT_RECEIVE.type);
    updateOrder.setDeliverTime(new Date());

    Example example=new Example(OrderStatus.class);
    Criteria criteria=example.createCriteria();
    criteria.andEqualTo("orderId",orderId);
    criteria.andEqualTo("orderStatus",OrderStatusEnum.WAIT_RECEIVE.type);
    orderStatusMapper.updateByExampleSelective(updateOrder,example);

  }

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public Orders queryMyOrder(String orderId, String userId) {
    Orders orders=new Orders();
    orders.setUserId(userId);
    orders.setId(orderId);
    orders.setIsDelete(YesOrNo.NO.type);
    return ordersMapper.selectOne(orders);
  }

  /*
  * 更改订单状态，确认收货
  * */
  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public boolean updateReceiveOrderStatus(String orderId) {
    OrderStatus orderStatus=new OrderStatus();
    orderStatus.setOrderStatus(OrderStatusEnum.SUCCESS.type);
    orderStatus.setSuccessTime(new Date());

    Example example=new Example(OrderStatus.class);
    Criteria criteria=example.createCriteria();
    criteria.andEqualTo("orderId",orderId);
    criteria.andEqualTo("orderStatus",OrderStatusEnum.WAIT_RECEIVE.type);
    int result=orderStatusMapper.updateByExampleSelective(orderStatus,example);
    return result==1?true:false;
  }

  /*
   * 删除订单
   * */
  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public boolean deleteOrder(String orderId,String userId) {
    Orders orders=new Orders();
    orders.setIsDelete(YesOrNo.YES.type);
    orders.setUpdatedTime(new Date());

    Example example=new Example(Orders.class);
    Criteria criteria=example.createCriteria();
    criteria.andEqualTo("id",orderId);
    criteria.andEqualTo("userId",userId);
    int result=ordersMapper.updateByExampleSelective(orders,example);
    return result==1?true:false;
  }

  /*分页方法*/
  private PagedGridResult setterPageGrid(List<?> list,Integer page){
    PageInfo<?> pageList=new PageInfo<>(list);
    PagedGridResult grid=new PagedGridResult();
    grid.setPage(page);
    grid.setRows(list);
    grid.setTotal(pageList.getPages());
    grid.setRecords(pageList.getTotal());
    return grid;
  }
}
