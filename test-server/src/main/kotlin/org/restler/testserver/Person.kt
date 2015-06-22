package org.restler.testserver

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
public class Person {
    Id
    GeneratedValue(strategy = GenerationType.AUTO)
    private var id: Long = 0;

    private var firstName = String();
    private var lastName = String();

    fun getFirstName(): String {
        return firstName;
    }

    fun setFirstName(firstName: String) {
        this.firstName = firstName;
    }

    fun getLastName(): String {
        return lastName;
    }

    fun setLastName(lastName: String) {
        this.lastName = lastName;
    }
}