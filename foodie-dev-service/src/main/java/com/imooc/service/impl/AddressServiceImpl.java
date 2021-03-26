package com.imooc.service.impl;

import com.imooc.enums.YesOrNo;
import com.imooc.mapper.UserAddressMapper;
import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.service.AddressService;
import java.util.Date;
import java.util.List;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddressServiceImpl implements AddressService {

  @Autowired
  private UserAddressMapper userAddressMapper;

  @Autowired
  private Sid sid;

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public List<UserAddress> queryAll(String userId) {
    UserAddress ua=new UserAddress();
    ua.setUserId(userId);
    return userAddressMapper.select(ua);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public void addNewAddress(AddressBO addressBO) {
    //判断当前用户是否存在地址，如果没有，则新增为默认地址
    List<UserAddress> list=this.queryAll(addressBO.getUserId());
    Integer isDefaul=0;
    if(list==null|| list.isEmpty() || list.size()==0){
      isDefaul=1;
    }
    //保存地址到数据库
    String addressId=sid.nextShort();
    UserAddress newAddress=new UserAddress();
    BeanUtils.copyProperties(addressBO,newAddress);
    newAddress.setId(addressId);
    newAddress.setIsDefault(isDefaul);
    newAddress.setCreatedTime(new Date());
    newAddress.setUpdatedTime(new Date());
    userAddressMapper.insert(newAddress);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public void updateAddress(AddressBO addressBO) {
    String addressId=addressBO.getAddressId();
    UserAddress pendingAddress=new UserAddress();
    BeanUtils.copyProperties(addressBO,pendingAddress);
    pendingAddress.setId(addressId);
    pendingAddress.setUpdatedTime(new Date());
    //updateByPrimaryKeySelective--只会更新不是null的字段，可以更新耨一些字段
    //updateByPrimaryKey---会把空的字段也更新上去
    userAddressMapper.updateByPrimaryKeySelective(pendingAddress);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public void deleteAddress(String userId, String addressId) {
    UserAddress ua=new UserAddress();
    ua.setId(addressId);
    ua.setUserId(userId);
    userAddressMapper.delete(ua);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public void updateUserAddressToBeDefault(String userId, String addressId) {

    //查找默认地址，改为非默认
    UserAddress queryAddress=new UserAddress();
    queryAddress.setUserId(userId);
    queryAddress.setIsDefault(YesOrNo.YES.type);
    List<UserAddress> list=userAddressMapper.select(queryAddress);
    //为了防止有多个默认地址
    for (UserAddress ua:list){
      ua.setIsDefault(YesOrNo.NO.type);
      userAddressMapper.updateByPrimaryKeySelective(ua);
    }
    //根据地址id设置默认地址
    UserAddress defaultAddress=new UserAddress();
    defaultAddress.setId(addressId);
    defaultAddress.setUserId(userId);
    defaultAddress.setIsDefault(YesOrNo.YES.type);
    userAddressMapper.updateByPrimaryKeySelective(defaultAddress);
  }
}
