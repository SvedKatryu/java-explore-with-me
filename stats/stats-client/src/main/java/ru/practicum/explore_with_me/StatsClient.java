package ru.practicum.explore_with_me;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Component
@Slf4j
@RequiredArgsConstructor
public class StatsClient {

    private final WebClient webClient;

    private final ResourceBundle resource = ResourceBundle.getBundle("messages");

    public void addStat(HitsDto hitsDto) {
        String uri = resource.getString("client.hits");
        log.info("StatsClient request on uri '{}'. Body '{}'.", uri, hitsDto);
        webClient
                .post()
                .uri(uri)
                .bodyValue(hitsDto)
                .retrieve()
                .bodyToMono(HitsDto.class)
                .block();
    }

    public List<StatsDto> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
                String uri = resource.getString("client.stats");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String request = String.format(uri, start.format(formatter), end.format(formatter),
                String.join(",", uris), unique);

        log.info("StatsClient request on uri '{}'.", request);
        return webClient.get()
                .uri(request)
                .retrieve()
                .bodyToFlux(StatsDto.class)
                .collectList()
                .block();
    }
}

