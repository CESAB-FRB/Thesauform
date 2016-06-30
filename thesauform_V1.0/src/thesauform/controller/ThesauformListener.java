package thesauform.controller;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import thesauform.model.ThesauformConfiguration;

/**
 * Application Lifecycle Listener implementation class ThesauformListener
 *
 */
@WebListener
public class ThesauformListener implements ServletContextListener {

	/**
	 * Default constructor.
	 */
	public ThesauformListener() {
	}

	/**
	 * @see ServletContextListener#contextDestroyed(ServletContextEvent)
	 */
	public void contextDestroyed(ServletContextEvent arg0) {
		// TODO Auto-generated method stub
	}

	/**
	 * @see ServletContextListener#contextInitialized(ServletContextEvent)
	 */
	public void contextInitialized(ServletContextEvent arg0) {
		new ThesauformConfiguration();
	}

}
