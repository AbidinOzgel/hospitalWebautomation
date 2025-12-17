package com.abidin.hospital.repository;

import com.abidin.hospital.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    // Rol ismine göre kullanıcıları bul
    List<User> findByRoles_Name(String roleName);
}
