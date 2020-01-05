package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/")
public class UserController {

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private AuthenticationService authenticationService;

    /*
    This api end point is used to register  a new user
    @Param signUp user request details from SignupUserRequest model
    @Return Json response with UUID of user
    @throws SignUpRestrictedException if validation for user details conflicts
     */

    @RequestMapping(method = RequestMethod.POST , path = "/user/signup" , consumes = MediaType.APPLICATION_JSON_UTF8_VALUE , produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
     public ResponseEntity<SignupUserResponse>signUp(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        // Creating User Entity Object
        final UserEntity userEntity = new UserEntity();

        //setting  values  to user entity object
        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setUserName(signupUserRequest.getUserName());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setSalt("salt");
        userEntity.setRole("nonadmin");

        // Returning response with created User Entity
        final UserEntity createdUserEntity = userBusinessService.signUp(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse , HttpStatus.CREATED);
    }

    /**
     This method is used to sign in a user who has successfully registered
     If not,throws a error message that the username does not exist or password is wrong

     @param authorization this contains the encoded username and password
     @return SignIn Response which contains user UUID and message stating sign in successfully or not
     @throws AuthenticationFailedException will be thrown when the username or password does not match
     */

    @RequestMapping(method = RequestMethod.POST ,path = "/user/signin" ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signIn(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {

        UserAuthEntity userAuthEntity = userBusinessService.signIn(authorization);
        UserEntity user = userAuthEntity.getUser();
        SigninResponse signinResponse = new SigninResponse().id(user.getUuid()).message("SIGNED IN SUCCESSFULLY");
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access_token", userAuthEntity.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse, httpHeaders, HttpStatus.OK);

    }
    /**
      This method signs out the user from the application if his session is still active.
      If not, throws an error message stating the user is not logged in before to signout.

      @param authorization Holds the access token generated at the time of signin and is used for authentication
      @return UUID of the use̥r and a message stating Sign Out Successful
      @throws SignOutRestrictedException when the user session is inactive or he never signed in before
     */
       @RequestMapping(method = RequestMethod.POST ,path ="/user/signout" ,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse>signOut(@RequestHeader("authorization") final String authorization) throws SignOutRestrictedException {
        // Sign out user
        final UserEntity userEntity = userBusinessService.signOut(authorization);

        // Return response
        SignoutResponse signoutResponse = new SignoutResponse().id(userEntity.getUuid())
                .message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK);

    }
}
