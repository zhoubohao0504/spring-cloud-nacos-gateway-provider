package com.zbh.gatewayroute.service.impl;

import com.zbh.gatewayroute.dao.UsersDao;
import com.zbh.gatewayroute.entity.Users;
import com.zbh.gatewayroute.service.IUsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
public class UsersServiceImpl implements IUsersService {


    @Autowired
    UsersDao dao;

    @Autowired
    public List<Users> getAll(){
        return dao.getAll();
    }

    @Override
    public List<Users> getTopGroupUser() {
        return dao.getTopGroupUser();
    }

    @Override
    public Users getRandomUser() {
        return dao.getRandomUser();
    }
}
