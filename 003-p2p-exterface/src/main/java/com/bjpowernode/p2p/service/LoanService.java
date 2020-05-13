package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.vo.PaginationVO;

import java.util.List;
import java.util.Map;

/**
 * ClassName:LoanService
 * Package:com.bjpowernode.p2p.service
 * Description
 *
 * @Date:2020/3/1321:09
 * @author:xyh
 */
public interface LoanService {

    /**
     * 查询历史平均年化收益率
     * @return
     */
    Double queryHistoryAverageRate();

    /**
     * 根据产品类型,查询产品信息列表
     * @param paramMap
     * @return
     */
    List<LoanInfo> queryLoanInfoListByProductType(Map<String, Object> paramMap);

    /**
     * 根据条件查询产品信息列表
     * @param paramMap
     * @return
     */
    PaginationVO<LoanInfo> queryLoanInfoListByPage(Map<String, Object> paramMap);

    /**
     * 根据产品id查询产品详情
     * @param id
     * @return
     */
    LoanInfo queryLoanInfoByProductId(Integer id);
}
