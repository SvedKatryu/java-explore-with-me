package ru.practicum.explore_with_me.service;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.HitsDto;
import ru.practicum.explore_with_me.StatsDto;
import ru.practicum.explore_with_me.mapper.StatsMapper;
import ru.practicum.explore_with_me.model.Stat;
import ru.practicum.explore_with_me.repository.StatsRepository;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsServiceImpl implements StatsService {

    private final StatsRepository statsRepository;
    private final StatsMapper mapper;

    @Override
    @Transactional
    public HitsDto addStat(HitsDto hitsDto) {
        Stat stat = statsRepository.save(mapper.toStat(hitsDto));
        log.info("StatsServiceImpl: сохранение в историю обращения {}", hitsDto.toString());
        return mapper.toHitsDto(stat);
    }

    @Override
    @Transactional(readOnly = true)
    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (uris == null || uris.isEmpty()) {
            return unique ? statsRepository.findAllStatsUniqueIp(start, end) :
                    statsRepository.findAllStats(start, end);
        } else {
            return unique ? statsRepository.findAllStatsUniqueIpUrisIn(start, end, uris) :
                    statsRepository.findAllStatsUrisIn(start, end, uris);
        }
    }
}
