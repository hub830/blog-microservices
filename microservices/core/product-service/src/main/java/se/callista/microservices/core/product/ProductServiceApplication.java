package se.callista.microservices.core.product;

import com.codahale.metrics.MetricRegistry;
import com.readytalk.metrics.StatsDReporter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableDiscoveryClient
@ComponentScan({"se.callista.microservices.core.product", "se.callista.microservices.util"})
public class ProductServiceApplication {

    private static final Logger LOG = LoggerFactory.getLogger(ProductServiceApplication.class);

    @Inject()
    MetricRegistry registry;

    @PostConstruct
    public void postInject() {
        LOG.info("Register a StatsD Metrics Reporter");
        StatsDReporter.forRegistry(registry)
            .prefixedWith("product-service")
            .build("statsd", 8125)
            .start(1, TimeUnit.SECONDS);
        LOG.info("Registration of a StatsD Metrics Reporter done!");
    }

    @Value("${app.rabbitmq.host:localhost}")
    String rabbitMqHost;

    @Bean
    public ConnectionFactory connectionFactory() {
        LOG.info("Create RabbitMqCF for host: {}", rabbitMqHost);
        CachingConnectionFactory connectionFactory = new CachingConnectionFactory(rabbitMqHost);
        return connectionFactory;
    }

    public static void main(String[] args) {
        SpringApplication.run(ProductServiceApplication.class, args);
    }
}
