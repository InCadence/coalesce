package com.proto.irems.spring;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import com.proto.irems.services.monitor.CoalesceDataServiceMonitorImpl;
import com.proto.irems.services.monitor.IGeneralServicesDAO;


public class MvcAppConfig extends WebMvcConfigurerAdapter{
	
    @Bean
    public DataSource getDataSource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.jdbc.Driver");
        dataSource.setUrl("jdbc:mysql://localhost:3306/trse");
        dataSource.setUsername("dev");
        dataSource.setPassword("web23ler");
         
        return dataSource;
    }	
    @Bean
    public IGeneralServicesDAO get() throws NamingException {
        return new CoalesceDataServiceMonitorImpl(getDataSource());
    }
}
