package ru.practicum.explore_with_me;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StatsDto {
    private String app;
    private String uri;
    private Long hits;
}
