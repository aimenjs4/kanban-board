package ch.aimen.labidi.kanban_board.user;

import ch.aimen.labidi.kanban_board.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public List<User> findAll() { return userRepository.findAll(); }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User mit ID " + id + " nicht gefunden"));
    }

    public User create(User user) {
        if (userRepository.existsByUsername(user.getUsername()))
            throw new IllegalArgumentException("Username bereits vergeben: " + user.getUsername());
        if (userRepository.existsByEmail(user.getEmail()))
            throw new IllegalArgumentException("E-Mail bereits vergeben: " + user.getEmail());
        return userRepository.save(user);
    }

    public User update(Long id, User updated) {
        User existing = findById(id);
        existing.setUsername(updated.getUsername());
        existing.setEmail(updated.getEmail());
        return userRepository.save(existing);
    }

    public void delete(Long id) {
        if (!userRepository.existsById(id))
            throw new ResourceNotFoundException("User mit ID " + id + " nicht gefunden");
        userRepository.deleteById(id);
    }
}