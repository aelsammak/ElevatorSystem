package common;

import java.io.IOException;
import java.util.Properties;

/**
 * The Config class is responsible for loading all the properties necessary for the simulation from a given config file.
 *
 * @author Adi El-Sammak
 * @author Kareem El-Hajjar
 * @version 3.0
 * 
 */
public class Config {
	
    private static final String CONFIG_FILE_NAME = "./resources/config.cfg";
    private final Properties props;

    /**
     * Constructor used to create an instance of the Config class
     *
     * @throws IOException thrown if error when parsing file
     */
    public Config() throws IOException {
    	props = new Properties();
    	props.load(this.getClass().getClassLoader().getResourceAsStream(CONFIG_FILE_NAME));
    }

    /**
     * Gets a property from the config file
     *
     * @param key the property key
     * @return The property in string format
     */
    public String getProperty(String key) {
        return this.props.getProperty(key);
    }
    
    /**
     * Gets the property from the config file as a int
     *
     * @param key the property key
     * @return The property in int format
     */
    public int getIntProperty(String key) {
    	return Integer.parseInt(getProperty(key));
    }
    
    /**
     * Gets the property from the config file as a Long
     *
     * @param key the property key
     * @return The property as a Long
     * @throws NumberFormatException if the property is not a Long
     */
    public Long getLongProperty(String key) {
        return Long.parseLong(getProperty(key));
    }

}
