package exa15;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;


public class Exa15 {
    public static Connection conexion=null;

    public static Connection getConexion() throws SQLException  {
        String usuario = "hr";
        String password = "hr";
        String host = "localhost"; 
        String puerto = "1521";
        String sid = "orcl";
        String ulrjdbc = "jdbc:oracle:thin:" + usuario + "/" + password + "@" + host + ":" + puerto + ":" + sid;
        
           
            conexion = DriverManager.getConnection(ulrjdbc);
            return conexion;
        }

     
     public static void closeConexion() throws SQLException {
      conexion.close();
      }
    public static void main(String[] args) throws FileNotFoundException, IOException, SQLException, ClassNotFoundException, XMLStreamException {
       //codigo aqui
        //System.out.println("Working Directory: " + System.getProperty("user.dir"));
        ArrayList<Platos> ps=new ArrayList();
        ConsultaArchivo(ps);
        
        
    }
    
    public static ArrayList<Integer> ConsultaDB(ArrayList<Platos> ps){
        ArrayList<String> Codc = new ArrayList();
        Statement st1, st2;
        ResultSet res1,res2;
        String query1 = "select codc from composicion where codp =";
        String query2 = "select graxas from componentes where codc =";
        try{
            conexion = getConexion();
            st1=conexion.createStatement();
            st2=conexion.createStatement();
            for(int i=0 ; i<ps.size();i++){
                res1=st1.executeQuery(query1 + ps.get(i).getCodigop());
                while(res1.next()){
                    Codc.add(res1.getString("codc"));
                }
                for(int j=0;i<Codc.size();i++){
                    res2=st2.executeQuery(query2 + Codc.get(j));
                }
            }
            
        }catch(SQLException e){
            System.out.println("No se puede conectar a la base de datos");
        }
    }
    
    public static void ConsultaArchivo(ArrayList<Platos> ps){
        ObjectInputStream in;
        Platos aux;
        try{
            in= new ObjectInputStream(new FileInputStream("platoss"));
            while((aux=(Platos)in.readObject()) != null){
                ps.add(aux);
            }
            //System.out.println("Numero de platos: " + ps.size());
            in.close();
        }catch(IOException e){
            System.out.println("Error al abrir el archivo");
        }catch(ClassNotFoundException e2){
            System.out.println("Objeto no encontrado");
        }
    }
}

   