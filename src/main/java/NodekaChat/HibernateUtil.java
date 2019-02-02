package NodekaChat;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import java.util.List;

/**
 * Hibernate Utility class with a convenient method to get Session Factory
 * object.
 *
 * @author Isaac
 */
public class HibernateUtil {

    private static SessionFactory sessionFactory;

    public static SessionFactory getSessionFactory() {
        if (null != sessionFactory)
            return sessionFactory;

        Configuration configuration = new Configuration();

        configuration.configure();
        ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).configure().build();

        try {
            sessionFactory = configuration.buildSessionFactory(serviceRegistry);
        } catch (HibernateException e) {
            System.err.println("Initial SessionFactory creation failed." + e);
            throw new ExceptionInInitializerError(e);
        }
        return sessionFactory;
    }

    /**
     *
     * @param username
     * @param password
     * @param isAdmin
     * @return
     */
    public boolean addUser(String username, String password, boolean isAdmin) {
        Session session = getSessionFactory().openSession();
        boolean userExists = false;
        Transaction tx = session.beginTransaction();
        try {
            List result = session.createQuery("FROM User").list();
            for (User u : (List<User>) result) {
                if (u.getLoginName() == null ? username == null : u.getLoginName().equals(username)) {
                    userExists = true;
                }
            }
            if (!userExists) {
                session.save(new User(username, password, isAdmin));
            }
            session.getTransaction().commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            session.close();
        }
        return userExists;
    }
    
    public boolean verifyUser(User user) {
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        session.flush();
        List result = session.createQuery("FROM User U where U.loginName = :name and U.password = :password")
                .setParameter("name", user.getLoginName())
                .setParameter("password", user.getPassword())
                .list();
        tx.commit();
        if (!result.isEmpty()) {
            for (User u : (List<User>) result) {
                user.setDefaultChannel(u.getDefaultChannel());
                user.setCurrentChannel(u.getDefaultChannel());
                if (u.isAdministrator()) {
                    user.setAdministrator(true);
                    user.setAdminChannel(true);
                }
                if (u.isAddUser()) {
                    user.setAddUser(true);
                }
                if (u.isDeleteUser()) {
                    user.setDeleteUser(true);
                }
                if (u.isBan()) {
                    user.setBan(true);
                }
                if (u.isChangePassword()) {
                    user.setChangePassword(true);
                }
                if (u.isCanBlind()) {
                    user.setCanBlind(true);
                }
                if (u.isChangeTag()) {
                    user.setChangeTag(true);
                }
                if (u.isKick()) {
                    user.setKick(true);
                }
                if (u.isMotdUpdate()) {
                    user.setMotdUpdate(true);
                }
                if (u.isCanMute()) {
                    user.setCanMute(true);
                }
            }
        }
        session.close();
        return !result.isEmpty();
    }
    
    public User searchForUser(String username) {
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        User target = null;
        try {
            List result = session.createQuery("FROM User").list();
            for (User u : (List<User>) result) {
                if (u.getLoginName() == null ? username == null : u.getLoginName().equals(username)) {
                    target = u;
                }
            }
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            session.close();
        }
        return target;
    }
    
    public void deleteUser(User user) {
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.delete(session.load(User.class, user.getId()));
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            session.close();
        }
    }
    
    void updatePassword(User user, String password) {
        Session session = getSessionFactory().openSession();
        Transaction tx = session.beginTransaction();
        try {
            session.load(User.class, user.getId()).setPassword(password);
            tx.commit();
        } catch (HibernateException e) {
            if (tx != null) {
                tx.rollback();
            }
        } finally {
            session.close();
        }
    }
}
