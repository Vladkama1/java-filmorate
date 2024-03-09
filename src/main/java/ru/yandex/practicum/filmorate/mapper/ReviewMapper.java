package ru.yandex.practicum.filmorate.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.filmorate.dto.ReviewDTO;
import ru.yandex.practicum.filmorate.model.Review;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ReviewMapper {
    Review toModel(ReviewDTO reviewDTO);

    ReviewDTO toDTO(Review review);

    List<ReviewDTO> toListDTO(List<Review> reviewList);
}
