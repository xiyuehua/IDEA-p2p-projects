package com.bjpowernode.p2p.mapper.loan;

import com.bjpowernode.p2p.model.loan.IncomeRecord;

import java.util.List;

public interface IncomeRecordMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(IncomeRecord record);

    int insertSelective(IncomeRecord record);

    IncomeRecord selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(IncomeRecord record);

    int updateByPrimaryKey(IncomeRecord record);

    /**
     * 根据收益状态为0且预计收益日期为今天的收益记录
     * @return
     */
    List<IncomeRecord> selectIncomeRecordListByIncomeStatusAndCurrentDate(Integer incomeStatus);
}