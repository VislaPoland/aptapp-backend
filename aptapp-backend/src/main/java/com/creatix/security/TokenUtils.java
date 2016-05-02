package com.creatix.security;

import com.creatix.domain.entity.Account;
import com.creatix.domain.entity.Gym;
import com.creatix.domain.entity.Trainer;
import com.creatix.domain.enums.Role;
import com.creatix.payment.StripePaymentProcessor;
import com.creatix.service.GymService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.DefaultClaims;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class TokenUtils {

    private String secret;
    private Long expiration;

    public String getSecret() {
        return secret;
    }

    public void setSecret(String secret) {
        this.secret = secret;
    }

    public Long getExpiration() {
        return expiration;
    }

    public void setExpiration(Long expiration) {
        this.expiration = expiration;
    }

    public String getUsernameFromToken(String token) {

        if (StringUtils.isBlank(token)) {
            return null;
        }

        String username;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public Date getCreatedDateFromToken(String token) {
        Date created;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            created = new Date((Long) claims.get("created"));
        } catch (Exception e) {
            created = null;
        }
        return created;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .setSigningKey(this.secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    private Date generateCurrentDate() {
        return new Date(System.currentTimeMillis());
    }

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + this.expiration * 1000);
    }

    private Boolean isTokenExpired(String token) {
        final Date expiration = this.getExpirationDateFromToken(token);
        return expiration.before(this.generateCurrentDate());
    }

    private Boolean isCreatedBeforeLastPasswordReset(Date created, Date lastPasswordReset) {
        return (lastPasswordReset != null && created.before(lastPasswordReset));
    }

    public String generateToken(UserDetails userDetails) {
        final Claims claims = new DefaultClaims();
        claims.setSubject(userDetails.getUsername());
        claims.setIssuedAt(this.generateCurrentDate());
        if ( userDetails instanceof AuthenticatedUserDetails ) {
            final AuthenticatedUserDetails auth = (AuthenticatedUserDetails) userDetails;
            final Account account = auth.getAccount();
            claims.setId(Long.toString(account.getId()));
            if ( (account.getRole() == Role.Trainer) && (account.getTrainer() != null) ) {
                final Trainer trainer = account.getTrainer();
                claims.put("trainerId", trainer.getId());
                claims.put("hasPaymentInfo", StringUtils.isNotBlank(trainer.getStripeCustomerId()));
            }
            else if ( account.getRole() == Role.GymManager ) {
                final List<Gym> gyms = gymService.findGymsByManager(account);
                if ( gyms.size() > 0 ) {
                    final Gym gym = gyms.get(0);
                    claims.put("gymId", gym.getId());
                    claims.put("hasPaymentInfo", StringUtils.isNotBlank(gym.getStripeRecipientId()));
                }
            }
        }
        return this.generateToken(claims);
    }

    private String generateToken(Claims claims) {
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(this.generateExpirationDate())
                .signWith(SignatureAlgorithm.HS512, this.secret)
                .compact();
    }

    public Boolean canTokenBeRefreshed(String token, Date lastPasswordReset) {
        final Date created = this.getCreatedDateFromToken(token);
        return (!(this.isCreatedBeforeLastPasswordReset(created, lastPasswordReset)) && (!(this.isTokenExpired(token))));
    }

    public String refreshToken(String token) {
        String refreshedToken;
        try {
            final Claims claims = this.getClaimsFromToken(token);
            claims.put("created", this.generateCurrentDate());
            refreshedToken = this.generateToken(claims);
        } catch (Exception e) {
            refreshedToken = null;
        }
        return refreshedToken;
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        if ( userDetails instanceof AuthenticatedUserDetails ) {
            final Account user = ((AuthenticatedUserDetails) userDetails).getAccount();
            final String username = this.getUsernameFromToken(token);
            return (username.equals(user.getEmail()) && !(this.isTokenExpired(token)));
        }
        else {
            throw new IllegalArgumentException("Expected object of type Account as user details.");
        }
    }

}

