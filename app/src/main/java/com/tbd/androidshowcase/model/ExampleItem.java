package com.tbd.androidshowcase.model;

/**
 * Created by Trevor on 9/5/2016.
 */
public class ExampleItem
{
    private String name;
    private String email;

    public ExampleItem(String name, String email)
    {
        this.name = name;
        this.email = email;
    }

    public String getName() { return this.name; }
    public String getEmail() { return this.email; }

    @Override
    public String toString() { return name; }
}
