package com.imooc.service.center;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.CenterUserBO;

public interface CenterUserService {

  //判断用户是否存在
  public Users queryUserInfo(String userId);

  //修改用户信息
  public Users updateUserInfo(String userId, CenterUserBO centerUserBO);

  //修改用户信息
  public Users updateUserFace(String userId, String faceUrl);

}
