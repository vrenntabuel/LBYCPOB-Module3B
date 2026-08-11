module ph.edu.dlsu.lbycpob.hellojavafx {
    requires javafx.controls;
    requires javafx.fxml;


    opens ph.edu.dlsu.lbycpob.hellojavafx to javafx.fxml;
    exports ph.edu.dlsu.lbycpob.hellojavafx;
}