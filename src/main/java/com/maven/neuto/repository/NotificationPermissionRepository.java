package com.maven.neuto.repository;

import com.maven.neuto.model.NotificationPermission;
import com.maven.neuto.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NotificationPermissionRepository extends JpaRepository<NotificationPermission, Long> {

    Optional<NotificationPermission> findByNotificationPermissionUser(User user);
}
