package ch.aimen.labidi.kanban_board.task;

import ch.aimen.labidi.kanban_board.board.Board;
import ch.aimen.labidi.kanban_board.column.BoardColumn;
import ch.aimen.labidi.kanban_board.user.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jpa.test.autoconfigure.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class TaskRepositoryTest {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private TestEntityManager em;

    private Task createTask(String title, int position) {
        User user = new User();
        user.setUsername("u" + position);
        user.setEmail("u" + position + "@test.ch");
        em.persist(user);

        Board board = new Board();
        board.setTitle("Board");
        board.setOwner(user);
        em.persist(board);

        BoardColumn col = new BoardColumn();
        col.setTitle("To Do");
        col.setPosition(0);
        col.setBoard(board);
        em.persist(col);

        Task task = new Task();
        task.setTitle(title);
        task.setPosition(position);
        task.setColumn(col);
        return task;
    }

    @Test
    void create_shouldPersistAndSetCreatedAt() {
        Task saved = taskRepository.save(createTask("Doku schreiben", 0));
        em.flush();
        em.clear();

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
    }

    @Test
    void findByColumnId_shouldReturnOrderedByPosition() {
        Task t1 = createTask("Task A", 1);
        Task t2 = createTask("Task B", 0);

        // beide Tasks in derselben Column speichern
        User user = new User();
        user.setUsername("shared");
        user.setEmail("shared@test.ch");
        em.persist(user);
        Board board = new Board();
        board.setTitle("B");
        board.setOwner(user);
        em.persist(board);
        BoardColumn col = new BoardColumn();
        col.setTitle("Col");
        col.setPosition(0);
        col.setBoard(board);
        em.persist(col);

        t1.setColumn(col);
        t2.setColumn(col);
        taskRepository.save(t1);
        taskRepository.save(t2);
        em.flush();
        em.clear();

        List<Task> result = taskRepository.findByColumnIdOrderByPositionAsc(col.getId());
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getPosition()).isLessThan(result.get(1).getPosition());
    }

    @Test
    void delete_shouldRemoveTask() {
        Task saved = taskRepository.save(createTask("Zu loeschen", 0));
        em.flush();

        taskRepository.deleteById(saved.getId());
        em.flush();

        assertThat(taskRepository.findById(saved.getId())).isEmpty();
    }
}
