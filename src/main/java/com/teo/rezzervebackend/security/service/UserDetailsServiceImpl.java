package com.teo.foodzzzbackend.security.service;

import com.teo.foodzzzbackend.model.User;
import com.teo.foodzzzbackend.model.VerificationToken;
import com.teo.foodzzzbackend.repository.UserRepository;
import com.teo.foodzzzbackend.repository.VerificationTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Autowired
    private VerificationTokenRepository tokenRepository;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nu s-a putut găsi utilizatorul cu emailul: " + email));

        return UserDetailsImpl.build(user);
    }

    public User findUserByUsername(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Nu s-a putut găsi utilizatorul cu emailul: " + email));
    }

    public User getUser(String verificationToken) {
        User user = tokenRepository.findByToken(verificationToken).getUser();
        return user;
    }

    public VerificationToken getVerificationToken(String verificationToken) {
        return tokenRepository.findByToken(verificationToken.trim());
    }

    public void deleteVerificationToken(String verificationToken) {
        tokenRepository.deleteByToken(verificationToken);
    }


    public void createVerificationToken(User user, String token) {
        VerificationToken myToken = new VerificationToken(token, user);
        tokenRepository.save(myToken);
    }

    public void saveRegisteredUser(User user) {
        userRepository.save(user);
    }


}
