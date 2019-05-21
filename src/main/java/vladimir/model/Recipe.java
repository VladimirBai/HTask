package vladimir.model;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private long id;

    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "DATE_CREATED")
    private LocalDate dateCreation;

    @Column(name = "DATE_EXPIRATION")
    private LocalDate dateExpiration;

    @Column(name = "PRIORITY")
    private String priority;

    @ManyToOne
    @JoinColumn(name = "ID_DOCTOR", nullable = false)
    private Doctor doctor;

    @ManyToOne
    @JoinColumn(name = "ID_PATIENT", nullable = false)
    private Patient patient;

    public Recipe(String description, LocalDate dateCreation, LocalDate dateExpiration, String priority) {
        this.description = description;
        this.dateCreation = dateCreation;
        this.dateExpiration = dateExpiration;
        this.priority = priority;
    }

    public Recipe() {
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDate dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDate getDateExpiration() {
        return dateExpiration;
    }

    public void setDateExpiration(LocalDate dateExpiration) {
        this.dateExpiration = dateExpiration;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }
}
