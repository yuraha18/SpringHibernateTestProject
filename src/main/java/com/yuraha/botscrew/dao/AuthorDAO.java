package com.yuraha.botscrew.dao;

import com.yuraha.botscrew.model.Author;


public interface AuthorDAO {
    Author save(Author author);
    String getNameById(long id);
}
