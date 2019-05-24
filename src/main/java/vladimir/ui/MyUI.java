package vladimir.ui;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.*;
import org.hsqldb.cmdline.SqlFile;
import org.hsqldb.cmdline.SqlToolError;
import vladimir.util.ConnectionCreator;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * This UI is the application entry point. A UI may either represent a browser window 
 * (or tab) or some part of an HTML page where a Vaadin application is embedded.
 * <p>
 * The UI is initialized using {@link #init(VaadinRequest)}. This method is intended to be 
 * overridden to add component to the user interface and initialize non-component functionality.
 */
@Theme("mytheme")

public class MyUI extends UI {
    private Navigator navigator;
    private  static final String DOCTORS_VIEW = "doctors";
    private static final String PATIENTS_VIEW = "patients";
    private static final String RECIPES_VIEW = "recipes";

    @Override
    protected void init(VaadinRequest request) {
        getPage().setTitle("My Vaadin app");

        try (Connection connection = ConnectionCreator.getConnection()) {
            SqlFile sqlFile = new SqlFile(new File("taskdb/table-create.sql"));
            sqlFile.setConnection(connection);
            sqlFile.execute();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SqlToolError sqlToolError) {
            sqlToolError.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        navigator = new Navigator(this, this);
        navigator.addView("", new MainView());
        navigator.addView(DOCTORS_VIEW, new DoctorView(navigator));
        navigator.addView(PATIENTS_VIEW, new PatientView(navigator));
        navigator.addView(RECIPES_VIEW, new RecipeView(navigator));
    }

    private class MainView extends VerticalLayout implements View {

        private Button buttonDoctorUrl;
        private Button buttonPatientUrl;
        private Button buttonRecipeUrl;

        MainView() {
            buttonDoctorUrl = new Button("Doctors");
            buttonPatientUrl = new Button("Patients");
            buttonRecipeUrl = new Button("Recipes");

            addComponents(buttonDoctorUrl, buttonPatientUrl, buttonRecipeUrl);
        }

        @Override
        public void enter(ViewChangeListener.ViewChangeEvent event) {
            buttonDoctorUrl.addClickListener(x -> {
                navigator.navigateTo(DOCTORS_VIEW);
            });
            buttonPatientUrl.addClickListener(x -> {
                navigator.navigateTo(PATIENTS_VIEW);
            });
            buttonRecipeUrl.addClickListener(x -> {
                navigator.navigateTo(RECIPES_VIEW);
            });
        }
    }

    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {
    }
}
