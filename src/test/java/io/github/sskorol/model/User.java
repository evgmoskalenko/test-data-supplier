package io.github.sskorol.model;

public class User {

    private final String name;
    private final String password;

    public User(final String name, final String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public String getPassword() {
        return password;
    }
}
