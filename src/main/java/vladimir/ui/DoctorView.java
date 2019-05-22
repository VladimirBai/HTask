package vladimir.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.ui.*;
import jdk.nashorn.internal.runtime.options.Option;
import vladimir.dao.DoctorDao;
import vladimir.model.Doctor;

import javax.print.Doc;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

public class DoctorView extends VerticalLayout implements View {

    private Navigator navigator;
    private Button backBtn;
    private Button createBtn;
    private Button editBtn;
    private Button removeBtn;
    private Button statBtn;
    private Grid<Doctor> grid;
    private HorizontalLayout horizontalLayout;

    public DoctorView(Navigator navigator) {
        this.navigator = navigator;

        grid = new Grid<>(Doctor.class);
        backBtn = new Button("Back");
        createBtn = new Button("Create Doctor");
        editBtn = new Button("Edit Doctor");
        removeBtn = new Button("Remove Doctor");
        statBtn = new Button("Show statistic");
        horizontalLayout = new HorizontalLayout();

        horizontalLayout.addComponents(createBtn, editBtn, removeBtn, statBtn);
        addComponent(backBtn);
        addComponent(grid);
        addComponent(horizontalLayout);
    }

    @Override
    public void enter(ViewChangeListener.ViewChangeEvent event) {
        DoctorDao doctorDao = new DoctorDao();
        List<Doctor> doctors = doctorDao.findAll();
        grid.setColumns("name", "middleName", "lastName", "specialization");
        grid.setItems(doctors);
        customiseWidth();

        backBtn.addClickListener(x -> navigator.navigateTo(""));
        statBtn.addClickListener(x -> getStatistics());
        createBtn.addClickListener(x -> UI.getCurrent().addWindow(new SubWindow()));
        removeBtn.addClickListener(x -> removeRow(doctorDao));
        editBtn.addClickListener(x -> editRow());
    }

    private void customiseWidth() {
        grid.getColumn("name").setWidth(250);
        grid.getColumn("middleName").setWidth(250);
        grid.getColumn("lastName").setWidth(250);
        grid.getColumn("specialization").setWidth(300);
        grid.setWidth("1050");
    }

    private void editRow() {
        Optional<Doctor> doctor = grid.getSelectionModel().getFirstSelectedItem();
        doctor.ifPresent(x -> UI.getCurrent().addWindow(new SubWindow(doctor.get())));
    }

    private void removeRow(DoctorDao doctorDao) {
        Optional<Doctor> doctor = grid.getSelectionModel().getFirstSelectedItem();
        doctor.ifPresent(x -> doctorDao.remove(doctor.get()));
        grid.setItems(doctorDao.findAll());
    }

    private void getStatistics() {
        String dataToShow;
        Optional<Doctor> doctor = grid.getSelectionModel().getFirstSelectedItem();
        dataToShow = doctor.map(x -> String.valueOf(x.getRecipes().size())).orElse("There is no selected row");
        Notification.show(dataToShow);
    }

    private class SubWindow extends Window {

        private TextField textFieldName;
        private TextField textFieldMiddleName;
        private TextField textFieldLastName;
        private TextField textFieldSpecialization;
        private Button addBtn;
        private Button cancelBtn;
        private GridLayout gridLayout;
        private String regex = "^[a-zA-Z]{2,20}+$";
        private Doctor doctor;

        SubWindow() {
            super("Create doctor");
            center();

            textFieldName = new TextField("Name");
            textFieldMiddleName = new TextField("Middle name");
            textFieldLastName = new TextField("Last name");
            textFieldSpecialization = new TextField("Specialization");
            addBtn = new Button("Save");
            cancelBtn = new Button("Cancel");
            gridLayout = new GridLayout(2, 5);

            setContent(gridLayout);
            setComponentsAtLayout();
            addBtn.addClickListener(x -> saveData());
            cancelBtn.addClickListener(x -> close());
        }

        SubWindow(Doctor doctor) {
            this();
            this.doctor = doctor;
            textFieldName.setValue(doctor.getName());
            textFieldMiddleName.setValue(doctor.getMiddleName());
            textFieldLastName.setValue(doctor.getLastName());
            textFieldSpecialization.setValue(doctor.getSpecialization());
        }

        void setComponentsAtLayout() {
            gridLayout.addComponent(textFieldName, 0, 0,1, 0);
            gridLayout.addComponent(textFieldMiddleName, 0, 1, 1, 1);
            gridLayout.addComponent(textFieldLastName, 0, 2, 1, 2);
            gridLayout.addComponent(textFieldSpecialization, 0, 3, 1, 3);
            gridLayout.addComponent(addBtn, 0, 4);
            gridLayout.addComponent(cancelBtn, 1, 4);
        }

        void saveData() {
            Pattern pattern = Pattern.compile(regex);
            String name = textFieldName.getValue();
            String middleName = textFieldMiddleName.getValue();
            String lastName = textFieldLastName.getValue();
            String specialization = textFieldSpecialization.getValue();
            if (!pattern.matcher(name).matches() || !pattern.matcher(middleName).matches() ||
                    !pattern.matcher(lastName).matches() || !pattern.matcher(specialization).matches()) {
                Notification.show("Invalid data");
            } else {
                DoctorDao doctorDao = new DoctorDao();
                if (this.doctor == null) {
                    doctorDao.save(new Doctor(name, middleName, lastName, specialization));
                } else {
                    this.doctor.setName(name);
                    this.doctor.setMiddleName(middleName);
                    this.doctor.setLastName(lastName);
                    this.doctor.setSpecialization(specialization);
                    doctorDao.update(this.doctor);
                }
                grid.setItems(doctorDao.findAll());
                close();
            }
        }
    }
}
