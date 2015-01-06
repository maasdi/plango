package models;

import be.objectify.deadbolt.core.models.Permission;
import be.objectify.deadbolt.core.models.Subject;
import leodagdag.play2morphia.Model;
import org.mongodb.morphia.annotations.Embedded;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;
import play.data.validation.Constraints;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * User
 *
 * @author maasdianto
 *         create on 12/8/2014
 */
@Entity(value = "users", noClassnameStored = true)
public class User extends Model implements Subject {

    @Id
    @Constraints.Required
    public String username;

    @Constraints.Required
    public String password;

    @Constraints.Required
    public Boolean enabled = Boolean.TRUE;

    @Constraints.Required
    public String name;

    @Constraints.Email
    public String email;

    @Reference
    public List<Role> user_roles = new ArrayList<Role>();

    @Embedded
    public Profile profile;

    public String authToken;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public List<Role> getUser_roles() {
        return user_roles;
    }

    public void setUser_roles(List<Role> user_roles) {
        this.user_roles = user_roles;
    }

    public Profile getProfile() {
        return profile;
    }

    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public static Finder<String, User> find() {
        return new Finder<String, User>(String.class, User.class);
    }

    public String createToken() {
        authToken = UUID.randomUUID().toString();
        update();
        return authToken;
    }

    public void destroyToken() {
        authToken = null;
        update();
    }

    @Override
    public <T extends Model> T insert() {
        this.password = getMd5(password);
        return super.insert();
    }

    private static String getMd5(String plain) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(plain.getBytes());
            byte[] bytes = digest.digest();
            StringBuilder sb = new StringBuilder();
            for(int i=0; i< bytes.length ;i++)
            {
                sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return null;
        }
    }

    public static User auth(String username, String password) {
        String encrypted = getMd5(password);
        return User.find().filter("username", username).filter("password", encrypted).get();
    }

    public static User findByAuthToken(String authToken) {
        return User.find().filter("authToken", authToken).get();
    }

    @Override
    public List<? extends be.objectify.deadbolt.core.models.Role> getRoles() {
        return this.user_roles;
    }

    @Override
    public List<? extends Permission> getPermissions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public String getIdentifier() {
        return this.username;
    }
}
