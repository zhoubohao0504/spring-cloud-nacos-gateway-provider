package com.zbh.gatewayroute.service;

import com.zbh.gatewayroute.entity.Users;

import java.util.List;

public interface IUsersService {

    /**
     * 获取全部用户
     * @return
     */
    List<Users> getAll();

    /**
     * 获取顶部用户(访问正式环境)
     * @return
     */
    List<Users> getTopGroupUser();

    /**
     * 随机获取一个用户 模拟当前登录用户
     * @return
     */
    Users getRandomUser();
}
