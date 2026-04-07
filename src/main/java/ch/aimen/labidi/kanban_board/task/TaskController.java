package ch.aimen.labidi.kanban_board.task;

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
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
@Tag(name = "Tasks", description = "Aufgaben innerhalb der Spalten")
public class TaskController {

    private final TaskService taskService;

    @GetMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Alle Tasks auflisten (optional gefiltert nach Column)")
    public List<Task> getAll(@RequestParam(required = false) Long columnId) {
        return columnId == null ? taskService.findAll() : taskService.findByColumn(columnId);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Einzelnen Task abrufen")
    public Task getById(@PathVariable Long id) {
        return taskService.findById(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Neuen Task anlegen (USER und ADMIN)")
    public Task create(@Valid @RequestBody Task task,
                       @RequestParam Long columnId,
                       @RequestParam(required = false) Long assigneeId) {
        return taskService.create(task, columnId, assigneeId);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @Operation(summary = "Task aktualisieren")
    public Task update(@PathVariable Long id, @Valid @RequestBody Task task) {
        return taskService.update(id, task);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Task loeschen (nur Admin)")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        taskService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
