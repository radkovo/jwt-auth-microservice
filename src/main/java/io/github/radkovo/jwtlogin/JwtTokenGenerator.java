package io.github.radkovo.jwtlogin;

import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.eclipse.microprofile.jwt.Claims;

import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;


/**
 * Based on the original implementation at
 * https://github.com/javaee-samples/microprofile1.4-samples 
 */
public class JwtTokenGenerator {

    public static String generateJWTString(String username, String email, long duration, Set<String> roles, String privateKeyUrl) throws Exception {

        long currentTimeInSecs = (System.currentTimeMillis() / 1000);
        long expirationTime = currentTimeInSecs + duration;
       
        Map<String, Object> jwtJson = new HashMap<>();
        jwtJson.put(Claims.iss.name(), "jwtauthserv");
        jwtJson.put(Claims.jti.name(), "25");
        jwtJson.put(Claims.iat.name(), currentTimeInSecs);
        jwtJson.put(Claims.auth_time.name(), currentTimeInSecs);
        jwtJson.put(Claims.exp.name(), expirationTime);
        jwtJson.put(Claims.sub.name(), username);
        jwtJson.put(Claims.upn.name(), username);
        if (email != null && !email.isBlank())
            jwtJson.put(Claims.email.name(), email);
        jwtJson.put(Claims.groups.name(), roles);
        
        SignedJWT signedJWT = new SignedJWT(new JWSHeader
                                            .Builder(JWSAlgorithm.RS256)
                                            .keyID("/privateKey.pem")
                                            .type(JOSEObjectType.JWT)
                                            .build(), JWTClaimsSet.parse(jwtJson));
        
        signedJWT.sign(new RSASSASigner(readPrivateKey(privateKeyUrl)));
        
        return signedJWT.serialize();
    }
    
    private static PrivateKey readPrivateKey(String location) {
        PrivateKey key = null;
        if (location == null || location.isEmpty()) {
            try
            {
                key = readPrivateKeyFromResource("/privateKey.pem");
            } catch (Exception e) {
                System.err.println("ERROR: Couldn't read private key from classpath. jwtauth.privatekey.location should be specified.");
                e.printStackTrace();
            }
        } else {
            try {
                key = readPrivateKeyFromUrl(location);
            } catch (Exception e) {
                System.err.println("ERROR: Couldn't read private key from " + location);
                e.printStackTrace();
            }                
        }
        return key;
    }
    
    private static PrivateKey readPrivateKeyFromResource(String resourceName) throws Exception {
        byte[] byteBuffer = new byte[16384];
        int length = Thread.currentThread().getContextClassLoader()
                                    .getResource(resourceName)
                                    .openStream()
                                    .read(byteBuffer);
        return generateKeyFromBuffer(byteBuffer, length);
    }

    private static PrivateKey readPrivateKeyFromUrl(String urlString) throws Exception {
        byte[] byteBuffer = new byte[16384];
        int length = (new URL(urlString))
                                    .openStream()
                                    .read(byteBuffer);
        return generateKeyFromBuffer(byteBuffer, length);
    }

    private static PrivateKey generateKeyFromBuffer(byte[] byteBuffer,
            int length) throws InvalidKeySpecException, NoSuchAlgorithmException
    {
        String key = new String(byteBuffer, 0, length).replaceAll("-----BEGIN (.*)-----", "")
                                                      .replaceAll("-----END (.*)----", "")
                                                      .replaceAll("\r\n", "")
                                                      .replaceAll("\n", "")
                                                      .trim();
        return KeyFactory.getInstance("RSA")
                         .generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(key)));
    }

}
