package io.github.radkovo.jwtlogin.api;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
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
    @Inject
    UserService userService;
    
    @GET
    public String ping() {
        return "Ping OK";
    }
    
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(Credentials credentials) 
    {
        if (credentials != null
                && credentials.getUsername() != null
                && credentials.getPassword() != null
                && userService.verifyUser(credentials.getUsername(), credentials.getPassword()))
        {
            try
            {
                String token = JwtTokenGenerator.generateJWTString("/jwt-token.json", credentials.getUsername());
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
    public Response registerUser(UserDTO data)
    {
        if (data.getUsername() != null && data.getPassword() != null)
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
                    .entity(new MessageResponse("username and password are required"))
                    .build();
        }

    }
    
}
