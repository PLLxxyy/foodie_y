package com.imooc.mapper;


import com.imooc.pojo.vo.CategoryVO;
import com.imooc.pojo.vo.NewItemsVO;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface CategoryMapperCustomer {
  public List<CategoryVO> getSubCatList(Integer rootCatId);

  public List<NewItemsVO> getSixNewItemsLazy(@Param("paramsMap") Map<String,Object> map);
}
