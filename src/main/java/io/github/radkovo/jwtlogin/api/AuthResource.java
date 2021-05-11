package io.github.radkovo.jwtlogin.api;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.github.radkovo.jwtlogin.JwtTokenGenerator;
import io.github.radkovo.jwtlogin.dao.UserService;
import io.github.radkovo.jwtlogin.data.Credentials;
import io.github.radkovo.jwtlogin.data.MessageResponse;
import io.github.radkovo.jwtlogin.data.TokenResponse;
import io.github.radkovo.jwtlogin.data.User;
import io.github.radkovo.jwtlogin.data.UserDTO;


/**
 *
 * @author burgetr
 */
@Path("auth")
public class AuthResource 
{
    private static final long TOKEN_DURATION = 7200; // token duration in seconds
    
    @Inject
    UserService userService;
    
    @GET
    public String ping() {
        return "Ping OK";
    }
    
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(Credentials credentials) 
    {
        if (credentials != null
                && credentials.getUsername() != null
                && credentials.getPassword() != null
                && userService.verifyUser(credentials.getUsername(), credentials.getPassword()))
        {
            try
            {
                User user = userService.getUser(credentials.getUsername()).orElse(null);
                String token = JwtTokenGenerator.generateJWTString("/jwt-token.json", credentials.getUsername(), 
                        TOKEN_DURATION, user.getRoles());
                TokenResponse resp = new TokenResponse(token);
                return Response.ok(resp).build();
            } catch (Exception e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
            }
            
        } else {
            return Response.status(Status.FORBIDDEN).entity(new MessageResponse("invalid login")).build();
        }
    }
    
    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(UserDTO data)
    {
        if (data.getUsername() != null && data.getPassword() != null)
        {
            if (data.getUsername().length() >= 3
                    && data.getPassword().length() >= 6
                    && data.getUsername().matches("^[A-Za-z0-9][A-Za-z0-9-]*[A-Za-z0-9]$"))
            {
                User user = userService.getUser(data.getUsername()).orElse(null);
                if (user == null)
                {
                    try {
                        userService.createUser(data);
                        return Response.ok(new MessageResponse("ok")).build();
                    } catch (Exception e) {
                        return Response.status(Status.INTERNAL_SERVER_ERROR)
                                .entity(e.getMessage())
                                .build();
                    }
                }
                else
                {
                    return Response.status(Status.BAD_REQUEST)
                            .entity(new MessageResponse("username already exists"))
                            .build();
                }
            }
            else
            {
                return Response.status(Status.BAD_REQUEST)
                        .entity(new MessageResponse("Invalid username or password"))
                        .build();
            }
        }
        else
        {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new MessageResponse("username and password are required"))
                    .build();
        }
    }
    
    @GET
    @Path("checkUserId/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response userExists(@PathParam("userId") String userId)
    {
        if (userId != null)
        {
            User user = userService.getUser(userId).orElse(null);
            if (user != null)
                return Response.ok(new MessageResponse("yes")).build();
            else
                return Response.ok(new MessageResponse("no")).build();
        }
        else
            return Response.ok(new MessageResponse("no")).build();
    }
    
}
