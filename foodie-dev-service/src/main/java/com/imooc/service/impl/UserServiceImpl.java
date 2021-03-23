package com.imooc.service.impl;

import com.imooc.enums.Sex;
import com.imooc.mapper.UsersMapper;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UserService;
import com.imooc.utils.DateUtil;
import com.imooc.utils.MD5Utils;
import java.util.Date;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

@Service
public class UserServiceImpl implements UserService {

  @Autowired
  private UsersMapper usersMapper;
  @Autowired
  private Sid sid;

  public static final String USER_FACE = "C:/Users/blueship/Desktop";

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public boolean queryUsernameIsExist(String username) {
    Example example=new Example(Users.class);
    Example.Criteria userCriteria=example.createCriteria();
    userCriteria.andEqualTo("username",username);
    Users res=usersMapper.selectOneByExample(example);

    return res== null?false:true;
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public Users createUser(UserBO userBO) {

    String userId=sid.nextShort();
    Users user=new Users();
    user.setId(userId);
    user.setUsername(userBO.getUsername());
    try {
      user.setPassword(MD5Utils.getMD5Str(userBO.getPassword()));
    } catch (Exception e) {
      e.printStackTrace();
    }
    //默认昵称和用户名一样
    user.setNickname(userBO.getUsername());
    //头像
    user.setFace(USER_FACE);
    //设置默认生日
    user.setBirthday(DateUtil.stringToDate("1993-01-01"));
    user.setSex(Sex.secret.type);
    user.setCreatedTime(new Date());
    user.setUpdatedTime(new Date());

    usersMapper.insert(user);
    return user;
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public Users queryUserForLogin(String username, String password) {
    Example userExample=new Example(Users.class);
    Example.Criteria userCriteria=userExample.createCriteria();
    userCriteria.andEqualTo("username",username);
    userCriteria.andEqualTo("password",password);
    Users u=usersMapper.selectOneByExample(userExample);
    return u;
  }
}
