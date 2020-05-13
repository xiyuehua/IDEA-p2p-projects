package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.loan.BidInfo;
import com.bjpowernode.p2p.model.loan.IncomeRecord;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.*;
import com.bjpowernode.p2p.util.HttpClientUtils;
import com.bjpowernode.p2p.util.ResultUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ClassName:UserController
 * Package:com.bjpowernode.p2p.web
 * Description
 *
 * @Date:2020/3/1615:45
 * @author:xyh
 */
@Controller
public class UserController {

    @Reference(interfaceClass = UserService.class, version = "1.0.0", check = false)
    private UserService userService;

    @Reference(interfaceClass = RedisService.class, version = "1.0.0", check = false)
    private RedisService redisService;

    @Reference(interfaceClass = FinanceAccountService.class, version = "1.0.0", check = false)
    private FinanceAccountService financeAccountService;

    @Reference(interfaceClass = BidInfoService.class, version = "1.0.0", check = false)
    private BidInfoService bidInfoService;

    @Reference(interfaceClass = RechargeRecordService.class, version = "1.0.0", check = false)
    private RechargeRecordService rechargeRecordService;

    @Reference(interfaceClass = IncomeRecordService.class, version = "1.0.0", check = false)
    private IncomeRecordService incomeRecordService;

    @RequestMapping("/loan/page/register")
    public String pageRegister() {
        return "register";
    }

    @RequestMapping("/loan/checkPhone")
    @ResponseBody
    public Map<String, Object> checkPhone(@RequestParam("phone") String phone) {
        Map<String, Object> resultMap = new HashMap<>();
        User user = userService.queryUserByPhone(phone);
        if (ObjectUtils.allNotNull(user)) {
            resultMap.put("success", false);
            resultMap.put("message", "您输入的手机号已被注册");
        } else {
            resultMap.put("success", true);
        }
        return resultMap;
    }

