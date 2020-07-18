package com.test;

import org.hibernate.Session; 
import org.hibernate.Transaction;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.hibernate.HibernateException; 
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.*;
import org.springframework.stereotype.Component;
import java.io.FileNotFoundException;
import javax.inject.Inject;
import java.util.*;

@Component
public class PersonDAO{
    
    private SessionFactory sessionFactory;
    @Inject private CacheManager cacheManager;
    
    public PersonDAO(){
        sessionFactory = new Configuration().configure().buildSessionFactory();
    }
    
    @Cacheable("persons")
    public Person getPersonByID(long id) throws HibernateException{
        Session session = sessionFactory.openSession();
        Person person = null;
        try{
            person = (Person) session.get(Person.class, id);
        }catch(HibernateException e){
            e.printStackTrace();
            throw e;
        }finally{
            session.close();
        }
        return person;
    }
    
    public void synchronizePerson(Person person) throws HibernateException{ // analogous to persistPerson in fact
        Session session = sessionFactory.openSession();
        Transaction transaction = null;
        try{
            if(getPersonByID(person.getId()) == null){
                System.out.println("An attempt to synchronize a person was made but no such person was found in DB");
                return;
            }
            session.merge(person);
            transaction = session.beginTransaction();
            session.flush();
            transaction.commit();
            System.out.println("Person merged. His BG is " + person.getBG());
        }catch(HibernateException e){
            if(transaction != null)
                transaction.rollback();
            e.printStackTrace();
            throw e;
        }finally{
            session.close();
        }
        Cache cache = cacheManager.getCache("persons");
        cache.evict(person.getId());
        cache.put(person.getId(), person);
    }
    
}