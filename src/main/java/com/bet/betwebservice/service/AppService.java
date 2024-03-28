package com.bet.betwebservice.service;

import com.bet.betwebservice.authentication.JwtTokenWrapper;
import com.bet.betwebservice.common.*;
import com.bet.betwebservice.dto.*;
import com.bet.betwebservice.entity.*;
import com.bet.betwebservice.model.*;
import com.bet.betwebservice.dao.*;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class AppService {
    private AppServiceRepositoryMiddleLayer appServiceRepositoryMiddleLayer;
    private AuthorizationService authorizationService;
    private AuthenticationManager authenticationManager;
    private PasswordEncoder passwordEncoder;

    // TODO: remove and rely on MiddleLayer instead
    private NotificationRepository notificationRepository;
    private PodRepository podRepository;
    private PodUserPodHasUserRepository podUserPodHasUserRepository;
    private StampRepository stampRepository;
    private StampTaskStampHasTaskRepository stampTaskStampHasTaskRepository;
    private StampUserUserCollectStampRepository stampUserUserCollectStampRepository;
    private TaskRepository taskRepository;
    private TaskUserTaskCommentReactionRepository taskUserTaskCommentReactionRepository;
    private TaskUserTaskCommentReplyReactionRepository taskUserTaskCommentReplyReactionRepository;
    private TaskUserTaskCommentReplyRepository taskUserTaskCommentReplyRepository;
    private TaskUserTaskCommentRepository taskUserTaskCommentRepository;
    private TaskUserTaskCompleteRepository taskUserTaskCompleteRepository;
    private TaskUserTaskNoteRepository taskUserTaskNoteRepository;
    private TaskUserTaskPinRepository taskUserTaskPinRepository;
    private TaskUserTaskReactionRepository taskUserTaskReactionRepository;
    private TaskUserTaskStarRepository taskUserTaskStarRepository;
    private UserRepository userRepository;
    private UserUserUser1FollowUser2Repository userUserUser1FollowUser2Repository;
    private Utilities utilities;

    @Autowired
    private JwtTokenWrapper jwtTokenWrapper;

    public AppService(
            AppServiceRepositoryMiddleLayer appServiceRepositoryMiddleLayer,
            AuthorizationService authorizationService,
            AuthenticationManager authenticationManager,
            PasswordEncoder passwordEncoder,

            // TODO: remove and rely on MiddleLayer instead
            NotificationRepository notificationRepository,
            PodRepository podRepository,
            PodUserPodHasUserRepository podUserPodHasUserRepository,
            StampRepository stampRepository,
            StampTaskStampHasTaskRepository stampTaskStampHasTaskRepository,
            StampUserUserCollectStampRepository stampUserUserCollectStampRepository,
            TaskRepository taskRepository,
            TaskUserTaskCommentReactionRepository taskUserTaskCommentReactionRepository,
            TaskUserTaskCommentReplyReactionRepository taskUserTaskCommentReplyReactionRepository,
            TaskUserTaskCommentReplyRepository taskUserTaskCommentReplyRepository,
            TaskUserTaskCommentRepository taskUserTaskCommentRepository,
            TaskUserTaskCompleteRepository taskUserTaskCompleteRepository,
            TaskUserTaskNoteRepository taskUserTaskNoteRepository,
            TaskUserTaskPinRepository taskUserTaskPinRepository,
            TaskUserTaskReactionRepository taskUserTaskReactionRepository,
            TaskUserTaskStarRepository taskUserTaskStarRepository,
            UserRepository userRepository,
            UserUserUser1FollowUser2Repository userUserUser1FollowUser2Repository,
            Utilities utilities
            ) {
        this.appServiceRepositoryMiddleLayer = appServiceRepositoryMiddleLayer;
        this.authorizationService = authorizationService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        // TODO: remove and rely on MiddleLayer instead
        this.notificationRepository = notificationRepository;
        this.podRepository = podRepository;
        this.podUserPodHasUserRepository = podUserPodHasUserRepository;
        this.stampRepository = stampRepository;
        this.stampTaskStampHasTaskRepository = stampTaskStampHasTaskRepository;
        this.stampUserUserCollectStampRepository = stampUserUserCollectStampRepository;
        this.taskRepository = taskRepository;
        this.taskUserTaskCommentReactionRepository = taskUserTaskCommentReactionRepository;
        this.taskUserTaskCommentReplyReactionRepository = taskUserTaskCommentReplyReactionRepository;
        this.taskUserTaskCommentReplyRepository = taskUserTaskCommentReplyRepository;
        this.taskUserTaskCommentRepository = taskUserTaskCommentRepository;
        this.taskUserTaskCompleteRepository = taskUserTaskCompleteRepository;
        this.taskUserTaskNoteRepository = taskUserTaskNoteRepository;
        this.taskUserTaskPinRepository = taskUserTaskPinRepository;
        this.taskUserTaskReactionRepository = taskUserTaskReactionRepository;
        this.taskUserTaskStarRepository = taskUserTaskStarRepository;
        this.userRepository = userRepository;
        this.userUserUser1FollowUser2Repository = userUserUser1FollowUser2Repository;
        this.utilities = utilities;
    }

    /**
     * id
     */
    public PodPageModel getPodPage(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetPodPage(idUser, idPod);
        return this._getPodPage(idUser, idPod);
    }

    public PodPageModel _getPodPage(
            String idUser,
            String idPod) throws Exception {
        PodEntity podEntity = this.podRepository.findById(UUID.fromString(idPod)).get();
        String podImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_POD_IMAGE, podEntity.getIdImageKey());

        List<UserBubbleModel> userBubblesPodMember = this._getUserBubblesPodMember(idUser, idPod);
        List<UserBubbleModel> userBubblesPodModerator = this._getUserBubblesPodModerator(idUser, idPod);

        Set<UUID> userIdsPodMember = new HashSet<>(userBubblesPodMember.stream().map(userBubblePodMember -> {
            return userBubblePodMember.getId();
        }).collect(Collectors.toList()));
        Set<UUID> userIdsPodModerator = new HashSet<>(userBubblesPodModerator.stream().map(userBubblePodModerator -> {
            return userBubblePodModerator.getId();
        }).collect(Collectors.toList()));

        Set<UUID> userIdsBecomePodModeratorRequestSent = new HashSet<>(
                this.podRepository.getUserIdsBecomePodModeratorRequestSent(idPod));
        boolean isReachedNumberOfTasksLimit = this.taskRepository.getTasksByIdPod(idPod).size() >= Limits.LIMIT_NUMBER_OF_TOTAL_TASKS_POD;
        return PodPageModel.builder()
                .id(podEntity.getId())
                .name(podEntity.getName())
                .description(podEntity.getDescription())
                .imageLink(podImageLink)
                .userBubblesPodMember(userBubblesPodMember.subList(0, Math.min(userBubblesPodMember.size(), 3)))
                .userBubblesPodMemberTotalNumber(userBubblesPodMember.size())
                .userBubblesPodModerator(
                        userBubblesPodModerator.subList(0, Math.min(userBubblesPodModerator.size(), 3)))
                .userBubblesPodModeratorTotalNumber(userBubblesPodModerator.size())
                .isPublic(podEntity.isPublic())
                .isPodMember(userIdsPodMember.contains(UUID.fromString(idUser)))
                .isPodModerator(userIdsPodModerator.contains(UUID.fromString(idUser)))
                .isReachedNumberOfTasksLimit(isReachedNumberOfTasksLimit)
                .numberOfPendingBecomeModeratorRequests(
                        this.podRepository.getUserIdsBecomePodModeratorRequestSentNotYetApproved(idPod).size())
                .isSentBecomePodModeratorRequest(userIdsBecomePodModeratorRequestSent.contains(UUID.fromString(idUser)))
                .build();
    }

    /**
     * id
     */
    public List<UserBubbleModel> getUserBubblesPodMember(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetUserBubblesPodMember(idUser, idPod);
        return this._getUserBubblesPodMember(idUser, idPod);
    }

    public List<UserBubbleModel> _getUserBubblesPodMember(String idUser, String idPod) throws Exception {
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesPodMember = this.userRepository.getUserBubblesPodMember(idPod).stream()
                .map(userBubbleDTOPodMember -> {
                    String userBubbleImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                            userBubbleDTOPodMember.getIdImageKey());
                    return UserBubbleModel.builder()
                            .id(userBubbleDTOPodMember.getId())
                            .name(userBubbleDTOPodMember.getName())
                            .username(userBubbleDTOPodMember.getUsername())
                            .imageLink(userBubbleImageLink)
                            .timestampToSortBy(userBubbleDTOPodMember.getTimestampToSortBy())
                            .datetimeDateOnlyLabel(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTOPodMember.getTimestampToSortBy()))
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTOPodMember.getTimestampToSortBy()))
                            .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTOPodMember.getId()))
                            .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted
                                    .contains(userBubbleDTOPodMember.getId()))
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
                } else if (userBubblePodMember1.isFollowedByMe() == false
                        && userBubblePodMember2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubblePodMember1.getUsername().compareTo(userBubblePodMember2.getUsername());
            }
        });
        return userBubblesPodMember;
    }

    /**
     * id
     */
    public List<UserBubbleModel> getUserBubblesPodModerator(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetUserBubblesPodModerator(idUser, idPod);
        return this._getUserBubblesPodModerator(idUser, idPod);
    }

    public List<UserBubbleModel> _getUserBubblesPodModerator(String idUser, String idPod) throws Exception {
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesPodModerator = this.userRepository.getUserBubblesPodModerator(idPod).stream()
                .map(userBubbleDTOPodModerator -> {
                    String userBubbleImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                            userBubbleDTOPodModerator.getIdImageKey());
                    return UserBubbleModel.builder()
                            .id(userBubbleDTOPodModerator.getId())
                            .name(userBubbleDTOPodModerator.getName())
                            .username(userBubbleDTOPodModerator.getUsername())
                            .imageLink(userBubbleImageLink)
                            .timestampToSortBy(userBubbleDTOPodModerator.getTimestampToSortBy())
                            .datetimeDateOnlyLabel(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTOPodModerator.getTimestampToSortBy()))
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTOPodModerator.getTimestampToSortBy()))
                            .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTOPodModerator.getId()))
                            .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted
                                    .contains(userBubbleDTOPodModerator.getId()))
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
                if (userBubblePodModerator1.isFollowedByMe() == true
                        && userBubblePodModerator2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubblePodModerator1.isFollowedByMe() == false
                        && userBubblePodModerator2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubblePodModerator1.getUsername().compareTo(userBubblePodModerator2.getUsername());
            }
        });
        return userBubblesPodModerator;
    }

    /**
     * filterByName
     * filterIsPublic
     * filterIsNotPublic
     * filterIsMember
     * filterIsNotMember
     * filterIsModerator
     * filterIsNotModerator
     * paginationIdxStart
     * paginationN
     */
    public PodCardsPaginatedModel getPodCardsDiscover(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetPodCardsDiscover(idUser);
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "filterByName",
                        "filterIsPublic",
                        "filterIsNotPublic",
                        "filterIsMember",
                        "filterIsNotMember",
                        "filterIsModerator",
                        "filterIsNotModerator",
                        "paginationIdxStart",
                        "paginationN"
                ));
        String filterByName = RequestBodyFormatter.fStringLowercase(rb.get("filterByName"));
        boolean filterIsPublic = RequestBodyFormatter.fBoolean(rb.get("filterIsPublic"));
        boolean filterIsNotPublic = RequestBodyFormatter.fBoolean(rb.get("filterIsNotPublic"));
        boolean filterIsMember = RequestBodyFormatter.fBoolean(rb.get("filterIsMember"));
        boolean filterIsNotMember = RequestBodyFormatter.fBoolean(rb.get("filterIsNotMember"));
        boolean filterIsModerator = RequestBodyFormatter.fBoolean(rb.get("filterIsModerator"));
        boolean filterIsNotModerator = RequestBodyFormatter.fBoolean(rb.get("filterIsNotModerator"));
        int paginationIdxStart = RequestBodyFormatter.fInt(rb.get("paginationIdxStart"));
        int paginationN = RequestBodyFormatter.fInt(rb.get("paginationN"));

        List<PodCardSharedPropertiesDTO> podCardsDiscover_SharedProperties = this.podRepository
                .getPodCardsDiscover_SharedProperties(filterByName, filterIsPublic, filterIsNotPublic);
        List<PodCardIndividualPropertiesDTO> podCardsDiscover_IndividualProperties = this.podRepository
                .getPodCardsDiscover_IndividualProperties(idUser, filterByName, filterIsPublic,
                        filterIsNotPublic);
        PodCardsPaginatedModel podCardsPaginatedModel = this._getPodCardsPaginatedModelFromSharedAndIndividualProperties(
                idUser,
                podCardsDiscover_SharedProperties,
                podCardsDiscover_IndividualProperties,
                filterIsPublic,
                filterIsNotPublic,
                filterIsMember,
                filterIsNotMember,
                filterIsModerator,
                filterIsNotModerator,
                paginationIdxStart,
                paginationN
                );
        return podCardsPaginatedModel;
    }

    /**
     * id
     * filterByName
     * filterIsPublic
     * filterIsNotPublic
     * filterIsMember
     * filterIsNotMember
     * filterIsModerator
     * filterIsNotModerator
     * paginationIdxStart
     * paginationN
     */
    public PodCardsPaginatedModel getPodCardsAssociatedWithStamp(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id",
                        "filterByName",
                        "filterIsPublic",
                        "filterIsNotPublic",
                        "filterIsMember",
                        "filterIsNotMember",
                        "filterIsModerator",
                        "filterIsNotModerator",
                        "paginationIdxStart",
                        "paginationN"
                        ));
        String idStamp = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetPodCardsAssociatedWithStamp(idUser, idStamp);
        String filterByName = RequestBodyFormatter.fStringLowercase(rb.get("filterByName"));
        boolean filterIsPublic = RequestBodyFormatter.fBoolean(rb.get("filterIsPublic"));
        boolean filterIsNotPublic = RequestBodyFormatter.fBoolean(rb.get("filterIsNotPublic"));
        boolean filterIsMember = RequestBodyFormatter.fBoolean(rb.get("filterIsMember"));
        boolean filterIsNotMember = RequestBodyFormatter.fBoolean(rb.get("filterIsNotMember"));
        boolean filterIsModerator = RequestBodyFormatter.fBoolean(rb.get("filterIsModerator"));
        boolean filterIsNotModerator = RequestBodyFormatter.fBoolean(rb.get("filterIsNotModerator"));
        int paginationIdxStart = RequestBodyFormatter.fInt(rb.get("paginationIdxStart"));
        int paginationN = RequestBodyFormatter.fInt(rb.get("paginationN"));

        List<PodCardSharedPropertiesDTO> podCardsAssociatedWithStamp_SharedProperties = this.podRepository
                .getPodCardsAssociatedWithStamp_SharedProperties(idStamp, filterByName, filterIsPublic,
                        filterIsNotPublic);
        List<PodCardIndividualPropertiesDTO> podCardsAssociatedWithStamp_IndividualProperties = this.podRepository
                .getPodCardsAssociatedWithStamp_IndividualProperties(idStamp, idUser, filterByName,
                        filterIsPublic, filterIsNotPublic);
        PodCardsPaginatedModel podCardsPaginatedModel = this._getPodCardsPaginatedModelFromSharedAndIndividualProperties(
                idUser,
                podCardsAssociatedWithStamp_SharedProperties,
                podCardsAssociatedWithStamp_IndividualProperties,
                filterIsPublic,
                filterIsNotPublic,
                filterIsMember,
                filterIsNotMember,
                filterIsModerator,
                filterIsNotModerator,
                paginationIdxStart,
                paginationN
                );
        return podCardsPaginatedModel;
    }

    /**
     * id
     * filterByName
     * filterIsPublic
     * filterIsNotPublic
     * filterIsMember
     * filterIsNotMember
     * filterIsModerator
     * filterIsNotModerator
     * GetPodCardsAssociatedWithUser
     * GetPodCardsAssociatedWithUser
     */
    public PodCardsPaginatedModel getPodCardsAssociatedWithUser(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetPodCardsAssociatedWithUser(idUser);
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id",
                        "filterByName",
                        "filterIsPublic",
                        "filterIsNotPublic",
                        "filterIsMember",
                        "filterIsNotMember",
                        "filterIsModerator",
                        "filterIsNotModerator",
                        "paginationIdxStart",
                        "paginationN"));
        String idUserProfile = RequestBodyFormatter.fString(rb.get("id"));
        String filterByName = RequestBodyFormatter.fStringLowercase(rb.get("filterByName"));
        boolean filterIsPublic = RequestBodyFormatter.fBoolean(rb.get("filterIsPublic"));
        boolean filterIsNotPublic = RequestBodyFormatter.fBoolean(rb.get("filterIsNotPublic"));
        boolean filterIsMember = RequestBodyFormatter.fBoolean(rb.get("filterIsMember"));
        boolean filterIsNotMember = RequestBodyFormatter.fBoolean(rb.get("filterIsNotMember"));
        boolean filterIsModerator = RequestBodyFormatter.fBoolean(rb.get("filterIsModerator"));
        boolean filterIsNotModerator = RequestBodyFormatter.fBoolean(rb.get("filterIsNotModerator"));
        int paginationIdxStart = RequestBodyFormatter.fInt(rb.get("paginationIdxStart"));
        int paginationN = RequestBodyFormatter.fInt(rb.get("paginationN"));

        List<PodCardSharedPropertiesDTO> podCardsAssociatedWithUser_SharedProperties = this.podRepository
                .getPodCardsAssociatedWithUser_SharedProperties(filterByName, filterIsPublic,
                        filterIsNotPublic);
        List<PodCardIndividualPropertiesDTO> podCardsAssociatedWithUser_IndividualProperties = this.podRepository
                .getPodCardsAssociatedWithUser_IndividualProperties(idUser, filterByName, filterIsPublic,
                        filterIsNotPublic);

        // need to filter for pods that user actually belongs to AFTER query above
        // because calculation numberOfMembers requires this
        Set<UUID> podIdsProfileUserIsMemberOf = new HashSet<>(
                this.podRepository.getPodIdsUserIsMemberOf(idUserProfile));
        podCardsAssociatedWithUser_SharedProperties = podCardsAssociatedWithUser_SharedProperties.stream()
                .filter(podCard -> {
                    return podIdsProfileUserIsMemberOf.contains(podCard.getId());
                }).collect(Collectors.toList());
        podCardsAssociatedWithUser_IndividualProperties = podCardsAssociatedWithUser_IndividualProperties.stream()
                .filter(podCard -> {
                    return podIdsProfileUserIsMemberOf.contains(podCard.getId());
                }).collect(Collectors.toList());

        PodCardsPaginatedModel podCardsPaginatedModel = this._getPodCardsPaginatedModelFromSharedAndIndividualProperties(
                idUser,
                podCardsAssociatedWithUser_SharedProperties,
                podCardsAssociatedWithUser_IndividualProperties,
                filterIsPublic,
                filterIsNotPublic,
                filterIsMember,
                filterIsNotMember,
                filterIsModerator,
                filterIsNotModerator,
                paginationIdxStart,
                paginationN);
        return podCardsPaginatedModel;
    }

    /**
     * name
     * isPublic
     * ?description
     */
    public PodPageModel createPod(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeCreatePod(idUser);
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "name",
                        "isPublic"));
        String name = RequestBodyValidator.stringRequired(RequestBodyFormatter.fString(rb.get("name")),
                Limits.POD_NAME_MIN_LENGTH_CHARACTERS, Limits.POD_NAME_MAX_LENGTH_CHARACTERS);
        boolean isPublic = RequestBodyFormatter.fBoolean(rb.get("isPublic"));

        if (this.podRepository.findByNameLowerCase(name).size() > 0) {
            throw new Exception("POD_DUPLICATE_NAME");
        }

        // save processed input
        PodEntity podEntity = new PodEntity();
        podEntity.setIdUserCreate(UUID.fromString(idUser));
        podEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
        podEntity.setName(name);
        podEntity.setPublic(isPublic);
        if (rb.has("description")) {
            String description = RequestBodyValidator.stringOptional(
                    RequestBodyFormatter.fString(rb.get("description")), Limits.POD_DESCRIPTION_MIN_LENGTH_CHARACTERS,
                    Limits.POD_DESCRIPTION_MAX_LENGTH_CHARACTERS);
            podEntity.setDescription(description);
        }
        this.podRepository.save(podEntity);

        utilities.ensureNoEntityExists(this.podUserPodHasUserRepository.findByIdPodIdUser(podEntity.getId().toString(), idUser).size());
        PodUserPodHasUserEntity podUserPodHasUserEntity = new PodUserPodHasUserEntity();
        podUserPodHasUserEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
        podUserPodHasUserEntity.setIdPod(podEntity.getId());
        podUserPodHasUserEntity.setIdUser(UUID.fromString(idUser));
        podUserPodHasUserEntity.setMember(true);
        podUserPodHasUserEntity.setTimestampBecomeMember((int) Instant.now().getEpochSecond());
        podUserPodHasUserEntity.setModerator(true);
        podUserPodHasUserEntity.setTimestampBecomeModerator((int) Instant.now().getEpochSecond());
        this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);

        return this._getPodPage(idUser, podEntity.getId().toString());
    }

    /**
     * id
     * ?name
     * ?description
     * ?imageAsBase64String
     */
    public PodPageModel updatePod(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeUpdatePod(idUser, idPod);
        PodEntity podEntity = this.podRepository.findById(UUID.fromString(idPod)).get();
        if (rb.has("name")) {
            String podName = RequestBodyValidator.stringRequired(RequestBodyFormatter.fString(rb.get("name")),
                    Limits.POD_NAME_MIN_LENGTH_CHARACTERS, Limits.POD_NAME_MAX_LENGTH_CHARACTERS);
            List<PodEntity> podEntitiesWithSameName = this.podRepository.findByNameLowerCase(podName);
            if (podEntitiesWithSameName.size() > 0
                    && !podEntitiesWithSameName.get(0).getId().toString().equals(idPod)) {
                throw new Exception("POD_DUPLICATE_NAME");
            }
            boolean isNoop = (podName == null && podEntity.getName() == null) ||
                    (podName != null && podName.equals(podEntity.getName()));
            if (!isNoop) {
                podEntity.setName(podName);
                this.podRepository.save(podEntity);
            }
        }
        if (rb.has("description")) {
            String podDescription = RequestBodyValidator.stringOptional(
                    RequestBodyFormatter.fString(rb.get("description")), Limits.POD_DESCRIPTION_MIN_LENGTH_CHARACTERS,
                    Limits.POD_DESCRIPTION_MAX_LENGTH_CHARACTERS);
            boolean isNoop = (podDescription == null && podEntity.getDescription() == null) ||
                    (podDescription != null && podDescription.equals(podEntity.getDescription()));
            if (!isNoop) {
                podEntity.setDescription(podDescription);
                this.podRepository.save(podEntity);
            }
        }
        if (rb.has("imageAsBase64String")) {
            String podImageAsBase64String = RequestBodyValidator
                    .imageOptional(RequestBodyFormatter.fString(rb.get("imageAsBase64String")));
            boolean isNoop = podImageAsBase64String == null && podEntity.getIdImageKey() == null;
            if (!isNoop) {
                if (podImageAsBase64String == null) {
                    podEntity.setIdImageKey(null);
                    this.podRepository.save(podEntity);
                } else {
                    UUID podImageKeyId = UUID.randomUUID();
                    byte[] imageAsByteArray = java.util.Base64.getDecoder()
                            .decode(podImageAsBase64String.substring(podImageAsBase64String.indexOf(",") + 1));
                    RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
                    utilities.putObjectInS3(Constants.S3_FOLDER_NAME_POD_IMAGE, podImageKeyId,
                            imageAsByteArrayRequestBody);
                    podEntity.setIdImageKey(podImageKeyId);
                    this.podRepository.save(podEntity);
                }
            }
        }
        return this._getPodPage(idUser, idPod);
    }

    /**
     * id
     */
    public void joinPod(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeJoinPod(idUser, idPod);
        List<PodUserPodHasUserEntity> podUserPodHasUserEntityList = this.podUserPodHasUserRepository
                .findByIdPodIdUser(idPod, idUser);
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
            utilities.ensureNoEntityExists(this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser).size());
            PodUserPodHasUserEntity podUserPodHasUserEntity = new PodUserPodHasUserEntity();
            podUserPodHasUserEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            podUserPodHasUserEntity.setIdPod(UUID.fromString(idPod));
            podUserPodHasUserEntity.setIdUser(UUID.fromString(idUser));
            podUserPodHasUserEntity.setMember(true);
            podUserPodHasUserEntity.setTimestampBecomeMember((int) Instant.now().getEpochSecond());
            if (isTheOnlyMemberOfPod) {
                podUserPodHasUserEntity.setModerator(true);
                podUserPodHasUserEntity.setTimestampBecomeModerator((int) Instant.now().getEpochSecond());
            }
            this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
        }
    }

    /**
     * id
     */
    public void leavePod(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeLeavePod(idUser, idPod);

        boolean isUserLastModeratorOfPod = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser).get(0).isModerator() && 
            this.podUserPodHasUserRepository.findModerators(idPod).size() == 1;
        if (isUserLastModeratorOfPod) {
            throw new Exception("POD_REQUIRES_AT_LEAST_1_MODERATOR");
        }
        PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository
                .findByIdPodIdUser(idPod, idUser).get(0);
        this.podUserPodHasUserRepository.delete(podUserPodHasUserEntity);
    }

    /**
     * id
     * idUsers
     */
    public List<UserBubbleModel> sendJoinPodInvite(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id",
                        "idUsers"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeSendJoinPodInvite(idUser, idPod);

        List<String> idUsersReceiveJoinPodInvite = new ArrayList<>();
        for (int i = 0; i < rb.get("idUsers").size(); i++) {
            String idUserBecomeModerator = RequestBodyFormatter.fString(rb.get("idUsers").get(i));
            idUsersReceiveJoinPodInvite.add(idUserBecomeModerator);
        }
        // always create new entry because idUserReceiveInvite is not a member yet
        boolean isIdUserSendInviteIsMember = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser).get(0)
                .isMember();
        if (!isIdUserSendInviteIsMember) {
            throw new Exception("USER_NOT_AUTHORIZED_TO_SEND_INVITE");
        }
        for (int i = 0; i < idUsersReceiveJoinPodInvite.size(); i++) {
            String idUserReceiveInvite = idUsersReceiveJoinPodInvite.get(i);
            /**
             * guaranteed new entry, because one can only send join invite to non-members
             * (who haven't yet received an invite yet), and non-members don't have an entry yet
             */
            utilities.ensureNoEntityExists(this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUserReceiveInvite).size());
            PodUserPodHasUserEntity podUserPodHasUserEntity = new PodUserPodHasUserEntity();
            podUserPodHasUserEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            podUserPodHasUserEntity.setIdPod(UUID.fromString(idPod));
            podUserPodHasUserEntity.setIdUser(UUID.fromString(idUserReceiveInvite));
            podUserPodHasUserEntity.setJoinPodInviteSent(true);
            podUserPodHasUserEntity.setIdUserJoinPodInviteSender(UUID.fromString(idUser));
            podUserPodHasUserEntity.setTimestampJoinPodInviteSent((int) Instant.now().getEpochSecond());
            this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
            utilities.generateNotification(
                    this.notificationRepository,
                    UUID.fromString(idUserReceiveInvite),
                    Constants.NOTIFICATION_TYPE_SENT_YOU_JOIN_POD_INVITE,
                    String.format("@%s has invited you to join Pod: %s",
                            this.userRepository.findById(UUID.fromString(idUser)).get().getUsername(),
                            this.podRepository.findById(UUID.fromString(idPod)).get().getName()),
                    Constants.NOTIFICATION_LINK_PAGE_TYPE_POD,
                    UUID.fromString(idPod));
        }

        return this._getUserBubblesInviteJoinPod(idUser, idPod);
    }

    /**
     * id
     */
    public void acceptJoinPodInvite(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeAcceptJoinPodInvite(idUser, idPod);
        PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository
                .findByIdPodIdUser(idPod, idUser).get(0);
        podUserPodHasUserEntity.setJoinPodInviteAccepted(true);
        podUserPodHasUserEntity.setMember(true);
        podUserPodHasUserEntity.setTimestampBecomeMember((int) Instant.now().getEpochSecond());
        this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
    }

    /**
     * id
     */
    public void declineJoinPodInvite(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeDeclineJoinPodInvite(idUser, idPod);
        PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository
                .findByIdPodIdUser(idPod, idUser).get(0);
        this.podUserPodHasUserRepository.delete(podUserPodHasUserEntity);
    }

    /**
     * id
     */
    public void sendBecomePodModeratorRequest(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeSendBecomePodModeratorRequest(idUser, idPod);

        PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository
                .findByIdPodIdUser(idPod, idUser).get(0);
        if (!podUserPodHasUserEntity.isMember()) {
            throw new Exception("USER_NOT_AUTHORIZED_TO_SEND_REQUEST");
        }
        podUserPodHasUserEntity.setBecomePodModeratorRequestSent(true);
        podUserPodHasUserEntity.setTimestampBecomePodModeratorRequestSent((int) Instant.now().getEpochSecond());
        this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
    }

    /**
     * id
     * idUsers
     */
    public List<UserBubbleModel> approveBecomePodModeratorRequests(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id",
                        "idUsers"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeApproveBecomePodModeratorRequests(idUser, idPod);
        List<String> idUsersSentBecomePodModeratorRequest = new ArrayList<>();
        for (int i = 0; i < rb.get("idUsers").size(); i++) {
            String idUserSentBecomePodModeratorRequest = RequestBodyFormatter.fString(rb.get("idUsers").get(i));
            idUsersSentBecomePodModeratorRequest.add(idUserSentBecomePodModeratorRequest);
        }
        boolean isIdUserApproveRequestModerator = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser)
                .size() > 0 &&
                this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser).get(0).isModerator();
        if (!isIdUserApproveRequestModerator) {
            throw new Exception("USER_NOT_AUTHORIZED_TO_APPROVE_REQUEST");
        }
        for (int i = 0; i < idUsersSentBecomePodModeratorRequest.size(); i++) {
            String idUserSentBecomePodModeratorRequest = idUsersSentBecomePodModeratorRequest.get(i);
            PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository
                    .findByIdPodIdUser(idPod, idUserSentBecomePodModeratorRequest).get(0);
            podUserPodHasUserEntity.setModerator(true);
            podUserPodHasUserEntity.setTimestampBecomeModerator((int) Instant.now().getEpochSecond());
            podUserPodHasUserEntity.setBecomePodModeratorRequestApproved(true);
            podUserPodHasUserEntity.setIdUserBecomePodModeratorRequestApprover(UUID.fromString(idUser));
            this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
            utilities.generateNotification(
                    this.notificationRepository,
                    UUID.fromString(idUserSentBecomePodModeratorRequest),
                    Constants.NOTIFICATION_TYPE_APPROVED_YOUR_BECOME_POD_MODERATOR_REQUEST,
                    String.format("@%s has approved your request to become moderator of Pod: %s",
                            this.userRepository.findById(UUID.fromString(idUser)).get().getUsername(),
                            this.podRepository.findById(UUID.fromString(idPod)).get().getName()),
                    Constants.NOTIFICATION_LINK_PAGE_TYPE_POD,
                    UUID.fromString(idPod));
        }
        return this._getUserBubblesPendingBecomePodModeratorRequest(idUser, idPod);
    }

    /**
     * id
     * idUsers
     */
    public List<UserBubbleModel> rejectBecomePodModeratorRequests(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id",
                        "idUsers"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeRejectBecomePodModeratorRequests(idUser, idPod);
        List<String> idUsersSentBecomePodModeratorRequest = new ArrayList<>();
        for (int i = 0; i < rb.get("idUsers").size(); i++) {
            String idUserSentBecomePodModeratorRequest = RequestBodyFormatter.fString(rb.get("idUsers").get(i));
            idUsersSentBecomePodModeratorRequest.add(idUserSentBecomePodModeratorRequest);
        }

        boolean isIdUserRejectRequestModerator = this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser)
                .size() > 0 &&
                this.podUserPodHasUserRepository.findByIdPodIdUser(idPod, idUser).get(0).isModerator();
        if (!isIdUserRejectRequestModerator) {
            throw new Exception("USER_NOT_AUTHORIZED_TO_REJECT_REQUEST");
        }
        for (int i = 0; i < idUsersSentBecomePodModeratorRequest.size(); i++) {
            String idUserSentBecomePodModeratorRequest = idUsersSentBecomePodModeratorRequest.get(i);
            PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository
                    .findByIdPodIdUser(idPod, idUserSentBecomePodModeratorRequest).get(0);
            podUserPodHasUserEntity.setBecomePodModeratorRequestSent(false);
            podUserPodHasUserEntity.setTimestampBecomePodModeratorRequestSent(null);
            this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
        }
        return this._getUserBubblesPendingBecomePodModeratorRequest(idUser, idPod);
    }

    /**
     * id
     * idUsers
     */
    public List<UserBubbleModel> addPodModerators(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id",
                        "idUsers"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeAddPodModerators(idUser, idPod);
        List<String> idUsersToBecomeModerator = new ArrayList<>();
        for (int i = 0; i < rb.get("idUsers").size(); i++) {
            String idUserToBecomeModerator = RequestBodyFormatter.fString(rb.get("idUsers").get(i));
            idUsersToBecomeModerator.add(idUserToBecomeModerator);
        }
        for (int i = 0; i < idUsersToBecomeModerator.size(); i++) {
            String idUserToBecomeModerator = idUsersToBecomeModerator.get(i);
            PodUserPodHasUserEntity podUserPodHasUserEntity = this.podUserPodHasUserRepository
                    .findByIdPodIdUser(idPod, idUserToBecomeModerator).get(0);
            podUserPodHasUserEntity.setModerator(true);
            podUserPodHasUserEntity.setTimestampBecomeModerator((int) Instant.now().getEpochSecond());
            this.podUserPodHasUserRepository.save(podUserPodHasUserEntity);
            utilities.generateNotification(
                    this.notificationRepository,
                    UUID.fromString(idUserToBecomeModerator),
                    Constants.NOTIFICATION_TYPE_ADDED_YOU_AS_POD_MODERATOR,
                    String.format("@%s has added you as a moderator of Pod: %s",
                            this.userRepository.findById(UUID.fromString(idUser)).get().getUsername(),
                            this.podRepository.findById(UUID.fromString(idPod)).get().getName()),
                    Constants.NOTIFICATION_LINK_PAGE_TYPE_POD,
                    UUID.fromString(idPod));
        }
        return this._getUserBubblesAddPodModerator(idUser, idPod);
    }

    /**
     * id
     */
    public List<UserBubbleModel> getUserBubblesInviteJoinPod(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetUserBubblesInviteJoinPod(idUser, idPod);
        return this._getUserBubblesInviteJoinPod(idUser, idPod);
    }

    public List<UserBubbleModel> _getUserBubblesInviteJoinPod(String idUser, String idPod) throws Exception {
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesInviteJoinPod = this.userRepository.getUserBubblesFollowing(idUser).stream()
                .filter(userBubbleDTO -> {
                    // keep only the following users: users that you follow who aren't a member and
                    // who haven't gotten a invite to join the pod yet
                    List<PodUserPodHasUserEntity> podUserPodHasUserEntityList = this.podUserPodHasUserRepository
                            .findByIdPodIdUser(idPod, userBubbleDTO.getId().toString());
                    if (podUserPodHasUserEntityList.size() == 0) {
                        return true;
                    }
                    if (podUserPodHasUserEntityList.get(0).isMember()
                            || podUserPodHasUserEntityList.get(0).isJoinPodInviteSent()) {
                        return false;
                    }
                    return true;
                }).map(userBubbleDTO -> {
                    String userBubbleImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                            userBubbleDTO.getIdImageKey());
                    return UserBubbleModel.builder()
                            .id(userBubbleDTO.getId())
                            .name(userBubbleDTO.getName())
                            .username(userBubbleDTO.getUsername())
                            .imageLink(userBubbleImageLink)
                            .timestampToSortBy(userBubbleDTO.getTimestampToSortBy())
                            .datetimeDateOnlyLabel(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTO.getTimestampToSortBy()))
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTO.getTimestampToSortBy()))
                            .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTO.getId()))
                            .isFollowRequestSentNotYetAccepted(
                                    userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleDTO.getId()))
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

    /**
     * id
     */
    public List<UserBubbleModel> getUserBubblesAddPodModerator(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetUserBubblesAddPodModerator(idUser, idPod);
        return this._getUserBubblesAddPodModerator(idUser, idPod);
    }

    public List<UserBubbleModel> _getUserBubblesAddPodModerator(String idUser, String idPod) throws Exception {
        Set<UUID> userIdsPodModerator = new HashSet<>(this.userRepository.getUserBubblesPodModerator(idPod).stream()
                .map(userBubbleDTO -> userBubbleDTO.getId()).collect(Collectors.toList()));
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);

        List<UserBubbleModel> userBubblesAddPodModerator = this.userRepository.getUserBubblesPodMember(idPod).stream()
                .filter(userBubbleDTO -> {
                    // keep those that haven't become moderators yet
                    return !userIdsPodModerator.contains(userBubbleDTO.getId());
                }).map(userBubbleDTO -> {
                    String userBubbleImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                            userBubbleDTO.getIdImageKey());
                    return UserBubbleModel.builder()
                            .id(userBubbleDTO.getId())
                            .name(userBubbleDTO.getName())
                            .username(userBubbleDTO.getUsername())
                            .imageLink(userBubbleImageLink)
                            .timestampToSortBy(userBubbleDTO.getTimestampToSortBy())
                            .datetimeDateOnlyLabel(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTO.getTimestampToSortBy()))
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTO.getTimestampToSortBy()))
                            .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTO.getId()))
                            .isFollowRequestSentNotYetAccepted(
                                    userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleDTO.getId()))
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

    /**
     * id
     */
    public List<UserBubbleModel> getUserBubblesPendingBecomePodModeratorRequest(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetUserBubblesPendingBecomePodModeratorRequest(idUser, idPod);
        return this._getUserBubblesPendingBecomePodModeratorRequest(idUser, idPod);
    }

    public List<UserBubbleModel> _getUserBubblesPendingBecomePodModeratorRequest(String idUser, String idPod)
            throws Exception {
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);

        List<UserBubbleModel> userBubblesPendingBecomePodModeratorRequest = this.userRepository
                .getUserBubblesBecomePodModeratorRequestSentNotYetApproved(idPod).stream().map(userBubbleDTO -> {
                    String userBubbleImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                            userBubbleDTO.getIdImageKey());
                    return UserBubbleModel.builder()
                            .id(userBubbleDTO.getId())
                            .name(userBubbleDTO.getName())
                            .username(userBubbleDTO.getUsername())
                            .imageLink(userBubbleImageLink)
                            .timestampToSortBy(userBubbleDTO.getTimestampToSortBy())
                            .datetimeDateOnlyLabel(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTO.getTimestampToSortBy()))
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTO.getTimestampToSortBy()))
                            .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTO.getId()))
                            .isFollowRequestSentNotYetAccepted(
                                    userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(userBubbleDTO.getId()))
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

    private PodCardsPaginatedModel _getPodCardsPaginatedModelFromSharedAndIndividualProperties(
            String idViewingUser,
            List<PodCardSharedPropertiesDTO> podCards_SharedProperties,
            List<PodCardIndividualPropertiesDTO> podCards_IndividualProperties,
            boolean filterIsPublic,
            boolean filterIsNotPublic,
            boolean filterIsMemberIndividual,
            boolean filterIsNotMemberIndividual,
            boolean filterIsModeratorIndividual,
            boolean filterIsNotModeratorIndividual,
            int paginationIdxStart,
            int paginationN
            ) {
        HashMap<UUID, PodCardSharedPropertiesDTO> idStampToPodCardSharedPropertiesDTOMap = new HashMap<>();
        HashMap<UUID, PodCardIndividualPropertiesDTO> idStampToPodCardIndividualPropertiesDTOMap = new HashMap<>();

        for (int i = 0; i < podCards_SharedProperties.size(); i++) {
            idStampToPodCardSharedPropertiesDTOMap.put(podCards_SharedProperties.get(i).getId(),
                    podCards_SharedProperties.get(i));
        }
        for (int i = 0; i < podCards_IndividualProperties.size(); i++) {
            idStampToPodCardIndividualPropertiesDTOMap.put(podCards_IndividualProperties.get(i).getId(),
                    podCards_IndividualProperties.get(i));
        }
        List<PodCardModel> podCardList = new ArrayList<>();
        for (UUID idStamp : idStampToPodCardSharedPropertiesDTOMap.keySet()) {
            PodCardSharedPropertiesDTO podCardSharedPropertiesEntry = idStampToPodCardSharedPropertiesDTOMap
                    .get(idStamp);
            PodCardIndividualPropertiesDTO podCardIndividualPropertiesEntry = idStampToPodCardIndividualPropertiesDTOMap
                    .get(idStamp);
            String podImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_POD_IMAGE,
                    podCardSharedPropertiesEntry.getIdImageKey());
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
        Set<UUID> podIdsViewingUserIsMemberOf = new HashSet<>(
                this.podRepository.getPodIdsUserIsMemberOf(idViewingUser));
        podCardList = podCardList.stream().filter(podCard -> {
            boolean isViewingUserAllowedAccessViewPodCard = podCard.isPublic();
            if (!podCard.isPublic()) {
                isViewingUserAllowedAccessViewPodCard = podIdsViewingUserIsMemberOf.contains(podCard.getId());
            }
            return isViewingUserAllowedAccessViewPodCard &&
                    (podCard.isPublic() == filterIsPublic || podCard.isPublic() != filterIsNotPublic) &&
                    (podCard.isMember() == filterIsMemberIndividual
                            || podCard.isMember() != filterIsNotMemberIndividual)
                    &&
                    (podCard.isModerator() == filterIsModeratorIndividual
                            || podCard.isModerator() != filterIsNotModeratorIndividual);
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

        // fix to correct size
        int idxStart = Math.min(paginationIdxStart, podCardList.size());
        int idxEnd = Math.min(paginationIdxStart+paginationN, podCardList.size());
        PodCardsPaginatedModel podCardsPaginatedModel = PodCardsPaginatedModel.builder()
            .data(podCardList.subList(idxStart, idxEnd))
            .totalN(podCardList.size())
            .build();
        return podCardsPaginatedModel;
    }

    /**
     * id
     */
    public List<UserBubbleModel> getUserBubblesTaskComplete(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTask = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetUserBubblesTaskComplete(idUser, idTask);
        return this._getUserBubblesTaskComplete(idUser, idTask);
    }

    public List<UserBubbleModel> _getUserBubblesTaskComplete(String idUser, String idTask) throws Exception {
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesTaskComplete = this.userRepository.getUserBubblesTaskComplete(idTask).stream()
                .map(userBubbleDTOTaskComplete -> {
                    return UserBubbleModel.builder()
                            .id(userBubbleDTOTaskComplete.getId())
                            .name(userBubbleDTOTaskComplete.getName())
                            .username(userBubbleDTOTaskComplete.getUsername())
                            .imageLink(utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                                    userBubbleDTOTaskComplete.getIdImageKey()))
                            .timestampToSortBy(userBubbleDTOTaskComplete.getTimestampToSortBy())
                            .datetimeDateOnlyLabel(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTOTaskComplete.getTimestampToSortBy()))
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTOTaskComplete.getTimestampToSortBy()))
                            .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTOTaskComplete.getId()))
                            .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted
                                    .contains(userBubbleDTOTaskComplete.getId()))
                            .isMe(idUser.equals(userBubbleDTOTaskComplete.getId().toString()))
                            .build();
                }).collect(Collectors.toList());
        Collections.sort(userBubblesTaskComplete, new Comparator<UserBubbleModel>() {
            public int compare(UserBubbleModel userBubbleTaskComplete1, UserBubbleModel userBubbleTaskComplete2) {
                if (userBubbleTaskComplete1.isMe() == true && userBubbleTaskComplete2.isMe() == false) {
                    return -1;
                } else if (userBubbleTaskComplete1.isMe() == false && userBubbleTaskComplete2.isMe() == true) {
                    return 1;
                }
                if (userBubbleTaskComplete1.isFollowedByMe() == true
                        && userBubbleTaskComplete2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubbleTaskComplete1.isFollowedByMe() == false
                        && userBubbleTaskComplete2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleTaskComplete1.getUsername().compareTo(userBubbleTaskComplete2.getUsername());
            }
        });
        return userBubblesTaskComplete;
    }

    /**
     * filterByName
     * filterIsComplete
     * filterIsNotComplete
     * filterIsStar
     * filterIsNotStar
     * filterIsPin
     * filterIsNotPin
     */
    public List<TaskModel> getTasksPersonal(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetTasksPersonal(idUser);
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "filterByName",
                        "filterIsComplete",
                        "filterIsNotComplete",
                        "filterIsStar",
                        "filterIsNotStar",
                        "filterIsPin",
                        "filterIsNotPin"));
        String filterByName = RequestBodyFormatter.fStringLowercase(rb.get("filterByName"));
        boolean filterIsComplete = RequestBodyFormatter.fBoolean(rb.get("filterIsComplete"));
        boolean filterIsNotComplete = RequestBodyFormatter.fBoolean(rb.get("filterIsNotComplete"));
        boolean filterIsStar = RequestBodyFormatter.fBoolean(rb.get("filterIsStar"));
        boolean filterIsNotStar = RequestBodyFormatter.fBoolean(rb.get("filterIsNotStar"));
        boolean filterIsPin = RequestBodyFormatter.fBoolean(rb.get("filterIsPin"));
        boolean filterIsNotPin = RequestBodyFormatter.fBoolean(rb.get("filterIsNotPin"));

        List<TaskSharedPropertiesDTO> tasksPersonal_SharedProperties = this.taskRepository
                .getTasksPersonal_SharedProperties(idUser, filterByName);
        List<TaskIndividualPropertiesDTO> tasksPersonal_IndividualProperties = this.taskRepository
                .getTasksPersonal_IndividualProperties(idUser, filterByName);
        List<TaskModel> tasksPage = this._getTaskModelFromSharedAndIndividualProperties(
                idUser,
                tasksPersonal_SharedProperties,
                tasksPersonal_IndividualProperties,
                filterIsComplete,
                filterIsNotComplete,
                filterIsStar,
                filterIsNotStar,
                filterIsPin,
                filterIsNotPin);
        return tasksPage;
    }

    /**
     * id
     * filterByName
     * filterIsComplete
     * filterIsNotComplete
     * filterIsStar
     * filterIsNotStar
     * filterIsPin
     * filterIsNotPin
     */
    public List<TaskModel> getTasksAssociatedWithPod(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id",
                        "filterByName",
                        "filterIsComplete",
                        "filterIsNotComplete",
                        "filterIsStar",
                        "filterIsNotStar",
                        "filterIsPin",
                        "filterIsNotPin"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetTasksAssociatedWithPod(idUser, idPod);
        String filterByName = RequestBodyFormatter.fStringLowercase(rb.get("filterByName"));
        boolean filterIsComplete = RequestBodyFormatter.fBoolean(rb.get("filterIsComplete"));
        boolean filterIsNotComplete = RequestBodyFormatter.fBoolean(rb.get("filterIsNotComplete"));
        boolean filterIsStar = RequestBodyFormatter.fBoolean(rb.get("filterIsStar"));
        boolean filterIsNotStar = RequestBodyFormatter.fBoolean(rb.get("filterIsNotStar"));
        boolean filterIsPin = RequestBodyFormatter.fBoolean(rb.get("filterIsPin"));
        boolean filterIsNotPin = RequestBodyFormatter.fBoolean(rb.get("filterIsNotPin"));

        List<TaskSharedPropertiesDTO> tasksAssociatedWithPod_SharedProperties = this.taskRepository
                .getTasksAssociatedWithPod_SharedProperties(idPod, filterByName);
        List<TaskIndividualPropertiesDTO> tasksAssociatedWithPod_IndividualProperties = this.taskRepository
                .getTasksAssociatedWithPod_IndividualProperties(idPod, idUser, filterByName);
        List<TaskModel> tasksPage = this._getTaskModelFromSharedAndIndividualProperties(
                idUser,
                tasksAssociatedWithPod_SharedProperties,
                tasksAssociatedWithPod_IndividualProperties,
                filterIsComplete,
                filterIsNotComplete,
                filterIsStar,
                filterIsNotStar,
                filterIsPin,
                filterIsNotPin);
        return tasksPage;
    }

    /**
     * id
     * filterByName
     * filterIsComplete
     * filterIsNotComplete
     * filterIsStar
     * filterIsNotStar
     * filterIsPin
     * filterIsNotPin
     */
    public List<TaskModel> getTasksAssociatedWithStamp(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id",
                        "filterByName",
                        "filterIsComplete",
                        "filterIsNotComplete",
                        "filterIsStar",
                        "filterIsNotStar",
                        "filterIsPin",
                        "filterIsNotPin"));
        String idStamp = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetTasksAssociatedWithStamp(idUser, idStamp);
        String filterByName = RequestBodyFormatter.fStringLowercase(rb.get("filterByName"));
        boolean filterIsComplete = RequestBodyFormatter.fBoolean(rb.get("filterIsComplete"));
        boolean filterIsNotComplete = RequestBodyFormatter.fBoolean(rb.get("filterIsNotComplete"));
        boolean filterIsStar = RequestBodyFormatter.fBoolean(rb.get("filterIsStar"));
        boolean filterIsNotStar = RequestBodyFormatter.fBoolean(rb.get("filterIsNotStar"));
        boolean filterIsPin = RequestBodyFormatter.fBoolean(rb.get("filterIsPin"));
        boolean filterIsNotPin = RequestBodyFormatter.fBoolean(rb.get("filterIsNotPin"));

        List<TaskSharedPropertiesDTO> tasksAssociatedWithStamp_SharedProperties = this.taskRepository
                .getTasksAssociatedWithStamp_SharedProperties(idStamp, filterByName);
        List<TaskIndividualPropertiesDTO> tasksAssociatedWithStamp_IndividualProperties = this.taskRepository
                .getTasksAssociatedWithStamp_IndividualProperties(idStamp, idUser, filterByName);
        List<TaskModel> tasksPage = this._getTaskModelFromSharedAndIndividualProperties(
                idUser,
                tasksAssociatedWithStamp_SharedProperties,
                tasksAssociatedWithStamp_IndividualProperties,
                filterIsComplete,
                filterIsNotComplete,
                filterIsStar,
                filterIsNotStar,
                filterIsPin,
                filterIsNotPin);
        return tasksPage;
    }

    /**
     * id
     * filterByName
     * filterIsComplete
     * filterIsNotComplete
     * filterIsStar
     * filterIsNotStar
     * filterIsPin
     * filterIsNotPin
     */
    public List<TaskModel> getPinnedTasksAssociatedWithUser(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetPinnedTasksAssociatedWithUser(idUser);
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id",
                        "filterByName",
                        "filterIsComplete",
                        "filterIsNotComplete",
                        "filterIsStar",
                        "filterIsNotStar",
                        "filterIsPin",
                        "filterIsNotPin"));
        String idUserProfile = RequestBodyFormatter.fString(rb.get("id"));
        String filterByName = RequestBodyFormatter.fStringLowercase(rb.get("filterByName"));
        boolean filterIsComplete = RequestBodyFormatter.fBoolean(rb.get("filterIsComplete"));
        boolean filterIsNotComplete = RequestBodyFormatter.fBoolean(rb.get("filterIsNotComplete"));
        boolean filterIsStar = RequestBodyFormatter.fBoolean(rb.get("filterIsStar"));
        boolean filterIsNotStar = RequestBodyFormatter.fBoolean(rb.get("filterIsNotStar"));
        boolean filterIsPin = RequestBodyFormatter.fBoolean(rb.get("filterIsPin"));
        boolean filterIsNotPin = RequestBodyFormatter.fBoolean(rb.get("filterIsNotPin"));
        List<TaskSharedPropertiesDTO> tasksAssociatedWithUser_SharedProperties = this.taskRepository
                .getPinnedTasksAssociatedWithUser_SharedProperties(idUserProfile, filterByName);
        List<TaskIndividualPropertiesDTO> tasksAssociatedWithUser_IndividualProperties = this.taskRepository
                .getPinnedTasksAssociatedWithUser_IndividualProperties(idUserProfile, filterByName);
        List<TaskModel> tasksPage = this._getTaskModelFromSharedAndIndividualProperties(
                idUser,
                tasksAssociatedWithUser_SharedProperties,
                tasksAssociatedWithUser_IndividualProperties,
                filterIsComplete,
                filterIsNotComplete,
                filterIsStar,
                filterIsNotStar,
                filterIsPin,
                filterIsNotPin);
        return tasksPage;
    }

    /**
     * N/A
     */
    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel getNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal() throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal(idUser);
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        String dateStartAsString = utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                this.userRepository.getTimestampUserCreateAccount(idUser));
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeList = this.taskRepository
                .getNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal(idUser);
        NumberOfPointsInTasksCompletedOverTimeVisualizationModel numberOfPointsInTasksCompletedOverTimeModel = this
                ._getNumberOfPointsInTasksCompletedOverTimeVisualization(
                        numberOfPointsInTasksCompletedOverTimeList,
                        dateStartAsString,
                        userTimeZoneZoneId,
                        true);
        return numberOfPointsInTasksCompletedOverTimeModel;
    }

    /**
     * id
     */
    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithPod(
            JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithPod(idUser, idPod);
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        Integer timestampUserFirstCompleteAnyTaskAssociatedWithPod = this.userRepository
                .getTimestampUserFirstCompleteAnyTaskAssociatedWithPod(idPod, idUser);
        boolean isHeatmapRelevantOverride = utilities
                .isValidTimestamp(timestampUserFirstCompleteAnyTaskAssociatedWithPod);
        String dateStartAsString;
        if (isHeatmapRelevantOverride) {
            dateStartAsString = utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                    timestampUserFirstCompleteAnyTaskAssociatedWithPod);
        } else {
            dateStartAsString = LocalDate.now(userTimeZoneZoneId).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        }
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeList = this.taskRepository
                .getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithPod(idPod, idUser);
        NumberOfPointsInTasksCompletedOverTimeVisualizationModel numberOfPointsInTasksCompletedOverTimeModel = this
                ._getNumberOfPointsInTasksCompletedOverTimeVisualization(
                        numberOfPointsInTasksCompletedOverTimeList,
                        dateStartAsString,
                        userTimeZoneZoneId,
                        isHeatmapRelevantOverride);
        return numberOfPointsInTasksCompletedOverTimeModel;
    }

    /**
     * id
     */
    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithStamp(
            JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idStamp = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithStamp(idUser, idStamp);
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        Integer timestampUserFirstCompleteAnyTaskAssociatedWithStamp = this.userRepository
                .getTimestampUserFirstCompleteAnyTaskAssociatedWithStamp(idStamp, idUser);
        boolean isHeatmapRelevantOverride = utilities
                .isValidTimestamp(timestampUserFirstCompleteAnyTaskAssociatedWithStamp);
        String dateStartAsString;
        if (isHeatmapRelevantOverride) {
            dateStartAsString = utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                    timestampUserFirstCompleteAnyTaskAssociatedWithStamp);
        } else {
            dateStartAsString = LocalDate.now(userTimeZoneZoneId).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        }
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeList = this.taskRepository
                .getNumberOfPointsInTasksCompletedOverTimeVisualizationAssociatedWithStamp(idStamp, idUser);
        NumberOfPointsInTasksCompletedOverTimeVisualizationModel numberOfPointsInTasksCompletedOverTimeModel = this
                ._getNumberOfPointsInTasksCompletedOverTimeVisualization(
                        numberOfPointsInTasksCompletedOverTimeList,
                        dateStartAsString,
                        userTimeZoneZoneId,
                        isHeatmapRelevantOverride);
        return numberOfPointsInTasksCompletedOverTimeModel;
    }

    public NumberOfPointsInTasksCompletedOverTimeVisualizationModel _getNumberOfPointsInTasksCompletedOverTimeVisualization(
            List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeList,
            String dateStartAsString,
            ZoneId userTimeZoneZoneId,
            boolean isHeatmapRelevantOverride) {
        /**
         * get day of week in label for aggregate daily line chart
         */
        Map<String, List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO>> dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap = new HashMap<>();

        for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeList.size(); i++) {
            NumberOfPointsInTasksCompletedOverTimeVisualizationDTO numberOfPointsInTasksCompletedOverTimeEntry = numberOfPointsInTasksCompletedOverTimeList
                    .get(i);
            Integer timestampOfTaskComplete = numberOfPointsInTasksCompletedOverTimeEntry.getTimestamp();
            if (utilities.isValidTimestamp(timestampOfTaskComplete)) {
                String dateLabel = utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                        timestampOfTaskComplete);
                List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeListValue = dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap
                        .getOrDefault(dateLabel, new ArrayList<>());
                numberOfPointsInTasksCompletedOverTimeListValue.add(numberOfPointsInTasksCompletedOverTimeEntry);
                dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap.put(dateLabel,
                        numberOfPointsInTasksCompletedOverTimeListValue);
            }
        }

        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.HeatmapChartDataPoint> dataHeatmapChart = new ArrayList<>();
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint> dataLineChart_AggregateDay_NotCumulative = new ArrayList<>();
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint> dataLineChart_AggregateWeek_NotCumulative = new ArrayList<>();
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint> dataLineChart_AggregateMonth_NotCumulative = new ArrayList<>();
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint> dataLineChart_AggregateDay_Cumulative = new ArrayList<>();
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint> dataLineChart_AggregateWeek_Cumulative = new ArrayList<>();
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint> dataLineChart_AggregateMonth_Cumulative = new ArrayList<>();

        DateTimeFormatter dtf_yyyyMMdd = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        DateTimeFormatter dtf_MMdd = DateTimeFormatter.ofPattern("MM/dd");
        String dateEndAsString = LocalDate.now(userTimeZoneZoneId).format(dtf_yyyyMMdd);
        // HeatmapChart (start same month, 1 year ago, on the Sunday of the week
        // including the same day of month, end on the next Saturday)
        String dateEndSundayAsString = LocalDate.now(userTimeZoneZoneId).format(dtf_yyyyMMdd);
        while (LocalDate.parse(dateEndSundayAsString, dtf_yyyyMMdd).getDayOfWeek().getValue() != 6) {
            dateEndSundayAsString = LocalDate.parse(dateEndSundayAsString, dtf_yyyyMMdd).plusDays(1)
                    .format(dtf_yyyyMMdd);
        }
        String HEATMAP_CHART_COLOR_DEFAULT = "#FFFFFF";
        String dateIteratorAsStringHeatmapChart = LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).minusYears(1)
                .format(dtf_yyyyMMdd);
        while (LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).getDayOfWeek().getValue() != 7) {
            dateIteratorAsStringHeatmapChart = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd)
                    .minusDays(1).format(dtf_yyyyMMdd);
        }
        List<Integer> totalNumberOfPointsInSingleDayForColorThresholdsList = new ArrayList<>();
        while (LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd)
                .compareTo(LocalDate.parse(dateEndSundayAsString, dtf_yyyyMMdd)) <= 0) {
            List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeListValue = dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap
                    .getOrDefault(dateIteratorAsStringHeatmapChart, new ArrayList<>());
            boolean isAfterOrEqualToRelevantStartDate = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd)
                    .isAfter(LocalDate.parse(dateStartAsString, dtf_yyyyMMdd)) ||
                    LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd)
                            .isEqual(LocalDate.parse(dateStartAsString, dtf_yyyyMMdd));
            if (isAfterOrEqualToRelevantStartDate) {
                int numberOfPoints = 0;
                for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeListValue.size(); i++) {
                    numberOfPoints += numberOfPointsInTasksCompletedOverTimeListValue.get(i).getNumberOfPoints();
                }
                totalNumberOfPointsInSingleDayForColorThresholdsList.add(numberOfPoints);
            }
            dateIteratorAsStringHeatmapChart = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd)
                    .plusDays(1).format(dtf_yyyyMMdd);
        }
        Collections.sort(totalNumberOfPointsInSingleDayForColorThresholdsList);
        String DEFAULT_COLOR_IRRELEVANT = "#e5e5e5";
        int colorP75to100UB = 0;
        int colorP75to100LB = 0;
        int colorP50to75UB = 0;
        int colorP50to75LB = 0;
        int colorP25to50UB = 0;
        int colorP25to50LB = 0;
        int colorP0to25UB = 0;
        int colorP0to25LB = 0;
        if (totalNumberOfPointsInSingleDayForColorThresholdsList.size() > 0) {
            colorP75to100UB = totalNumberOfPointsInSingleDayForColorThresholdsList
                    .get(totalNumberOfPointsInSingleDayForColorThresholdsList.size() - 1);
            colorP75to100LB = totalNumberOfPointsInSingleDayForColorThresholdsList
                    .get(3 * totalNumberOfPointsInSingleDayForColorThresholdsList.size() / 4);
            colorP50to75UB = colorP75to100LB - 1;
            colorP50to75LB = Math.min(colorP50to75UB, totalNumberOfPointsInSingleDayForColorThresholdsList
                    .get(totalNumberOfPointsInSingleDayForColorThresholdsList.size() / 2));
            colorP25to50UB = colorP50to75LB - 1;
            colorP25to50LB = Math.min(colorP25to50UB, totalNumberOfPointsInSingleDayForColorThresholdsList
                    .get(totalNumberOfPointsInSingleDayForColorThresholdsList.size() / 4));
            colorP0to25UB = colorP25to50LB - 1;
            colorP0to25LB = Math.min(colorP0to25UB, totalNumberOfPointsInSingleDayForColorThresholdsList.get(0));
        }
        dateIteratorAsStringHeatmapChart = LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).minusYears(1)
                .format(dtf_yyyyMMdd);
        while (LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).getDayOfWeek().getValue() != 7) {
            dateIteratorAsStringHeatmapChart = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd)
                    .minusDays(1).format(dtf_yyyyMMdd);
        }
        while (LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd)
                .compareTo(LocalDate.parse(dateEndSundayAsString, dtf_yyyyMMdd)) <= 0) {
            List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeListValue = dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap
                    .getOrDefault(dateIteratorAsStringHeatmapChart, new ArrayList<>());
            int numberOfTasksComplete = numberOfPointsInTasksCompletedOverTimeListValue.size();
            int numberOfPoints = 0;
            for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeListValue.size(); i++) {
                numberOfPoints += numberOfPointsInTasksCompletedOverTimeListValue.get(i).getNumberOfPoints();
            }
            String color = HEATMAP_CHART_COLOR_DEFAULT;
            if (numberOfTasksComplete == 0) {
                color = HEATMAP_CHART_COLOR_DEFAULT;
            } else if (numberOfPoints >= colorP0to25LB && numberOfPoints <= colorP0to25UB) {
                color = "#ccfff3";
            } else if (numberOfPoints >= colorP25to50LB && numberOfPoints <= colorP25to50UB) {
                color = "#ccfff3";
            } else if (numberOfPoints >= colorP50to75LB && numberOfPoints <= colorP50to75UB) {
                color = "#99ffe8";
            } else if (numberOfPoints >= colorP75to100LB && numberOfPoints <= colorP75to100UB) {
                color = "#89e5d0";
            }
            // // random to experiment with different color patterns: ["#ccfff3", "#99ffe8",
            // "#89e5d0"]
            // List<String> randomColors = new
            // ArrayList<>(Arrays.asList(HEATMAP_CHART_COLOR_DEFAULT, "#ccfff3", "#ccfff3",
            // "#99ffe8", "#99ffe8", "#89e5d0", "#89e5d0"));
            // color = randomColors.get(new Random().nextInt(randomColors.size()));
            boolean isAfterOrEqualToRelevantStartDate = isHeatmapRelevantOverride &&
                    (LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd)
                            .isAfter(LocalDate.parse(dateStartAsString, dtf_yyyyMMdd)) ||
                            LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd)
                                    .isEqual(LocalDate.parse(dateStartAsString, dtf_yyyyMMdd)));
            boolean isToday = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd)
                    .isEqual(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd));
            boolean isBeforeOrEqualToRelevantEndDate = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd)
                    .isBefore(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd)) || isToday;
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.HeatmapChartDataPoint heatmapChartDataPointEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.HeatmapChartDataPoint
                    .builder()
                    .numberOfPoints(numberOfPoints)
                    .numberOfTasksComplete(numberOfTasksComplete)
                    .color(isAfterOrEqualToRelevantStartDate && isBeforeOrEqualToRelevantEndDate ? color
                            : DEFAULT_COLOR_IRRELEVANT)
                    .dateLabel(dateIteratorAsStringHeatmapChart)
                    .dayOfWeek(
                            LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd).getDayOfWeek().toString())
                    .isAfterOrEqualToRelevantStartDate(isAfterOrEqualToRelevantStartDate)
                    .isBeforeOrEqualToRelevantEndDate(isBeforeOrEqualToRelevantEndDate)
                    .isToday(isToday)
                    .build();
            dataHeatmapChart.add(heatmapChartDataPointEntry);
            dateIteratorAsStringHeatmapChart = LocalDate.parse(dateIteratorAsStringHeatmapChart, dtf_yyyyMMdd)
                    .plusDays(1).format(dtf_yyyyMMdd);
        }

        // LineChart - AGGREGATE DAY - 7 days ago (1 full week)
        String dateIteratorAsStringLineChartAggregateDay = LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).minusWeeks(1)
                .format(dtf_yyyyMMdd);
        int dataLineChart_AggregateDay_Cumulative_numberOfPoints = 0;
        while (LocalDate.parse(dateIteratorAsStringLineChartAggregateDay, dtf_yyyyMMdd)
                .compareTo(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd)) <= 0) {
            List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeListValue = dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap
                    .getOrDefault(dateIteratorAsStringLineChartAggregateDay, new ArrayList<>());
            String dateLabel = LocalDate.parse(dateIteratorAsStringLineChartAggregateDay, dtf_yyyyMMdd)
                    .format(dtf_MMdd);
            int numberOfPoints = 0;
            for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeListValue.size(); i++) {
                numberOfPoints += numberOfPointsInTasksCompletedOverTimeListValue.get(i).getNumberOfPoints();
            }
            dataLineChart_AggregateDay_Cumulative_numberOfPoints += numberOfPoints;
            // Not Cumulative
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint dataLineChart_AggregateDay_NotCumulativeEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint
                    .builder()
                    .numberOfPoints(numberOfPoints)
                    .dateLabel(dateLabel)
                    .build();
            dataLineChart_AggregateDay_NotCumulative.add(dataLineChart_AggregateDay_NotCumulativeEntry);
            // Cumulative
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint dataLineChart_AggregateDay_CumulativeEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint
                    .builder()
                    .numberOfPoints(dataLineChart_AggregateDay_Cumulative_numberOfPoints)
                    .dateLabel(dateLabel)
                    .build();
            dataLineChart_AggregateDay_Cumulative.add(dataLineChart_AggregateDay_CumulativeEntry);
            dateIteratorAsStringLineChartAggregateDay = LocalDate
                    .parse(dateIteratorAsStringLineChartAggregateDay, dtf_yyyyMMdd).plusDays(1).format(dtf_yyyyMMdd);
        }

        // LineChart - AGGREGATE WEEK - Sunday to Sunday (start on the Sunday of the
        // week in previous month which includes this day of month)
        String dateIteratorAsStringLineChartAggregateWeek = LocalDate.parse(dateEndAsString, dtf_yyyyMMdd)
                .minusMonths(1).format(dtf_yyyyMMdd);
        while (LocalDate.parse(dateIteratorAsStringLineChartAggregateWeek, dtf_yyyyMMdd).getDayOfWeek()
                .getValue() != 7) {
            dateIteratorAsStringLineChartAggregateWeek = LocalDate
                    .parse(dateIteratorAsStringLineChartAggregateWeek, dtf_yyyyMMdd).minusDays(1).format(dtf_yyyyMMdd);
        }
        int dataLineChart_AggregateWeek_Cumulative_numberOfPoints = 0;
        while (LocalDate.parse(dateIteratorAsStringLineChartAggregateWeek, dtf_yyyyMMdd)
                .compareTo(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd)) <= 0) {
            String dateIteratorStartAsString = dateIteratorAsStringLineChartAggregateWeek;
            String dateIteratorEndAsString = LocalDate.parse(dateIteratorAsStringLineChartAggregateWeek, dtf_yyyyMMdd)
                    .plusWeeks(1).minusDays(1).compareTo(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd)) < 0
                            ? LocalDate.parse(dateIteratorAsStringLineChartAggregateWeek, dtf_yyyyMMdd).plusWeeks(1)
                                    .minusDays(1).format(dtf_yyyyMMdd)
                            : LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).format(dtf_yyyyMMdd);
            String dateLabel = new StringBuilder()
                    .append(LocalDate.parse(dateIteratorStartAsString, dtf_yyyyMMdd).format(dtf_MMdd))
                    .append("-")
                    .append(LocalDate.parse(dateIteratorEndAsString, dtf_yyyyMMdd).format(dtf_MMdd))
                    .toString();
            String dateIteratorByDayAsString = dateIteratorStartAsString;
            int numberOfPoints = 0;
            while (LocalDate.parse(dateIteratorByDayAsString, dtf_yyyyMMdd)
                    .compareTo(LocalDate.parse(dateIteratorEndAsString, dtf_yyyyMMdd)) <= 0) {
                List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeListValue = dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap
                        .getOrDefault(dateIteratorByDayAsString, new ArrayList<>());
                for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeListValue.size(); i++) {
                    numberOfPoints += numberOfPointsInTasksCompletedOverTimeListValue.get(i).getNumberOfPoints();
                }
                dateIteratorByDayAsString = LocalDate.parse(dateIteratorByDayAsString, dtf_yyyyMMdd).plusDays(1)
                        .format(dtf_yyyyMMdd);
            }
            dataLineChart_AggregateWeek_Cumulative_numberOfPoints += numberOfPoints;
            // Not Cumulative
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint dataLineChart_AggregateWeek_NotCumulativeEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint
                    .builder()
                    .numberOfPoints(numberOfPoints)
                    .dateLabel(dateLabel)
                    .build();
            dataLineChart_AggregateWeek_NotCumulative.add(dataLineChart_AggregateWeek_NotCumulativeEntry);
            // Cumulative
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint dataLineChart_AggregateWeek_CumulativeEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint
                    .builder()
                    .numberOfPoints(dataLineChart_AggregateWeek_Cumulative_numberOfPoints)
                    .dateLabel(dateLabel)
                    .build();
            dataLineChart_AggregateWeek_Cumulative.add(dataLineChart_AggregateWeek_CumulativeEntry);
            dateIteratorAsStringLineChartAggregateWeek = LocalDate
                    .parse(dateIteratorAsStringLineChartAggregateWeek, dtf_yyyyMMdd).plusWeeks(1).format(dtf_yyyyMMdd);
        }

        // AGGREGATE MONTH (start same month, 1 year ago, on the 1st of the month)
        String dateIteratorAsStringLineChartAggregateMonth = LocalDate.of(
                LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).getYear() - 1,
                LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).getMonth().getValue(),
                1).format(dtf_yyyyMMdd);
        int dataLineChart_AggregateMonth_Cumulative_numberOfPoints = 0;
        while (LocalDate.parse(dateIteratorAsStringLineChartAggregateMonth, dtf_yyyyMMdd)
                .compareTo(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd)) <= 0) {
            String dateIteratorStartAsString = dateIteratorAsStringLineChartAggregateMonth;
            String dateIteratorEndAsString = LocalDate.parse(dateIteratorAsStringLineChartAggregateMonth, dtf_yyyyMMdd)
                    .plusMonths(1).minusDays(1).compareTo(LocalDate.parse(dateEndAsString, dtf_yyyyMMdd)) < 0
                            ? LocalDate.parse(dateIteratorAsStringLineChartAggregateMonth, dtf_yyyyMMdd).plusMonths(1)
                                    .minusDays(1).format(dtf_yyyyMMdd)
                            : LocalDate.parse(dateEndAsString, dtf_yyyyMMdd).format(dtf_yyyyMMdd);
            String dateLabel = new StringBuilder()
                    .append(LocalDate.parse(dateIteratorStartAsString, dtf_yyyyMMdd).format(dtf_MMdd))
                    .append("-")
                    .append(LocalDate.parse(dateIteratorEndAsString, dtf_yyyyMMdd).format(dtf_MMdd))
                    .toString();
            String dateIteratorByDayAsString = dateIteratorStartAsString;
            int numberOfPoints = 0;
            while (LocalDate.parse(dateIteratorByDayAsString, dtf_yyyyMMdd)
                    .compareTo(LocalDate.parse(dateIteratorEndAsString, dtf_yyyyMMdd)) <= 0) {
                List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeListValue = dateLabelToNumberOfPointsInTasksCompletedOverTimeVisualizationListMap
                        .getOrDefault(dateIteratorByDayAsString, new ArrayList<>());
                for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeListValue.size(); i++) {
                    numberOfPoints += numberOfPointsInTasksCompletedOverTimeListValue.get(i).getNumberOfPoints();
                }
                dateIteratorByDayAsString = LocalDate.parse(dateIteratorByDayAsString, dtf_yyyyMMdd).plusDays(1)
                        .format(dtf_yyyyMMdd);
            }
            dataLineChart_AggregateMonth_Cumulative_numberOfPoints += numberOfPoints;
            // Not Cumulative
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint dataLineChart_AggregateMonth_NotCumulativeEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint
                    .builder()
                    .numberOfPoints(numberOfPoints)
                    .dateLabel(dateLabel)
                    .build();
            dataLineChart_AggregateMonth_NotCumulative.add(dataLineChart_AggregateMonth_NotCumulativeEntry);
            // Cumulative
            NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint dataLineChart_AggregateMonth_CumulativeEntry = NumberOfPointsInTasksCompletedOverTimeVisualizationModel.LineChartDataPoint
                    .builder()
                    .numberOfPoints(dataLineChart_AggregateMonth_Cumulative_numberOfPoints)
                    .dateLabel(dateLabel)
                    .build();
            dataLineChart_AggregateMonth_Cumulative.add(dataLineChart_AggregateMonth_CumulativeEntry);
            dateIteratorAsStringLineChartAggregateMonth = LocalDate
                    .parse(dateIteratorAsStringLineChartAggregateMonth, dtf_yyyyMMdd).plusMonths(1)
                    .format(dtf_yyyyMMdd);
        }

        NumberOfPointsInTasksCompletedOverTimeVisualizationModel numberOfPointsInTasksCompletedOverTimeModel = NumberOfPointsInTasksCompletedOverTimeVisualizationModel
                .builder()
                .dataHeatmapChart(dataHeatmapChart)
                .dataLineChart_AggregateDay_NotCumulative(dataLineChart_AggregateDay_NotCumulative)
                .dataLineChart_AggregateDay_Cumulative(dataLineChart_AggregateDay_Cumulative)
                .dataLineChart_AggregateWeek_NotCumulative(dataLineChart_AggregateWeek_NotCumulative)
                .dataLineChart_AggregateWeek_Cumulative(dataLineChart_AggregateWeek_Cumulative)
                .dataLineChart_AggregateMonth_NotCumulative(dataLineChart_AggregateMonth_NotCumulative)
                .dataLineChart_AggregateMonth_Cumulative(dataLineChart_AggregateMonth_Cumulative)
                .build();
        return numberOfPointsInTasksCompletedOverTimeModel;

    }

    /**
     * id
     * ?name
     * ?description
     * ?imageAsBase64String
     * ?numberOfPoints
     * ?datetimeTarget
     * ?isComplete
     * ?isStar
     * ?isPin
     * ?noteText
     * ?noteImageAsBase64String
     */
    public TaskModel updateTask(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTask = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeUpdateTask(idUser, idTask, rb);
        TaskEntity taskEntity = this.taskRepository.findById(UUID.fromString(idTask)).get();
        if (rb.has("name")) {
            String taskName = RequestBodyValidator.stringRequired(RequestBodyFormatter.fString(rb.get("name")),
                    Limits.TASK_NAME_MIN_LENGTH_CHARACTERS, Limits.TASK_NAME_MAX_LENGTH_CHARACTERS);
            boolean isNoop = (taskName == null && taskEntity.getName() == null) ||
                    (taskName != null && taskName.equals(taskEntity.getName()));
            if (!isNoop) {
                taskEntity.setName(taskName);
                taskEntity.setTimestampUpdate((int) Instant.now().getEpochSecond());
                this.taskRepository.save(taskEntity);
            }
        }
        if (rb.has("description")) {
            String taskDescription = RequestBodyValidator.stringOptional(
                    RequestBodyFormatter.fString(rb.get("description")),
                    Limits.TASK_DESCRIPTION_MIN_LENGTH_CHARACTERS, Limits.TASK_DESCRIPTION_MAX_LENGTH_CHARACTERS);
            boolean isNoop = (taskDescription == null && taskEntity.getDescription() == null) ||
                    (taskDescription != null && taskDescription.equals(taskEntity.getDescription()));
            if (!isNoop) {
                taskEntity.setDescription(taskDescription);
                taskEntity.setTimestampUpdate((int) Instant.now().getEpochSecond());
                this.taskRepository.save(taskEntity);
            }
        }
        if (rb.has("imageAsBase64String")) {
            String taskImageAsBase64String = RequestBodyValidator
                    .imageOptional(RequestBodyFormatter.fString(rb.get("imageAsBase64String")));
            boolean isNoop = taskImageAsBase64String == null && taskEntity.getIdImageKey() == null;
            if (!isNoop) {
                if (taskImageAsBase64String == null) {
                    taskEntity.setIdImageKey(null);
                    this.taskRepository.save(taskEntity);
                } else {
                    UUID taskImageKeyId = UUID.randomUUID();
                    byte[] imageAsByteArray = java.util.Base64.getDecoder()
                            .decode(taskImageAsBase64String.substring(taskImageAsBase64String.indexOf(",") + 1));
                    RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
                    utilities.putObjectInS3(Constants.S3_FOLDER_NAME_TASK_IMAGE, taskImageKeyId,
                            imageAsByteArrayRequestBody);
                    taskEntity.setIdImageKey(taskImageKeyId);
                    this.taskRepository.save(taskEntity);
                }
            }
        }
        if (rb.has("numberOfPoints")) {
            int taskNumberOfPoints = RequestBodyValidator.intRequired(
                    RequestBodyFormatter.fInt(rb.get("numberOfPoints")), Limits.TASK_NUMBER_OF_POINTS_MIN,
                    Limits.TASK_NUMBER_OF_POINTS_MAX);
            boolean isNoop = taskNumberOfPoints == taskEntity.getNumberOfPoints();
            if (!isNoop) {
                taskEntity.setNumberOfPoints(taskNumberOfPoints);
                taskEntity.setTimestampUpdate((int) Instant.now().getEpochSecond());
                this.taskRepository.save(taskEntity);
            }
        }
        if (rb.has("datetimeTarget")) {
            String taskDatetimeTarget = RequestBodyValidator
                    .datetimeOptional(RequestBodyFormatter.fString(rb.get("datetimeTarget")));
            boolean isNoop = ((taskDatetimeTarget == null && taskEntity.getDatetimeTarget() == null) ||
                    taskDatetimeTarget == taskEntity.getDatetimeTarget());
            if (!isNoop) {
                taskEntity.setDatetimeTarget(taskDatetimeTarget);
                taskEntity.setTimestampUpdate((int) Instant.now().getEpochSecond());
                this.taskRepository.save(taskEntity);
            }
        }
        if (rb.has("isComplete")) {
            boolean taskIsComplete = RequestBodyFormatter.fBoolean(rb.get("isComplete"));
            List<TaskUserTaskCompleteEntity> taskUserTaskCompleteEntities = this.taskUserTaskCompleteRepository
                    .findByIdTaskIdUser(idTask, idUser);
            if (taskIsComplete) {
                boolean isNoop = taskUserTaskCompleteEntities.size() > 0;
                if (!isNoop) {
                    utilities.ensureNoEntityExists(this.taskUserTaskCompleteRepository.findByIdTaskIdUser(idTask, idUser).size());
                    TaskUserTaskCompleteEntity taskUserTaskCompleteEntity = new TaskUserTaskCompleteEntity();
                    taskUserTaskCompleteEntity.setIdTask(UUID.fromString(idTask));
                    taskUserTaskCompleteEntity.setIdUser(UUID.fromString(idUser));
                    taskUserTaskCompleteEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                    this.taskUserTaskCompleteRepository.save(taskUserTaskCompleteEntity);
                }
            } else {
                boolean isNoop = taskUserTaskCompleteEntities.size() == 0;
                if (!isNoop) {
                    TaskUserTaskCompleteEntity taskUserTaskCompleteEntity = taskUserTaskCompleteEntities.get(0);
                    this.taskUserTaskCompleteRepository.delete(taskUserTaskCompleteEntity);
                }
            }
        }
        if (rb.has("isStar")) {
            boolean taskIsStar = RequestBodyFormatter.fBoolean(rb.get("isStar"));
            List<TaskUserTaskStarEntity> taskUserTaskStarEntities = this.taskUserTaskStarRepository
                    .findByIdTaskIdUser(idTask, idUser);
            if (taskIsStar) {
                boolean isNoop = taskUserTaskStarEntities.size() > 0;
                if (!isNoop) {
                    utilities.ensureNoEntityExists(this.taskUserTaskStarRepository.findByIdTaskIdUser(idTask, idUser).size());
                    TaskUserTaskStarEntity taskUserTaskStarEntity = new TaskUserTaskStarEntity();
                    taskUserTaskStarEntity.setIdTask(UUID.fromString(idTask));
                    taskUserTaskStarEntity.setIdUser(UUID.fromString(idUser));
                    taskUserTaskStarEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                    this.taskUserTaskStarRepository.save(taskUserTaskStarEntity);
                }
            } else {
                boolean isNoop = taskUserTaskStarEntities.size() == 0;
                if (!isNoop) {
                    this.taskUserTaskStarRepository.delete(taskUserTaskStarEntities.get(0));
                }
            }
        }
        if (rb.has("isPin")) {
            boolean taskIsPin = RequestBodyFormatter.fBoolean(rb.get("isPin"));
            List<TaskUserTaskPinEntity> taskUserTaskPinEntities = this.taskUserTaskPinRepository
                    .findByIdTaskIdUser(idTask, idUser);
            if (taskIsPin) {
                boolean isNoop = taskUserTaskPinEntities.size() > 0;
                if (!isNoop) {
                    utilities.ensureNoEntityExists(this.taskUserTaskPinRepository.findByIdTaskIdUser(idTask, idUser).size());
                    TaskUserTaskPinEntity taskUserTaskPinEntity = new TaskUserTaskPinEntity();
                    taskUserTaskPinEntity.setIdTask(UUID.fromString(idTask));
                    taskUserTaskPinEntity.setIdUser(UUID.fromString(idUser));
                    taskUserTaskPinEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                    this.taskUserTaskPinRepository.save(taskUserTaskPinEntity);
                }
            } else {
                boolean isNoop = taskUserTaskPinEntities.size() == 0;
                if (!isNoop) {
                    this.taskUserTaskPinRepository.delete(taskUserTaskPinEntities.get(0));
                }
            }
        }
        if (rb.has("noteText")) {
            String taskNoteText = RequestBodyValidator.stringOptional(
                    RequestBodyFormatter.fString(rb.get("noteText")), Limits.TASK_NOTE_TEXT_MIN_LENGTH_CHARACTERS,
                    Limits.TASK_NOTE_TEXT_MAX_LENGTH_CHARACTERS);
            TaskUserTaskNoteEntity taskUserTaskNoteEntity = this.taskUserTaskNoteRepository
                    .findByIdTaskIdUser(idTask, idUser).size() > 0
                            ? this.taskUserTaskNoteRepository.findByIdTaskIdUser(idTask, idUser).get(0)
                            : new TaskUserTaskNoteEntity();
            boolean isNoop = (taskNoteText == null && taskUserTaskNoteEntity.getNoteText() == null) ||
                    (taskNoteText != null && taskNoteText.equals(taskUserTaskNoteEntity.getNoteText()));
            if (!isNoop) {
                if (taskUserTaskNoteEntity.getTimestampUnix() == null) {
                    taskUserTaskNoteEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                }
                taskUserTaskNoteEntity.setIdTask(UUID.fromString(idTask));
                taskUserTaskNoteEntity.setIdUser(UUID.fromString(idUser));
                taskUserTaskNoteEntity.setNoteText(taskNoteText);
                if (taskNoteText == null) {
                    taskUserTaskNoteEntity.setTimestampNoteText(null);
                } else {
                    taskUserTaskNoteEntity.setTimestampNoteText((int) Instant.now().getEpochSecond());
                }
                if (taskUserTaskNoteEntity.getNoteText() == null
                        && taskUserTaskNoteEntity.getIdNoteImageKey() == null) {
                    this.taskUserTaskNoteRepository.delete(taskUserTaskNoteEntity);
                } else {
                    this.taskUserTaskNoteRepository.save(taskUserTaskNoteEntity);
                }
            }
        }
        if (rb.has("noteImageAsBase64String")) {
            String taskNoteImageAsBase64String = RequestBodyValidator
                    .imageOptional(RequestBodyFormatter.fString(rb.get("noteImageAsBase64String")));
            TaskUserTaskNoteEntity taskUserTaskNoteEntity = this.taskUserTaskNoteRepository
                    .findByIdTaskIdUser(idTask, idUser).size() > 0
                            ? this.taskUserTaskNoteRepository.findByIdTaskIdUser(idTask, idUser).get(0)
                            : new TaskUserTaskNoteEntity();
            boolean isNoop = taskNoteImageAsBase64String == null && taskEntity.getIdImageKey() == null;
            if (!isNoop) {
                if (taskUserTaskNoteEntity.getTimestampUnix() == null) {
                    taskUserTaskNoteEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                }
                taskUserTaskNoteEntity.setIdTask(UUID.fromString(idTask));
                taskUserTaskNoteEntity.setIdUser(UUID.fromString(idUser));
                if (taskNoteImageAsBase64String == null) {
                    taskUserTaskNoteEntity.setIdNoteImageKey(null);
                    taskUserTaskNoteEntity.setTimestampNoteImage(null);
                } else {
                    UUID taskNoteImageKeyId = UUID.randomUUID();
                    byte[] imageAsByteArray = java.util.Base64.getDecoder().decode(
                            taskNoteImageAsBase64String.substring(taskNoteImageAsBase64String.indexOf(",") + 1));
                    RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
                    utilities.putObjectInS3(Constants.S3_FOLDER_NAME_TASK_NOTE_IMAGE, taskNoteImageKeyId,
                            imageAsByteArrayRequestBody);
                    taskUserTaskNoteEntity.setIdNoteImageKey(taskNoteImageKeyId);
                    taskUserTaskNoteEntity.setTimestampNoteImage((int) Instant.now().getEpochSecond());
                }
                if (taskUserTaskNoteEntity.getNoteText() == null
                        && taskUserTaskNoteEntity.getIdNoteImageKey() == null) {
                    this.taskUserTaskNoteRepository.delete(taskUserTaskNoteEntity);
                } else {
                    this.taskUserTaskNoteRepository.save(taskUserTaskNoteEntity);
                }
            }
        }

        boolean taskIsComplete = this.taskUserTaskCompleteRepository.findByIdTaskIdUser(idTask, idUser).size() > 0;
        boolean taskIsStar = this.taskUserTaskStarRepository.findByIdTaskIdUser(idTask, idUser).size() > 0;
        boolean taskIsPin = this.taskUserTaskPinRepository.findByIdTaskIdUser(idTask, idUser).size() > 0;
        Integer taskTimestampComplete = this.taskUserTaskCompleteRepository.findByIdTaskIdUser(idTask, idUser)
                .size() > 0
                        ? this.taskUserTaskCompleteRepository.findByIdTaskIdUser(idTask, idUser).get(0)
                                .getTimestampUnix()
                        : null;
        String noteText = this.taskUserTaskNoteRepository.findByIdTaskIdUser(idTask, idUser).size() > 0
                ? this.taskUserTaskNoteRepository.findByIdTaskIdUser(idTask, idUser).get(0).getNoteText()
                : null;
        UUID noteImageKey = this.taskUserTaskNoteRepository.findByIdTaskIdUser(idTask, idUser).size() > 0
                ? this.taskUserTaskNoteRepository.findByIdTaskIdUser(idTask, idUser).get(0).getIdNoteImageKey()
                : null;
        List<UserBubbleModel> userBubblesTaskComplete = this._getUserBubblesTaskComplete(idUser, idTask.toString());
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        Set<UUID> podIdsUserIsMemberOf = new HashSet<>(this.podRepository.getPodIdsUserIsMemberOf(idUser));
        TaskModel task = TaskModel.builder()
                .id(UUID.fromString(idTask))
                .name(taskEntity.getName())
                .description(taskEntity.getDescription())
                .imageLink(utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_TASK_IMAGE, taskEntity.getIdImageKey()))
                .numberOfPoints(taskEntity.getNumberOfPoints())
                .idPod(taskEntity.getIdPod())
                .isComplete(taskIsComplete)
                .isStar(taskIsStar)
                .isPin(taskIsPin)
                .noteText(noteText)
                .noteImageLink(utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_TASK_NOTE_IMAGE, noteImageKey))
                .datetimeCreate(
                        utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, taskEntity.getTimestampUnix()))
                .datetimeUpdate(
                        utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, taskEntity.getTimestampUpdate()))
                .datetimeTarget(taskEntity.getDatetimeTarget())
                .datetimeComplete(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, taskTimestampComplete))
                .userBubblesTaskComplete(
                        userBubblesTaskComplete.subList(0, Math.min(userBubblesTaskComplete.size(), 3)))
                .userBubblesTaskCompleteTotalNumber(userBubblesTaskComplete.size())
                .isMemberOfTaskPod(
                        taskEntity.getIdPod() == null || podIdsUserIsMemberOf.contains(taskEntity.getIdPod()))
                .build();
        return task;
    }

    /**
     * name
     * numberOfPoints
     * ?description
     * ?datetimeTarget
     * ?idPod
     */
    public void createTask(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "name",
                        "numberOfPoints"));
        String taskName = RequestBodyValidator.stringRequired(RequestBodyFormatter.fString(rb.get("name")),
                Limits.TASK_NAME_MIN_LENGTH_CHARACTERS, Limits.TASK_NAME_MAX_LENGTH_CHARACTERS);
        int taskNumberOfPoints = RequestBodyValidator.intRequired(
                RequestBodyFormatter.fInt(rb.get("numberOfPoints")), Limits.TASK_NUMBER_OF_POINTS_MIN,
                Limits.TASK_NUMBER_OF_POINTS_MAX);

        TaskEntity taskEntity = new TaskEntity();
        taskEntity.setIdUserCreate(UUID.fromString(idUser));
        taskEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
        taskEntity.setName(taskName);
        taskEntity.setNumberOfPoints(taskNumberOfPoints);
        if (rb.has("idPod")) {
            String taskIdPod = RequestBodyFormatter.fString(rb.get("idPod"));
            this.authorizationService.authorizeCreateTask(idUser, taskIdPod);
            if (this.taskRepository.getTasksByIdPod(taskIdPod).size() >= Limits.LIMIT_NUMBER_OF_TOTAL_TASKS_POD) {
                throw new Exception("EXCEEDED_LIMIT_NUMBER_OF_TOTAL_TASKS_POD");
            }
            taskEntity.setIdPod(UUID.fromString(taskIdPod));
        } else {
            int numberOfIncompletePersonalTasks = this.taskRepository.getPersonalTasks(idUser)
                .stream()
                .filter(task -> !task.isComplete())
                .collect(Collectors.toList()).size();
            if (numberOfIncompletePersonalTasks >= Limits.LIMIT_NUMBER_OF_INCOMPLETE_TASKS_PERSONAL) {
                throw new Exception("EXCEEDED_LIMIT_NUMBER_OF_INCOMPLETE_TASKS_PERSONAL");
            }
        }
        if (rb.has("description")) {
            String taskDescription = RequestBodyValidator.stringOptional(
                    RequestBodyFormatter.fString(rb.get("description")),
                    Limits.TASK_DESCRIPTION_MIN_LENGTH_CHARACTERS, Limits.TASK_DESCRIPTION_MAX_LENGTH_CHARACTERS);
            taskEntity.setDescription(taskDescription);
        }
        if (rb.has("datetimeTarget")) {
            String taskDatetimeTarget = RequestBodyValidator
                    .datetimeOptional(RequestBodyFormatter.fString(rb.get("datetimeTarget")));
            taskEntity.setDatetimeTarget(taskDatetimeTarget);
        }
        this.taskRepository.save(taskEntity);
    }

    private List<TaskModel> _getTaskModelFromSharedAndIndividualProperties(
            String idUser,
            List<TaskSharedPropertiesDTO> tasks_SharedProperties,
            List<TaskIndividualPropertiesDTO> tasks_IndividualProperties,
            boolean filterIsCompleteIndividual,
            boolean filterIsNotCompleteIndividual,
            boolean filterIsStarIndividual,
            boolean filterIsNotStarIndividual,
            boolean filterIsPinIndividual,
            boolean filterIsNotPinIndividual) throws Exception {
        HashMap<UUID, TaskSharedPropertiesDTO> idTaskToTaskSharedPropertiesDTOMap = new HashMap<>();
        HashMap<UUID, TaskIndividualPropertiesDTO> idTaskToTaskIndividualPropertiesDTOMap = new HashMap<>();

        for (int i = 0; i < tasks_SharedProperties.size(); i++) {
            idTaskToTaskSharedPropertiesDTOMap.put(tasks_SharedProperties.get(i).getId(),
                    tasks_SharedProperties.get(i));
        }
        for (int i = 0; i < tasks_IndividualProperties.size(); i++) {
            idTaskToTaskIndividualPropertiesDTOMap.put(tasks_IndividualProperties.get(i).getId(),
                    tasks_IndividualProperties.get(i));
        }
        List<TaskModel> taskList = new ArrayList<>();
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        for (UUID idTask : idTaskToTaskSharedPropertiesDTOMap.keySet()) {
            TaskSharedPropertiesDTO taskSharedPropertiesEntry = idTaskToTaskSharedPropertiesDTOMap.get(idTask);
            TaskIndividualPropertiesDTO taskIndividualPropertiesEntry = idTaskToTaskIndividualPropertiesDTOMap
                    .get(idTask);
            Set<UUID> podIdsUserIsMemberOf = new HashSet<>(this.podRepository.getPodIdsUserIsMemberOf(idUser));
            boolean isTaskPodPrivate = false;
            if (taskSharedPropertiesEntry.getIdPod() != null && !this.podRepository.findById(taskSharedPropertiesEntry.getIdPod()).get().isPublic()) {
                isTaskPodPrivate = true;
            }

            TaskModel task = TaskModel.builder()
                    .id(taskSharedPropertiesEntry.getId())
                    .name(taskSharedPropertiesEntry.getName())
                    .description(taskSharedPropertiesEntry.getDescription())
                    .imageLink(utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_TASK_IMAGE,
                            taskSharedPropertiesEntry.getIdImageKey()))
                    .numberOfPoints(taskSharedPropertiesEntry.getNumberOfPoints())
                    .idPod(taskSharedPropertiesEntry.getIdPod())
                    .isComplete(taskIndividualPropertiesEntry.isComplete())
                    .isStar(taskIndividualPropertiesEntry.isStar())
                    .isPin(taskIndividualPropertiesEntry.isPin())
                    .noteText(taskIndividualPropertiesEntry.getNoteText())
                    .noteImageLink(utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_TASK_NOTE_IMAGE,
                            taskIndividualPropertiesEntry.getIdNoteImageKey()))
                    .datetimeCreate(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                            taskSharedPropertiesEntry.getTimestampUnix()))
                    .datetimeUpdate(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                            taskSharedPropertiesEntry.getTimestampUpdate()))
                    .datetimeTarget(taskSharedPropertiesEntry.getDatetimeTarget())
                    .datetimeComplete(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                            taskIndividualPropertiesEntry.getTimestampComplete()))
                    .isMemberOfTaskPod(taskSharedPropertiesEntry.getIdPod() == null
                            || podIdsUserIsMemberOf.contains(taskSharedPropertiesEntry.getIdPod()))
                    .isTaskPodPrivate(isTaskPodPrivate)
                    .build();
            taskList.add(task);
        }

        // filter
        Set<UUID> podIdsUserIsMemberOf = new HashSet<>(this.podRepository.getPodIdsUserIsMemberOf(idUser));
        Set<UUID> podIdsPublicPod = new HashSet<>(this.podRepository.getPodIdsPublicPod());
        taskList = taskList.stream().filter(task -> {
            boolean isUserAllowedAccessViewTask = task.getIdPod() == null;
            if (task.getIdPod() != null) {
                isUserAllowedAccessViewTask = podIdsPublicPod.contains(task.getIdPod())
                        || podIdsUserIsMemberOf.contains(task.getIdPod());
            }
            return isUserAllowedAccessViewTask &&
                    (task.isComplete() == filterIsCompleteIndividual
                            || task.isComplete() != filterIsNotCompleteIndividual)
                    &&
                    (task.isStar() == filterIsStarIndividual || task.isStar() != filterIsNotStarIndividual) &&
                    (task.isPin() == filterIsPinIndividual || task.isPin() != filterIsNotPinIndividual);
        }).collect(Collectors.toList());

        for (TaskModel taskModel : taskList) {
            UUID taskId = taskModel.getId();
            if (taskModel.getIdPod() != null) {
                List<UserBubbleModel> userBubblesTaskComplete = this._getUserBubblesTaskComplete(idUser,
                        taskId.toString());
                taskModel.setUserBubblesTaskComplete(
                        userBubblesTaskComplete.subList(0, Math.min(userBubblesTaskComplete.size(), 3)));
                taskModel.setUserBubblesTaskCompleteTotalNumber(userBubblesTaskComplete.size());
            }
        }
        // sort
        Collections.sort(taskList, new Comparator<TaskModel>() {
            public int compare(TaskModel task1, TaskModel task2) {
                if (task1.isComplete() == true && task2.isComplete() == false) {
                    return 1;
                } else if (task1.isComplete() == false && task2.isComplete() == true) {
                    return -1;
                }
                Integer task1TimestampCreate = idTaskToTaskSharedPropertiesDTOMap.get(task1.getId()).getTimestampUnix();
                Integer task1TimestampUpdate = idTaskToTaskSharedPropertiesDTOMap.get(task1.getId())
                        .getTimestampUpdate();
                Integer task2TimestampCreate = idTaskToTaskSharedPropertiesDTOMap.get(task2.getId()).getTimestampUnix();
                Integer task2TimestampUpdate = idTaskToTaskSharedPropertiesDTOMap.get(task2.getId())
                        .getTimestampUpdate();
                int task1TimestampCreateOrUpdate = task1TimestampUpdate != null ? task1TimestampUpdate
                        : task1TimestampCreate;
                int task2TimestampCreateOrUpdate = task2TimestampUpdate != null ? task2TimestampUpdate
                        : task2TimestampCreate;
                return task2TimestampCreateOrUpdate - task1TimestampCreateOrUpdate;
            }
        });

        return taskList;
    }

    /**
     * id
     */
    public void deleteTask(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTask = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeDeleteTask(idUser, idTask);
        TaskEntity taskEntity = this.taskRepository.findById(UUID.fromString(idTask)).get();
        taskEntity.setArchived(true);
        this.taskRepository.save(taskEntity);
    }

    /**
     * id
     * reactionType
     */
    public void updateTaskReaction(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id",
                        "reactionType"));
        String idTask = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeUpdateTaskReaction(idUser, idTask);
        String reactionType = RequestBodyValidator.stringChoice(
                RequestBodyFormatter.fString(rb.get("reactionType")), Constants.REACTION_TYPE_CHOICES);
        List<TaskUserTaskReactionEntity> taskUserTaskReactionEntityList = this.taskUserTaskReactionRepository
                .findByIdTaskIdUser(idTask, idUser);
        if (taskUserTaskReactionEntityList.size() > 0) {
            TaskUserTaskReactionEntity taskUserTaskReactionEntity = taskUserTaskReactionEntityList.get(0);
            if (reactionType.equals(taskUserTaskReactionEntity.getReactionType())) {
                this.taskUserTaskReactionRepository.delete(taskUserTaskReactionEntity);
            } else {
                taskUserTaskReactionEntity.setReactionType(reactionType);
                this.taskUserTaskReactionRepository.save(taskUserTaskReactionEntity);
            }
        } else {
            utilities.ensureNoEntityExists(this.taskUserTaskReactionRepository.findByIdTaskIdUser(idTask, idUser).size());
            TaskUserTaskReactionEntity taskUserTaskReactionEntity = new TaskUserTaskReactionEntity();
            taskUserTaskReactionEntity.setIdTask(UUID.fromString(idTask));
            taskUserTaskReactionEntity.setIdUser(UUID.fromString(idUser));
            taskUserTaskReactionEntity.setReactionType(reactionType);
            taskUserTaskReactionEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            this.taskUserTaskReactionRepository.save(taskUserTaskReactionEntity);
        }
    }

    /**
     * id
     * reactionType
     */
    public void updateTaskCommentReaction(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id",
                        "reactionType"));
        String idTaskComment = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeUpdateTaskCommentReaction(idUser, idTaskComment);
        String reactionType = RequestBodyValidator.stringChoice(
                RequestBodyFormatter.fString(rb.get("reactionType")), Constants.REACTION_TYPE_CHOICES);
        List<TaskUserTaskCommentReactionEntity> taskUserTaskCommentReactionEntityList = this.taskUserTaskCommentReactionRepository
                .findByIdTaskCommentIdUser(idTaskComment, idUser);
        if (taskUserTaskCommentReactionEntityList.size() > 0) {
            TaskUserTaskCommentReactionEntity taskUserTaskCommentReactionEntity = taskUserTaskCommentReactionEntityList
                    .get(0);
            if (reactionType.equals(taskUserTaskCommentReactionEntity)) {
                this.taskUserTaskCommentReactionRepository.delete(taskUserTaskCommentReactionEntity);
            } else {
                taskUserTaskCommentReactionEntity.setReactionType(reactionType);
                this.taskUserTaskCommentReactionRepository.save(taskUserTaskCommentReactionEntity);
            }
        } else {
            utilities.ensureNoEntityExists(this.taskUserTaskCommentReactionRepository.findByIdTaskCommentIdUser(idTaskComment, idUser).size());
            TaskUserTaskCommentReactionEntity taskUserTaskCommentReactionEntity = new TaskUserTaskCommentReactionEntity();
            taskUserTaskCommentReactionEntity.setIdTaskComment(UUID.fromString(idTaskComment));
            taskUserTaskCommentReactionEntity.setIdUser(UUID.fromString(idUser));
            taskUserTaskCommentReactionEntity.setReactionType(reactionType);
            taskUserTaskCommentReactionEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            this.taskUserTaskCommentReactionRepository.save(taskUserTaskCommentReactionEntity);
        }
    }

    /**
     * id
     * reactionType
     */
    public void updateTaskCommentReplyReaction(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id",
                        "reactionType"));
        String idTaskCommentReply = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeUpdateTaskCommentReplyReaction(idUser, idTaskCommentReply);
        String reactionType = RequestBodyValidator.stringChoice(
                RequestBodyFormatter.fString(rb.get("reactionType")), Constants.REACTION_TYPE_CHOICES);
        List<TaskUserTaskCommentReplyReactionEntity> taskUserTaskCommentReplyReactionEntityList = this.taskUserTaskCommentReplyReactionRepository
                .findByIdTaskCommentReplyIdUser(idTaskCommentReply, idUser);
        if (taskUserTaskCommentReplyReactionEntityList.size() > 0) {
            TaskUserTaskCommentReplyReactionEntity taskUserTaskCommentReplyReactionEntity = taskUserTaskCommentReplyReactionEntityList
                    .get(0);
            if (reactionType.equals(taskUserTaskCommentReplyReactionEntity.getReactionType())) {
                this.taskUserTaskCommentReplyReactionRepository.delete(taskUserTaskCommentReplyReactionEntity);
            } else {
                taskUserTaskCommentReplyReactionEntity.setReactionType(reactionType);
                this.taskUserTaskCommentReplyReactionRepository.save(taskUserTaskCommentReplyReactionEntity);
            }
        } else {
            utilities.ensureNoEntityExists(this.taskUserTaskCommentReplyReactionRepository.findByIdTaskCommentReplyIdUser(idTaskCommentReply, idUser).size());
            TaskUserTaskCommentReplyReactionEntity taskUserTaskCommentReplyReactionEntity = new TaskUserTaskCommentReplyReactionEntity();
            taskUserTaskCommentReplyReactionEntity.setIdTaskCommentReply(UUID.fromString(idTaskCommentReply));
            taskUserTaskCommentReplyReactionEntity.setIdUser(UUID.fromString(idUser));
            taskUserTaskCommentReplyReactionEntity.setReactionType(reactionType);
            taskUserTaskCommentReplyReactionEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            this.taskUserTaskCommentReplyReactionRepository.save(taskUserTaskCommentReplyReactionEntity);
        }
    }

    /**
     * id
     * ?text
     * ?imageAsBase64String
     */
    public void createTaskComment(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTask = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeCreateTaskComment(idUser, idTask);
        if (rb.has("text")) {
            String commentText = RequestBodyValidator.stringOptional(RequestBodyFormatter.fString(rb.get("text")),
                    Limits.TASK_COMMENT_TEXT_MIN_LENGTH_CHARACTERS, Limits.TASK_COMMENT_TEXT_MAX_LENGTH_CHARACTERS);
            TaskUserTaskCommentEntity taskUserTaskCommentEntity = new TaskUserTaskCommentEntity();
            taskUserTaskCommentEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            taskUserTaskCommentEntity.setIdTask(UUID.fromString(idTask));
            taskUserTaskCommentEntity.setIdUser(UUID.fromString(idUser));
            taskUserTaskCommentEntity.setText(true);
            taskUserTaskCommentEntity.setCommentText(commentText);
            this.taskUserTaskCommentRepository.save(taskUserTaskCommentEntity);
        }
        if (rb.has("imageAsBase64String")) {
            String commentImageAsBase64String = RequestBodyValidator
                    .imageOptional(RequestBodyFormatter.fString(rb.get("imageAsBase64String")));
            TaskUserTaskCommentEntity taskUserTaskCommentEntity = new TaskUserTaskCommentEntity();
            taskUserTaskCommentEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            taskUserTaskCommentEntity.setIdTask(UUID.fromString(idTask));
            taskUserTaskCommentEntity.setIdUser(UUID.fromString(idUser));
            taskUserTaskCommentEntity.setImage(true);
            UUID taskCommentImageKeyId = UUID.randomUUID();
            byte[] imageAsByteArray = java.util.Base64.getDecoder()
                    .decode(commentImageAsBase64String.substring(commentImageAsBase64String.indexOf(",") + 1));
            RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
            utilities.putObjectInS3(Constants.S3_FOLDER_NAME_TASK_COMMENT_IMAGE, taskCommentImageKeyId,
                    imageAsByteArrayRequestBody);
            taskUserTaskCommentEntity.setIdCommentImageKey(taskCommentImageKeyId);
            this.taskUserTaskCommentRepository.save(taskUserTaskCommentEntity);
        }
    }

    /**
     * id
     * text
     * imageAsBase64String
     */
    public void createTaskCommentReply(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTaskComment = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeCreateTaskCommentReply(idUser, idTaskComment);
        if (rb.has("text")) {
            String commentReplyText = RequestBodyValidator.stringOptional(
                    RequestBodyFormatter.fString(rb.get("text")), Limits.TASK_COMMENT_TEXT_MIN_LENGTH_CHARACTERS,
                    Limits.TASK_COMMENT_TEXT_MAX_LENGTH_CHARACTERS);
            TaskUserTaskCommentReplyEntity taskUserTaskCommentReplyEntity = new TaskUserTaskCommentReplyEntity();
            taskUserTaskCommentReplyEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            taskUserTaskCommentReplyEntity.setIdTaskComment(UUID.fromString(idTaskComment));
            taskUserTaskCommentReplyEntity.setIdUser(UUID.fromString(idUser));
            taskUserTaskCommentReplyEntity.setText(true);
            taskUserTaskCommentReplyEntity.setCommentReplyText(commentReplyText);
            this.taskUserTaskCommentReplyRepository.save(taskUserTaskCommentReplyEntity);
        }
        if (rb.has("imageAsBase64String")) {
            String commentReplyImageAsBase64String = RequestBodyValidator
                    .imageOptional(RequestBodyFormatter.fString(rb.get("imageAsBase64String")));
            TaskUserTaskCommentReplyEntity taskUserTaskCommentReplyEntity = new TaskUserTaskCommentReplyEntity();
            taskUserTaskCommentReplyEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            taskUserTaskCommentReplyEntity.setIdTaskComment(UUID.fromString(idTaskComment));
            taskUserTaskCommentReplyEntity.setIdUser(UUID.fromString(idUser));
            taskUserTaskCommentReplyEntity.setImage(true);
            UUID taskCommentImageKeyId = UUID.randomUUID();
            byte[] imageAsByteArray = java.util.Base64.getDecoder().decode(
                    commentReplyImageAsBase64String.substring(commentReplyImageAsBase64String.indexOf(",") + 1));
            RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
            utilities.putObjectInS3(Constants.S3_FOLDER_NAME_TASK_COMMENT_REPLY_IMAGE, taskCommentImageKeyId,
                    imageAsByteArrayRequestBody);
            taskUserTaskCommentReplyEntity.setIdCommentReplyImageKey(taskCommentImageKeyId);
            this.taskUserTaskCommentReplyRepository.save(taskUserTaskCommentReplyEntity);
        }
    }

    /**
     * id
     */
    public ReactionsModel getTaskReactions(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTask = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetTaskReactions(idUser, idTask);
        return this._getTaskReactions(rb);
    }

    /**
     * id
     */
    public ReactionsModel getTaskReactionsSample(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTask = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetTaskReactionsSample(idUser, idTask);
        ReactionsModel reactionsModel = this._getTaskReactions(rb);
        reactionsModel.setUserBubblesReaction(
            reactionsModel.getUserBubblesReaction().subList(
                0, 
                Math.min(
                    reactionsModel.getUserBubblesReaction().size(), 
                    3
                )
            )
        );
        return reactionsModel;
    }

    /**
     * id
     */
    public ReactionsModel _getTaskReactions(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTask = RequestBodyFormatter.fString(rb.get("id"));

        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleReactionModel> userBubblesReactionTask = this.taskRepository.getUserBubblesReactionTask(idTask)
                .stream().map(userBubbleReactionTaskReactionDTO -> {
                    UserEntity userEntityOfReaction = this.userRepository
                            .findById(userBubbleReactionTaskReactionDTO.getIdUser()).get();
                    return UserBubbleReactionModel.builder()
                            .id(userEntityOfReaction.getId())
                            .name(userEntityOfReaction.getName())
                            .username(userEntityOfReaction.getUsername())
                            .imageLink(utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                                    userEntityOfReaction.getIdImageKey()))
                            .timestampToSortBy(userBubbleReactionTaskReactionDTO.getTimestampToSortBy())
                            .datetimeDateOnlyLabel(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                    userBubbleReactionTaskReactionDTO.getTimestampToSortBy()))
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    userBubbleReactionTaskReactionDTO.getTimestampToSortBy()))
                            .isFollowedByMe(
                                    userIdsFollowedByGivenUser.contains(userBubbleReactionTaskReactionDTO.getIdUser()))
                            .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted
                                    .contains(userBubbleReactionTaskReactionDTO.getIdUser()))
                            .isMe(idUser.equals(userBubbleReactionTaskReactionDTO.getIdUser().toString()))
                            .reactionType(userBubbleReactionTaskReactionDTO.getReactionType())
                            .build();
                }).collect(Collectors.toList());
        Collections.sort(userBubblesReactionTask, new Comparator<UserBubbleReactionModel>() {
            public int compare(UserBubbleReactionModel userBubbleReaction1,
                    UserBubbleReactionModel userBubbleReaction2) {
                if (userBubbleReaction1.isMe() == true && userBubbleReaction2.isMe() == false) {
                    return -1;
                } else if (userBubbleReaction1.isMe() == false && userBubbleReaction2.isMe() == true) {
                    return 1;
                }
                if (userBubbleReaction1.isFollowedByMe() == true && userBubbleReaction2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubbleReaction1.isFollowedByMe() == false
                        && userBubbleReaction2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleReaction1.getUsername().compareTo(userBubbleReaction2.getUsername());
            }
        });
        String myReactionTask = this.taskUserTaskReactionRepository.findByIdTaskIdUser(idTask, idUser).size() == 0
                ? null
                : this.taskUserTaskReactionRepository.findByIdTaskIdUser(idTask, idUser).get(0).getReactionType();
        return ReactionsModel.builder()
                .idReactionTargetEntity(idTask)
                .userBubblesReaction(userBubblesReactionTask)
                .userBubblesReactionTotalNumber(userBubblesReactionTask.size())
                .myReactionType(myReactionTask)
                .build();
    }

    /**
     * id
     */
    public ReactionsModel getTaskCommentReactions(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTaskComment = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetTaskCommentReactions(idUser, idTaskComment);
        return this._getTaskCommentReactions(rb);
    }

    /**
     * id
     */
    public ReactionsModel getTaskCommentReactionsSample(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTaskComment = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetTaskCommentReactionsSample(idUser, idTaskComment);
        ReactionsModel reactionsModel = this._getTaskCommentReactions(rb);
        reactionsModel.setUserBubblesReaction(
            reactionsModel.getUserBubblesReaction().subList(
                0, 
                Math.min(
                    reactionsModel.getUserBubblesReaction().size(), 
                    3
                )
            )
        );
        return reactionsModel;
    }

    /**
     * id
     */
    public ReactionsModel _getTaskCommentReactions(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTaskComment = RequestBodyFormatter.fString(rb.get("id"));

        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleReactionModel> userBubblesReactionTaskComment = this.taskRepository
                .getUserBubblesReactionTaskComment(idTaskComment).stream().map(userBubbleReactionTaskReactionDTO -> {
                    UserEntity userEntityOfReaction = this.userRepository
                            .findById(userBubbleReactionTaskReactionDTO.getIdUser()).get();
                    return UserBubbleReactionModel.builder()
                            .id(userEntityOfReaction.getId())
                            .name(userEntityOfReaction.getName())
                            .username(userEntityOfReaction.getUsername())
                            .imageLink(utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                                    userEntityOfReaction.getIdImageKey()))
                            .timestampToSortBy(userBubbleReactionTaskReactionDTO.getTimestampToSortBy())
                            .datetimeDateOnlyLabel(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                    userBubbleReactionTaskReactionDTO.getTimestampToSortBy()))
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    userBubbleReactionTaskReactionDTO.getTimestampToSortBy()))
                            .isFollowedByMe(
                                    userIdsFollowedByGivenUser.contains(userBubbleReactionTaskReactionDTO.getIdUser()))
                            .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted
                                    .contains(userBubbleReactionTaskReactionDTO.getIdUser()))
                            .isMe(idUser.equals(userBubbleReactionTaskReactionDTO.getIdUser().toString()))
                            .reactionType(userBubbleReactionTaskReactionDTO.getReactionType())
                            .build();
                }).collect(Collectors.toList());
        Collections.sort(userBubblesReactionTaskComment, new Comparator<UserBubbleReactionModel>() {
            public int compare(UserBubbleReactionModel userBubbleReaction1,
                    UserBubbleReactionModel userBubbleReaction2) {
                if (userBubbleReaction1.isMe() == true && userBubbleReaction2.isMe() == false) {
                    return -1;
                } else if (userBubbleReaction1.isMe() == false && userBubbleReaction2.isMe() == true) {
                    return 1;
                }
                if (userBubbleReaction1.isFollowedByMe() == true && userBubbleReaction2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubbleReaction1.isFollowedByMe() == false
                        && userBubbleReaction2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleReaction1.getUsername().compareTo(userBubbleReaction2.getUsername());
            }
        });
        String myReactionTaskComment = this.taskUserTaskCommentReactionRepository
                .findByIdTaskCommentIdUser(idTaskComment, idUser).size() == 0 ? null
                        : this.taskUserTaskCommentReactionRepository.findByIdTaskCommentIdUser(idTaskComment, idUser)
                                .get(0).getReactionType();
        return ReactionsModel.builder()
                .idReactionTargetEntity(idTaskComment)
                .userBubblesReaction(userBubblesReactionTaskComment)
                .myReactionType(myReactionTaskComment)
                .build();
    }

    /**
     * id
     */
    public ReactionsModel getTaskCommentReplyReactions(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTaskCommentReply = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetTaskCommentReplyReactions(idUser, idTaskCommentReply);
        return this._getTaskCommentReplyReactions(rb);
    }

    /**
     * id
     */
    public ReactionsModel getTaskCommentReplyReactionsSample(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTaskCommentReply = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetTaskCommentReplyReactionsSample(idUser, idTaskCommentReply);
        ReactionsModel reactionsModel = this._getTaskCommentReplyReactions(rb);
        reactionsModel.setUserBubblesReaction(
            reactionsModel.getUserBubblesReaction().subList(
                0, 
                Math.min(
                    reactionsModel.getUserBubblesReaction().size(), 
                    3
                )
            )
        );
        return reactionsModel;
    }

    /**
     * id
     */
    public ReactionsModel _getTaskCommentReplyReactions(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTaskCommentReply = RequestBodyFormatter.fString(rb.get("id"));

        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleReactionModel> userBubblesReactionTaskCommentReply = this.taskRepository
                .getUserBubblesReactionTaskCommentReply(idTaskCommentReply).stream()
                .map(userBubbleReactionTaskReplyReactionDTO -> {
                    UserEntity userEntityOfReaction = this.userRepository
                            .findById(userBubbleReactionTaskReplyReactionDTO.getIdUser()).get();
                    return UserBubbleReactionModel.builder()
                            .id(userEntityOfReaction.getId())
                            .name(userEntityOfReaction.getName())
                            .username(userEntityOfReaction.getUsername())
                            .imageLink(utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                                    userEntityOfReaction.getIdImageKey()))
                            .timestampToSortBy(userBubbleReactionTaskReplyReactionDTO.getTimestampToSortBy())
                            .datetimeDateOnlyLabel(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                    userBubbleReactionTaskReplyReactionDTO.getTimestampToSortBy()))
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    userBubbleReactionTaskReplyReactionDTO.getTimestampToSortBy()))
                            .isFollowedByMe(
                                    userIdsFollowedByGivenUser.contains(userBubbleReactionTaskReplyReactionDTO.getId()))
                            .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted
                                    .contains(userBubbleReactionTaskReplyReactionDTO.getId()))
                            .isMe(idUser.equals(userBubbleReactionTaskReplyReactionDTO.getId().toString()))
                            .reactionType(userBubbleReactionTaskReplyReactionDTO.getReactionType())
                            .build();
                }).collect(Collectors.toList());
        Collections.sort(userBubblesReactionTaskCommentReply, new Comparator<UserBubbleReactionModel>() {
            public int compare(UserBubbleReactionModel userBubbleReaction1,
                    UserBubbleReactionModel userBubbleReaction2) {
                if (userBubbleReaction1.isMe() == true && userBubbleReaction2.isMe() == false) {
                    return -1;
                } else if (userBubbleReaction1.isMe() == false && userBubbleReaction2.isMe() == true) {
                    return 1;
                }
                if (userBubbleReaction1.isFollowedByMe() == true && userBubbleReaction2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubbleReaction1.isFollowedByMe() == false
                        && userBubbleReaction2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleReaction1.getUsername().compareTo(userBubbleReaction2.getUsername());
            }
        });
        String myReactionTaskCommentReply = this.taskUserTaskCommentReplyReactionRepository
                .findByIdTaskCommentReplyIdUser(idTaskCommentReply, idUser).size() == 0 ? null
                        : this.taskUserTaskCommentReplyReactionRepository
                                .findByIdTaskCommentReplyIdUser(idTaskCommentReply, idUser).get(0).getReactionType();
        return ReactionsModel.builder()
                .idReactionTargetEntity(idTaskCommentReply)
                .userBubblesReaction(userBubblesReactionTaskCommentReply)
                .myReactionType(myReactionTaskCommentReply)
                .build();
    }

    /**
     * id
     */
    public List<TaskCommentModel> getTaskComments(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTask = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetTaskComments(idUser, idTask);

        // get reactions
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<TaskCommentModel> taskCommentModelList = this.taskRepository.getTaskComments(idTask).stream()
                .map(taskCommentDTO -> {
                    String idTaskComment = taskCommentDTO.getId().toString();
                    UserEntity taskCommentModelUserEntity = this.userRepository.findById(taskCommentDTO.getIdUser())
                            .get();
                    // get reactions
                    List<UserBubbleReactionModel> userBubblesReactionTaskComment = this.taskRepository
                            .getUserBubblesReactionTaskComment(idTaskComment).stream()
                            .map(userBubbleReactionTaskReactionDTO -> {
                                UserEntity userEntityOfReaction = this.userRepository
                                        .findById(userBubbleReactionTaskReactionDTO.getIdUser()).get();
                                return UserBubbleReactionModel.builder()
                                        .id(userEntityOfReaction.getId())
                                        .name(userEntityOfReaction.getName())
                                        .username(userEntityOfReaction.getUsername())
                                        .imageLink(utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                                                userEntityOfReaction.getIdImageKey()))
                                        .timestampToSortBy(userBubbleReactionTaskReactionDTO.getTimestampToSortBy())
                                        .datetimeDateOnlyLabel(
                                                utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                                        userBubbleReactionTaskReactionDTO.getTimestampToSortBy()))
                                        .datetimeDateAndTimeLabel(
                                                utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                                        userBubbleReactionTaskReactionDTO.getTimestampToSortBy()))
                                        .isFollowedByMe(userIdsFollowedByGivenUser
                                                .contains(userBubbleReactionTaskReactionDTO.getIdUser()))
                                        .isFollowRequestSentNotYetAccepted(
                                                userIds_FollowRequestSentByGivenUser_NotYetAccepted
                                                        .contains(userBubbleReactionTaskReactionDTO.getIdUser()))
                                        .isMe(idUser.equals(userBubbleReactionTaskReactionDTO.getIdUser().toString()))
                                        .reactionType(userBubbleReactionTaskReactionDTO.getReactionType())
                                        .build();
                            }).collect(Collectors.toList());
                    Collections.sort(userBubblesReactionTaskComment, new Comparator<UserBubbleReactionModel>() {
                        public int compare(UserBubbleReactionModel userBubbleReaction1,
                                UserBubbleReactionModel userBubbleReaction2) {
                            if (userBubbleReaction1.isMe() == true && userBubbleReaction2.isMe() == false) {
                                return -1;
                            } else if (userBubbleReaction1.isMe() == false && userBubbleReaction2.isMe() == true) {
                                return 1;
                            }
                            if (userBubbleReaction1.isFollowedByMe() == true
                                    && userBubbleReaction2.isFollowedByMe() == false) {
                                return -1;
                            } else if (userBubbleReaction1.isFollowedByMe() == false
                                    && userBubbleReaction2.isFollowedByMe() == true) {
                                return 1;
                            }
                            return userBubbleReaction1.getUsername().compareTo(userBubbleReaction2.getUsername());
                        }
                    });
                    String myReactionTaskComment = this.taskUserTaskCommentReactionRepository
                            .findByIdTaskCommentIdUser(idTaskComment, idUser).size() == 0 ? null
                                    : this.taskUserTaskCommentReactionRepository
                                            .findByIdTaskCommentIdUser(idTaskComment, idUser).get(0).getReactionType();
                    ReactionsModel taskCommentReactions = ReactionsModel.builder()
                            .idReactionTargetEntity(idTaskComment)
                            .userBubblesReaction(userBubblesReactionTaskComment.subList(0,
                                    Math.min(userBubblesReactionTaskComment.size(), 3)))
                            .userBubblesReactionTotalNumber(userBubblesReactionTaskComment.size())
                            .myReactionType(myReactionTaskComment)
                            .build();
                    return TaskCommentModel.builder()
                            .idTaskComment(taskCommentDTO.getId().toString())
                            .idUser(taskCommentModelUserEntity.getId().toString())
                            .username(taskCommentModelUserEntity.getUsername())
                            .userImageLink(utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                                    taskCommentModelUserEntity.getIdImageKey()))
                            .timestampToSortBy(taskCommentDTO.getTimestampToSortBy())
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    taskCommentDTO.getTimestampToSortBy()))
                            .isText(taskCommentDTO.isText())
                            .commentText(taskCommentDTO.getCommentText())
                            .isImage(taskCommentDTO.isImage())
                            .commentImageLink(utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_TASK_COMMENT_IMAGE,
                                    taskCommentDTO.getIdTaskCommentImageKey()))
                            .numberOfReplies(this.taskRepository.getTaskCommentReplies(idTaskComment).size())
                            .reactions(taskCommentReactions)
                            .build();
                }).collect(Collectors.toList());

        Collections.sort(taskCommentModelList, new Comparator<TaskCommentModel>() {
            public int compare(TaskCommentModel taskCommentModel1, TaskCommentModel taskCommentModel2) {
                if (taskCommentModel1.getTimestampToSortBy() > taskCommentModel2.getTimestampToSortBy()) {
                    return 1;
                } else if (taskCommentModel1.getTimestampToSortBy() < taskCommentModel2.getTimestampToSortBy()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return taskCommentModelList;
    }

    /**
     * id
     */
    public List<TaskCommentReplyModel> getTaskCommentReplies(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idTaskComment = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetTaskCommentReplies(idUser, idTaskComment);

        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);

        List<TaskCommentReplyModel> taskCommentReplyModelList = this.taskRepository.getTaskCommentReplies(idTaskComment)
                .stream().map(taskCommentReplyDTO -> {
                    String idTaskCommentReply = taskCommentReplyDTO.getId().toString();
                    UserEntity taskCommentReplyUserEntity = this.userRepository
                            .findById(taskCommentReplyDTO.getIdUser()).get();

                    List<UserBubbleReactionModel> userBubblesReactionTaskCommentReply = this.taskRepository
                            .getUserBubblesReactionTaskCommentReply(idTaskCommentReply).stream()
                            .map(userBubbleReactionTaskReplyReactionDTO -> {
                                UserEntity userEntityOfReaction = this.userRepository
                                        .findById(userBubbleReactionTaskReplyReactionDTO.getIdUser()).get();
                                return UserBubbleReactionModel.builder()
                                        .id(userEntityOfReaction.getId())
                                        .name(userEntityOfReaction.getName())
                                        .username(userEntityOfReaction.getUsername())
                                        .imageLink(utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                                                userEntityOfReaction.getIdImageKey()))
                                        .timestampToSortBy(
                                                userBubbleReactionTaskReplyReactionDTO.getTimestampToSortBy())
                                        .datetimeDateOnlyLabel(
                                                utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                                        userBubbleReactionTaskReplyReactionDTO.getTimestampToSortBy()))
                                        .datetimeDateAndTimeLabel(
                                                utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                                        userBubbleReactionTaskReplyReactionDTO.getTimestampToSortBy()))
                                        .isFollowedByMe(userIdsFollowedByGivenUser
                                                .contains(userBubbleReactionTaskReplyReactionDTO.getId()))
                                        .isFollowRequestSentNotYetAccepted(
                                                userIds_FollowRequestSentByGivenUser_NotYetAccepted
                                                        .contains(userBubbleReactionTaskReplyReactionDTO.getId()))
                                        .isMe(idUser.equals(userBubbleReactionTaskReplyReactionDTO.getId().toString()))
                                        .reactionType(userBubbleReactionTaskReplyReactionDTO.getReactionType())
                                        .build();
                            }).collect(Collectors.toList());
                    Collections.sort(userBubblesReactionTaskCommentReply, new Comparator<UserBubbleReactionModel>() {
                        public int compare(UserBubbleReactionModel userBubbleReaction1,
                                UserBubbleReactionModel userBubbleReaction2) {
                            if (userBubbleReaction1.isMe() == true && userBubbleReaction2.isMe() == false) {
                                return -1;
                            } else if (userBubbleReaction1.isMe() == false && userBubbleReaction2.isMe() == true) {
                                return 1;
                            }
                            if (userBubbleReaction1.isFollowedByMe() == true
                                    && userBubbleReaction2.isFollowedByMe() == false) {
                                return -1;
                            } else if (userBubbleReaction1.isFollowedByMe() == false
                                    && userBubbleReaction2.isFollowedByMe() == true) {
                                return 1;
                            }
                            return userBubbleReaction1.getUsername().compareTo(userBubbleReaction2.getUsername());
                        }
                    });
                    String myReactionTaskCommentReply = this.taskUserTaskCommentReplyReactionRepository
                            .findByIdTaskCommentReplyIdUser(idTaskCommentReply, idUser).size() == 0
                                    ? null
                                    : this.taskUserTaskCommentReplyReactionRepository
                                            .findByIdTaskCommentReplyIdUser(idTaskCommentReply, idUser).get(0)
                                            .getReactionType();
                    ReactionsModel taskCommentReplyReactions = ReactionsModel.builder()
                            .idReactionTargetEntity(idTaskCommentReply)
                            .userBubblesReaction(userBubblesReactionTaskCommentReply.subList(0,
                                    Math.min(userBubblesReactionTaskCommentReply.size(), 3)))
                            .userBubblesReactionTotalNumber(userBubblesReactionTaskCommentReply.size())
                            .myReactionType(myReactionTaskCommentReply)
                            .build();
                    return TaskCommentReplyModel.builder()
                            .idTaskCommentReply(idTaskCommentReply)
                            .idUser(taskCommentReplyUserEntity.getId().toString())
                            .username(taskCommentReplyUserEntity.getUsername())
                            .userImageLink(utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                                    taskCommentReplyUserEntity.getIdImageKey()))
                            .timestampToSortBy(taskCommentReplyDTO.getTimestampToSortBy())
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    taskCommentReplyDTO.getTimestampToSortBy()))
                            .isText(taskCommentReplyDTO.isText())
                            .commentReplyText(taskCommentReplyDTO.getCommentText())
                            .isImage(taskCommentReplyDTO.isImage())
                            .commentReplyImageLink(
                                    utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_TASK_COMMENT_REPLY_IMAGE,
                                            taskCommentReplyDTO.getIdTaskCommentImageKey()))
                            .reactions(taskCommentReplyReactions)
                            .build();
                }).collect(Collectors.toList());

        Collections.sort(taskCommentReplyModelList, new Comparator<TaskCommentReplyModel>() {
            public int compare(TaskCommentReplyModel taskCommentReply1, TaskCommentReplyModel taskCommentReply2) {
                if (taskCommentReply1.getTimestampToSortBy() > taskCommentReply2.getTimestampToSortBy()) {
                    return 1;
                } else if (taskCommentReply1.getTimestampToSortBy() < taskCommentReply2.getTimestampToSortBy()) {
                    return -1;
                } else {
                    return 0;
                }
            }
        });
        return taskCommentReplyModelList;
    }

    /**
     * id
     */
    public StampPageModel getStampPage(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idStamp = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetStampPage(idUser, idStamp);
        return this._getStampPage(idUser, idStamp);
    }

    public StampPageModel _getStampPage(String idUser, String idStamp) throws Exception {
        StampEntity stampEntity = this.stampRepository.findById(UUID.fromString(idStamp)).get();
        String stampImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_STAMP_IMAGE,
                stampEntity.getIdImageKey());

        List<UserBubbleModel> userBubblesStampCollect = this._getUserBubblesStampCollect(idUser, idStamp);

        boolean isEligibleToBeCollectedByMe = true;
        List<UUID> stampTaskIds = this.stampTaskStampHasTaskRepository.findByIdStamp(idStamp).stream()
                .map(stampTaskStampHasTaskEntity -> stampTaskStampHasTaskEntity.getIdTask())
                .collect(Collectors.toList());
        for (int i = 0; i < stampTaskIds.size(); i++) {
            boolean isTaskCompleteByMe = this.taskUserTaskCompleteRepository
                    .findByIdTaskIdUser(stampTaskIds.get(i).toString(), idUser).size() > 0;
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
                .userBubblesStampCollect(
                        userBubblesStampCollect.subList(0, Math.min(userBubblesStampCollect.size(), 3)))
                .userBubblesStampCollectTotalNumber(userBubblesStampCollect.size())
                .isCollectedByMe(
                        this.stampUserUserCollectStampRepository.findByIdStampIdUser(idStamp, idUser).size() > 0)
                .isEligibleToBeCollectedByMe(isEligibleToBeCollectedByMe)
                .build();
    }

    /**
     * id
     */
    public List<UserBubbleModel> getUserBubblesStampCollect(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idStamp = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetUserBubblesStampCollect(idUser, idStamp);
        return this._getUserBubblesStampCollect(idUser, idStamp);
    }

    public List<UserBubbleModel> _getUserBubblesStampCollect(String idUser, String idStamp) throws Exception {
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesStampCollect = this.userRepository.getUserBubblesStampCollect(idStamp).stream()
                .map(userBubbleDTOStampCollect -> {
                    String userBubbleImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                            userBubbleDTOStampCollect.getIdImageKey());
                    return UserBubbleModel.builder()
                            .id(userBubbleDTOStampCollect.getId())
                            .name(userBubbleDTOStampCollect.getName())
                            .username(userBubbleDTOStampCollect.getUsername())
                            .imageLink(userBubbleImageLink)
                            .timestampToSortBy(userBubbleDTOStampCollect.getTimestampToSortBy())
                            .datetimeDateOnlyLabel(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTOStampCollect.getTimestampToSortBy()))
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTOStampCollect.getTimestampToSortBy()))
                            .isFollowedByMe(userIdsFollowedByGivenUser.contains(userBubbleDTOStampCollect.getId()))
                            .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByGivenUser_NotYetAccepted
                                    .contains(userBubbleDTOStampCollect.getId()))
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
                if (userBubbleStampCollect1.isFollowedByMe() == true
                        && userBubbleStampCollect2.isFollowedByMe() == false) {
                    return -1;
                } else if (userBubbleStampCollect1.isFollowedByMe() == false
                        && userBubbleStampCollect2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleStampCollect1.getUsername().compareTo(userBubbleStampCollect2.getUsername());
            }
        });
        return userBubblesStampCollect;
    }

    /**
     * filterByName
     * filterIsPublic
     * filterIsNotPublic
     * filterIsCollect
     * filterIsNotCollect
     */
    public StampCardsPaginatedModel getStampCardsDiscover(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetStampCardsDiscover(idUser);
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "filterByName",
                        "filterIsPublic",
                        "filterIsNotPublic",
                        "filterIsCollect",
                        "filterIsNotCollect",
                        "paginationIdxStart",
                        "paginationN"));
        String filterByName = RequestBodyFormatter.fStringLowercase(rb.get("filterByName"));
        boolean filterIsPublic = RequestBodyFormatter.fBoolean(rb.get("filterIsPublic"));
        boolean filterIsNotPublic = RequestBodyFormatter.fBoolean(rb.get("filterIsNotPublic"));
        boolean filterIsCollect = RequestBodyFormatter.fBoolean(rb.get("filterIsCollect"));
        boolean filterIsNotCollect = RequestBodyFormatter.fBoolean(rb.get("filterIsNotCollect"));
        int paginationIdxStart = RequestBodyFormatter.fInt(rb.get("paginationIdxStart"));
        int paginationN = RequestBodyFormatter.fInt(rb.get("paginationN"));

        List<StampCardSharedPropertiesDTO> stampCardsDiscover_SharedProperties = this.stampRepository
                .getStampCardsDiscover_SharedProperties(filterByName);
        List<StampCardIndividualPropertiesDTO> stampCardsDiscover_IndividualProperties = this.stampRepository
                .getStampCardsDiscover_IndividualProperties(idUser, filterByName);
        StampCardsPaginatedModel stampCardsPaginatedModel = this._getStampCardModelFromSharedAndIndividualProperties(
                idUser,
                stampCardsDiscover_SharedProperties,
                stampCardsDiscover_IndividualProperties,
                filterIsPublic,
                filterIsNotPublic,
                filterIsCollect,
                filterIsNotCollect,
                paginationIdxStart,
                paginationN
                );
        return stampCardsPaginatedModel;
    }

    /**
     * id
     * filterByName
     * filterIsPublic
     * filterIsNotPublic
     * filterIsCollect
     * filterIsNotCollect
     * paginationIdxStart
     * paginationN
     */
    public StampCardsPaginatedModel getStampCardsAssociatedWithPod(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "filterByName",
                        "filterIsPublic",
                        "filterIsNotPublic",
                        "filterIsCollect",
                        "filterIsNotCollect",
                        "paginationIdxStart",
                        "paginationN"));
        String idPod = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeGetStampCardsAssociatedWithPod(idUser, idPod);
        String filterByName = RequestBodyFormatter.fStringLowercase(rb.get("filterByName"));
        boolean filterIsPublic = RequestBodyFormatter.fBoolean(rb.get("filterIsPublic"));
        boolean filterIsNotPublic = RequestBodyFormatter.fBoolean(rb.get("filterIsNotPublic"));
        boolean filterIsCollect = RequestBodyFormatter.fBoolean(rb.get("filterIsCollect"));
        boolean filterIsNotCollect = RequestBodyFormatter.fBoolean(rb.get("filterIsNotCollect"));
        int paginationIdxStart = RequestBodyFormatter.fInt(rb.get("paginationIdxStart"));
        int paginationN = RequestBodyFormatter.fInt(rb.get("paginationN"));

        List<StampCardSharedPropertiesDTO> stampCardsAssociatedWithPod_SharedProperties = this.stampRepository
                .getStampCardsAssociatedWithPod_SharedProperties(idPod, filterByName);
        List<StampCardIndividualPropertiesDTO> stampCardsAssociatedWithPod_IndividualProperties = this.stampRepository
                .getStampCardsAssociatedWithPod_IndividualProperties(idPod, idUser, filterByName);
        StampCardsPaginatedModel stampCardsPaginatedModel = this._getStampCardModelFromSharedAndIndividualProperties(
                idUser,
                stampCardsAssociatedWithPod_SharedProperties,
                stampCardsAssociatedWithPod_IndividualProperties,
                filterIsPublic,
                filterIsNotPublic,
                filterIsCollect,
                filterIsNotCollect,
                paginationIdxStart,
                paginationN
                );
        return stampCardsPaginatedModel;
    }

    /**
     * id
     * filterByName
     * filterIsPublic
     * filterIsNotPublic
     * filterIsCollect
     * filterIsNotCollect
     * paginationIdxStart
     * paginationN
     */
    public StampCardsPaginatedModel getStampCardsAssociatedWithUser(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetStampCardsAssociatedWithUser(idUser);
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id",
                        "filterByName",
                        "filterIsPublic",
                        "filterIsNotPublic",
                        "filterIsCollect",
                        "filterIsNotCollect",
                        "paginationIdxStart",
                        "paginationN"));
        String idUserProfile = RequestBodyFormatter.fString(rb.get("id"));
        String filterByName = RequestBodyFormatter.fStringLowercase(rb.get("filterByName"));
        boolean filterIsPublic = RequestBodyFormatter.fBoolean(rb.get("filterIsPublic"));
        boolean filterIsNotPublic = RequestBodyFormatter.fBoolean(rb.get("filterIsNotPublic"));
        boolean filterIsCollect = RequestBodyFormatter.fBoolean(rb.get("filterIsCollect"));
        boolean filterIsNotCollect = RequestBodyFormatter.fBoolean(rb.get("filterIsNotCollect"));
        int paginationIdxStart = RequestBodyFormatter.fInt(rb.get("paginationIdxStart"));
        int paginationN = RequestBodyFormatter.fInt(rb.get("paginationN"));

        List<StampCardSharedPropertiesDTO> stampCardsAssociatedWithPod_SharedProperties = this.stampRepository
                .getStampCardsAssociatedWithUser_SharedProperties(filterByName);
        List<StampCardIndividualPropertiesDTO> stampCardsAssociatedWithPod_IndividualProperties = this.stampRepository
                .getStampCardsAssociatedWithUser_IndividualProperties(idUser, filterByName);

        // need to filter for stamps that profile user has actually collected AFTER
        // query above because calculation numberOfUsersCollect requires this
        Set<UUID> stampIdsProfileUserHasCollected = new HashSet<>(
                this.stampRepository.getStampIdsUserHasCollected(idUserProfile));
        stampCardsAssociatedWithPod_SharedProperties = stampCardsAssociatedWithPod_SharedProperties.stream()
                .filter(stampCard -> {
                    return stampIdsProfileUserHasCollected.contains(stampCard.getId());
                }).collect(Collectors.toList());
        stampCardsAssociatedWithPod_IndividualProperties = stampCardsAssociatedWithPod_IndividualProperties.stream()
                .filter(stampCard -> {
                    return stampIdsProfileUserHasCollected.contains(stampCard.getId());
                }).collect(Collectors.toList());

        StampCardsPaginatedModel stampCardsPaginatedModel = this._getStampCardModelFromSharedAndIndividualProperties(
                idUser,
                stampCardsAssociatedWithPod_SharedProperties,
                stampCardsAssociatedWithPod_IndividualProperties,
                filterIsPublic,
                filterIsNotPublic,
                filterIsCollect,
                filterIsNotCollect,
                paginationIdxStart,
                paginationN
        );
        return stampCardsPaginatedModel;
    }

    /**
     * name
     * idTasks
     * ?description
     */
    public StampPageModel createStamp(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeCreateStamp(idUser);
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "name",
                        "idTasks"));
        String stampName = RequestBodyValidator.stringRequired(RequestBodyFormatter.fString(rb.get("name")),
                Limits.POD_NAME_MIN_LENGTH_CHARACTERS, Limits.POD_NAME_MAX_LENGTH_CHARACTERS);
        List<String> stampIdTasks = new ArrayList<>();
        for (int i = 0; i < rb.get("idTasks").size(); i++) {
            String stampIdTask = RequestBodyFormatter.fString(rb.get("idTasks").get(i));
            stampIdTasks.add(stampIdTask);
        }
        if (stampIdTasks.size() >= Limits.LIMIT_NUMBER_OF_TOTAL_TASKS_STAMP) {
            throw new Exception("EXCEEDED_LIMIT_NUMBER_OF_TOTAL_TASKS_STAMP");
        }

        if (this.stampRepository.findByNameLowerCase(stampName).size() > 0) {
            throw new Exception("STAMP_DUPLICATE_NAME");
        }
        StampEntity stampEntity = new StampEntity();
        stampEntity.setIdUserCreate(UUID.fromString(idUser));
        stampEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
        stampEntity.setName(stampName);
        if (rb.has("description")) {
            String description = RequestBodyValidator.stringOptional(
                    RequestBodyFormatter.fString(rb.get("description")),
                    Limits.STAMP_DESCRIPTION_MIN_LENGTH_CHARACTERS, Limits.STAMP_DESCRIPTION_MAX_LENGTH_CHARACTERS);
            stampEntity.setDescription(description);
        }
        this.stampRepository.save(stampEntity);
        // needs to save this required field after because of foreign key constraint
        // (stamp needs to exist before referencing the stampId)

        UUID idStamp = stampEntity.getId();
        for (int i = 0; i < stampIdTasks.size(); i++) {
            utilities.ensureNoEntityExists(this.stampTaskStampHasTaskRepository.findByIdStampIdTask(idStamp.toString(), stampIdTasks.get(i)).size());
            StampTaskStampHasTaskEntity stampTaskStampHasTaskEntity = new StampTaskStampHasTaskEntity();
            stampTaskStampHasTaskEntity.setIdTask(UUID.fromString(stampIdTasks.get(i)));
            stampTaskStampHasTaskEntity.setIdStamp(idStamp);
            stampTaskStampHasTaskEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            this.stampTaskStampHasTaskRepository.save(stampTaskStampHasTaskEntity);
        }
        return this._getStampPage(idUser, idStamp.toString());
    }

    /**
     * id
     * ?name
     * ?description
     * ?imageAsBase64String
     * ?idTasks
     */
    public StampPageModel updateStamp(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idStamp = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeUpdateStamp(idUser, idStamp);
        StampEntity stampEntity = this.stampRepository.findById(UUID.fromString(idStamp)).get();

        if (rb.has("name")) {
            String stampName = RequestBodyValidator.stringRequired(RequestBodyFormatter.fString(rb.get("name")),
                    Limits.STAMP_NAME_MIN_LENGTH_CHARACTERS, Limits.STAMP_NAME_MAX_LENGTH_CHARACTERS);
            List<StampEntity> stampEntitiesWithSameName = this.stampRepository.findByNameLowerCase(stampName);
            if (stampEntitiesWithSameName.size() > 0
                    && !stampEntitiesWithSameName.get(0).getId().toString().equals(idStamp)) {
                throw new Exception("STAMP_DUPLICATE_NAME");
            }
            boolean isNoop = (stampName == null && stampEntity.getName() == null) ||
                    (stampName != null && stampName.equals(stampEntity.getName()));
            if (!isNoop) {
                stampEntity.setName(stampName);
                this.stampRepository.save(stampEntity);
            }
        }
        if (rb.has("description")) {
            String stampDescription = RequestBodyValidator.stringOptional(
                    RequestBodyFormatter.fString(rb.get("description")),
                    Limits.STAMP_DESCRIPTION_MIN_LENGTH_CHARACTERS, Limits.STAMP_DESCRIPTION_MAX_LENGTH_CHARACTERS);
            boolean isNoop = (stampDescription == null && stampEntity.getDescription() == null) ||
                    (stampDescription != null && stampDescription.equals(stampEntity.getDescription()));
            if (!isNoop) {
                stampEntity.setDescription(stampDescription);
                this.stampRepository.save(stampEntity);
            }
        }
        if (rb.has("imageAsBase64String")) {
            String stampImageAsBase64String = RequestBodyValidator
                    .imageOptional(RequestBodyFormatter.fString(rb.get("imageAsBase64String")));
            boolean isNoop = stampImageAsBase64String == null && stampEntity.getIdImageKey() == null;
            if (!isNoop) {
                if (stampImageAsBase64String == null) {
                    stampEntity.setIdImageKey(null);
                    this.stampRepository.save(stampEntity);
                } else {
                    UUID stampImageKeyId = UUID.randomUUID();
                    byte[] imageAsByteArray = java.util.Base64.getDecoder()
                            .decode(stampImageAsBase64String.substring(stampImageAsBase64String.indexOf(",") + 1));
                    RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
                    utilities.putObjectInS3(Constants.S3_FOLDER_NAME_STAMP_IMAGE, stampImageKeyId,
                            imageAsByteArrayRequestBody);
                    stampEntity.setIdImageKey(stampImageKeyId);
                    this.stampRepository.save(stampEntity);
                }

            }
        }
        if (rb.has("idTasks")) {
            List<String> idTasks = new ArrayList<>();
            for (int i = 0; i < rb.get("idTasks").size(); i++) {
                String idTask = RequestBodyFormatter.fString(rb.get("idTasks").get(i));
                idTasks.add(idTask);
            }
            if (idTasks.size() >= Limits.LIMIT_NUMBER_OF_TOTAL_TASKS_STAMP) {
                throw new Exception("EXCEEDED_LIMIT_NUMBER_OF_TOTAL_TASKS_STAMP");
            }
            // remove all previous taskIds before adding in the new ones
            List<StampTaskStampHasTaskEntity> stampTaskStampHasTaskEntityList = this.stampTaskStampHasTaskRepository
                    .findByIdStamp(idStamp);
            for (int i = 0; i < stampTaskStampHasTaskEntityList.size(); i++) {
                this.stampTaskStampHasTaskRepository.delete(stampTaskStampHasTaskEntityList.get(i));
            }
            // add all new taskIds
            for (int i = 0; i < idTasks.size(); i++) {
                utilities.ensureNoEntityExists(this.stampTaskStampHasTaskRepository.findByIdStampIdTask(idStamp, idTasks.get(i)).size());
                StampTaskStampHasTaskEntity stampTaskStampHasTaskEntity = new StampTaskStampHasTaskEntity();
                stampTaskStampHasTaskEntity.setIdTask(UUID.fromString(idTasks.get(i)));
                stampTaskStampHasTaskEntity.setIdStamp(stampEntity.getId());
                stampTaskStampHasTaskEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
                this.stampTaskStampHasTaskRepository.save(stampTaskStampHasTaskEntity);
            }
        }
        return this._getStampPage(idUser, idStamp);
    }

    private StampCardsPaginatedModel _getStampCardModelFromSharedAndIndividualProperties(
            String idViewingUser,
            List<StampCardSharedPropertiesDTO> stampCards_SharedProperties,
            List<StampCardIndividualPropertiesDTO> stampCards_IndividualProperties,
            boolean filterIsPublic,
            boolean filterIsNotPublic,
            boolean filterIsCollect,
            boolean filterIsNotCollect,
            int paginationIdxStart,
            int paginationN
    ) {
        HashMap<UUID, StampCardSharedPropertiesDTO> idStampToStampCardSharedPropertiesDTOMap = new HashMap<>();
        HashMap<UUID, StampCardIndividualPropertiesDTO> idStampToStampCardIndividualPropertiesDTOMap = new HashMap<>();

        for (int i = 0; i < stampCards_SharedProperties.size(); i++) {
            idStampToStampCardSharedPropertiesDTOMap.put(stampCards_SharedProperties.get(i).getId(),
                    stampCards_SharedProperties.get(i));
        }
        for (int i = 0; i < stampCards_IndividualProperties.size(); i++) {
            idStampToStampCardIndividualPropertiesDTOMap.put(stampCards_IndividualProperties.get(i).getId(),
                    stampCards_IndividualProperties.get(i));
        }
        List<StampCardModel> stampCardList = new ArrayList<>();
        for (UUID idStamp : idStampToStampCardSharedPropertiesDTOMap.keySet()) {
            StampCardSharedPropertiesDTO stampCardSharedPropertiesEntry = idStampToStampCardSharedPropertiesDTOMap
                    .get(idStamp);
            StampCardIndividualPropertiesDTO stampCardIndividualPropertiesEntry = idStampToStampCardIndividualPropertiesDTOMap
                    .get(idStamp);
            String stampImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_STAMP_IMAGE,
                    stampCardSharedPropertiesEntry.getIdImageKey());
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
            boolean isViewingUserAllowedAccessViewStampCard = true;
            List<StampTaskStampHasTaskEntity> stampTaskStampHasTaskEntityList = this.stampTaskStampHasTaskRepository.findByIdStamp(stampCard.getId().toString());
            Set<UUID> idTasksAssociatedWithStamp = new HashSet<>();
            for (int i = 0; i < stampTaskStampHasTaskEntityList.size(); i++) {
                idTasksAssociatedWithStamp.add(stampTaskStampHasTaskEntityList.get(i).getIdTask());
            }
            Set<String> idPodsAssociatedWithStamp = new HashSet<>();
            for (UUID idTaskAssociatedWithStamp : idTasksAssociatedWithStamp) {
                UUID idPodAssociatedWithStamp = this.taskRepository.findById(idTaskAssociatedWithStamp).get().getIdPod();
                if (idPodAssociatedWithStamp != null) {
                    idPodsAssociatedWithStamp.add(idPodAssociatedWithStamp.toString());
                }
            }
            for (String idPodAssociatedWithStamp : idPodsAssociatedWithStamp) {
                boolean isPodPublic = this.podRepository.findById(UUID.fromString(idPodAssociatedWithStamp)).get().isPublic();
                if (!isPodPublic) {
                    boolean isViewingUserMemberOfPod = this.podUserPodHasUserRepository.findByIdPodIdUser(idPodAssociatedWithStamp, idViewingUser).size() > 0 &&
                            this.podUserPodHasUserRepository.findByIdPodIdUser(idPodAssociatedWithStamp, idViewingUser).get(0).isMember();
                    if (!isViewingUserMemberOfPod) {
                        isViewingUserAllowedAccessViewStampCard = false;
                    }
                }
            }
            return isViewingUserAllowedAccessViewStampCard &&
                    (stampCard.isCollect() == filterIsCollect || stampCard.isCollect() != filterIsNotCollect) &&
                    (stampCard.isPublic() == filterIsPublic || stampCard.isPublic() != filterIsNotPublic);
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

        // fix to correct size
        int idxStart = Math.min(paginationIdxStart, stampCardList.size());
        int idxEnd = Math.min(paginationIdxStart+paginationN, stampCardList.size());
        StampCardsPaginatedModel stampCardsPaginatedModel = StampCardsPaginatedModel.builder()
            .data(stampCardList.subList(idxStart, idxEnd))
            .totalN(stampCardList.size())
            .build();
        return stampCardsPaginatedModel;
    }

    /**
     * id
     */
    public void collectStamp(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idStamp = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeCollectStamp(idUser, idStamp);
        boolean isEligibleToBeCollectedByMe = true;
        List<UUID> stampTaskIds = this.stampTaskStampHasTaskRepository.findByIdStamp(idStamp).stream()
                .map(stampTaskStampHasTaskEntity -> stampTaskStampHasTaskEntity.getIdTask())
                .collect(Collectors.toList());
        for (int i = 0; i < stampTaskIds.size(); i++) {
            boolean isTaskCompleteByMe = this.taskUserTaskCompleteRepository
                    .findByIdTaskIdUser(stampTaskIds.get(i).toString(), idUser).size() > 0;
            if (!isTaskCompleteByMe) {
                isEligibleToBeCollectedByMe = false;
            }
        }
        if (isEligibleToBeCollectedByMe) {
            utilities.ensureNoEntityExists(this.stampUserUserCollectStampRepository.findByIdStampIdUser(idStamp, idUser).size());
            StampUserUserCollectStampEntity stampUserUserCollectStampEntity = new StampUserUserCollectStampEntity();
            stampUserUserCollectStampEntity.setIdStamp(UUID.fromString(idStamp));
            stampUserUserCollectStampEntity.setIdUser(UUID.fromString(idUser));
            stampUserUserCollectStampEntity.setTimestampUnix((int) Instant.now().getEpochSecond());
            this.stampUserUserCollectStampRepository.save(stampUserUserCollectStampEntity);
        }
    }

    /**
     * id
     */
    public UserPageModel getUserPage(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetUserPage(idUser);
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idUserOfUserPage = RequestBodyFormatter.fString(rb.get("id"));
        return this._getUserPage(idUser, idUserOfUserPage);
    }

    public UserPageModel _getUserPage(String idUser, String idUserOfUserPage) throws Exception {
        UserEntity userEntity = this.userRepository.findById(UUID.fromString(idUserOfUserPage)).get();
        String userImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                userEntity.getIdImageKey());
        List<UserBubbleModel> userBubblesFollowing = this._getUserBubblesFollowing(idUser, idUserOfUserPage);
        List<UserBubbleModel> userBubblesFollower = this._getUserBubblesFollower(idUser, idUserOfUserPage);
        Set<UUID> userIdsFollowedByGivenUser = new HashSet<>(this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByGivenUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));

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
                .isMe(idUserOfUserPage.equals(idUser))
                .isFollowedByMe(userIdsFollowedByGivenUser.contains(UUID.fromString(idUserOfUserPage)))
                .isFollowRequestSentNotYetAccepted(
                        userIds_FollowRequestSentByGivenUser_NotYetAccepted.contains(UUID.fromString(idUserOfUserPage)))
                .numberOfPendingFollowUserRequests(this.userRepository
                        .getUserIdsFollowRequestSentToGivenUser_NotYetAccepted(idUserOfUserPage).size())
                .build();
    }

    /**
     * N/A
     */
    public PersonalPageModel getPersonalPage() throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetPersonalPage(idUser);
        return this._getPersonalPage();
    }

    /**
     * N/A
     */
    public PersonalPageModel _getPersonalPage() throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        UserEntity userEntity = this.userRepository.findById(UUID.fromString(idUser)).get();
        String userImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                userEntity.getIdImageKey());

        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<NumberOfPointsInTasksCompletedOverTimeVisualizationDTO> numberOfPointsInTasksCompletedOverTimeList = this.taskRepository
                .getNumberOfPointsInTasksCompletedOverTimeVisualizationPersonal(idUser);
        String dateTodayAsString = LocalDate.now(userTimeZoneZoneId).format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        int numberOfPointsTaskCompleteToday = 0;
        for (int i = 0; i < numberOfPointsInTasksCompletedOverTimeList.size(); i++) {
            NumberOfPointsInTasksCompletedOverTimeVisualizationDTO numberOfPointsInTasksCompletedOverTimeEntry = numberOfPointsInTasksCompletedOverTimeList
                    .get(i);
            Integer timestampOfTaskComplete = numberOfPointsInTasksCompletedOverTimeEntry.getTimestamp();
            String dateLabel = utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId, timestampOfTaskComplete);
            if (dateLabel.equals(dateTodayAsString)) {
                numberOfPointsTaskCompleteToday += numberOfPointsInTasksCompletedOverTimeEntry.getNumberOfPoints();
            }
        }
        int numberOfIncompletePersonalTasks = this.taskRepository.getPersonalTasks(idUser)
            .stream()
            .filter(task -> !task.isComplete())
            .collect(Collectors.toList()).size();
        boolean isReachedNumberOfTasksLimit = numberOfIncompletePersonalTasks >= Limits.LIMIT_NUMBER_OF_INCOMPLETE_TASKS_PERSONAL;
        return PersonalPageModel.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .name(userEntity.getName())
                .imageLink(userImageLink)
                .numberOfPointsTaskCompleteToday(numberOfPointsTaskCompleteToday)
                .isReachedNumberOfTasksLimit(isReachedNumberOfTasksLimit)
                .build();
    }

    /**
     * ?username
     * ?name
     * ?bio
     * ?imageAsBase64String
     */
    public UserPageModel updateUserPage(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeUpdateUserPage(idUser);
        UserEntity userEntity = this.userRepository.findById(UUID.fromString(idUser)).get();
        if (rb.has("username")) {
            String userUsername = RequestBodyValidator.stringRegex(
                RequestBodyValidator.stringRequired(RequestBodyFormatter.fString(rb.get("username")),
                            Limits.USER_USERNAME_MIN_LENGTH_CHARACTERS, Limits.USER_USERNAME_MAX_LENGTH_CHARACTERS), Constants.REGEX_USER_USERNAME);
            List<UserEntity> userEntitiesWithSameUsername = this.userRepository.findByUsernameLowerCase(userUsername);
            if (userEntitiesWithSameUsername.size() > 0
                    && !userEntitiesWithSameUsername.get(0).getId().toString().equals(idUser)) {
                throw new Exception("USER_DUPLICATE_USERNAME");
            }
            boolean isNoop = (userUsername == null && userEntity.getUsername() == null) ||
                    (userUsername != null && userUsername.equals(userEntity.getUsername()));
            if (!isNoop) {
                userEntity.setUsername(userUsername);
                this.userRepository.save(userEntity);
            }
        }
        if (rb.has("name")) {
            String userName = RequestBodyValidator.stringRequired(RequestBodyFormatter.fString(rb.get("name")),
                    Limits.USER_NAME_MIN_LENGTH_CHARACTERS, Limits.USER_NAME_MAX_LENGTH_CHARACTERS);
            boolean isNoop = (userName == null && userEntity.getName() == null) ||
                    (userName != null && userName.equals(userEntity.getName()));
            if (!isNoop) {
                userEntity.setName(userName);
                this.userRepository.save(userEntity);
            }
        }
        if (rb.has("bio")) {
            String userBio = RequestBodyValidator.stringOptional(RequestBodyFormatter.fString(rb.get("bio")),
                    Limits.USER_BIO_MIN_LENGTH_CHARACTERS, Limits.USER_BIO_MAX_LENGTH_CHARACTERS);
            boolean isNoop = (userBio == null && userEntity.getBio() == null) ||
                    (userBio != null && userBio.equals(userEntity.getBio()));
            if (!isNoop) {
                userEntity.setBio(userBio);
                this.userRepository.save(userEntity);
            }
        }
        if (rb.has("imageAsBase64String")) {
            String userImageAsBase64String = RequestBodyValidator
                    .imageOptional(RequestBodyFormatter.fString(rb.get("imageAsBase64String")));
            boolean isNoop = userImageAsBase64String == null && userEntity.getIdImageKey() == null;
            if (!isNoop) {
                UUID userImageKeyId = UUID.randomUUID();
                byte[] imageAsByteArray = java.util.Base64.getDecoder()
                        .decode(userImageAsBase64String.substring(userImageAsBase64String.indexOf(",") + 1));
                RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
                utilities.putObjectInS3(Constants.S3_FOLDER_NAME_USER_IMAGE, userImageKeyId,
                        imageAsByteArrayRequestBody);
                userEntity.setIdImageKey(userImageKeyId);
                this.userRepository.save(userEntity);
            }
        }
        return this._getUserPage(idUser, idUser);
    }

    /**
     * id
     */
    public List<UserBubbleModel> getUserBubblesFollowing(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetUserBubblesFollowing(idUser);
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idUserBeingViewed = RequestBodyFormatter.fString(rb.get("id"));
        return this._getUserBubblesFollowing(idUser, idUserBeingViewed);
    }

    /**
     * id
     */
    public List<UserBubbleModel> _getUserBubblesFollowing(String idUser, String idUserBeingViewed) throws Exception {
        Set<UUID> userIdsFollowedByViewingUser = new HashSet<>(
                this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByViewingUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));

        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesFollowing = this.userRepository.getUserBubblesFollowing(idUserBeingViewed)
                .stream().map(userBubbleDTOFollowing -> {
                    String userBubbleImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                            userBubbleDTOFollowing.getIdImageKey());
                    return UserBubbleModel.builder()
                            .id(userBubbleDTOFollowing.getId())
                            .name(userBubbleDTOFollowing.getName())
                            .username(userBubbleDTOFollowing.getUsername())
                            .imageLink(userBubbleImageLink)
                            .timestampToSortBy(userBubbleDTOFollowing.getTimestampToSortBy())
                            .datetimeDateOnlyLabel(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTOFollowing.getTimestampToSortBy()))
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTOFollowing.getTimestampToSortBy()))
                            .isFollowedByMe(userIdsFollowedByViewingUser.contains(userBubbleDTOFollowing.getId()))
                            .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByViewingUser_NotYetAccepted
                                    .contains(userBubbleDTOFollowing.getId()))
                            .isMe(idUser.equals(userBubbleDTOFollowing.getId().toString()))
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
                } else if (userBubbleFollowing1.isFollowedByMe() == false
                        && userBubbleFollowing2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleFollowing1.getUsername().compareTo(userBubbleFollowing2.getUsername());
            }
        });
        return userBubblesFollowing;
    }

    /**
     * id
     */
    public List<UserBubbleModel> getUserBubblesFollower(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetUserBubblesFollower(idUser);
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idUserBeingViewed = RequestBodyFormatter.fString(rb.get("id"));
        return this._getUserBubblesFollower(idUser, idUserBeingViewed);
    }

    public List<UserBubbleModel> _getUserBubblesFollower(String idUser, String idUserBeingViewed) throws Exception {
        Set<UUID> userIdsFollowedByViewingUser = new HashSet<>(
                this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByViewingUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesFollower = this.userRepository.getUserBubblesFollower(idUserBeingViewed)
                .stream().map(userBubbleDTOFollower -> {
                    String userBubbleImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                            userBubbleDTOFollower.getIdImageKey());
                    return UserBubbleModel.builder()
                            .id(userBubbleDTOFollower.getId())
                            .name(userBubbleDTOFollower.getName())
                            .username(userBubbleDTOFollower.getUsername())
                            .imageLink(userBubbleImageLink)
                            .timestampToSortBy(userBubbleDTOFollower.getTimestampToSortBy())
                            .datetimeDateOnlyLabel(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTOFollower.getTimestampToSortBy()))
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTOFollower.getTimestampToSortBy()))
                            .isFollowedByMe(userIdsFollowedByViewingUser.contains(userBubbleDTOFollower.getId()))
                            .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByViewingUser_NotYetAccepted
                                    .contains(userBubbleDTOFollower.getId()))
                            .isMe(idUser.equals(userBubbleDTOFollower.getId().toString()))
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
                } else if (userBubbleFollower1.isFollowedByMe() == false
                        && userBubbleFollower2.isFollowedByMe() == true) {
                    return 1;
                }
                return userBubbleFollower1.getUsername().compareTo(userBubbleFollower2.getUsername());
            }
        });
        return userBubblesFollower;
    }

    /**
     * id
     */
    public void sendFollowUserRequest(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeSendFollowUserRequest(idUser);
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idUserReceiveFollowRequest = RequestBodyFormatter.fString(rb.get("id"));
        boolean isUserReceiveFollowRequest_Public = this.userRepository
                .findById(UUID.fromString(idUserReceiveFollowRequest)).get().isPublic();
        if (isUserReceiveFollowRequest_Public) {
            utilities.ensureNoEntityExists(this.userUserUser1FollowUser2Repository.findByIdUser1IdUser2(idUser, idUserReceiveFollowRequest).size());
            UserUserUser1FollowUser2Entity userUserUser1FollowUser2Entity = new UserUserUser1FollowUser2Entity();
            userUserUser1FollowUser2Entity.setTimestampUnix((int) Instant.now().getEpochSecond());
            userUserUser1FollowUser2Entity.setIdUser1(UUID.fromString(idUser));
            userUserUser1FollowUser2Entity.setIdUser2(UUID.fromString(idUserReceiveFollowRequest));
            userUserUser1FollowUser2Entity.setRequestSent(true);
            userUserUser1FollowUser2Entity.setTimestampRequestSent((int) Instant.now().getEpochSecond());
            userUserUser1FollowUser2Entity.setFollowing(true);
            userUserUser1FollowUser2Entity.setTimestampOfFollowing((int) Instant.now().getEpochSecond());
            this.userUserUser1FollowUser2Repository.save(userUserUser1FollowUser2Entity);
        } else {
            utilities.ensureNoEntityExists(this.userUserUser1FollowUser2Repository.findByIdUser1IdUser2(idUser, idUserReceiveFollowRequest).size());
            UserUserUser1FollowUser2Entity userUserUser1FollowUser2Entity = new UserUserUser1FollowUser2Entity();
            userUserUser1FollowUser2Entity.setTimestampUnix((int) Instant.now().getEpochSecond());
            userUserUser1FollowUser2Entity.setIdUser1(UUID.fromString(idUser));
            userUserUser1FollowUser2Entity.setIdUser2(UUID.fromString(idUserReceiveFollowRequest));
            userUserUser1FollowUser2Entity.setRequestSent(true);
            userUserUser1FollowUser2Entity.setTimestampRequestSent((int) Instant.now().getEpochSecond());
            utilities.generateNotification(
                    this.notificationRepository,
                    UUID.fromString(idUserReceiveFollowRequest),
                    Constants.NOTIFICATION_TYPE_SENT_YOU_FOLLOW_REQUEST,
                    String.format("@%s has sent you a follow request",
                            this.userRepository.findById(UUID.fromString(idUser)).get().getUsername()),
                    Constants.NOTIFICATION_LINK_PAGE_TYPE_USER,
                    UUID.fromString(idUser));
            this.userUserUser1FollowUser2Repository.save(userUserUser1FollowUser2Entity);
        }
    }

    /**
     * id
     */
    public void unfollowUser(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idUserToBeUnfollowed = RequestBodyFormatter.fString(rb.get("id"));
        this.authorizationService.authorizeUnfollowUser(idUser, idUserToBeUnfollowed);
        UserUserUser1FollowUser2Entity userUserUser1FollowUser2Entity = this.userUserUser1FollowUser2Repository
                    .findByIdUser1IdUser2(idUser, idUserToBeUnfollowed).get(0);
        this.userUserUser1FollowUser2Repository.delete(userUserUser1FollowUser2Entity);
    }

    /**
     * idUsers
     */
    public List<UserBubbleModel> acceptFollowUserRequests(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "idUsers"));
        List<String> idUsersWithFollowRequestAccepted = new ArrayList<>();
        for (int i = 0; i < rb.get("idUsers").size(); i++) {
            String idUserWithFollowRequestAccepted = RequestBodyFormatter.fString(rb.get("idUsers").get(i));
            idUsersWithFollowRequestAccepted.add(idUserWithFollowRequestAccepted);
        }
        this.authorizationService.authorizeAcceptFollowUserRequests(idUser, idUsersWithFollowRequestAccepted);

        for (int i = 0; i < idUsersWithFollowRequestAccepted.size(); i++) {
            String idUserWithFollowRequestAccepted = idUsersWithFollowRequestAccepted.get(i);
            UserUserUser1FollowUser2Entity userUserUser1FollowUser2Entity = this.userUserUser1FollowUser2Repository
                    .findByIdUser1IdUser2(idUserWithFollowRequestAccepted, idUser).get(0);
            userUserUser1FollowUser2Entity.setRequestAccepted(true);
            userUserUser1FollowUser2Entity.setTimestampRequestAccepted((int) Instant.now().getEpochSecond());
            userUserUser1FollowUser2Entity.setFollowing(true);
            userUserUser1FollowUser2Entity.setTimestampOfFollowing((int) Instant.now().getEpochSecond());
            this.userUserUser1FollowUser2Repository.save(userUserUser1FollowUser2Entity);
            utilities.generateNotification(
                    this.notificationRepository,
                    UUID.fromString(idUserWithFollowRequestAccepted),
                    Constants.NOTIFICATION_TYPE_ACCEPTED_YOUR_FOLLOW_REQUEST,
                    String.format("@%s has accepted your follow request",
                            this.userRepository.findById(UUID.fromString(idUser)).get().getUsername()),
                    Constants.NOTIFICATION_LINK_PAGE_TYPE_USER,
                    UUID.fromString(idUser));
        }
        return this._getUserBubblesPendingFollowUserRequest(idUser);
    }

    /**
     * idUsers
     */
    public List<UserBubbleModel> declineFollowUserRequests(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "idUsers"));
        List<String> idUsersWithFollowRequestDeclined = new ArrayList<>();
        for (int i = 0; i < rb.get("idUsers").size(); i++) {
            String idUserWithFollowRequestDeclined = RequestBodyFormatter.fString(rb.get("idUsers").get(i));
            idUsersWithFollowRequestDeclined.add(idUserWithFollowRequestDeclined);
        }
        this.authorizationService.authorizeDeclineFollowUserRequests(idUser, idUsersWithFollowRequestDeclined);

        for (int i = 0; i < idUsersWithFollowRequestDeclined.size(); i++) {
            String idUserWithFollowRequestDeclined = idUsersWithFollowRequestDeclined.get(i);
            UserUserUser1FollowUser2Entity userUserUser1FollowUser2Entity = this.userUserUser1FollowUser2Repository
                    .findByIdUser1IdUser2(idUserWithFollowRequestDeclined, idUser).get(0);
            if (!userUserUser1FollowUser2Entity.isFollowing()) {
                this.userUserUser1FollowUser2Repository.delete(userUserUser1FollowUser2Entity);
            }
        }
        return this._getUserBubblesPendingFollowUserRequest(idUser);
    }

    /**
     * N/A
     */
    public List<UserBubbleModel> getUserBubblesPendingFollowUserRequest() throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetUserBubblesPendingFollowUserRequest(idUser);
        return this._getUserBubblesPendingFollowUserRequest(idUser);
    }

    /**
     * N/A
     */
    public List<UserBubbleModel> _getUserBubblesPendingFollowUserRequest(String idUser) throws Exception {
        Set<UUID> userIdsFollowedByViewingUser = new HashSet<>(
                this.userRepository.getUserIdsFollowedByGivenUser(idUser));
        Set<UUID> userIds_FollowRequestSentByViewingUser_NotYetAccepted = new HashSet<>(
                this.userRepository.getUserIds_FollowRequestSentByGivenUser_NotYetAccepted(idUser));
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);
        List<UserBubbleModel> userBubblesPendingFollowUserRequest = this.userRepository
                .getUserBubblesFollowUserRequestSentNotYetAccepted(idUser).stream().map(userBubbleDTO -> {
                    String userBubbleImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                            userBubbleDTO.getIdImageKey());
                    return UserBubbleModel.builder()
                            .id(userBubbleDTO.getId())
                            .name(userBubbleDTO.getName())
                            .username(userBubbleDTO.getUsername())
                            .imageLink(userBubbleImageLink)
                            .timestampToSortBy(userBubbleDTO.getTimestampToSortBy())
                            .datetimeDateOnlyLabel(utilities.getDatetimeDateOnlyFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTO.getTimestampToSortBy()))
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    userBubbleDTO.getTimestampToSortBy()))
                            .isFollowedByMe(userIdsFollowedByViewingUser.contains(userBubbleDTO.getId()))
                            .isFollowRequestSentNotYetAccepted(userIds_FollowRequestSentByViewingUser_NotYetAccepted
                                    .contains(userBubbleDTO.getId()))
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

    /**
     * N/A
     */
    public int getNotificationsUnseenCount() throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetNotificationsUnseenCount(idUser);
        return this.notificationRepository.findByIdUser(idUser).stream()
                .filter(notificationEntity -> !notificationEntity.isSeen()).collect(Collectors.toList()).size();
    }

    /**
     * N/A
     */
    public List<NotificationModel> getNotifications() throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetNotifications(idUser);
        ZoneId userTimeZoneZoneId = utilities.getUserTimeZoneZoneId(idUser, this.userRepository);

        List<NotificationModel> notifications = this.notificationRepository.findByIdUser(idUser).stream()
                .map(notificationEntity -> {
                    String notificationImageLink = null;
                    if (notificationEntity.getLinkPageType() != null
                            && notificationEntity.getLinkPageType().equals(Constants.NOTIFICATION_LINK_PAGE_TYPE_POD)) {
                        notificationImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_POD_IMAGE,
                                this.podRepository.findById(notificationEntity.getIdLinkPage()).get().getIdImageKey());
                    } else if (notificationEntity.getLinkPageType() != null && notificationEntity.getLinkPageType()
                            .equals(Constants.NOTIFICATION_LINK_PAGE_TYPE_USER)) {
                        notificationImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                                this.userRepository.findById(notificationEntity.getIdLinkPage()).get().getIdImageKey());
                    }
                    boolean isMemberOfPod = false;
                    if (notificationEntity.getLinkPageType() != null
                            && notificationEntity.getLinkPageType().equals(Constants.NOTIFICATION_LINK_PAGE_TYPE_POD)) {
                        Set<UUID> userIdsPodMember = new HashSet<>(this.userRepository
                                .getUserBubblesPodMember(notificationEntity.getIdLinkPage().toString()).stream()
                                .map(userBubblePodMember -> {
                                    return userBubblePodMember.getId();
                                }).collect(Collectors.toList()));
                        isMemberOfPod = userIdsPodMember.contains(notificationEntity.getIdUser());
                    }
                    boolean isFollowedByUserWhoSentFollowRequest = false;
                    if (notificationEntity.getLinkPageType() != null && notificationEntity.getLinkPageType()
                            .equals(Constants.NOTIFICATION_LINK_PAGE_TYPE_USER)) {
                        Set<UUID> userIdsMyFollowers = new HashSet<>(
                                this.userRepository.getUserBubblesFollower(idUser).stream().map(userBubblePodMember -> {
                                    return userBubblePodMember.getId();
                                }).collect(Collectors.toList()));
                        isFollowedByUserWhoSentFollowRequest = userIdsMyFollowers
                                .contains(notificationEntity.getIdLinkPage());
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
                            .datetimeDateAndTimeLabel(utilities.getDatetimeDateAndTimeFromTimestamp(userTimeZoneZoneId,
                                    notificationEntity.getTimestampUnix()))
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

    /**
     * N/A
     */
    public void markAllNotificationsAsSeen() throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeMarkAllNotificationsAsSeen(idUser);
        List<NotificationEntity> notificationEntityList = this.notificationRepository.findByIdUser(idUser);
        for (int i = 0; i < notificationEntityList.size(); i++) {
            NotificationEntity notificationEntity = notificationEntityList.get(i);
            notificationEntity.setSeen(true);
            this.notificationRepository.save(notificationEntity);
        }
    }

    /**
     * N/A
     */
    public void dismissNotification(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeDismissNotification(idUser);
        RequestBodyValidator.verifyRequiredFieldNamesExist(
                rb,
                Arrays.asList(
                        "id"));
        String idNotification = RequestBodyFormatter.fString(rb.get("id"));
        NotificationEntity notificationEntity = this.notificationRepository.findById(UUID.fromString(idNotification))
                .get();
        notificationEntity.setDismissed(true);
        this.notificationRepository.save(notificationEntity);
    }

    /**
     * N/A
     */
    public AccountSettingsPageModel getAccountSettingsPage() throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeGetAccountSettingsPage(idUser);
        return this._getAccountSettingsPage();
    }

    /**
     * N/A
     */
    public AccountSettingsPageModel _getAccountSettingsPage() throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        UserEntity userEntity = this.userRepository.findById(UUID.fromString(idUser)).get();
        String userImageLink = utilities.getPresignedUrl(Constants.S3_FOLDER_NAME_USER_IMAGE,
                userEntity.getIdImageKey());
        return AccountSettingsPageModel.builder()
                .id(userEntity.getId())
                .username(userEntity.getUsername())
                .email(userEntity.getEmail())
                .imageLink(userImageLink)
                .timeZone(userEntity.getTimeZone())
                .build();
    }

    /**
     * ?timeZone
     * ?email
     * ?imageAsBase64String
     */
    public AccountSettingsPageModel updateAccountSettingsPage(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeUpdateAccountSettingsPage(idUser);
        UserEntity userEntity = this.userRepository.findById(UUID.fromString(idUser)).get();
        if (rb.has("timeZone")) {
            String userTimeZone = RequestBodyValidator.stringChoice(
                RequestBodyFormatter.fString(rb.get("timeZone")), Constants.TIME_ZONE_CHOICES);
            userEntity.setTimeZone(userTimeZone);
            this.userRepository.save(userEntity);
        }
        if (rb.has("email")) {
            String email = RequestBodyValidator.email(RequestBodyFormatter.fString(rb.get("email")));
            if (this.userRepository.findByEmail(email).isPresent() && 
                !this.userRepository.findByEmail(email).get().getId().toString().equals(idUser)
            ) {
                throw new Error("DUPLICATE_ACCOUNT_EMAIL");
            }
            userEntity.setEmail(email);
            this.userRepository.save(userEntity);
        }
        if (rb.has("imageAsBase64String")) {
            String userImageAsBase64String = RequestBodyValidator
                    .imageOptional(RequestBodyFormatter.fString(rb.get("imageAsBase64String")));
            boolean isNoop = userImageAsBase64String == null && userEntity.getIdImageKey() == null;
            if (!isNoop) {
                UUID userImageKeyId = UUID.randomUUID();
                byte[] imageAsByteArray = java.util.Base64.getDecoder()
                        .decode(userImageAsBase64String.substring(userImageAsBase64String.indexOf(",") + 1));
                RequestBody imageAsByteArrayRequestBody = RequestBody.fromBytes(imageAsByteArray);
                utilities.putObjectInS3(Constants.S3_FOLDER_NAME_USER_IMAGE, userImageKeyId,
                        imageAsByteArrayRequestBody);
                userEntity.setIdImageKey(userImageKeyId);
                this.userRepository.save(userEntity);
            }
        }
        return this._getAccountSettingsPage();
    }

    /**
     * password
     */
    public void deleteAccount(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeDeleteAccount(idUser);
        String userPassword = rb.get("password").asText();
        try {
            Authentication authenticationResult = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    idUser,
                    userPassword
                )
            );
            UserEntity userEntity = this.userRepository.findById(UUID.fromString(idUser)).get();
            this.userRepository.delete(userEntity);
        } catch(Exception e) {
            throw new Exception("INCORRECT_PASSWORD");
        }
    }

    /**
     * currentPassword
     * newPassword
     * newPasswordConfirmed
     */
    public void changePassword(JsonNode rb) throws Exception {
        String idUser = jwtTokenWrapper.getJwtTokenSubject();
        this.authorizationService.authorizeChangePassword(idUser);
        String currentPassword = RequestBodyValidator.stringRequired(
            RequestBodyFormatter.fString(rb.get("currentPassword")), 
            Limits.USER_PASSWORD_MIN_LENGTH_CHARACTERS, 
            Limits.USER_PASSWORD_MAX_LENGTH_CHARACTERS
        );
        String newPassword = RequestBodyValidator.stringRequired(
            RequestBodyFormatter.fString(rb.get("newPassword")), 
            Limits.USER_PASSWORD_MIN_LENGTH_CHARACTERS, 
            Limits.USER_PASSWORD_MAX_LENGTH_CHARACTERS
        );
        String newPasswordConfirmed = RequestBodyValidator.stringRequired(
            RequestBodyFormatter.fString(rb.get("newPasswordConfirmed")), 
            Limits.USER_PASSWORD_MIN_LENGTH_CHARACTERS, 
            Limits.USER_PASSWORD_MAX_LENGTH_CHARACTERS
        );
        if (!newPassword.equals(newPasswordConfirmed)) {
            // should never reach here though
            throw new Exception("NEW_PASSWORDS_DO_NOT_MATCH");
        }
        try {
            Authentication authenticationResult = this.authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    idUser,
                    currentPassword
                )
            );
            UserEntity userEntity = this.userRepository.findById(UUID.fromString(idUser)).get();
            String encodedNewPassword = this.passwordEncoder.encode(newPassword);
            userEntity.setPassword(encodedNewPassword);
            this.userRepository.save(userEntity);
        } catch(Exception e) {
            throw new Exception("INCORRECT_CURRENT_PASSWORD");
        }
    }

}