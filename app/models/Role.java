package models;

import leodagdag.play2morphia.Model;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import play.data.validation.Constraints;

import java.util.HashMap;
import java.util.Map;

/**
 * Role
 *
 * @author maasdianto
 *         create on 12/8/2014
 */
@Entity(value = "roles", noClassnameStored = true)
public class Role extends Model implements be.objectify.deadbolt.core.models.Role {

    @Id
    @Constraints.Required
    public String id;

    @Constraints.Required
    public String name;

    public static Finder<String, Role> find() {
      return new Finder<String, Role>(String.class, Role.class);
    }

    public static Map<String, String> options() {
        Map<String, String> options = new HashMap<>();
        for (Role role : Role.find().all()) {
            options.put(String.valueOf(role.id), role.name);
        }
        return options;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Role{");
        sb.append("id=").append(id);
        sb.append(", name='").append(name).append('\'');
        sb.append('}');
        return sb.toString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
