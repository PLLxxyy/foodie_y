package com.imooc.service.impl;

import com.imooc.mapper.StuMapper;
import com.imooc.pojo.Stu;
import com.imooc.service.StuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class StuServiceImpl implements StuService {

  @Autowired
  private StuMapper stuMapper;


  @Transactional(propagation = Propagation.SUPPORTS)
  @Override
  public Stu getStuInfo(int id) {
    return stuMapper.selectByPrimaryKey(id);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public void saveStu() {
    Stu stu=new Stu();
    stu.setAge(18);
    stu.setName("lxy");
    stuMapper.insert(stu);
  }

  @Transactional(propagation = Propagation.REQUIRED)
  @Override
  public void updateStu(int id) {
    Stu stu=new Stu();
    stu.setId(id);
    stu.setAge(19);
    stu.setName("parent");
    stuMapper.updateByPrimaryKey(stu);
  }

  @Override
  public void deleteStu(int id) {
    stuMapper.deleteByPrimaryKey(id);
  }
}
