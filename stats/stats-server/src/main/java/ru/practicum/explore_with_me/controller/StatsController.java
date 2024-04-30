package ru.practicum.explore_with_me.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.explore_with_me.HitsDto;
import ru.practicum.explore_with_me.StatsDto;
import ru.practicum.explore_with_me.error.exeption.BadRequestException;
import ru.practicum.explore_with_me.error.exeption.ValidationException;
import ru.practicum.explore_with_me.service.StatsServiceImpl;

import javax.validation.constraints.NotBlank;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
public class StatsController {
    private final StatsServiceImpl service;

    @PostMapping("/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public HitsDto addStat(@RequestBody HitsDto hitsDto) {
        log.info("StatsController uri '{}', request body '{}'.", "/hit", hitsDto);
        return service.addStat(hitsDto);
    }

    @GetMapping("/stats")
    public List<StatsDto> getStats(
            @RequestParam @NotBlank String start,
            @RequestParam @NotBlank String end,
            @RequestParam(required = false) List<String> uris,
            @RequestParam(defaultValue = "false") boolean unique) {
        LocalDateTime decodedStart = decodeLocalDateTime(start);
        LocalDateTime decodedEnd = decodeLocalDateTime(end);

        log.info("StatsController uri '{}', start = '{}', end = '{}', uris = '{}', unique = '{}'.", "/stats", start,
                end, uris, unique);
        checkDates(decodedStart, decodedEnd);
        return service.getStats(decodedStart, decodedEnd, uris, unique);
    }

    private void checkDates(LocalDateTime start, LocalDateTime end) {
        if (start.isAfter(end)) {
            throw new ValidationException("Incorrect date. End date should be after start date.");
        }
    }

    private LocalDateTime decodeLocalDateTime(String encodedDateTime) {
        String decodedDateTime = URLDecoder.decode(encodedDateTime, StandardCharsets.UTF_8);
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        try {
            return LocalDateTime.parse(decodedDateTime, dateTimeFormatter);
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Incorrect date.");
        }
    }
}
