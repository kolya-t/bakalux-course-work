package io.bakalux.course.service;

import io.bakalux.course.model.Contact;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.table.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import javax.validation.constraints.Pattern;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;

import static org.springframework.shell.table.CellMatchers.at;

@ShellComponent
public class ShellService {
    private static final String PHONE_PATTERN = "[0-9]{10}";
    private static final String PHONE_MESSAGE = "Phone number should be 10 numbers like 9991234567";

    @Autowired
    private ContactService contactService;

    @ShellMethod(key = "list", value = "Show contact list.")
    public Table getContacts() throws IOException {
        return asTable(contactService.findAll());
    }

    @ShellMethod(key = "list_sorted_firstname", value = "Show sorted by firstName contact list.")
    public Table getContactsSortedByFirstName() throws IOException {
        return asTable(contactService.findAll(Comparator.comparing(Contact::getFirstName)));
    }

    @ShellMethod(key = "list_sorted_lastname", value = "Show sorted by lastName contact list.")
    public Table getContactsSortedByLastName() throws IOException {
        return asTable(contactService.findAll(Comparator.comparing(Contact::getLastName)));
    }

    @ShellMethod(key = "list_sorted_phone", value = "Show sorted by phone contact list.")
    public Table getContactsSortedByPhone() throws IOException {
        return asTable(contactService.findAll(Comparator.comparing(Contact::getPhone)));
    }

    @ShellMethod(key = "list_sorted_birthday", value = "Show sorted by birthday contact list.")
    public Table getContactsSortedByBirthday() throws IOException {
        return asTable(contactService.findAll(Comparator.comparing(Contact::getBirthday)));
    }

    @ShellMethod(key = "search", value = "Search contacts by lastName.", prefix = "")
    public Table getContactsByLastName(String lastName) throws IOException {
        return asTable(contactService.findByLastName(lastName));
    }

    @ShellMethod(key = "edit", value = "Modify contact.", prefix = "")
    public void edit(
            @NotNull Long id,
            @NotBlank String firstName,
            @NotBlank String lastName,
            @Pattern(regexp = PHONE_PATTERN, message = PHONE_MESSAGE) String phone,
            @PastOrPresent LocalDate birthday
    ) throws IOException {
        contactService.update(new Contact(id, firstName, lastName, phone, birthday));
    }

    @ShellMethod(key = "add", value = "Add new contact.", prefix = "")
    public void addContact(
            @NotBlank String firstName,
            @NotBlank String lastName,
            @Pattern(regexp = PHONE_PATTERN, message = PHONE_MESSAGE) String phone,
            @PastOrPresent LocalDate birthday
    ) throws IOException {
        contactService.save(new Contact(firstName, lastName, phone, birthday));
    }

    @ShellMethod(key = "file", value = "Show json filename used in app.")
    public String getFilename() {
        return contactService.getFilename();
    }

    private Table asTable(List<Contact> contacts) {
        Object[][] data = new Object[contacts.size() + 1][5];
        TableModel model = new ArrayTableModel(data);
        TableBuilder tableBuilder = new TableBuilder(model);

        data[0] = new Object[]{"Id", "First name", "Last name", "Phone", "Birthday"};

        for (int r = 1; r < data.length; r++) {
            Contact contact = contacts.get(r - 1);
            data[r][0] = contact.getId();
            data[r][1] = contact.getFirstName();
            data[r][2] = contact.getLastName();
            data[r][3] = contact.getPhone();
            data[r][4] = contact.getBirthday();
        }

        for (int r = 0; r < data.length; r++) {
            for (int c = 0; c < data[r].length; c++) {
                tableBuilder.on(at(r, c)).addAligner(SimpleHorizontalAligner.center);
                tableBuilder.on(at(r, c)).addAligner(SimpleVerticalAligner.middle);
            }
        }

        return tableBuilder.addFullBorder(BorderStyle.fancy_light).build();
    }
//    add first-name Колян last-name Топорков phone 9996338528 birthday 2018-01-02
}
