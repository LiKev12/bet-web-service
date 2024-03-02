package com.bet.betwebservice.service;

import com.bet.betwebservice.authentication.JwtTokenWrapper;
import com.bet.betwebservice.common.*;
import com.bet.betwebservice.dao.v1.*;
import com.bet.betwebservice.dto.NumberOfPointsInTasksCompletedOverTimeVisualizationDTO;
import com.bet.betwebservice.entity.NotificationEntity;
import com.bet.betwebservice.entity.UserEntity;
import com.bet.betwebservice.entity.UserUserUser1FollowUser2Entity;
import com.bet.betwebservice.model.NotificationModel;
import com.bet.betwebservice.model.PersonalPageModel;
import com.bet.betwebservice.model.UserBubbleModel;
import com.bet.betwebservice.model.UserPageModel;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import software.amazon.awssdk.core.sync.RequestBody;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class UserService {

    private UserRepository userRepository;
    private TaskRepository taskRepository;
    private PodRepository podRepository;
    private UserUserUser1FollowUser2Repository userUserUser1FollowUser2Repository;
    private NotificationRepository notificationRepository;

    @Autowired
    private JwtTokenWrapper jwtTokenWrapper;

    public UserService(
        UserRepository userRepository,
        TaskRepository taskRepository, 
        PodRepository podRepository,
        UserUserUser1FollowUser2Repository userUserUser1FollowUser2Repository,
        NotificationRepository notificationRepository
    ) {
        this.userRepository = userRepository;
        this.taskRepository = taskRepository;
        this.podRepository = podRepository;
        this.userUserUser1FollowUser2Repository = userUserUser1FollowUser2Repository;
        this.notificationRepository = notificationRepository;
    }

    public UserPageModel getUserPage(
        String idUserOfUserPage,
        String idViewingUser
    ) throws Exception {
        Optional<UserEntity> userEntityOptional = this.userRepository.findById(UUID.fromString(idUserOfUserPage));
        if (!userEntityOptional.isPresent()) {
            throw new Exception("Error: invalid input");
        }
        UserEntity userEntity = userEntityOptional.get();
        String userImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userEntity.getIdImageKey());
        List<UserBubbleModel> userBubblesFollowing = this.getUserBubblesFollowing(idUserOfUserPage, idViewingUser);
        List<UserBubbleModel> userBubblesFollower = this.getUserBubblesFollower(idUserOfUserPage, idViewingUser);
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idViewingUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idViewingUser));

        return UserPageModel.builder()
            .id(userEntity.getId())
            .username(userEntity.getUsername())
            .name(userEntity.getName())
            .bio(userEntity.getBio())
            .imageLink(userImageLink)
            .userBubblesFollowing(userBubblesFollowing.subList(0, Math.min(userBubblesFollowing.size(), 3)))
            .userBubblesFollowingTotalNumber(userBubblesFollowing.size())
            .userBubblesFollower(userBubblesFollower.subList(0, Math.min(userBubblesFollower.size(), 3)))
            .userBubblesFollowerTotalNumber(userBubblesFollower.size())
            .isMe(idUserOfUserPage.equals(idViewingUser))
            .isFollowedByMe(userIdsFollowedByGivenUser.contains(UUID.fromString(idUserOfUserPage)))
            .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(UUID.fromString(idUserOfUserPage)))
            .numberOfPendingFollowUserRequests(this.userRepository.getUserIdsFollowRequestSentToGivenUser_NotYetAccepted(idUserOfUserPage).size())
            .build();
    }

    public PersonalPageModel getPersonalPage(
        // String idUser
    ) throws Exception {
        // test:
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        Optional<UserEntity> userEntityOptional = this.userRepository.findById(UUID.fromString(idUser));
        if (!userEntityOptional.isPresent()) {
            throw new Exception("Error: invalid input");
        }
        UserEntity userEntity = userEntityOptional.get();
        String userImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userEntity.getIdImageKey());

        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeList = this.taskRepository.getNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal(idUser);
        String dateTodayAsString = LocalDate.now(userTimeZoneZoneId).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        int numberOfPointsTaskCompleteToday = 0;
        for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeList.size(); i++) {
            NumberOfPointsInTasksCompletedOverTimeVisualizationDTO numberOfPointsInTasksCompletedOverTimeEntry = numberOfPointsInTasksCompletedOverTimeList.get(i);
            Integer timestampOfTaskComplete = numberOfPointsInTasksCompletedOverTimeEntry.getTimestamp();
            String dateLabel = Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, timestampOfTaskComplete);
            if (dateLabel.equals(dateTodayAsString)) {
                numberOfPointsTaskCompleteToday += numberOfPointsInTasksCompletedOverTimeEntry.getNumberOfPoints();
            }
        }
        return PersonalPageModel.builder()
            .id(userEntity.getId())
            .username(userEntity.getUsername())
            .name(userEntity.getName())
            .imageLink(userImageLink)
            .numberOfPointsTaskCompleteToday(numberOfPointsTaskCompleteToday)
            .build();
    }

    public UserPageModel updateUserPage(
            String idUserViewing,
            JsonNode requestBody
    ) throws Exception {
        if (
            idUserViewing == null ||
            !requestBody.has(RequestBodyKeys.UPDATE_USER_ID)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String userIdOfUserPage = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.UPDATE_USER_ID));
        if (
            !RequestValidatorUser.id(userIdOfUserPage)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }

        Optional<UserEntity> userEntityOptional = this.userRepository.findById(UUID.fromString(userIdOfUserPage));
        if (!userEntityOptional.isPresent()) {
            throw new Exception("Error: invalid input");
        }
        UserEntity userEntity = userEntityOptional.get();

        if (requestBody.has(RequestBodyKeys.UPDATE_USER_USERNAME)) {
            String userUsername = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_USER_USERNAME));
            if (!RequestValidatorUser.username(userUsername)) {
                throw new Exception("Error: invalid input");
            }
            List<UserEntity> userEntitiesWithSameUsername = this.userRepository.findByUsernameLowerCase(userUsername);
            if (userEntitiesWithSameUsername.size() > 0 && !userEntitiesWithSameUsername.get(0).getId().toString().equals(userIdOfUserPage)) {
                throw new Exception("USER_DUPLICATE_USERNAME");
            }
            boolean isNoChange = (userUsername == null && userEntity.getUsername() == null) || 
                (userUsername != null && userUsername.equals(userEntity.getUsername())
            );
            if (!isNoChange) {
                userEntity.setUsername(userUsername);
                this.userRepository.save(userEntity);
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_USER_NAME)) {
            String userName = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_USER_NAME));
            if (!RequestValidatorUser.name(userName)) {
                throw new Exception("Error: invalid input");
            }
            boolean isNoChange = (userName == null && userEntity.getName() == null) || 
                (userName != null && userName.equals(userEntity.getName())
            );
            if (!isNoChange) {
                userEntity.setName(userName);
                this.userRepository.save(userEntity);
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_USER_BIO)) {
            String userBio = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_USER_BIO));
            if (!RequestValidatorUser.bio(userBio)) {
                throw new Exception("Error: invalid input");
            }
            boolean isNoChange = (userBio == null && userEntity.getBio() == null) || 
                (userBio != null && userBio.equals(userEntity.getBio())
            );
            if (!isNoChange) {
                userEntity.setBio(userBio);
                this.userRepository.save(userEntity);
            }
        }
        if (requestBody.has(RequestBodyKeys.UPDATE_USER_IMAGE)) {
            String userImageAsBase64String = RequestFormatterCommon.fString(requestBody.get(RequestBodyKeys.UPDATE_USER_IMAGE));
            if (!RequestValidatorUser.imageAsBase64String(userImageAsBase64String)) {
                throw new Exception("Error: invalid input");
            }
            boolean isNoChange = userImageAsBase64String == null && userEntity.getIdImageKey() == null;
            if (!isNoChange) {
                UUID userImageKeyId = UUID.randomUUID();
                byte[] imageAsByteArray = java.util.Base64.getDecoder().decode(userImageAsBase64String.substring(userImageAsBase64String.indexOf(",") + 1));
                RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
                Utilities.putObjectInS3(Constants.S3_FOLDER_NAME_USER_IMAGE, userImageKeyId, imageAsByteArrayRequestBody);
                userEntity.setIdImageKey(userImageKeyId);
                this.userRepository.save(userEntity);
            }
        }
        return this.getUserPage(userIdOfUserPage, idUserViewing);
    }

    public List<UserBubbleModel> getUserBubblesFollowing(String idUser, String idViewingUser) {
        Set<UUID> userIdsFollowedByViewingUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idViewingUser));
        Set<UUID> userIds_FollowRequestSentByViewingUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idViewingUser));

        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesFollowing = this.userRepository.getUserBubblesFollowing(idUser).stream().map(userBubbleDTOFollowing -> {
            String userBubbleImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userBubbleDTOFollowing.getIdImageKey());
            return UserBubbleModel.builder()
                .id(userBubbleDTOFollowing.getId())
                .name(userBubbleDTOFollowing.getName())
                .username(userBubbleDTOFollowing.getUsername())
                .imageLink(userBubbleImageLink)
                .timestampToSortBy(userBubbleDTOFollowing.getTimestampToSortBy())
                .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleDTOFollowing.getTimestampToSortBy()))
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleDTOFollowing.getTimestampToSortBy()))
                .isFollowedByMe(userIdsFollowedByViewingUser.contains(userBubbleDTOFollowing.getId()))
                .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByViewingUser_NotYetAccepted.contains(userBubbleDTOFollowing.getId()))
                .isMe(idViewingUser.equals(userBubbleDTOFollowing.getId().toString()))
                .build();
        }).collect(Collectors.toList());
        Collections.sort(userBubblesFollowing, new Comparator<UserBubbleModel>() {
            public int compare(UserBubbleModel userBubbleFollowing1, UserBubbleModel userBubbleFollowing2) {
                if (userBubbleFollowing1.isMe() == true && userBubbleFollowing2.isMe() == false) {
                    return -1;
                } else if (userBubbleFollowing1.isMe() == false && userBubbleFollowing2.isMe() == true) {
                    return 1;
                }
                if (userBubbleFollowing1.isFollowedByMe() == true && userBubbleFollowing2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubbleFollowing1.isFollowedByMe() == false && userBubbleFollowing2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleFollowing1.getUsername().compareTo(userBubbleFollowing2.getUsername());
            }
        });
        return userBubblesFollowing;
    }

    public List<UserBubbleModel> getUserBubblesFollower(String idUser, String idViewingUser) {
        Set<UUID> userIdsFollowedByViewingUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idViewingUser));
        Set<UUID> userIds_FollowRequestSentByViewingUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idViewingUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesFollower = this.userRepository.getUserBubblesFollower(idUser).stream().map(userBubbleDTOFollower -> {
            String userBubbleImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userBubbleDTOFollower.getIdImageKey());
            return UserBubbleModel.builder()
                .id(userBubbleDTOFollower.getId())
                .name(userBubbleDTOFollower.getName())
                .username(userBubbleDTOFollower.getUsername())
                .imageLink(userBubbleImageLink)
                .timestampToSortBy(userBubbleDTOFollower.getTimestampToSortBy())
                .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleDTOFollower.getTimestampToSortBy()))
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleDTOFollower.getTimestampToSortBy()))
                .isFollowedByMe(userIdsFollowedByViewingUser.contains(userBubbleDTOFollower.getId()))
                .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByViewingUser_NotYetAccepted.contains(userBubbleDTOFollower.getId()))
                .isMe(idViewingUser.equals(userBubbleDTOFollower.getId().toString()))
                .build();
        }).collect(Collectors.toList());
        Collections.sort(userBubblesFollower, new Comparator<UserBubbleModel>() {
            public int compare(UserBubbleModel userBubbleFollower1, UserBubbleModel userBubbleFollower2) {
                if (userBubbleFollower1.isMe() == true && userBubbleFollower2.isMe() == false) {
                    return -1;
                } else if (userBubbleFollower1.isMe() == false && userBubbleFollower2.isMe() == true) {
                    return 1;
                }
                if (userBubbleFollower1.isFollowedByMe() == true && userBubbleFollower2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubbleFollower1.isFollowedByMe() == false && userBubbleFollower2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleFollower1.getUsername().compareTo(userBubbleFollower2.getUsername());
            }
        });
        return userBubblesFollower;
    }

    public void sendFollowUserRequest(
        String idUserSendFollowRequest,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUserSendFollowRequest == null ||
            !requestBody.has(RequestBodyKeys.SEND_FOLLOW_USER_REQUEST_ID_USER_RECEIVE_FOLLOW_REQUEST)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idUserReceiveFollowRequest = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.SEND_FOLLOW_USER_REQUEST_ID_USER_RECEIVE_FOLLOW_REQUEST));
        if (
            !RequestValidatorUser.id(idUserReceiveFollowRequest)
        ) {
            throw new Exception("Error: invalid input");
        }
        boolean isUserReceiveFollowRequest_Public = this.userRepository.findById(UUID.fromString(idUserReceiveFollowRequest)).get().isPublic();
        if (isUserReceiveFollowRequest_Public) {
            UserUserUser1FollowUser2Entity userUserUser1FollowUser2Entity = new UserUserUser1FollowUser2Entity();
            userUserUser1FollowUser2Entity.setTimestampUnix((int) Instant.now().getEpochSecond());
            userUserUser1FollowUser2Entity.setIdUser1(UUID.fromString(idUserSendFollowRequest));
            userUserUser1FollowUser2Entity.setIdUser2(UUID.fromString(idUserReceiveFollowRequest));
            userUserUser1FollowUser2Entity.setRequestSent(true);
            userUserUser1FollowUser2Entity.setTimestampRequestSent((int) Instant.now().getEpochSecond());
            userUserUser1FollowUser2Entity.setFollowing(true);
            userUserUser1FollowUser2Entity.setTimestampOfFollowing((int) Instant.now().getEpochSecond());
            this.userUserUser1FollowUser2Repository.save(userUserUser1FollowUser2Entity);
        } else {
            UserUserUser1FollowUser2Entity userUserUser1FollowUser2Entity = new UserUserUser1FollowUser2Entity();
            userUserUser1FollowUser2Entity.setTimestampUnix((int) Instant.now().getEpochSecond());
            userUserUser1FollowUser2Entity.setIdUser1(UUID.fromString(idUserSendFollowRequest));
            userUserUser1FollowUser2Entity.setIdUser2(UUID.fromString(idUserReceiveFollowRequest));
            userUserUser1FollowUser2Entity.setRequestSent(true);
            userUserUser1FollowUser2Entity.setTimestampRequestSent((int) Instant.now().getEpochSecond());
            Utilities.generateNotification(
                this.notificationRepository,
                UUID.fromString(idUserReceiveFollowRequest),
                Constants.NOTIFICATION_TYPE_SENT_YOU_FOLLOW_REQUEST,
                String.format("@%s has sent you a follow request", this.userRepository.findById(UUID.fromString(idUserSendFollowRequest)).get().getUsername()),
                Constants.NOTIFICATION_LINK_PAGE_TYPE_USER,
                UUID.fromString(idUserSendFollowRequest)
            );
            this.userUserUser1FollowUser2Repository.save(userUserUser1FollowUser2Entity);
        }
    }

    public List<UserBubbleModel> acceptFollowUserRequests(
        String idUserAcceptFollowRequest,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUserAcceptFollowRequest == null ||
            !requestBody.has(RequestBodyKeys.ACCEPT_FOLLOW_USER_REQUESTS_ID_USERS_SEND_FOLLOW_REQUEST)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        List<String> idUsersWithFollowRequestAccepted = new ArrayList<>();
        for (int i = 0; i < requestBody.get(RequestBodyKeys.ACCEPT_FOLLOW_USER_REQUESTS_ID_USERS_SEND_FOLLOW_REQUEST).size(); i++) {
            String idUserWithFollowRequestAccepted = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.ACCEPT_FOLLOW_USER_REQUESTS_ID_USERS_SEND_FOLLOW_REQUEST).get(i));
            idUsersWithFollowRequestAccepted.add(idUserWithFollowRequestAccepted);
        }
        if (
            !RequestValidatorUser.idUsersWithFollowRequestAccepted(idUsersWithFollowRequestAccepted)
        ) {
            throw new Exception("Error: invalid input");
        }
        
        for (int i = 0; i < idUsersWithFollowRequestAccepted.size(); i++) {
            String idUserWithFollowRequestAccepted = idUsersWithFollowRequestAccepted.get(i);
            UserUserUser1FollowUser2Entity userUserUser1FollowUser2Entity = this.userUserUser1FollowUser2Repository.findByIdUser1IdUser2(idUserWithFollowRequestAccepted, idUserAcceptFollowRequest).get(0);
            userUserUser1FollowUser2Entity.setRequestAccepted(true);
            userUserUser1FollowUser2Entity.setTimestampRequestAccepted((int) Instant.now().getEpochSecond());
            userUserUser1FollowUser2Entity.setFollowing(true);
            userUserUser1FollowUser2Entity.setTimestampOfFollowing((int) Instant.now().getEpochSecond());
            this.userUserUser1FollowUser2Repository.save(userUserUser1FollowUser2Entity);
            Utilities.generateNotification(
                this.notificationRepository,
                UUID.fromString(idUserWithFollowRequestAccepted),
                Constants.NOTIFICATION_TYPE_ACCEPTED_YOUR_FOLLOW_REQUEST,
                String.format("@%s has accepted your follow request", this.userRepository.findById(UUID.fromString(idUserAcceptFollowRequest)).get().getUsername()),
                Constants.NOTIFICATION_LINK_PAGE_TYPE_USER,
                UUID.fromString(idUserAcceptFollowRequest)
            );
        }
        return this.getUserBubblesPendingFollowUserRequest(idUserAcceptFollowRequest);
    }

    public List<UserBubbleModel> declineFollowUserRequests(
        String idUserDeclineFollowRequest,
        JsonNode requestBody
    ) throws Exception {
        if (
            idUserDeclineFollowRequest == null ||
            !requestBody.has(RequestBodyKeys.DECLINE_FOLLOW_USER_REQUESTS_ID_USERS_SEND_FOLLOW_REQUEST)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        List<String> idUsersWithFollowRequestDeclined = new ArrayList<>();
        for (int i = 0; i < requestBody.get(RequestBodyKeys.DECLINE_FOLLOW_USER_REQUESTS_ID_USERS_SEND_FOLLOW_REQUEST).size(); i++) {
            String idUserWithFollowRequestDeclined = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.DECLINE_FOLLOW_USER_REQUESTS_ID_USERS_SEND_FOLLOW_REQUEST).get(i));
            idUsersWithFollowRequestDeclined.add(idUserWithFollowRequestDeclined);
        }
        if (
            !RequestValidatorUser.idUsersWithFollowRequestDeclined(idUsersWithFollowRequestDeclined)
        ) {
            throw new Exception("Error: invalid input");
        }
        for (int i = 0; i < idUsersWithFollowRequestDeclined.size(); i++) {
            String idUserWithFollowRequestDeclined = idUsersWithFollowRequestDeclined.get(i);
            UserUserUser1FollowUser2Entity userUserUser1FollowUser2Entity = this.userUserUser1FollowUser2Repository.findByIdUser1IdUser2(idUserWithFollowRequestDeclined, idUserDeclineFollowRequest).get(0);
            if (!userUserUser1FollowUser2Entity.isFollowing()) {
                this.userUserUser1FollowUser2Repository.delete(userUserUser1FollowUser2Entity);
            }
        }
        return this.getUserBubblesPendingFollowUserRequest(idUserDeclineFollowRequest);
    }

    public List<UserBubbleModel> getUserBubblesPendingFollowUserRequest(String idUser) {
        Set<UUID> userIdsFollowedByViewingUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByViewingUser_NotYetAccepted = new HashSet<>(this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesPendingFollowUserRequest = this.userRepository.getUserBubblesFollowUserRequestSentNotYetAccepted(idUser).stream().map(userBubbleDTO -> {
            String userBubbleImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, userBubbleDTO.getIdImageKey());
            return UserBubbleModel.builder()
                .id(userBubbleDTO.getId())
                .name(userBubbleDTO.getName())
                .username(userBubbleDTO.getUsername())
                .imageLink(userBubbleImageLink)
                .timestampToSortBy(userBubbleDTO.getTimestampToSortBy())
                .datetimeDateOnlyLabel(Utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, userBubbleDTO.getTimestampToSortBy()))
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, userBubbleDTO.getTimestampToSortBy()))
                .isFollowedByMe(userIdsFollowedByViewingUser.contains(userBubbleDTO.getId()))
                .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByViewingUser_NotYetAccepted.contains(userBubbleDTO.getId()))
                .isMe(idUser.equals(userBubbleDTO.getId().toString()))
                .build();
        }).collect(Collectors.toList());
        Collections.sort(userBubblesPendingFollowUserRequest, new Comparator<UserBubbleModel>() {
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
        return userBubblesPendingFollowUserRequest;
    }

    public int getNotificationsUnseenCount(String idUser) {
        return this.notificationRepository.findByIdUser(idUser).stream().filter(notificationEntity -> !notificationEntity.isSeen()).collect(Collectors.toList()).size();
    }

    public List<NotificationModel> getNotifications(String idUser) {
        ZoneId userTimeZoneZoneId = Utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        
        List<NotificationModel> notifications = this.notificationRepository.findByIdUser(idUser).stream().map(notificationEntity -> {
            String notificationImageLink = null;
            if (notificationEntity.getLinkPageType() != null && notificationEntity.getLinkPageType().equals(Constants.NOTIFICATION_LINK_PAGE_TYPE_POD)) {
                notificationImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_POD_IMAGE, this.podRepository.findById(notificationEntity.getIdLinkPage()).get().getIdImageKey());
            } else if (notificationEntity.getLinkPageType() != null && notificationEntity.getLinkPageType().equals(Constants.NOTIFICATION_LINK_PAGE_TYPE_USER)) {
                notificationImageLink = Utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE, this.userRepository.findById(notificationEntity.getIdLinkPage()).get().getIdImageKey());
            }
            boolean isMemberOfPod = false;
            if (notificationEntity.getLinkPageType() != null && notificationEntity.getLinkPageType().equals(Constants.NOTIFICATION_LINK_PAGE_TYPE_POD)) {
                Set<UUID> userIdsPodMember = new HashSet<>(this.userRepository.getUserBubblesPodMember(notificationEntity.getIdLinkPage().toString()).stream().map(userBubblePodMember -> {
                    return userBubblePodMember.getId();
                }).collect(Collectors.toList()));
                isMemberOfPod = userIdsPodMember.contains(notificationEntity.getIdUser());
            }
            boolean isFollowedByUserWhoSentFollowRequest = false;
            if (notificationEntity.getLinkPageType() != null && notificationEntity.getLinkPageType().equals(Constants.NOTIFICATION_LINK_PAGE_TYPE_USER)) {
                Set<UUID> userIdsMyFollowers = new HashSet<>(this.userRepository.getUserBubblesFollower(idUser).stream().map(userBubblePodMember -> {
                    return userBubblePodMember.getId();
                }).collect(Collectors.toList()));
                isFollowedByUserWhoSentFollowRequest = userIdsMyFollowers.contains(notificationEntity.getIdLinkPage());
            }
            return NotificationModel.builder()
                .id(notificationEntity.getId())
                .idUser(notificationEntity.getIdUser())
                .notificationType(notificationEntity.getNotificationType())
                .notificationMessage(notificationEntity.getNotificationMessage())
                .linkPageType(notificationEntity.getLinkPageType())
                .idLinkPage(notificationEntity.getIdLinkPage())
                .isSeen(notificationEntity.isSeen())
                .timestampToSortBy(notificationEntity.getTimestampUnix())
                .datetimeDateAndTimeLabel(Utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId, notificationEntity.getTimestampUnix()))
                .imageLink(notificationImageLink)
                .isDismissed(notificationEntity.isDismissed())
                .isMemberOfPod(isMemberOfPod)
                .isFollowedByUserWhoSentFollowRequest(isFollowedByUserWhoSentFollowRequest)
                .build();
        }).collect(Collectors.toList());
        Collections.sort(notifications, new Comparator<NotificationModel>() {
            public int compare(NotificationModel notificationModel1, NotificationModel notificationModel2) {
                if (notificationModel1.getTimestampToSortBy() > notificationModel2.getTimestampToSortBy()) {
                    return 1;
                } else if (notificationModel1.getTimestampToSortBy() < notificationModel2.getTimestampToSortBy()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return notifications;
    }

    public void markAllNotificationsAsSeen(String idUser) {
        List<NotificationEntity> notificationEntityList = this.notificationRepository.findByIdUser(idUser);
        for (int i = 0; i < notificationEntityList.size(); i++) {
            NotificationEntity notificationEntity = notificationEntityList.get(i);
            notificationEntity.setSeen(true);
            this.notificationRepository.save(notificationEntity);
        }
    }

    public void dismissNotification(
            String idUser,
            JsonNode requestBody
    ) throws Exception {
        if (
            idUser == null ||
            !requestBody.has(RequestBodyKeys.DISMISS_NOTIFICATION_ID_NOTIFICATION)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        String idNotification = RequestFormatterCommon.fUUID(requestBody.get(RequestBodyKeys.DISMISS_NOTIFICATION_ID_NOTIFICATION));
        if (
            !RequestValidatorUser.id(idUser) ||
            !RequestValidatorUser.idNotification(idNotification)
        ) {
            throw new Exception("Error: invalid input"); // TODO create an exception class
        }
        NotificationEntity notificationEntity = this.notificationRepository.findById(UUID.fromString(idNotification)).get();
        notificationEntity.setDismissed(true);
        this.notificationRepository.save(notificationEntity);
    }
}

