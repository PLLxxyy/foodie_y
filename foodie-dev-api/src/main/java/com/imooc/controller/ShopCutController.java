package com.imooc.controller;

import com.imooc.pojo.bo.ShopcartBO;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "购物车接口controller", tags = "购物车接口相关api")
@RequestMapping("shopcart")
@RestController
public class ShopCutController {

  @ApiOperation(value = "添加商品到购物车", notes = "添加商品到购物车", httpMethod = "POST")
  @PostMapping("/add")
  public IMOOCJSONResult add(
      @RequestParam String userId,
      @RequestBody ShopcartBO shopcartBO,
      HttpServletRequest request,
      HttpServletResponse response) {
    if (StringUtils.isBlank(userId)) {
      return IMOOCJSONResult.errorMsg("");
      // TODO 前端用户登录的情况下，添加商品到购物车，会同时在后端同步数据到redis

    }
    System.out.println(shopcartBO);
    return IMOOCJSONResult.ok();
  }


  @ApiOperation(value = "从购物车删除商品", notes = "从购物车删除商品", httpMethod = "POST")
  @PostMapping("/del")
  public IMOOCJSONResult del(
      @RequestParam String userId,
      @RequestParam  String itemSpecId,
      HttpServletRequest request,
      HttpServletResponse response) {
    if (StringUtils.isBlank(userId) || StringUtils.isBlank(itemSpecId)) {
      return IMOOCJSONResult.errorMsg("");
    }
    // TODO 用户在页面删除购物车商品，如果此时用户已经登录，则需要同事删除redis
    return IMOOCJSONResult.ok();
  }
}
