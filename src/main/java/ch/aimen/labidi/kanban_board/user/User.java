package ch.aimen.labidi.kanban_board.user;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ch.aimen.labidi.kanban_board.board.Board;
import ch.aimen.labidi.kanban_board.task.Task;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 100)
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank @Email @Size(max = 255)
    @Column(nullable = false, unique = true)
    private String email;

    @Column(unique = true)
    private String keycloakId;

    @JsonIgnore
    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Board> boards = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "assignee")
    private List<Task> assignedTasks = new ArrayList<>();
}