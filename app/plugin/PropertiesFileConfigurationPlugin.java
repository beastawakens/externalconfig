package plugin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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
		String propertiesFileAbsolutePath = Play.configuration.getProperty("externalConfig.fileAbsolutePath");
		
		for (String propertiesFilename : propertiesFilenames) {
			String propertiesFilenameAndPath = null;
			InputStream is = null;
			if (propertiesFileAbsolutePath != null) {
				propertiesFilenameAndPath = propertiesFileAbsolutePath + System.getProperty("file.separator") + propertiesFilename;
				try {
					is = new FileInputStream(propertiesFilenameAndPath);
				} catch (FileNotFoundException fnfe) {
					Logger.error("Not found configuration file " + propertiesFilenameAndPath);
				}
			} else {
				// get from classpath
				propertiesFilenameAndPath = propertiesFilename;
        is = this.getClass().getResourceAsStream(propertiesFilenameAndPath);
      }
			Logger.info("Loading configuration from " + propertiesFilenameAndPath);
			
			try  {
				Properties properties = IO.readUtf8Properties(is);
				
				for (Entry<Object, Object> entry : properties.entrySet()) {
					Play.configuration.setProperty((String) entry.getKey(),(String) entry.getValue());
				}
			} catch (Exception e) {
				Logger.error("Error when loading file " + propertiesFilenameAndPath);
			}
		}
	}

}
