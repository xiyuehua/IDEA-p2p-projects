package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.BidInfoService;
import com.bjpowernode.p2p.util.ResultUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * ClassName:BidInfoController
 * Package:com.bjpowernode.p2p.web
 * Description
 *
 * @Date:2020/3/1821:47
 * @author:xyh
 */
@Controller
public class BidInfoController {

    @Reference(interfaceClass = BidInfoService.class,version = "1.0.0",check = false)
    private BidInfoService bidInfoService;

    @RequestMapping("/loan/invest")
    @ResponseBody
    public Map<String, Object> invest(@RequestParam("loanId")Integer loanId,
                                      @RequestParam("bidMoney")Double bidMoney,
                                      HttpServletRequest request) {
        try {
            User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
            Map<String, Object> paramMap = new HashMap();
            paramMap.put("uid", sessionUser.getId());
            paramMap.put("bidMoney", bidMoney);
            paramMap.put("loanId", loanId);
            paramMap.put("phone", sessionUser.getPhone());

            bidInfoService.invest(paramMap);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.Error("投资失败~");
        }
        return ResultUtils.Success();
    }
}
