package com.imooc.service.impl;

import com.imooc.mapper.CategoryMapper;
import com.imooc.mapper.CategoryMapperCustomer;
import com.imooc.pojo.Category;
import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;
import com.imooc.service.CategoryService;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

@Service
public class CategoryServiceImpl implements CategoryService {

  @Autowired
  private CategoryMapper categoryMapper;

  @Autowired
  private CategoryMapperCustomer categoryMapperCustomer;

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public List<Category> queryAllRootLevelCat() {
    Example example=new Example(Category.class);
    Example.Criteria criteria=example.createCriteria();
    criteria.andEqualTo("type",1);
    List<Category> list=categoryMapper.selectByExample(example);
    return list;
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public List<CategoryVO> getSubCatList(Integer rootCatId) {
    return categoryMapperCustomer.getSubCatList(rootCatId);
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public List<NewItemsVO> getSixNewItemsLazy(Integer rootCatId) {
    Map<String,Object> map=new HashMap<>();
    map.put("rootCatId",rootCatId);
    return categoryMapperCustomer.getSixNewItemsLazy(map);
  }
}
