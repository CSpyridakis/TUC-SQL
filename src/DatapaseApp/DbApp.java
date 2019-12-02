package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.Scanner;


public class DbApp {
    Connection conn;
    Scanner input = new Scanner(System.in);

    public DbApp() {
        conn = null;
    }

    public void dbConnect(String ip, int port, String database, String username, String password) {
        try {
            // Check if postgres driver is loaded
            Class.forName("org.postgresql.Driver");
            // Establish connection with the database
            conn = DriverManager.getConnection("jdbc:postgresql://" + ip + ":" + port + "/" + database, username, password);
            System.out.println("Connection Established!");
            // Disable autocommit.
            conn.setAutoCommit(false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // For Db changes
    public void db_commit() {                                                                                           
        try {
            // Commit all changes
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void db_abort() {
        try {
            // Rollback all changes
            conn.rollback();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void waitForEnter() {
        Scanner scn = new Scanner(System.in);
        System.out.println("Press Enter..");
        scn.nextLine();
    }

    public void insertDiploma() {
        Scanner input = new Scanner(System.in);
        String sup, prof1, prof2, stud, title;

        try {
            PreparedStatement pst = conn.prepareStatement("SELECT * from create_diploma(?,?,?,?,?)");
            System.out.println("\n Please enter the required data for diploma:");
            System.out.println("\n Supervisor's amka:");
            sup = input.nextLine();
            System.out.println("\n First professor's amka:");
            prof1 = input.nextLine();
            System.out.println("\n Second professor's amka:");
            prof2 = input.nextLine();
            System.out.println("\n Student's amka:");
            stud = input.nextLine();
            System.out.println("\n Diploma's title:");
            title = input.nextLine();

            pst.setString(1, sup);
            pst.setString(2, prof1);
            pst.setString(3, prof2);
            pst.setString(4, stud);
            pst.setString(5, title);

            pst.execute();
            db_commit();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void showGrades() throws IOException {
        BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
        String am, course;                                      //inputs from user
        System.out.println("Please instert data of the student whose information you seek:");
        System.out.println("Student's am:");
        am = buffer.readLine();
        System.out.println("Course code:");
        course = buffer.readLine();

        try {
            PreparedStatement pst = conn.prepareStatement("SELECT final_grade FROM \"Register\" r,\"Students\" s,\"CourseRun\" cr\n WHERE s.amka = r.student_amka AND cr.serial_number = r.course_run_id AND am=? AND implements_course=?");

            pst.setString(1, am);
            pst.setString(2, course);
            pst.execute();
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                System.out.println("Grade is: " + rs.getInt(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }


    public void updateGrade() {
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
            String am, course, grade, lab;
            System.out.println("Please instert data of the student whose information you seek:");
            System.out.println("Student's am:");
            am = buffer.readLine();
            System.out.println("Course to be updated:");
            course = buffer.readLine();
            System.out.println("New grade for:" + course);
            grade = buffer.readLine();
            int gr = Integer.valueOf(grade);
            //System.out.println("New lab grade for:" + course);
            //lab = buffer.readLine();

            //PreparedStatement pst2 = conn.prepareStatement("")

            PreparedStatement pst = conn.prepareStatement("UPDATE \"Register\"\n" +
                    "SET final_grade = ?\n" +
                    "FROM \"Students\",\"CourseRun\"\n" +
                    "WHERE \"Students\".amka=\"Register\".student_amka and \"Students\".am = ? \n" +
                    "      AND \"CourseRun\".serial_number=\"Register\".course_run_id AND implements_course = ? ");

            pst.setInt(1, gr);
            pst.setString(2, am);
            pst.setString(3, course);
            pst.executeUpdate();
            db_commit();
            System.out.println("The updated grade is :" + gr);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void searchMember() {
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
            String initials;
            System.out.println("Please insert the starting letters of the surname of the person you seek:");

            initials = buffer.readLine();
            PreparedStatement pst = conn.prepareStatement("SELECT \"Person\".fname,\"Person\".surname,'Students'\n" +
                    "            FROM \"Person\" JOIN \"Students\" USING (amka)\n" +
                    "                    \n" +
                    "            WHERE left(surname,length( ? ))= ? \n" +
                    "            UNION\n" +
                    "            SELECT \"Person\".fname,\"Person\".surname,'Professors'\n" +
                    "            FROM \"Person\" JOIN \"Professors\" USING (amka)\n" +
                    "            WHERE left(surname,length( ? ))= ? \n" +
                    "            UNION\n" +
                    "            SELECT \"Person\".fname,\"Person\".surname,'Labstaff'\n" +
                    "            FROM \"Person\" JOIN \"Labstaff\" USING (amka)\n" +
                    "            WHERE left(surname,length( ? ))=? ");

            pst.setString(1, initials);
            pst.setString(2, initials);
            pst.setString(3, initials);
            pst.setString(4, initials);
            pst.setString(5, initials);
            pst.setString(6, initials);

            pst.execute();
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                System.out.println("\nPossible university member:" + rs.getString(1) + "|" + rs.getString(2) + "|" + rs.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }

    public void viewAllGrades(){
        try {
            BufferedReader buffer = new BufferedReader(new InputStreamReader(System.in));
            String am;                                      //inputs from user
            System.out.println("Please insert a.m. of the student whose list of grades you seek:");
            System.out.println("Student am:");
            am = buffer.readLine();

            PreparedStatement pst = conn.prepareStatement("SELECT \"CourseRun\".implements_course,\"Register\".final_grade\n" +
                    "FROM \"CourseRun\" JOIN \"Register\" ON \"CourseRun\".serial_number = \"Register\".course_run_id\n" +
                    "JOIN \"Students\" ON \"Register\".student_amka = \"Students\".amka\n" +
                    "WHERE \"Students\".am= ? AND \"Register\".register_status='pass';");

            pst.setString(1, am);
            pst.execute();
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                System.out.println("Course: " + rs.getString(1) + " final grade " + rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        catch (IOException e){
            e.printStackTrace();
        }
    }
}