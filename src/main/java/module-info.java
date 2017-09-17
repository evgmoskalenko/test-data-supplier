module io.github.sskorol {
    exports io.github.sskorol.core;
    exports io.github.sskorol.model;

    opens io.github.sskorol.utils to joor;

    requires testng;
    requires vavr;
    requires streamex;
    requires joor;
}