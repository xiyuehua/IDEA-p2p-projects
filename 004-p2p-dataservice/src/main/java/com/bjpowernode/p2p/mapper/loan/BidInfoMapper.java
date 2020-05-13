package com.bjpowernode.p2p.mapper.loan;

import com.bjpowernode.p2p.model.loan.BidInfo;

import java.util.List;
import java.util.Map;

public interface BidInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BidInfo record);

    int insertSelective(BidInfo record);

    BidInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BidInfo record);

    int updateByPrimaryKey(BidInfo record);

    /**
     * 获取累计投资金额
     * @return
     */
    Double selectAllBidMoney();

    /**
     * 根据产品标识获取最近十条的投资记录
     * @param paramMap
     * @return
     */
    List<BidInfo> selectRecentHistoryBidInfoListByLoanId(Map paramMap);

    /**
     * 根据产品的id获取所有投资记录
     * @param loanId
     * @return
     */
    List<BidInfo> selectAllBidInfoListByLoanId(Integer loanId);
}