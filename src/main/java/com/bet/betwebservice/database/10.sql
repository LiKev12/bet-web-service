SELECT 
	stamp.id,
	stamp.name, 
	stamp.description,
	stamp.image,
	numberOfUsersCollect,
	isCollectByUser
FROM
    (SELECT 
        stamp.id as joinStampCardPropertiesSharedIdStamp,
        COUNT(*) as numberOfUsersCollect 
    FROM stamp
    LEFT JOIN stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.id__stamp = stamp.id
    GROUP BY stamp.id) as joinStampCardPropertiesShared
    LEFT JOIN
	(SELECT 
	stamp.id as joinStampCardPropertiesIdIndividual,
    stamp.name,
	MAX(CASE
		WHEN BIN_TO_UUID(stamp_user_user_collect_stamp.id__user) = 'd79adbfb-dc53-461f-8e41-5c16e0e64103' THEN TRUE
		ELSE FALSE
	END) = 1 as isCollectByUser,
    COUNT(DISTINCT((CASE
		WHEN (BIN_TO_UUID(pod_user_pod_has_user.id__user) = 'd79adbfb-dc53-461f-8e41-5c16e0e64103' AND pod_user_pod_has_user.is_member = TRUE) THEN pod_user_pod_has_user.id__pod
		ELSE NULL
	END))) as numberOfPodsAssociatedWithStampUserIsMemberOf,
    COUNT(DISTINCT(pod.id)) as numberOfPodsAssociatedWithStamp,
    MAX(pod.is_public) as isStampPublic
    FROM stamp
    LEFT JOIN stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.id__stamp = stamp.id
    LEFT JOIN stamp_task_stamp_has_task on stamp_task_stamp_has_task.id__stamp = stamp.id
    LEFT JOIN task on stamp_task_stamp_has_task.id__task = task.id
    LEFT JOIN pod on pod.id = task.id__pod
    LEFT JOIN pod_user_pod_has_user on pod_user_pod_has_user.id__pod = pod.id
    GROUP BY stamp.id) as joinStampCardPropertiesIndividual
	ON joinStampCardPropertiesSharedIdStamp = joinStampCardPropertiesIdIndividual
    LEFT JOIN stamp on stamp.id = joinStampCardPropertiesSharedIdStamp
WHERE
(isStampPublic OR numberOfPodsAssociatedWithStampUserIsMemberOf = numberOfPodsAssociatedWithStamp) AND
-- (LOWER(stamp.name) LIKE "%filterText%" OR LOWER(stamp.description) LIKE "%filterText%") AND 
(isCollectByUser IS TRUE OR isCollectByUser IS NOT TRUE)
ORDER BY numberOfUsersCollect DESC, stamp.name ASC