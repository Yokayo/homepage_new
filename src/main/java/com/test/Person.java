package com.test;

import java.util.*;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import com.test.Skill;

@Entity @Table(name = "persons")
@NoArgsConstructor
public class Person{
    
    @Column(name = "name")
    @Getter @Setter
    private String name;
    
    @Column(name = "surname")
    @Getter @Setter
    private String surname;
    
    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private long id;
    
    @Column(name = "brief")
    @Getter @Setter
    private String brief;
    
    @Column(name = "bg")
    @Getter @Setter
    private String BG;
    
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id")
    @Getter @Setter
    private List<Skill> skills = new ArrayList<>();
    
    public String getFullName(){
        return name + " " + surname;
    }
    
    @Override
    public boolean equals(Object object){
        Person person = (Person) object;
        return person.id == this.id;
    }
    
}