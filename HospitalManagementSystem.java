package com.hospitalManagementSystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;



public class HospitalManagementSystem {

	private static final String url ="jdbc:oracle:thin:@localhost:1521:orcl";
	
	private static final String username ="hr";
	
	private static final String password ="hr";
	
	//check avaialability for doctor
	public static boolean checkDoctorAvailability(int doctorId , String appointmentDate, Connection connection) {
		  String query= "SELECT COUNT(*) FROM appointments WHERE doctor_id = ? AND appointment_date=?";
		  try {
			PreparedStatement preparedStatement = connection.prepareStatement(query);
			preparedStatement.setInt(1, doctorId);
			preparedStatement.setString(2, appointmentDate);
			ResultSet resultSet= preparedStatement.executeQuery();
			if (resultSet.next()) {
				int count= resultSet.getInt(1);
				if (count==0) {
					return true;
				}
				else {
					return false;
				}
			}
			
		} catch (SQLException e) {
				e.printStackTrace();
		}
		  return false;
	}
	
	//Arguments for taking input and connection with db
	public static void bookAppointment(Patient patient,Doctor doctor, Connection connection,Scanner scanner) {
		 System.out.println("Enter Patient Id: ");
		 int patientId=scanner.nextInt();
		 System.out.println("Enter Doctor Id: ");
		 int doctorId= scanner.nextInt();
		 System.out.println("Enter appointment date (YYYY-MM-DD)");
		 String appointmentDate= scanner.next();
		 if (patient.getPatientById(patientId)&& doctor.getPatientById(doctorId)) {
			//check doctor's availability
			 if (checkDoctorAvailability(doctorId, appointmentDate, connection)) {
				String appointQuery = "INSERT INTO appointnments(patient_id, doctor_id, appointment_date) VALUES(?,?,?)"; 
				try {
					PreparedStatement preparedStatement = connection.prepareStatement(appointQuery);
					preparedStatement.setInt(1,patientId);
					preparedStatement.setInt(2,doctorId);
					preparedStatement.setString(3, appointmentDate);
					int rowsAffected = preparedStatement.executeUpdate();
					if (rowsAffected>0) {
						System.out.println("Appointment Booked!!");
					} else {
						System.out.println("Failed to Book Appointnment!!");
					}
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		} else {
			System.out.println("Either patient or doctor does not exist!!");
		}

	}
	
	public static void main(String[] args) {
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Scanner scanner=new Scanner(System.in);
		try {
			// for establishing database connection
			Connection connection=DriverManager.getConnection(url,username,password);
			Patient patient = new Patient(connection, scanner);
			Doctor doctor= new Doctor(connection);
			
			while (true) {
				System.out.println(" \t\t\tHOSPITAL MANAGEMENT SYSTEM ");
				System.out.println();
				System.out.println("1. Add Patient");
				System.out.println("2. View Patient");
				System.out.println("3. View Doctors");
				System.out.println("4. Book Appointment");
				System.out.println("5. Exit");
				System.out.println();
				System.out.println("Enter your choice: ");
				int choice=scanner.nextInt();
				
				switch (choice) {
				case 1:
					//Add Patient
					patient.addPatient();
					System.out.println();
					break;
				case 2:
					//View Patient
					patient.viewPatients();
					System.out.println();
					break;
				case 3:
					//View Doctors
					doctor.viewDoctors();
					System.out.println();
					break;
				case 4:
					//Book Appointment
					bookAppointment(patient, doctor, connection, scanner);
					System.out.println();
					break;
					
				case 5:
					//exit
					return;
				default:
					System.out.println("Enter valid choice");
					break;
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
