package com.test.Controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.cache.annotation.Cacheable;
import org.hibernate.HibernateException;
import java.util.*;
import javax.json.*;
import javax.inject.Inject;
import com.test.PersonDAO;
import com.test.Skill;
import com.test.Person;

@RestController
public class AjaxController{
    
    @Inject private PersonDAO personDAO;
    
    @GetMapping("getPersonName")
    public ResponseEntity<String> getPersonName(@RequestParam("id") String personID){
        String name;
        Person person;
        person = personDAO.getPersonByID(Long.parseLong(personID));
        if(person == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        name = person.getFullName();
        return new ResponseEntity<>(Json.createObjectBuilder()
        .add("name", name)
        .build()
        .toString(), HttpStatus.OK);
    }
    
    @GetMapping("getPersonBrief")
    public ResponseEntity<String> getPersonBrief(@RequestParam("id") String personID){
        String brief;
        Person person = personDAO.getPersonByID(Long.parseLong(personID));
        if(person == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        brief = person.getBrief();
        return new ResponseEntity<>(Json.createObjectBuilder()
        .add("brief", brief)
        .build()
        .toString(), HttpStatus.OK);
    }
    
    @GetMapping("getPersonBG")
    public ResponseEntity<String> getPersonBG(@RequestParam("id") String personID){
        String BG;
        Person person;
        person = personDAO.getPersonByID(Long.parseLong(personID));
        if(person == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        BG = person.getBG();
        return new ResponseEntity<>(Json.createObjectBuilder()
        .add("BG", BG)
        .build()
        .toString(), HttpStatus.OK);
    }
    
    @GetMapping("getPersonSkills")
    public ResponseEntity<String> getPersonSkills(@RequestParam("id") String personID){
        List<Skill> personsSkills;
        Person person = personDAO.getPersonByID(Long.parseLong(personID));
        if(person == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        personsSkills = person.getSkills();
        JsonObjectBuilder builder = Json.createObjectBuilder();
        JsonArrayBuilder skillsBuilder = Json.createArrayBuilder();
        for(int a = 0; a < personsSkills.size(); a++){
            JsonObjectBuilder currentSkill = Json.createObjectBuilder();
            System.out.println("a = " + a + ", " + System.currentTimeMillis());
            Skill skill = personsSkills.get(a);
            if(skill == null)
                continue;
            currentSkill.add("type", skill.getType())
            .add("name", skill.getName());
            skillsBuilder.add(currentSkill.build());
        }
        builder.add("Skills", skillsBuilder.build());
        return new ResponseEntity<>(builder.build()
        .toString(), HttpStatus.OK);
    }
    
    @PostMapping("setPersonName")
    public ResponseEntity<String> setPersonName(@RequestParam("id") String personID, @RequestParam("newName") String newName){
        Person person = personDAO.getPersonByID(Long.parseLong(personID));
        if(person == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        person.setName(newName);
        personDAO.synchronizePerson(person);
        return new ResponseEntity<>(Json.createObjectBuilder()
        .add("result", "Name changed")
        .build()
        .toString(), HttpStatus.OK);
    }
    
    @PostMapping("setPersonSurname")
    public ResponseEntity<String> setPersonSurname(@RequestParam("id") String personID, @RequestParam("newSurname") String newSurname){
        Person person = personDAO.getPersonByID(Long.parseLong(personID));
        if(person == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        person.setSurname(newSurname);
        personDAO.synchronizePerson(person);
        return new ResponseEntity<>(Json.createObjectBuilder()
        .add("result", "Surname changed")
        .build()
        .toString(), HttpStatus.OK);
    }
    
    @PostMapping("setPersonBrief")
    public ResponseEntity<String> setPersonBrief(@RequestParam("id") String personID, @RequestParam("newBrief") String newBrief){
        Person person = personDAO.getPersonByID(Long.parseLong(personID));
        if(person == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        person.setBrief(newBrief);
        personDAO.synchronizePerson(person);
        return new ResponseEntity<>(Json.createObjectBuilder()
        .add("result", "Brief changed")
        .build()
        .toString(), HttpStatus.OK);
    }
    
    @PostMapping("setPersonBG")
    public ResponseEntity<String> setPersonBG(@RequestParam("id") String personID, @RequestParam("newBG") String newBG){
        Person person = personDAO.getPersonByID(Long.parseLong(personID));
        if(person == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        person.setBG(newBG);
        personDAO.synchronizePerson(person);
        return new ResponseEntity<>(Json.createObjectBuilder()
        .add("result", "BG changed")
        .build()
        .toString(), HttpStatus.OK);
    }
    
    @PostMapping("setPersonSkills")
    public ResponseEntity<String> setPersonSkills(@RequestParam("id") String personID, @RequestParam("skill") ArrayList<String> skills){
        Person person = personDAO.getPersonByID(Long.parseLong(personID));
        if(person == null){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        List<Skill> newSkills = new ArrayList<>();
        List<Skill> currentSkills = person.getSkills();
        forming:
        for(int a = 0; a < skills.size(); a++){
            String[] description = skills.get(a).split("_");
            String name = description[1];
            String type = description[0];
            Skill[] stream = currentSkills.stream().filter(skill -> {System.out.println(skill.getName()); return skill.getName().equals(name) && skill.getType().equals(type);}).toArray(Skill[]::new);
            if(stream.length > 0){
                newSkills.add(stream[0]);
                continue forming;
            }
            Skill skill = new Skill();
            skill.setName(name);
            skill.setType(type);
            newSkills.add(skill);
        }
        person.setSkills(newSkills);
        personDAO.synchronizePerson(person);
        return new ResponseEntity<>(Json.createObjectBuilder()
        .add("result", "Skills updated")
        .build()
        .toString(), HttpStatus.OK);
    }
    
}