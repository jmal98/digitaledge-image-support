package com.deleidos.rtws.container.service.application;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.glassfish.jersey.servlet.ServletContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.deleidos.rtws.container.service.util.WorkManager;

public class ContainerService {

	private Logger logger = LoggerFactory.getLogger(getClass());

	private void run() {
		Server server = null;
		try {

			ExecutorService pool = Executors.newFixedThreadPool(1);
			pool.submit(new WorkManager());

			ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);

			context.setContextPath("/");

			ServletHolder jerseyServlet = context.addServlet(ServletContainer.class, "/*");
			jerseyServlet.setInitOrder(0);

			jerseyServlet.setInitParameter("jersey.config.server.provider.packages",
					"com.deleidos.rtws.container.service.resources");
			jerseyServlet.setInitParameter("jersey.config.server.provider.scanning.recursive", "false");

			server = new Server(8080);
			server.setHandler(context);
			server.start();
			server.join();

			pool.awaitTermination(2l, TimeUnit.MINUTES);

		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		} finally {
			if (server != null)
				server.destroy();
			System.setProperty("SHUTDOWN_REQUESTED", "true");
		}
	}

	public static void main(String[] args) {
		ContainerService app = new ContainerService();

		app.run();

	}

}
