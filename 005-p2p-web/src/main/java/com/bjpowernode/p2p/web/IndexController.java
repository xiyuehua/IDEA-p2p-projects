package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.BidInfoService;
import com.bjpowernode.p2p.service.LoanService;
import com.bjpowernode.p2p.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:IndexController
 * Package:com.bjpowernode.p2p.web
 * Description
 *
 * @Date:2020/3/1321:02
 * @author:xyh
 */
@Controller
public class IndexController {

    @Reference(interfaceClass = LoanService.class,version = "1.0.0",check = false)
    private LoanService loanService;
    @Reference(interfaceClass = UserService.class,version = "1.0.0",check = false)
    private UserService userService;
    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false)
    private BidInfoService bidInfoService;

    @RequestMapping("/index")
    public String index(Model model) {

        //获取产品平均年化收益率
        Double historyAverageRate = loanService.queryHistoryAverageRate();
        model.addAttribute(Constants.HISTORY_AVERAGE_RATE,historyAverageRate);
        //获取平台总人数
        Long allUserCount = userService.queryAllUserCount();
        model.addAttribute(Constants.ALL_USER_COUNT, allUserCount);
        //获取历史成交总额
        Double allBindMoney = bidInfoService.queryAllBidMoney();
        model.addAttribute(Constants.ALL_BIND_MONEY,allBindMoney);

        Map<String,Object> paramMap = new HashMap<>();
        paramMap.put("skipCount",0);
        //获取新手宝
        paramMap.put("productType",Constants.PRODUCT_TYPE_X);
        paramMap.put("pageSize",1);
        List<LoanInfo> xLoanInfoList = loanService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("xLoanInfoList",xLoanInfoList);
        //获取优选
        paramMap.put("productType",Constants.PRODUCT_TYPE_U);
        paramMap.put("pageSize",4);
        List<LoanInfo> uLoanInfoList = loanService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("uLoanInfoList",uLoanInfoList);
        //获取散标
        paramMap.put("productType",Constants.PRODUCT_TYPE_S);
        paramMap.put("pageSize",8);
        List<LoanInfo> sLoanInfoList = loanService.queryLoanInfoListByProductType(paramMap);
        model.addAttribute("sLoanInfoList",sLoanInfoList);
        return "index";
    }
}
