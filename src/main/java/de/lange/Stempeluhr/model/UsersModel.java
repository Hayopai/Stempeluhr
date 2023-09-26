package de.lange.Stempeluhr.model;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode


@Entity
public class UsersModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String login;

    private String password;

    private String email;


    private String firstName;


    private String lastName;


    @OneToMany(mappedBy = "usersModel", cascade = CascadeType.ALL)
    private List<Zeiterfassung> zeiterfassungList;

}
