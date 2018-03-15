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
       /**
        * Socket utilizzato per stabilire una connessione
        */
        Socket connection;
        
       /**
        * Variabile utilizzata per permettere di avere una colorazione diversa per i messaggi presi in input 
        * dal client
        */
        private final String VERDE="\u001B[32m";
        
       /**
        * Variabile utilizzata per ritornare alla colorazione base dello standard output 
        * per i messaggi inviati dal server
        */
        private final String Reset="\u001B[0m";
        
       /**
        * COSTRUTTORE
        * viene inizializzato a 1 il socket connection
        */
       ClientConnessioneTCP(){
           connection=null;
       }
       
       /**
        * Metodo run utilizzato per eseguire i metodi della classe, utili 
        * per instaurare una connessione con un server, scambiare messaggi
        * e chiudere la connessione
        */
        @Override
        public void run(){
            avviaConnessione("localhost",2000);
            scriviMessaggio();
            chiudiConnessione();
        }
       
       /**
        * Metodo che permette di stabilire una connesione con il server
        * NB:eseguire prima il server e poi il client
        * @param indirizzoServer Indirizzo del server con cui vogliamo connetterci
        * @param porta  porta sulla quale verrà effettuata la richiesta di connessione
        */
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
                    
                    
                    //controllo per insermento del comando autore
                    String[] mex = messaggio.split(":");
                    int lunghezzaArray = mex.length;
                    if(lunghezzaArray==2){
                        comando = mex[0];
                        autore = mex[1];
                    }else{
                        comando = messaggio;
                    }
                    //Controllo lo stato dell'host(se offline non invio nessun messaggio al server)
                    //se scrivo echo allora reinvio al client il messaggio precedentemene ricevuto dal server
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
                            rispostaServer=inputClientRispServer.readLine();
                            System.out.println(VERDE+rispostaServer+Reset);
                            if("end".equals(messaggio)){
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
        
        /**
         * Chiusura del connection socket in modo da concludere la connessione del client
         */
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
