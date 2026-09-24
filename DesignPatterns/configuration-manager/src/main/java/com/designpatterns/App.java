package com.designpatterns;


public class App 
{
    public static void main( String[] args )
    {
        ConfigurationManager manager = ConfigurationManager.getInstance();

        System.out.println("Application Name: " + manager.get("app.name"));

        System.out.println("Environment: " + manager.get("app.environment"));

        System.out.println("version: " + manager.get("app.version"));
    }
}
