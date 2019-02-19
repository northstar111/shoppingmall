package com.pyg.sellergoods.service;

import com.pyg.pojo.TbBrand;
import entity.PageResult;

import java.util.List;
import java.util.Map;

/**
 * 作者：杨立波
 * 时间：2018/8/29 20:17
 */
public interface BrandService {

    /**
     * 查询所有品牌
     *
     * @return 品牌列表
     */
    public List<TbBrand> findAll();

    /**
     * 查询分页结果
     *
     * @param pageNum  页码
     * @param pageSize 记录数
     * @return 分页结果
     */
    public PageResult findPage(int pageNum, int pageSize);

    /**
     * 添加品牌
     *
     * @param brand 品牌对象
     */
    public void add(TbBrand brand);

    /**
     * 查询指定品牌
     *
     * @param id 品牌id
     * @return 品牌对象
     */
    public TbBrand findBrand(Long id);

    /**
     * 修改品牌
     *
     * @param brand 品牌对象
     */
    public void update(TbBrand brand);

    /**
     * 删除品牌
     *
     * @param ids 品牌id数组
     */
    public void delete(Long[] ids);

    /**
     * 按条件查询分页
     *
     * @param brand    品牌对象
     * @param pageNum  页码
     * @param pageSize 记录数
     * @return 分页结果
     */
    public PageResult findPage(TbBrand brand, int pageNum, int pageSize);

    /**
     * 选择规格列表
     *
     * @return 规格列表
     */
    public List<Map> selectOptionList();
}
