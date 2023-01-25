package com.polozov.postgrestomongobyspringbatch.repository;

import com.polozov.postgrestomongobyspringbatch.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
}
