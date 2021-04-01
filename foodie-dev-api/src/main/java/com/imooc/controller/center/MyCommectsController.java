package com.imooc.controller.center;


import com.imooc.controller.BaseController;
import com.imooc.enums.YesOrNo;
import com.imooc.pojo.OrderItems;
import com.imooc.pojo.Orders;
import com.imooc.pojo.bo.center.OrderItemsCommentBO;
import com.imooc.service.center.MyCommentsService;
import com.imooc.service.center.MyOrdersService;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.PagedGridResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "用户中心评价模块",tags = "用户中心评价模块")
@RestController
@RequestMapping("mycomments")
public class MyCommectsController extends BaseController {

  @Autowired
  private MyCommentsService myCommentsService;


  @ApiOperation(value = "查询订单列表",notes = "查询订单列表",httpMethod = "POST")
  @PostMapping("/pending")
  public IMOOCJSONResult pending(
      @ApiParam(name="userId",value = "用户id",required = true)
      @RequestParam String userId,
      @ApiParam(name="orderId",value = "id",required = true)
      @RequestParam String orderId
  ){
    //判断用户的订单是否关联
    IMOOCJSONResult checkResult=checkUserOrder(orderId,userId);
    if(checkResult.getStatus()!= HttpStatus.OK.value()){
      return checkResult;//不是200，直接返回结果
    }
    //判断该笔订单是否已经评价过了，评价过就不再继续
    Orders myOrder=(Orders)checkResult.getData();
    if(myOrder.getIsComment() == YesOrNo.YES.type){
      return IMOOCJSONResult.errorMsg("该笔订单已经评价");
    }
    List<OrderItems> list=myCommentsService.queryPendingComment(orderId);
    return IMOOCJSONResult.ok(list);
  }



  @ApiOperation(value = "保存评论列表",notes = "保存评论列表",httpMethod = "POST")
  @PostMapping("/saveList")
  public IMOOCJSONResult saveList(
      @ApiParam(name="userId",value = "用户id",required = true)
      @RequestParam String userId,
      @ApiParam(name="orderId",value = "id",required = true)
      @RequestParam String orderId,
      @RequestBody List<OrderItemsCommentBO> commentList
  ){
    System.out.println(commentList);
    //判断用户的订单是否关联
    IMOOCJSONResult checkResult=checkUserOrder(orderId,userId);
    if(checkResult.getStatus()!= HttpStatus.OK.value()){
      return checkResult;//不是200，直接返回结果
    }
    //判断评论内容是否为空
    if (commentList==null || commentList.isEmpty() || commentList.size()==0){
      return IMOOCJSONResult.errorMsg("评论内容不能为空！");
    }

    //保存评论
    List<OrderItems> list=myCommentsService.queryPendingComment(orderId);
    return IMOOCJSONResult.ok(list);
  }

}
