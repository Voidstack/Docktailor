module com.enosi.docktailor {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.slf4j;
    requires static lombok;
    requires java.desktop;
    requires javafx.web;
    requires javax.inject;
    requires org.reflections;

    opens com.enosi.docktailor to javafx.fxml;
    exports com.enosi.docktailor;
}