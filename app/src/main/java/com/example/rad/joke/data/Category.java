package com.example.rad.joke.data;

public class Category {

    private String name;
    private String code;

    public Category(String name) {
        this.name=this.upperCase(name);
        this.code="";
    }

    public Category(String name, String code) {
        this.name=this.upperCase(name);
        this.code=this.remove(code);
    }

    public Category() {
        this.name="";
    }

    public String getName() {
        return name;
    }

    public String geCode() {
        return code;
    }

    private String upperCase(String name){
        return this.remove(name).toUpperCase();
    }

    private String remove(String name){
        return name.replace('"',' ').replace('[',' ').replace(']',' ');
    }

}
