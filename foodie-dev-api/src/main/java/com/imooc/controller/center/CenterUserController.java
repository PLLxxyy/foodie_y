package com.imooc.controller.center;


import com.imooc.controller.BaseController;
import com.imooc.pojo.Users;
import com.imooc.pojo.bo.CenterUserBO;
import com.imooc.resource.FileUpload;
import com.imooc.service.center.CenterUserService;
import com.imooc.utils.CookieUtils;
import com.imooc.utils.DateUtil;
import com.imooc.utils.IMOOCJSONResult;
import com.imooc.utils.JsonUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(value = "用户信息接口",tags = "用户信息接口相关api")
@RestController
@RequestMapping("userInfo")
public class CenterUserController extends BaseController {

  @Autowired
  private CenterUserService centerUserService;
  @Autowired
  private FileUpload fileUpload;

  @ApiOperation(value = "修改用户信息",notes = "修改用户信息",httpMethod = "POST")
  @PostMapping("update")
  public IMOOCJSONResult update(
      @ApiParam(name="userId",value = "用户id",required = true)
      @RequestParam String userId,
      @RequestBody @Valid CenterUserBO centerUserBO,
      BindingResult result,//绑定校验的错误信息
      HttpServletRequest request, HttpServletResponse response)
  {
    //判定是否有错误的验证信息
    if(result.hasErrors()){
      Map<String,String> map=getErrors(result);
      return IMOOCJSONResult.errorMap(map);
    }

    Users user= centerUserService.updateUserInfo(userId,centerUserBO);

    user=setNullProperty(user);
    CookieUtils.setCookie(request,response,"user",
        JsonUtils.objectToJson(user),true);

    //TODO 后续要改，增加令牌token,会整合redis，分布式会话
    return IMOOCJSONResult.ok(user);
  }





  @ApiOperation(value = "用户头像修改",notes = "用户头像修改",httpMethod = "POST")
  @PostMapping("uploadFace")
  public IMOOCJSONResult uploadFace(
      @ApiParam(name="userId",value = "用户id",required = true)
      @RequestParam String userId,
      @ApiParam(name="file",value = "用户头像",required = true)
     MultipartFile file,//上传文件
      HttpServletRequest request, HttpServletResponse response)
  {
    //定义头像保存的地址
    String fileSpace=fileUpload.getImageUserFaceLocation();
    //String fileSpace=IMAGE_USER_FACE_LOCATION;
    //在路径上为每一个用户增加一个userid,用于区分不同的用户上传
    String uploadPathPrefix = File.separator+userId;
    //开始文件上传
    if(file !=null){
      FileOutputStream outputStream=null;
      try{
        //获得文件名称
        String fileName=file.getOriginalFilename();
        if (StringUtils.isNoneBlank(fileName)){
          //文件重命名
          String fileNameArr[]=fileName.split("\\.");
          //获取文件后缀名，后缀判读N
          String suffix=fileNameArr[fileNameArr.length-1];
          if (!suffix.equalsIgnoreCase("png") &&
              !suffix.equalsIgnoreCase("jpg") &&
              !suffix.equalsIgnoreCase("jpeg") ) {
            return IMOOCJSONResult.errorMsg("图片格式不正确！");
          }
          //face-{userId}.png
          //文件重名命
          String newFileName="face-"+userId+"."+suffix;

          //上传头像最终保存的位置
          String finalFacePath=fileSpace+uploadPathPrefix+File.separator+newFileName;
          uploadPathPrefix+=("/" + newFileName);
          File outFile=new File(finalFacePath);
          if(outFile.getParentFile() !=null){
            //创建文件夹
            outFile.getParentFile().mkdirs();
          }
          //文件输出保存到目录
          outputStream=new FileOutputStream(outFile);
          InputStream inputStream=file.getInputStream();
          IOUtils.copy(inputStream,outputStream);
        }
      }catch (IOException e){
        e.printStackTrace();
      }finally{
        try {
          if(outputStream!=null){
            outputStream.flush();
            outputStream.close();
          }
        } catch (IOException e) {
          e.printStackTrace();
        }
      }


    }else {
      return IMOOCJSONResult.errorMsg("文件不能为空");
    }

    //获得图片地址
    String imageServerUrl=fileUpload.getImageServerUrl();

    //由于浏览器可能存在缓存的情况，所以在这里，加上时间戳，保证更新后的图片可以及时更新
    String finalUserFaceUrl=imageServerUrl+uploadPathPrefix
        +"?t="+ DateUtil.getCurrentDateString(DateUtil.DATETIME_PATTERN);
    //更新用户头像到数据库
    Users userResult=centerUserService.updateUserFace(userId,finalUserFaceUrl);

    userResult=setNullProperty(userResult);
    CookieUtils.setCookie(request,response,"user",
        JsonUtils.objectToJson(userResult),true);

    //TODO 后续要改，增加令牌token,会整合redis，分布式会话

    return IMOOCJSONResult.ok();
  }


  //没必要传递到前端的数据设置为nll
  private Users setNullProperty(Users user) {
    user.setPassword(null);
    user.setMobile(null);
    user.setCreatedTime(null);
    user.setUpdatedTime(null);
    user.setBirthday(null);
    user.setEmail(null);
    return user;
  }

  private Map<String,String> getErrors(BindingResult result){
    Map<String,String> map = new HashMap<>();

    List<FieldError> errorList=result.getFieldErrors();
    for (FieldError error:errorList){
      //发生校验错误所对应的某一个属性
      String errorField=error.getField();
      //校验错误信息
      String errorMsg= error.getDefaultMessage();
      map.put(errorField,errorMsg);
    }
    return map;
  }



}
