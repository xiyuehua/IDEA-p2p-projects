package com.bjpowernode.p2p.web;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSONObject;
import com.alipay.api.internal.util.AlipaySignature;
import com.bjpowernode.p2p.config.AlipayConfig;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.model.loan.RechargeRecord;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.RechargeRecordService;
import com.bjpowernode.p2p.service.RedisService;
import com.bjpowernode.p2p.util.DateUtils;
import com.bjpowernode.p2p.util.HttpClientUtils;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * ClassName:RechargeRecordController
 * Package:com.bjpowernode.p2p.web
 * Description
 *
 * @Date:2020/3/1920:50
 * @author:xyh
 */
@Controller
public class RechargeRecordController {

    @Reference(interfaceClass = RedisService.class,version = "1.0.0",check = false)
    private RedisService redisService;

    @Reference(interfaceClass = RechargeRecordService.class,version = "1.0.0",check = false)
    private RechargeRecordService rechargeRecordService;

    @RequestMapping("/loan/page/toRecharge")
    public String toRecharge() {
        return "toRecharge";
    }

    @RequestMapping("/loan/toAlipayRecharge")
    public String toAlipayRecharge(HttpServletRequest request, @RequestParam("rechargeMoney")Double rechargeMoney, Model model) {
        try {
            User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);

            String rechargeNo = DateUtils.getTimestamp() + redisService.getOnlyNumber();
            //生成充值记录
            RechargeRecord rechargeRecord = new RechargeRecord();

            rechargeRecord.setRechargeTime(new Date());
            rechargeRecord.setRechargeStatus("0");
            rechargeRecord.setUid(user.getId());
            rechargeRecord.setRechargeMoney(rechargeMoney);
            rechargeRecord.setRechargeDesc("用户充值");
            rechargeRecord.setRechargeNo(rechargeNo);
            int count = rechargeRecordService.addRechargeRecord(rechargeRecord);
            if (count <= 0) {
                model.addAttribute("trade_msg", "新增充值记录失败");
                return "toRechargeBack";
            }

            model.addAttribute("rechargeNo",rechargeNo);
            model.addAttribute("rechargeMoney",rechargeMoney);
            model.addAttribute("rechargeDesc","支付宝充值");
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("trade_msg", "新增充值记录失败");
            return "toRechargeBack";
        }
        return "p2pToAlipay";
    }


    @RequestMapping("/loan/alipayBack")
    public String alipayBack(HttpServletRequest request,Model model,
                             @RequestParam(value = "out_trade_no",required = true)String out_trade_no,
                             @RequestParam(value = "total_amount",required = true) Double total_amount) throws Exception {
        //获取支付宝GET过来反馈信息
        Map<String,String> params = new HashMap<String,String>();
        Map<String,String[]> requestParams = request.getParameterMap();
        for (Iterator<String> iter = requestParams.keySet().iterator(); iter.hasNext();) {
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for (int i = 0; i < values.length; i++) {
                valueStr = (i == values.length - 1) ? valueStr + values[i]
                        : valueStr + values[i] + ",";
            }
            //乱码解决，这段代码在出现乱码时使用
            valueStr = new String(valueStr.getBytes("ISO-8859-1"), "utf-8");
            params.put(name, valueStr);
        }

        boolean signVerified = AlipaySignature.rsaCheckV1(params, AlipayConfig.alipay_public_key, AlipayConfig.charset, AlipayConfig.sign_type); //调用SDK验证签名

        //——请在这里编写您的程序（以下代码仅作参考）——
        if(signVerified) {
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("out_trade_no",out_trade_no);
            //调用pay工程的订单查询接口
            String resultJSON = HttpClientUtils.doPost("http://localhost:9090/pay/api/alipayQuery", paramMap);
            System.out.println(resultJSON);
            /*{
                    "alipay_trade_query_response": {
                    "code": "10000",
                    "msg": "Success",
                    "buyer_logon_id": "eod***@sandbox.com",
                    "buyer_pay_amount": "0.00",
                    "buyer_user_id": "2088102173075677",
                    "buyer_user_type": "PRIVATE",
                    "invoice_amount": "0.00",
                    "out_trade_no": "202003201047567",
                    "point_amount": "0.00",
                    "receipt_amount": "0.00",
                    "send_pay_date": "2020-03-20 10:51:16",
                    "total_amount": "300.00",
                    "trade_no": "2020032022001475670501012814",
                    "trade_status": "TRADE_SUCCESS"
                },
                    "sign": "s3Qjuzap4R1BKtsCGSowitQrphrNsyqPrYAtpogadn9xaw1ETJNZLO9m8tsJpDnX0/2IDVmPMhMW0XUZBl55WWO8yN7BlyVov8wdIhnypS1p9p5e2AS0k8Q0oZJAhcXhgTc07yog3wqUo5Ib9YLfQ5CKb1wHIfnlPBR+l8pN68IyY0elOZIwKml9U8azjWd3HF9IcIiQuRCOl6vZq4CNeYqqIMYFbu+TvxkRVq+NUUbG+kkO1BGH0YIAY/AG8GXV461Ngw5BwdZHc+Bd3SOdogwtDf+FnXU6AD5zLFFowH7SGbw0SDT3Fc4i2r7dkVP3RVPXiorUo/tOUUXCY/GMqQ=="
                }*/
            JSONObject jsonObject = JSONObject.parseObject(resultJSON);
            JSONObject alipay_trade_query_response = jsonObject.getJSONObject("alipay_trade_query_response");
            String code = alipay_trade_query_response.getString("code");
            if (!StringUtils.equals("10000", code)) {
                //通信失败
                model.addAttribute("trade_msg", "通信异常");
                return "toRechargeBack";
            }
            //通信成功
            String trade_status = alipay_trade_query_response.getString("trade_status");
            /*WAIT_BUYER_PAY	交易创建，等待买家付款
                TRADE_CLOSED	未付款交易超时关闭，或支付完成后全额退款
                TRADE_SUCCESS	交易支付成功
                TRADE_FINISHED	交易结束，不可退款*/

            if (StringUtils.equals("TRADE_CLOSED", trade_status)) {
                //更新商户系统订单状态为2充值失败
                RechargeRecord rechargeRecord = new RechargeRecord();
                rechargeRecord.setRechargeNo(out_trade_no);

                rechargeRecord.setRechargeStatus("2");
                rechargeRecordService.modifyRechargeRecordByRechargeNo(rechargeRecord);
                model.addAttribute("trade_msg","订单超时关闭");
                return "toRechargeBack";
            }

            if (StringUtils.equals("TRADE_SUCCESS", trade_status)) {

                //获取订单详情
                RechargeRecord rechargeRecord = rechargeRecordService.queryRechargeRecordByRechargeNo(out_trade_no);

                if (StringUtils.equals(rechargeRecord.getRechargeStatus(), "0")) {
                    //从session中获取用户的信息
                    User sessionUser = (User) request.getSession().getAttribute(Constants.SESSION_USER);
                    //给用户充值[1.更新帐户可用余额 2.更新充值记录的状态为1](用户标识,充值金额,充值订单号)
                    paramMap.put("uid",sessionUser.getId());
                    paramMap.put("rechargeMoney",total_amount);
                    paramMap.put("rechargeNo",out_trade_no);
                    rechargeRecordService.recharge(paramMap);
                }
            }

        }else {
            model.addAttribute("trade_msg", "充值异常请稍后再试");
            return "toChargeBack";
        }
        return "redirect:/loan/myCenter";
    }



    @RequestMapping("/loan/toWxpayRecharge")
    public String toWxpayRecharge(HttpServletRequest request, @RequestParam("rechargeMoney")Double rechargeMoney,Model model)
    {
        try {
            User user = (User) request.getSession().getAttribute(Constants.SESSION_USER);

            String rechargeNo = DateUtils.getTimestamp() + redisService.getOnlyNumber();
            //生成充值记录
            RechargeRecord rechargeRecord = new RechargeRecord();

            rechargeRecord.setRechargeTime(new Date());
            rechargeRecord.setRechargeStatus("0");
            rechargeRecord.setUid(user.getId());
            rechargeRecord.setRechargeMoney(rechargeMoney);
            rechargeRecord.setRechargeDesc("用户微信充值");
            rechargeRecord.setRechargeNo(rechargeNo);
            int count = rechargeRecordService.addRechargeRecord(rechargeRecord);
            if (count <= 0) {
                model.addAttribute("trade_msg", "新增充值记录失败");
                return "toRechargeBack";
            }

            model.addAttribute("rechargeNo",rechargeNo);
            model.addAttribute("rechargeMoney",rechargeMoney);
            model.addAttribute("rechargeDesc","支付宝充值");
            model.addAttribute("rechargeTime",new Date());
        } catch (Exception e) {
            e.printStackTrace();
            model.addAttribute("trade_msg", "新增充值记录失败");
            return "toRechargeBack";
        }
        return "showQRCode";
    }

    @RequestMapping("/loan/generateQRCode")
    public void generateQRCode(HttpServletRequest request, HttpServletResponse response,Model model,
                                 @RequestParam(value = "out_trade_no",required = true)String out_trade_no,
                                 @RequestParam(value = "total_fee",required = true)String total_fee) throws WriterException, IOException {
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("out_trade_no",out_trade_no);
        paramMap.put("total_fee",total_fee);
        paramMap.put("body", "微信扫码支付");
        String resultJSON = null;
        try {
            resultJSON = HttpClientUtils.doPost("http://localhost:9090/pay/api/wxpay", paramMap);
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*
            nonce_str	"tRGmIn5kZWpyEhQz"
            code_url	"weixin://wxpay/bizpayurl?pr=Mvasgua"
            appid	"wx8a3fcf509313fd74"
            sign	"D2D06BF7464B5E0356E8ECEE79505A4D"
            trade_type	"NATIVE"
            return_msg	"OK"
            result_code	"SUCCESS"
            mch_id	"1361137902"
            return_code	"SUCCESS"
            prepay_id	"wx0212265180388219f723515c1868346800"*/
        JSONObject jsonObject = JSONObject.parseObject(resultJSON);
        String return_code = jsonObject.getString("return_code");
        String result_code = jsonObject.getString("result_code");
        if (!StringUtils.equals(return_code, "SUCCESS")) {
            response.sendRedirect(request.getContextPath() + "/toRechargeBack");
        }if (!StringUtils.equals(result_code, "SUCCESS")) {
            response.sendRedirect(request.getContextPath() + "/toRechargeBack");
        }

        String code_url = jsonObject.getString("code_url");
        model.addAttribute("codeUrl", code_url);
        Map<EncodeHintType,Object> map = new HashMap();
        map.put(EncodeHintType.CHARACTER_SET, "utf-8");
        //创建一个矩阵对象
        BitMatrix bitMatrix = new MultiFormatWriter().encode(code_url, BarcodeFormat.QR_CODE, 200, 200, map);
        //将这个矩阵对象转换为流
        ServletOutputStream outputStream = response.getOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix,"jpg",outputStream);
        outputStream.flush();
        outputStream.close();
    }
}
