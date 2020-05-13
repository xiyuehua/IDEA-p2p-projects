package com.bjpowernode.p2p.service;

/**
 * ClassName:RedisService
 * Package:com.bjpowernode.p2p.service
 * Description
 *
 * @Date:2020/3/1717:17
 * @author:xyh
 */
public interface RedisService {
    /**
     * 把数据已键值对的形式存到redis里
     * @param key
     * @param value
     */
    void put(String key, String value);

    /**
     * 根据key获取value
     * @param key
     * @return
     */
    String get(String key);

    /**
     * 获得唯一数字
     * @return
     */
    Long getOnlyNumber();
}
