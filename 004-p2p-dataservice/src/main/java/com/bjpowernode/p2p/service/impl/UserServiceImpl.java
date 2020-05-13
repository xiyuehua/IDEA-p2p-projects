package com.bjpowernode.p2p.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.bjpowernode.p2p.constant.Constants;
import com.bjpowernode.p2p.mapper.loan.LoanInfoMapper;
import com.bjpowernode.p2p.mapper.user.FinanceAccountMapper;
import com.bjpowernode.p2p.mapper.user.UserMapper;
import com.bjpowernode.p2p.model.loan.LoanInfo;
import com.bjpowernode.p2p.model.user.FinanceAccount;
import com.bjpowernode.p2p.model.user.User;
import com.bjpowernode.p2p.service.UserService;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Time;
import java.util.Date;
import java.util.Map;

/**
 * ClassName:UserServiceImpl
 * Package:com.bjpowernode.p2p.service.impl
 * Description
 *
 * @Date:2020/3/1417:09
 * @author:xyh
 */
@Component
@Service(interfaceClass = UserService.class, version = "1.0.0", timeout = 15000)
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FinanceAccountMapper financeAccountMapper;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public Long queryAllUserCount() {

        Long allUserCount = (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);
        if (!ObjectUtils.allNotNull(allUserCount)) {
            synchronized (this) {
                allUserCount = (Long) redisTemplate.opsForValue().get(Constants.ALL_USER_COUNT);
                if (!ObjectUtils.allNotNull(allUserCount)) {
                    allUserCount = userMapper.selectAllUserCount();
                    redisTemplate.opsForValue().set(Constants.ALL_USER_COUNT,allUserCount);
                    System.out.println("mysql获取总人数");
                }else {
                    System.out.println("redis获取总人数");
                }
            }
        }

        return allUserCount;
    }

    @Override
    public User queryUserByPhone(String phone) {
        return userMapper.selectUserByPhone(phone);
    }

    @Transactional
    @Override
    public User register(String phone, String password) throws Exception {
        //1.添加用户
        User user = new User();
        user.setAddTime(new Date());
        user.setLoginPassword(password);
        user.setPhone(phone);
        int result = userMapper.insertSelective(user);
        if (result <= 0) {
            throw new Exception("新增用户失败");
        }

        //2.给用户账户发红包888
        FinanceAccount financeAccount = new FinanceAccount();
        financeAccount.setAvailableMoney(888.0);
        /*这里的uid有俩种方式获取
        *   a.再通过手机号查询一遍
        *   b.再User的Mapper文件 修改返回的key。获取自增的主键 保存到User里
        * */

        financeAccount.setUid(user.getId());
        result = financeAccountMapper.insertSelective(financeAccount);

        if (result <= 0) {
            throw new Exception("新增账户失败");
        }
        return user;
    }

    @Override
    public int modifyUserById(User user) {
        return userMapper.updateByPrimaryKeySelective(user);
    }

    @Transactional
    @Override
    public User login(String phone, String loginPassword) throws Exception {
        //1.根据手机号和密码查询用户信息
        User user = userMapper.login(phone, loginPassword);
        if (!ObjectUtils.allNotNull(user)) {
            throw new Exception("用户名或密码错误");
        }

        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setLastLoginTime(new Date());
        int count = userMapper.updateByPrimaryKeySelective(updateUser);
        if (count <= 0) {
            throw new Exception("更新最近登录时间异常");
        }

        return user;
    }

    @Override
    public void updateLoginPassword(Map<String, Object> paramMap) throws Exception {
        int count = userMapper.updateLoginPassword(paramMap);
        if (count <= 0) {
            throw new Exception("更新密码失败");
        }
    }
}
