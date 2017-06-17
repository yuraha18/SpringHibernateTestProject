package com.yuraha.botscrew.dao.impl;

import com.yuraha.botscrew.dao.AuthorDAO;
import com.yuraha.botscrew.model.Author;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;


public class AuthorDAOImpl implements AuthorDAO {

    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    /* save author in db*/
    public Author save(Author author) {
        /* check if the same author is in db return it's id
        * that's why whole rows in this table is unique*/
     Author savedAuthor = getByName(author.getName());
        if (savedAuthor!=null)
            return savedAuthor;

        return addAuthorToDb(author);
    }

    /* get author name for printing book info*/
    public String getNameById(long id) {
        Session session = this.sessionFactory.openSession();
       String name = session.get(Author.class, id).getName();
        session.close();
        return name;
    }

    /* save new author in db*/
    private Author addAuthorToDb(Author author) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(author);
        tx.commit();
        session.close();
        return author;
    }

    @SuppressWarnings("unchecked")
    private Author getByName(String name)
    {
        /* get author object from its name, it's valuable for knowing author id*/
        Session session = this.sessionFactory.openSession();
        EntityManager em = session.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder builder = session.getEntityManagerFactory().getCriteriaBuilder();
        CriteriaQuery<Author> criteria = builder.createQuery( Author.class );
        Root<Author> authorRoot = criteria.from( Author.class );
        criteria.select(authorRoot);
        criteria.where( builder.equal( authorRoot.get( "name" ), name ) );
        Author author;
        try {
             author = em.createQuery(criteria).getSingleResult();
        }
       catch (NoResultException e){author=null;}
        session.close();
        em.close();
        return author;
    }
}
