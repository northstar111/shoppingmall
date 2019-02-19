package com.pyg.content.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.content.service.ContentService;
import com.pyg.mapper.TbContentMapper;
import com.pyg.pojo.TbContent;
import com.pyg.pojo.TbContentExample;
import com.pyg.pojo.TbContentExample.Criteria;
import entity.PageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.List;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class ContentServiceImpl implements ContentService {

    @Autowired
    private TbContentMapper contentMapper;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 查询全部
     */
    @Override
    public List<TbContent> findAll() {
        return contentMapper.selectByExample(null);
    }

    /**
     * 按分页查询
     */
    @Override
    public PageResult findPage(int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 增加
     */
    @Override
    public void add(TbContent content) {
        // 添加新广告后，清空缓存
        contentMapper.insert(content);
        redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
    }


    /**
     * 修改
     */
    @Override
    public void update(TbContent content) {
        // 更新广告后，清空缓存
        TbContent tbContent = contentMapper.selectByPrimaryKey(content.getId());
        redisTemplate.boundHashOps("contentList").delete(tbContent.getCategoryId());    // 清空原分类缓存
        contentMapper.updateByPrimaryKey(content);
        if (content.getCategoryId().equals(tbContent.getCategoryId())) {    // 如果广告从原分类调整到新分类，则需清空新分类缓存
            redisTemplate.boundHashOps("contentList").delete(content.getCategoryId());
        }

    }

    /**
     * 根据ID获取实体
     *
     * @param id
     * @return
     */
    @Override
    public TbContent findOne(Long id) {
        return contentMapper.selectByPrimaryKey(id);
    }

    /**
     * 批量删除
     */
    @Override
    public void delete(Long[] ids) {
        for (Long id : ids) {
            contentMapper.deleteByPrimaryKey(id);
            // 清空每个广告的分类缓存
            redisTemplate.boundHashOps("contentList").delete(contentMapper.selectByPrimaryKey(id).getCategoryId());
        }
    }


    @Override
    public PageResult findPage(TbContent content, int pageNum, int pageSize) {
        PageHelper.startPage(pageNum, pageSize);

        TbContentExample example = new TbContentExample();
        Criteria criteria = example.createCriteria();

        if (content != null) {
            if (content.getTitle() != null && content.getTitle().length() > 0) {
                criteria.andTitleLike("%" + content.getTitle() + "%");
            }
            if (content.getUrl() != null && content.getUrl().length() > 0) {
                criteria.andUrlLike("%" + content.getUrl() + "%");
            }
            if (content.getPic() != null && content.getPic().length() > 0) {
                criteria.andPicLike("%" + content.getPic() + "%");
            }
            if (content.getStatus() != null && content.getStatus().length() > 0) {
                criteria.andStatusLike("%" + content.getStatus() + "%");
            }

        }

        Page<TbContent> page = (Page<TbContent>) contentMapper.selectByExample(example);
        return new PageResult(page.getTotal(), page.getResult());
    }

    @Override
    public List<TbContent> findListByCategoryId(Long categoryId) {
        // 从缓存获取广告
        List<TbContent> contentList = (List<TbContent>) redisTemplate.boundHashOps("contentList").get(categoryId);
        if (contentList == null || contentList.size() == 0) {    // 缓存不存在，再查询数据库
            TbContentExample example = new TbContentExample();
            Criteria criteria = example.createCriteria();
            // 广告分类
            criteria.andCategoryIdEqualTo(categoryId);
            // 是否有效
            criteria.andStatusEqualTo("1");
            // 字段排序
            example.setOrderByClause("sort_order");
            contentList = contentMapper.selectByExample(example);
            // 放入缓存
            redisTemplate.boundHashOps("contentList").put(categoryId, contentList);
            System.out.println("从数据库获取");
        } else {
            System.out.println("从redis缓存获取");
        }
        return contentList;
    }

}
