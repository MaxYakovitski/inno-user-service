package com.innowise.userservice.mapper;

import com.innowise.userservice.dto.paymentcard.PaymentCardCreateDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardResponseDto;
import com.innowise.userservice.dto.paymentcard.PaymentCardUpdateDto;
import com.innowise.userservice.entity.PaymentCard;
import org.mapstruct.*;

@Mapper(componentModel = "spring",
        unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface PaymentCardMapper {

    PaymentCard toEntity(PaymentCardCreateDto dto);
    void updateEntity(PaymentCardUpdateDto dto, @MappingTarget PaymentCard entity);

    @Mapping(source = "user.id",  target = "userId")
    PaymentCardResponseDto toDto(PaymentCard entity);
}
