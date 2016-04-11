package be.ordina.ordineo.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;

/**
 * Created by gide on 11/04/16.
 */
@Getter
@Setter
@Entity
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Size(min=2 ,max = 30)
    private String username;

    //TODO add objective; not null

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate createDate;

    @NotNull
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate dueDate;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endDate;

    private String moreInformation;

}
