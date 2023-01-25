DROP TABLE IF EXISTS TASKS;

CREATE TABLE TASKS  (
                        task_id SERIAL PRIMARY KEY,
                        task_name VARCHAR(1024) NOT NULL ,
                        priority SMALLINT NOT NULL ,
                        created_at TIMESTAMP default now(),
                        start_datetime TIMESTAMP,
                        end_datetime TIMESTAMP,
                        is_complete BOOLEAN default FALSE
);
