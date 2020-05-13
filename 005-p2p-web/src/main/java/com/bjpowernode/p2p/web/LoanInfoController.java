package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.model.vo.BidUser;
import com.bjpowernode.p2p.model.vo.PaginationVO;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.service.BidInfoService;
import com.bjpowernode.p2p.service.FinanceAccountService;
import com.bjpowernode.p2p.service.LoanService;
import com.bjpowernode.p2p.service.RedisService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:LoanInfoController
 * Package:com.bjpowernode.p2p.web
 * Description
 *
 * @Date:2020/3/159:30
 * @author:xyh
 */
@Controller
@RequestMapping("/loan")
public class LoanInfoController {

    @Reference(interfaceClass = LoanService.class,version = "1.0.0",check = false)
    private LoanService loanService;

    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false)
    private BidInfoService bidInfoService;

    @Reference(interfaceClass = FinanceAccountService.class, version = "1.0.0", check = false)
    private FinanceAccountService financeAccountService;

    @Reference(interfaceClass = RedisService.class,version = "1.0.0",check = false)
    private RedisService redisService;
    @RequestMapping("/loan")
    public String loan(@RequestParam(value = "ptype",required = false) Integer ptype,
                           @RequestParam(value = "currentPage", defaultValue = "1") Integer currentPage,
                           Model model) {
        Map<String, Object> paramMap = new HashMap<>();
        Integer pageSize = 9;
        paramMap.put("skipCount", (currentPage - 1) * pageSize);
        paramMap.put("pageSize", pageSize);
        if (ObjectUtils.allNotNull(ptype)) {
            paramMap.put("productType",ptype);
        }
        PaginationVO<LoanInfo> paginationVO = loanService.queryLoanInfoListByPage(paramMap);

        Integer totalRows = paginationVO.getTotal().intValue();
        Integer totalPage = totalRows/pageSize;
        if (totalRows % pageSize >0 ) {
            totalPage = totalPage +1 ;
        }

        if (ObjectUtils.allNotNull(ptype)) {
            model.addAttribute("ptype",ptype);
        }
        model.addAttribute("currentPage",currentPage);
        model.addAttribute("loanInfoList",paginationVO.getLoanInfoList());
        model.addAttribute("totalRows",totalRows);
        model.addAttribute("totalPage", totalPage);

        //用户投资排行榜

        List<BidUser> investList = bidInfoService.queryInvestTop();
        model.addAttribute("investList", investList);
        return "loan";
    }

    @RequestMapping("/loanInfo")
    public String loanInfo(@RequestParam(value = "id",required = true) Integer id, Model model,
                           HttpServletRequest request) {

        //根据产品标识获取产品的具体信息
        LoanInfo loanInfo = loanService.queryLoanInfoByProductId(id);
        model.addAttribute("loanInfo", loanInfo);


        //根据产品标识获取最近十条投资记录
        Map paramMap = new HashMap();
        paramMap.put("skipCount", 0);
        paramMap.put("pageSize", 10);
        paramMap.put("LoanId", id);
        List<BidInfo> bidInfoList = bidInfoService.queryRecentHistoryBidInfoListByLoanId(paramMap);
        model.addAttribute("bidInfoList", bidInfoList);

        //查询用户可用余额
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        //判断用户是否登录
        if (ObjectUtils.allNotNull(sessionUser)) {
            FinanceAccount financeAccount = financeAccountService.queryMyFinanceAccountByuId(sessionUser.getId());
            model.addAttribute("financeAccount", financeAccount);
        }
        return "loanInfo";
    }
}
