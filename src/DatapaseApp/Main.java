package com.company;

import java.io.IOException;
import java.sql.*;
import java.util.Random;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws IOException {
        int choose;
        DbApp db = new DbApp();

        do {
            Scanner in= new Scanner(System.in);
            //____________________________Read From User____________________________
            System.out.println("\nWelcome to our DatabaseApp, what would you want to do: ");
            System.out.println("1) Connect to Database (Obligatory)");
            System.out.println("2) Insert a diploma to the Database");
            System.out.println("3) Present a student's grade for a specific Course");
            System.out.println("4) Update the final grade of a Course for a specific student");
            System.out.println("5) Search for existing members of the university using surname's starting letters");
            System.out.println("6) Present a detailed report of the academic course of a specific Student");        
            System.out.println("(Press Everything Else for Exit)");
            choose= in.nextInt();

            switch (choose){
                case 1:
                    db.dbConnect("localhost", 5432, "postgres", "postgres", "password");
                    break;
                case 2:
                    db.insertDiploma();
                    db.waitForEnter();
                    break;
                case 3:
                    db.showGrades();
                    db.waitForEnter();
                    break;
                case 4:
                    db.updateGrade();
                    db.waitForEnter();
                    db.db_commit();
                    break;
                case 5:
                    db.searchMember();
                    db.waitForEnter();
                    break;
                case 6:
                    db.viewAllGrades();
                    db.waitForEnter();
                    break;
                default:
                    System.out.println("Goodbye...");
                    break;
            }
        }while(choose>0&&choose<6);

    }
}
