package ch.aimen.labidi.kanban_board.board;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ch.aimen.labidi.kanban_board.column.BoardColumn;
import ch.aimen.labidi.kanban_board.user.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Data @Entity @Table(name = "boards")
@NoArgsConstructor @AllArgsConstructor
public class Board {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank @Size(max = 150)
    @Column(nullable = false)
    private String title;

    @Size(max = 1000)
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id")
    private User owner;

    @OneToMany(mappedBy = "board", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<BoardColumn> columns = new ArrayList<>();
}