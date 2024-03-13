package com.restapi.vinted.utils;

import com.restapi.vinted.repository.RoleRepository;
import jakarta.annotation.PostConstruct;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.jdbc.datasource.init.ScriptUtils;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;

import java.sql.Connection;

@Configuration
public class DataInitializer {

    private final DataSource dataSource;

    private final RoleRepository roleRepository;

    public DataInitializer(DataSource dataSource, RoleRepository roleRepository){
        this.dataSource = dataSource;
        this.roleRepository = roleRepository;
    }

    @PostConstruct
    @Transactional
    public void loadInitialData() {
        if (!dataExists()) {
            loadSqlScript("data.sql");
        }
    }

    private boolean dataExists() {
        return roleRepository.count() > 0;
    }

    private void loadSqlScript(String fileName) {
        try (Connection connection = dataSource.getConnection()) {
            Resource resource = new ClassPathResource(fileName);
            ScriptUtils.executeSqlScript(connection, resource);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
