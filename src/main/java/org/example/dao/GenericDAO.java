package org.example.dao;
import org.example.util.HibernateUtil;
import javax.persistence.*;
import java.io.Serializable;
import java.util.*;
import java.util.function.Function;

public class GenericDAO<T,ID extends Serializable> {
    private final Class<T> entityClass;
    protected final EntityManagerFactory emf;
    public GenericDAO(Class<T> entityClass) { this(entityClass,HibernateUtil.getSessionFactory()); }
    public GenericDAO(Class<T> entityClass,EntityManagerFactory emf) { this.entityClass=entityClass;this.emf=emf; }
    public <R> R read(Function<EntityManager,R> work) { EntityManager em=emf.createEntityManager();try{return work.apply(em);}finally{em.close();} }
    public <R> R transaction(Function<EntityManager,R> work) {
        EntityManager em=emf.createEntityManager();EntityTransaction tx=em.getTransaction();
        try { tx.begin();R result=work.apply(em);tx.commit();return result; }
        catch(RuntimeException e) { if(tx.isActive())tx.rollback();throw e; }
        finally { em.close(); }
    }
    public T find(ID id) { return read(em->em.find(entityClass,id)); }
    public List<T> findAll() { return read(em->em.createQuery("select e from "+entityClass.getSimpleName()+" e order by e.id",entityClass).getResultList()); }
    public void create(T entity) { Objects.requireNonNull(entity,"Entite requise");transaction(em->{em.persist(entity);return null;}); }
    public T update(T entity) { Objects.requireNonNull(entity,"Entite requise");return transaction(em->em.merge(entity)); }
    public void delete(T entity) { Objects.requireNonNull(entity,"Entite requise");transaction(em->{em.remove(em.contains(entity)?entity:em.merge(entity));return null;}); }
}
