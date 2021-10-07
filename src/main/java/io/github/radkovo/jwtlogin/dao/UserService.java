package io.github.radkovo.jwtlogin.dao;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.security.enterprise.identitystore.Pbkdf2PasswordHash;
import javax.transaction.Transactional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.github.radkovo.jwtlogin.Roles;
import io.github.radkovo.jwtlogin.data.PasswordChallenge;
import io.github.radkovo.jwtlogin.data.User;
import io.github.radkovo.jwtlogin.data.UserDTO;

/**
 *
 * @author burgetr
 */
@Stateless
public class UserService 
{
    private static final Set<String> defaultRoles = Set.of(Roles.USER);
    private static final Set<String> adminRoles = Set.of(Roles.ADMIN, Roles.USER);
    private static final String adminUsername = "admin";
    
    @PersistenceContext(unitName = "usersPU")
    EntityManager em;
    
    @Inject
    @ConfigProperty(name = "jwtauth.admin.password")
    String defaultAdminPassword;
    
    @Inject
    Pbkdf2PasswordHash passwordHasher;
    
    @Transactional
    public User createUser(UserDTO dto)
    {
        User newUser = new User(dto.getUsername(),
                passwordHasher.generate(dto.getPassword().toCharArray()),
                dto.getName(), dto.getEmail());
        newUser.setRoles(defaultRoles);
        em.persist(newUser);
        em.flush();
        return newUser;
    }
    
    @Transactional
    public User createDefaultAdmin()
    {
        User newUser = new User(adminUsername,
                passwordHasher.generate(defaultAdminPassword.toCharArray()),
                "", "");
        newUser.setRoles(adminRoles);
        em.persist(newUser);
        em.flush();
        return newUser;
    }
    
    public List<User> getUsers()
    {
        return em.createNamedQuery("User.all", User.class).getResultList();
    }
    
    public Optional<User> getUser(String username)
    {
        return em.createNamedQuery("User.byUsername", User.class)
                 .setParameter("username", username)
                 .getResultList()
                 .stream()
                 .findFirst();
    }

    public Optional<User> getUserByEmail(String email)
    {
        return em.createNamedQuery("User.byEmail", User.class)
                 .setParameter("email", email)
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
    
    @Transactional
    public User updateUser(String username, UserDTO dto)
    {
        User user = getUser(username).orElse(null);
        if (user != null)
        {
            user.updateWith(dto);
            em.merge(user);
            em.flush();
            return user;
        }
        else
            return null;
    }
    
    @Transactional
    public User updateUserPassword(String username, String password)
    {
        User user = getUser(username).orElse(null);
        if (user != null)
        {
            user.setPassword(passwordHasher.generate(password.toCharArray()));
            em.merge(user);
            em.flush();
            return user;
        }
        else
            return null;
    }
    
    @Transactional
    public User deleteUser(String username)
    {
        User user = getUser(username).orElse(null);
        if (user != null)
        {
            clearPasswordChallenges(user);
            em.remove(user);
            em.flush();
            return user;
        }
        else
            return null;
    }

    //=====================================================================================
    
    @Transactional
    public PasswordChallenge createPasswordChallenge(User user)
    {
        clearPasswordChallenges(user);
        PasswordChallenge c = new PasswordChallenge(user);
        em.merge(c);
        em.flush();
        return c;
    }
    
    public Optional<PasswordChallenge> findChallenge(String hash)
    {
        return em.createNamedQuery("PasswordChallenge.byHash", PasswordChallenge.class)
                 .setParameter("hash", hash)
                 .getResultList()
                 .stream()
                 .findFirst();
    }

    public void clearPasswordChallenges(User user)
    {
        em.createNamedQuery("PasswordChallenge.clearUser")
            .setParameter("userId", user.getId())
            .executeUpdate();
    }
    
}
