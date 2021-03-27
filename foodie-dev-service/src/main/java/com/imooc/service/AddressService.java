package com.imooc.service;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import java.util.List;

public interface AddressService {

  /*
  * 根据用户id查询用户的收货地址
  * */
  public List<UserAddress> queryAll(String userId);

  /*
  * 新增地址
  * */
  public void addNewAddress(AddressBO addressBO);

  /*
   * 修改地址
   * */
  public void updateAddress(AddressBO addressBO);

  /*
  * 根据用户id和地址id删除地址
  * */
  public void deleteAddress(String userId, String addressId);



  /*
   * 根据用户id和地址id设置默认地址
   * */
  public void updateUserAddressToBeDefault(String userId, String addressId);

  /*
   * 根据用户id和地址id,查询具体的用户对象信息
   * */
  public UserAddress queryUserAddress(String userId, String addressId);
}
