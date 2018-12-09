package io.bakalux.course.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import io.bakalux.course.model.Contact;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class ContactService {
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private LastIdService lastIdService;

    @Getter
    @Value("${filename}")
    private String filename;

    public List<Contact> findAll() throws IOException {
        List<Contact> contacts = new ArrayList<>();
        try {
            contacts.addAll(Arrays.asList(objectMapper.readValue(new File(filename), Contact[].class)));
        } catch (MismatchedInputException ignored) {
        } catch (FileNotFoundException ignored) {
            createFile();
            return findAll();
        }

        try {
            long maxId = contacts
                    .stream()
                    .mapToLong(Contact::getId)
                    .max()
                    .getAsLong();
            lastIdService.setLastId(maxId);
        } catch (RuntimeException ignored) {
        }

        return contacts;
    }

    public List<Contact> findAll(Comparator<? super Contact> comparator) throws IOException {
        return findAll()
                .stream()
                .sorted(comparator)
                .collect(Collectors.toList());
    }

    public List<Contact> findByLastName(String lastName) throws IOException {
        return findAll()
                .stream()
                .filter(contact -> contact.getLastName().equalsIgnoreCase(lastName))
                .collect(Collectors.toList());
    }

    public void save(Contact contact) throws IOException {
        List<Contact> list = findAll();
        lastIdService.next();
        contact.setId(lastIdService.getLastId());
        list.add(contact);
        save(list);
    }

    private void save(List<Contact> contacts) throws IOException {
        try {
            objectMapper.writeValue(new File(filename), contacts);
        } catch (FileNotFoundException ignored) {
            createFile();
            save(contacts);
        }
    }

    private void createFile() throws IOException {
        //noinspection ResultOfMethodCallIgnored
        new File(filename).createNewFile();
    }

    public void update(Contact newContact) throws IOException {
        Long id = newContact.getId();
        List<Contact> updatedList = findAll()
                .stream()
                .map(oldContact -> oldContact.getId().equals(id) ? newContact : oldContact)
                .collect(Collectors.toList());
        save(updatedList);
    }
}
