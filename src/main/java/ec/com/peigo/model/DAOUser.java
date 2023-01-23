package ec.com.peigo.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user")
public class DAOUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column
    private String username;
    @Column
    @JsonIgnore
    private String password;
    @JsonIgnore
    @OneToMany(mappedBy="user",fetch=FetchType.EAGER)
    private Set<DAOAuthority> authorities;
}
