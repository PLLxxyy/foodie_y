package com.imooc.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.imooc.enums.CommentLevel;
import com.imooc.mapper.ItemsCommentsMapper;
import com.imooc.mapper.ItemsImgMapper;
import com.imooc.mapper.ItemsMapper;
import com.imooc.mapper.ItemsMapperCustom;
import com.imooc.mapper.ItemsParamMapper;
import com.imooc.mapper.ItemsSpecMapper;
import com.imooc.pojo.Items;
import com.imooc.pojo.ItemsComments;
import com.imooc.pojo.ItemsImg;
import com.imooc.pojo.ItemsParam;
import com.imooc.pojo.ItemsSpec;
import com.imooc.pojo.vo.CommentLevelCountsVO;
import com.imooc.pojo.vo.ItemCommentVO;
import com.imooc.pojo.vo.SearchItemsVO;
import com.imooc.service.ItemService;
import com.imooc.utils.DesensitizationUtil;
import com.imooc.utils.PagedGridResult;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

@Service
public class ItemServiceImpl implements ItemService {

  @Autowired
  private ItemsMapper itemsMapper;

  @Autowired
  private ItemsImgMapper itemsImgMapper;

  @Autowired
  private ItemsSpecMapper itemsSpecMapper;
  @Autowired
  private ItemsParamMapper itemsParamMapper;
  @Autowired
  private ItemsCommentsMapper itemsCommentsMapper;
  @Autowired
  private ItemsMapperCustom itemsMapperCustom;

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public Items queryItemById(String itemId) {
    return itemsMapper.selectByPrimaryKey(itemId);
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public List<ItemsImg> queryItemImgList(String itemId) {
    Example example=new Example(ItemsImg.class);
    Example.Criteria criteria= example.createCriteria();
    criteria.andEqualTo("itemId",itemId);
    return  itemsImgMapper.selectByExample(example);
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public List<ItemsSpec> queryItemSpecList(String itemId) {
    Example example=new Example(ItemsSpec.class);
    Example.Criteria criteria= example.createCriteria();
    criteria.andEqualTo("itemId",itemId);
    return  itemsSpecMapper.selectByExample(example);
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public ItemsParam queryItemParam(String itemId) {
    Example example=new Example(ItemsParam.class);
    Example.Criteria criteria= example.createCriteria();
    criteria.andEqualTo("itemId",itemId);
    return  itemsParamMapper.selectOneByExample(example);
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public CommentLevelCountsVO queryCommentCounts(String itemId) {
    Integer goodCounts=getCommentCounts(itemId, CommentLevel.GOOD.type);
    Integer normalCounts=getCommentCounts(itemId, CommentLevel.NORMAL.type);
    Integer badCounts=getCommentCounts(itemId, CommentLevel.BAD.type);
    Integer totalCounts=goodCounts+normalCounts+badCounts;
    CommentLevelCountsVO commentLevelCountsVO=new CommentLevelCountsVO();
    commentLevelCountsVO.setTotalCounts(totalCounts);
    commentLevelCountsVO.setGoodCounts(goodCounts);
    commentLevelCountsVO.setNormalCounts(normalCounts);
    commentLevelCountsVO.setBadCounts(badCounts);
    return commentLevelCountsVO;
  }

  /*
  * */
  @Transactional(propagation = Propagation.SUPPORTS)
  Integer getCommentCounts(String itemId,Integer level){
    ItemsComments condition=new ItemsComments();
    condition.setItemId(itemId);
    if(level !=null){
      condition.setCommentLevel(level);
    }
    return itemsCommentsMapper.selectCount(condition);
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public PagedGridResult queryPagedComments
                   (String itemId,
                      Integer level,
                      Integer page,
                      Integer pageSize) {
    Map<String,Object> map=new HashMap<>();
    map.put("itemId",itemId);
    map.put("level",level);

    //mybatis-pagehelper
    PageHelper.startPage(page,pageSize);
    List<ItemCommentVO> list=itemsMapperCustom.queryItemComments(map);
    //用户昵称脱敏
    for (ItemCommentVO vo:list){
      vo.setNickname(DesensitizationUtil.commonDisplay(vo.getNickname()));
    }


    return setterPageGrid(list,page);
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public PagedGridResult searchItems(String keywords, String sort,
      Integer page, Integer pageSize) {
    Map<String,Object> map=new HashMap<>();
    map.put("keywords",keywords);
    map.put("sort",sort);
    PageHelper.startPage(page,pageSize);
    List<SearchItemsVO> list=itemsMapperCustom.searchItems(map);
    return setterPageGrid(list,page);
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public PagedGridResult searchItemsByThirdCat(Integer catId, String sort, Integer page,
      Integer pageSize) {
    Map<String,Object> map=new HashMap<>();
    map.put("catId",catId);
    map.put("sort",sort);
    PageHelper.startPage(page,pageSize);
    List<SearchItemsVO> list=itemsMapperCustom.searchItemsByThirdCat(map);
    return setterPageGrid(list,page);
  }

  /*分页方法*/
  private PagedGridResult setterPageGrid(List<?> list,Integer page){
    PageInfo<?> pageList=new PageInfo<>(list);
    PagedGridResult grid=new PagedGridResult();
    grid.setPage(page);
    grid.setRows(list);
    grid.setTotal(pageList.getPages());
    grid.setRecords(pageList.getTotal());
    return grid;
  }
}
