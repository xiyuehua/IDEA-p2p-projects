package com.bjpowernode.p2p.util;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ClassName:DateUtils
 * Package:com.bjpowernode.p2p.util
 * Description
 *
 * @Date:2020/3/2021:19
 * @author:xyh
 */
public class DateUtils {
    public static String getTimestamp() {

        return new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());

    }
}
