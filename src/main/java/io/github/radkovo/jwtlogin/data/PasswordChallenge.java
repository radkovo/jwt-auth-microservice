/**
 * PasswordChallenge.java
 *
 * Created on 6. 10. 2021, 19:42:24 by burgetr
 */
package io.github.radkovo.jwtlogin.data;

import java.util.Date;
import java.util.Random;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Column;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import static javax.persistence.TemporalType.TIMESTAMP;
import javax.persistence.Table;

/**
 * 
 * @author burgetr
 */
@Entity
@Table(name = "challenges")
@NamedQueries({
    @NamedQuery(name = "PasswordChallenge.byHash", query = "select c from PasswordChallenge c where c.hash = :hash"),
    @NamedQuery(name = "PasswordChallenge.clearUser", query = "delete from PasswordChallenge c where c.user.id = :userId")
})
public class PasswordChallenge
{
    @Id
    private long id;
    
    @Column(length = 64)
    private String hash;
    
    @OneToOne
    private User user;
    
    @Temporal(TIMESTAMP)
    private Date issued;

    
    public PasswordChallenge()
    {
    }
    
    public PasswordChallenge(User user)
    {
        this.user = user;
        hash = generateRandomHash();
        issued = new Date();
    }
    
    public String getHash()
    {
        return hash;
    }

    public void setHash(String hash)
    {
        this.hash = hash;
    }

    public User getUser()
    {
        return user;
    }

    public void setUser(User user)
    {
        this.user = user;
    }

    public Date getIssued()
    {
        return issued;
    }

    public void setIssued(Date issued)
    {
        this.issued = issued;
    }

    public static String generateRandomHash() 
    {
        final int leftLimit = 48; // '0'
        final int rightLimit = 122; // 'z'
        final int targetStringLength = 64;
        Random random = new Random();

        return random.ints(leftLimit, rightLimit + 1)
          .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
          .limit(targetStringLength)
          .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
          .toString();
    }

}
