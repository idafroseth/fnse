package no.mil.fnse.configuration;



import java.beans.PropertyVetoException;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.dao.annotation.PersistenceExceptionTranslationPostProcessor;
import org.springframework.orm.hibernate4.HibernateTransactionManager;
import org.springframework.orm.hibernate4.LocalSessionFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import com.mchange.v2.c3p0.ComboPooledDataSource;

import no.mil.fnse.model.Peer;
import no.mil.fnse.model.SDNController;

import org.springframework.context.annotation.Configuration;


//beans are typically the middle-tier and data-tier components that drive the back end of the application
@Configuration
@ComponentScan(basePackages={"no.mil.fnse","no.mil.fnse.repository","no.mil.fnse.service"})
@EnableTransactionManagement
@EnableScheduling
public  class RootConfig  implements SchedulingConfigurer{
	static Logger logger = Logger.getLogger(RootConfig.class);
	@Bean
	public LocalSessionFactoryBean sessionFactory(ComboPooledDataSource dataSource) {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource);
	//	 ((Object) sessionFactory).setPackagesToScan(new String[]{"no.cyfor.zelus.news_stand.dao.hibernate"});
		sessionFactory.setAnnotatedPackages(new String[] { "no.mil.fnse"});
		sessionFactory.setAnnotatedClasses(new Class[] {SDNController.class, Peer.class});
		//sessionFactory.setMappingResources(;//, "model.KeyWord", "model.Search", "model.Publisher" });
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
		dataSource.setJdbcUrl("jdbc:postgresql:news_stand");
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

	private Properties hibernateProperties() {
		return new Properties() {
			{
				setProperty("hibernate.hbm2ddl.auto", "create-drop");
				setProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
				setProperty("hibernate.globally_quoted_identifiers", "true");
			}
		};
	}

	@Override
	public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
		taskRegistrar.setScheduler(taskExecutor());
	}

    @Bean(destroyMethod="shutdown")
    public Executor taskExecutor() {
        return Executors.newScheduledThreadPool(10);
    }
}
