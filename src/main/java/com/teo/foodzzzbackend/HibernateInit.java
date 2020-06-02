package com.teo.foodzzzbackend;

import org.hibernate.search.jpa.FullTextEntityManager;
import org.hibernate.search.jpa.Search;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;


@Component
public class HibernateInit implements ApplicationListener<ContextRefreshedEvent> {
    @PersistenceContext
    private EntityManager entityManager;


    @Override
    @Transactional
    public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
        FullTextEntityManager fullTextEntityManager = Search.getFullTextEntityManager(entityManager);
        try {
            fullTextEntityManager.createIndexer().startAndWait();
        } catch (InterruptedException e) {
            System.out.println("Error occured trying to build Hibernate Search indexes "
                    + e.toString());
        }
    }
}
