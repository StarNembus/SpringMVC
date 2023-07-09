package ru.larisa.spring.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.larisa.spring.dao.PersonDAO;
import ru.larisa.spring.models.Person;
import ru.larisa.spring.util.PersonValidator;

import javax.validation.Valid;
import java.sql.SQLException;

@Controller
@RequestMapping("/people")
public class PeopleController {

    private final PersonDAO personDAO;
    private final PersonValidator personValidator;

    // зависимости
    @Autowired
    public PeopleController(PersonDAO personDAO, PersonValidator personValidator) {
        this.personDAO = personDAO;
        this.personValidator = personValidator;
    }

    @GetMapping()
    public String index(Model model) throws SQLException {
        // Получим всех людей и положим их в модель, чтобы передать их в представление
        model.addAttribute("people", personDAO.index());
        return "people/index";
    }
    @GetMapping("/{id}")
    public String show(@PathVariable("id") int id, Model model) throws SQLException {
        // Получим одного человека по id из DAO и передадим на отображение в представление
        model.addAttribute("person", personDAO.show(id));
        return "people/show";
    }
    @GetMapping("/new")
    public String newPerson(@ModelAttribute("person") Person person) {
        return "people/new";
    }

    @PostMapping()
    // @ModelAttribute добавляет объект в модель
    // @Valid проверяет на валидность
    public String create(@ModelAttribute("person") @Valid Person person,
                         // ошибка валидности помещается в отдельный объект bindingResult
                         BindingResult bindingResult) throws SQLException {
        personValidator.validate(person, bindingResult);
        // если в форме есть ошибки заново переходим на страницу создания человека
        if(bindingResult.hasErrors())
            return "people/new";
        // добавляем в базу данных
        personDAO.save(person);
        return "redirect:/people";
    }

    @GetMapping("/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) throws SQLException {
        model.addAttribute("person", personDAO.show(id));
        return "people/edit";
    }

    @PatchMapping("/{id}")
    public String update(@ModelAttribute("person") @Valid Person person,
                         BindingResult bindingResult,
                         @PathVariable("id") int id) throws SQLException {
        personValidator.validate(person, bindingResult);
        if(bindingResult.hasErrors())
            return "people/edit";
        personDAO.update(id, person);
        return "redirect:/people";
    }
    @DeleteMapping("/{id}")
    public String delete(@PathVariable("id") int id) throws SQLException {
        personDAO.delete(id);
        return "redirect:/people";
    }

}
