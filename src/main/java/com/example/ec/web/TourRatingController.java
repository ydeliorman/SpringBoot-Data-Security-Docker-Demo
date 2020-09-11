package com.example.ec.web;

import com.example.ec.domain.TourRating;
import com.example.ec.service.TourRatingService;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.AbstractMap;

@RestController
@RequestMapping(path = "/tours/{tourId}/ratings")
@NoArgsConstructor
public class TourRatingController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TourRatingController.class);
    private TourRatingService tourRatingService;
    private RatingAssembler assembler;

    @Autowired
    public TourRatingController(TourRatingService tourRatingService,
                                RatingAssembler assembler) {
        this.tourRatingService = tourRatingService;
        this.assembler = assembler;
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_CSR')")
    @ResponseStatus(HttpStatus.CREATED)
    public void createTourRating(@PathVariable(value = "tourId") int tourId, @RequestBody @Validated RatingDto ratingDto) {
        LOGGER.info("POST /tours/{}/ratings", tourId);
        tourRatingService.createNew(tourId, ratingDto.getCustomerId(), ratingDto.getScore(), ratingDto.getComment());
    }

    @PostMapping("/{score}")
    @PreAuthorize("hasRole('ROLE_CSR')")
    @ResponseStatus(HttpStatus.CREATED)
    public void createManyTourRatings(@PathVariable(value = "tourId") int tourId,
                                      @PathVariable(value = "score") int score,
                                      @RequestParam("customers") Integer[] customers) {
        LOGGER.info("POST /tours/{}/ratings/{}", tourId, score);
        tourRatingService.rateMany(tourId, score, customers);
    }

    @GetMapping
    public PagedResources<RatingDto> getAllRatingsForTour(@PathVariable(value = "tourId") int tourId, Pageable pageable,
                                                          PagedResourcesAssembler pagedAssembler) {
        LOGGER.info("GET /tours/{}/ratings", tourId);
        Page<TourRating> tourRatingPage = tourRatingService.lookupRatings(tourId, pageable);
        PagedResources<RatingDto> result = pagedAssembler.toResource(tourRatingPage, assembler);
        return result;
    }

    @GetMapping("/average")
    public AbstractMap.SimpleEntry<String, Double> getAverage(@PathVariable(value = "tourId") int tourId) {
        LOGGER.info("GET /tours/{}/ratings/average", tourId);
        return new AbstractMap.SimpleEntry<String, Double>("average", tourRatingService.getAverageScore(tourId));
    }

    @PutMapping
    @PreAuthorize("hasRole('ROLE_CSR')")
    public RatingDto updateWithPut(@PathVariable(value = "tourId") int tourId, @RequestBody @Validated RatingDto ratingDto) {
        LOGGER.info("PUT /tours/{}/ratings", tourId);
        return toDto(tourRatingService.update(tourId, ratingDto.getCustomerId(),
                ratingDto.getScore(), ratingDto.getComment()));
    }

    @PatchMapping
    @PreAuthorize("hasRole('ROLE_CSR')")
    public RatingDto updateWithPatch(@PathVariable(value = "tourId") int tourId, @RequestBody @Validated RatingDto ratingDto) {
        LOGGER.info("PATCH /tours/{}/ratings", tourId);
        return toDto(tourRatingService.updateSome(tourId, ratingDto.getCustomerId(),
                ratingDto.getScore(), ratingDto.getComment()));
    }

    @DeleteMapping("/{customerId}")
    @PreAuthorize("hasRole('ROLE_CSR')")
    public void delete(@PathVariable(value = "tourId") int tourId, @PathVariable(value = "customerId") int customerId) {
        LOGGER.info("DELETE /tours/{}/ratings/{}", tourId, customerId);
        tourRatingService.delete(tourId, customerId);
    }

    private RatingDto toDto(TourRating tourRating) {
        return assembler.toResource(tourRating);
    }


}
