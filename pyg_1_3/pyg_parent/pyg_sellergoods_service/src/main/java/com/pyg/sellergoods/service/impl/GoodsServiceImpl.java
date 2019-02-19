package com.pyg.sellergoods.service.impl;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.pyg.mapper.*;
import com.pyg.pojo.*;
import com.pyg.pojogroup.Goods;
import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.pojo.TbGoodsExample.Criteria;
import com.pyg.sellergoods.service.GoodsService;

import entity.PageResult;
import org.springframework.transaction.annotation.Transactional;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class GoodsServiceImpl implements GoodsService {

    @Autowired
    private TbGoodsMapper goodsMapper;
    @Autowired
    private TbGoodsDescMapper goodsDescMapper;
    @Autowired
    private TbItemMapper itemMapper;
    @Autowired
    private TbBrandMapper brandMapper;
    @Autowired
    private TbItemCatMapper itemCatMapper;
    @Autowired
    private TbSellerMapper sellerMapper;

    /**
     * 查询全部
     */
    @Override
    public List<TbGoods> findAll() {
        return goodsMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     * 扩展类
     * 设置事务：
     * timeout：默认超时时间=数据库设置的时间
     * readOnly：只读，不能增删改
     * isolation：隔离级别
     * propagation：传播
     */
    @Override
    public void add(Goods goods) {
        // 添加基本信息
        goods.getGoods().setAuditStatus("0"); // 新加商品为未审核状态
        goodsMapper.insert(goods.getGoods());

        // 添加扩展信息
        goods.getGoodsDesc().setGoodsId(goods.getGoods().getId()); // 同步商品id
        goodsDescMapper.insert(goods.getGoodsDesc());
        // 添加详情
        insertItem(goods);
    }

    /**
     * 修改
     */
    @Override
    public void update(Goods goods) {
        // 1.修改基本信息
        goods.getGoods().setAuditStatus("0");   // 修改后需重新审核
        goodsMapper.updateByPrimaryKey(goods.getGoods());
        // 2.修改扩展信息
        goodsDescMapper.updateByPrimaryKey(goods.getGoodsDesc());
        // 3.修改SKU列表
        // 3.1删除数据库中原SKU列表
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getGoods().getId());
        itemMapper.deleteByExample(example);
        // 3.2重新关联新提交的SKU列表
        insertItem(goods);
    }

    // 添加商品公共方法
    private void insertItem(Goods goods){
        // goods.getItemList();
        if ("1".equals(goods.getGoods().getIsEnableSpec())) {    // 启动规格
            for (TbItem item : goods.getItemList()) {
                String title = goods.getGoods().getGoodsName();    //SPU名称
                Map<String, String> map = JSON.parseObject(item.getSpec(), Map.class);
                for (String key : map.keySet()) {
                    title += " " + map.get(key);
                }
                item.setTitle(title);
                setItem(goods, item);

                itemMapper.insert(item);
            }
        } else {    // 不启用规格，只添加一条SKU
            TbItem item = new TbItem();
            item.setPrice(goods.getGoods().getPrice()); //
            item.setNum(99999);
            item.setStatus("1");    // 正常
            item.setIsDefault("1"); // 默认
            item.setSpec("{}");
            item.setTitle(goods.getGoods().getGoodsName()); //SPU名称就是SKU名称
            setItem(goods, item);
            itemMapper.insert(item);
        }
    }

    // 抽取公共方法
    // 设置item表共有字段
    private void setItem(Goods goods, TbItem item) {
        List<Map> imageList = JSON.parseArray(goods.getGoodsDesc().getItemImages(), Map.class);
        if (imageList != null && imageList.size() > 0) {
            item.setImage((String) imageList.get(0).get("url"));    // 获取第一张图片
        }
        item.setCategoryid(goods.getGoods().getCategory3Id());    // 第三级分类id
        item.setCreateTime(new Date());
        item.setUpdateTime(new Date());
        item.setGoodsId(goods.getGoods().getId());    //外键
        item.setSellerId(goods.getGoods().getSellerId());

        // 设置品牌冗余字段
        item.setBrand(brandMapper.selectByPrimaryKey(goods.getGoods().getBrandId()).getName());
        // 设置分类冗余
        item.setCategory(itemCatMapper.selectByPrimaryKey(goods.getGoods().getCategory3Id()).getName());
        // 设置商家冗余
        item.setSeller(sellerMapper.selectByPrimaryKey(goods.getGoods().getSellerId()).getNickName());
    }



    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public Goods findOne(Long id) {
        Goods goods = new Goods();
        // 查询基本信息
        TbGoods tbGoods = goodsMapper.selectByPrimaryKey(id);
        goods.setGoods(tbGoods);
        // 查询扩展信息
        TbGoodsDesc tbGoodsDesc = goodsDescMapper.selectByPrimaryKey(id);
        goods.setGoodsDesc(tbGoodsDesc);
        // 查询SKU列表
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(id);
        List<TbItem> itemList = itemMapper.selectByExample(example);
        goods.setItemList(itemList);
        return goods;
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            // 逻辑删除
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setIsDelete("1");
            goodsMapper.updateByPrimaryKey(goods);
        }
    }


    @Override
    public PageResult findPage(TbGoods goods, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbGoodsExample example = new TbGoodsExample();
        Criteria criteria = example.createCriteria();

        if (goods != null) {
            if (goods.getSellerId() != null && goods.getSellerId().length() > 0) {
                // 设置精确匹配
                criteria.andSellerIdEqualTo(goods.getSellerId());
            }
            if (goods.getGoodsName() != null && goods.getGoodsName().length() > 0) {
                criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
            }
            if (goods.getAuditStatus() != null && goods.getAuditStatus().length() > 0) {
                criteria.andAuditStatusLike("%" + goods.getAuditStatus() + "%");
            }
            if (goods.getIsMarketable() != null && goods.getIsMarketable().length() > 0) {
                criteria.andIsMarketableLike("%" + goods.getIsMarketable() + "%");
            }
            if (goods.getCaption() != null && goods.getCaption().length() > 0) {
                criteria.andCaptionLike("%" + goods.getCaption() + "%");
            }
            if (goods.getSmallPic() != null && goods.getSmallPic().length() > 0) {
                criteria.andSmallPicLike("%" + goods.getSmallPic() + "%");
            }
            if (goods.getIsEnableSpec() != null && goods.getIsEnableSpec().length() > 0) {
                criteria.andIsEnableSpecLike("%" + goods.getIsEnableSpec() + "%");
            }
            if (goods.getIsDelete() != null && goods.getIsDelete().length() > 0) {
                criteria.andIsDeleteLike("%" + goods.getIsDelete() + "%");
            }
            criteria.andIsDeleteIsNull();   //is_delete = null
        }

        Page<TbGoods> page = (Page<TbGoods>) goodsMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public void updateStatus(Long[] ids, String status) {
        for (Long id: ids){
            TbGoods goods = goodsMapper.selectByPrimaryKey(id);
            goods.setAuditStatus(status);
            goodsMapper.updateByPrimaryKey(goods);
        }
    }

    @Override
    public List<TbItem> findItemListByGoodsIdandStatus(Long[] goodsIds, String status) {
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andStatusEqualTo(status);
        criteria.andGoodsIdIn(Arrays.asList(goodsIds));
        List<TbItem> itemList = itemMapper.selectByExample(example);
        return itemList;
    }

}
