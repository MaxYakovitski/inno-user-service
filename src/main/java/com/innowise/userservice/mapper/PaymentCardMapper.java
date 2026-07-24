package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardUpdateDto;
import com.innowise.userservice.entity.PaymentCard;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring",
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentCardMapper {

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active",  ignore = true)
    PaymentCard toEntity(PaymentCardCreateDto dto);

    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "active",  ignore = true)
    void updateEntity(PaymentCardUpdateDto dto, @MappingTarget PaymentCard entity);

    @Mapping(source = "user.id",  target = "userId")
    PaymentCardResponseDto toDto(PaymentCard entity);
}
