package com.bjpowernode.p2p.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.service.FinanceAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * ClassName:FinanceAccountServiceImpl
 * Package:com.bjpowernode.p2p.service.impl
 * Description
 *
 * @Date:2020/3/1815:46
 * @author:xyh
 */
@Component
@Service(interfaceClass = FinanceAccountService.class,version = "1.0.0",timeout = 25000)
public class FinanceAccountServiceImpl implements FinanceAccountService {
    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public FinanceAccount queryMyFinanceAccountByuId(Integer uid) {
        return financeAccountMapper.selectMyFinanceAccountByuId(uid);
    }
}
