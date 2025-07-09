package com.example.GestionClinique.configuration.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User; // Important: import Spring Security's User class
import java.util.Collection;

@Getter
public class MonUserDetailsCustom extends User {

    private final Long id; // This is the ID you want to expose

    public MonUserDetailsCustom(Long id, String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities); // Call the constructor of Spring Security's User class
        this.id = id;
    }

    public MonUserDetailsCustom(Long id, String username, String password, boolean enabled, boolean accountNonExpired,
                                boolean credentialsNonExpired, boolean accountNonLocked,
                                Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
    }

}
