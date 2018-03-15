/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chattcp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.ConnectException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Armando Palermo
 */
public class ClientConnessioneTCP extends Thread {
        Socket connection;
        private final String VERDE="\u001B[32m";
        private final String Reset="\u001B[0m";
        
       ClientConnessioneTCP(){
           connection=null;
       }
       
       
        @Override
        public void run(){
            avviaConnessione("localhost",2000);
            scriviMessaggio();
            chiudiConnessione();
        }
       
       //metodo che avvia la connessione con il server
       public void avviaConnessione(String indirizzoServer, int porta){
           
            
            try{

                this.connection = new Socket(indirizzoServer, porta);
                System.out.println("Connessione aperta");
               
            }
            catch(ConnectException e){//gestione dele eccezioni
                System.err.println("Server non disponibile!");
            }
            catch(UnknownHostException e1){
                System.err.println("Errore DNS!");
            }
            catch(IOException e2){
                 System.err.println("Errore I/O");
             }
			 
       }
       
       
       //scrittura messaggio da inoltrare al server
        public void scriviMessaggio(){
            boolean a=true;
            boolean statoHost=true;//true=online e false=offline
            String autore = "";
            String messaggio="";
            String comando="";
            String messaggioSalvato="";
            String rispostaServer="";
            try {
                BufferedReader inputClient= new BufferedReader(new InputStreamReader(System.in));//Input da tastiera
                BufferedReader inputClientRispServer= new BufferedReader(new InputStreamReader(this.connection.getInputStream()));//Stream per gestione della risposta del server
                PrintStream outputClient= new PrintStream(this.connection.getOutputStream());
                
                while(a){
                    if(autore!=""){
                         System.out.print(autore+":");
                    }
                    messaggio=inputClient.readLine();//primo input da tastiera del client
                    
                    
                    //controllo per insermento  comandi(echo, autore)
                    String[] mex = messaggio.split(":");
                    int lunghezzaArray = mex.length;
                    if(lunghezzaArray==2){
                        comando = mex[0];
                        autore = mex[1];
                    }else{
                        comando = messaggio;
                    }
                    
                    if(statoHost){
                        switch(messaggio){
                            case "echo":
                                 outputClient.println(messaggioSalvato);//invio del messaggio
                                 outputClient.flush();
                                break;
                            case "offline":
                                statoHost=false;
                                break;
                            case "smile":
                                
                                outputClient.println("\u263a");
                                break;
                            case "like":
                                outputClient.println("\uD83D\uDC4D");
                                break;
                            default:
                                messaggioSalvato=rispostaServer;
                                outputClient.println(messaggio);//invio del messaggio
                                outputClient.flush(); 
                                break;
                        }
                        if(!messaggio.equals("offline") && !messaggio.equals("online")){
                            rispostaServer=inputClientRispServer.readLine();//lettura della risposta inviata dal server
                            System.out.println(VERDE+rispostaServer+Reset);//stampo la risposta del server
                            if("end".equals(messaggio)){//chiusura della connessione in casso si invia "end"
                                    a=false;
                            }
                            messaggioSalvato=rispostaServer;
                        }
                    }else{
                        if(messaggio.equals("online")){
                            System.out.println("Ora sei online");
                            statoHost=true;
                        }else{
                            System.out.println("SEI OFFLINE, il messaggio non è stato inoltrato");
                        }
                    }
                        
                }
            } catch (IOException ex) {
                Logger.getLogger(ClientConnessioneTCP.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        //chiusura della connessione in seguito all'invio del messaggio "chiudi"
        void chiudiConnessione(){
                try {
                    if (this.connection!=null)
                        {
                            this.connection.close();
                            System.out.println("Connessione chiusa!");
                        }
                    }
                catch(IOException e){
                    System.err.println("Errore nella chiusura della connessione!");
                }
        }
}
