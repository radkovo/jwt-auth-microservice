/**
 * User.java
 *
 * Created on 17. 4. 2021, 18:56:11 by burgetr
 */
package io.github.radkovo.jwtlogin.data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import javax.persistence.Column;

/**
 * 
 * @author burgetr
 */
@Entity
@Table(name = "users")
@NamedQueries({
    @NamedQuery(name = "User.all", query = "select us from User us order by us.id"),
    @NamedQuery(name = "User.byUsername", query = "select us from User us where us.username = :username")
})
public class User
{
    @Id
    @GeneratedValue
    private long id;
    
    @Column(nullable = false, unique = true)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    private String name;
    
    private String email;

    public User()
    {
    }

    public User(String username, String password, String name, String email)
    {
        this.username = username;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public long getId()
    {
        return id;
    }

    public void setId(long id)
    {
        this.id = id;
    }

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getName()
    {
        return name;
    }

    public void setName(String name)
    {
        this.name = name;
    }

    public String getEmail()
    {
        return email;
    }

    public void setEmail(String email)
    {
        this.email = email;
    }
    
}
