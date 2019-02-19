package com.pyg.user.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.pyg.mapper.TbAddressMapper;
import com.pyg.pojo.TbAddress;
import com.pyg.pojo.TbAddressExample;
import com.pyg.pojo.TbAddressExample.Criteria;
import com.pyg.user.service.AddressService;

import entity.PageResult;

/**
 * 服务实现层
 *
 * @author Administrator
 */
@Service
public class AddressServiceImpl implements AddressService {

    @Autowired
    private TbAddressMapper addressMapper;

    /**
     * 增加
     */
    @Override
    public void add(TbAddress address) {
        addressMapper.insert(address);
    }


    /**
     * 修改
     */
    @Override
    public void update(TbAddress address) {
        addressMapper.updateByPrimaryKey(address);
    }

    /**
     * 删除
     */
    @Override
    public void delete(Long id) {
        addressMapper.deleteByPrimaryKey(id);
    }

    @Override
    public List<TbAddress> findAddressListByUserId(String userId) {
        TbAddressExample example = new TbAddressExample();
        Criteria criteria = example.createCriteria();
        criteria.andUserIdEqualTo(userId);
        List<TbAddress> addressList = addressMapper.selectByExample(example);
        return addressList;
    }

}
