-- INSERT INTO `pod_user_pod_has_user` (
--     id, 
--     id__pod, 
--     id__user, 
--     timestamp_unix, 
--     is_member,
--     is_moderator
-- ) VALUES
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('f0a30b67-efb1-451f-b795-c7dc29f9fc79'), UUID_TO_BIN('ef613db3-d5be-4f1c-9a7c-d6c27edfbe45'), UNIX_TIMESTAMP(NOW()), TRUE, FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('7f53c6f3-1fa4-4201-8a3e-d3fe52cde248'), UUID_TO_BIN('ef613db3-d5be-4f1c-9a7c-d6c27edfbe45'), UNIX_TIMESTAMP(NOW()), TRUE, FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('29039ac6-dd96-40a9-bf82-ce0084845edf'), UUID_TO_BIN('ef613db3-d5be-4f1c-9a7c-d6c27edfbe45'), UNIX_TIMESTAMP(NOW()), TRUE, FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('8a4ae019-f46b-41c8-bb14-5a230f5cd935'), UUID_TO_BIN('ef613db3-d5be-4f1c-9a7c-d6c27edfbe45'), UNIX_TIMESTAMP(NOW()), TRUE, FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('0612ca0f-324a-4108-8d09-efa5159b1584'), UUID_TO_BIN('ef613db3-d5be-4f1c-9a7c-d6c27edfbe45'), UNIX_TIMESTAMP(NOW()), TRUE, FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('894d33d1-8171-4967-8b8b-b4720ba54ee1'), UUID_TO_BIN('ef613db3-d5be-4f1c-9a7c-d6c27edfbe45'), UNIX_TIMESTAMP(NOW()), TRUE, FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('f0a30b67-efb1-451f-b795-c7dc29f9fc79'), UUID_TO_BIN('22857ceb-39a0-4569-836f-29dba98c75bc'), UNIX_TIMESTAMP(NOW()), TRUE, FALSE );

-- INSERT INTO `user_user_user_1_follow_user_2` (
--   `id`,
--   `id__user_1`,
--   `id__user_2`,
--   `timestamp_unix`,
--   `timestamp_request_sent`,
--   `is_request_accepted`,
--   `timestamp_request_accepted`,
--   `is_muted`
-- ) VALUES
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('985a92b3-c0f3-486a-8d95-1c2b020ca80d'), UUID_TO_BIN('ef613db3-d5be-4f1c-9a7c-d6c27edfbe45'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), TRUE, UNIX_TIMESTAMP(NOW()), FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('985a92b3-c0f3-486a-8d95-1c2b020ca80d'), UUID_TO_BIN('d79adbfb-dc53-461f-8e41-5c16e0e64103'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), TRUE, UNIX_TIMESTAMP(NOW()), FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('985a92b3-c0f3-486a-8d95-1c2b020ca80d'), UUID_TO_BIN('22857ceb-39a0-4569-836f-29dba98c75bc'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), TRUE, UNIX_TIMESTAMP(NOW()), FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('985a92b3-c0f3-486a-8d95-1c2b020ca80d'), UUID_TO_BIN('f4e7c3eb-73b7-409b-b553-602a41db3f26'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), TRUE, UNIX_TIMESTAMP(NOW()), FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('ef613db3-d5be-4f1c-9a7c-d6c27edfbe45'), UUID_TO_BIN('985a92b3-c0f3-486a-8d95-1c2b020ca80d'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), TRUE, UNIX_TIMESTAMP(NOW()), FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('d79adbfb-dc53-461f-8e41-5c16e0e64103'), UUID_TO_BIN('985a92b3-c0f3-486a-8d95-1c2b020ca80d'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), TRUE, UNIX_TIMESTAMP(NOW()), FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('22857ceb-39a0-4569-836f-29dba98c75bc'), UUID_TO_BIN('985a92b3-c0f3-486a-8d95-1c2b020ca80d'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), TRUE, UNIX_TIMESTAMP(NOW()), FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('f4e7c3eb-73b7-409b-b553-602a41db3f26'), UUID_TO_BIN('985a92b3-c0f3-486a-8d95-1c2b020ca80d'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), TRUE, UNIX_TIMESTAMP(NOW()), FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('d79adbfb-dc53-461f-8e41-5c16e0e64103'), UUID_TO_BIN('ef613db3-d5be-4f1c-9a7c-d6c27edfbe45'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), TRUE, UNIX_TIMESTAMP(NOW()), FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('d79adbfb-dc53-461f-8e41-5c16e0e64103'), UUID_TO_BIN('22857ceb-39a0-4569-836f-29dba98c75bc'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), TRUE, UNIX_TIMESTAMP(NOW()), FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('ef613db3-d5be-4f1c-9a7c-d6c27edfbe45'), UUID_TO_BIN('d79adbfb-dc53-461f-8e41-5c16e0e64103'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), TRUE, UNIX_TIMESTAMP(NOW()), FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('7b4bc526-1d01-4b66-b692-a80ccc04dc72'), UUID_TO_BIN('1842375f-b859-4f4d-8382-3492f85475b1'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), TRUE, UNIX_TIMESTAMP(NOW()), FALSE ),
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('1842375f-b859-4f4d-8382-3492f85475b1'), UUID_TO_BIN('7b4bc526-1d01-4b66-b692-a80ccc04dc72'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), TRUE, UNIX_TIMESTAMP(NOW()), FALSE );

