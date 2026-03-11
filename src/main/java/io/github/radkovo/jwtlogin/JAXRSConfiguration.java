package io.github.radkovo.jwtlogin;

import jakarta.annotation.security.DeclareRoles;
import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

import org.eclipse.microprofile.auth.LoginConfig;

/**
 * Configures a JAX-RS endpoint.
 *
 * @author burgetr
 */
@ApplicationPath("/")
@LoginConfig(authMethod = "MP-JWT")
@DeclareRoles({ "admin", "user" })
public class JAXRSConfiguration extends Application {

}
