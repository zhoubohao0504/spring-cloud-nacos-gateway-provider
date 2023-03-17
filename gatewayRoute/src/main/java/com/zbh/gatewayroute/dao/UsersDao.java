package com.zbh.gatewayroute.dao;

import com.zbh.gatewayroute.entity.Users;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;


@Mapper
public interface UsersDao {

    List<Users> getAll();

    List<Users> getTopGroupUser();

    Users getRandomUser();
}
