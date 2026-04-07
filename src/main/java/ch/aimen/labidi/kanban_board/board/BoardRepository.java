package ch.aimen.labidi.kanban_board.board;

import ch.aimen.labidi.kanban_board.board.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findByOwnerId(Long ownerId);
}