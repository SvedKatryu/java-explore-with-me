package ru.practicum.explore_with_me.compilation.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationDto;
import ru.practicum.explore_with_me.compilation.dto.UpdateCompilationDto;

import java.util.List;

public interface CompilationService {
    CompilationDto addCompilation(NewCompilationDto newCompilationDto);

    void deleteCompilation(Long compId);

    CompilationDto updateCompilation(UpdateCompilationDto updateCompilationDto, Long compId);

    CompilationDto getCompilation(Long compId);

    List<CompilationDto> getCompilations(Boolean pined, PageRequest pageRequest);
}
