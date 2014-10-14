package com.citynix.tools.db.entities;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Book {

    @Id
    private String isbn;

    private String title;

    private Author author;

    public Author getAuthor()
    {
	return author;
    }

    public void setAuthor(Author author)
    {
	this.author = author;
    }

    public String getIsbn()
    {
	return isbn;
    }

    public void setIsbn(String isbn)
    {
	this.isbn = isbn;
    }

    public String getTitle()
    {
	return title;
    }

    public void setTitle(String title)
    {
	this.title = title;
    }

}
