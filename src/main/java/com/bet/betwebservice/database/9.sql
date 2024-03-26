
-- SELECT BIN_TO_UUID(stamp.id), stamp.name, BIN_TO_UUID(stamp_user_user_collect_stamp.id__user)

-- SELECT * FROM
-- 	(SELECT 
-- 		stamp.id as joinStampCardPropertiesSharedIdStamp,
--         COUNT(*) as numberOfUsersCollect 
-- 	FROM stamp
-- 	LEFT JOIN stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.id__stamp = stamp.id
-- 	GROUP BY stamp.id) as joinStampCardPropertiesShared
-- 	LEFT JOIN 
-- 	(SELECT 
-- 		stamp.id as joinStampCardPropertiesIdIndividual,
-- 		MAX(CASE
-- 			WHEN BIN_TO_UUID(stamp_user_user_collect_stamp.id__user) = 'd79adbfb-dc53-461f-8e41-5c16e0e64103' THEN TRUE
-- 			ELSE FALSE
-- 		END) as isCollect,
-- 		MAX(CASE
-- 			WHEN BIN_TO_UUID(pod_user_pod_has_user.id__user) = 'd79adbfb-dc53-461f-8e41-5c16e0e64103' THEN TRUE
-- 			ELSE FALSE
-- 		END) as isMemberOfAssociatedPod
-- 	FROM stamp
-- 	LEFT JOIN stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.id__stamp = stamp.id
-- 	LEFT JOIN stamp_task_stamp_has_task on stamp_task_stamp_has_task.id__stamp = stamp.id
-- 	LEFT JOIN task on stamp_task_stamp_has_task.id__task = task.id
-- 	LEFT JOIN pod on pod.id = task.id__pod
-- 	LEFT JOIN pod_user_pod_has_user on pod_user_pod_has_user.id__pod = pod.id
-- 	GROUP BY stamp.id) as joinStampCardProprtiesIndividual
-- 	ON joinStampCardPropertiesSharedIdStamp = joinStampCardPropertiesIdIndividual
--     LEFT JOIN stamp on stamp.id = joinStampCardPropertiesSharedIdStamp
-- ORDER BY numberOfUsersCollect DESC, stamp.name ASC


-- SELECT 
--     *
-- FROM
-- 	(SELECT 
-- 		stamp.id as joinStampCardPropertiesSharedIdStamp,
-- 		COUNT(DISTINCT(stamp_user_user_collect_stamp.id__user)) as numberOfUsersCollect,
-- 		MAX(pod.is_public) as isStampPublic
-- 	FROM stamp
-- 	LEFT JOIN stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.id__stamp = stamp.id
-- 	LEFT JOIN stamp_task_stamp_has_task on stamp_task_stamp_has_task.id__stamp = stamp.id
-- 	LEFT JOIN task on task.id = stamp_task_stamp_has_task.id__task
-- 	LEFT JOIN pod on pod.id = task.id__pod
-- 	GROUP BY stamp.id) as joinStampCardPropertiesShared
-- 	LEFT JOIN 
-- 	(SELECT 
-- 		stamp.id as joinStampCardPropertiesIdIndividual,
-- 		MAX(CASE
-- 			WHEN BIN_TO_UUID(stamp_user_user_collect_stamp.id__user) = 'd79adbfb-dc53-461f-8e41-5c16e0e64103' THEN TRUE
-- 			ELSE FALSE
-- 		END) as isCollect,
-- 		CASE WHEN (MIN(CASE
-- 			WHEN BIN_TO_UUID(pod_user_pod_has_user.id__user) = 'd79adbfb-dc53-461f-8e41-5c16e0e64103' THEN TRUE
-- 			ELSE FALSE
-- 		END) = 1 THEN TRUE ELSE FALSE END)
-- 	FROM stamp
-- 	LEFT JOIN stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.id__stamp = stamp.id
-- 	LEFT JOIN stamp_task_stamp_has_task on stamp_task_stamp_has_task.id__stamp = stamp.id
-- 	LEFT JOIN task on stamp_task_stamp_has_task.id__task = task.id
-- 	LEFT JOIN pod on pod.id = task.id__pod
-- 	LEFT JOIN pod_user_pod_has_user on pod_user_pod_has_user.id__pod = pod.id
-- 	GROUP BY stamp.id) as joinStampCardPropertiesIndividual
-- 	ON joinStampCardPropertiesSharedIdStamp = joinStampCardPropertiesIdIndividual
--     LEFT JOIN stamp on stamp.id = joinStampCardPropertiesSharedIdStamp
-- WHERE 
-- -- (LOWER(stamp.name) LIKE "%scioly%" OR LOWER(stamp.description) LIKE "%scioly%") AND 
-- (isCollect IS TRUE OR isCollect IS NOT TRUE)
-- ORDER BY numberOfUsersCollect DESC, stamp.name ASC



-- SELECT 
-- 	stamp.id as joinStampCardPropertiesSharedIdStamp,
--     stamp.name,
-- 	COUNT(DISTINCT(stamp_user_user_collect_stamp.id__user)) as numberOfUsersCollect,
-- 	MAX(pod.is_public) as isStampPublic
-- FROM stamp
-- LEFT JOIN stamp_user_user_collect_stamp on stamp_user_user_collect_stamp.id__stamp = stamp.id
-- LEFT JOIN stamp_task_stamp_has_task on stamp_task_stamp_has_task.id__stamp = stamp.id
-- LEFT JOIN task on task.id = stamp_task_stamp_has_task.id__task
-- LEFT JOIN pod on pod.id = task.id__pod
-- GROUP BY stamp.id

SELECT 
	stamp.id as joinStampCardPropertiesIdIndividual,
    stamp.name,
	MAX(CASE
		WHEN BIN_TO_UUID(stamp_user_user_collect_stamp.id__user) = 'd79adbfb-dc53-461f-8e41-5c16e0e64103' THEN TRUE
		ELSE FALSE
	END) as isCollect,
    COUNT(DISTINCT((CASE
		WHEN BIN_TO_UUID(pod_user_pod_has_user.id__user) = 'd79adbfb-dc53-461f-8e41-5c16e0e64103' AND pod_user_pod_has_user.is_member=1 THEN pod_user_pod_has_user.id__pod
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
WHERE LOWER(stamp.name) LIKE '%scioly%' or LOWER(stamp.description) LIKE '%scioly%'
GROUP BY stamp.id;
