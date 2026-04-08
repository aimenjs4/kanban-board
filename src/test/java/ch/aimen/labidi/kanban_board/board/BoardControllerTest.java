package ch.aimen.labidi.kanban_board.board;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Controller slice test for BoardController.
 *
 * Uses @SpringBootTest + @AutoConfigureMockMvc so the real SecurityConfig
 * (with @EnableMethodSecurity) is active.  Authentication is injected via
 * SecurityMockMvcRequestPostProcessors.user() — this sets the SecurityContext
 * directly on the request, which works correctly even with STATELESS sessions
 * (unlike @WithMockUser, which uses SecurityContextHolder and is silently
 * overwritten by NullSecurityContextRepository in Spring Security 6+).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class BoardControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private BoardService boardService;

    // ── helpers ──────────────────────────────────────────────────────────────

    private static org.springframework.test.web.servlet.request.RequestPostProcessor asUser() {
        return user("testuser").authorities(new SimpleGrantedAuthority("ROLE_USER"));
    }

    private static org.springframework.test.web.servlet.request.RequestPostProcessor asAdmin() {
        return user("testadmin").authorities(new SimpleGrantedAuthority("ROLE_ADMIN"));
    }

    // ── tests ─────────────────────────────────────────────────────────────────

    @Test
    void getAll_asUser_returns200() throws Exception {
        Board b = new Board();
        b.setId(1L);
        b.setTitle("Sprint 1");
        when(boardService.findAll()).thenReturn(List.of(b));

        mockMvc.perform(get("/api/boards").with(asUser()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Sprint 1"));
    }

    @Test
    void getAll_unauthenticated_returns401() throws Exception {
        mockMvc.perform(get("/api/boards"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_asUser_returns403() throws Exception {
        Board b = new Board();
        b.setTitle("X");
        mockMvc.perform(post("/api/boards")
                        .with(asUser()).with(csrf())
                        .param("ownerId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(b)))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_asAdmin_returns201() throws Exception {
        Board b = new Board();
        b.setTitle("Sprint 2");
        when(boardService.create(any(Board.class), anyLong())).thenReturn(b);

        mockMvc.perform(post("/api/boards")
                        .with(asAdmin()).with(csrf())
                        .param("ownerId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(b)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Sprint 2"));
    }

    @Test
    void create_invalidBody_returns400() throws Exception {
        Board b = new Board(); // title missing → validation fails
        mockMvc.perform(post("/api/boards")
                        .with(asAdmin()).with(csrf())
                        .param("ownerId", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(b)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void delete_asAdmin_returns204() throws Exception {
        mockMvc.perform(delete("/api/boards/1").with(asAdmin()).with(csrf()))
                .andExpect(status().isNoContent());
    }
}
