package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class AdminBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    /*
          Used to create new user
          It uses @Param UserEntity & @return UserEntity Object & @throw SignUpRestrictedException
          if validation for user details conflict.
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity createUser(final UserEntity userEntity)throws SignUpRestrictedException {

        //Validation for requested username is available
        if(userDao.getUserByUserName(userEntity.getUserName()) != null) {
            throw new SignUpRestrictedException("SGR-001" ,"Try any other Username, this Username has already been taken");
        }

        // Validation for provided email id if available
        if(userDao.getUserByEmail(userEntity.getEmail()) != null) {
            throw  new SignUpRestrictedException("SGR-002" ,"This user has already been registered, try with any other emailId");

        }

        // Encrypt salt & Password
        String[] encryptedText = cryptographyProvider.encrypt(userEntity.getPassword());
        userEntity.setSalt(encryptedText[0]);
        userEntity.setPassword(encryptedText[1]);

        return userDao.createUser(userEntity);

    }
    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity logoutUser(final String authorizationToken) throws SignOutRestrictedException {
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorizationToken);

        // Validate if user is signed in or not
        if (userAuthEntity == null) {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }

        final ZonedDateTime now = ZonedDateTime.now();
        userAuthEntity.setLogoutAt(now);

        return userAuthEntity.getUser();
    }

}
