/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model_controller;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.RequestScoped;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author Evgenii
 */
@ManagedBean
@RequestScoped
public class StudentManagedBean {

    private int id;
    private String name;
    private String address;
    private int age;
    
    public StudentManagedBean() {
    }

    public StudentManagedBean(int id, String name, String address, int age) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.age = age;
    }

    

    public int getId() {
        return id;
    }

    public void setId(int studentId) {
        this.id = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String firstName) {
        this.name = firstName;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String lastName) {
        this.address = lastName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int year) {
        this.age = year;
    }
    
    public static Connection conn=null;
    public static PreparedStatement pstmt=null;
    public static ResultSet rs=null;
    public String str="";
    
    public static Connection getConnection() throws InstantiationException, IllegalAccessException{
        try {
            Class.forName("com.mysql.jdbc.Driver");
            conn=DriverManager.getConnection("jdbc:mysql://localhost:3306/studentdb?zeroDateTimeBehavior=convertToNull", "root", "");
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(StudentManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        return conn;
    }
    
    public static void closeAll(Connection conn, PreparedStatement pstmt, ResultSet rs){
        if (conn!=null) {
            try {
                conn.close();
            } catch (SQLException e) {
                Logger.getLogger(StudentManagedBean.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        if (pstmt!=null) {
            try {
                pstmt.close();
            } catch (SQLException e) {
                Logger.getLogger(StudentManagedBean.class.getName()).log(Level.SEVERE, null, e);
            }
        }
        if (rs!=null) {
            try {
                conn.close();
            } catch (SQLException e) {
                Logger.getLogger(StudentManagedBean.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
    
//    public static void main(String[] args) throws InstantiationException, IllegalAccessException {
//        //Test Connecction
//        System.out.println(getConnection());
//    }
    
    public ArrayList<StudentManagedBean> getAllStudents(){
        ArrayList<StudentManagedBean> al = new ArrayList<>();
        str = "SELECT `id`, `name`, `address`, `age` FROM `student`";
        try {
            getConnection();
            pstmt = conn.prepareStatement(str);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                StudentManagedBean st = new StudentManagedBean();
                st.setId(rs.getInt("id"));
                st.setName(rs.getString("name"));
                st.setAddress(rs.getString("address"));
                st.setAge(rs.getInt("age"));
                al.add(st);
                
            }
        } catch (InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(StudentManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        finally{
            closeAll(conn, pstmt, rs);
        }
        return al;
    }
    
    
    public void add(){
        try {
            getConnection();
            str = "INSERT INTO `student`(`name`, `address`, `age`) VALUES (?, ?, ?)";
            pstmt = conn.prepareStatement(str);
            pstmt.setString(1, this.getName());
            pstmt.setString(2, this.getAddress());
            pstmt.setInt(3, this.getAge());
            int executeUpdate = pstmt.executeUpdate();
            if(executeUpdate > 0){
                System.out.println("Update Succes");
            }
        } catch (InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(StudentManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            closeAll(conn, pstmt, rs);
        }
    }
    
    public void edit(){
        ArrayList<StudentManagedBean> alist = getAllStudents();
        FacesContext fc = FacesContext.getCurrentInstance();    
        int studentId;
        
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        studentId = Integer.parseInt(request.getParameter("id"));
        
        for(StudentManagedBean studentManagedBean:alist){
            if (studentManagedBean.getId()==studentId) {
                this.setId(studentManagedBean.getId());
                this.setName(studentManagedBean.getName());
                this.setAddress(studentManagedBean.getAddress());
                this.setAge(studentManagedBean.getAge());
            }
        }
    }
    
    public void update(){
        
        try {
            getConnection();
            str = "UPDATE `student` SET `name`=?,`address`=?,`age`=? WHERE `id`=?";
            FacesContext fc = FacesContext.getCurrentInstance();               
            HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
            int studentId = Integer.parseInt(request.getParameter("id"));
            
            pstmt = conn.prepareStatement(str);
            pstmt.setString(1, this.getName());
            pstmt.setString(2, this.getAddress());
            pstmt.setInt(3, this.getAge());
            pstmt.setInt(4, studentId);
            
            int executeUpdate = pstmt.executeUpdate();

            if(executeUpdate > 0){
                System.out.println("Edit Succes");
                
            }
        } catch (InstantiationException | IllegalAccessException | SQLException ex) {
            Logger.getLogger(StudentManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            closeAll(conn, pstmt, rs);
        }
    }
    
    public void delete(){
        try {
            getConnection();
        } catch (InstantiationException | IllegalAccessException ex) {
            Logger.getLogger(StudentManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        }
        str = "DELETE FROM `student` WHERE id=?";
        FacesContext fc = FacesContext.getCurrentInstance();
        HttpServletRequest request = (HttpServletRequest) fc.getExternalContext().getRequest();
        int  idStudent = Integer.parseInt(request.getParameter("id"));
        try {
            pstmt = conn.prepareStatement(str);
            pstmt.setInt(1, idStudent);
            int executeUpdate = pstmt.executeUpdate();
            if (executeUpdate > 0) {
                System.out.println("Delete Success");
            }
        } catch (SQLException ex) {
            Logger.getLogger(StudentManagedBean.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            closeAll(conn, pstmt, rs);
        }

    }
}
