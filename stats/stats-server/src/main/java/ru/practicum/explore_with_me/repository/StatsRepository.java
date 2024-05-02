package ru.practicum.explore_with_me.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.explore_with_me.StatsDto;
import ru.practicum.explore_with_me.model.Stat;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface StatsRepository extends JpaRepository<Stat, Long> {
    @Query("SELECT new ru.practicum.explore_with_me.StatsDto(s.app, s.uri, COUNT(s.ip)) " +
            "FROM Stat as s " +
            "WHERE s.timestamp > ?1 AND s.timestamp < ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatsDto> findAllStats(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.explore_with_me.StatsDto(s.app, s.uri, COUNT(DISTINCT(s.ip))) " +
            "FROM Stat as s " +
            "WHERE s.timestamp > ?1 AND s.timestamp < ?2 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatsDto> findAllStatsUniqueIp(LocalDateTime start, LocalDateTime end);

    @Query("SELECT new ru.practicum.explore_with_me.StatsDto(s.app, s.uri, COUNT(s.ip))  " +
            "FROM Stat as s " +
            "WHERE s.timestamp > ?1 AND s.timestamp < ?2 AND s.uri IN ?3 " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatsDto> findAllStatsUrisIn(LocalDateTime start, LocalDateTime end, List<String> uris);

    @Query("SELECT new ru.practicum.explore_with_me.StatsDto(s.app, s.uri, COUNT(DISTINCT s.ip)) " +
            "FROM Stat as s " +
            "WHERE s.timestamp > ?1 AND s.timestamp < ?2 AND s.uri IN (?3)  " +
            "GROUP BY s.app, s.uri " +
            "ORDER BY COUNT(s.ip) DESC")
    List<StatsDto> findAllStatsUniqueIpUrisIn(LocalDateTime start, LocalDateTime end, List<String> uris);


}
