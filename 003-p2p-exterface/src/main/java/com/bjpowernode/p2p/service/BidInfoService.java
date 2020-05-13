package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.vo.BidUser;

import java.util.List;
import java.util.Map;

/**
 * ClassName:BidInfoService
 * Package:com.bjpowernode.p2p.service
 * Description
 *
 * @Date:2020/3/1417:18
 * @author:xyh
 */
public interface BidInfoService {
    /**
     * 获取平台投资总金额
     * @return
     */
    Double queryAllBidMoney();

    /**
     * 根据产品标识获取最近十条的投资记录
     * @param paramMap
     * @return
     */
    List<BidInfo> queryRecentHistoryBidInfoListByLoanId(Map paramMap);

    /**
     * 根据用户标识查询最近投资记录
     * @return
     */
    List<BidInfo> queryRecentHistoryBidInfoListByUid();

    /**
     * 投资
     */
    void invest(Map<String, Object> paramMap) throws Exception;

    /**
     * 获取投资排行榜
     * @return
     */
    List<BidUser> queryInvestTop();
}
