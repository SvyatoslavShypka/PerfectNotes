package com.goit.finalproject.note;

import com.goit.finalproject.access.Access;
import com.goit.finalproject.user.UserService;
import com.goit.finalproject.validation.Validator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@Slf4j
@Controller
@RequestMapping("/note")
@RequiredArgsConstructor
public class NoteController {
    private final NoteService noteService;
    private final UserService userService;
    private static final String REDIRECT = "redirect:/note/list";

    @GetMapping(value = "/")
    public String getNotes() {
        return REDIRECT;
    }

    @GetMapping(value = "/list")
    public ModelAndView getListNotes(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size
    ) {
        ModelAndView result = new ModelAndView("note/notesList");
        List<NoteDto> notes = noteService.listAll();
        result.addObject("notes",
                noteService.getPageNoteDto(notes, PageRequest.of(page, size)));
        return result;
    }

    @GetMapping(value = "/create")
    public ModelAndView getCreatePage() {
        return new ModelAndView("note/noteCreate");
    }

    @PostMapping(value = "/create")
    public String createNewNote(@ModelAttribute NoteDto noteDto) {
        Long userId = userService.getUserId();
        noteService.add(noteDto, userId);
        log.info("{} created new note {}", userId, noteDto.getTitle());
        return REDIRECT;
    }

    @GetMapping(value = "/edit/{id}")
    public ModelAndView getEditPage(@PathVariable Long id) {
        ModelAndView result = new ModelAndView("note/noteEdit");
        NoteDto noteDto = noteService.getById(id);
        Validator.validateUserId(userService.getUserId(), noteDto.getUserId());
        result.addObject("note", noteDto);
        result.addObject("noteAccess", noteDto.getAccess().equals(Access.PUBLIC));
        return result;
    }

    @PostMapping(value = "/edit")
    public String editNote(@ModelAttribute NoteDto noteDto) {
        noteService.updateNoteDto(noteDto);
        log.info("{} user edited note {}", noteDto.getUserId(), noteDto.getId());
        return REDIRECT;
    }

    @GetMapping(value = "/share/{id}")
    public ModelAndView getSharePage(@PathVariable Long id) {
        try {
            NoteDto noteDto = noteService.getById(id);
            if (noteDto.getAccess() == Access.PUBLIC) {
                ModelAndView result = new ModelAndView("note/publicNote");
                result.addObject("note", noteDto);
                return result;
            } else {
                return new ModelAndView("note/publicErrorNote");
            }
        } catch (Exception e) {
            return new ModelAndView("note/publicErrorNote");
        }

    }

    @PostMapping(value = "/delete/{id}")
    public String deleteNoteById(@PathVariable Long id) {
        noteService.deleteById(id);
        log.info("deleted note {}", id);
        return REDIRECT;
    }
}

