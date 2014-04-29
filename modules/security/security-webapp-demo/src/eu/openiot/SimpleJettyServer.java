package eu.openiot;

import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.server.HttpConfiguration;
import org.eclipse.jetty.server.HttpConnectionFactory;
import org.eclipse.jetty.server.SecureRequestCustomizer;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.server.SslConnectionFactory;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.util.security.Constraint;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.eclipse.jetty.webapp.WebAppContext;


public class SimpleJettyServer {
	public static void main(String[] args) throws Exception {
		Server server = new Server();

		// Configuring SSL and default http connector
		HttpConfiguration httpConfig = new HttpConfiguration();
		httpConfig.setSecureScheme("https");
		httpConfig.setSecurePort(9443);
		httpConfig.setOutputBufferSize(32768);
		httpConfig.setRequestHeaderSize(8192);
		httpConfig.setResponseHeaderSize(8192);
		httpConfig.setSendServerVersion(true);
		httpConfig.setSendDateHeader(false);

		ServerConnector http = new ServerConnector(server, new HttpConnectionFactory(httpConfig));
		http.setPort(9080);
		http.setIdleTimeout(30000);
		server.addConnector(http);
		
		SslContextFactory sslContextFactory = new SslContextFactory();
		sslContextFactory.setKeyStorePath("webapp/ssl/keystore");
		sslContextFactory.setKeyStorePassword("changeit");
		sslContextFactory.setKeyManagerPassword("changeit");
		sslContextFactory.setTrustStorePath("webapp/ssl/keystore");
		sslContextFactory.setTrustStorePassword("changeit");
		sslContextFactory.setExcludeCipherSuites("SSL_RSA_WITH_DES_CBC_SHA", "SSL_DHE_RSA_WITH_DES_CBC_SHA", "SSL_DHE_DSS_WITH_DES_CBC_SHA", "SSL_RSA_EXPORT_WITH_RC4_40_MD5",
				"SSL_RSA_EXPORT_WITH_DES40_CBC_SHA", "SSL_DHE_RSA_EXPORT_WITH_DES40_CBC_SHA", "SSL_DHE_DSS_EXPORT_WITH_DES40_CBC_SHA");
		HttpConfiguration https_config = new HttpConfiguration(httpConfig);
		https_config.addCustomizer(new SecureRequestCustomizer());
		ServerConnector sslConnector = new ServerConnector(server, new SslConnectionFactory(sslContextFactory, "http/1.1"), new HttpConnectionFactory(https_config));
		sslConnector.setPort(9443);
		server.addConnector(sslConnector);

		
//		ResourceHandler resourceHandler = new ResourceHandler();
//		resourceHandler.setDirectoriesListed(false);
//		resourceHandler.setWelcomeFiles(new String[] { "login.html" });
//		resourceHandler.setResourceBase("./webapp");

				
//		ServletContextHandler servletCtxHandler = new ServletContextHandler(ServletContextHandler.SESSIONS);
//		servletCtxHandler.addServlet(AuthServlet.class, "/login");
//		servletCtxHandler.addServlet(AuthServlet.class, "/actions");
//		servletCtxHandler.addServlet(AdminServlet.class, "/admin");
//		servletCtxHandler.addServlet(LogoutServlet.class, "/logout");

		//Forces redirect to https
		ConstraintSecurityHandler securityHandler = new ConstraintSecurityHandler();
		ConstraintMapping constraintMapping = new ConstraintMapping();
		constraintMapping.setPathSpec("/*");
		Constraint constraint = new Constraint();
		constraint.setDataConstraint(2);
		constraintMapping.setConstraint(constraint);
		securityHandler.addConstraintMapping(constraintMapping);
		
		
		
		WebAppContext webapp = new WebAppContext(); 
		webapp.setContextPath("/");
		webapp.setExtractWAR(true);
		webapp.setWar("./webapp");
		webapp.setSecurityHandler(securityHandler);
		
//		EnvConfiguration envConfiguration = new EnvConfiguration();
//		envConfiguration.configure(webapp);
//		
//		PlusConfiguration plusConfiguration = new PlusConfiguration();
//		plusConfiguration.configure(webapp);
		
		
//		servletCtxHandler.setSecurityHandler(securityHandler);
		
		
		HandlerList handlerList = new HandlerList();
//		handlerList.addHandler(servletCtxHandler);
//		handlerList.addHandler(resourceHandler);
		handlerList.addHandler(webapp);

		server.setHandler(handlerList);

	
		server.start();
		server.join();
	}
}
