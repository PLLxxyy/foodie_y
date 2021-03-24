package com.imooc.service;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;

public interface UserService {

  //判断用户是否存在
  public boolean queryUsernameIsExist(String username);

  //UserBO--前端传给后端的数据
  public Users createUser(UserBO userBO);

  //检索用户名和密码是否匹配，用于登录
  public Users queryUserForLogin(String username,String password);

}