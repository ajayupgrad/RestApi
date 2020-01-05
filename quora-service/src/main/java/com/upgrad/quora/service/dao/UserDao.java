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

    /*
     * This method is added to persist the authData in database
     *
     * @param userAuthEntity Contains user information who has signed in and the access token
     * @return The userAuthEntity that is saved in data base
     */

    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthEntity){
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    /*
     * Retrieves the user auth record matched with the access token passed
     * The access token is the one generated at the time of login
     *
     * @param accessToken The Security accessToken generated at the time of Sign in
     * @return The UserAuthEntity record matched with the accessToken
     */

    public UserAuthEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthByAccessToken", UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }
    /*Retrieves the user detail matched with the userId passed
     * @param userUUID Id of the user
     * @return matched userID detail
     */
    public UserEntity getUserByUuid(final String uuid) {
        try {
            return entityManager.createNamedQuery("userByUuid", UserEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    /*
      This method is used to delete a user from db

      @param uuid of User that needed to be deleted from db

     */
    public void deleteUser(String uuid) {
        UserEntity userEntity = getUserByUuid(uuid);
        entityManager.remove(userEntity);
    }

}
