package com.imooc.service;

import com.imooc.pojo.Carousel;
import java.util.List;

public interface CarouselService {

  /*
  * 查询所有包轮播图列表
  * */
  public List<Carousel> queryAll(Integer isShow);

}
