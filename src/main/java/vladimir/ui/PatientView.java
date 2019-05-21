package vladimir.ui;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

public class PatientView extends VerticalLayout implements View {

    private Navigator navigator;
    private Button button;

    public PatientView(Navigator navigator) {
        this.navigator = navigator;
        button = new Button("Back");
        button.addClickListener(x -> {
            navigator.navigateTo("");
        });
        addComponent(button);
    }

}
