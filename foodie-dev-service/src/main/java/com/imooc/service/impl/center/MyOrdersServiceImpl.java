package com.imooc.service.impl.center;


import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.CenterUserBO;
import com.imooc.service.center.CenterUserService;
import com.imooc.service.center.MyOrdersService;
import java.util.Date;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;


@Service
public class MyOrdersServiceImpl implements MyOrdersService {

  @Autowired private UsersMapper usersMapper;



  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public Users queryUserInfo(String userId) {
    Users user=usersMapper.selectByPrimaryKey(userId);
    user.setPassword(null);
    return user;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public Users updateUserInfo(String userId, CenterUserBO centerUserBO) {
    Users updateUser=new Users();
    BeanUtils.copyProperties(centerUserBO,updateUser);
    updateUser.setId(userId);
    updateUser.setUpdatedTime(new Date());
    usersMapper.updateByPrimaryKeySelective(updateUser);
    return queryUserInfo(userId);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public Users updateUserFace(String userId, String faceUrl) {
    Users updateUser=new Users();
    updateUser.setId(userId);
    updateUser.setFace(faceUrl);
    updateUser.setUpdatedTime(new Date());
    return updateUser;
  }
}
