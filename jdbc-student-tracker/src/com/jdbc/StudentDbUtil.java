package com.jdbc;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class StudentDbUtil {

	private static StudentDbUtil instance;
	private DataSource dataSource;
	private String jndiName = "java:comp/env/jdbc/student-tracker";

	public static StudentDbUtil getInstance() throws Exception {
		if (instance == null) {
			instance = new StudentDbUtil();
		}

		return instance;
	}

	private StudentDbUtil() throws Exception {
		dataSource = getDataSource();
	}

	private DataSource getDataSource() throws NamingException {
		Context context = new InitialContext();
		DataSource theDataSource = (DataSource) context.lookup(jndiName);
		return theDataSource;
	}

	public List<Student> getStudents() throws Exception {
		List<Student> students = new ArrayList<>();

		Connection myConn = null;
		Statement myStmt = null;
		ResultSet myRs = null;

		try {

			myConn = getConnection();
			String sql = "Select * from student order by last_name";
			myStmt = myConn.createStatement();
			myRs = myStmt.executeQuery(sql);

			while (myRs.next()) {
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");

				String lastName = myRs.getString("last_name");

				String email = myRs.getString("email");

				Student tempStudent = new Student(id, firstName, lastName, email);
				students.add(tempStudent);
			}

			return students;
		} finally {
			// close(myConn, myStmt, myRs);
			myConn.close();
			myStmt.close();
			myRs.close();
		}
	}

	public void addStudent(Student theStudent) throws Exception {

		Connection myConn = null;
		PreparedStatement myStmt = null;

		try {
			myConn = getConnection();

			String sql = "insert into student(id,first_name,last_name,email) values (?,?,?,?)";
			myStmt = myConn.prepareStatement(sql);
			myStmt.setInt(1,theStudent.getId());
			myStmt.setString(2, theStudent.getFirstName());
			myStmt.setString(3, theStudent.getLastName());
			myStmt.setString(4, theStudent.getEmail());

			myStmt.execute();
		} finally {
			// close(myConn, myStmt);
			myConn.close();
		}

	}

	public void updateStudent(Student theStudent) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		try {
			myConn = getConnection();
			String sql = "update student set first_name=?, last_name=?, email=? where id=?";
			myStmt = myConn.prepareStatement(sql);
			myStmt.setString(1, theStudent.getFirstName());
			myStmt.setString(2, theStudent.getLastName());
			myStmt.setString(3, theStudent.getEmail());
			myStmt.setInt(4, theStudent.getId());
			myStmt.execute();
		} finally {
			// close(myConn, myStmt);
			myConn.close();
		}
	}

	public void deleteStudent(int studentId) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		try {
			myConn = getConnection();
			String sql = "delete from student where id=?";
			myStmt = myConn.prepareStatement(sql);
			myStmt.setInt(1, studentId);
			myStmt.execute();

		} finally {
			// close(myConn,myStmt);
			myConn.close();
		}
	}

	public Student getStudent(int studentId) throws Exception {
		Connection myConn = null;
		PreparedStatement myStmt = null;
		ResultSet myRs = null;

		try {
			myConn = getConnection();
			String sql = "select * from student where id=?";
			myStmt = myConn.prepareStatement(sql);
			myStmt.setInt(1, studentId);
			myRs = myStmt.executeQuery();
			Student theStudent = null;
			if (myRs.next()) {
				int id = myRs.getInt("id");
				String firstName = myRs.getString("first_name");
				String lastName = myRs.getString("last_name");
				String email = myRs.getString("email");

				theStudent = new Student(id, firstName, lastName, email);
			} else {
				throw new Exception("Could not find student id: " + studentId);
			}
			return theStudent;
		} finally {
			// close(myConn, myStmt, myRs);
			myConn.close();
		}
	}

	private Connection getConnection() {

		Connection myConn = null;
		Driver myDriver = new com.microsoft.sqlserver.jdbc.SQLServerDriver();
		try {

			DriverManager.registerDriver(myDriver);
			String url = "jdbc:sqlserver://localhost:1433;database=student-tracker;user=sa;password=Newuser123";
			myConn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return myConn;
	}
}
