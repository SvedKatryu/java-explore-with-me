package ru.practicum.explore_with_me.service;

import ru.practicum.explore_with_me.HitsDto;
import ru.practicum.explore_with_me.StatsDto;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsService {

    HitsDto addStat(HitsDto hitsDto);

    List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique);
}

