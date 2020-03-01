package se.iths.elena.microservice;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class Book {
    @Id
    @GeneratedValue Long id;
    String title;
    String author;
    int year;
    int pages;
    String genre;

    public Book(Long id, String title, String author, int year, int pages, String genre){
        this.id = id;
        this.title = title;
        this.author = author;
        this.year = year;
        this.pages = pages;
        this.genre = genre;
    }
}
