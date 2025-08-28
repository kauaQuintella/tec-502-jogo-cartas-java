package org.project;

import org.project.server.TCPServerMain;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {
    public static void main(String[] args) {

        try {
            new TCPServerMain(); // Start the server

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}