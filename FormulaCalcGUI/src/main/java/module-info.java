module ph.edu.dlsu.lbycpei.formulacalcgui {
    requires javafx.controls;
    requires javafx.fxml;


    opens ph.edu.dlsu.lbycpob.formulamenu to javafx.fxml;
    exports ph.edu.dlsu.lbycpob.formulamenu;
}