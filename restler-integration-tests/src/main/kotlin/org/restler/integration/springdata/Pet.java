package org.restler.integration.springdata;

import javax.persistence.*;
import java.io.Serializable;
import java.util.List;

/**
 * Created by rudenko on 10.02.2016.
 */

@Entity(name = "pets")
public class Pet implements Serializable {
    @Id
    private Long id;

    @Column
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner1_id")
    private Person person;

    public Pet(Long id, String name, Person person) {
        this.id = id;
        this.name = name;
        this.person = person;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Person getPerson() {
        return person;
    }

    // for JPA
    Pet() {
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }
}