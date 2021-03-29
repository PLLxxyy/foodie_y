package com.imooc.controller;


import com.imooc.enums.YesOrNo;
import com.imooc.pojo.Carousel;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;
import com.imooc.service.CarouselService;
import com.imooc.service.CategoryService;
import com.imooc.utils.IMOOCJSONResult;
import io.swagger.annotations.Api;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "首页",tags = "首页展示的相关接口")
@RestController
@RequestMapping("index")
public class IndexController {

  @Autowired
  private CarouselService carouselService;
  @Autowired
  private CategoryService categoryService;

  @ApiOperation(value = "获取首页轮播图列表",notes = "获取首页轮播图列表",httpMethod = "GET")
  @GetMapping("/carousel")
  public IMOOCJSONResult carousel(){
    List<Carousel> list=carouselService.queryAll(YesOrNo.YES.type);
    return IMOOCJSONResult.ok(list);
  }

  /*
  * 首页分类展示需求：
  * 1、第一次刷新主页大分类，渲染展示到首页
  * 2、如果鼠标移到大分类，则加载其子分类的内容，
  *     如果已经存在子分类，则不需要加载（懒加载）
  * */

  @ApiOperation(value = "获取商品一级分类",notes = "获取商品一级分类",httpMethod = "GET")
  @GetMapping("/cats")
  public IMOOCJSONResult cats(){
    List<Category> list=categoryService.queryAllRootLevelCat();
    return IMOOCJSONResult.ok(list);
  }

  /*
  * 获取商品子分类
  * */
  @ApiOperation(value = "获取商品子分类",notes = "获取商品子分类",httpMethod = "GET")
  @GetMapping("/subCat/{rootCatId}")
  public IMOOCJSONResult subCat(
        @ApiParam(name ="rootCatId",value = "一级分类id",required = true)
        @PathVariable Integer rootCatId){
    if (rootCatId ==null){
      return IMOOCJSONResult.errorMsg("分类不存在");
    }
    List<CategoryVO> list=categoryService.getSubCatList(rootCatId);
    return IMOOCJSONResult.ok(list);
  }

  /*
   * 获取商品子分类
   * */
  @ApiOperation(value = "查询每个一级分类下的6个商品",notes = "查询每个一级分类下的6个商品",httpMethod = "GET")
  @GetMapping("/sixNewItems/{rootCatId}")
  public IMOOCJSONResult sixNewItems(
      @ApiParam(name ="rootCatId",value = "一级分类id",required = true)
      @PathVariable Integer rootCatId){
    if (rootCatId ==null){
      return IMOOCJSONResult.errorMsg("分类不存在");
    }
    List<NewItemsVO> list=categoryService.getSixNewItemsLazy(rootCatId);
    return IMOOCJSONResult.ok(list);
  }


}
