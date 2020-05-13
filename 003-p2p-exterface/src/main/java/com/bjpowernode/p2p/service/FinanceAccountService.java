package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.user.FinanceAccount;

/**
 * ClassName:FinanceAccountService
 * Package:com.bjpowernode.p2p.service
 * Description
 *
 * @Date:2020/3/1815:44
 * @author:xyh
 */
public interface FinanceAccountService {

    //根据用户标识查询用户可用余额
    FinanceAccount queryMyFinanceAccountByuId(Integer id);
}
