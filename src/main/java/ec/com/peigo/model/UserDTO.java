package ec.com.peigo.model;

import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private String username;
    private String password;
    private Set<DAOAuthority> authorities;
}
