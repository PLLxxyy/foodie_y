package com.imooc.service.impl;

import com.imooc.mapper.CarouselMapper;
import com.imooc.pojo.Carousel;
import com.imooc.service.CarouselService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

@Service
public class CarouselServiceImpl implements CarouselService {

  @Autowired
  private CarouselMapper carouselMapper;

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public List<Carousel> queryAll(Integer isShow) {
    Example example=new Example(Carousel.class);
    //根据sort排序，倒序
    example.orderBy("sort").desc();
    Example.Criteria criteria=example.createCriteria();
    criteria.andEqualTo("isShow");
    List<Carousel> result=carouselMapper.selectByExample(example);
    return result;
  }
}
