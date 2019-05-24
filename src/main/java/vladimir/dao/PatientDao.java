package vladimir.dao;

import org.hibernate.Session;
import vladimir.model.Patient;
import vladimir.util.HibernateUtil;

import java.util.List;

public class PatientDao {

    public void save(Patient patient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.getTransaction().begin();
            session.save(patient);
            session.getTransaction().commit();
        }
    }

    public Patient findById(long id) {
        Patient patient;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            patient = session.load(Patient.class, id);
        }
        return patient;
    }

    public List<Patient> findAll() {
        List<Patient> patients;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            patients = session.createQuery("select distinct p from Patient p left join fetch p.recipes").list();
        }
        return patients;
    }

    public void update(Patient patient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.getTransaction().begin();
            session.update(patient);
            session.getTransaction().commit();
        }
    }

    public void remove(Patient patient) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.getTransaction().begin();
            session.delete(patient);
            session.getTransaction().commit();
        }
    }

}
