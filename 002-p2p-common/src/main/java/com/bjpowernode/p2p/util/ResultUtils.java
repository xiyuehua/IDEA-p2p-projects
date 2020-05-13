package com.bjpowernode.p2p.util;

import java.util.HashMap;

/**
 * ClassName:ResultUtils
 * Package:com.bjpowernode.p2p.util
 * Description
 *
 * @Date:2020/3/1620:35
 * @author:xyh
 */
public class ResultUtils extends HashMap {

    public static ResultUtils Success(){
        ResultUtils resultUtils = new ResultUtils();
        resultUtils.put("success", true);
        return resultUtils;
    }
    public static ResultUtils Success(Object data){
        ResultUtils resultUtils = new ResultUtils();
        resultUtils.put("success", true);
        resultUtils.put("data", data);
        return resultUtils;
    }

    public static ResultUtils Error(Object data){
        ResultUtils resultUtils = new ResultUtils();
        resultUtils.put("success", false);
        resultUtils.put("data", data);
        return resultUtils;
    }
}
