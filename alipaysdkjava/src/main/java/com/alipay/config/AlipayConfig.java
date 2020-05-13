package com.alipay.config;

import java.io.FileWriter;
import java.io.IOException;

/* *
 *类名：com.alipay.config.AlipayConfig
 *功能：基础配置类
 *详细：设置帐户有关信息及返回路径
 *修改日期：2017-04-05
 *说明：
 *以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
 *该代码仅供学习和研究支付宝接口使用，只是提供一个参考。
 */

public class AlipayConfig {

//↓↓↓↓↓↓↓↓↓↓请在这里配置您的基本信息↓↓↓↓↓↓↓↓↓↓↓↓↓↓↓

    // 应用ID,您的APPID，收款账号既是您的APPID对应支付宝账号
    public static String app_id = "2016101900722019";

    // 商户私钥，您的PKCS8格式RSA2私钥
    public static String merchant_private_key = "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQDJulsC/8DX/rYCKIr4aZz6i/Kwqc1DoNV+b2yPbKSR9Vemz6EnGqnA2gzSmDMMx98DXnH4rYPicjMzS6PtZH+qWJxgQSl3yfXCVBLtX25NdcWhxWvffKhRs7Lg0bY27Vn9Vc5Z2Vw3poZdcaa/O/NEA79M73EZDnOtopHum6v6Hh6vlYu6qm9XMnfU6NvOj4tFLwihyvfPMIloVwyJN2BULm3TFFRdbEXaHR5q6m6V6UolcGdLmoFofu548OYiUZ1I94WvINYl+uf5qNBHjBnO4GlyQ1EJudjntY1OKQOW4/YH3/vfJxR+cK7tAJHHmptixanF9UzAgS1Yoleay/M5AgMBAAECggEBALMhvLfJDCHBmTYJcn5sqm0B4RxnbFLley+vGiOyQeEqkA8cyOnEj4ElH8XSSWLMCYkgUOyWiEqJRlYCkSq413UZAqOmuYAZ4xYROk13NKesMTcZ7FSkiYMjo3BoSPo1gnenvnmivnTcdux7g6rFPZvkB/f1l+S8JJTHi/bqXajXiP8yhTMeVISKvkegl9aw3W1RwdZq2avIfw5ZgJSjYCHGudTPAHZhzmS/hbJCz4EPOjiwsjIFr7HOmj5DTpC17sGjTj9VcJ5vV7KR8PKQ6CIFlgI745MB0nl5/aq69EsSk/gkdxLOVp7ocJnf7GlEHa9utWbGdE94Sid01RPK99ECgYEA+M6VmCVpBvzX0IqQJJc7uiTAwnEaZhGXmQ9yhckv+smeh+eIj3URm2BtEYJWgSbvpZBtfuie6P+5+BZ15HAafR1c3SyzH9Gz9TdXt2/C6pIhU6HrLD2i5y2P9rm3LYiOyyW4UlxnQVEc7mt/pAl23YKkou+XJo1WGfP2tAQW2zMCgYEAz49XGe85dLpKB9zjelINXPfAyL/l78uzG9lSQgeJvgZn3p6zq6/YJ7XbSjjCX1TDXQDKM6GtRO/7eUZ/Gk/+b0PTCRAp1AODU87+/2EzQdc2LD/JhcWkuVYB8WxgYrCI8DUU5BcOrr+avDhNnAplqOvGkEDbjtDFOYG1NW/SF+MCgYEAgL6lOe1hw3PiWM7ad5SqG1AZToQoHNjNkS4lHIVWhnb9yWVNjK4PRk9HIOBI0tvK9ekeP6UvVXG+UVmN937qtgMTon8W0Ug5zpGsmgcC3U5wYE5Q2ruqJ+WzsP+4gZ9ABG6oSjX2WB4Y0mZdzgv3rfeEKavAkvpfq15KMixLh3ECgYBBF7oANyb0tYuMBbkNXng0ZLp/+raYynBEVpZWgNiw0H94sJq2SDJo2b6I+13x2bpfC2VeZeDaEszxzqsDzHyF1mZznC8R/3wLJ4qO+qPBjUW/AM4pdr9fJFr9pLhhmu2HL456rcR8Bzj2uE2WlygknrSp/r0cf9qqW0RCGuxvcwKBgHir7ohC9E1LygXo2TuuDTE8u5fc4Hawc9vY/D1pHAfOefM2M9UNSvzjwK6Zu+tIcs/DMapFMXgntlyr6uxrMiKCCwAID8Kskq9sO2xHv+GbztroX+vRnQImeBt40bIQJkRBLrlgZoNjq4TPNxMp+i3ecYXw/kkQGtKoAOVaiOeK";

    // 支付宝公钥,查看地址：https://openhome.alipay.com/platform/keyManage.htm 对应APPID下的支付宝公钥。
    public static String alipay_public_key = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAnNu6RO1o6qPJQwPkPQlK/9GdjTQ6VBUdBBFBCBJMP5ymt3hVvIXl0Dn4bc7+JJNCDF8yDjRexPbfso9sP/IGFO+FvUF1ZKkXKviCoAupbfeWvPtgWEp0llHJoE0ovz3JMbgtTKp2VCrElBAggDtP8ZcklE5Elf8CB+1EHGVpyNgDSSIPzQm+zn1yLB3ifjiP6y6z17KqPeBnC9BWCudqLqcR29M4pkAWne7piuKgomuj96JyR6w0bXnFRNKb/yGdaXlSy4Qzv8Boj5b3A0ylr1AwDfIpSAFarHBgGRKIIPd+i2SnbjtdeFBY/Csh0HNKV+Bozfn++mtMc/+LHHI5pwIDAQAB";

    // 服务器异步通知页面路径  需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String notify_url = "http://工程公网访问地址/alipay.trade.page.pay-JAVA-UTF-8/notify_url.jsp";

    // 页面跳转同步通知页面路径 需http://格式的完整路径，不能加?id=123这类自定义参数，必须外网可以正常访问
    public static String return_url = "http://localhost:8080/alipay.trade.page.pay-JAVA-UTF-8/return_url.jsp";

    // 签名方式
    public static String sign_type = "RSA2";

    // 字符编码格式
    public static String charset = "utf-8";

    // 支付宝网关
    //public static String gatewayUrl = "https://openapi.alipay.com/gateway.do";
    // 支付宝网关 沙箱
    public static String gatewayUrl = "https://openapi.alipaydev.com/gateway.do";

    // 支付宝网关
    public static String log_path = "C:\\";


//↑↑↑↑↑↑↑↑↑↑请在这里配置您的基本信息↑↑↑↑↑↑↑↑↑↑↑↑↑↑↑

    /**
     * 写日志，方便测试（看网站需求，也可以改成把记录存入数据库）
     * @param sWord 要写入日志里的文本内容
     */
    public static void logResult(String sWord) {
        FileWriter writer = null;
        try {
            writer = new FileWriter(log_path + "alipay_log_" + System.currentTimeMillis()+".txt");
            writer.write(sWord);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (writer != null) {
                try {
                    writer.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}

