package com.imooc.service.center;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.CenterUserBO;
import com.imooc.utils.PagedGridResult;

public interface MyOrdersService {

  //判断用户是否存在
  public PagedGridResult queryMyOrders(String userId,
            Integer orderStatus,
            Integer page,
            Integer pageSize);


}
