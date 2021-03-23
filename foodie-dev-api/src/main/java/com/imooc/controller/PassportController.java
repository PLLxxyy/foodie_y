package com.imooc.controller;

import com.imooc.pojo.Users;
import com.imooc.pojo.bo.UserBO;
import com.imooc.service.UserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import com.imooc.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "注册登录", tags = "用于注册登录的相关接口")
@RestController
@RequestMapping("passport")
public class PassportController {

  @Autowired private UserService userService;

  @ApiOperation(value = "用户名是否存在", notes = "用户名是否存在", httpMethod = "GET")
  @GetMapping("/usernameIsExist")
  public IMOOCJSONResult usernameIsExist(@RequestParam String username) {
    // 判断用户名不能为空
    if (StringUtils.isBlank(username)) {
      return IMOOCJSONResult.errorMsg("用户名不能为空");
    }
    // 查找注册的name是否存在
    boolean res = userService.queryUsernameIsExist(username);
    if (res) {
      return IMOOCJSONResult.errorMsg("用户名已经存在");
    }
    return IMOOCJSONResult.ok();
  }

  @ApiOperation(value = "用户注册", notes = "用户注册", httpMethod = "POST")
  @PostMapping("/regist")
  public IMOOCJSONResult regist(
      @RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response) {
    String username = userBO.getUsername();
    String password = userBO.getPassword();
    String confirmPwd = userBO.getConfirmPassword();

    // 判断用户名和密码必须不为空
    if (StringUtils.isBlank(username)
        || StringUtils.isBlank(password)
        || StringUtils.isBlank(confirmPwd)) {
      return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
    }

    // 查询用户名是否存在
    boolean res = userService.queryUsernameIsExist(username);
    if (res) {
      return IMOOCJSONResult.errorMsg("用户名已经存在");
    }
    // 密码长度不小于6位
    if (password.length() < 6) {
      return IMOOCJSONResult.errorMsg("密码长度不能少于6位");
    }
    // 判断两次密码是否一致
    if (!password.equals(confirmPwd)) {
      return IMOOCJSONResult.errorMsg("两次密码输入不一致");
    }
    // 实现注册
    Users userRes = userService.createUser(userBO);
    setNullProperty(userRes);
    CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(userRes), true);
    return IMOOCJSONResult.ok();
  }

  @ApiOperation(value = "用户登录", notes = "用户登录", httpMethod = "POST")
  @PostMapping("/login")
  public IMOOCJSONResult login(
      @RequestBody UserBO userBO, HttpServletRequest request, HttpServletResponse response)
      throws Exception {
    String username = userBO.getUsername();
    String password = userBO.getPassword();

    // 判断用户名和密码必须不为空
    if (StringUtils.isBlank(username) || StringUtils.isBlank(password)) {
      return IMOOCJSONResult.errorMsg("用户名或密码不能为空");
    }

    // 实现登录
    Users res = userService.queryUserForLogin(username, MD5Utils.getMD5Str(password));

    if (res == null) {
      return IMOOCJSONResult.errorMsg("用户名或密码不正确");
    }
    res = setNullProperty(res);
    CookieUtils.setCookie(request, response, "user", JsonUtils.objectToJson(res), true);
    return IMOOCJSONResult.ok();
  }

  private Users setNullProperty(Users user) {
    user.setPassword(null);
    user.setEmail(null);
    return user;
  }

  @ApiOperation(value = "用户退出登录", notes = "用户退出登录", httpMethod = "POST")
  @PostMapping("/logout")
  public IMOOCJSONResult logout(
      @RequestParam String userId, HttpServletRequest request, HttpServletResponse response) {

    // 清除用户相关信息cookie
    CookieUtils.deleteCookie(request, response, "user");

    // 用户退出登录，需要清空购物车
    // 分布式会话中需要清除用户数据

    return IMOOCJSONResult.ok();
  }
}
