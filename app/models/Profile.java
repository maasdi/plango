package models;

import org.mongodb.morphia.annotations.Embedded;

/**
 * Profile
 *
 * @author maasdianto
 *         create on 12/16/2014
 */
@Embedded
public class Profile {

    public String profile_img;

    public String getProfile_img() {
        return profile_img;
    }

    public void setProfile_img(String profile_img) {
        this.profile_img = profile_img;
    }
}
