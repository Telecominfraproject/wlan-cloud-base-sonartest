package com.telecominfraproject.wlan.remote.tests;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.web.context.WebServerApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.Primary;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.Profiles;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.SimpleTransactionStatus;

import com.telecominfraproject.wlan.server.RemoteTestServer;

/**
 * Base class for remote integration tests. Starts an embedded web application
 * server on port assigned by OS.
 * 
 * Meant to be used with *-in-memory datastores. Uses simulated
 * PlatformTransactionManager which does nothing.
 * 
 * <pre>
 * <code> 
 * &#64;ActiveProfiles(profiles = { "integration_test", "no_ssl", "http_digest_auth",
 *       "rest-template-single-user-per-service-digest-auth" })
 * &#64;Import(value = { TestConfiguration.class })
 * </code>
 * </pre>
 * 
 * See
 * {@link https://docs.spring.io/spring-boot/docs/current/reference/html/boot-features-testing.html}
 * 
 * @author dtop
 * @author yongli
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest(
        webEnvironment = WebEnvironment.RANDOM_PORT,
        classes = { RemoteTestServer.class },
        value = { "tip.wlan.serviceUser=user", "tip.wlan.servicePassword=password",
                "tip.wlan.csrf-enabled=false" })
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
@Import(value = {
})
public abstract class BaseRemoteTest {

    private static final Logger LOG = LoggerFactory.getLogger(BaseRemoteTest.class);

    private static final String TEST_PROPERTY_SOURCE = BaseRemoteTest.class.getSimpleName();

    @Autowired
    protected WebServerApplicationContext server;

    @Autowired
    protected ConfigurableEnvironment env;

    @Value("${local.server.port}")
    protected String port;
    
    private static AtomicInteger customerIdGen = new AtomicInteger();
    private static AtomicLong equipmentIdGen = new AtomicLong();
    private static AtomicLong locationIdGen = new AtomicLong();

    protected void configureBaseUrl(String propertyName) {
        if (env.getProperty(propertyName) == null) {
            if (env.acceptsProfiles(Profiles.of("use_ssl"))) {
                addProperties(propertyName, "https://localhost:" + server.getWebServer().getPort());
            } else {
                addProperties(propertyName, "http://localhost:" + server.getWebServer().getPort());
            }
        }
    }

    protected void addProperties(String propertyName, Object value) {
        if (env.getProperty(propertyName) == null) {
            MutablePropertySources propertySources = env.getPropertySources();
            MapPropertySource ps = (MapPropertySource) propertySources.get(TEST_PROPERTY_SOURCE);
            if (null != ps) {
                ps.getSource().put(propertyName, value);
            } else {
                HashMap<String, Object> myMap = new HashMap<>();
                myMap.put(propertyName, value);
                propertySources.addFirst(new MapPropertySource(TEST_PROPERTY_SOURCE, myMap));
            }
        }
    }

    @Configuration
    // @PropertySource({ "classpath:persistence-${envTarget:dev}.properties" })
    public static class Config {

        @Bean
        @Primary
        public PlatformTransactionManager transactionManager() {
            PlatformTransactionManager ptm = new PlatformTransactionManager() {

                {
                    LOG.info("*** Using simulated PlatformTransactionManager");
                }

                @Override
                public void rollback(TransactionStatus status) throws TransactionException {
                    LOG.info("Simulating Rollback for {}", status);
                }

                @Override
                public void commit(TransactionStatus status) throws TransactionException {
                    LOG.info("Simulating Commit for {}", status);
                }

                @Override
                public TransactionStatus getTransaction(TransactionDefinition definition) throws TransactionException {
                    LOG.info("Simulating getTransaction for {}", definition);
                    TransactionStatus ts = new SimpleTransactionStatus();
                    return ts;
                }

            };

            return ptm;
        }

    }

    protected int getNextCustomerId() {
    	return customerIdGen.incrementAndGet();
    }
    
    protected long getNextEquipmentId() {
    	return equipmentIdGen.incrementAndGet();
    }

    protected long getNextLocationId() {
    	return locationIdGen.incrementAndGet();
    }

}
