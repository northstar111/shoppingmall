package com.pyg.manager.controller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.pyg.pojo.TbBrand;
import com.pyg.sellergoods.service.BrandService;
import entity.PageResult;
import entity.Result;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 作者：杨立波
 * 时间：2018/8/29 21:01
 */
@RestController
@RequestMapping("brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    @RequestMapping("findAll")
    public List<TbBrand> findAll() {
        return brandService.findAll();
    }

    /**
     * 分页显示
     */
    @RequestMapping("findPage")
    public PageResult findPage(int page, int rows) {
        return brandService.findPage(page, rows);
    }

    /**
     * 按条件分页显示
     */
    @RequestMapping("search")
    public PageResult search(@RequestBody TbBrand brand, int page, int rows) {
        return brandService.findPage(brand, page, rows);
    }

    /**
     * 添加品牌
     */
    @RequestMapping("add")
    public Result add(@RequestBody TbBrand brand) {
        try {
            brandService.add(brand);
            return new Result(true, "添加成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(false, "添加失败");
        }
    }

    /**
     * 查询修改品牌
     */
    @RequestMapping("findBrand")
    public TbBrand findBrand(Long id) {
        return brandService.findBrand(id);
    }

    /**
     * 修改品牌
     */
    @RequestMapping("update")
    public Result update(@RequestBody TbBrand brand) {
        try {
            brandService.update(brand);
            return new Result(true, "修改成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(false, "修改失败");
        }
    }

    /**
     * 删除品牌
     */
    @RequestMapping("delete")
    public Result delete(Long[] ids) {
        try {
            brandService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception ex) {
            ex.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

    /**
     * 选择规格列表
     */
    @RequestMapping("selectOptionList")
    public List<Map> selectOptionList() {
        return brandService.selectOptionList();
    }
}
