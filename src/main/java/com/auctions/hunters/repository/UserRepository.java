package com.auctions.hunters.repository;

import com.auctions.hunters.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Integer> {

    /**
     * This query that retrieves a {@link User} entity from the database where the email field is equal to the value of the "username" parameter.
     */
    @Query("SELECT u from User u Where u.username = :username")
    Optional<User> findByUsername(@Param("username") String username);
    
    /**
     * This query that retrieves a {@link User} entity from the database where the email field is equal to the value of the "email" parameter.
     */
    @Query("SELECT u from User u Where u.email = :email")
    Optional<User> findByEmail(@Param("email") String email);

    /**
     * This query updates the "enabled" field of a {@link User}  in the database to "true" where the email of
     * the user matches the email specified as the first parameter in the query.
     */
    @Modifying
    @Query("UPDATE User a SET a.enabled = TRUE WHERE a.email = ?1")
    int enableUser(String email);

    /**
     * This query updates the "locked" field of a {@link User} in the database to "true" where the email of
     * the user matches the email specified as the first parameter in the query.
     */
    @Modifying
    @Query("UPDATE User a SET a.locked = FALSE WHERE a.email = ?1")
    int unlockUser(String email);
}
