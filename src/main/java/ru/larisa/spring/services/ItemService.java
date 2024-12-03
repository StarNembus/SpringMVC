package ru.larisa.spring.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.larisa.spring.models.Item;
import ru.larisa.spring.models.Person;
import ru.larisa.spring.repositories.ItemsRepository;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class ItemService {
    private final ItemsRepository itemsRepository;
    @Autowired
    public ItemService(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }
    public List<Item> findByItemName(String itemName) {
        return itemsRepository.findByItemName(itemName);
    }
    public List<Item> findByOwner(Person owner) {
        return itemsRepository.findByOwner(owner);
    }

}
