package org.example.util;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import java.util.Map;

public final class HibernateUtil {
    private HibernateUtil() {}
    private static class Holder { static final SessionFactory FACTORY=buildSessionFactory("revision",Map.of()); }
    public static SessionFactory getSessionFactory() { return Holder.FACTORY; }
    public static SessionFactory buildSessionFactory(String database,Map<String,String> overrides) {
        Configuration configuration=new Configuration().configure();
        configuration.setProperty("hibernate.connection.url","jdbc:h2:mem:"+database+";DB_CLOSE_DELAY=-1");
        if("mysql".equals(System.getProperty("db"))) {
            configuration.setProperty("hibernate.connection.driver_class","com.mysql.cj.jdbc.Driver");
            configuration.setProperty("hibernate.connection.url",env("DB_URL","jdbc:mysql://localhost:3306/gestion_commerciale?serverTimezone=UTC"));
            configuration.setProperty("hibernate.connection.username",env("DB_USER","root"));
            configuration.setProperty("hibernate.connection.password",env("DB_PASSWORD",""));
            configuration.setProperty("hibernate.dialect","org.hibernate.dialect.MySQL8Dialect");
            configuration.setProperty("hibernate.hbm2ddl.auto","update");
        }
        overrides.forEach(configuration::setProperty);
        return configuration.buildSessionFactory();
    }
    private static String env(String name,String fallback) { String value=System.getenv(name);return value==null?fallback:value; }
    public static void shutdown() { getSessionFactory().close(); }
}
