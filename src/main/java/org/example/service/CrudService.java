package org.example.service;
import org.example.dao.GenericDAO;
import org.example.util.HibernateUtil;
import javax.persistence.EntityManagerFactory;
import java.io.Serializable;
import java.util.List;

public class CrudService<T,ID extends Serializable> {
    protected final GenericDAO<T,ID> dao;
    public CrudService(Class<T> type) { this(type,HibernateUtil.getSessionFactory()); }
    public CrudService(Class<T> type,EntityManagerFactory emf) { dao=new GenericDAO<>(type,emf); }
    public T getById(ID id) { return dao.find(id); }
    public List<T> getAll() { return dao.findAll(); }
    public void create(T entity) { validate(entity);dao.create(entity); }
    public T update(T entity) { validate(entity);return dao.update(entity); }
    public void delete(T entity) { validate(entity);dao.delete(entity); }
    private void validate(T entity) { if(entity==null)throw new IllegalArgumentException("L'entite ne doit pas etre nulle"); }
}
