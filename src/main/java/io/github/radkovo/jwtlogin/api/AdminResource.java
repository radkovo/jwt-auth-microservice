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
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.jwt.JsonWebToken;

import io.github.radkovo.jwtlogin.dao.LogService;
import io.github.radkovo.jwtlogin.dao.UserService;
import io.github.radkovo.jwtlogin.data.MessageResponse;
import io.github.radkovo.jwtlogin.data.PasswordDTO;
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
    
    @Inject
    LogService logService;

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
    
    @GET
    @Path("user/{username}")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getUser(@PathParam("username") String username)
    {
        User user = userService.getUser(username).orElse(null);
        if (user != null)
            return Response.ok(new UserDTO(user)).build();
        else
            return Response.status(Status.NOT_FOUND).entity(new MessageResponse("not found")).build();
    }
    
    @PUT
    @Path("user/{username}")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(@PathParam("username") String username, UserDTO userData)
    {
        if (username != null && userData != null)
        {
            User user = userService.updateUser(username, userData);
            if (user != null)
                return Response.ok(new UserDTO(user)).build();
            else
                return Response.status(Status.NOT_FOUND).entity(new MessageResponse("not found")).build();
        }
        else
            return Response.status(Status.BAD_REQUEST).entity(new MessageResponse("invalid parametres")).build();
    }
    
    @PUT
    @Path("user/{username}/password")
    @RolesAllowed("admin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response setUserPassword(@PathParam("username") String username, PasswordDTO pwData)
    {
        if (username != null && pwData != null && pwData.getNewPassword() != null)
        {
            User user = userService.updateUserPassword(username, pwData.getNewPassword());
            if (user != null)
                return Response.ok(new UserDTO(user)).build();
            else
                return Response.status(Status.NOT_FOUND).entity(new MessageResponse("not found")).build();
        }        
        else
            return Response.status(Status.BAD_REQUEST).entity(new MessageResponse("invalid parametres")).build();
    }
    
    @DELETE
    @Path("user/{username}")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deleteUser(@PathParam("username") String username)
    {
        if (username != null)
        {
            User user = userService.deleteUser(username);
            if (user != null)
                return Response.ok(new MessageResponse("ok")).build();
            else
                return Response.status(Status.NOT_FOUND).entity(new MessageResponse("not found")).build();
        }
        else
            return Response.status(Status.BAD_REQUEST).entity(new MessageResponse("invalid parametres")).build();
    }
    
    @GET
    @Path("log")
    @RolesAllowed("admin")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getLog()
    {
        var entries = logService.getEntries();
        return Response.ok(entries).build();
    }
    
}
