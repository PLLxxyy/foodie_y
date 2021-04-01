package com.imooc.service.impl.center;


import com.imooc.mapper.OrderItemsMapper;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.CenterUserBO;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.service.center.CenterUserService;
import com.imooc.service.center.MyCommentsService;
import java.util.Date;
import java.util.List;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MyCommentsServiceImpl implements MyCommentsService {

  @Autowired private OrderItemsMapper orderItemsMapper;



  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public List<OrderItems> queryPendingComment(String orderId) {
    OrderItems query=new OrderItems();
    query.setOrderId(orderId);
    return orderItemsMapper.select(query);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public void saveComments(String orderId, String userId, List<OrderItemsCommentBO> commentList) {
    //保存评价
  }
}
