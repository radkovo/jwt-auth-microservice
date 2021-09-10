package io.github.radkovo.jwtlogin;

import static com.nimbusds.jose.JOSEObjectType.JWT;
import static com.nimbusds.jose.JWSAlgorithm.RS256;
import static com.nimbusds.jwt.JWTClaimsSet.parse;
import static java.lang.Thread.currentThread;
import static net.minidev.json.parser.JSONParser.DEFAULT_PERMISSIVE_MODE;

import java.net.URL;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Set;

import org.eclipse.microprofile.jwt.Claims;

import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jwt.SignedJWT;

import net.minidev.json.JSONObject;
import net.minidev.json.parser.JSONParser;

/**
 * Based on the original implementation at
 * https://github.com/javaee-samples/microprofile1.4-samples 
 */
public class JwtTokenGenerator {

    public static String generateJWTString(String jsonResource, String username, long duration, Set<String> roles) throws Exception {
        byte[] byteBuffer = new byte[16384];
        currentThread().getContextClassLoader()
                       .getResource(jsonResource)
                       .openStream()
                       .read(byteBuffer);

        JSONParser parser = new JSONParser(DEFAULT_PERMISSIVE_MODE);
        JSONObject jwtJson = (JSONObject) parser.parse(byteBuffer);
        
        long currentTimeInSecs = (System.currentTimeMillis() / 1000);
        long expirationTime = currentTimeInSecs + duration;
       
        jwtJson.put(Claims.iat.name(), currentTimeInSecs);
        jwtJson.put(Claims.auth_time.name(), currentTimeInSecs);
        jwtJson.put(Claims.exp.name(), expirationTime);
        jwtJson.put(Claims.sub.name(), username);
        jwtJson.put(Claims.upn.name(), username);
        jwtJson.put(Claims.groups.name(), roles);
        
        SignedJWT signedJWT = new SignedJWT(new JWSHeader
                                            .Builder(RS256)
                                            .keyID("/privateKey.pem")
                                            .type(JWT)
                                            .build(), parse(jwtJson));
        
        signedJWT.sign(new RSASSASigner(readPrivateKey()));
        
        return signedJWT.serialize();
    }
    
    private static PrivateKey readPrivateKey() throws Exception {
        final String location = System.getProperty("mp.jwt.verify.publickey.location");
        if (location == null)
            return readPrivateKeyFromResource("/privateKey.pem");
        else
            return readPrivateKeyFromUrl(location);
    }
    
    private static PrivateKey readPrivateKeyFromResource(String resourceName) throws Exception {
        byte[] byteBuffer = new byte[16384];
        int length = currentThread().getContextClassLoader()
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
