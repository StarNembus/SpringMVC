package ru.larisa.spring.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import ru.larisa.spring.dao.PersonDAO;
import ru.larisa.spring.models.Person;
@Component
public class PersonValidator implements Validator {
    // обращение к базе данных
    private final PersonDAO personDAO;
    @Autowired
    public PersonValidator(PersonDAO personDAO) {
        this.personDAO = personDAO;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return Person.class.equals(aClass);
    }

    @Override
    public void validate(Object o, Errors errors) {
        Person person = (Person) o;
        if (personDAO.show(person.getEmail()).isPresent()) {
            errors.rejectValue("email", "", "This email is already taken");
        }
        // посмотреть, есть ли человек с таким же email'ом в БД
    }
}
