package com.test;

import java.util.*;
import javax.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity @Table(name = "skills")
@NoArgsConstructor
public class Skill{
    
    @Column(name = "type")
    @Getter @Setter
    private String type;
    
    @Column(name = "skill")
    @Getter @Setter
    private String name;
    
    @Id
    @Column(name = "skill_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Getter @Setter
    private long id;
    
}