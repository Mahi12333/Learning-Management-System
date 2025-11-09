package com.maven.neuto.repository;

import com.maven.neuto.model.ModuleTaskSubmit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModuleTaskRepository extends JpaRepository<ModuleTaskSubmit, Long> {
}
