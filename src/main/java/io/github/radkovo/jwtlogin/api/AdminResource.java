/**
 * AdminResource.java
 *
 * Created on 30. 4. 2021, 15:42:11 by burgetr
 */
package io.github.radkovo.jwtlogin.api;

import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.github.radkovo.jwtlogin.dao.UserService;
import io.github.radkovo.jwtlogin.data.User;
import io.github.radkovo.jwtlogin.data.UserDTO;

/**
 * 
 * @author burgetr
 */
@Path("admin")
public class AdminResource
{
    @Inject
    JsonWebToken token;
    
    @Inject
    Principal principal;
    
    @Inject
    UserService userService;

    @GET
    @Path("init")
    @Produces(MediaType.TEXT_PLAIN)
    public String init()
    {
        Optional<User> admin = userService.getUser("admin");
        if (!admin.isPresent())
        {
            userService.createDefaultAdmin();
            return "created";
        }
        else
            return "ok";
    }

    @GET
    @Path("whoami")
    @RolesAllowed("admin")
    public String whoami() 
    {
        String login = (principal != null) ? principal.getName() : "unknown";
        return "Hello, " + login + " " + token.getGroups() + "!";
    }
    
    @GET
    @Path("user")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUsers()
    {
        List<UserDTO> list = userService.getUsers().stream().map(u -> new UserDTO(u)).collect(Collectors.toList());
        return Response.ok(list).build();
    }
    
}
