package com.bjpowernode.p2p.mapper.user;

import com.bjpowernode.p2p.model.user.User;

import java.util.Map;

public interface UserMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    /**
     * 获取平台总人数
     * @return
     */
    Long selectAllUserCount();

    /**
     * 通过手机号查询用户信息
     * @return
     */
    User selectUserByPhone(String phone);

    /**
     * 根据手机号和密码查询用户信息
     * @param phone
     * @param loginPassword
     * @return
     */
    User login(String phone, String loginPassword);

    int updateLoginPassword(Map<String, Object> paramMap);
}