package ru.oleg.rsoi.service.client.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.oleg.rsoi.service.client.domain.Client;
import ru.oleg.rsoi.service.client.repository.ClientRepository;

import java.util.Collections;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    ClientRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        String t = new BCryptPasswordEncoder().encode("test");
        Client client = repository.findByUsername(username);
        if (client == null) {
            throw new UsernameNotFoundException(username);
        }
        return new User(client.getUsername(), client.getPassword(), Collections.emptyList());
    }
}
