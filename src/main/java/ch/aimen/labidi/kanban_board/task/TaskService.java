package ch.aimen.labidi.kanban_board.task;

import ch.aimen.labidi.kanban_board.column.BoardColumn;
import ch.aimen.labidi.kanban_board.column.BoardColumnService;
import ch.aimen.labidi.kanban_board.exception.ResourceNotFoundException;
import ch.aimen.labidi.kanban_board.user.User;
import ch.aimen.labidi.kanban_board.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TaskService {

    private final TaskRepository taskRepository;
    private final BoardColumnService columnService;
    private final UserService userService;

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findByColumn(Long columnId) {
        return taskRepository.findByColumnIdOrderByPositionAsc(columnId);
    }

    public Task findById(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Task mit ID " + id + " nicht gefunden"));
    }

    public Task create(Task task, Long columnId, Long assigneeId) {
        BoardColumn column = columnService.findById(columnId);
        task.setColumn(column);
        if (assigneeId != null) {
            User assignee = userService.findById(assigneeId);
            task.setAssignee(assignee);
        }
        return taskRepository.save(task);
    }

    public Task update(Long id, Task updated) {
        Task existing = findById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        existing.setPosition(updated.getPosition());
        return taskRepository.save(existing);
    }

    public void delete(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new ResourceNotFoundException("Task mit ID " + id + " nicht gefunden");
        }
        taskRepository.deleteById(id);
    }
}
