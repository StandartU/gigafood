package ru.gigafood.backend.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.ReportingPolicy;

import ru.gigafood.backend.dto.DishDto;
import ru.gigafood.backend.entity.Meal;

@Mapper(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MealMapper {

    public void updateMealFromDto(DishDto.redactRequest dto, @MappingTarget Meal meal);
}
