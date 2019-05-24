package vladimir.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import vladimir.dao.DoctorDao;
import vladimir.dao.PatientDao;
import vladimir.dao.RecipeDao;
import vladimir.model.Doctor;
import vladimir.model.Patient;
import vladimir.model.Recipe;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class RecipeView extends VerticalLayout implements View {

    private Navigator navigator;
    private Button backBtn;
    private Button createBtn;
    private Button editBtn;
    private Button removeBtn;
    private Button doFilterBtn;
    private Grid<Recipe> grid;
    private Panel filterPanel;
    private TextField textFilterDescription;
    private TextField textFilterPriority;
    private TextField textFilterDoctor;
    private TextField textFilterPatient;
    private HorizontalLayout horizontalLayout;

    public RecipeView(Navigator navigator) {
        this.navigator = navigator;

        grid = new Grid<>(Recipe.class);
        backBtn = new Button("Back");
        createBtn = new Button("Create Recipe");
        editBtn = new Button("Edit Recipe");
        removeBtn = new Button("Remove Recipe");
        doFilterBtn = new Button("Search");
        filterPanel = new Panel("Filter");
        textFilterDescription = new TextField();
        textFilterPriority = new TextField();
        textFilterDoctor = new TextField();
        textFilterPatient = new TextField();
        horizontalLayout = new HorizontalLayout();

        horizontalLayout.addComponents(createBtn, editBtn, removeBtn);

        filterConstructor();

        textFilterDescription.setPlaceholder("Description");
        textFilterPriority.setPlaceholder("Priority");
        textFilterDoctor.setPlaceholder("Doctor name");
        textFilterPatient.setPlaceholder("Patient name");

        addComponent(backBtn);
        addComponent(filterPanel);
        addComponent(grid);
        addComponent(horizontalLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        RecipeDao recipeDao = new RecipeDao();
        List<Recipe> recipes = recipeDao.findAll();

        grid.setColumns("description", "dateCreation", "dateExpiration", "priority");
        grid.addColumn(recipe -> (recipe.getDoctor().getName() + " " + recipe.getDoctor().getLastName()))
                .setCaption("Doctor").setId("doctor");
        grid.addColumn(recipe -> (recipe.getPatient().getName() + " " + recipe.getPatient().getLastName()))
                .setCaption("Patient").setId("patient");

        grid.setItems(recipes);
        customiseWidth();

        backBtn.addClickListener(x -> navigator.navigateTo(""));
        createBtn.addClickListener(x -> UI.getCurrent().addWindow(new SubWindow()));
        removeBtn.addClickListener(x -> removeRow(recipeDao));
        editBtn.addClickListener(x -> editRow());
        doFilterBtn.addClickListener(x -> doFilter(recipeDao));
    }

    private void filterConstructor() {
        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.addComponents(textFilterDescription, textFilterPriority, textFilterDoctor, textFilterPatient, doFilterBtn);
        filterPanel.setContent(horizontalLayout);
        filterPanel.setWidth("1000");
    }

    private void customiseWidth() {
        grid.getColumn("description").setWidth(300);
        grid.getColumn("dateCreation").setWidth(150);
        grid.getColumn("dateExpiration").setWidth(150);
        grid.getColumn("priority").setWidth(150);
        grid.getColumn("doctor").setWidth(300);
        grid.getColumn("patient").setWidth(300);
        grid.setWidth("1350");
    }

    private void doFilter(RecipeDao recipeDao) {
        String description = textFilterDescription.getValue();
        String priority = textFilterPriority.getValue().toUpperCase();
        String doctorData = textFilterDoctor.getValue();
        String patientData = textFilterPatient.getValue();
        if (description.equals("") && doctorData.equals("") && patientData.equals("") && priority.equals("")) {
            grid.setItems(recipeDao.findAll());
        } else {
            description = description.equals("") ? null : description;
            grid.setItems(recipeDao.filter(description, priority, doctorData, patientData));
        }
    }

    private void editRow() {
        Optional<Recipe> recipe = grid.getSelectionModel().getFirstSelectedItem();
        recipe.ifPresent(x -> UI.getCurrent().addWindow(new SubWindow(recipe.get())));
    }

    private void removeRow(RecipeDao recipeDao) {
        Optional<Recipe> recipe = grid.getSelectionModel().getFirstSelectedItem();
        recipe.ifPresent(x -> recipeDao.remove(recipe.get()));
        grid.setItems(recipeDao.findAll());
    }

    private class SubWindow extends Window {

        private TextArea textAreaDescription;
        private DateField dateFieldCreation;
        private DateField dateFieldExpiration;
        private ComboBox<String> comboBoxPriority;
        private ComboBox<Doctor> doctorComboBox;
        private ComboBox<Patient> patientComboBox;
        private Button addBtn;
        private Button cancelBtn;
        private GridLayout gridLayout;
        private Recipe recipe;

        SubWindow() {
            super("Create patient");
            center();

            textAreaDescription = new TextArea("Description");
            dateFieldCreation = new DateField("When created", LocalDate.now());
            dateFieldExpiration = new DateField("Date expiration", LocalDate.now());
            comboBoxPriority = new ComboBox<>("Chose priority");
            doctorComboBox = new ComboBox<>("Chose doctor");
            patientComboBox = new ComboBox<>("Chose patient");
            addBtn = new Button("Save");
            cancelBtn = new Button("Cancel");
            gridLayout = new GridLayout(4, 7);

            textAreaDescription.setWidth("400");
            textAreaDescription.setMaxLength(100);

            comboBoxPriority.setItems("NORMAL", "CITO", "STATIM");
            doctorComboBox.setItems(new DoctorDao().findAll());
            patientComboBox.setItems(new PatientDao().findAll());

            doctorComboBox.setItemCaptionGenerator(doctor -> doctor.getName() + " " + doctor.getLastName());
            patientComboBox.setItemCaptionGenerator(patient -> patient.getName() + " " + patient.getLastName());

            setContent(gridLayout);
            setComponentsAtLayout();
            addBtn.addClickListener(x -> saveData());
            cancelBtn.addClickListener(x -> close());
        }

        SubWindow(Recipe recipe) {
            this();
            this.recipe = recipe;
            textAreaDescription.setValue(recipe.getDescription());
            dateFieldExpiration.setValue(recipe.getDateExpiration());
            dateFieldCreation.setValue(recipe.getDateCreation());
            comboBoxPriority.setValue(recipe.getPriority());
            doctorComboBox.setValue(recipe.getDoctor());
            patientComboBox.setValue(recipe.getPatient());
        }

        void setComponentsAtLayout() {
            gridLayout.addComponent(textAreaDescription, 0, 0,3, 1);
            gridLayout.addComponent(dateFieldCreation, 0, 2, 1, 2);
            gridLayout.addComponent(dateFieldExpiration, 2, 2, 3, 2);
            gridLayout.addComponent(doctorComboBox, 0, 4, 1, 4);
            gridLayout.addComponent(patientComboBox, 2, 4, 3, 4);
            gridLayout.addComponent(comboBoxPriority, 0, 5, 1, 5);
            gridLayout.addComponent(addBtn, 2, 5);
            gridLayout.addComponent(cancelBtn, 3, 5);
        }

        void saveData() {
            String description = textAreaDescription.getValue();
            LocalDate dateCreation = dateFieldCreation.getValue();
            LocalDate dateExpiration = dateFieldExpiration.getValue();
            Optional<String> priority = comboBoxPriority.getSelectedItem();
            Optional<Doctor> doctor = doctorComboBox.getSelectedItem();
            Optional<Patient> patient = patientComboBox.getSelectedItem();
            if (description.equals("") || !priority.isPresent() || !doctor.isPresent() || !patient.isPresent()) {
                Notification.show("Please check input data");
            } else {
                RecipeDao recipeDao = new RecipeDao();
                if (this.recipe == null) {
                    recipeDao.save(new Recipe(description, dateCreation, dateExpiration, priority.get(),
                            doctor.get(), patient.get()));
                } else {
                    this.recipe.setDescription(description);
                    this.recipe.setDateCreation(dateCreation);
                    this.recipe.setDateExpiration(dateExpiration);
                    this.recipe.setPriority(priority.get());
                    this.recipe.setDoctor(doctor.get());
                    this.recipe.setPatient(patient.get());
                    recipeDao.update(this.recipe);
                }
                grid.setItems(recipeDao.findAll());
                close();
            }
        }
    }
}
