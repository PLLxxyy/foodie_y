package com.imooc.controller.center;


import com.imooc.controller.BaseController;
import com.imooc.pojo.Orders;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.CenterUserBO;
import com.imooc.resource.FileUpload;
import com.imooc.service.center.CenterUserService;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.DateUtil;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "用户中心我的订单",tags = "用户中心我的订单")
@RestController
@RequestMapping("myorders")
public class MyOrdersController extends BaseController {

  @Autowired
  private MyOrdersService myOrdersService;


  @ApiOperation(value = "查询订单列表",notes = "查询订单列表",httpMethod = "POST")
  @PostMapping("/query")
  public IMOOCJSONResult comments(
      @ApiParam(name="userId",value = "用户id",required = true)
      @RequestParam String userId,
      @ApiParam(name="orderStatus",value = "订单状态",required = false)
      @RequestParam Integer orderStatus,
      @ApiParam(name="page",value = "查询下一页的第几页",required = false)
      @RequestParam Integer page,
      @ApiParam(name="pageSize",value = "每一页显示的条数",required = false)
      @RequestParam Integer pageSize
  ){
    if(StringUtils.isBlank(userId)){
      return IMOOCJSONResult.errorMsg(null);
    }
    if(page==null){
      page=1;
    }
    if(pageSize==null){
      pageSize=COMMEN_PAGE_SIZE;
    }
    //分页不适合嵌套结果集
    PagedGridResult grid= myOrdersService.queryMyOrders(userId, orderStatus, page, pageSize);
    return IMOOCJSONResult.ok(grid);
  }

  //上家发货后没有后端，这个接口仅仅是模拟
  @ApiOperation(value = "商家发货",notes = "商家发货",httpMethod = "GET")
  @GetMapping("/deliver")
  public IMOOCJSONResult deliver(
      @ApiParam(name="orderId",value = "订单id",required = true)
      @RequestParam String orderId){

    if (StringUtils.isBlank(orderId)){
      return IMOOCJSONResult.errorMsg(null);
    }
    myOrdersService.updateDeliverOrderStatus(orderId);
    return IMOOCJSONResult.ok();

  }

  //确认收货
  @ApiOperation(value = "用户确认收货",notes = "用户确认收货",httpMethod = "POST")
  @PostMapping("/confirmReceive")
  public IMOOCJSONResult deliver(
      @ApiParam(name="orderId",value = "订单id",required = true)
  @RequestParam String orderId,
      @ApiParam(name="userId",value = "用户id",required = true)
      @RequestParam String userId){

    IMOOCJSONResult checkResult=checkUserOrder(orderId,userId);
    if(checkResult.getStatus()!= HttpStatus.OK.value()){
      return checkResult;//不是200，直接返回结果
    }
   boolean res= myOrdersService.updateReceiveOrderStatus(orderId);
    if(!res){
      return IMOOCJSONResult.errorMsg("订单确认收货失败");
    }
    return IMOOCJSONResult.ok();
  }

  //删除订单
  @ApiOperation(value = "用户删除订单",notes = "用户删除订单",httpMethod = "POST")
  @PostMapping("/delete")
  public IMOOCJSONResult delete(
      @ApiParam(name="orderId",value = "订单id",required = true)
      @RequestParam String orderId,
      @ApiParam(name="userId",value = "用户id",required = true)
      @RequestParam String userId){
    IMOOCJSONResult checkResult=checkUserOrder(orderId,userId);
    if(checkResult.getStatus()!= HttpStatus.OK.value()){
      return checkResult;//不是200，直接返回结果
    }
    boolean res=myOrdersService.deleteOrder(orderId,userId);
    if(!res){
      return IMOOCJSONResult.errorMsg("订单确认收货失败");
    }
    return IMOOCJSONResult.ok();
  }



}
