package com.bet.betwebservice.service;

import com.bet.betwebservice.common.*;
import com.bet.betwebservice.dao.*;
import com.bet.betwebservice.dto.StampCardIndividualPropertiesDTO;
import com.bet.betwebservice.dto.StampCardSharedPropertiesDTO;
import com.bet.betwebservice.entity.StampEntity;
import com.bet.betwebservice.entity.StampTaskStampHasTaskEntity;
import com.bet.betwebservice.entity.StampUserUserCollectStampEntity;
import com.bet.betwebservice.model.StampCardModel;
import com.bet.betwebservice.model.StampPageModel;
import com.bet.betwebservice.model.UserBubbleModel;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import software.amazon.awssdk.core.sync.RequestBody;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class StampService {
    private StampRepository stampRepository;
    private UserRepository userRepository;
    private StampTaskStampHasTaskRepository stampTaskStampHasTaskRepository;
    private StampUserUserCollectStampRepository stampUserUserCollectStampRepository;
    private TaskUserTaskCompleteRepository taskUserTaskCompleteRepository;

    public StampService(
            StampRepository stampRepository,
            UserRepository userRepository,
            StampTaskStampHasTaskRepository stampTaskStampHasTaskRepository,
            StampUserUserCollectStampRepository stampUserUserCollectStampRepository,
            TaskUserTaskCompleteRepository taskUserTaskCompleteRepository
    ) {
        this.stampRepository = stampRepository;
        this.userRepository = userRepository;
        this.stampTaskStampHasTaskRepository = stampTaskStampHasTaskRepository;
        this.stampUserUserCollectStampRepository = stampUserUserCollectStampRepository;
        this.taskUserTaskCompleteRepository = taskUserTaskCompleteRepository;
    }

    public StampPageModel getStampPage(
        String idStamp,
        String idUser
    ) throws Exception {
        Optional<StampEntity> stampEntityOptional = this.stampRepository.findById(UUID.fromString(idStamp));
        if (!stampEntityOptional.isPresent()) {
            throw new Exception("Error: invalid input");
        }
        StampEntity stampEntity = stampEntityOptional.get();
        String stampImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_STAMP_IMAGE, stampEntity.getIdImageKey());

        List<UserBubbleModel> userBubblesStampCollect = this.getUserBubblesStampCollect(idStamp, idUser);
        
        boolean isEligibleToBeCollectedByMe = true;
        List<UUID> stampTaskIds = this.stampTaskStampHasTaskRepository.findByIdStamp(idStamp).stream().map(stampTaskStampHasTaskEntity -> stampTaskStampHasTaskEntity.getIdTask()).collect(Collectors.toList());
        for (int i = 0; i < stampTaskIds.size(); i++) {
            boolean isTaskCompleteByMe = this.taskUserTaskCompleteRepository.findByIdTaskIdUser(stampTaskIds.get(i).toString(), idUser).size() > 0;
            if (!isTaskCompleteByMe) {
                isEligibleToBeCollectedByMe = false;
            }
        }
        return StampPageModel.builder()
            .id(stampEntity.getId())
            .isCreatedByMe(stampEntity.getIdUserCreate().toString().equals(idUser))
            .idUserCreate(stampEntity.getIdUserCreate().toString())
            .usernameUserCreate(this.userRepository.findById(stampEntity.getIdUserCreate()).get().getUsername())
            .name(stampEntity.getName())
            .description(stampEntity.getDescription())
            .imageLink(stampImageLink)
            .userBubblesStampCollect(userBubblesStampCollect.subList(0, Math.min(userBubblesStampCollect.size(), 3)))
            .userBubblesStampCollectTotalNumber(userBubblesStampCollect.size())
            .isCollectedByMe(this.stampUserUserCollectStampRepository.findByIdStampIdUser(idStamp, idUser).size() > 0)
            .isEligibleToBeCollectedByMe(isEligibleToBeCollectedByMe)
            .build();
    }

    public List<UserBubbleModel> getUserBubblesStampCollect(String idStamp, String idUser) {
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesStampCollect = this.userRepository.getUserBubblesStampCollect(idStamp).stream().map(userBubbleDTOStampCollect -> {
            String userBubbleImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userBubbleDTOStampCollect.getIdImageKey());
            return UserBubbleModel.builder()
                .id(userBubbleDTOStampCollect.getId())
                .name(userBubbleDTOStampCollect.getName())
                .username(userBubbleDTOStampCollect.getUsername())
                .imageLink(userBubbleImageLink)
                .timestampToSortBy(userBubbleDTOStampCollect.getTimestampToSortBy())
                .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleDTOStampCollect.getTimestampToSortBy()))
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleDTOStampCollect.getTimestampToSortBy()))
                .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTOStampCollect.getId()))
                .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleDTOStampCollect.getId()))
                .isMe(idUser.equals(userBubbleDTOStampCollect.getId().toString()))
                .build();
        }).collect(Collectors.toList());
        Collections.sort(userBubblesStampCollect, new Comparator<UserBubbleModel>() {
            public int compare(UserBubbleModel userBubbleStampCollect1, UserBubbleModel userBubbleStampCollect2) {
                if (userBubbleStampCollect1.isMe() == true && userBubbleStampCollect2.isMe() == false) {
                    return -1;
                } else if (userBubbleStampCollect1.isMe() == false && userBubbleStampCollect2.isMe() == true) {
                    return 1;
                }
                if (userBubbleStampCollect1.isFollowedByMe() == true && userBubbleStampCollect2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubbleStampCollect1.isFollowedByMe() == false && userBubbleStampCollect2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleStampCollect1.getUsername().compareTo(userBubbleStampCollect2.getUsername());
            }
        });
        return userBubblesStampCollect;
    }

    public Page<StampCardModel> getStampCardsDiscover(
        String idUser, 
        String filterNameOrDescription,
        boolean filterIsPublicShared,
        boolean filterIsNotPublicShared,
        boolean filterIsCollectIndividual,
        boolean filterIsNotCollectIndividual,
        Pageable pageable
    ) {
        List<StampCardSharedPropertiesDTO> stampCardsDiscover_SharedProperties = this.stampRepository.getStampCardsDiscover_SharedProperties(filterNameOrDescription);
        List<StampCardIndividualPropertiesDTO> stampCardsDiscover_IndividualProperties = this.stampRepository.getStampCardsDiscover_IndividualProperties(idUser, filterNameOrDescription);
        Page<StampCardModel> stampCardsPage = this._getStampCardModelFromSharedAndIndividualProperties(
                idUser,
                stampCardsDiscover_SharedProperties,
                stampCardsDiscover_IndividualProperties,
                filterIsCollectIndividual,
                filterIsNotCollectIndividual,
                pageable
        );
        return stampCardsPage;
    }

    public Page<StampCardModel> getStampCardsAssociatedWithPod(
        String idPod, 
        String idUser,
        String filterNameOrDescription,
        boolean filterIsCollectIndividual,
        boolean filterIsNotCollectIndividual,
        Pageable pageable
    ) {
        List<StampCardSharedPropertiesDTO> stampCardsAssociatedWithPod_SharedProperties = this.stampRepository.getStampCardsAssociatedWithPod_SharedProperties(idPod, filterNameOrDescription);
        List<StampCardIndividualPropertiesDTO> stampCardsAssociatedWithPod_IndividualProperties = this.stampRepository.getStampCardsAssociatedWithPod_IndividualProperties(idPod, idUser, filterNameOrDescription);
        Page<StampCardModel> stampCardsPage = this._getStampCardModelFromSharedAndIndividualProperties(
                idUser,
                stampCardsAssociatedWithPod_SharedProperties,
                stampCardsAssociatedWithPod_IndividualProperties,
                filterIsCollectIndividual,
                filterIsNotCollectIndividual,
                pageable
        );
        return stampCardsPage;
    }

    public Page<StampCardModel> getStampCardsAssociatedWithUser(
        String idUserProfile,
        String idUser,
        String filterNameOrDescription,
        boolean filterIsCollectIndividual,
        boolean filterIsNotCollectIndividual,
        Pageable pageable
    ) {
        List<StampCardSharedPropertiesDTO> stampCardsAssociatedWithPod_SharedProperties = this.stampRepository.getStampCardsAssociatedWithUser_SharedProperties(filterNameOrDescription);
        List<StampCardIndividualPropertiesDTO> stampCardsAssociatedWithPod_IndividualProperties = this.stampRepository.getStampCardsAssociatedWithUser_IndividualProperties(idUser, filterNameOrDescription);

        // need to filter for stamps that profile user has actually collected AFTER query above because calculation numberOfUsersCollect requires this
        Set<UUID> stampIdsProfileUserHasCollected = new HashSet<>(this.stampRepository.getStampIdsUserHasCollected(idUserProfile));
        stampCardsAssociatedWithPod_SharedProperties = stampCardsAssociatedWithPod_SharedProperties.stream().filter(stampCard -> {
            return stampIdsProfileUserHasCollected.contains(stampCard.getId());
        }).collect(Collectors.toList());
        stampCardsAssociatedWithPod_IndividualProperties = stampCardsAssociatedWithPod_IndividualProperties.stream().filter(stampCard -> {
            return stampIdsProfileUserHasCollected.contains(stampCard.getId());
        }).collect(Collectors.toList());

        Page<StampCardModel> stampCardsPage = this._getStampCardModelFromSharedAndIndividualProperties(
                idUser,
                stampCardsAssociatedWithPod_SharedProperties,
                stampCardsAssociatedWithPod_IndividualProperties,
                filterIsCollectIndividual,
                filterIsNotCollectIndividual,
                pageable
        );
        return stampCardsPage;
    }

    public StampPageModel createStamp(
            @RequestParam String idUser,
            JsonNode requestBody
    ) throws Exception {
        if (
           idUser == null ||
           !requestBody.has(RequestBodyKeys.CREATE_STAMP_NAME) || 
           !requestBody.has(RequestBodyKeys.CREATE_STAMP_ID_TASKS)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String stampIdUserCreate = idUser;
        String stampName = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.CREATE_STAMP_NAME));
        List<String> stampIdTasks = new ArrayList<>();
         for (int i = 0; i < requestBody.get(RequestBodyKeys.CREATE_STAMP_ID_TASKS).size(); i++) {
             String stampIdTask = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.CREATE_STAMP_ID_TASKS).get(i));
             stampIdTasks.add(stampIdTask);
         }
       if (
           !RequestValidatorStamp.id(stampIdUserCreate) ||
           !RequestValidatorStamp.name(stampName) ||
           !RequestValidatorStamp.idTasks(stampIdTasks)
       ) {
           throw new Exception("Error: invalid input"); // TODO create an exception class
       }

       List<StampEntity> stampEntitiesWithSameName = this.stampRepository.findByNameLowerCase(stampName);
        if (stampEntitiesWithSameName.size() > 0) {
            throw new Exception("STAMP_DUPLICATE_NAME");
        }
       // save processed input
       StampEntity stampEntity = new StampEntity();
       stampEntity.setIdUserCreate(UUID.fromString(idUser));
       stampEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
       stampEntity.setName(stampName);
       if (requestBody.has(RequestBodyKeys.CREATE_STAMP_DESCRIPTION)) {
           String stampDescription = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.CREATE_STAMP_DESCRIPTION));
           if (!RequestValidatorStamp.description(stampDescription)) {
               throw new Exception("Error: invalid input");
           }
           stampEntity.setDescription(stampDescription);
       }
       this.stampRepository.save(stampEntity);
       UUID idStamp = stampEntity.getId();
      // needs to save this required field after because of foreign key constraint (stamp needs to exist before referencing the stampId)
       for (int i = 0; i < stampIdTasks.size(); i++) {
           StampTaskStampHasTaskEntity stampTaskStampHasTaskEntity = new StampTaskStampHasTaskEntity();
           stampTaskStampHasTaskEntity.setIdTask(UUID.fromString(stampIdTasks.get(i)));
           stampTaskStampHasTaskEntity.setIdStamp(idStamp);
           stampTaskStampHasTaskEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
           this.stampTaskStampHasTaskRepository.save(stampTaskStampHasTaskEntity);
       }
       return this.getStampPage(idStamp.toString(), idUser);
    }

    public StampPageModel updateStamp(
            String idUser,
            JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.UPDATE_STAMP_ID)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String stampId = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.UPDATE_STAMP_ID));
        if (
            !RequestValidatorStamp.id(stampId)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }

        Optional<StampEntity> stampEntityOptional = this.stampRepository.findById(UUID.fromString(stampId));
        if (!stampEntityOptional.isPresent()) {
            throw new Exception("Error: invalid input");
        }
        StampEntity stampEntity = stampEntityOptional.get();

        if (requestBody.has(RequestBodyKeys.UPDATE_STAMP_NAME)) {
            String stampName = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_STAMP_NAME));
            if (!RequestValidatorStamp.name(stampName)) {
                throw new Exception("Error: invalid input");
            }
            List<StampEntity> stampEntitiesWithSameName = this.stampRepository.findByNameLowerCase(stampName);
            if (stampEntitiesWithSameName.size() > 0 && !stampEntitiesWithSameName.get(0).getId().toString().equals(stampId)) {
                throw new Exception("STAMP_DUPLICATE_NAME");
            }
            boolean isNoChange = (stampName == null && stampEntity.getName() == null) || 
                (stampName != null && stampName.equals(stampEntity.getName())
            );
            if (!isNoChange) {
                stampEntity.setName(stampName);
                this.stampRepository.save(stampEntity);
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_STAMP_DESCRIPTION)) {
            String stampDescription = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_STAMP_DESCRIPTION));
            if (!RequestValidatorStamp.description(stampDescription)) {
                throw new Exception("Error: invalid input");
            }
            boolean isNoChange = (stampDescription == null && stampEntity.getDescription() == null) || 
                (stampDescription != null && stampDescription.equals(stampEntity.getDescription())
            );
            if (!isNoChange) {
                stampEntity.setDescription(stampDescription);
                this.stampRepository.save(stampEntity);
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_STAMP_IMAGE)) {
            String stampImageAsBase64String = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_STAMP_IMAGE));
            if (!RequestValidatorStamp.imageAsBase64String(stampImageAsBase64String)) {
                throw new Exception("Error: invalid input");
            }
            boolean isNoChange = stampImageAsBase64String == null && stampEntity.getIdImageKey() == null;
            if (!isNoChange) {
                if (stampImageAsBase64String == null) {
                    stampEntity.setIdImageKey(null);
                    this.stampRepository.save(stampEntity);
                } else {
                    UUID stampImageKeyId = UUID.randomUUID();
                    byte[] imageAsByteArray = java.util.Base64.getDecoder().decode(stampImageAsBase64String.substring(stampImageAsBase64String.indexOf(",") + 1));
                    RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
                    Utilities.putObjectInS3(Constants.S3_FOLDER_NAME_STAMP_IMAGE, stampImageKeyId, imageAsByteArrayRequestBody);
                    stampEntity.setIdImageKey(stampImageKeyId);
                    this.stampRepository.save(stampEntity);
                }
                
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_STAMP_ID_TASKS)) {
            List<String> stampIdTasks = new ArrayList<>();
            for (int i = 0; i < requestBody.get(RequestBodyKeys.UPDATE_STAMP_ID_TASKS).size(); i++) {
                String stampIdTask = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.UPDATE_STAMP_ID_TASKS).get(i));
                stampIdTasks.add(stampIdTask);
            }
            // remove all previous taskIds before adding in the new ones
            List<StampTaskStampHasTaskEntity> stampTaskStampHasTaskEntityList = this.stampTaskStampHasTaskRepository.findByIdStamp(stampId);
            for (int i = 0; i < stampTaskStampHasTaskEntityList.size(); i++) {
                this.stampTaskStampHasTaskRepository.delete(stampTaskStampHasTaskEntityList.get(i));
            }
            // add all new taskIds
            for (int i = 0; i < stampIdTasks.size(); i++) {
                StampTaskStampHasTaskEntity stampTaskStampHasTaskEntity = new StampTaskStampHasTaskEntity();
                stampTaskStampHasTaskEntity.setIdTask(UUID.fromString(stampIdTasks.get(i)));
                stampTaskStampHasTaskEntity.setIdStamp(stampEntity.getId());
                stampTaskStampHasTaskEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                this.stampTaskStampHasTaskRepository.save(stampTaskStampHasTaskEntity);
            }
        }
        return this.getStampPage(stampId, idUser);
    }

    private Page<StampCardModel> _getStampCardModelFromSharedAndIndividualProperties(
        String idViewingUser,
        List<StampCardSharedPropertiesDTO> stampCards_SharedProperties,
        List<StampCardIndividualPropertiesDTO> stampCards_IndividualProperties,
        boolean filterIsCollectIndividual,
        boolean filterIsNotCollectIndividual,
        Pageable pageable
    ) {
        HashMap<UUID, StampCardSharedPropertiesDTO> idStampToStampCardSharedPropertiesDTOMap = new HashMap<>();
        HashMap<UUID, StampCardIndividualPropertiesDTO> idStampToStampCardIndividualPropertiesDTOMap = new HashMap<>();

        for (int i = 0; i < stampCards_SharedProperties.size(); i++) {
            idStampToStampCardSharedPropertiesDTOMap.put(stampCards_SharedProperties.get(i).getId(), stampCards_SharedProperties.get(i));
        }
        for (int i = 0; i < stampCards_IndividualProperties.size(); i++) {
            idStampToStampCardIndividualPropertiesDTOMap.put(stampCards_IndividualProperties.get(i).getId(), stampCards_IndividualProperties.get(i));
        }
        List<StampCardModel> stampCardList = new ArrayList<>();
        for (UUID idStamp : idStampToStampCardSharedPropertiesDTOMap.keySet()) {
            StampCardSharedPropertiesDTO stampCardSharedPropertiesEntry = idStampToStampCardSharedPropertiesDTOMap.get(idStamp);
            StampCardIndividualPropertiesDTO stampCardIndividualPropertiesEntry = idStampToStampCardIndividualPropertiesDTOMap.get(idStamp);
            String stampImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_STAMP_IMAGE, stampCardSharedPropertiesEntry.getIdImageKey());
            StampCardModel stampCard = StampCardModel.builder()
                    .id(stampCardSharedPropertiesEntry.getId())
                    .name(stampCardSharedPropertiesEntry.getName())
                    .description(stampCardSharedPropertiesEntry.getDescription())
                    .imageLink(stampImageLink)
                    .numberOfUsersCollect(stampCardSharedPropertiesEntry.getNumberOfUsersCollect())
                    .isPublic(stampCardSharedPropertiesEntry.isPublic())
                    .isCollect(stampCardIndividualPropertiesEntry.isCollect())
                    .build();
            stampCardList.add(stampCard);
        }

        // filter
        stampCardList = stampCardList.stream().filter(stampCard -> {
            boolean isViewingUserAllowedAccessViewStampCard = stampCard.isPublic();
            if (!stampCard.isPublic()) {
                int numberOfPodsAssociatedWithStampUserIsMemberOf = this.stampRepository.getNumberOfPodsAssociatedWithStampUserIsMemberOf(stampCard.getId().toString(), idViewingUser);
                int numberOfPodsAssociatedWithStampTotal = this.stampRepository.getNumberOfPodsAssociatedWithStampTotal(stampCard.getId().toString());
                isViewingUserAllowedAccessViewStampCard = numberOfPodsAssociatedWithStampUserIsMemberOf == numberOfPodsAssociatedWithStampTotal;
            } 
            return isViewingUserAllowedAccessViewStampCard &&
                (stampCard.isCollect() == filterIsCollectIndividual || stampCard.isCollect() != filterIsNotCollectIndividual);
        }).collect(Collectors.toList());

        // sort
        Collections.sort(stampCardList, new Comparator<StampCardModel>() {
            public int compare(StampCardModel stampCard1, StampCardModel stampCard2) {
                if (stampCard1.getNumberOfUsersCollect() > stampCard2.getNumberOfUsersCollect()) {
                    return -1;
                } else if (stampCard1.getNumberOfUsersCollect() < stampCard2.getNumberOfUsersCollect()) {
                    return 1;
                }
                return (stampCard1.getName()).compareTo(stampCard2.getName());
            }
        });

        Page<StampCardModel> stampCardsPage = new PageImpl<StampCardModel>(stampCardList, pageable, stampCardList.size());
        return stampCardsPage;
    }

    public void collectStamp(
            @RequestParam String idUser,
            JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.COLLECT_STAMP_ID)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String stampId = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.COLLECT_STAMP_ID));
        if (
            !RequestValidatorStamp.id(stampId)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        boolean isEligibleToBeCollectedByMe = true;
        List<UUID> stampTaskIds = this.stampTaskStampHasTaskRepository.findByIdStamp(stampId).stream().map(stampTaskStampHasTaskEntity -> stampTaskStampHasTaskEntity.getIdTask()).collect(Collectors.toList());
        for (int i = 0; i < stampTaskIds.size(); i++) {
            boolean isTaskCompleteByMe = this.taskUserTaskCompleteRepository.findByIdTaskIdUser(stampTaskIds.get(i).toString(), idUser).size() > 0;
            if (!isTaskCompleteByMe) {
                isEligibleToBeCollectedByMe = false;
            }
        }
        if (isEligibleToBeCollectedByMe) {
            StampUserUserCollectStampEntity stampUserUserCollectStampEntity = new StampUserUserCollectStampEntity();
            stampUserUserCollectStampEntity.setIdStamp(UUID.fromString(stampId));
            stampUserUserCollectStampEntity.setIdUser(UUID.fromString(idUser));
            stampUserUserCollectStampEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            this.stampUserUserCollectStampRepository.save(stampUserUserCollectStampEntity);
        } else {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
    }
}