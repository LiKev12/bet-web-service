package com.bet.betwebservice.controller;

import com.bet.betwebservice.model.PodCardModel;
import com.bet.betwebservice.model.StampCardModel;
import com.bet.betwebservice.service.PodService;
import com.bet.betwebservice.service.StampService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@CrossOrigin("http://localhost:3000")
@RestController
@RequestMapping("/api/stamp")
public class StampController {

    private PodService podService;
    private StampService stampService;

    @Autowired
    public StampController(StampService stampService, PodService podService) {
        this.podService = podService;
        this.stampService = stampService;
    }

    @GetMapping("/discover") // TODO: fix name, FO if pathVariables or requestParam is best practice
    public Page<StampCardModel> getDiscoverPageStampCards(Pageable pageable) {
        return this.stampService.getDiscoverPageStampCards(pageable);
    }

    @GetMapping("/{idStamp}/pod")
    public Page<PodCardModel> getPodCardsAssociatedWithStamp(
            @PathVariable("idStamp") String idStamp,
            Pageable pageable
    ) {
        return this.podService.getPodCardsAssociatedWithStamp(idStamp, pageable);
    }
}
