package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.loan.RechargeRecord;

import java.util.List;
import java.util.Map;

/**
 * ClassName:RechargeRecordService
 * Package:com.bjpowernode.p2p.service
 * Description
 *
 * @Date:2020/3/1819:01
 * @author:xyh
 */
public interface RechargeRecordService {
    List<RechargeRecord> queryRecentHistoryRechargeListByUid();

    /**
     * 生成充值记录
     * @param rechargeRecord
     * @return
     */
    int addRechargeRecord(RechargeRecord rechargeRecord);

    /**
     * 修改充值状态
     * @param rechargeRecord
     */
    void modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord);

    /**
     * 根据充值订单号查询充值订单
     * @param out_trade_no
     * @return
     */
    RechargeRecord queryRechargeRecordByRechargeNo(String out_trade_no);

    /**
     * 充值
     * @param paramMap
     */
    void recharge(Map<String, Object> paramMap) throws Exception;
}
