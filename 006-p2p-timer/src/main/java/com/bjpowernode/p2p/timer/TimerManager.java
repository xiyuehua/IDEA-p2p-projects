package com.bjpowernode.p2p.timer;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.service.IncomeRecordService;
import com.bjpowernode.p2p.service.RechargeRecordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * ClassName:TimerManager
 * Package:com.bjpowernode.p2p.timer
 * Description
 *
 * @Date:2020/3/1918:44
 * @author:xyh
 */
@Slf4j
@Component
public class TimerManager {

    @Reference(interfaceClass = IncomeRecordService.class,version = "1.0.0",check = false)
    private IncomeRecordService incomeRecordService;


    //生成收益计划
    /*@Scheduled(cron = "0/5 * * * * ?")*/
    public void generateIncomePlan() {
        log.info("-----------生成收益计划开始-------------");
        incomeRecordService.generateIncomePlan();
        log.info("-----------生成收益计划结束-------------");
    }

    //收益返还
    @Scheduled(cron = "0/5 * * * * ?")
    public void generateIncomeBack() {
        log.info("-----收益返还开始------");
        incomeRecordService.generateInBack();
        log.info("-----收益返还结束------");
    }

}
