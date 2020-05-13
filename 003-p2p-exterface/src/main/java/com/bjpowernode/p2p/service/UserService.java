package com.bjpowernode.p2p.service;

import com.bjpowernode.p2p.model.user.User;

import java.util.Map;

/**
 * ClassName:UserService
 * Package:com.bjpowernode.p2p.service
 * Description
 *
 * @Date:2020/3/1417:09
 * @author:xyh
 */
public interface UserService {
    /**
     * 获取平台总人数
     * @return
     */
    Long queryAllUserCount();

    /**
     * 通过手机号查询用户信息
     * @param phone
     * @return
     */
    User queryUserByPhone(String phone);


    User register(String phone, String password) throws Exception;

    /**
     * 修改用户信息根据用户标识
     * @param user
     * @return
     */
    int modifyUserById(User user);

    /**
     * 根据手机号和密码查询用户
     * @param phone
     * @param loginPassword
     * @return
     */
    User login(String phone, String loginPassword) throws Exception;

    /**
     * 修改用户密码
     * @param paramMap
     */
    void updateLoginPassword(Map<String, Object> paramMap) throws Exception;
}
