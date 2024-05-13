package ru.practicum.explore_with_me.location.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explore_with_me.location.dto.LocationDto;
import ru.practicum.explore_with_me.location.model.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {
    @Mapping(target = "id", ignore = true)
    Location toLocation(LocationDto locationDto);
}
