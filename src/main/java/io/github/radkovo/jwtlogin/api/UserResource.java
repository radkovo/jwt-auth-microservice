/**
 * UserResource.java
 *
 * Created on 2. 10. 2021, 20:28:07 by burgetr
 */
package io.github.radkovo.jwtlogin.api;

import java.security.Principal;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.github.radkovo.jwtlogin.dao.UserService;
import io.github.radkovo.jwtlogin.data.MessageResponse;
import io.github.radkovo.jwtlogin.data.PasswordDTO;
import io.github.radkovo.jwtlogin.data.ResultResponse;
import io.github.radkovo.jwtlogin.data.User;
import io.github.radkovo.jwtlogin.data.UserDTO;

/**
 * 
 * @author burgetr
 */
@Path("user")
@RolesAllowed("user")
public class UserResource
{
    @Inject
    Principal principal;
    
    @Inject
    UserService userService;
    
    @GET
    @Path("/")
    public Response getUserInfo()
    {
        String login = (principal != null) ? principal.getName() : "unknown";
        User user = userService.getUser(login).orElse(null);
        if (user != null)
            return Response.ok(new ResultResponse("ok", new UserDTO(user))).build();
        else
            return Response.ok(new ResultResponse("unknown", null)).build();
    }
    
    @PUT
    @Path("/")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updateUser(UserDTO userData)
    {
        final String username = (principal != null) ? principal.getName() : null;
        if (username != null && userData != null)
        {
            userData.setUsername(null); //prevent users from changing their username and roles
            userData.setRoles(null);
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
    @Path("/password")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response updatePasword(PasswordDTO pwData)
    {
        final String username = (principal != null) ? principal.getName() : null;
        if (username != null && pwData != null && pwData.getOldPassword() != null && pwData.getNewPassword() != null)
        {
            if (userService.verifyUser(username, pwData.getOldPassword()))
            {
                User user = userService.updateUserPassword(username, pwData.getNewPassword());
                if (user != null)
                    return Response.ok(new UserDTO(user)).build();
                else
                    return Response.status(Status.NOT_FOUND).entity(new MessageResponse("not found")).build();
            }
            else
                return Response.status(Status.BAD_REQUEST).entity(new MessageResponse("Current passwod does not match")).build();
        }
        else
            return Response.status(Status.BAD_REQUEST).entity(new MessageResponse("invalid parametres")).build();
    }

}
