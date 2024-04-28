package ru.practicum.explore_with_me.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explore_with_me.HitsDto;
import ru.practicum.explore_with_me.model.Stat;

@Mapper(componentModel = "spring")
public interface StatsMapper {
    Stat toStat(HitsDto hitsDto);

    HitsDto toHitsDto(Stat stat);
}
