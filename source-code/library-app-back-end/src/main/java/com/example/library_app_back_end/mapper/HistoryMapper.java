package com.example.library_app_back_end.mapper;

import com.example.library_app_back_end.dto.HistoryDTO;
import com.example.library_app_back_end.entity.History;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring",uses={})
public interface HistoryMapper extends EntityMapper<HistoryDTO, History> {
    @Override
    @Named("id")
    HistoryDTO toDto(History entity);
}
