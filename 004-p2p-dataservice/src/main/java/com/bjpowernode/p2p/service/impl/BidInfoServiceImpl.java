package com.bjpowernode.p2p.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.BidInfoMapper;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.vo.BidUser;
import com.bjpowernode.p2p.service.BidInfoService;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * ClassName:BidInfoServiceImpl
 * Package:com.bjpowernode.p2p.service.impl
 * Description
 *
 * @Date:2020/3/1417:22
 * @author:xyh
 */
@Component
@Service(interfaceClass = BidInfoService.class, version = "1.0.0", timeout = 15000)
public class BidInfoServiceImpl implements BidInfoService {


    @Resource
    private BidInfoMapper bidInfoMapper;

    @Resource
    private RedisTemplate<Object, Object> redisTemplate;

    @Resource
    private LoanInfoMapper loanInfoMapper;

    @Resource
    private FinanceAccountMapper financeAccountMapper;

    @Override
    public Double queryAllBidMoney() {
        Double allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BIND_MONEY);
        if (!ObjectUtils.allNotNull(allBidMoney)) {
            synchronized (this) {
                allBidMoney = (Double) redisTemplate.opsForValue().get(Constants.ALL_BIND_MONEY);
                if (!ObjectUtils.allNotNull(allBidMoney)) {
                    allBidMoney = bidInfoMapper.selectAllBidMoney();
                    redisTemplate.opsForValue().set(Constants.ALL_BIND_MONEY, allBidMoney);
                    System.out.println("mysql获取总金额");
                } else {
                    System.out.println("redis获取总金额");
                }
            }
        }

        return allBidMoney;
    }
    @Override
    public List<BidInfo> queryRecentHistoryBidInfoListByLoanId(Map paramMap) {
        return bidInfoMapper.selectRecentHistoryBidInfoListByLoanId(paramMap);
    }

    @Override
    public List<BidInfo> queryRecentHistoryBidInfoListByUid() {
        return null;
    }

    @Transactional
    @Override
    public void invest(Map<String, Object> paramMap) throws Exception {
        Integer loanId = (Integer) paramMap.get("loanId");
        Integer uid = (Integer) paramMap.get("uid");
        Double bidMoney = (Double) paramMap.get("bidMoney");
        String phone = (String) paramMap.get("phone");
        //1.更新产品可投金额减少,防止超卖使用数据库乐观锁
        //a.查询产品的版本号 (乐观锁)
        LoanInfo loanInfo = loanInfoMapper.selectByPrimaryKey(loanId);
        Integer version = loanInfo.getVersion();
        paramMap.put("version", version);
        int count = loanInfoMapper.updateLeftProductMoney(paramMap);
        if (count <= 0) {
            throw new Exception("更新产品剩余可投金额失败");
        }

        //1.扣自己账户余额里的钱
        count = financeAccountMapper.updateFinanceAccountByBid(paramMap);
        if (count <= 0) {
            throw new Exception("更新产品账户余额失败");
        }
        //3.生成投资记录
        BidInfo bidInfo = new BidInfo();
        bidInfo.setBidMoney(bidMoney);
        bidInfo.setBidTime(new Date());
        bidInfo.setLoanId(loanId);
        bidInfo.setUid(uid);
        bidInfo.setBidStatus(1);

        count = bidInfoMapper.insertSelective(bidInfo);
        if (count <= 0) {
            throw new Exception("新增投资记录失败");
        }
        //4.判断产品是否满标
        //a.再次查询产品的详情
        LoanInfo loanInfoDetail = loanInfoMapper.selectByPrimaryKey(loanId);

        if (0 == loanInfoDetail.getLeftProductMoney()) {
            //产品一旦满标 更新产品的状态和满标时间
            loanInfoDetail.setProductFullTime(new Date());
            loanInfoDetail.setProductStatus(1);
            count = loanInfoMapper.updateByPrimaryKeySelective(loanInfoDetail);
            if (count <= 0) {
                throw new Exception("更新产品的状态失败 满标或否");
            }
        }

        redisTemplate.opsForZSet().incrementScore(Constants.INVEST_TOP,phone,bidMoney);
    }

    @Override
    public List<BidUser> queryInvestTop() {
        List<BidUser> investList = new ArrayList<>();

        Set<ZSetOperations.TypedTuple<Object>> set = redisTemplate.opsForZSet().reverseRangeWithScores(Constants.INVEST_TOP, 0, 5);

        Iterator<ZSetOperations.TypedTuple<Object>> iterator = set.iterator();
        while (iterator.hasNext()) {
            ZSetOperations.TypedTuple<Object> next = iterator.next();
            String phone = (String) next.getValue();
            Double score = next.getScore();
            BidUser bidUser = new BidUser();
            bidUser.setPhone(phone);
            bidUser.setScore(score);
            investList.add(bidUser);
        }
        return investList;
    }
}
