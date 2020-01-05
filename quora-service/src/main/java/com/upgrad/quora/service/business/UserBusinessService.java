package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.common.UnexpectedException;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Base64;

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
    public UserEntity signUp(UserEntity userEntity) throws SignUpRestrictedException {
        return adminBusinessService.createUser(userEntity);
    }

    /* This method takes the authorization string  which is encoded username & password
        If the username and password doesnot matches than it throws Authentication failed exception
        If the username and password match than auth token is generated
        If the input is illegal it  throws Unexpected Exception

        @Param authorization holds the basic access token used for authentication
        @return userAuthTokenEntity that conatins acess token and user UUID
        @throws AuthenticationFailedException if the username doesnot exists or  does not match
     */

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signIn(String authorization) throws AuthenticationFailedException {
        // Used to decode the request header authorization
        try{
            byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
            String decodedText = new String(decode);
            String[] decodedArray = decodedText.split(":");
            String username = decodedArray[0];
            String password = decodedArray[1];

            UserEntity user = userDao.getUserByUserName(username);
            if(user == null){
                throw  new AuthenticationFailedException("ATH-001","This user name does not exists" );

            }
            final String encryptedPassword = passwordCryptographyProvider.encrypt(password , user.getSalt());
            if(encryptedPassword.equals(user.getPassword())){
                JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
                UserAuthEntity userAuthTokenEntity = new UserAuthEntity();
                userAuthTokenEntity.setUser(user);
                final ZonedDateTime now = ZonedDateTime.now();
                final ZonedDateTime expiresAt = now.plusHours(8);
                userAuthTokenEntity.setAccessToken(jwtTokenProvider.generateToken(user.getUuid(), now, expiresAt));
                userAuthTokenEntity.setLoginAt(now);
                userAuthTokenEntity.setExpiresAt(expiresAt);
                userAuthTokenEntity.setUuid(user.getUuid());
                return userDao.createAuthToken(userAuthTokenEntity);
            }
            else{
                throw  new AuthenticationFailedException("ATH-002" ,"Password Failed");
            }

        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ex){
            GenericErrorCode genericErrorCode = GenericErrorCode.GEN_001;
            throw new UnexpectedException(genericErrorCode ,ex);

        }
    }

    public UserEntity signOut(final String authorizationToken) throws SignOutRestrictedException {

        return adminBusinessService.logoutUser(authorizationToken);
    }

}
