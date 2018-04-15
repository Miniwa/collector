package se.miniwa.fourchan.collector;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.fluent.Configurations;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.flywaydb.core.Flyway;
import org.hibernate.SessionFactory;
import org.hibernate.boot.Metadata;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.service.ServiceRegistry;
import se.miniwa.fourchan.api.FourChanClient;

import java.io.File;

public class CollectorApp {
    private static final Logger logger = LogManager.getLogger(CollectorApp.class);
    public static void main(String[] args) {
        HttpTransport transport = new NetHttpTransport();
        JsonFactory jsonFactory = new GsonFactory();
        FourChanClient client = new FourChanClient(transport.createRequestFactory(), jsonFactory);
        Flyway flyway = new Flyway();

        try {
            Configuration config = buildConfiguration();
            String host = config.getString("db.host");
            int port = config.getInt("db.port");
            String dbName = config.getString("db.name");
            String username = config.getString("db.username");
            String password = config.getString("db.password");
            String url = String.format("jdbc:postgresql://%s:%d/%s", host, port, dbName);
            logger.debug("Database URL is: " + url);

            flyway.setDataSource(url, username, password);
            flyway.migrate();

            SessionFactory sessionFactory = buildSessionFactory(url, username, password);
            CollectorService collectorService = new CollectorService("v", client, sessionFactory);

            logger.debug("Starting service..");
            collectorService.startAsync();
            collectorService.awaitRunning();
            logger.debug("Service started. Awaiting termination..");
            collectorService.awaitTerminated();
        } catch (Throwable e) {
            logger.error("Unhandled exception in main thread.", e);
            System.exit(1);
        }
    }

    private static SessionFactory buildSessionFactory(String url, String username, String password) {
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().configure("hibernate.xml")
                .applySetting("hibernate.connection.url", url)
                .applySetting("hibernate.connection.username", username)
                .applySetting("hibernate.connection.password", password).build();
        Metadata metadata = new MetadataSources(serviceRegistry).getMetadataBuilder().build();
        return metadata.buildSessionFactory();
    }

    private static Configuration buildConfiguration() throws ConfigurationException {
        String filename = "collector-cfg.xml";
        File configFile = new File(filename);
        if (!configFile.exists() || !configFile.canRead()) {
            throw new IllegalStateException(String.format("Could not locate configuration file %s.", filename));
        }
        Configurations configurations = new Configurations();
        XMLConfiguration config = configurations.xml(configFile);
        config.setThrowExceptionOnMissing(true);
        return config;
    }
}
