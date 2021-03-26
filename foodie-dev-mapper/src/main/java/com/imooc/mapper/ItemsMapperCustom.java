package com.imooc.mapper;

import com.imooc.pojo.vo.ItemCommentVO;


import com.imooc.pojo.vo.SearchItemsVO;
import com.imooc.pojo.vo.ShopcartVO;
import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;

public interface ItemsMapperCustom {

    public List<ItemCommentVO> queryItemComments
        (@Param("paramsMap") Map<String, Object> map);

    public List<SearchItemsVO> searchItems
        (@Param("paramsMap") Map<String, Object> map);

    public List<SearchItemsVO> searchItemsByThirdCat
        (@Param("paramsMap") Map<String, Object> map);
    /*
    * 渲染购物车
    * */
    public List<ShopcartVO> queryItemsBySpecIds(@Param("paramsList")
        List specIdsList);



}
