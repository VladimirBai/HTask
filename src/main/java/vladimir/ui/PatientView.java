package vladimir.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import vladimir.dao.PatientDao;
import vladimir.model.Patient;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class PatientView extends VerticalLayout implements View {

    private Navigator navigator;
    private Button backBtn;
    private Button createBtn;
    private Button editBtn;
    private Button removeBtn;
    private Grid<Patient> grid;
    private HorizontalLayout horizontalLayout;

    public PatientView(Navigator navigator) {
        this.navigator = navigator;

        grid = new Grid<>(Patient.class);
        backBtn = new Button("Back");
        createBtn = new Button("Create Patient");
        editBtn = new Button("Edit Patient");
        removeBtn = new Button("Remove Patient");
        horizontalLayout = new HorizontalLayout();

        customiseWidth();



        horizontalLayout.addComponents(createBtn, editBtn, removeBtn);
        addComponent(backBtn);
        addComponent(grid);
        addComponent(horizontalLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        PatientDao patientDao = new PatientDao();
        List<Patient> patients = patientDao.findAll();
        grid.setColumns("name", "middleName", "lastName", "phone");
        grid.setItems(patients);

        backBtn.addClickListener(x -> navigator.navigateTo(""));
        createBtn.addClickListener(x -> UI.getCurrent().addWindow(new SubWindow()));
        removeBtn.addClickListener(x -> removeRow(patientDao));
        editBtn.addClickListener(x -> editRow());
    }

    private void customiseWidth() {
        grid.getColumn("name").setWidth(250);
        grid.getColumn("middleName").setWidth(250);
        grid.getColumn("lastName").setWidth(250);
        grid.getColumn("phone").setWidth(200);
        grid.setWidth("950");
    }

    private void editRow() {
        Optional<Patient> patient = grid.getSelectionModel().getFirstSelectedItem();
        patient.ifPresent(x -> UI.getCurrent().addWindow(new SubWindow(patient.get())));
    }

    private void removeRow(PatientDao patientDao) {
        Optional<Patient> patient = grid.getSelectionModel().getFirstSelectedItem();
        patient.ifPresent(x -> {
            Patient pat = patient.get();
            if (pat.getRecipes().size() > 0) {
                Notification.show("There is one or more recipes");
            } else {
                patientDao.remove(pat);
            }
        });
        grid.setItems(patientDao.findAll());
    }

    private class SubWindow extends Window {

        private TextField textFieldName;
        private TextField textFieldMiddleName;
        private TextField textFieldLastName;
        private TextField textFieldPhone;
        private Button addBtn;
        private Button cancelBtn;
        private GridLayout gridLayout;
        private String regex = "^[a-zA-Z]{2,20}+$";
        private String regexPhone = "^89[0-9]{9}$";
        private Patient patient;

        SubWindow() {
            super("Create patient");
            center();

            textFieldName = new TextField("Name");
            textFieldMiddleName = new TextField("Middle name");
            textFieldLastName = new TextField("Last name");
            textFieldPhone = new TextField("Phone");
            addBtn = new Button("Save");
            cancelBtn = new Button("Cancel");
            gridLayout = new GridLayout(2, 5);

            setContent(gridLayout);
            setComponentsAtLayout();
            addBtn.addClickListener(x -> saveData());
            cancelBtn.addClickListener(x -> close());
        }

        SubWindow(Patient patient) {
            this();
            this.patient = patient;
            textFieldName.setValue(patient.getName());
            textFieldMiddleName.setValue(patient.getMiddleName());
            textFieldLastName.setValue(patient.getLastName());
            textFieldPhone.setValue(patient.getPhone());
        }

        void setComponentsAtLayout() {
            gridLayout.addComponent(textFieldName, 0, 0,1, 0);
            gridLayout.addComponent(textFieldMiddleName, 0, 1, 1, 1);
            gridLayout.addComponent(textFieldLastName, 0, 2, 1, 2);
            gridLayout.addComponent(textFieldPhone, 0, 3, 1, 3);
            gridLayout.addComponent(addBtn, 0, 4);
            gridLayout.addComponent(cancelBtn, 1, 4);
        }

        void saveData() {
            Pattern pattern = Pattern.compile(regex);
            Pattern patternPhone = Pattern.compile(regexPhone);
            String name = textFieldName.getValue();
            String middleName = textFieldMiddleName.getValue();
            String lastName = textFieldLastName.getValue();
            String phone = textFieldPhone.getValue();
            if (!pattern.matcher(name).matches() || !pattern.matcher(middleName).matches() ||
                    !pattern.matcher(lastName).matches() || !patternPhone.matcher(phone).matches()) {
                Notification.show("Invalid data");
            } else {
                PatientDao patientDao = new PatientDao();
                if (this.patient == null) {
                    patientDao.save(new Patient(name, middleName, lastName, phone));
                } else {
                    this.patient.setName(name);
                    this.patient.setMiddleName(middleName);
                    this.patient.setLastName(lastName);
                    this.patient.setPhone(phone);
                    patientDao.update(this.patient);
                }
                grid.setItems(patientDao.findAll());
                UI.getCurrent().removeWindow(this);
                close();
            }
        }
    }

}
