package com.polozov.postgrestomongobyspringbatch.repository;

import com.polozov.postgrestomongobyspringbatch.model.TaskDetail;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskMongoDBRepository extends MongoRepository<TaskDetail, String> {

}
