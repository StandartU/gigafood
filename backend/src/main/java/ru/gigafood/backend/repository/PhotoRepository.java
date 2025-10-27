package ru.gigafood.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import ru.gigafood.backend.entity.Photo;
import ru.gigafood.backend.entity.User;

@Repository
public interface PhotoRepository extends JpaRepository<Photo, Long> {
    boolean existsByAttachTitleAndUser(String attachTitle, User user);
}
