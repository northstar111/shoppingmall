package com.pyg.sellergoods.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbBrandMapper;
import com.pyg.pojo.TbBrand;
import com.pyg.pojo.TbBrandExample;
import com.pyg.sellergoods.service.BrandService;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;
import java.util.Map;

/**
 * 作者：杨立波
 * 时间：2018/8/29 20:13
 */
@Service
public class BrandServiceImpl implements BrandService {

    @Value("#{tbBrandMapper}")
    private TbBrandMapper tbBrandMapper;

    @Override
    public List<TbBrand> findAll() {
        return tbBrandMapper.selectByExample(null);
    }

    /**
     * 使用分页插件实现分页
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        // 定义分页参数，拦截分页结果
        PageHelper.startPage(pageNum, pageSize);
        // 查询，封装分页结果
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(null);
        // 返回查询结果
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void add(TbBrand brand) {
        tbBrandMapper.insert(brand);
    }

    @Override
    public TbBrand findBrand(Long id) {
        return tbBrandMapper.selectByPrimaryKey(id);
    }

    @Override
    public void update(TbBrand brand) {
        tbBrandMapper.updateByPrimaryKey(brand);
    }

    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            tbBrandMapper.deleteByPrimaryKey(id);
        }
    }

    @Override
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize) {
        // 定义分页参数，拦截分页结果
        PageHelper.startPage(pageNum, pageSize);
        // 添加查询条件
        TbBrandExample example = new TbBrandExample();
        TbBrandExample.Criteria criteria = example.createCriteria();
        if (brand.getName() != null && !"".equals(brand.getFirstChar())) {
            criteria.andNameLike("%" + brand.getName() + "%");
        }
        if (brand.getFirstChar() != null && !"".equals(brand.getFirstChar())) {
            criteria.andFirstCharEqualTo(brand.getFirstChar());
        }
        // 查询，封装分页结果
        Page<TbBrand> page = (Page<TbBrand>) tbBrandMapper.selectByExample(example);
        // 返回查询结果
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<Map> selectOptionList() {
        return tbBrandMapper.selectOptionList();
    }
}
