package com.yuraha.botscrew.dao.impl;

import com.yuraha.botscrew.dao.BookDAO;
import com.yuraha.botscrew.model.Author;
import com.yuraha.botscrew.model.Book;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

public class BookDAOImpl implements BookDAO {
    private SessionFactory sessionFactory;

    public void setSessionFactory(SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    public void save(Book b) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.save(b);
        tx.commit();
        session.close();
    }

    @SuppressWarnings("unchecked")
    public List<Book> getList() {
        /* get all books from db*/
        Session session = this.sessionFactory.openSession();
        List<Book> bookList = session.createQuery("from Book").list();
        session.close();
        return bookList;
    }

    @SuppressWarnings("unchecked")
    public List<Book> getBooksByName(String title) {
        /* get list of books with the parametr title*/
        Session session = this.sessionFactory.openSession();
        EntityManager em = session.getEntityManagerFactory().createEntityManager();
        CriteriaBuilder builder = session.getEntityManagerFactory().getCriteriaBuilder();
        CriteriaQuery<Book> criteria = builder.createQuery( Book.class );
        Root<Book> bookRoot = criteria.from( Book.class );
        criteria.select(bookRoot);
        criteria.where( builder.equal( bookRoot.get( "title" ), title ) );
        List<Book> bookList;
        try {
            bookList = em.createQuery( criteria ).getResultList();
        }
        catch (NoResultException e){bookList = new ArrayList<Book>();}// if there are no books with this title - return empty list

        session.close();
        em.close();
        return bookList;
    }


    public void remove(Book book) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.remove(book);
        tx.commit();
        session.close();
    }

    public void edit(Book book) {
        Session session = this.sessionFactory.openSession();
        Transaction tx = session.beginTransaction();
        session.update(book);
        tx.commit();
        session.close();
    }

    /* check if book exist in db*/
    public boolean isExist(Book book) {
        List<Book> bookList = getBooksByName(book.getTitle());// get list of books with the same title

        if (bookList.isEmpty())
            return false;

        /* check author in books, if the same in example - return true*/
        if (isTheSameAuthor(bookList, book.getAuthor()))
            return true;

        return false;
    }

    private boolean isTheSameAuthor(List<Book> bookList, long author) {
        for (Book book : bookList)
        {
            if (book.getAuthor()==author)
                return true;
        }
        return false;
    }
}
