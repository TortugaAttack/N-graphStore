package com.oppsci.ngraphstore.web.root;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationFactory.PropertiesConfigurationFactory;
import org.apache.commons.configuration.PropertiesConfiguration;
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
import com.oppsci.ngraphstore.web.config.NgraphStoreConfiguration;
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
	
	private static final String IGNORE_LOAD_ERRORS = "ngraphstorage.rdf.cluster.ignoreErrors";
	private static final String LUCENE_BASE = "ngraphstorage.rdf.cluster.lucenBase";
	private static final String CLUSTER_TIMEOUT = "ngraphstorage.rdf.cluster.timeout";
	private static final String CLUSTER_SIZE = "ngraphstorage.rdf.cluster.size";

	public static @Bean DataSource dataSource(){
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
	             "database/spring-database.xml");
		DataSource dataSource = context.getBean(DataSource.class);
		context.close();
		return dataSource;
	}

	

	 
	public static @Bean CompositeConfiguration configuration() {
		return NgraphStoreConfiguration.getInstance();
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
	
	public static @Bean ClusterOverseer clusterOverseer(CompositeConfiguration config) throws IOException {
		
		int clusterSize = config.getInt(CLUSTER_SIZE);
		int timeout = config.getInt(CLUSTER_TIMEOUT);
		String luceneBase = config.getString(LUCENE_BASE);
		boolean ignoreErrors = config.getBoolean(IGNORE_LOAD_ERRORS);
		ClusterOverseer overseer = new ClusterOverseer(luceneBase, clusterSize, timeout, ignoreErrors);
		return overseer;
	}
	

}
