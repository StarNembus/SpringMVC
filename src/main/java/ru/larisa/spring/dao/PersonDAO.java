package ru.larisa.spring.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import ru.larisa.spring.models.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public Optional<Person> show (String email) {
        return jdbcTemplate.query("SELECT * FROM public.person WHERE email=?", new Object[] {email},
                new BeanPropertyRowMapper<>(Person.class)).stream().findAny();
    }
    public Person show (int id){
        return jdbcTemplate.query("SELECT * FROM public.person WHERE id=?", new Object[]{id},
                        new BeanPropertyRowMapper<>(Person.class))
                .stream().findAny().orElse(null);

//        return people.stream().filter(person -> person.getId() == id).findAny().orElse(null);

    }
    public void save(Person person){
        jdbcTemplate.update("INSERT INTO public.person(name, age, email, address) VALUES(?, ?, ?, ?)", person.getName(),
                person.getAge(), person.getEmail(), person.getAddress());
    }
    public void update(int id, Person updatePerson){
        jdbcTemplate.update("UPDATE public.person SET  name=?, age=?, email=?, address=? WHERE id=?", updatePerson.getName(),
                updatePerson.getAge(), updatePerson.getEmail(), updatePerson.getAddress(), id);
    }
    public void delete(int id) throws SQLException {
        jdbcTemplate.update("DELETE FROM public.person WHERE id=?", id);
    }

    ////////////////////////////////////
    /// Тестируем производительность пакетной вставки
    ////////////////////////////////////
    public void testMultipleUpdate() {
        List<Person> people = create1000People();
        long before = System.currentTimeMillis();

        for (Person person : people) {
            jdbcTemplate.update("INSERT INTO public.person VALUES(?, ?, ?, ?)",person.getId(), person.getName(),
                    person.getAge(), person.getEmail());
        }
        long after = System.currentTimeMillis();
        System.out.println("Time: " + (after - before));
    }

    public void testBatchUpdate() {
        List<Person> people = create1000People();

        long before = System.currentTimeMillis();
        jdbcTemplate.batchUpdate("INSERT INTO public.person VALUES(?, ?, ?, ?)",
                new BatchPreparedStatementSetter() {
                    @Override
                    public void setValues(PreparedStatement preparedStatement, int i) throws SQLException {
                        preparedStatement.setInt(1, people.get(i).getId());
                        preparedStatement.setString(2, people.get(i).getName());
                        preparedStatement.setInt(3, people.get(i).getAge());
                        preparedStatement.setString(4, people.get(i).getEmail());
                    }

                    @Override
                    public int getBatchSize() {
                        return people.size();
                    }
                });
        long after = System.currentTimeMillis();
        System.out.println("Time: " + (after - before));
    }

    private List<Person> create1000People() {
        List<Person> people = new ArrayList<>();
        for (int i = 0; i < 1000; i++) {
            people.add(new Person(i, "Name" + i, 30, "test" + i + "mail.ru", "some address"));
        }
        return people;
    }

}
