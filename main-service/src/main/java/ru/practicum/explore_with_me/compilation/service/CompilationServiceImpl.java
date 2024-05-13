package ru.practicum.explore_with_me.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.explore_with_me.compilation.dto.CompilationDto;
import ru.practicum.explore_with_me.compilation.dto.NewCompilationDto;
import ru.practicum.explore_with_me.compilation.dto.UpdateCompilationDto;
import ru.practicum.explore_with_me.compilation.mapper.CompilationMapper;
import ru.practicum.explore_with_me.compilation.model.Compilation;
import ru.practicum.explore_with_me.compilation.repository.CompilationRepository;
import ru.practicum.explore_with_me.error.exeption.NotFoundException;
import ru.practicum.explore_with_me.event.model.Event;
import ru.practicum.explore_with_me.event.repository.EventRepository;
import ru.practicum.explore_with_me.event.service.EventService;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventRepository eventRepository;
    private final CompilationMapper compilationMapper;
    private final EventService eventService;

    @Override
    @Transactional
    public CompilationDto addCompilation(NewCompilationDto newCompilationDto) {
        log.debug("Adding compilation {}", newCompilationDto);
        Compilation compilation = compilationMapper.toCompilation(newCompilationDto);
        List<Event> events = newCompilationDto.getEvents() == null ? new ArrayList<>()
                : eventRepository.findAllByIdIn(newCompilationDto.getEvents());
        events = eventService.fillWithConfirmedRequests(events);
        events = eventService.fillWithEventViews(events);
        compilation.setEvents(new HashSet<>(events));
        CompilationDto compilationDto = compilationMapper.toCompilationDto(compilationRepository.save(compilation));
        log.debug("Added new compilation {}", compilationDto);
        return compilationDto;
    }

    @Override
    @Transactional
    public void deleteCompilation(Long compId) {
        log.debug("Deleting compilation ID{}", compId);
        getCompilationIfPresent(compId);
        compilationRepository.deleteById(compId);
    }

    @Override
    @Transactional
    public CompilationDto updateCompilation(UpdateCompilationDto updateCompilationDto, Long compId) {
        log.debug("Updating compilation ID{}", compId);
        Compilation compilation = getCompilationIfPresent(compId);
        if (updateCompilationDto.getEvents() != null) {
            HashSet<Event> events = new HashSet<>(eventRepository.findAllByIdIn(updateCompilationDto.getEvents()));
            compilation.setEvents(events);
        }
        if (updateCompilationDto.getPinned() != null) compilation.setPinned(updateCompilationDto.getPinned());
        if (updateCompilationDto.getTitle() != null) compilation.setTitle(updateCompilationDto.getTitle());

        return compilationMapper.toCompilationDto(compilationRepository.save(compilation));
    }

    @Override
    @Transactional(readOnly = true)
    public CompilationDto getCompilation(Long compId) {
        log.debug("Getting compilation ID{}", compId);
        return compilationMapper.toCompilationDto(getCompilationIfPresent(compId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CompilationDto> getCompilations(Boolean pined, PageRequest pageRequest) {
        log.debug("Getting compilations");
        return (pined == null) ? compilationMapper.toCompilationDto(compilationRepository.findAll(pageRequest).toList())
                : compilationMapper.toCompilationDto(compilationRepository.findAllByPinned(pined, pageRequest).toList());

    }

    private Compilation getCompilationIfPresent(Long compId) {
        return compilationRepository.findById(compId).orElseThrow(() ->
                new NotFoundException(String.format("Compilation ID%d not found", compId)));
    }
}
