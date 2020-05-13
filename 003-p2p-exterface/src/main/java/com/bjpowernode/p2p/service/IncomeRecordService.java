package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.loan.IncomeRecord;

import java.util.List;

/**
 * ClassName:IncomeRecordService
 * Package:com.bjpowernode.p2p.service
 * Description
 *
 * @Date:2020/3/1819:02
 * @author:xyh
 */
public interface IncomeRecordService {

    /**
     * 小金库 根据用户id查询最近历史收益记录
     * @return
     */
    List<IncomeRecord> queryRecentHistoryIncomeListByUid();

    /**
     * 生成收益极坏
     */
    void generateIncomePlan();

    /**
     * 收益返还
     */
    void generateInBack();
}
