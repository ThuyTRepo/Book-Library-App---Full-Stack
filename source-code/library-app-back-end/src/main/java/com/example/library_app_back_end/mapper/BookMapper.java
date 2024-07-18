package com.example.library_app_back_end.mapper;

import com.example.library_app_back_end.dto.BookDTO;
import com.example.library_app_back_end.entity.Book;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring", uses = {})
public interface BookMapper extends EntityMapper<BookDTO, Book> {
    @Override
    @Named("id")
    BookDTO toDto(Book entity);
}