-- INSERT INTO `user_user_user_1_follow_user_2` (
--   `id`,
--   `id__user_1`,
--   `id__user_2`,
--   `timestamp_unix`,
--   `timestamp_request_sent`,
--   `is_request_accepted`,
--   `timestamp_request_accepted`,
--   `is_muted`
-- ) VALUES
--     ( UUID_TO_BIN(UUID()), UUID_TO_BIN('f4e7c3eb-73b7-409b-b553-602a41db3f26'), UUID_TO_BIN('ef613db3-d5be-4f1c-9a7c-d6c27edfbe45'), UNIX_TIMESTAMP(NOW()), UNIX_TIMESTAMP(NOW()), FALSE, 0, FALSE );
SET SQL_SAFE_UPDATES = 0;
-- select * from task_user_task_note;
-- ALTER TABLE notifications RENAME TO notification;  
-- ALTER TABLE task_user_task_comment_reply
-- RENAME COLUMN comment_text to comment_reply_text;  
-- ALTER TABLE task_user_task_comment_reply
-- RENAME COLUMN id__comment_image_key to id__comment_reply_image_key;  
-- UPDATE task_user_task_note
-- SET timestamp_note_text = timestamp_unix;
-- ALTER TABLE user_user_user_1_follow_user_2
-- DROP COLUMN is_muted;
-- SELECT * FROM user_user_user_1_follow_user_2
-- select * from pod;
-- WHERE BIN_TO_UUID(id__user_1) = 'f4e7c3eb-73b7-409b-b553-602a41db3f26' AND BIN_TO_UUID(id__user_2) = 'ef613db3-d5be-4f1c-9a7c-d6c27edfbe45';
-- ALTER TABLE user_user_user_1_follow_user_2
-- ADD timestamp_of_following INT DEFAULT NULL;

-- ALTER TABLE task
-- ALTER is_archived SET DEFAULT FALSE;

-- ALTER TABLE pod_user_pod_has_user
-- ADD id__user_join_pod_invite_sender BINARY(16);

-- ALTER TABLE pod_user_pod_has_user
-- ADD timestamp_join_pod_invite_sent INT DEFAULT NULL;

-- ALTER TABLE pod_user_pod_has_user
-- ADD is_become_pod_moderator_request_sent BOOLEAN;

-- ALTER TABLE pod_user_pod_has_user
-- ADD is_become_pod_moderator_request_approved BOOLEAN;

-- ALTER TABLE pod_user_pod_has_user
-- ADD id__user_become_pod_moderator_request_approver BINARY(16);

-- ALTER TABLE pod_user_pod_has_user
-- ADD timestamp_become_pod_moderator_request_sent INT DEFAULT NULL;
-- select * from pod_user_pod_has_user;-- 
-- select * from user_user_user_1_follow_user_2;
-- select * from user;
-- ALTER TABLE pod_user_pod_has_user
-- ADD CONSTRAINT FK__id__user_join_pod_invite_sender
-- FOREIGN KEY (id__user_join_pod_invite_sender) REFERENCES user(id);

-- select *, BIN_TO_UUID(id__user_2) from user_user_user_1_follow_user_2;
-- ALTER TABLE user_user_user_1_follow_user_2 MODIFY timestamp_request_sent INT DEFAULT NULL;
-- ALTER TABLE user_user_user_1_follow_user_2 ALTER COLUMN timestamp_request_sent SET DEFAULT NULL;
select *, BIN_TO_UUID(task_user_task_comment_reply.id) from task_user_task_comment_reply;
-- select * from task_user_task_comment;
SET SQL_SAFE_UPDATES = 1;
-- SELECT * FROM user_user_user_1_follow_user_2;