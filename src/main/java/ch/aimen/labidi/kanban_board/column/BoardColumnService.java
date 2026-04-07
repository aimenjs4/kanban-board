package ch.aimen.labidi.kanban_board.column;

import ch.aimen.labidi.kanban_board.board.Board;
import ch.aimen.labidi.kanban_board.board.BoardService;
import ch.aimen.labidi.kanban_board.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BoardColumnService {

    private final BoardColumnRepository columnRepository;
    private final BoardService boardService;

    public List<BoardColumn> findAll() {
        return columnRepository.findAll();
    }

    public List<BoardColumn> findByBoard(Long boardId) {
        return columnRepository.findByBoardIdOrderByPositionAsc(boardId);
    }

    public BoardColumn findById(Long id) {
        return columnRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Column mit ID " + id + " nicht gefunden"));
    }

    public BoardColumn create(BoardColumn column, Long boardId) {
        Board board = boardService.findById(boardId);
        column.setBoard(board);
        return columnRepository.save(column);
    }

    public BoardColumn update(Long id, BoardColumn updated) {
        BoardColumn existing = findById(id);
        existing.setTitle(updated.getTitle());
        existing.setPosition(updated.getPosition());
        return columnRepository.save(existing);
    }

    public void delete(Long id) {
        if (!columnRepository.existsById(id)) {
            throw new ResourceNotFoundException("Column mit ID " + id + " nicht gefunden");
        }
        columnRepository.deleteById(id);
    }
}
