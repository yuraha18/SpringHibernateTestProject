package com.yuraha.botscrew.dao;

import com.yuraha.botscrew.model.Book;

import java.util.List;


public interface BookDAO {
    void save(Book b);
    List<Book> getList();
    List<Book> getBooksByName(String title);
    void remove(Book book);
    void edit(Book book);
    boolean isExist(Book book);
}
