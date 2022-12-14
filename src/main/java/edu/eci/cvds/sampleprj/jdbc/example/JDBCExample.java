/*
 * Copyright (C) 2015 hcadavid
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.eci.cvds.sampleprj.jdbc.example;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author hcadavid
 */
public class JDBCExample {
    
	//private static PreparedStatement prepareSt = null; 
	
    public static void main(String args[]){
        try {
            String url="jdbc:mysql://desarrollo.is.escuelaing.edu.co:3306/bdprueba";
            String driver="com.mysql.jdbc.Driver";
            String user="bdprueba";
            String pwd="prueba2019";
                        
            Class.forName(driver);
            Connection con=DriverManager.getConnection(url,user,pwd);
            con.setAutoCommit(false);
                 
            
            System.out.println("Valor total pedido 1:"+valorTotalPedido(con, 1));
            
            List<String> prodsPedido=nombresProductosPedido(con, 1);
            
            
            System.out.println("Productos del pedido 1:");
            System.out.println("-----------------------");
            for (String nomprod:prodsPedido){
                System.out.println(nomprod);
            }
            System.out.println("-----------------------");
            
            
            int suCodigoECI=20134429;
            //registrarNuevoProducto(con, suCodigoECI, "SU NOMBRE", 99999999);            
            //con.commit();
                        
            
            con.close();
                                   
        } catch (ClassNotFoundException | SQLException ex) {
            Logger.getLogger(JDBCExample.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.exit(0);
        
    }
    
    /**
     * Agregar un nuevo producto con los par??metros dados
     * @param con la conexi??n JDBC
     * @param codigo
     * @param nombre
     * @param precio
     * @throws SQLException 
     */
    public static void registrarNuevoProducto(Connection con, int codigo, String nombre,int precio) throws SQLException{
    	PreparedStatement prepareSt = null;
    	String query = "insert into ORD_PRODUCTOS values(?,?,?)";
    	try {
    		//Crear preparedStatement
    		prepareSt = con.prepareStatement(query);
            //Asignar par??metros
    		prepareSt.setInt(1, codigo);
    		prepareSt.setString(2, nombre);
    		prepareSt.setInt(3, precio);
            //usar 'execute'
    		prepareSt.executeUpdate();
    		prepareSt.close();
    		con.commit();
        }catch(SQLException e) {
        	e.printStackTrace();
        }
        
    }
    
    /**
     * Consultar los nombres de los productos asociados a un pedido
     * @param con la conexi??n JDBC
     * @param codigoPedido el c??digo del pedido
     * @return 
     */
    public static List<String> nombresProductosPedido(Connection con, int codigoPedido){
    	PreparedStatement prepareSt = null;
        List<String> np=new LinkedList<>();
        String query = "Select nombre "+ "From ORD_PRODUCTOS INNER JOIN ORD_DETALLE_PEDIDO detalle ON (codigo = producto_fk)"+
        "WHERE pedido_fk = ?";
        try {
        	//Crear prepared statement
        	prepareSt = con.prepareStatement(query);
            //asignar par??metros
        	prepareSt.setInt(1, codigoPedido);
            //usar executeQuery
        	ResultSet resultado = prepareSt.executeQuery();
            //Sacar resultados del ResultSet
        	while(resultado.next()) {
        		//Llenar la lista y retornarla
        		np.add(resultado.getString("nombre"));
        	}
        	prepareSt.close();
        }catch(SQLException e) {
        	e.printStackTrace();
        }
        return np;
    }

    
    /**
     * Calcular el costo total de un pedido
     * @param con
     * @param codigoPedido c??digo del pedido cuyo total se calcular??
     * @return el costo total del pedido (suma de: cantidades*precios)
     */
    public static int valorTotalPedido(Connection con, int codigoPedido){
    	PreparedStatement prepareSt = null;
    	int total = 0;
    	String query = "Select SUM(cantidad * precio) from ORD_DETALLE_PEDIDO inner join ORD_PRODUCTOS ON (codigo=producto_fk) WHERE pedido_fk = ?";
        try {
            //Crear prepared statement
        	prepareSt = con.prepareStatement(query);
            //asignar par??metros
        	prepareSt.setInt(1, codigoPedido);
            //usar executeQuery
        	ResultSet resultado = prepareSt.executeQuery();
        	
            //Sacar resultado del ResultSet
        	while(resultado.next()) {
        		
        		total = resultado.getInt(1);
        	}
        	prepareSt.close();
        }catch(SQLException e) {
        	e.printStackTrace();
        }
        return total;
    }
    

    
    
    
}