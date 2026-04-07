package ch.aimen.labidi.kanban_board.board;

import ch.aimen.labidi.kanban_board.exception.ResourceNotFoundException;
import ch.aimen.labidi.kanban_board.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;


@Service @RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;
    private final UserService userService;

    public List<Board> findAll() { return boardRepository.findAll(); }

    public Board findById(Long id) {
        return boardRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Board mit ID " + id + " nicht gefunden"));
    }

    public Board create(Board board, Long ownerId) {
        board.setOwner(userService.findById(ownerId));
        return boardRepository.save(board);
    }

    public Board update(Long id, Board updated) {
        Board existing = findById(id);
        existing.setTitle(updated.getTitle());
        existing.setDescription(updated.getDescription());
        return boardRepository.save(existing);
    }

    public void delete(Long id) {
        if (!boardRepository.existsById(id))
            throw new ResourceNotFoundException("Board mit ID " + id + " nicht gefunden");
        boardRepository.deleteById(id);
    }
}