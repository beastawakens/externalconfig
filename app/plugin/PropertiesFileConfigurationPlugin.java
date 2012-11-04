package plugin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;

import play.Logger;
import play.Play;
import play.PlayPlugin;
import play.libs.IO;

/**
 * This class takes care of reading additional properties from external sources.
 * @author Rugbyhead (Original)
 * @author Hoogheemraad
 * @author Neoh79
 */
public class PropertiesFileConfigurationPlugin extends PlayPlugin {

    /**
     * This method is automatically called when application.conf has been parsed
     * and loaded.
     */
    public final void onConfigurationRead() {
        String[] propertiesFilenames = Play.configuration.getProperty("externalConfig.fileName",
                "/" + Play.id + ".properties").split(",");
        String propertiesFileAbsolutePath = Play.configuration.getProperty("externalConfig.fileAbsolutePath");

        for (String propertiesFilename : propertiesFilenames) {
            String propertiesFilenameAndPath = null;
            InputStream is = null;
            if (propertiesFileAbsolutePath != null) {
                propertiesFilenameAndPath = propertiesFileAbsolutePath + System.getProperty("file.separator")
                        + propertiesFilename;
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

            try {
                Properties properties = IO.readUtf8Properties(is);

                for (Entry<Object, Object> entry : properties.entrySet()) {
                    Play.configuration.setProperty((String) entry.getKey(), (String) entry.getValue());
                }
            } catch (Exception e) {
                Logger.error("Error when loading file " + propertiesFilenameAndPath);
            }
        }
    }

}
