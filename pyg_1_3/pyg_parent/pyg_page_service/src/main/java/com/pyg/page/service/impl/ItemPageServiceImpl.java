package com.pyg.page.service.impl;

import com.pyg.mapper.TbGoodsDescMapper;
import com.pyg.mapper.TbGoodsMapper;
import com.pyg.mapper.TbItemCatMapper;
import com.pyg.mapper.TbItemMapper;
import com.pyg.page.service.ItemPageService;
import com.pyg.pojo.*;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 作者：杨立波  时间：2018-09-15 11:01
 * 项目：pyg_parent
 * 说明：页面服务
 */
@Service
public class ItemPageServiceImpl implements ItemPageService {

    @Value("#{tbGoodsMapper}")
    private TbGoodsMapper tbGoodsMapper;
    @Value("#{tbGoodsDescMapper}")
    private TbGoodsDescMapper tbGoodsDescMapper;
    @Value("#{tbItemCatMapper}")
    private TbItemCatMapper tbItemCatMapper;
    @Value("#{tbItemMapper}")
    private TbItemMapper tbItemMapper;
    @Value("#{freemarkerConfig}")
    private FreeMarkerConfigurer freemarkerConfig;
    @Value("${pageDir}")
    private String pageDir;

    @Override
    public void genHtml(Long goodsId) {
        // 1.准备数据
        // 1.1商品基本信息
        Map dataModel = new HashMap();
        TbGoods tbGoods = tbGoodsMapper.selectByPrimaryKey(goodsId);
        dataModel.put("tbGoods", tbGoods);
        // 1.2商品扩展信息
        TbGoodsDesc tbGoodsDesc = tbGoodsDescMapper.selectByPrimaryKey(goodsId);
        dataModel.put("tbGoodsDesc", tbGoodsDesc);
        // 1.3商品分类
        TbItemCat tbItemCat1 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory1Id());
        TbItemCat tbItemCat2 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory2Id());
        TbItemCat tbItemCat3 = tbItemCatMapper.selectByPrimaryKey(tbGoods.getCategory3Id());
        dataModel.put("tbItemCat1", tbItemCat1);
        dataModel.put("tbItemCat2", tbItemCat2);
        dataModel.put("tbItemCat3", tbItemCat3);
        // 1.4SKU列表
        TbItemExample example = new TbItemExample();
        TbItemExample.Criteria criteria = example.createCriteria();
        criteria.andGoodsIdEqualTo(goodsId);
        criteria.andStatusEqualTo("1");
        example.setOrderByClause("is_default desc");
        List<TbItem> tbItems = tbItemMapper.selectByExample(example);
        dataModel.put("tbItems", tbItems);

        try {
            // 2.读取模板
            Configuration configuration = freemarkerConfig.getConfiguration();
            Template template = configuration.getTemplate("item.ftl");
            // 3.调用freemarker引擎，生成静态页面
            Writer out = new FileWriter(pageDir + goodsId + ".html");
            template.process(dataModel, out);
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TemplateException e) {
            e.printStackTrace();
        }

        System.out.println(goodsId);
    }

    @Override
    public void deleteHtml(Long goodsId) {
        File file = new File(pageDir + goodsId + ".html");
        if (file.exists() && file.isFile()) {
            file.delete();
        }
    }
}
