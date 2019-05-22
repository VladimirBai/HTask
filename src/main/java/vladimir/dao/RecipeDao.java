package vladimir.dao;

import org.hibernate.Session;
import vladimir.model.Recipe;
import vladimir.util.HibernateUtil;

import javax.persistence.Query;
import java.util.List;

public class RecipeDao {

    public void save(Recipe recipe) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.getTransaction().begin();
            session.save(recipe);
            session.getTransaction().commit();
        }
    }

    public Recipe findById(long id) {
        Recipe recipe;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            recipe = session.load(Recipe.class, id);
        }
        return recipe;
    }

    public List<Recipe> findAll() {
        List<Recipe> recipes;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            recipes = session.createQuery("from Recipe ").list();
        }
        return recipes;
    }

    public void update(Recipe recipe) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.getTransaction().begin();
            session.update(recipe);
            session.getTransaction().commit();
        }
    }

    public void remove(Recipe recipe) {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            session.getTransaction().begin();
            session.delete(recipe);
            session.getTransaction().commit();
        }
    }

    public List<Recipe> filter(String description, String priority, String doctorData, String patientData) {
        List<Recipe> recipes;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Query query = session.createQuery("from Recipe where description like ?1 or doctor.name like ?2 or patient.name like ?3 or priority like ?4");
            query.setParameter(1, "%" + description + "%");
            query.setParameter(2, doctorData);
            query.setParameter(3, patientData);
            query.setParameter(4, priority);
            recipes = query.getResultList();
        }
        return recipes;
    }

}
