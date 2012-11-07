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
    private static final String EC_FILENAME = "externalConfig.fileName";
    private static final String EC_FILE_ABSOLUTE_PATH = "externalConfig.fileAbsolutePath";
    private static final String SEPARATOR = System.getProperty("file.separator");

    /**
     * This method is automatically called when application.conf has been parsed
     * and loaded. It tries to read the properties mentioned in the
     * 'externalConfig.fileName' and the 'externalConfig.fileNameAbsolute'
     * properties.
     */
    public final void onConfigurationRead() {
        readPropertiesFromFileName();
    }

    /**
     * Read 0, 1 or more files from files mentioned in the
     * 'externalConfig.fileName' property relative to the 'conf' directory. If
     * 'externalConfig.fileAbsolutePath' is present is uses that value to prefix
     * the filenames.
     */
    private void readPropertiesFromFileName() {
        String filenameValue = Play.configuration.getProperty(EC_FILENAME, "/" + Play.id + ".properties");
        String propertiesFileAbsolutePath = Play.configuration.getProperty(EC_FILE_ABSOLUTE_PATH);
        String propertiesFilenameAndPath;
        InputStream is;

        String[] propertiesFilenames = filenameValue.split(",");

        for (String propertiesFilename : propertiesFilenames) {
            propertiesFilenameAndPath = null;
            is = null;

            try {
                if (propertiesFileAbsolutePath != null && propertiesFileAbsolutePath.length() > 0) {
                    if (propertiesFilename.startsWith("/") || propertiesFilename.startsWith("/")) {
                        propertiesFilename = propertiesFilename.substring(1);
                    }
                    propertiesFilenameAndPath = propertiesFileAbsolutePath + SEPARATOR + propertiesFilename;
                    is = new FileInputStream(propertiesFilenameAndPath);
                } else {
                    is = this.getClass().getResourceAsStream(propertiesFilename);
                }

                if (is == null) {
                    Logger.warn("Configuration file '" + propertiesFilenameAndPath
                            + "' is not found. Ignoring the file.");
                } else {
                    Properties properties = IO.readUtf8Properties(is);

                    Logger.info("Loading configuration from " + propertiesFilenameAndPath);
                    for (Entry<Object, Object> entry : properties.entrySet()) {
                        Play.configuration.setProperty((String) entry.getKey(), (String) entry.getValue());
                    }
                }
            } catch (NullPointerException e) {
                Logger.error("Error when loading file " + propertiesFilenameAndPath + ". Check your '" + EC_FILENAME
                        + "' property.");
            } catch (RuntimeException e) {
                Logger.error("Error when loading file " + propertiesFilenameAndPath + ". Ignoring the file.");
            } catch (FileNotFoundException e) {
                Logger.warn("Configuration file '" + propertiesFilenameAndPath + "' is not found. Ignoring the file.");
            }
        }
    }
}