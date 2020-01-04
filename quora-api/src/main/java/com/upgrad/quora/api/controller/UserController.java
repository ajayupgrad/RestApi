package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.AuthenticationService;
import com.upgrad.quora.service.business.UserBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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
     public ResponseEntity<SignupUserResponse>signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

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
        final UserEntity createdUserEntity = userBusinessService.signup(userEntity);
        SignupUserResponse userResponse = new SignupUserResponse().id(createdUserEntity.getUuid()).status("USER SUCCESSFULLY REGISTERED");
        return new ResponseEntity<SignupUserResponse>(userResponse , HttpStatus.CREATED);
    }
}
