package project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
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
@SpringBootApplication
public class Application extends WebMvcAutoConfiguration {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public DataSource dataSource() {
        return DataSourceBuilder
                .create()
                .username("root")
                .password("")
                .url("jdbc:mysql://127.0.0.1:30249/picssshare?autoReconnect=true&useSSL=false&useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC")
                .driverClassName("com.mysql.cj.jdbc.Driver")
                .build();
    }
}

