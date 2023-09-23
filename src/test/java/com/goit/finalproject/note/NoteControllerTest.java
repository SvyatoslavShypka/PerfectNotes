package com.goit.finalproject.note;

import com.goit.finalproject.access.Access;
import com.goit.finalproject.user.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.ArrayList;
import java.util.List;

@AutoConfigureMockMvc(addFilters = false)
@WebMvcTest(NoteController.class)
class NoteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private NoteService noteService;

    @Test
    void getNotes() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/note/"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/note/list"));
    }

    @Test
    void createNewNote() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/note/create"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/note/list"));
    }

    @Test
    void getCreatePage() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/note/create"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("note/noteCreate"));
    }

    @Test
    void getEditPage() throws Exception {
        NoteDto testNoteDto = getTestNoteDto();
        Mockito.when(noteService.getById(5L)).thenReturn(testNoteDto);
        Mockito.when(userService.getUserId()).thenReturn(testNoteDto.getUserId());
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/note/edit/" + 5L))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("note/noteEdit"))
                .andExpect(MockMvcResultMatchers.model().attribute("note", Matchers.equalTo(testNoteDto)))
                .andExpect(MockMvcResultMatchers.model().attribute("noteAccess", Matchers.equalTo(testNoteDto.getAccess().equals(Access.PUBLIC))));
    }

    @Test
    void editNote() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/note/edit"))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/note/list"));
    }

    @Test
    void getSharePage() throws Exception {
        NoteDto testNoteDto = getTestNoteDto();
        Mockito.when(noteService.getById(5L)).thenReturn(testNoteDto);
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/note/share/" + 5))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("note/publicNote"))
                .andExpect(MockMvcResultMatchers.model().attribute("note", Matchers.equalTo(testNoteDto)));
    }

    @Test
    void getShareErrorPage() throws Exception {
        NoteDto testNoteDto = getTestNoteDto();
        testNoteDto.setAccess(Access.PRIVATE);
        Mockito.when(noteService.getById(5L)).thenReturn(testNoteDto);
        this.mockMvc
                .perform(MockMvcRequestBuilders.get("/note/share/" + 5))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.view().name("note/publicErrorNote"));
    }

    @Test
    void deleteNoteById() throws Exception {
        this.mockMvc
                .perform(MockMvcRequestBuilders.post("/note/delete/" + 5))
                .andExpect(MockMvcResultMatchers.status().is3xxRedirection())
                .andExpect(MockMvcResultMatchers.redirectedUrl("/note/list"));
    }

    private List<NoteDto> createTestNotesList() {
        List<NoteDto> noteDtos = new ArrayList<>();
        for (long i = 0; i < 10; i++) {
            NoteDto noteDto = new NoteDto(i, "Title " + i, "Content " + i, Access.PUBLIC, 1L);
            noteDtos.add(noteDto);
        }
        return noteDtos;
    }

    private NoteDto getTestNoteDto() {
        return createTestNotesList().get(6);
    }
}

