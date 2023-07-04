package ru.larisa.spring.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.larisa.spring.models.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@Component
public class PersonDAO {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public PersonDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Person> index(){
        return jdbcTemplate.query("SELECT * FROM public.person", new BeanPropertyRowMapper<>(Person.class));
    }
    public Person show (int id){
        return jdbcTemplate.query("SELECT * FROM public.person WHERE id=?", new Object[]{id},
                        new BeanPropertyRowMapper<>(Person.class))
                .stream().findAny().orElse(null);

//        return people.stream().filter(person -> person.getId() == id).findAny().orElse(null);

    }
    public void save(Person person){
        jdbcTemplate.update("INSERT INTO public.person VALUES(6, ?, ?, ?)", person.getName(),
                person.getAge(), person.getEmail());
    }
    public void update(int id, Person updatePerson){
        jdbcTemplate.update("UPDATE public.person SET  name=?, age=?, email=? WHERE id=?", updatePerson.getName(),
                updatePerson.getAge(), updatePerson.getEmail(), id);
    }
    public void delete(int id) throws SQLException {
        jdbcTemplate.update("DELETE FROM public.person WHERE id=?", id);
    }
}
