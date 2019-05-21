package vladimir.model;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Patient {

    @Id
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "NAME")
    private String name;

    @Column(name = "MIDDLE_NAME")
    private String middleName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "PHONE")
    private String phone;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "patient")
    private List<Recipe> recipes = new ArrayList<>();

    public Patient(String name, String middleName, String lastName, String phone) {
        this.name = name;
        this.middleName = middleName;
        this.lastName = lastName;
        this.phone = phone;
    }

    public Patient() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public List<Recipe> getRecipes() {
        return recipes;
    }

    public void setRecipes(List<Recipe> recipes) {
        this.recipes = recipes;
    }
}
