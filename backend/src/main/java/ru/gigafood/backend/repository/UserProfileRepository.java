package ru.gigafood.backend.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.gigafood.backend.entity.UserProfile;

@Repository
public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {
	Optional<UserProfile> findByEmail(String email);

    boolean existsByEmail(String email);
}
