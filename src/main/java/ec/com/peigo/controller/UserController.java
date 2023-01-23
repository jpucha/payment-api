package ec.com.peigo.controller;

import ec.com.peigo.model.UserDTO;
import ec.com.peigo.service.JwtUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author jpucha
 * User Controller Class.
 */
@RestController
@CrossOrigin
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private JwtUserDetailsService userDetailsService;

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public ResponseEntity<?> saveUser(@RequestBody UserDTO user) throws Exception {
        return ResponseEntity.ok(userDetailsService.save(user));
    }
}
