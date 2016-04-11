package be.ordina.ordineo.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.hateoas.Identifiable;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Objective implements Identifiable<Long>{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name="title",unique = true)
    private String title;
    private String description;
    @NotNull
    @Enumerated(EnumType.STRING)
    private ObjectiveType objectiveType;
    @ElementCollection
    @Column(name = "tag")
    private List<String> tags = new ArrayList<>();

}


