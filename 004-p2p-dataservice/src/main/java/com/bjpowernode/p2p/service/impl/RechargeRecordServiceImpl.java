package com.bjpowernode.p2p.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.service.RechargeRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * ClassName:RechargeRecordServiceImpl
 * Package:com.bjpowernode.p2p.service.impl
 * Description
 *
 * @Date:2020/3/2021:31
 * @author:xyh
 */
@Component
@Service(interfaceClass = RechargeRecordService.class, version = "1.0.0", timeout = 25000)
public class RechargeRecordServiceImpl implements RechargeRecordService {

    @Resource
    private RechargeRecordMapper rechargeRecordMapper;
    @Resource
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public List<RechargeRecord> queryRecentHistoryRechargeListByUid() {
        return null;
    }

    @Override
    public int addRechargeRecord(RechargeRecord rechargeRecord) {
        int i = rechargeRecordMapper.insertSelective(rechargeRecord);
        return i;
    }

    @Override
    public void modifyRechargeRecordByRechargeNo(RechargeRecord rechargeRecord) {
        rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
    }

    @Override
    public RechargeRecord queryRechargeRecordByRechargeNo(String out_trade_no) {
        return rechargeRecordMapper.selectByRechargeNo(out_trade_no);
    }

    @Transactional
    @Override
    public void recharge(Map<String, Object> paramMap) throws Exception {
        //更新用户可用余额
        int count = financeAccountMapper.updateFinanceAccountByRecharge(paramMap);
        if (count <= 0) {
            throw new Exception("更新账户余额失败");
        }
        // 更新充值状态
        RechargeRecord rechargeRecord = new RechargeRecord();
        rechargeRecord.setRechargeNo((String) paramMap.get("rechargeNo"));
        rechargeRecord.setRechargeStatus("1");
        count = rechargeRecordMapper.updateRechargeRecordByRechargeNo(rechargeRecord);
        if (count <= 0) {
            throw new Exception("更新充值记录标识失败 0->1");
        }
    }
}

