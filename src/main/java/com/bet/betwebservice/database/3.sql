-- select BIN_TO_UUID(id) from task
-- WHERE name LIKE '%create null%';

-- select * from task_user_task_complete
-- WHERE BIN_TO_UUID(id__task) = '2d2d95c1-a7c9-415f-9236-5dda8daf354c';

-- select name, BIN_TO_UUID(id), CAST(image as CHAR) from user ORDER BY BIN_TO_UUID(id) ASC;
-- select * from pod;
-- select *, BIN_TO_UUID(id__task) from task_user_task_comment;
-- select * from task WHERE BIN_TO_UUID(id) = '6e2eea60-97bd-4f5e-a132-26f2f7e3e834';
-- select * from task_user_task_comment_reaction;
SET SQL_SAFE_UPDATES = 0;
-- select BIN_TO_UUID(id), name from user;
select * from user;
-- ALTER TABLE user
-- DROP COLUMN is_selected_time_zone;
-- UPDATE user
-- SET time_zone = 'America/Los_Angeles';
-- WHERE username = 'chenpachi';
-- UPDATE user
-- SET is_selected_time_zone = TRUE;
-- ALTER TABLE user
-- ALTER time_zone SET DEFAULT 'UTC';
-- ALTER TABLE user
-- ADD is_selected_time_zone BOOL;
-- DELETE FROM user WHERE is_selected_time_zone = FALSE;
-- select * from notification;
-- select *, BIN_TO_UUID(stamp_user_user_collect_stamp.id__stamp) from stamp_user_user_collect_stamp;
-- UPDATE user
-- SET password = "still_needs_to_be_set";

-- SET SQL_SAFE_UPDATES = 1;
-- select * from stamp;

-- select * from stamp;
-- select * from user;
-- select * from pod;
-- SET SQL_SAFE_UPDATES = 0;
-- ALTER TABLE pod
-- ALTER description SET DEFAULT NULL;
-- ALTER TABLE stamp
-- ALTER description SET DEFAULT NULL;
-- ALTER TABLE user
-- ALTER bio SET DEFAULT NULL;
-- UPDATE pod
-- SET is_require_approve_request_to_join = 1 WHERE name like '%class 2015%';
-- UPDATE stamp
-- SET description = NULL WHERE description = "";
-- UPDATE user
-- SET bio = NULL WHERE bio = "";
-- DELETE FROM task WHERE name like '%create an%';
SET SQL_SAFE_UPDATES = 1;