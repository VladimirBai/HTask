package vladimir.dao;

import org.hibernate.Session;
import vladimir.model.Doctor;
import vladimir.util.HibernateUtil;

import javax.transaction.Transactional;
import java.util.List;

public class DoctorDao {

    public void save(Doctor doctor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.getTransaction().begin();
            session.save(doctor);
            session.getTransaction().commit();
        }
    }

    public Doctor findById(long id) {
        Doctor doctor;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            doctor = session.load(Doctor.class, id);
        }
        return doctor;
    }

    public List<Doctor> findAll() {
        List<Doctor> doctors;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            doctors = session.createQuery("select distinct d from Doctor d left join fetch d.recipes").getResultList();
        }
        return doctors;
    }

    public void update(Doctor doctor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.getTransaction().begin();
            session.update(doctor);
            session.getTransaction().commit();
        }
    }

    public void remove(Doctor doctor) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.getTransaction().begin();
            session.delete(doctor);
            session.getTransaction().commit();
        }
    }
}
