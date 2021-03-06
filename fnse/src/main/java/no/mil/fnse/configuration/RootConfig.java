package no.mil.fnse.configuration;

import java.beans.PropertyVetoException;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import no.mil.fnse.core.model.*;
import no.mil.fnse.core.model.networkElement.BgpConfig;
import no.mil.fnse.core.model.networkElement.GlobalConfiguration;
import no.mil.fnse.core.model.networkElement.InterfaceAddress;
import no.mil.fnse.core.model.networkElement.MsdpConfig;
import no.mil.fnse.core.model.networkElement.NetworkInterface;
import no.mil.fnse.core.model.networkElement.Router;

import org.springframework.context.annotation.Configuration;

//beans are typically the middle-tier and data-tier components that drive the back end of the application
@Configuration
@ComponentScan(basePackages = { "no.mil.fnse", "no.mil.fnse.repository", "no.mil.fnse.service" })
@EnableTransactionManagement
public class RootConfig implements SchedulingConfigurer {
	static Logger logger = Logger.getLogger(RootConfig.class);

	@Bean
	public LocalSessionFactoryBean sessionFactory(ComboPooledDataSource dataSource) {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
		// ((Object) sessionFactory).setPackagesToScan(new
		// String[]{"no.cyfor.zelus.news_stand.dao.hibernate"});
		sessionFactory.setAnnotatedPackages(new String[] { "no.mil.fnse" });
		sessionFactory.setAnnotatedClasses(new Class[] { SDNController.class, Peer.class, BgpConfig.class,SystemConfiguration.class,
				GlobalConfiguration.class, MsdpConfig.class, NetworkInterface.class, NtpConfig.class, Router.class, InterfaceAddress.class});
		// sessionFactory.setMappingResources(;//, "model.KeyWord",
		// "model.Search", "model.Publisher" });
		sessionFactory.setHibernateProperties(hibernateProperties());
		logger.info("SessionFatory: " + sessionFactory);
		return sessionFactory;
	}

	@Bean
	public ComboPooledDataSource dataSource() {
		ComboPooledDataSource dataSource = new ComboPooledDataSource();
		try {
			dataSource.setDriverClass("org.postgresql.Driver");
		} catch (PropertyVetoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		dataSource.setJdbcUrl("jdbc:postgresql:fnse");
		dataSource.setUser("zelus");
		dataSource.setPassword("admin123");

		return dataSource;
	}

	@Bean(name = "transactionManager")
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager txManager = new HibernateTransactionManager();
		txManager.setSessionFactory(sessionFactory);
		return txManager;
	}

	@Bean
	public PersistenceExceptionTranslationPostProcessor exceptionTranslation() {
		return new PersistenceExceptionTranslationPostProcessor();
	}

	@SuppressWarnings("serial")
	private Properties hibernateProperties() {
		return new Properties() {
			{
				setProperty("hibernate.hbm2ddl.auto", "create-drop");
				setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
				setProperty("hibernate.globally_quoted_identifiers", "true");
//				setProperty("hibernate.enable_lazy_load_no_trans","true");
			}
		};
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}
 
//	@Bean(destroyMethod = "shutdown")
//	public Executor taskExecutor() {
//		return Executors.newScheduledThreadPool(10);
//	}
//	
	@Bean(destroyMethod = "shutdown")
	public ThreadPoolTaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor pool = new ThreadPoolTaskExecutor();
		pool.setCorePoolSize(5);
		pool.setMaxPoolSize(10);
		pool.setQueueCapacity(25);
		return pool;
	}
}
