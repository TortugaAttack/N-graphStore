package com.oppsci.ngraphstore.web.root;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.hibernate.SessionFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import com.oppsci.ngraphstore.processor.SPARQLProcessor;
import com.oppsci.ngraphstore.processor.SPARQLProcessorFactory;
import com.oppsci.ngraphstore.processor.UpdateProcessor;
import com.oppsci.ngraphstore.processor.impl.DirectUpdateProcessor;
import com.oppsci.ngraphstore.storage.ClusterOverseer;
import com.oppsci.ngraphstore.storage.MemoryStorage;
import com.oppsci.ngraphstore.web.rest.rdf.SPARQLRestController;
import com.oppsci.ngraphstore.web.rest.rdf.TripleRestController;
import com.oppsci.ngraphstore.web.rest.rdf.UpdateRestController;
import com.oppsci.ngraphstore.web.role.RoleDAO;
import com.oppsci.ngraphstore.web.sec.BCryptPasswordEncoder;
import com.oppsci.ngraphstore.web.user.UserDAO;

/**
 * Controller for autowired elememnts
 * 
 * creates the Elements according to configuration file
 * 
 * @author f.conrads
 *
 */
@Configuration
@ComponentScan(basePackages = {"com.oppsci.ngraphstore.web.root", "com.oppsci.ngraphstore.web.rest", "com.oppsci.ngraphstore.web.user","com.oppsci.ngraphstore.web.role"})
public class RootController {
	
	public static @Bean DataSource dataSource(){
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
	             "database/spring-database.xml");
		DataSource dataSource = context.getBean(DataSource.class);
		context.close();
		return dataSource;
	}
	 
	 
	public static @Bean RoleDAO roleDAO(DataSource dataSource) {
		return new RoleDAO(dataSource);
	}
	
	public static @Bean UserDAO userDAO(DataSource dataSource){
		return new UserDAO(dataSource);
	}
	
	public static @Bean PasswordEncoder encoder() {
		return new BCryptPasswordEncoder(11);
	}
	
	/**
	 * Creates the default sparql processor to use
	 * @return a DefaultSPARQLProcessor object
	 */
	public static @Bean SPARQLProcessor createSPARQLProcessor() {
		SPARQLProcessor processor = SPARQLProcessorFactory.createDefaultProcessor();
		return processor;
	}
	
	public static @Bean MemoryStorage createMemoryStorage() throws MalformedURLException {
		File f = new File("data.nt");
		String ntFile = f.toURI().toURL().toString();
		MemoryStorage mem = new MemoryStorage(ntFile);
		return mem;
	}
//	
//	public static @Bean DataSource dataSource(ServletConfig servlet) {
//		WebApplicationContext ctx = WebApplicationContextUtils.getRequiredWebApplicationContext(servlet.getServletContext());
//		return (DataSource) ctx.getBean("dataSource");
//	}
	
	/**
	 * Creates a SAPRQLRestController using the specified processor
	 * @param processor the SPARQLProcessor to use
	 * @return a SPARQLRestController Object
	 */
	public static @Bean SPARQLRestController createSPARQLRestController(SPARQLProcessor processor) {
		SPARQLRestController sparqlRestController = new SPARQLRestController(processor);
		return sparqlRestController;
	}
	
	/**
	 * Creates a UpdateRestController
	 * 
	 * @return
	 */
	public static @Bean UpdateRestController createUpdateRestController() {
		UpdateRestController updateRestController = new UpdateRestController();
		return updateRestController;
	}
	
	/**
	 * Creates a TripleRestController
	 * @return
	 */
	public static @Bean TripleRestController tripleUpdateRestController() {
		TripleRestController tripleRestController = new TripleRestController();
		return tripleRestController;
	}
	
	public static @Bean UpdateProcessor directProcessor() {
		DirectUpdateProcessor processor = new DirectUpdateProcessor();
		return processor;
	}
	
	public static @Bean ClusterOverseer clusterOverseer() throws IOException {
		//TODO get parameter from properties file
		ClusterOverseer overseer = new ClusterOverseer("lucene_test", 1, 180);
		return overseer;
	}
	
	public static @Bean boolean ignoreErrors() {
		//TODO get parameter from properties file
		return true;
	}

}
