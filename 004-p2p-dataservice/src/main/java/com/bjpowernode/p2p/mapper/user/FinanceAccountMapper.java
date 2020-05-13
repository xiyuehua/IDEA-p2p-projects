package com.bjpowernode.p2p.mapper.user;

import com.bjpowernode.p2p.model.user.FinanceAccount;

import java.util.Map;

public interface FinanceAccountMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(FinanceAccount record);

    int insertSelective(FinanceAccount record);

    FinanceAccount selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(FinanceAccount record);

    int updateByPrimaryKey(FinanceAccount record);

    /**
     * 根据用户id查询用户账户信息
     * @param uid
     * @return
     */
    FinanceAccount selectMyFinanceAccountByuId(Integer uid);

    /**
     * 更新账户可以余额  投资时更新
     * @param paramMap
     * @return
     */
    int updateFinanceAccountByBid(Map<String, Object> paramMap);

    /**
     * 更新收益 返还给用户
     * @param paramMap
     */
    void updateFinanceAccountByIncomeBack(Map<String, Object> paramMap);

    /**
     * 更新可用余额 根据充值
     * @param paramMap
     * @return
     */
    int updateFinanceAccountByRecharge(Map<String, Object> paramMap);
}