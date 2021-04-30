/**
 * UserDTO.java
 *
 * Created on 17. 4. 2021, 20:01:02 by burgetr
 */
package io.github.radkovo.jwtlogin.data;

import java.util.Set;

/**
 * 
 * @author burgetr
 */
public class UserDTO
{
    private String username;
    
    private String password;
    
    private String name;
    
    private String email;
    
    private Set<String> roles;
    
    
    public UserDTO()
    {
    }

    public UserDTO(User user)
    {
        username = user.getUsername();
        password = null;
        name = user.getName();
        email = user.getEmail();
        roles = user.getRoles();
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

    public Set<String> getRoles()
    {
        return roles;
    }

    public void setRoles(Set<String> roles)
    {
        this.roles = roles;
    }

}
