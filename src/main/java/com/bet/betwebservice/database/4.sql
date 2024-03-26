
-- SELECT pod.name, SUM(pod_user_pod_has_user.is_member) FROM pod
-- LEFT JOIN pod_user_pod_has_user on pod.id = pod_user_pod_has_user.id__pod
-- GROUP BY pod.id
-- ORDER BY SUM(pod_user_pod_has_user.is_member) DESC, pod.name ASC;
-- SELECT * FROM stamp_user_user_collect_stamp;

-- attempt which needs to be validated:
SELECT pod.id, pod.name, pod_user_pod_has_user.is_member FROM pod
LEFT JOIN task on task.id__pod = pod.id
LEFT JOIN stamp_task_stamp_has_task on stamp_task_stamp_has_task.id__task = task.id
LEFT JOIN pod_user_pod_has_user on pod.id = pod_user_pod_has_user.id__pod
WHERE BIN_TO_UUID(stamp_task_stamp_has_task.id__stamp) = '1cd70d99-c6ce-4cbb-bda4-264d874ef8d6'
GROUP BY pod.id

-- validation queries (johto 2, kanto 2, hoenn 2):
-- SELECT pod.name, SUM(pod_user_pod_has_user.is_member) FROM pod
-- LEFT JOIN pod_user_pod_has_user on pod.id = pod_user_pod_has_user.id__pod
-- GROUP BY pod.id

