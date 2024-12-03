package model;

public class CreateUserModel {

    private String email;
    private String password;
    private String name;


    public CreateUserModel(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }
    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

}
