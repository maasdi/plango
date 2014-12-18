import com.mongodb.WriteConcern;
import leodagdag.play2morphia.MorphiaPlugin;
import leodagdag.play2morphia.utils.ConfigKey;
import models.Role;
import models.User;
import org.apache.commons.lang3.StringUtils;
import play.Application;
import play.Configuration;
import play.GlobalSettings;
import play.libs.Yaml;

import java.util.List;
import java.util.Map;

/**
 * Global
 *
 * @author maasdianto
 *         create on 12/8/2014
 */
public class Global extends GlobalSettings {

    @Override
    public void onStart(Application app) {
        // update morphia write concern
        Configuration morhiaConfig = Configuration.root().getConfig("morphia");
        if (morhiaConfig != null) {
            String writeConcern = morhiaConfig.getString(ConfigKey.DEFAULT_WRITE_CONCERN.getKey());
            if (StringUtils.isNotBlank(writeConcern)) {
                play.Logger.debug("Set Morphia WriteConcern : {}", writeConcern);
                MorphiaPlugin.db().setWriteConcern(WriteConcern.valueOf(writeConcern));
            }
        }
        // Load initial-data.yml
        loadInitialData();
    }

    private void loadInitialData() {
        if (User.find().countAll() == 0) {
            Map<String, List<Object>> all = (Map<String, List<Object>>) Yaml.load("initial-data.yml");
            List<Object> roles = all.get("roles");
            for (Object o : roles) {
                Role role = (Role) o;
                role.insert();
            }

            List<Object> users = all.get("users");
            for (Object o : users) {
                User user = (User) o;
                user.insert();
            }
        }
    }
}
