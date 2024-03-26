-- SELECT * FROM database_bet_alpha.pod ORDER BY name;
-- SELECT * FROM database_bet_alpha.task_user_task_complete;
-- SELECT * FROM database_bet_alpha.pod_user_pod_has_user;
-- 1cd70d99-c6ce-4cbb-bda4-264d874ef8d6

-- SELECT pod.name, SUM(pod_user_pod_has_user.is_member) FROM 
-- 	(SELECT DISTINCT(pod.id) as distinct_id__pod FROM pod
-- 	LEFT JOIN task on task.id__pod = pod.id
-- 	LEFT JOIN stamp_task_stamp_has_task on stamp_task_stamp_has_task.id__task = task.id
-- 	WHERE BIN_TO_UUID(stamp_task_stamp_has_task.id__stamp) = '1cd70d99-c6ce-4cbb-bda4-264d874ef8d6') AS join_distinct_id__pod
-- LEFT JOIN pod on pod.id = distinct_id__pod
-- LEFT JOIN pod_user_pod_has_user on pod.id = pod_user_pod_has_user.id__pod
-- GROUP BY pod.id
-- ORDER BY SUM(pod_user_pod_has_user.is_member) DESC, pod.name ASC

-- SELECT stamp.name, COUNT(stamp_user_user_collect_stamp.id) FROM
-- (SELECT DISTINCT(stamp.id) as distinct__id__stamp FROM stamp
-- LEFT JOIN stamp_task_stamp_has_task on stamp_task_stamp_has_task.id__stamp = stamp.id
-- LEFT JOIN task on task.id = stamp_task_stamp_has_task.id__task
-- LEFT JOIN pod on pod.id = task.id__pod
-- WHERE BIN_TO_UUID(pod.id) = '79bf8d60-223f-4813-a15b-82726421758b') AS join__distinct__id__stamp
-- LEFT JOIN STAMP on stamp.id = distinct__id__stamp
-- LEFT JOIN stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.id__stamp = stamp.id
-- GROUP BY stamp.id
-- ORDER BY COUNT(stamp_user_user_collect_stamp.id) DESC, stamp.name ASC

-- select BIN_TO_UUID(id__stamp), BIN_TO_UUID(id__user) from stamp_user_user_collect_stamp;


