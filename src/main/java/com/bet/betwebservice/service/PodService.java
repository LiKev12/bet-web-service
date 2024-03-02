package com.bet.betwebservice.service;

import com.bet.betwebservice.common.*;
import com.bet.betwebservice.dao.v1.NotificationRepository;
import com.bet.betwebservice.dao.v1.PodRepository;
import com.bet.betwebservice.dao.v1.PodUserPodHasUserRepository;
import com.bet.betwebservice.dao.v1.UserRepository;
import com.bet.betwebservice.dto.PodCardIndividualPropertiesDTO;
import com.bet.betwebservice.dto.PodCardSharedPropertiesDTO;
import com.bet.betwebservice.entity.PodEntity;
import com.bet.betwebservice.entity.PodUserPodHasUserEntity;
import com.bet.betwebservice.model.PodCardModel;
import com.bet.betwebservice.model.PodPageModel;
import com.bet.betwebservice.model.UserBubbleModel;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;

import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class PodService {
    private PodRepository podRepository;
    private UserRepository userRepository;
    private PodUserPodHasUserRepository podUserPodHasUserRepository;
    private NotificationRepository notificationRepository;

    public PodService(
        PodRepository podRepository, 
        UserRepository userRepository, 
        PodUserPodHasUserRepository podUserPodHasUserRepository,
        NotificationRepository notificationRepository
    ) {
        this.podRepository = podRepository;
        this.userRepository = userRepository;
        this.podUserPodHasUserRepository = podUserPodHasUserRepository;
        this.notificationRepository = notificationRepository;
    }
    
    public PodPageModel getPodPage(
        String idPod,
        String idUser
    ) throws Exception {
        Optional<PodEntity> podEntityOptional = this.podRepository.findById(UUID.fromString(idPod));
        if (!podEntityOptional.isPresent()) {
            throw new Exception("Error: invalid input");
        }
        PodEntity podEntity = podEntityOptional.get();
        String podImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_POD_IMAGE, podEntity.getIdImageKey());

        List<UserBubbleModel> userBubblesPodMember = this.getUserBubblesPodMember(idPod, idUser);
        List<UserBubbleModel> userBubblesPodModerator = this.getUserBubblesPodModerator(idPod, idUser);

        Set<UUID> userIdsPodMember = new HashSet<>(userBubblesPodMember.stream().map(userBubblePodMember -> {
            return userBubblePodMember.getId();
        }).collect(Collectors.toList()));
        Set<UUID> userIdsPodModerator = new HashSet<>(userBubblesPodModerator.stream().map(userBubblePodModerator -> {
            return userBubblePodModerator.getId();
        }).collect(Collectors.toList()));

        Set<UUID> userIdsBecomePodModeratorRequestSent = new HashSet<>(this.podRepository.getUserIdsBecomePodModeratorRequestSent(idPod));
        return PodPageModel.builder()
            .id(podEntity.getId())
            .name(podEntity.getName())
            .description(podEntity.getDescription())
            .imageLink(podImageLink)
            .userBubblesPodMember(userBubblesPodMember.subList(0, Math.min(userBubblesPodMember.size(), 3)))
            .userBubblesPodMemberTotalNumber(userBubblesPodMember.size())
            .userBubblesPodModerator(userBubblesPodModerator.subList(0, Math.min(userBubblesPodModerator.size(), 3)))
            .userBubblesPodModeratorTotalNumber(userBubblesPodModerator.size())
            .isPodMember(userIdsPodMember.contains(UUID.fromString(idUser)))
            .isPodModerator(userIdsPodModerator.contains(UUID.fromString(idUser)))
            .numberOfPendingBecomeModeratorRequests(this.podRepository.getUserIdsBecomePodModeratorRequestSentNotYetApproved(idPod).size())
            .isSentBecomePodModeratorRequest(userIdsBecomePodModeratorRequestSent.contains(UUID.fromString(idUser)))
            .build();
    }

    public List<UserBubbleModel> getUserBubblesPodMember(String idPod, String idUser) {
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesPodMember = this.userRepository.getUserBubblesPodMember(idPod).stream().map(userBubbleDTOPodMember -> {
            String userBubbleImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userBubbleDTOPodMember.getIdImageKey());
            return UserBubbleModel.builder()
                .id(userBubbleDTOPodMember.getId())
                .name(userBubbleDTOPodMember.getName())
                .username(userBubbleDTOPodMember.getUsername())
                .imageLink(userBubbleImageLink)
                .timestampToSortBy(userBubbleDTOPodMember.getTimestampToSortBy())
                .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleDTOPodMember.getTimestampToSortBy()))
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleDTOPodMember.getTimestampToSortBy()))
                .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTOPodMember.getId()))
                .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleDTOPodMember.getId()))
                .isMe(idUser.equals(userBubbleDTOPodMember.getId().toString()))
                .build();
        }).collect(Collectors.toList());
        Collections.sort(userBubblesPodMember, new Comparator<UserBubbleModel>() {
            public int compare(UserBubbleModel userBubblePodMember1, UserBubbleModel userBubblePodMember2) {
                if (userBubblePodMember1.isMe() == true && userBubblePodMember2.isMe() == false) {
                    return -1;
                } else if (userBubblePodMember1.isMe() == false && userBubblePodMember2.isMe() == true) {
                    return 1;
                }
                if (userBubblePodMember1.isFollowedByMe() == true && userBubblePodMember2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubblePodMember1.isFollowedByMe() == false && userBubblePodMember2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubblePodMember1.getUsername().compareTo(userBubblePodMember2.getUsername());
            }
        });
        return userBubblesPodMember;
    }

    public List<UserBubbleModel> getUserBubblesPodModerator(String idPod, String idUser) {
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesPodModerator = this.userRepository.getUserBubblesPodModerator(idPod).stream().map(userBubbleDTOPodModerator -> {
            String userBubbleImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userBubbleDTOPodModerator.getIdImageKey());
            return UserBubbleModel.builder()
                .id(userBubbleDTOPodModerator.getId())
                .name(userBubbleDTOPodModerator.getName())
                .username(userBubbleDTOPodModerator.getUsername())
                .imageLink(userBubbleImageLink)
                .timestampToSortBy(userBubbleDTOPodModerator.getTimestampToSortBy())
                .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleDTOPodModerator.getTimestampToSortBy()))
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleDTOPodModerator.getTimestampToSortBy()))
                .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTOPodModerator.getId()))
                .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleDTOPodModerator.getId()))
                .isMe(idUser.equals(userBubbleDTOPodModerator.getId().toString()))
                .build();
        }).collect(Collectors.toList());
        Collections.sort(userBubblesPodModerator, new Comparator<UserBubbleModel>() {
            public int compare(UserBubbleModel userBubblePodModerator1, UserBubbleModel userBubblePodModerator2) {
                if (userBubblePodModerator1.isMe() == true && userBubblePodModerator2.isMe() == false) {
                    return -1;
                } else if (userBubblePodModerator1.isMe() == false && userBubblePodModerator2.isMe() == true) {
                    return 1;
                }
                if (userBubblePodModerator1.isFollowedByMe() == true && userBubblePodModerator2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubblePodModerator1.isFollowedByMe() == false && userBubblePodModerator2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubblePodModerator1.getUsername().compareTo(userBubblePodModerator2.getUsername());
            }
        });
        return userBubblesPodModerator;
    }

    public Page<PodCardModel> getPodCardsDiscover(
        String idUser, 
        String filterNameOrDescription,
        boolean filterIsPublic,
        boolean filterIsNotPublic,
        boolean filterIsMemberIndividual,
        boolean filterIsNotMemberIndividual,
        boolean filterIsModeratorIndividual,
        boolean filterIsNotModeratorIndividual,
        Pageable pageable
    ) {
        List<PodCardSharedPropertiesDTO> podCardsDiscover_SharedProperties = this.podRepository.getPodCardsDiscover_SharedProperties(filterNameOrDescription, filterIsPublic, filterIsNotPublic);
        List<PodCardIndividualPropertiesDTO> podCardsDiscover_IndividualProperties = this.podRepository.getPodCardsDiscover_IndividualProperties(idUser, filterNameOrDescription, filterIsPublic, filterIsNotPublic);
        Page<PodCardModel> podCardsPage = this._getPodCardModelFromSharedAndIndividualProperties(
                idUser,
                podCardsDiscover_SharedProperties, 
                podCardsDiscover_IndividualProperties,
                filterIsPublic,
                filterIsNotPublic,
                filterIsMemberIndividual,
                filterIsNotMemberIndividual,
                filterIsModeratorIndividual,
                filterIsNotModeratorIndividual,
                pageable
        );
        return podCardsPage;
    }

    
    public Page<PodCardModel> getPodCardsAssociatedWithStamp(
        String idStamp, 
        String idUser,
        String filterNameOrDescription,
        boolean filterIsPublic,
        boolean filterIsNotPublic,
        boolean filterIsMemberIndividual,
        boolean filterIsNotMemberIndividual,
        boolean filterIsModeratorIndividual,
        boolean filterIsNotModeratorIndividual,
        Pageable pageable
    ) {
        List<PodCardSharedPropertiesDTO> podCardsAssociatedWithStamp_SharedProperties = this.podRepository.getPodCardsAssociatedWithStamp_SharedProperties(idStamp, filterNameOrDescription, filterIsPublic, filterIsNotPublic);
        List<PodCardIndividualPropertiesDTO> podCardsAssociatedWithStamp_IndividualProperties = this.podRepository.getPodCardsAssociatedWithStamp_IndividualProperties(idStamp, idUser, filterNameOrDescription, filterIsPublic, filterIsNotPublic);
        Page<PodCardModel> podCardsPage = this._getPodCardModelFromSharedAndIndividualProperties(
                idUser,
                podCardsAssociatedWithStamp_SharedProperties, 
                podCardsAssociatedWithStamp_IndividualProperties,
                filterIsPublic,
                filterIsNotPublic,
                filterIsMemberIndividual,
                filterIsNotMemberIndividual,
                filterIsModeratorIndividual,
                filterIsNotModeratorIndividual,
                pageable
        );
        return podCardsPage;
    }

    public Page<PodCardModel> getPodCardsAssociatedWithUser(
        String idUserProfile,
        String idUser,
        String filterNameOrDescription,
        boolean filterIsPublic,
        boolean filterIsNotPublic,
        boolean filterIsMemberIndividual,
        boolean filterIsNotMemberIndividual,
        boolean filterIsModeratorIndividual,
        boolean filterIsNotModeratorIndividual,
        Pageable pageable
    ) {
        List<PodCardSharedPropertiesDTO> podCardsAssociatedWithUser_SharedProperties = this.podRepository.getPodCardsAssociatedWithUser_SharedProperties(filterNameOrDescription, filterIsPublic, filterIsNotPublic);
        List<PodCardIndividualPropertiesDTO> podCardsAssociatedWithUser_IndividualProperties = this.podRepository.getPodCardsAssociatedWithUser_IndividualProperties(idUser, filterNameOrDescription, filterIsPublic, filterIsNotPublic);
        
        // need to filter for pods that user actually belongs to AFTER query above because calculation numberOfMembers requires this
        Set<UUID> podIdsProfileUserIsMemberOf = new HashSet<>(this.podRepository.getPodIdsUserIsMemberOf(idUserProfile));
        podCardsAssociatedWithUser_SharedProperties = podCardsAssociatedWithUser_SharedProperties.stream().filter(podCard -> {
            return podIdsProfileUserIsMemberOf.contains(podCard.getId());
        }).collect(Collectors.toList());
        podCardsAssociatedWithUser_IndividualProperties = podCardsAssociatedWithUser_IndividualProperties.stream().filter(podCard -> {
            return podIdsProfileUserIsMemberOf.contains(podCard.getId());
        }).collect(Collectors.toList());

        Page<PodCardModel> podCardsPage = this._getPodCardModelFromSharedAndIndividualProperties(
                idUser,
                podCardsAssociatedWithUser_SharedProperties, 
                podCardsAssociatedWithUser_IndividualProperties,
                filterIsPublic,
                filterIsNotPublic,
                filterIsMemberIndividual,
                filterIsNotMemberIndividual,
                filterIsModeratorIndividual,
                filterIsNotModeratorIndividual,
                pageable
        );
        return podCardsPage;
    }

    public PodPageModel createPod(
            String idUser,
            JsonNode requestBody
    ) throws Exception {
        if (
           idUser == null ||
           !requestBody.has(RequestBodyKeys.CREATE_POD_NAME) ||
           !requestBody.has(RequestBodyKeys.CREATE_POD_IS_PUBLIC)
       ) {
           throw new Exception("Error: invalid input"); // TODO create an exception class
       }
       String podIdUserCreate = idUser;
       String podName = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.CREATE_POD_NAME));
       boolean podIsPublic = requestBody.get(RequestBodyKeys.CREATE_POD_IS_PUBLIC).asBoolean();
       if (
           !RequestValidatorPod.id(podIdUserCreate) ||
           !RequestValidatorPod.name(podName) ||
           !RequestValidatorPod.isPublic(podIsPublic)
       ) {
           throw new Exception("Error: invalid input"); // TODO create an exception class
       }

       List<PodEntity> podEntitiesWithSameName = this.podRepository.findByNameLowerCase(podName);
       if (podEntitiesWithSameName.size() > 0) {
           throw new Exception("POD_DUPLICATE_NAME");
       }

       // save processed input
       PodEntity podEntity = new PodEntity();
       podEntity.setIdUserCreate(UUID.fromString(idUser));
       podEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
       podEntity.setName(podName);
       podEntity.setPublic(podIsPublic);
       if (requestBody.has(RequestBodyKeys.CREATE_POD_DESCRIPTION)) {
           String podDescription = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.CREATE_POD_DESCRIPTION));
           if (!RequestValidatorPod.description(podDescription)) {
               throw new Exception("Error: invalid input");
           }
           podEntity.setDescription(podDescription);
       }
       this.podRepository.save(podEntity);

       PodUserPodHasUserEntity podUserPodHasUserEntity = new PodUserPodHasUserEntity();
       podUserPodHasUserEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
       podUserPodHasUserEntity.setIdPod(podEntity.getId());
       podUserPodHasUserEntity.setIdUser(UUID.fromString(idUser));
       podUserPodHasUserEntity.setMember(true);
       podUserPodHasUserEntity.setTimestampBecomeMember((int) Instant.now().getEpochSecond());
       podUserPodHasUserEntity.setModerator(true);
       podUserPodHasUserEntity.setTimestampBecomeModerator((int) Instant.now().getEpochSecond());
       this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);

       return this.getPodPage(podEntity.getId().toString(), idUser);
    }

    public PodPageModel updatePod(
            String idUser,
            JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.UPDATE_POD_ID)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String podId = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.UPDATE_POD_ID));
        if (
            !RequestValidatorPod.id(podId)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }

        Optional<PodEntity> podEntityOptional = this.podRepository.findById(UUID.fromString(podId));
        if (!podEntityOptional.isPresent()) {
            throw new Exception("Error: invalid input");
        }
        PodEntity podEntity = podEntityOptional.get();

        if (requestBody.has(RequestBodyKeys.UPDATE_POD_NAME)) {
            String podName = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_POD_NAME));
            if (!RequestValidatorPod.name(podName)) {
                throw new Exception("Error: invalid input");
            }
            List<PodEntity> podEntitiesWithSameName = this.podRepository.findByNameLowerCase(podName);
            if (podEntitiesWithSameName.size() > 0 && !podEntitiesWithSameName.get(0).getId().toString().equals(podId)) {
                throw new Exception("POD_DUPLICATE_NAME");
            }
            boolean isNoChange = (podName == null && podEntity.getName() == null) || 
                (podName != null && podName.equals(podEntity.getName())
            );
            if (!isNoChange) {
                podEntity.setName(podName);
                this.podRepository.save(podEntity);
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_POD_DESCRIPTION)) {
            String podDescription = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_POD_DESCRIPTION));
            if (!RequestValidatorPod.description(podDescription)) {
                throw new Exception("Error: invalid input");
            }
            boolean isNoChange = (podDescription == null && podEntity.getDescription() == null) || 
                (podDescription != null && podDescription.equals(podEntity.getDescription())
            );
            if (!isNoChange) {
                podEntity.setDescription(podDescription);
                this.podRepository.save(podEntity);
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_POD_IMAGE)) {
            String podImageAsBase64String = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_POD_IMAGE));
            if (!RequestValidatorPod.imageAsBase64String(podImageAsBase64String)) {
                throw new Exception("Error: invalid input");
            }
            boolean isNoChange = podImageAsBase64String == null && podEntity.getIdImageKey() == null;
            if (!isNoChange) {
                if (podImageAsBase64String == null) {
                    podEntity.setIdImageKey(null);
                    this.podRepository.save(podEntity);
                } else {
                    UUID podImageKeyId = UUID.randomUUID();
                    byte[] imageAsByteArray = java.util.Base64.getDecoder().decode(podImageAsBase64String.substring(podImageAsBase64String.indexOf(",") + 1));
                    RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
                    Utilities.putObjectInS3(Constants.S3_FOLDER_NAME_POD_IMAGE, podImageKeyId, imageAsByteArrayRequestBody);
                    podEntity.setIdImageKey(podImageKeyId);
                    this.podRepository.save(podEntity);
                }
            }
        }
        return this.getPodPage(podId, idUser);
    }

    public void joinPod(
        String idUserJoinPod,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUserJoinPod == null ||
            !requestBody.has(RequestBodyKeys.JOIN_POD_POD_ID)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idPod = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.JOIN_POD_POD_ID));
        if (
            !RequestValidatorPod.id(idPod)
        ) {
            throw new Exception("Error: invalid input");
        }
        List<PodUserPodHasUserEntity> podUserPodHasUserEntityList = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUserJoinPod);
        boolean isJoinPodInviteAlreadySent = podUserPodHasUserEntityList.size() > 0;
        if (isJoinPodInviteAlreadySent) {
            // if you are the only one in the Pod, you are the moderator
            boolean isTheOnlyMemberOfPod = this.userRepository.getUserBubblesPodMember(idPod).size() == 0;
            PodUserPodHasUserEntity podUserPodHasUserEntity = podUserPodHasUserEntityList.get(0);
            podUserPodHasUserEntity.setMember(true);
            podUserPodHasUserEntity.setTimestampBecomeMember((int) Instant.now().getEpochSecond());
            if (isTheOnlyMemberOfPod) {
                podUserPodHasUserEntity.setModerator(true);
                podUserPodHasUserEntity.setTimestampBecomeModerator((int) Instant.now().getEpochSecond());
            }
            this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
        } else {
            // if you are the only one in the Pod, you are the moderator
            boolean isTheOnlyMemberOfPod = this.userRepository.getUserBubblesPodMember(idPod).size() == 0;
            PodUserPodHasUserEntity podUserPodHasUserEntity = new PodUserPodHasUserEntity();
            podUserPodHasUserEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            podUserPodHasUserEntity.setIdPod(UUID.fromString(idPod));
            podUserPodHasUserEntity.setIdUser(UUID.fromString(idUserJoinPod));
            podUserPodHasUserEntity.setMember(true);
            podUserPodHasUserEntity.setTimestampBecomeMember((int) Instant.now().getEpochSecond());
            if (isTheOnlyMemberOfPod) {
                podUserPodHasUserEntity.setModerator(true);
                podUserPodHasUserEntity.setTimestampBecomeModerator((int) Instant.now().getEpochSecond());
            }
            this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
        }
    }

    public void leavePod(
        String idUser,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.LEAVE_POD_POD_ID)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idPod = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.LEAVE_POD_POD_ID));
        if (
            !RequestValidatorPod.id(idPod)
        ) {
            throw new Exception("Error: invalid input");
        }
        PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser).get(0);
        this.podUserPodHasUserRepository.delete(podUserPodHasUserEntity);
    }

    public List<UserBubbleModel> sendJoinPodInvite(
            String idUserSendInvite,
            JsonNode requestBody
    ) throws Exception {
        if (
            idUserSendInvite == null ||
            !requestBody.has(RequestBodyKeys.SEND_JOIN_POD_INVITE_ID_USERS_RECEIVE_INVITE) ||
            !requestBody.has(RequestBodyKeys.SEND_JOIN_POD_INVITE_POD_ID)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idPod = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.SEND_JOIN_POD_INVITE_POD_ID));
        List<String> idUsersReceiveInvite = new ArrayList<>();
        for (int i = 0; i < requestBody.get(RequestBodyKeys.SEND_JOIN_POD_INVITE_ID_USERS_RECEIVE_INVITE).size(); i++) {
            String idUserBecomeModerator = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.SEND_JOIN_POD_INVITE_ID_USERS_RECEIVE_INVITE).get(i));
            idUsersReceiveInvite.add(idUserBecomeModerator);
        }
        if (
            !RequestValidatorPod.id(idPod) ||
            !RequestValidatorPod.idUsersReceiveJoinPodInvite(idUsersReceiveInvite)
        ) {
            throw new Exception("Error: invalid input");
        }
        // always create new entry because idUserReceiveInvite is not a member yet
        boolean isIdUserSendInviteIsMember = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUserSendInvite).get(0).isMember();
        if (!isIdUserSendInviteIsMember) {
            throw new Exception("USER_NOT_AUTHORIZED_TO_SEND_INVITE");
        }
        for (int i = 0; i < idUsersReceiveInvite.size(); i++) {
            String idUserReceiveInvite = idUsersReceiveInvite.get(i);
            // guaranteed new entry, because one can only send join invite to non-members (who haven't yet received an invite yet), and non-members don't have an entry yet
            PodUserPodHasUserEntity podUserPodHasUserEntity = new PodUserPodHasUserEntity();
            podUserPodHasUserEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            podUserPodHasUserEntity.setIdPod(UUID.fromString(idPod));
            podUserPodHasUserEntity.setIdUser(UUID.fromString(idUserReceiveInvite));
            podUserPodHasUserEntity.setJoinPodInviteSent(true);
            podUserPodHasUserEntity.setIdUserJoinPodInviteSender(UUID.fromString(idUserSendInvite));
            podUserPodHasUserEntity.setTimestampJoinPodInviteSent((int) Instant.now().getEpochSecond());
            this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
            Utilities.generateNotification(
                this.notificationRepository,
                UUID.fromString(idUserReceiveInvite),
                Constants.NOTIFICATION_TYPE_SENT_YOU_JOIN_POD_INVITE,
                String.format("@%s has invited you to join Pod: %s", this.userRepository.findById(UUID.fromString(idUserSendInvite)).get().getUsername(), this.podRepository.findById(UUID.fromString(idPod)).get().getName()),
                Constants.NOTIFICATION_LINK_PAGE_TYPE_POD,
                UUID.fromString(idPod)
            );
        }
        
        return this.getUserBubblesInviteJoinPod(idPod, idUserSendInvite);
    }

    public void acceptJoinPodInvite(
        String idUserAcceptInvite,
        JsonNode requestBody
    ) throws Exception {
        // TODO: TEST with notifications
        if (
            idUserAcceptInvite == null ||
            !requestBody.has(RequestBodyKeys.ACCEPT_JOIN_POD_INVITE_POD_ID)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idPod = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.ACCEPT_JOIN_POD_INVITE_POD_ID));
        if (
            !RequestValidatorPod.id(idPod)
        ) {
            throw new Exception("Error: invalid input");
        }
        PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUserAcceptInvite).get(0);
        podUserPodHasUserEntity.setJoinPodInviteAccepted(true);
        podUserPodHasUserEntity.setMember(true);
        podUserPodHasUserEntity.setTimestampBecomeMember((int) Instant.now().getEpochSecond());
        this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
    }
    
    public void declineJoinPodInvite(
        String idUserDeclineInvite,
        JsonNode requestBody
    ) throws Exception {
        // TODO: TEST with notifications
        if (
            idUserDeclineInvite == null ||
            !requestBody.has(RequestBodyKeys.DECLINE_JOIN_POD_INVITE_POD_ID)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idPod = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.DECLINE_JOIN_POD_INVITE_POD_ID));
        if (
            !RequestValidatorPod.id(idPod)
        ) {
            throw new Exception("Error: invalid input");
        }
        PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUserDeclineInvite).get(0);
        if (podUserPodHasUserEntity.isMember()) {
            // should never reach here but just in case
            return;
        } else {
            this.podUserPodHasUserRepository.delete(podUserPodHasUserEntity);
        }
    }

    public void sendBecomePodModeratorRequest(
        String idUserSendRequest,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUserSendRequest == null ||
            !requestBody.has(RequestBodyKeys.SEND_BECOME_POD_MODERATOR_REQUEST_POD_ID)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idPod = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.SEND_BECOME_POD_MODERATOR_REQUEST_POD_ID));
        if (
            !RequestValidatorPod.id(idPod)
        ) {
            throw new Exception("Error: invalid input");
        }
        PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUserSendRequest).get(0);
        if (!podUserPodHasUserEntity.isMember()) {
            throw new Exception("USER_NOT_AUTHORIZED_TO_SEND_REQUEST");
        }
        podUserPodHasUserEntity.setBecomePodModeratorRequestSent(true);
        podUserPodHasUserEntity.setTimestampBecomePodModeratorRequestSent((int) Instant.now().getEpochSecond());
        this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
    }

    public List<UserBubbleModel> approveBecomePodModeratorRequests(
        String idUserApproveRequest,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUserApproveRequest == null ||
            !requestBody.has(RequestBodyKeys.APPROVE_BECOME_POD_MODERATOR_REQUESTS_POD_ID) || 
            !requestBody.has(RequestBodyKeys.APPROVE_BECOME_POD_MODERATOR_REQUESTS_ID_USERS_WITH_REQUESTS_APPROVED)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idPod = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.APPROVE_BECOME_POD_MODERATOR_REQUESTS_POD_ID));
        List<String> idUsersSentBecomePodModeratorRequest = new ArrayList<>();
        for (int i = 0; i < requestBody.get(RequestBodyKeys.APPROVE_BECOME_POD_MODERATOR_REQUESTS_ID_USERS_WITH_REQUESTS_APPROVED).size(); i++) {
            String idUserSentBecomePodModeratorRequest = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.APPROVE_BECOME_POD_MODERATOR_REQUESTS_ID_USERS_WITH_REQUESTS_APPROVED).get(i));
            idUsersSentBecomePodModeratorRequest.add(idUserSentBecomePodModeratorRequest);
        }
        if (
            !RequestValidatorPod.id(idPod) ||
            !RequestValidatorPod.idUsersWithApprovedBecomeModeratorRequest(idUsersSentBecomePodModeratorRequest)
        ) {
            throw new Exception("Error: invalid input");
        }
        boolean isIdUserApproveRequestModerator = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUserApproveRequest).size() > 0 && 
            this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUserApproveRequest).get(0).isModerator();
        if (!isIdUserApproveRequestModerator) {
            throw new Exception("USER_NOT_AUTHORIZED_TO_APPROVE_REQUEST");
        }
        for (int i = 0; i < idUsersSentBecomePodModeratorRequest.size(); i++) {
            String idUserSentBecomePodModeratorRequest = idUsersSentBecomePodModeratorRequest.get(i);
            PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUserSentBecomePodModeratorRequest).get(0);
            podUserPodHasUserEntity.setModerator(true);
            podUserPodHasUserEntity.setTimestampBecomeModerator((int) Instant.now().getEpochSecond());
            podUserPodHasUserEntity.setBecomePodModeratorRequestApproved(true);
            podUserPodHasUserEntity.setIdUserBecomePodModeratorRequestApprover(UUID.fromString(idUserApproveRequest));
            this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
            Utilities.generateNotification(
                this.notificationRepository,
                UUID.fromString(idUserSentBecomePodModeratorRequest),
                Constants.NOTIFICATION_TYPE_APPROVED_YOUR_BECOME_POD_MODERATOR_REQUEST,
                String.format("@%s has approved your request to become moderator of Pod: %s", this.userRepository.findById(UUID.fromString(idUserApproveRequest)).get().getUsername(), this.podRepository.findById(UUID.fromString(idPod)).get().getName()),
                Constants.NOTIFICATION_LINK_PAGE_TYPE_POD,
                UUID.fromString(idPod)
            );
        }
        return this.getUserBubblesPendingBecomePodModeratorRequest(idPod, idUserApproveRequest);
    }

    public List<UserBubbleModel> rejectBecomePodModeratorRequests(
        String idUserRejectRequest,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUserRejectRequest == null ||
            !requestBody.has(RequestBodyKeys.REJECT_BECOME_POD_MODERATOR_REQUESTS_POD_ID) || 
            !requestBody.has(RequestBodyKeys.REJECT_BECOME_POD_MODERATOR_REQUESTS_ID_USERS_WITH_REQUESTS_REJECTED)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idPod = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.REJECT_BECOME_POD_MODERATOR_REQUESTS_POD_ID));
        List<String> idUsersSentBecomePodModeratorRequest = new ArrayList<>();
        for (int i = 0; i < requestBody.get(RequestBodyKeys.REJECT_BECOME_POD_MODERATOR_REQUESTS_ID_USERS_WITH_REQUESTS_REJECTED).size(); i++) {
            String idUserSentBecomePodModeratorRequest = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.REJECT_BECOME_POD_MODERATOR_REQUESTS_ID_USERS_WITH_REQUESTS_REJECTED).get(i));
            idUsersSentBecomePodModeratorRequest.add(idUserSentBecomePodModeratorRequest);
        }
        if (
            !RequestValidatorPod.id(idPod) ||
            !RequestValidatorPod.idUsersWithRejectedBecomeModeratorRequest(idUsersSentBecomePodModeratorRequest)
        ) {
            throw new Exception("Error: invalid input");
        }
        boolean isIdUserRejectRequestModerator = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUserRejectRequest).size() > 0 && 
            this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUserRejectRequest).get(0).isModerator();
        if (!isIdUserRejectRequestModerator) {
            throw new Exception("USER_NOT_AUTHORIZED_TO_REJECT_REQUEST");
        }
        for (int i = 0; i < idUsersSentBecomePodModeratorRequest.size(); i++) {
            String idUserSentBecomePodModeratorRequest = idUsersSentBecomePodModeratorRequest.get(i);
            PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUserSentBecomePodModeratorRequest).get(0);
            podUserPodHasUserEntity.setBecomePodModeratorRequestSent(false);
            podUserPodHasUserEntity.setTimestampBecomePodModeratorRequestSent(null);
            this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
        }
        return this.getUserBubblesPendingBecomePodModeratorRequest(idPod, idUserRejectRequest);
    }

    public List<UserBubbleModel> addPodModerators(
        String idUserModeratorAddingOtherModerators,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUserModeratorAddingOtherModerators == null ||
            !requestBody.has(RequestBodyKeys.ADD_POD_MODERATORS_ID_USERS_TO_BECOME_MODERATOR)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idPod = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.ADD_POD_MODERATORS_ID_POD));
        List<String> idUsersToBecomeModerator = new ArrayList<>();
        for (int i = 0; i < requestBody.get(RequestBodyKeys.ADD_POD_MODERATORS_ID_USERS_TO_BECOME_MODERATOR).size(); i++) {
            String idUserBecomeModerator = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.ADD_POD_MODERATORS_ID_USERS_TO_BECOME_MODERATOR).get(i));
            idUsersToBecomeModerator.add(idUserBecomeModerator);
        }
        if (
            !RequestValidatorPod.id(idPod) ||
            !RequestValidatorPod.idUsersToBecomeModerator(idUsersToBecomeModerator)
        ) {
            throw new Exception("Error: invalid input");
        }
        for (int i = 0; i < idUsersToBecomeModerator.size(); i++) {
            String idUserToBecomeModerator = idUsersToBecomeModerator.get(i);
            PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUserToBecomeModerator).get(0);
            podUserPodHasUserEntity.setModerator(true);
            podUserPodHasUserEntity.setTimestampBecomeModerator((int) Instant.now().getEpochSecond());
            this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
            Utilities.generateNotification(
                this.notificationRepository,
                UUID.fromString(idUserToBecomeModerator),
                Constants.NOTIFICATION_TYPE_ADDED_YOU_AS_POD_MODERATOR,
                String.format("@%s has added you as a moderator of Pod: %s", this.userRepository.findById(UUID.fromString(idUserModeratorAddingOtherModerators)).get().getUsername(), this.podRepository.findById(UUID.fromString(idPod)).get().getName()),
                Constants.NOTIFICATION_LINK_PAGE_TYPE_POD,
                UUID.fromString(idPod)
            );
        }
        return this.getUserBubblesAddPodModerator(idPod, idUserModeratorAddingOtherModerators);
    }

    public List<UserBubbleModel> getUserBubblesInviteJoinPod(
        String idPod,
        String idUser
    ) {
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesInviteJoinPod = this.userRepository.getUserBubblesFollowing(idUser).stream().filter(userBubbleDTO -> {
            // keep only the following users: users that you follow who aren't a member and who haven't gotten a invite to join the pod yet
            List<PodUserPodHasUserEntity> podUserPodHasUserEntityList = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, userBubbleDTO.getId().toString());
            if (podUserPodHasUserEntityList.size() == 0) {
                return true;
            }
            if (podUserPodHasUserEntityList.get(0).isMember() || podUserPodHasUserEntityList.get(0).isJoinPodInviteSent()) {
                return false;
            }
            return true;
        }).map(userBubbleDTO -> {
                String userBubbleImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userBubbleDTO.getIdImageKey());
                return UserBubbleModel.builder()
                .id(userBubbleDTO.getId())
                .name(userBubbleDTO.getName())
                .username(userBubbleDTO.getUsername())
                .imageLink(userBubbleImageLink)
                .timestampToSortBy(userBubbleDTO.getTimestampToSortBy())
                .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleDTO.getTimestampToSortBy()))
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleDTO.getTimestampToSortBy()))
                .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTO.getId()))
                .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleDTO.getId()))
                .isMe(idUser.equals(userBubbleDTO.getId().toString()))
                .build();
        }).collect(Collectors.toList());
        Collections.sort(userBubblesInviteJoinPod, new Comparator<UserBubbleModel>() {
            public int compare(UserBubbleModel userBubble1, UserBubbleModel userBubble2) {
                if (userBubble1.isMe() == true && userBubble2.isMe() == false) {
                    return -1;
                } else if (userBubble1.isMe() == false && userBubble2.isMe() == true) {
                    return 1;
                }
                if (userBubble1.isFollowedByMe() == true && userBubble2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubble1.isFollowedByMe() == false && userBubble2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubble1.getUsername().compareTo(userBubble2.getUsername());
            }
        });
        return userBubblesInviteJoinPod;
    }

    public List<UserBubbleModel> getUserBubblesAddPodModerator(
        String idPod,
        String idUser
    ) {
        Set<UUID> userIdsPodModerator = new HashSet<>(this.userRepository.getUserBubblesPodModerator(idPod).stream().map(userBubbleDTO -> userBubbleDTO.getId()).collect(Collectors.toList()));
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);

        List<UserBubbleModel> userBubblesAddPodModerator = this.userRepository.getUserBubblesPodMember(idPod).stream().filter(userBubbleDTO -> {
            // keep those that haven't become moderators yet
            return !userIdsPodModerator.contains(userBubbleDTO.getId());
        }).map(userBubbleDTO -> {
                String userBubbleImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userBubbleDTO.getIdImageKey());
                return UserBubbleModel.builder()
                .id(userBubbleDTO.getId())
                .name(userBubbleDTO.getName())
                .username(userBubbleDTO.getUsername())
                .imageLink(userBubbleImageLink)
                .timestampToSortBy(userBubbleDTO.getTimestampToSortBy())
                .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleDTO.getTimestampToSortBy()))
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleDTO.getTimestampToSortBy()))
                .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTO.getId()))
                .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleDTO.getId()))
                .isMe(idUser.equals(userBubbleDTO.getId().toString()))
                .build();
        }).collect(Collectors.toList());
        Collections.sort(userBubblesAddPodModerator, new Comparator<UserBubbleModel>() {
            public int compare(UserBubbleModel userBubble1, UserBubbleModel userBubble2) {
                if (userBubble1.isMe() == true && userBubble2.isMe() == false) {
                    return -1;
                } else if (userBubble1.isMe() == false && userBubble2.isMe() == true) {
                    return 1;
                }
                if (userBubble1.isFollowedByMe() == true && userBubble2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubble1.isFollowedByMe() == false && userBubble2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubble1.getUsername().compareTo(userBubble2.getUsername());
            }
        });
        return userBubblesAddPodModerator;
    }

    public List<UserBubbleModel> getUserBubblesPendingBecomePodModeratorRequest(
        String idPod,
        String idUser
    ) {
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);

        List<UserBubbleModel> userBubblesPendingBecomePodModeratorRequest = this.userRepository.getUserBubblesBecomePodModeratorRequestSentNotYetApproved(idPod).stream().map(userBubbleDTO -> {
                String userBubbleImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userBubbleDTO.getIdImageKey());
                return UserBubbleModel.builder()
                .id(userBubbleDTO.getId())
                .name(userBubbleDTO.getName())
                .username(userBubbleDTO.getUsername())
                .imageLink(userBubbleImageLink)
                .timestampToSortBy(userBubbleDTO.getTimestampToSortBy())
                .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleDTO.getTimestampToSortBy()))
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleDTO.getTimestampToSortBy()))
                .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTO.getId()))
                .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleDTO.getId()))
                .isMe(idUser.equals(userBubbleDTO.getId().toString()))
                .build();
        }).collect(Collectors.toList());
        Collections.sort(userBubblesPendingBecomePodModeratorRequest, new Comparator<UserBubbleModel>() {
            public int compare(UserBubbleModel userBubble1, UserBubbleModel userBubble2) {
                if (userBubble1.isMe() == true && userBubble2.isMe() == false) {
                    return -1;
                } else if (userBubble1.isMe() == false && userBubble2.isMe() == true) {
                    return 1;
                }
                if (userBubble1.isFollowedByMe() == true && userBubble2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubble1.isFollowedByMe() == false && userBubble2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubble1.getUsername().compareTo(userBubble2.getUsername());
            }
        });
        return userBubblesPendingBecomePodModeratorRequest;
    }
    
    private Page<PodCardModel> _getPodCardModelFromSharedAndIndividualProperties(
        String idViewingUser,
        List<PodCardSharedPropertiesDTO> podCards_SharedProperties,
        List<PodCardIndividualPropertiesDTO> podCards_IndividualProperties,
        boolean filterIsPublic,
        boolean filterIsNotPublic,
        boolean filterIsMemberIndividual,
        boolean filterIsNotMemberIndividual,
        boolean filterIsModeratorIndividual,
        boolean filterIsNotModeratorIndividual,
        Pageable pageable
    ) {
        HashMap<UUID, PodCardSharedPropertiesDTO> idStampToPodCardSharedPropertiesDTOMap = new HashMap<>();
        HashMap<UUID, PodCardIndividualPropertiesDTO> idStampToPodCardIndividualPropertiesDTOMap = new HashMap<>();

        for (int i = 0; i < podCards_SharedProperties.size(); i++) {
            idStampToPodCardSharedPropertiesDTOMap.put(podCards_SharedProperties.get(i).getId(), podCards_SharedProperties.get(i));
        }
        for (int i = 0; i < podCards_IndividualProperties.size(); i++) {
            idStampToPodCardIndividualPropertiesDTOMap.put(podCards_IndividualProperties.get(i).getId(), podCards_IndividualProperties.get(i));
        }
        List<PodCardModel> podCardList = new ArrayList<>();
        for (UUID idStamp : idStampToPodCardSharedPropertiesDTOMap.keySet()) {
            PodCardSharedPropertiesDTO podCardSharedPropertiesEntry = idStampToPodCardSharedPropertiesDTOMap.get(idStamp);
            PodCardIndividualPropertiesDTO podCardIndividualPropertiesEntry = idStampToPodCardIndividualPropertiesDTOMap.get(idStamp);
            String podImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_POD_IMAGE, podCardSharedPropertiesEntry.getIdImageKey());
            PodCardModel podCard = PodCardModel.builder()
                        .id(podCardSharedPropertiesEntry.getId())
                        .name(podCardSharedPropertiesEntry.getName())
                        .description(podCardSharedPropertiesEntry.getDescription())
                        .imageLink(podImageLink)
                        .isPublic(podCardSharedPropertiesEntry.isPublic())
                        .numberOfMembers(podCardSharedPropertiesEntry.getNumberOfMembers())
                        .isMember(podCardIndividualPropertiesEntry.isMember())
                        .isModerator(podCardIndividualPropertiesEntry.isModerator())
                        .build();
            podCardList.add(podCard);
        }

        // filter
        Set<UUID> podIdsViewingUserIsMemberOf = new HashSet<>(this.podRepository.getPodIdsUserIsMemberOf(idViewingUser));
        podCardList = podCardList.stream().filter(podCard -> {
            boolean isViewingUserAllowedAccessViewPodCard = podCard.isPublic();
            if (!podCard.isPublic()) {
                isViewingUserAllowedAccessViewPodCard = podIdsViewingUserIsMemberOf.contains(podCard.getId());
            }
            return isViewingUserAllowedAccessViewPodCard &&
                (podCard.isPublic() == filterIsPublic || podCard.isPublic() != filterIsNotPublic) &&
                (podCard.isMember() == filterIsMemberIndividual || podCard.isMember() != filterIsNotMemberIndividual) &&
                (podCard.isModerator() == filterIsModeratorIndividual || podCard.isModerator() != filterIsNotModeratorIndividual);
        }).collect(Collectors.toList());

        // sort
        Collections.sort(podCardList, new Comparator<PodCardModel>() {
            public int compare(PodCardModel podCard1, PodCardModel podCard2) {
                if (podCard1.getNumberOfMembers() > podCard2.getNumberOfMembers()) {
                    return -1;
                } else if (podCard1.getNumberOfMembers() < podCard2.getNumberOfMembers()) {
                    return 1;
                }
                return (podCard1.getName()).compareTo(podCard2.getName());
            }
        });

        Page<PodCardModel> podCardsPage = new PageImpl<PodCardModel>(podCardList, pageable, podCardList.size());
        return podCardsPage;
    }
}
