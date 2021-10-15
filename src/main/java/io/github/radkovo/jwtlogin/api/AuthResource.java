package io.github.radkovo.jwtlogin.api;

import java.security.Principal;

import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.mail.MessagingException;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.github.radkovo.jwtlogin.JwtTokenGenerator;
import io.github.radkovo.jwtlogin.dao.LogService;
import io.github.radkovo.jwtlogin.dao.UserService;
import io.github.radkovo.jwtlogin.data.CaptchaResponse;
import io.github.radkovo.jwtlogin.data.Credentials;
import io.github.radkovo.jwtlogin.data.LogEntry;
import io.github.radkovo.jwtlogin.data.MessageResponse;
import io.github.radkovo.jwtlogin.data.PasswordChallenge;
import io.github.radkovo.jwtlogin.data.RegisterUserDTO;
import io.github.radkovo.jwtlogin.data.ResultResponse;
import io.github.radkovo.jwtlogin.data.TokenResponse;
import io.github.radkovo.jwtlogin.data.User;
import io.github.radkovo.jwtlogin.data.UserDTO;
import io.github.radkovo.jwtlogin.service.MailerService;


/**
 *
 * @author burgetr
 */
@Path("auth")
public class AuthResource 
{
    private static final long TOKEN_DURATION = 7200; // token duration in seconds
    
    @Inject
    Principal principal;
    
    @Inject
    UserService userService;
    
    @Inject
    LogService logService;
    
    @Inject
    MailerService mailer;
    
    @Inject
    @ConfigProperty(name = "jwtauth.privatekey.location", defaultValue = "")
    String privateKeyLocation;
    
    @Inject
    @ConfigProperty(name = "jwtauth.captcha.secret", defaultValue = "")
    String captchaSecret;

    
    @GET
    public String ping() {
        return "Ping OK";
    }
    
    @GET
    @Path("userInfo")
    @PermitAll //TODO allow only for user & admin?
    public Response getUserInfo()
    {
        String login = (principal != null) ? principal.getName() : "unknown";
        User user = userService.getUser(login).orElse(null);
        if (user != null)
            return Response.ok(new ResultResponse("ok", new UserDTO(user))).build();
        else
            return Response.ok(new ResultResponse("unknown", null)).build();
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
                String token = JwtTokenGenerator.generateJWTString(credentials.getUsername(), 
                        user.getEmail(), TOKEN_DURATION, user.getRoles(), privateKeyLocation);
                TokenResponse resp = new TokenResponse(token);
                logService.log(new LogEntry("auth", "login", user.getUsername(), "Successfull login"));
                return Response.ok(resp).build();
            } catch (Exception e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).entity(e).build();
            }
            
        } else {
            logService.log(new LogEntry("auth", "login", credentials.getUsername(), "Invalid login"));
            return Response.status(Status.FORBIDDEN).entity(new MessageResponse("invalid login")).build();
        }
    }
    
    @POST
    @Path("register")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registerUser(RegisterUserDTO data)
    {
        if (data.getUsername() != null && data.getPassword() != null && data.getCaptchaToken() != null)
        {
            if (data.getUsername().length() >= 3
                    && data.getPassword().length() >= 6
                    && data.getUsername().matches("^[A-Za-z0-9][A-Za-z0-9-]*[A-Za-z0-9]$"))
            {
                CaptchaResponse resp = checkToken(data.getCaptchaToken());
                if (resp.isSuccess())
                {
                    User user = userService.getUser(data.getUsername()).orElse(null);
                    if (user == null)
                    {
                        try {
                            userService.createUser(data);
                            logService.log(new LogEntry("auth", "register", data.getUsername(), "User registration"));
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
                            .entity(new MessageResponse("captcha verification failed"))
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
    
    @POST
    @Path("resetPasswordChallenge")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPasswordChallenge(RegisterUserDTO data)
    {
        if (data.getUsername() != null && data.getCaptchaToken() != null)
        {
            CaptchaResponse resp = checkToken(data.getCaptchaToken());
            if (resp.isSuccess())
            {
                User user = userService.getUser(data.getUsername()).orElse(null);
                if (user == null)
                    user = userService.getUserByEmail(data.getUsername()).orElse(null);
                if (user != null && user.getEmail() != null)
                {
                    try
                    {
                        PasswordChallenge challenge = userService.createPasswordChallenge(user);
                        mailer.sendPasswordReset(challenge);
                        logService.log(new LogEntry("auth", "recover", data.getUsername(), "Password recovery request"));
                        return Response.ok(new MessageResponse("ok")).build();
                    } catch (MessagingException e) {
                        e.printStackTrace();
                        return Response.status(Status.INTERNAL_SERVER_ERROR)
                                .entity(new MessageResponse("E-mail sending failed"))
                                .build();
                    }
                }
                else
                {
                    return Response.status(Status.BAD_REQUEST)
                            .entity(new MessageResponse("We are sorry, we don't know such user"))
                            .build();
                }
            }
            else
            {
                return Response.status(Status.BAD_REQUEST)
                        .entity(new MessageResponse("captcha verification failed"))
                        .build();
            }
        }
        else
        {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new MessageResponse("username is required (may contain e-mail too)"))
                    .build();
        }
    }
    
    @GET
    @Path("verifyChallenge/{hash}")
    public Response verifyChallenge(@PathParam(value = "hash") String hash)
    {
        if (hash != null)
        {
            PasswordChallenge cal = userService.findChallenge(hash).orElse(null);
            if (cal != null)
            {
                return Response.ok(new UserDTO(cal.getUser())).build();
            }
            else
            {
                return Response.status(Status.NOT_FOUND).entity(new MessageResponse("Invalid or expired reset code")).build();
            }
        }
        else
        {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new MessageResponse("hash code missing"))
                    .build();
        }
    }
    
    @POST
    @Path("resetPassword")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response resetPassword(RegisterUserDTO data)
    {
        if (data.getUsername() != null && data.getPassword() != null && data.getCaptchaToken() != null)
        {
            PasswordChallenge cal = userService.findChallenge(data.getCaptchaToken()).orElse(null);
            if (cal != null && data.getUsername().equals(cal.getUser().getUsername()))
            {
                User user = cal.getUser();
                if (user != null)
                {
                    userService.updateUserPassword(user.getUsername(), data.getPassword());
                    userService.clearPasswordChallenges(user);
                    logService.log(new LogEntry("auth", "reset", data.getUsername(), "Password reset"));
                    return Response.ok(new MessageResponse("ok")).build();
                }
                else
                {
                    return Response.status(Status.BAD_REQUEST)
                            .entity(new MessageResponse("We are sorry, we don't know such user"))
                            .build();
                }
            }
            else
            {
                return Response.status(Status.BAD_REQUEST)
                        .entity(new MessageResponse("verification failed"))
                        .build();
            }
        }
        else
        {
            return Response.status(Status.BAD_REQUEST)
                    .entity(new MessageResponse("username, password and challenge code required"))
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
    
    //===============================================================================================================

    private CaptchaResponse checkToken(String token)
    {
        MultivaluedMap<String, String> map = new MultivaluedHashMap<String, String>();
        map.putSingle("secret", captchaSecret);
        map.putSingle("response", token);
        Client client = ClientBuilder.newClient();
        CaptchaResponse response = client.target("https://www.google.com/recaptcha/api/siteverify")
                .request().post(Entity.form(map), CaptchaResponse.class);
        return response;
    }

}
