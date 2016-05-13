package be.ordina.ordineo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.hateoas.Identifiable;
import org.springframework.hateoas.Resource;
import org.springframework.hateoas.Resources;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by gide on 11/04/16.
 */
@Getter
@Setter
@Entity
@Table(uniqueConstraints=
        @UniqueConstraint(columnNames = {"username", "objective_id"}, name = "UC_USERNAME_OBJECTIVEID"))
public class Milestone implements Identifiable<Long> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min=2 ,max = 30)
    private String username;

    @NotNull
    @ManyToOne(cascade = CascadeType.DETACH)
    private Objective objective;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    @Size(max=5000)
    private String moreInformation;

    @OneToMany(cascade= CascadeType.ALL, fetch = FetchType.EAGER, mappedBy = "milestone")
    private List<Comment> comments;

}
