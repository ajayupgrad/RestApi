package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Autowired
    private AdminBusinessService adminBusinessService;

    /*
       This method  used to create  a new user
       @parameter -UserEntity object from which user is created
       It will return user entity object
       @throws -SignUpRestrictedException if  user is already rgistered with same username or
        email address
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity signup(UserEntity userEntity) throws SignUpRestrictedException {
        return adminBusinessService.createUser(userEntity);
    }

    public UserEntity signout(final String authorizationToken) throws SignOutRestrictedException {

        return adminBusinessService.logoutUser(authorizationToken);
    }

}
