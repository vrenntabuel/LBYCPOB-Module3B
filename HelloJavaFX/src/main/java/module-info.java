module ph.edu.dlsu.lbycpob.hellojavafx {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;

    opens ph.edu.dlsu.lbycpob.hellojavafx to javafx.fxml;
    exports ph.edu.dlsu.lbycpob.hellojavafx;
}