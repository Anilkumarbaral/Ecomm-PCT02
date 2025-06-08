package com.ecomm.userservice.repository;

import com.ecomm.userservice.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String name);
    //get the role based username
//    Optional<Role> findByUsers_Email(String email);
    //get the role based user id
    //Optional<Role> findByUsers_Id(Long userId);
}



