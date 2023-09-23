package com.goit.finalproject.note;

import com.goit.finalproject.user.User;
import com.goit.finalproject.user.UserService;
import com.goit.finalproject.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NoteService {

    private final NoteRepository noteRepository;
    private final NoteMapper noteMapper;
    private final UserService userService;

    public List<NoteDto> listAll() {
        User user = userService.getUserById(userService.getUserId());
        return noteMapper.mapEntityToDto(noteRepository.findAllByUser(user));
    }

    public Page<NoteDto> getPageNoteDto(List<NoteDto> dtoList, Pageable pageable) {
        List<NoteDto> pageList = dtoList.stream()
                .skip(pageable.getOffset())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        return new PageImpl<>(pageList, pageable, dtoList.size());
    }

    public void add(NoteDto noteDto, Long userId) {
        if (noteDto.getUserId() == null) {
            if (userId != null) {
                noteDto.setUserId(userId);
            } else {
                noteDto.setUserId(userService.getUserId());
            }
        }
        Validator.validateNoteDto(noteDto);
        Note note = noteMapper.mapDtoToEntity(noteDto);
        noteRepository.save(note);
    }

    public void deleteById(Long id) {
        noteRepository.deleteById(id);
    }

    public void update(Note note) {
        Validator.validateNote(note);
        noteRepository.save(note);
    }

    public NoteDto getById(Long id) {
        Note note = noteRepository.findById(id).orElseThrow();
        NoteDto noteDto = noteMapper.mapEntityToDto(note);
//        Validator.validateUserId(userService.getUserId(), noteDto.getUserId());
        return noteDto;
    }

    public void updateNoteDto(NoteDto note) {
        update(noteMapper.mapDtoToEntity(note));
    }
}
