package ec.com.peigo.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Table(name = "authorities")
public class DAOAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idAuthority;

    private String name;

    @ManyToOne
    @JoinColumn(name = "id")
    private DAOUser user;

}
