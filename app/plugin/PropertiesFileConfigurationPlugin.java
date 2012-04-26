package plugin;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.libs.IO;

public class PropertiesFileConfigurationPlugin extends PlayPlugin {
	
	@Override
	public void onConfigurationRead() {
		String[] propertiesFilenames = Play.configuration.getProperty("externalConfig.fileName", "/" + Play.id + ".properties").split(",");
		
		for (String propertiesFilename : propertiesFilenames) {
			Logger.info("Loading configuration from " + propertiesFilename);
			Properties properties = IO.readUtf8Properties(this.getClass().getResourceAsStream(propertiesFilename));
			
			for (Entry<Object, Object> entry : properties.entrySet()) {
				Play.configuration.setProperty((String) entry.getKey(),(String) entry.getValue());
			}
		}
	}

}
