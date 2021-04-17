package io.github.radkovo.jwtlogin.api;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import io.github.radkovo.jwtlogin.JwtTokenGenerator;
import io.github.radkovo.jwtlogin.data.Credentials;
import io.github.radkovo.jwtlogin.data.TokenResponse;


/**
 *
 * @author burgetr
 */
@Path("auth")
public class AuthResource 
{

    @GET
    public String ping() {
        return "Ping OK";
    }
    
    @POST
    @Path("login")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response login(Credentials credentials) {
        if (credentials != null
                && "test".equals(credentials.getLogin())
                && "secret".equals(credentials.getPassword())) {
            
            try
            {
                String token = JwtTokenGenerator.generateJWTString("/jwt-token.json", credentials.getLogin());
                TokenResponse resp = new TokenResponse(token);
                return Response.ok(resp).build();
            } catch (Exception e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
            }
            
        } else {
            return Response.status(Status.FORBIDDEN).entity("invalid login").build();
        }
    }
    
}
