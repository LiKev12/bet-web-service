package com.bet.betwebservice.service;

import com.bet.betwebservice.dao.PodRepository;
import com.bet.betwebservice.model.PodCardModel;
import com.bet.betwebservice.model.PodPageModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PodService {
    private PodRepository podRepository;

    public PodService(PodRepository podRepository) {
        this.podRepository = podRepository;
    }

    public Page<PodCardModel> getDiscoverPagePodCards(Pageable pageable) {
        return this.podRepository.getDiscoverPagePodCards(pageable).map(
                podCardDTO -> PodCardModel.builder()
                    .id(podCardDTO.getId())
                    .name(podCardDTO.getName())
                    .description(podCardDTO.getDescription())
                    .image(podCardDTO.getImage())
                    .numberOfMembers(podCardDTO.getNumberOfMembers())
                    .build()
        );
    }

    public List<PodPageModel> getPodPage() {
        return null;
//        return this.podRepository.getPodPage();
    }

    public Page<PodCardModel> getPodCardsAssociatedWithStamp(String idStamp, Pageable pageable) {
        return this.podRepository.getPodCardsAssociatedWithStamp(idStamp, pageable).map(
                podCardDTO -> PodCardModel.builder()
                        .id(podCardDTO.getId())
                        .name(podCardDTO.getName())
                        .description(podCardDTO.getDescription())
                        .image(podCardDTO.getImage())
                        .numberOfMembers(podCardDTO.getNumberOfMembers())
                        .build()
        );
    }
}
