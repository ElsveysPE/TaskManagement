package org.elsveys.service;

import org.elsveys.entity.Credentials;
import org.elsveys.repository.CredentialsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    private CredentialsRepository credentialsRepository;

    @Override
    public UserDetails loadUserByUsername(String alias) throws UsernameNotFoundException {
        Credentials creds = credentialsRepository.findByAlias(alias)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return User.builder()
                .username(creds.getAlias())
                .password(creds.getPassword())
                .authorities("ROLE_" + creds.getUser().getUserRole().toString())
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled(creds.isDeleted())
                .build();
    }
}