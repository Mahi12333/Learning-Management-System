package com.maven.neuto.repository;

import com.maven.neuto.model.FcNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<FcNotification, Long> {
}
