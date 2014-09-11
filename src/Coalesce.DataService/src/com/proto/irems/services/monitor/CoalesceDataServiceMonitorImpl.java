package com.proto.irems.services.monitor;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

public class CoalesceDataServiceMonitorImpl implements
		IGeneralServicesDAO {
	private JdbcTemplate jdbcTemplate;
	Context ctx = null;
	public CoalesceDataServiceMonitorImpl(DataSource dataSource) throws NamingException{
		//String DATASOURCE_CONTEXT = "java:/comp/env/jdbc/devsys";
		//DataSource ds = (DataSource) ctx.lookup(DATASOURCE_CONTEXT); 
		jdbcTemplate = new JdbcTemplate(dataSource);
		//jdbcTemplate = new JdbcTemplate(ds);
	}
	@Override
	public CoalesceDataServiceMonitor get() {
	    String sql = "select MONITOR_START_STATE from trse.CoalesceDataServiceMonitor";
	    return jdbcTemplate.query(sql, new ResultSetExtractor<CoalesceDataServiceMonitor>() {
	 
	        @Override
	        public CoalesceDataServiceMonitor extractData(ResultSet rs) throws SQLException,
	                DataAccessException {
	            if (rs.next()) {
	            	CoalesceDataServiceMonitor ctx = new CoalesceDataServiceMonitor();
	                ctx.setMonitorStartState(rs.getBoolean(0));
	                return ctx;
	            }
	 
	            return null;
	        }
	 
	    });
	}

	@Override
	public void set(CoalesceDataServiceMonitor coalesceDataServiceMonitor) {
		String sql = "UPDATE trse.CoalesceDataServiceMonitor SET MONITOR_START_STATE=?";
			jdbcTemplate.update(sql, coalesceDataServiceMonitor.getMonitorStartState());
	}

}
