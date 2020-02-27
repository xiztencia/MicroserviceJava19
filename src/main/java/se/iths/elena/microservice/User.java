package se.iths.elena.microservice;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Data
@NoArgsConstructor
@Entity
public class User {
    @Id
    @GeneratedValue Long id;
    String name;
    int age;

    public User(Long id, String name, int age){
        this.id = id;
        this.name = name;
        this.age = age;
    }
}
