package ch.aimen.labidi.kanban_board.board;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/boards")
@RequiredArgsConstructor @Tag(name = "Boards")
public class BoardController {
    private final BoardService boardService;

    @GetMapping @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public List<Board> getAll() { return boardService.findAll(); }

    @GetMapping("/{id}") @PreAuthorize("hasAnyRole('USER','ADMIN')")
    public Board getById(@PathVariable Long id) { return boardService.findById(id); }

    @PostMapping @PreAuthorize("hasRole('ADMIN')") @ResponseStatus(HttpStatus.CREATED)
    public Board create(@Valid @RequestBody Board board, @RequestParam Long ownerId) {
        return boardService.create(board, ownerId);
    }

    @PutMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public Board update(@PathVariable Long id, @Valid @RequestBody Board board) {
        return boardService.update(id, board);
    }

    @DeleteMapping("/{id}") @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        boardService.delete(id);
        return ResponseEntity.noContent().build();
    }
}