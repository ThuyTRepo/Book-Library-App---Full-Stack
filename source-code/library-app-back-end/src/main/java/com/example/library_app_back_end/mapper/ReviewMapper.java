package com.example.library_app_back_end.mapper;

import com.example.library_app_back_end.dto.ReviewDTO;
import com.example.library_app_back_end.entity.Review;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring",uses={})
public interface ReviewMapper extends EntityMapper<ReviewDTO, Review> {
    @Override
    @Named("id")
    ReviewDTO toDto(Review entity);
}