package ec.com.peigo.service;

import java.util.*;
import java.util.stream.Collectors;

import ec.com.peigo.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private AuthorityDao autorityDao;

    @Autowired
    private PasswordEncoder bcryptEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        DAOUser user = userDao.findByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
                getGrantedAuthorities(user.getAuthorities()));
    }

    private List<GrantedAuthority> getGrantedAuthorities(Set<DAOAuthority> authorities) {
        List<GrantedAuthority> grantedAuthorities = new ArrayList<>();
        for (DAOAuthority authority : authorities) {
            grantedAuthorities.add(new SimpleGrantedAuthority(authority.getName()));
        }
        return grantedAuthorities;
    }
    public DAOUser save(UserDTO user) {
        DAOUser newUser = new DAOUser();
        DAOAuthority rol = new DAOAuthority();
        newUser.setUsername(user.getUsername());
        newUser.setPassword(bcryptEncoder.encode(user.getPassword()));
        newUser = userDao.save(newUser);
        rol.setName(user.getAuthorities().iterator().next().getName());
        rol.setUser(newUser);
        autorityDao.save(rol);
        Set<DAOAuthority>  rolSet = new HashSet<DAOAuthority>();
        rolSet.add(rol);
        newUser.setAuthorities(rolSet);
        return newUser;
    }
}
