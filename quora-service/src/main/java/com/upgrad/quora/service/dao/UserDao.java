package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    /*
        This method will create new user from UserEntity object
        User Entity object from which new user will be created
        This return UserEntity Object
     */
    public UserEntity createUser(UserEntity userEntity){
        entityManager.persist(userEntity);

        return userEntity;
    }

    public UserEntity getUserByUserName(final String userName){
        try{
            return entityManager.createNamedQuery("userByUserName" ,UserEntity.class).setParameter("userName" ,userName)
                    .getSingleResult();

        }
        catch (NoResultException nre){
            return null;
        }

        /*
        This method helps to  find exisiting  user by email id
        parameter email email id will be searched in db for exisiting user
        it will return UserEntity if email id exists in db
         */
    }
    public UserEntity getUserByEmail(final String email){
        try{
            return entityManager.createNamedQuery("userByEmail" ,UserEntity.class).setParameter("email" ,email)
                    .getSingleResult();
        }
        catch (NoResultException nre){
            return null;
        }
    }

    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthEntity){
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }
}
