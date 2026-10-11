package org.example.web;
import javax.servlet.*;
import javax.servlet.annotation.WebListener;
import org.example.util.HibernateUtil;

@WebListener
public class PersistenceListener implements ServletContextListener {
    @Override public void contextInitialized(ServletContextEvent event) { HibernateUtil.getSessionFactory(); }
    @Override public void contextDestroyed(ServletContextEvent event) { HibernateUtil.shutdown(); }
}
