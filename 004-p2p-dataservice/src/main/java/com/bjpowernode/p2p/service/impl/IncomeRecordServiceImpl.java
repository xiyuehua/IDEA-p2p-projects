package com.bjpowernode.p2p.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.IncomeRecordMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.loan.RechargeRecordMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.service.BidInfoService;
import com.bjpowernode.p2p.service.IncomeRecordService;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:IncomeRecordServiceImpl
 * Package:com.bjpowernode.p2p.service.impl
 * Description
 *
 * @Date:2020/3/1918:49
 * @author:xyh
 */
@Component
@Service(interfaceClass = IncomeRecordService.class, version = "1.0.0", timeout = 25000)
public class IncomeRecordServiceImpl implements IncomeRecordService {

    @Autowired
    private LoanInfoMapper loanInfoMapper;
    @Autowired
    private BidInfoMapper bidInfoMapper;
    @Autowired
    private IncomeRecordMapper incomeRecordMapper;
    @Autowired
    private FinanceAccountMapper financeAccountMapper;
    @Override
    public List<IncomeRecord> queryRecentHistoryIncomeListByUid() {
        return null;
    }

    @Transactional
    @Override
    public void generateIncomePlan() {
        //生成收益计划
        //a. 判断产品是否满标
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanIfoListByProductStatus(1);

        for (LoanInfo loanInfo : loanInfoList) {
            //把满标产品的id拿出来
            Integer loanId = loanInfo.getId();
            //b.通过满标产品的loanId去查询 投资记录表所有的投资记录
            List<BidInfo> bidInfoList = bidInfoMapper.selectAllBidInfoListByLoanId(loanId);
            for (BidInfo bidInfo : bidInfoList) {
                //遍历所有的投资记录 生成新的收益记录
                IncomeRecord incomeRecord = new IncomeRecord();

                incomeRecord.setBidId(bidInfo.getId());
                incomeRecord.setBidMoney(bidInfo.getBidMoney());
                incomeRecord.setLoanId(loanId);
                incomeRecord.setIncomeStatus(0);
                incomeRecord.setUid(bidInfo.getUid());
                Date incomeDate = null;
                Double incomeMoney = null;
                if (Constants.PRODUCT_TYPE_X == loanInfo.getProductType()) {
                    //新手包
                    incomeDate = DateUtils.addDays(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle();
                } else {
                    //优选 散标
                    incomeDate = DateUtils.addMonths(loanInfo.getProductFullTime(), loanInfo.getCycle());
                    incomeMoney = bidInfo.getBidMoney() * (loanInfo.getRate() / 100 / 365) * loanInfo.getCycle() * 365;
                }
                incomeRecord.setIncomeDate(incomeDate);
                incomeRecord.setIncomeMoney(incomeMoney);

                incomeRecordMapper.insertSelective(incomeRecord);
            }

            //跟新当前产品的状态为2 (满标且生成收益状态)
            LoanInfo updateLoanInfo = new LoanInfo();
            updateLoanInfo.setProductStatus(2);
            updateLoanInfo.setId(loanId);
            loanInfoMapper.updateByPrimaryKeySelective(updateLoanInfo);
        }

    }

    @Transactional
    @Override
    public void generateInBack() {
        //查询收益记录状态为0 且收益时间为今天的的收益计划
        List<IncomeRecord> incomeRecordList = incomeRecordMapper.selectIncomeRecordListByIncomeStatusAndCurrentDate(0);
        for (IncomeRecord incomeRecord : incomeRecordList) {
            Double bidMoney = incomeRecord.getBidMoney();
            Integer uid = incomeRecord.getUid();
            Double incomeMoney = incomeRecord.getIncomeMoney();

            //将对应的投资本金和收入返还给对应的投资用户
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("bidMoney",bidMoney);
            paramMap.put("uid",uid);
            paramMap.put("incomeMoney",incomeMoney);
            financeAccountMapper.updateFinanceAccountByIncomeBack(paramMap);

            incomeRecord.setIncomeStatus(1);
            //把当前的收益记录改为1
            incomeRecordMapper.updateByPrimaryKeySelective(incomeRecord);
        }
    }
}
