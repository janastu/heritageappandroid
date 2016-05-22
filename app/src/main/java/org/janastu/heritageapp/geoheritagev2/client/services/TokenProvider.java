package org.janastu.heritageapp.geoheritagev2.client.services;

/**
 * Created by Graphics-User on 2/9/2016.
 */

import org.janastu.heritageapp.geoheritagev2.client.pojo.Hex;
import org.janastu.heritageapp.geoheritagev2.client.pojo.Token;
import org.janastu.heritageapp.geoheritagev2.client.pojo.UserDetails;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TokenProvider {

    private final String secretKey="f8cc66c540868fdfe97e02abb0fd08638a5284ea";
    private final int tokenValidity=864000;

    public TokenProvider( ) {
      //  this.secretKey = secretKey;
        //this.tokenValidity = tokenValidity;
    }

    public Token createToken(UserDetails userDetails) {
        long expires = System.currentTimeMillis() + 1000L * tokenValidity;
        String token = userDetails.getUsername() + ":" + expires + ":" + computeSignature(userDetails, expires);
        return new Token(token, expires);
    }

    public String computeSignature(UserDetails userDetails, long expires) {
        StringBuilder signatureBuilder = new StringBuilder();
        signatureBuilder.append(userDetails.getUsername()).append(":");
        signatureBuilder.append(expires).append(":");
        signatureBuilder.append(userDetails.getPassword()).append(":");
        signatureBuilder.append(secretKey);

        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("No MD5 algorithm available!");
        }
        return new String(Hex.encode(digest.digest(signatureBuilder.toString().getBytes())));
    }

    public String getUserNameFromToken(String authToken) {
        if (null == authToken) {
            return null;
        }
        String[] parts = authToken.split(":");
        return parts[0];
    }

    public boolean validateToken(String authToken, UserDetails userDetails) {
        String[] parts = authToken.split(":");
        long expires = Long.parseLong(parts[1]);
        String signature = parts[2];
        String signatureToMatch = computeSignature(userDetails, expires);
        return expires >= System.currentTimeMillis() && constantTimeEquals(signature, signatureToMatch);
    }

    /**
     * String comparison that doesn't stop at the first character that is different but instead always
     * iterates the whole string length to prevent timing attacks.
     */
    private boolean constantTimeEquals(String a, String b) {
        if (a.length() != b.length()) {
            return false;
        } else {
            int equal = 0;
            for (int i = 0; i < a.length(); i++) {
                equal |= a.charAt(i) ^ b.charAt(i);
            }
            return equal == 0;
        }
    }

}
