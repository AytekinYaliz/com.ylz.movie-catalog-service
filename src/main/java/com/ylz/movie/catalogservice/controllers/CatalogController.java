package com.ylz.movie.catalogservice.controllers;

import com.ylz.movie.catalogservice.models.CatalogItem;
import com.ylz.movie.catalogservice.models.Movie;
import com.ylz.movie.catalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalogs")
public class CatalogController {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Value("${movie_info_service_base_url}")
    private String movieInfoServiceBaseUrl;

    @Value("${movie_ratings_service_base_url}")
    private String movieRatingsServiceBaseUrl;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalogs(@PathVariable("userId") String userId) {

        UserRating userRating = restTemplate.getForObject(movieRatingsServiceBaseUrl + "/ratings/users/" + userId, UserRating.class);

        return userRating.getRatings().stream().map(rating -> {
            Movie movie = restTemplate.getForObject(movieInfoServiceBaseUrl + "/movies/" + rating.getMovieId(), Movie.class);
            /* // ASYNC call here..
            Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();*/

            return new CatalogItem(movie.getName(), movie.getDescription(), rating.getRating());
        })
        .collect(Collectors.toList());
    }
}
