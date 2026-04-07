package ch.aimen.labidi.kanban_board.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByColumnIdOrderByPositionAsc(Long columnId);
    List<Task> findByAssigneeId(Long assigneeId);
}
