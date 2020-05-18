package com.ylz.movie.catalogservice.resources;

import com.ylz.movie.catalogservice.models.CatalogItem;
import com.ylz.movie.catalogservice.models.Movie;
import com.ylz.movie.catalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalogs")
public class CatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalogs(@PathVariable("userId") String userId) {

        UserRating userRating = restTemplate.getForObject("http://com.ylz.movie.ratings-service/ratings/users/" + userId, UserRating.class);

        return userRating.getRatings().stream().map(rating -> {
            Movie movie = restTemplate.getForObject("http://com.ylz.movie.info-service/movies/" + rating.getMovieId(), Movie.class);
            /*
            // ASYNC call here..
            Movie movie = webClientBuilder.build()
                    .get()
                    .uri("http://localhost:8082/movies/" + rating.getMovieId())
                    .retrieve()
                    .bodyToMono(Movie.class)
                    .block();*/

            return new CatalogItem(movie.getName(), "desc:" + movie.getMovieId(), rating.getRating());
        })
        .collect(Collectors.toList());
    }
}
