package io.bakalux.course.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Contact {
    @Setter
    private Long id;
    private String firstName;
    private String lastName;
    private String phone;
    private LocalDate birthday;

    public Contact(String firstName, String lastName, String phone, LocalDate birthday) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.birthday = birthday;
    }
}
