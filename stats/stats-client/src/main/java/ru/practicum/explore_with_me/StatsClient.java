package ru.practicum.explore_with_me;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatsClient {

    private final WebClient webClient;

    public HitsDto addStat(HitsDto hitsDto) {
        String uri = "/hit";
        log.info("StatClient request on uri '{}'. Body '{}'.", uri, hitsDto);
        return webClient
                .post()
                .uri(uri)
                .bodyValue(hitsDto)
                .retrieve()
                .bodyToMono(HitsDto.class)
                .block();
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        String requestUrl = String.format("/stats?start=%s&end=%s&uris=%s&unique=%s",
                URLEncoder.encode(String.valueOf(start), StandardCharsets.UTF_8),
                URLEncoder.encode(String.valueOf(end), StandardCharsets.UTF_8),
                uris,
                unique);

        log.info("StatClient request on uri '{}'.", requestUrl);
        return webClient.get()
                .uri(requestUrl)
                .retrieve()
                .bodyToFlux(StatsDto.class)
                .collectList()
                .block();
    }
}

