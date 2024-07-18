package com.example.library_app_back_end.mapper;
import com.example.library_app_back_end.dto.MessageDTO;
import com.example.library_app_back_end.entity.Message;
import org.mapstruct.Mapper;
import org.mapstruct.Named;

@Mapper(componentModel = "spring",uses={})
public interface MessageMapper extends EntityMapper<MessageDTO, Message> {
    @Override
    @Named("id")
    MessageDTO toDto(Message entity);
}
