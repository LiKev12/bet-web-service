-- SELECT
-- 	task.id,
--     task.name,
--     task.description,
--     task.image,
--     task.number_of_points,
--     task.timestamp_unix,
--     task.timestamp_update,
--     task.timestamp_target,
-- 	CASE
-- 		WHEN task_user_task_complete.id IS NOT NULL THEN task_user_task_complete.timestamp_unix
-- 		ELSE NULL
-- 	END AS timestamp_complete,
-- 	CASE
-- 		WHEN task_user_task_complete.id IS NOT NULL THEN TRUE
-- 		ELSE FALSE
-- 	END AS is_complete,
-- 	CASE
-- 		WHEN task_user_task_star.id IS NOT NULL THEN TRUE
-- 		ELSE FALSE
-- 	END AS is_star,
--     CASE
-- 		WHEN task_user_task_pin.id IS NOT NULL THEN TRUE
-- 		ELSE FALSE
-- 	END AS is_pin
-- FROM task
-- LEFT JOIN task_user_task_complete ON task.id = task_user_task_complete.id__task AND task_user_task_complete.id__user = UUID_TO_BIN('22857ceb-39a0-4569-836f-29dba98c75bc')
-- LEFT JOIN task_user_task_star ON task.id = task_user_task_star.id__task AND task_user_task_star.id__user = UUID_TO_BIN('22857ceb-39a0-4569-836f-29dba98c75bc')
-- LEFT JOIN task_user_task_pin ON task.id = task_user_task_pin.id__task AND task_user_task_pin.id__user = UUID_TO_BIN('22857ceb-39a0-4569-836f-29dba98c75bc')
-- WHERE BIN_TO_UUID(task.id__pod) = '41de4032-68e9-409f-b9e4-f79deeb4e0a9'
-- ORDER BY isComplete ASC, sortByTimestamp DESC;

-- SELECT task.id, task.name FROM task
-- -- LEFT JOIN task_user_task_note_text on task_user_task_note_text.id__task = task.id
-- -- LEFT JOIN task_user_task_note_image on task_user_task_note_image.id__task = task.id
-- LEFT JOIN task_user_task_complete ON task_user_task_complete.id__task = task.id
-- -- LEFT JOIN task_user_task_star ON task_user_task_star.id__task = task.id
-- -- LEFT JOIN task_user_task_pin ON task_user_task_pin.id__task = task.id
-- WHERE BIN_TO_UUID(task.id__pod) = '4060af93-8756-4442-adf6-4262f7dda377'
-- GROUP BY task.id

-- SELECT user.id, user.name, user.username, user.id__image_key, task_user_task_complete.timestamp_unix from user
-- LEFT JOIN task_user_task_complete ON task_user_task_complete.id__user = user.id
-- LEFT JOIN task ON task.id = task_user_task_complete.id__task
-- WHERE BIN_TO_UUID(task.id) = '153470c6-2813-4503-9633-048543c231cc'
-- GROUP BY user.id

-- SELECT COUNT(DISTINCT(pod.id)) as numberOfPodsAssociatedWithStampTotal FROM stamp
-- LEFT JOIN stamp_task_stamp_has_task on stamp_task_stamp_has_task.id__stamp = stamp.id
-- LEFT JOIN task on stamp_task_stamp_has_task.id__task = task.id
-- LEFT JOIN pod on pod.id = task.id__pod
-- LEFT JOIN pod_user_pod_has_user on pod_user_pod_has_user.id__pod = pod.id
-- WHERE BIN_TO_UUID(stamp.id) = '1cd70d99-c6ce-4cbb-bda4-264d874ef8d6' AND
-- (BIN_TO_UUID(pod_user_pod_has_user.id__user) = 'f4e7c3eb-73b7-409b-b553-602a41db3f26' AND pod_user_pod_has_user.is_member = TRUE)

-- SELECT pod.name, COUNT(DISTINCT(pod_user_pod_has_user.id__user)) FROM pod
-- LEFT JOIN pod_user_pod_has_user on pod_user_pod_has_user.id__pod = pod.id
-- GROUP BY pod.id;

-- SELECT task.name
-- FROM task
-- LEFT JOIN task_user_task_note_text on task_user_task_note_text.id__task = task.id
-- LEFT JOIN task_user_task_note_image on task_user_task_note_image.id__task = task.id
-- LEFT JOIN task_user_task_complete ON task_user_task_complete.id__task = task.id
-- LEFT JOIN task_user_task_star ON task_user_task_star.id__task = task.id
-- LEFT JOIN task_user_task_pin ON task_user_task_pin.id__task = task.id
-- WHERE BIN_TO_UUID(task_user_task_pin.id__user) = 'd79adbfb-dc53-461f-8e41-5c16e0e64103'
-- GROUP BY task.id

-- SELECT * FROM user_user_user_1_follow_user_2;


-- SET SQL_SAFE_UPDATES = 0;
-- UPDATE task
-- SET datetime_target = DATE_FORMAT(DATE_ADD('1970-01-01', INTERVAL timestamp_target SECOND), "%Y/%m/%d");
-- ALTER TABLE task_user_task_note
-- SET id__note_image_key BINARY(16);
-- SELECT *, DATE_FORMAT(DATE_ADD('1970-01-01', INTERVAL timestamp_target SECOND), "%Y/%m/%d") FROM task;
-- ALTER TABLE task
-- DROP COLUMN random_number;
-- UPDATE task
-- SET random_number = FLOOR(RAND()*(2)+1);
-- SELECT * FROM task;
-- SET SQL_SAFE_UPDATES = 1;


-- select * from user;
-- select *, CAST(id AS char(1000)) from stamp;

-- select * from stamp_task_stamp_has_task;

-- select * from pod_user_pod_has_user WHERE BIN_TO_UUID(pod_user_pod_has_user.id__pod) = 'f0a30b67-efb1-451f-b795-c7dc29f9fc79'
-- select * from user;
-- SELECT user.name, MAX(pod_user_pod_has_user.timestamp_become_member)
-- FROM user
-- LEFT JOIN pod_user_pod_has_user ON pod_user_pod_has_user.id__user = user.id AND pod_user_pod_has_user.is_member = TRUE
-- LEFT JOIN pod ON pod.id = pod_user_pod_has_user.id__pod
-- WHERE BIN_TO_UUID(pod.id) = 'f0a30b67-efb1-451f-b795-c7dc29f9fc79'
-- GROUP BY user.id

-- SELECT user.name, MAX(user_user_user_1_follow_user_2.timestamp_unix) FROM user
-- LEFT JOIN user_user_user_1_follow_user_2 ON user_user_user_1_follow_user_2.id__user_1 = user.id
-- WHERE BIN_TO_UUID(user_user_user_1_follow_user_2.id__user_2) = '985a92b3-c0f3-486a-8d95-1c2b020ca80d'
-- GROUP BY user.id

-- SELECT timestamp_unix FROM user_user_user_1_follow_user_2
-- WHERE BIN_TO_UUID(user_user_user_1_follow_user_2.id__user_2) = '985a92b3-c0f3-486a-8d95-1c2b020ca80d'

-- select * from pod;
-- select * from pod where pod.name like CONCAT('%','UNIVERSE','%');
-- select * from user_user_user_1_follow_user_2;
-- SELECT * FROM task_user_task_complete
-- WHERE BIN_TO_UUID(task_user_task_complete.id__task) = '153470c6-2813-4503-9633-048543c231cc'

