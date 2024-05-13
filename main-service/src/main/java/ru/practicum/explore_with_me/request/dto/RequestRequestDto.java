package ru.practicum.explore_with_me.request.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.explore_with_me.enums.RequestStatus;

import javax.validation.constraints.NotNull;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestRequestDto {
    @NotNull
    private List<Long> requestIds;
    @NotNull
    private RequestStatus status;
}
