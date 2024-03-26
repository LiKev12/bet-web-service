-- -- EXAMPLE
-- ALTER TABLE task_user_task_star
-- ADD CONSTRAINT FK__task_user_task_star__id__task
-- FOREIGN KEY (id__task) REFERENCES task(id);
-- ALTER TABLE task_user_task_star
-- ADD CONSTRAINT FK__task_user_task_star__id__user
-- FOREIGN KEY (id__user) REFERENCES user(id);

-- SET SQL_SAFE_UPDATES = 0;
-- UPDATE pod_user_pod_has_user SET is_moderator = FALSE;
-- UPDATE task
-- SET timestamp_updated = NULL
-- WHERE BIN_TO_UUID(id) IN ("54ce0bf5-0960-470e-97aa-841a5f7d22b8","6ce753bd-54ca-4750-9860-5a959a080876","dcf26589-8994-4b8b-a566-acf39ab1a709","4d79aac6-39d7-45db-bb11-c7d926e9a967","45f50b35-a6eb-4e53-91c3-5069128bc29f","dcf45f09-ced3-42d3-ade4-2beb8d8ce9c4","0efa40b0-7c7d-482e-a7d4-7974c0b7f918","4c4cc186-72e2-442f-9214-83176a82caea","62cfcc9f-69ad-44aa-9f2c-59cf540333a6","29421c8b-db80-41ef-9d32-4d93654e3e18","8c754535-7e2c-4734-a36e-ca52d43d7806","673f57e2-aaee-4531-9495-8437b54b3a06","12134605-647c-44df-aa28-4ec60ff7226e","722056be-8748-4471-86a3-11e7fcaa3bcb","e6b11813-d1e2-49ba-bea3-b9b047c26710","1cbeb2b6-8d51-4f10-a367-838bfcd283bc");
-- SET SQL_SAFE_UPDATES = 1;

-- select * from pod_user_pod_has_user
-- select * from pod_user_pod_has_user;
-- ALTER TABLE task
-- ADD Email varchar(255);
-- (LOWER(task.name) LIKE CONCAT('%',:filterNameOrDescription,'%') OR LOWER(task.description) LIKE CONCAT('%',:filterNameOrDescription,'%'))

-- SELECT 
-- 	task.id, 
--     task.name,  
--     MAX(CASE WHEN task.name = 'universe task name 1' THEN TRUE ELSE FALSE END) as myTestField
-- FROM task
-- LEFT JOIN task_user_task_complete on task_user_task_complete.id__task = task.id
-- GROUP BY task.id;

-- SELECT 
-- 	CAST(FROM_UNIXTIME(task_user_task_complete.timestamp_unix) AS DATE), 
-- 	SUM(task.number_of_points)
-- FROM task_user_task_complete
-- LEFT JOIN task on task.id = task_user_task_complete.id__task
-- WHERE task.id__pod IS NULL AND BIN_TO_UUID(task.id__user_create) = '985a92b3-c0f3-486a-8d95-1c2b020ca80d' AND
-- DATEDIFF(FROM_UNIXTIME(UNIX_TIMESTAMP(NOW())), CAST(FROM_UNIXTIME(task_user_task_complete.timestamp_unix) AS DATE)) <= 366
-- GROUP BY CAST(FROM_UNIXTIME(task_user_task_complete.timestamp_unix) AS DATE)
-- ORDER BY CAST(FROM_UNIXTIME(task_user_task_complete.timestamp_unix) AS DATE) ASC;

-- SELECT 
-- 	*, 
--     CAST(FROM_UNIXTIME(task_user_task_complete.timestamp_unix) AS DATE),
--     DATEDIFF(CAST(FROM_UNIXTIME(UNIX_TIMESTAMP(NOW())) AS DATE), CAST(FROM_UNIXTIME(task_user_task_complete.timestamp_unix) AS DATE))
-- FROM task
-- LEFT JOIN task_user_task_complete on task_user_task_complete.id__task = task.id;

-- select * 
-- FROM task_user_task_complete
-- LEFT JOIN task ON task.id = task_user_task_complete.id__task
-- WHERE BIN_TO_UUID(task_user_task_complete.id__user) = '985a92b3-c0f3-486a-8d95-1c2b020ca80d'
-- SET SQL_SAFE_UPDATES = 0;
-- UPDATE user
-- SET timestamp_unix = 1680549132
-- WHERE BIN_TO_UUID(id) = '985a92b3-c0f3-486a-8d95-1c2b020ca80d';
-- SET SQL_SAFE_UPDATES = 1;
select * FROM task WHERE task.name = 'create task 1 name';