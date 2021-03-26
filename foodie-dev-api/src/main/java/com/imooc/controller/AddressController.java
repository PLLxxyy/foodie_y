package com.imooc.controller;

import com.imooc.pojo.UserAddress;
import com.imooc.pojo.bo.AddressBO;
import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.service.AddressService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.MobileEmailUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "地址相关", tags = "地址相关接口相关api")
@RequestMapping("address")
@RestController
public class AddressController {

  @Autowired
  private AddressService addressService;

  //用户在确认订单页面，可以针对收货地址做如下操作
  /*
  * 查询，新增，删除，修改，设置默认地址
  * */
  @ApiOperation(value = "根据用户id查询收货地址列表", notes = "根据用户id查询收货地址列表", httpMethod = "POST")
  @PostMapping("/list")
  public IMOOCJSONResult list(
      @RequestParam String userId){
    if (StringUtils.isBlank(userId)) {
      return IMOOCJSONResult.errorMsg("");
    }
    List<UserAddress> list=addressService.queryAll(userId);
    return IMOOCJSONResult.ok(list);
  }


  @ApiOperation(value = "用户新增地址", notes = "用户新增地址", httpMethod = "POST")
  @PostMapping("/add")
  public IMOOCJSONResult add(@RequestBody AddressBO addressBO){

    IMOOCJSONResult checkRes=checkAddress(addressBO);
    if (checkRes.getStatus()!=200){
      return checkRes;
    }
    addressService.addNewAddress(addressBO);
    return IMOOCJSONResult.ok();
  }

  @ApiOperation(value = "用户修改地址", notes = "用户修改地址", httpMethod = "POST")
  @PostMapping("/update")
  public IMOOCJSONResult update(@RequestBody AddressBO addressBO){

   if(StringUtils.isBlank(addressBO.getAddressId())){
     return IMOOCJSONResult.errorMsg("修改地址错误：addressId不能为空");
   }
    IMOOCJSONResult checkRes=checkAddress(addressBO);
    if (checkRes.getStatus()!=200){
      return checkRes;
    }
    addressService.updateAddress(addressBO);
    return IMOOCJSONResult.ok();
  }


  @ApiOperation(value = "删除地址", notes = "删除地址", httpMethod = "POST")
  @PostMapping("/delete")
  public IMOOCJSONResult delete(@RequestParam String userId,@RequestParam String addressId){

    if(StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)){
      return IMOOCJSONResult.errorMsg("");
    }
    addressService.deleteAddress(userId,addressId);
    return IMOOCJSONResult.ok();
  }


  @ApiOperation(value = "设置默认地址", notes = "设置默认地址", httpMethod = "POST")
  @PostMapping("/setDefalut")
  public IMOOCJSONResult setDefalut(@RequestParam String userId,@RequestParam String addressId){

    if(StringUtils.isBlank(userId) || StringUtils.isBlank(addressId)){
      return IMOOCJSONResult.errorMsg("");
    }

    addressService.updateUserAddressToBeDefault(userId,addressId);
    return IMOOCJSONResult.ok();
  }
  //校验地址信息方法
  private IMOOCJSONResult checkAddress(AddressBO addressBO){
    String receiver=addressBO.getReceiver();
    if(StringUtils.isBlank(receiver)){
      return IMOOCJSONResult.errorMsg("收货人不能为空");
    }
    if(receiver.length()>12){
      return IMOOCJSONResult.errorMsg("收货人姓名不能太长");
    }
    String mobile=addressBO.getMobile();
    if(StringUtils.isBlank(mobile)){
      return IMOOCJSONResult.errorMsg("收货人手机号不能为空");
    }
    if(mobile.length() !=11){
      return IMOOCJSONResult.errorMsg("收货人手机号长度不正确");
    }
    boolean isMobileOK=MobileEmailUtils.checkMobileIsOk(mobile);

    if(!isMobileOK){
      return IMOOCJSONResult.errorMsg("收货人手机号格式不正确！");
    }
    String province = addressBO.getProvince();
    String city = addressBO.getCity();
    String district = addressBO.getDistrict();
    String detail = addressBO.getDetail();
    if (StringUtils.isBlank(province) ||
        StringUtils.isBlank(city) ||
        StringUtils.isBlank(district) ||
        StringUtils.isBlank(detail)) {
      return IMOOCJSONResult.errorMsg("收货地址信息不能为空");
    }
    return IMOOCJSONResult.ok();
  }

}
