package com.bet.betwebservice.service;

import com.bet.betwebservice.dao.StampRepository;
import com.bet.betwebservice.model.StampCardModel;
import com.bet.betwebservice.model.StampPageModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class StampService {
    private StampRepository stampRepository;

    public StampService(StampRepository stampRepository) {
        this.stampRepository = stampRepository;
    }

    public Page<StampCardModel> getDiscoverPageStampCards(Pageable pageable) {
        return this.stampRepository.getDiscoverPageStampCards(pageable).map(
                stampCardDTO -> StampCardModel.builder()
                        .id(stampCardDTO.getId())
                        .name(stampCardDTO.getName())
                        .description(stampCardDTO.getDescription())
                        .image(stampCardDTO.getImage())
                        .numberOfUsersCollect(stampCardDTO.getNumberOfUsersCollect())
                        .build()
        );
    }

    public List<StampPageModel> getStampPage() {
        return null;
    }

    public Page<StampCardModel> getStampCardsAssociatedWithPod(String idPod, Pageable pageable) {
        return this.stampRepository.getStampCardsAssociatedWithPod(idPod, pageable).map(
                stampCardDTO -> StampCardModel.builder()
                        .id(stampCardDTO.getId())
                        .name(stampCardDTO.getName())
                        .description(stampCardDTO.getDescription())
                        .image(stampCardDTO.getImage())
                        .numberOfUsersCollect(stampCardDTO.getNumberOfUsersCollect())
                        .build()
        );
    }
}