package plugin;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;

import play.Logger;
import play.Play;
import play.PlayPlugin;

public class PropertiesFileConfigurationPlugin extends PlayPlugin {

	public void onApplicationStart() {
		String propertiesFilename = "/" + Play.id + ".properties";

		Logger.info("Loading configuration from " + propertiesFilename);

		try {
			Properties properties = new Properties();
			properties.load(this.getClass().getResourceAsStream(propertiesFilename));

			for (Entry<Object, Object> entry : properties.entrySet()) {
				Play.configuration.setProperty((String) entry.getKey(),(String) entry.getValue());
			}
		} catch (IOException e) {
			throw new RuntimeException("Unable to read .properties from classpath");
		}
	}
}