    @RequestMapping("/user/register")
    @ResponseBody
    public Map<String, Object> register(@RequestParam("phone") String phone,
                                        @RequestParam("password") String password,
                                        @RequestParam("messageCode") String messageCode,
                                        HttpServletRequest request) {
        try {

            String redisMessageCode = redisService.get(phone);
            if (!StringUtils.equals(messageCode, redisMessageCode)) {
                return ResultUtils.Error("请输入正确的验证码");
            }

            User user = userService.register(phone, password);
            //放入session里
            request.getSession().setAttribute(Constants.SESSION_USER, user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.Error("注册失败");
        }
        ResultUtils success = ResultUtils.Success();

        return success;
    }

    @RequestMapping("/loan/getMessageCode")
    @ResponseBody
    public Map<String, Object> getMessageCode(@RequestParam("phone") String phone, HttpServletResponse response) throws Exception {
        System.out.println("1111111111111111111111111111");
        response.addHeader("Access-Control-Allow-Origin","*");
        response.addHeader("Access-Control-Allow-Method","POST,GET");

        String messageCode = "";
        try {
            //生成随机验证码
            messageCode = getRandomCode(6);
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("appkey", "9946af0d13ce84083305e2c4c6dccc10");
            paramMap.put("mobile", phone);
            paramMap.put("content", "【凯信通】您的验证码是：" + messageCode);
            //String resultJSON = HttpClientUtils.doPost("https://way.jd.com/kaixintong/kaixintong", paramMap);

            //请求参数(请求报文),响应参数(响应报文)
            //模拟报文
            String resultJSON = "{\n" +
                    "    \"code\": \"10000\",\n" +
                    "    \"charge\": false,\n" +
                    "    \"remain\": 0,\n" +
                    "    \"msg\": \"查询成功\",\n" +
                    "    \"result\": \"<?xml version=\\\"1.0\\\" encoding=\\\"utf-8\\\" ?><returnsms>\\n <returnstatus>Success</returnstatus>\\n <message>ok</message>\\n <remainpoint>-1111611</remainpoint>\\n <taskID>101609164</taskID>\\n <successCounts>1</successCounts></returnsms>\"\n" +
                    "}";
            JSONObject jsonObject = JSONObject.parseObject(resultJSON);
            String code = jsonObject.getString("code");
            if (!StringUtils.equals(code, "10000")) {
                return ResultUtils.Error("通信失败~");
            }

            String resultXML = jsonObject.getString("result");
            Document document = DocumentHelper.parseText(resultXML);
            //括号里为xpath表达式
            Node node = document.selectSingleNode("//returnstatus");
            String result = node.getText();
            if (!StringUtils.equals("Success", result)) {
                return ResultUtils.Error("短信发送失败~");
            }
            redisService.put(phone, messageCode);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResultUtils.Success(messageCode);
    }

    /**
     * 跳转实名认证页面
     *
     * @return
     */
    @RequestMapping("/page/realName")
    public String toRealName() {
        return "realName";
    }

    /**
     * 实名认证
     */
    @RequestMapping("/user/realName")
    @ResponseBody
    public Map<String, Object> realName(@RequestParam(value = "phone", required = true) String phone,
                                        @RequestParam(value = "realName", required = true) String realName,
                                        @RequestParam(value = "idCard", required = true) String idCard,
                                        @RequestParam(value = "messageCode", required = true) String messageCode,
                                        HttpServletRequest request,
                                        HttpServletResponse response) {

        response.addHeader("Access-Control-Allow-Origin","*");
        response.addHeader("Access-Control-Allow-Method","POST,GET");

        try {
            String redisMessageCode = redisService.get(phone);
            if (!StringUtils.equals(redisMessageCode, messageCode)) {
                return ResultUtils.Error("验证码错误");
            }
            //验证码正确 开始实名认证
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("appkey", "9946af0d13ce84083305e2c4c6dccc10");
            paramMap.put("cardNo", idCard);
            paramMap.put("realName", realName);
            //String resultJOSN = HttpClientUtils.doPost("https://way.jd.com/youhuoBeijing/test", paramMap);
            String resultJOSN = "{\n" +
                    "    \"code\": \"10000\",\n" +
                    "    \"charge\": false,\n" +
                    "    \"remain\": 1305,\n" +
                    "    \"msg\": \"查询成功\",\n" +
                    "    \"result\": {\n" +
                    "        \"error_code\": 0,\n" +
                    "        \"reason\": \"成功\",\n" +
                    "        \"result\": {\n" +
                    "            \"realname\": \"乐天磊\",\n" +
                    "            \"idcard\": \"350721197702134399\",\n" +
                    "            \"isok\": true\n" +
                    "        }\n" +
                    "    }\n" +
                    "}";
            //将json格式的字符串转换为JSON对象
            JSONObject jsonObject = JSONObject.parseObject(resultJOSN);
            String code = jsonObject.getString("code");
            if (!StringUtils.equals("10000", code)) {
                ResultUtils.Error("通信异常");
            }

            Boolean isok = jsonObject.getJSONObject("result").getJSONObject("result").getBoolean("isok");
            if (!isok) {
                return ResultUtils.Error("姓名与身份证号码不匹配");
            }
            //实名成功  更新session中的User
            User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);
            user.setIdCard(idCard);
            user.setName(realName);
            int count = userService.modifyUserById(user);
            if (count <= 0) {
                return ResultUtils.Error("实名认证失败");
            }


            return ResultUtils.Success();
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.Error("实名认证失败~");
        }
    }

    //查用户的可用余额
    @RequestMapping("/loan/myFinanceAccount")
    @ResponseBody
    public String myFinanceAccount(HttpServletRequest request) {
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        FinanceAccount financeAccount = financeAccountService.queryMyFinanceAccountByuId(sessionUser.getId());
        String availableMoney = financeAccount.getAvailableMoney().toString();
        return availableMoney;
    }

    @RequestMapping("/loan/logout")
    public String logout(HttpServletRequest request) {

        //将session的user清空
        request.getSession().removeAttribute(Constants.SESSION_USER);
        return "redirect:/index";
    }

    @RequestMapping("/loan/page/login")
    public String pageLogin(@RequestParam(value = "localPageUrl", required = false) String localPageUrl,
                            Model model) {
        model.addAttribute("localPageUrl", localPageUrl);
        return "login";
    }

    @RequestMapping("/user/login")
    @ResponseBody
    public ResultUtils login(@RequestParam("phone") String phone,
                             @RequestParam("loginPassword") String loginPassword,
                             @RequestParam("messageCode") String messageCode,
                             HttpServletRequest request,
                             HttpServletResponse response) {
        response.addHeader("Access-Control-Allow-Origin","*");
        response.addHeader("Access-Control-Allow-Method","POST,GET");
        try {
            //1.验证验证码是否正确
            String redisMessageCode = redisService.get(phone);
            if (!StringUtils.equals(redisMessageCode, messageCode)) {
                return ResultUtils.Error("验证码错误");
            }
            //这里在service层已经判断过是否为空
            User user = userService.login(phone, loginPassword);

            request.getSession().setAttribute(Constants.SESSION_USER, user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultUtils.Error("登录异常");
        }
        return ResultUtils.Success();
    }


    /**
     * 进入用户中心 我的小金库
     */
    @RequestMapping("/loan/myCenter")
    public String myCenter(HttpServletRequest request, Model model) {
        //根据用户获取账户信息
        User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
        FinanceAccount financeAccount = financeAccountService.queryMyFinanceAccountByuId(sessionUser.getId());
        model.addAttribute("financeAccount", financeAccount);
/*
        //根据用户获取投资记录
        List<BidInfo> bidInfoList = bidInfoService.queryRecentHistoryBidInfoListByUid();
        model.addAttribute("bidInfoList", bidInfoList);

        //根据用户获取充值记录
        List<RechargeRecord> rechargeRecordList =rechargeRecordService.queryRecentHistoryRechargeListByUid();
        model.addAttribute("rechargeRecordList", rechargeRecordList);

        //根据用户获取最近收益记录
        List<IncomeRecord> IncomeRecord =incomeRecordService.queryRecentHistoryIncomeListByUid();
        model.addAttribute("IncomeRecord", IncomeRecord);*/


        return "myCenter";
    }

    @RequestMapping("/loan/page/modifyLoginPassword")
    public String toModifyPassword() {
        return "updatePassword.html";
    }

    @RequestMapping("/loan/modifyLoginPassword")
    @ResponseBody
    public Map<String,Object> modifyPassword(HttpServletRequest request,
                                 @RequestParam(value = "phone",required = true)String phone,
                                 @RequestParam(value = "updatePassword",required = true)String password,
                                 @RequestParam(value = "messageCode",required = false)String messageCode) {
        if (ObjectUtils.allNotNull(messageCode)) {
            String redisMessageCode = redisService.get(phone);
            if (!StringUtils.equals(redisMessageCode, messageCode)) {
                return ResultUtils.Error("验证码错误");
            }
        }

        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("phone", phone);
        paramMap.put("password", password);
        try {
            userService.updateLoginPassword(paramMap);
        } catch (Exception e) {
            e.printStackTrace();
            ResultUtils.Error("修改密码失败~");
        }

        return ResultUtils.Success();
    }

    /**
     * 投资
     */


    /**
     * 获取验证码的随机数
     *
     * @param count
     * @return
     */
    private String getRandomCode(int count) {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < count; i++) {
            int index = (int) Math.round(Math.random() * 9);
            sb.append(index);
        }
        return sb.toString();
    }


}
