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
        String sid = "XE";
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
        ArrayList<Integer> peso = new ArrayList();
        ConsultaArchivo(ps);
        peso=ConsultaDB(ps);
        writeXML(ps,peso);
        
    }
    
    public static ArrayList<Integer> ConsultaDB(ArrayList<Platos> ps){
        ArrayList<String> Codc = new ArrayList();
        ArrayList<Integer> grasas = new ArrayList();
        ArrayList<Integer> pesos = new ArrayList();
        Statement st1, st2;
        ResultSet res1,res2;
        int sum_grasa=0;
        int grasap=0;
        String query1 = "select codc,peso from composicion where codp =";
        String query2 = "select graxa from componentes where codc =";
        try{
            conexion = getConexion();
            st1=conexion.createStatement();
            st2=conexion.createStatement();
            for(int i=0 ; i<ps.size();i++){
                res1=st1.executeQuery(query1 + "'"+ps.get(i).getCodigop()+"'");
                //System.out.println("Consulta en la base de datos:");
                System.out.println("Codigo del Plato: " + ps.get(i).getCodigop());
                System.out.println("Nombre del Plato: " + ps.get(i).getNomep());
                while(res1.next()){
                    Codc.add(res1.getString("codc"));
                    pesos.add(res1.getInt("peso"));
                }
                /*System.out.println("CodC:" + Codc.toString());
                System.out.println("pesos:" + pesos.toString());*/
                for(int j=0;j<Codc.size();j++){
                    System.out.print("Codigo del componente: "+ Codc.get(j));
                    res2=st2.executeQuery(query2 + "'"+Codc.get(j)+"'");
                    while(res2.next()){
                        System.out.println(" -> grasa por cada 100g= " + res2.getInt("graxa"));
                        grasap=res2.getInt("graxa")*pesos.get(j)/100;
                        sum_grasa=sum_grasa+grasap;
                    }
                    //grasas.add(sum_grasa);
                    System.out.println("Peso:" + pesos.get(j));
                    System.out.println("Grasa total del componente: " +grasap + "\n");
                    /*System.out.println("Lista grasas: " + grasas.toString());*/
                    //sum_grasa=0;
                }
                //System.out.println("Grasa total del componente: " + grasap);
                System.out.println("total de grasas del plato: " + sum_grasa + "\n");
                grasas.add(sum_grasa);
                //pesos.add(sum_peso);
                //grasap=0;
                sum_grasa=0;
                Codc.clear();
                pesos.clear();
            }
            //System.out.println("pesos: " + grasas.toString());
            closeConexion();
        }catch(SQLException e){
            System.out.println("No se puede conectar a la base de datos");
        }
        return grasas;
    }
    
    public static void ConsultaArchivo(ArrayList<Platos> ps){
        ObjectInputStream in;
        Platos aux;
        try{
            in= new ObjectInputStream(new FileInputStream("platoss"));
            //System.out.println("Objetos del archivo:");
            while((aux=(Platos)in.readObject()) != null){
                /*System.out.println("codigo: " + aux.getCodigop());
                System.out.println("nombre: " + aux.getNomep());*/
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
    
    public static void writeXML(ArrayList<Platos> ps, ArrayList<Integer> grasa){
        XMLOutputFactory fac = XMLOutputFactory.newInstance();
        try{
            XMLStreamWriter write = fac.createXMLStreamWriter(new FileOutputStream("totalgraxas.xml"));
            write.writeStartDocument("1.0");
            write.writeStartElement("Platos");
            for(int i = 0; i<ps.size();i++){
                write.writeStartElement("Plato");
                write.writeAttribute("codigo", ps.get(i).getCodigop());
                write.writeStartElement("nomep");
                write.writeCharacters(ps.get(i).getNomep());
                write.writeEndElement();
                write.writeStartElement("grasaTotal");
                write.writeCharacters(grasa.get(i).toString());
                write.writeEndElement();
                write.writeEndElement();
            }
            write.writeEndElement();
            write.close();
        }catch(IOException e){
            System.out.println("No se pudo crear el archivo");
        }catch(XMLStreamException e2){
            System.out.println("Error al operar con el XML");
        }
    }
}

   