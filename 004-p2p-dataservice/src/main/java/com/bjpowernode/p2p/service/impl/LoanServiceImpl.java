package com.bjpowernode.p2p.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.service.LoanService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * ClassName:LoanServiceImpl
 * Package:com.bjpowernode.p2p.service.impl
 * Description
 *
 * @Date:2020/3/1321:10
 * @author:xyh
 */
@Component
@Service(interfaceClass = LoanService.class,version = "1.0.0",timeout = 25000)
public class LoanServiceImpl implements LoanService {

    @Autowired
    private RedisTemplate<Object,Object> redisTemplate;

    @Autowired
    private LoanInfoMapper loanInfoMapper;

    @Override
    public LoanInfo queryLoanInfoByProductId(Integer id) {
        return loanInfoMapper.selectByPrimaryKey(id);
    }

    @Override
    public List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap) {
        return loanInfoMapper.selectLoanInfoListByProductType(paramMap);
    }

    @Override
    public PaginationVO<LoanInfo> queryLoanInfoListByPage(Map<String, Object> paramMap) {
        PaginationVO<LoanInfo> paginationVO = new PaginationVO<>();
        List<LoanInfo> loanInfoList = loanInfoMapper.selectLoanInfoListByProductType(paramMap);
        Long total = loanInfoMapper.selectLoanInfoCount(paramMap);
        paginationVO.setTotal(total);
        paginationVO.setLoanInfoList(loanInfoList);
        return paginationVO;
    }

    @Override
    public Double queryHistoryAverageRate() {

        Double historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);
        if(!ObjectUtils.allNotNull(historyAverageRate)){
            synchronized (this) {
                historyAverageRate = (Double) redisTemplate.opsForValue().get(Constants.HISTORY_AVERAGE_RATE);
                if(!ObjectUtils.allNotNull(historyAverageRate)) {
                    historyAverageRate = loanInfoMapper.selectHistoryAverageRate();
                    redisTemplate.opsForValue().set(Constants.HISTORY_AVERAGE_RATE,historyAverageRate,15, TimeUnit.MINUTES);
                    System.out.println("mysql中取");
                }else {
                    System.out.println("redis中取");
                }
            }
        }

        return historyAverageRate;
    }
}
