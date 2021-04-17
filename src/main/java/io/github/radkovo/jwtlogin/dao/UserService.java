package io.github.radkovo.jwtlogin.dao;

import java.util.Optional;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import io.github.radkovo.jwtlogin.data.User;
import io.github.radkovo.jwtlogin.data.UserDTO;

/**
 *
 * @author burgetr
 */
@Stateless
public class UserService 
{

    @PersistenceContext(unitName = "usersPU")
    EntityManager em;
    
    @Inject
    Pbkdf2PasswordHash passwordHasher;
    
    @Transactional
    public User createUser(UserDTO dto)
    {
        User newUser = new User(dto.getUsername(),
                passwordHasher.generate(dto.getPassword().toCharArray()),
                dto.getName(), dto.getEmail());
        em.persist(newUser);
        em.flush();
        return newUser;
    }
    
    public Optional<User> getUser(String username)
    {
        return em.createNamedQuery("User.byUsername", User.class)
                 .setParameter("username", username)
                 .getResultList()
                 .stream()
                 .findFirst();
    }

    public boolean verifyUser(String username, String password)
    {
        User user = getUser(username).orElse(null);
        if (user != null)
            return passwordHasher.verify(password.toCharArray(), user.getPassword());
        else
            return false;
    }
    
}
