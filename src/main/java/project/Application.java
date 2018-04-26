package project;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import javax.sql.DataSource;

@Configuration
@ComponentScan("project")
@EnableAutoConfiguration
@EnableConfigurationProperties
@SpringBootApplication
public class Application extends WebMvcAutoConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Value("${spring.datasource.url}") String jdbcUrl;
    @Value("${spring.datasource.username}") String jdbcUser;
    @Value("${spring.datasource.password}") String jdbcPassword;
    @Value("${spring.datasource.driver-class-name}") String jdbcDriver;

    @Bean
    public DataSource dataSource() {

        return DataSourceBuilder
                .create()
                .url(jdbcUrl)
                .password(jdbcPassword)
                .username(jdbcUser)
                .driverClassName(jdbcDriver)
                .build();
    }


}

