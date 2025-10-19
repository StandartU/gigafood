package ru.gigafood.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import ru.gigafood.backend.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);
}
