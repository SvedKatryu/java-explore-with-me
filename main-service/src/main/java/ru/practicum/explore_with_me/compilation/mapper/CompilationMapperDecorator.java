package ru.practicum.explore_with_me.compilation.mapper;

import org.springframework.beans.factory.annotation.Autowired;
import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.model.Compilation;
import ru.practicum.explore_with_me.event.mapper.EventMapper;

public abstract class CompilationMapperDecorator implements CompilationMapper {
    @Autowired
    private CompilationMapper compilationMapper;
    @Autowired
    private EventMapper eventMapper;

    public CompilationDto compilationDto(Compilation compilation) {
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilation);
        compilationDto.setEvents(eventMapper.toEventShortDto(compilation.getEvents()));
        return compilationDto;
    }
}
