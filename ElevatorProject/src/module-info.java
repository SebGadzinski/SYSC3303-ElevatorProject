module ElevatorProject {
    exports project.tests to org.junit.platform.commons;
    exports project.tests.integration to org.junit.platform.commons;
    exports project.tests.unit to org.junit.platform.commons;
    requires java.base;
    requires org.junit.jupiter.api;
    requires org.junit.platform.launcher;
}