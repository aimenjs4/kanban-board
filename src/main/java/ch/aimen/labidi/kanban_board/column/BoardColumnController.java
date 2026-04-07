package ch.aimen.labidi.kanban_board.column;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/columns")
@RequiredArgsConstructor
@Tag(name = "Columns", description = "Spalten innerhalb eines Boards")
public class BoardColumnController {

    private final BoardColumnService columnService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Alle Columns auflisten (optional gefiltert nach Board)")
    public List<BoardColumn> getAll(@RequestParam(required = false) Long boardId) {
        return boardId == null ? columnService.findAll() : columnService.findByBoard(boardId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Einzelne Column abrufen")
    public BoardColumn getById(@PathVariable Long id) {
        return columnService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Neue Column anlegen (nur Admin)")
    public BoardColumn create(@Valid @RequestBody BoardColumn column, @RequestParam Long boardId) {
        return columnService.create(column, boardId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Column aktualisieren (nur Admin)")
    public BoardColumn update(@PathVariable Long id, @Valid @RequestBody BoardColumn column) {
        return columnService.update(id, column);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Column loeschen (nur Admin)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        columnService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
