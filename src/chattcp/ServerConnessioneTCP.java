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
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 *
 * @author Armando Palermo
 * Classe che simula il funzionamento di un server che è in grado di rispondere
 * a messaggi del client con un pattern di risposte predefinite e con risposta dinamica
 * in caso si riceva un messaggio che non corrisponde a nessuno presente nello switch.
 */
public class ServerConnessioneTCP extends Thread {
       /**
        * Socket utilizzato per la connessione
        */
        private Socket connection;
        
        /**
         * Socket utilizzato dal server per accettare connessioni dal client
         * es. connection=connessioneServer.accept()
         */
        private ServerSocket connessioneServer;
        
        /**
         * Variabile utilizzata per permettere du avere una colorazione diversa per i messaggi presi in input 
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
         * vengono inizializzati a null i due socket
         */
        ServerConnessioneTCP(){
           connessioneServer=null;
           connection=null;
        }
        
        /**
         * Metodo run utilizzato per eseguire i metodi della classe ,utili 
         * per mettersi in ascolto su una porta, per scambiare messaggi 
         * e chiudere la connessione
         */
        @Override
        public void run(){
            inAscolto(2000);
            rispondi();
            chiudiConnessione();
        }
        
        /**
         * Viene istanziato il socket del server(ServerSocket) e si mette in ascolto su una determinata porta.
         * Attraverso la primitiva  accept()  vengono accettate le eventuali connessioni che si verificheranno.
         * La primitiva accept ritorna  un oggetto di tipo Socket con il quale andremo ad istanziare 
         * l'attributo connection di questa classe.
         * @param porta   Il server si mette in ascolto sulla questa porta
         */
        public void inAscolto(int porta){
            
            try{
                // il server si mette in ascolto sulla porta voluta
                connessioneServer = new ServerSocket(porta);
                System.out.println("In attesa di connessioni!");
                //si è stabilita la connessione
                this.connection = connessioneServer.accept();
                System.out.println("Connessione stabilita!");
                System.out.println("Socket server: " + connection.getLocalSocketAddress());
                System.out.println("Socket client: " + connection.getRemoteSocketAddress());
                
            }
               catch(IOException e){
                   System.err.println("Errore di I/O!");
            }
			
        }
        
        /**
         * Metodo che permette di effettuare lo scambio di messaggi tra gli 
         * host connessi
         * La struttura principale di questo metodo è uno switch che determina la risposta da inviare al client
         * a seconda del messaggio recapitato dal client
         */
        public void rispondi(){
            //eventuale parametro passato con il comando autore(autore:parametro ====> autore:armando)
            //utilizzato per spezzare in due il comando in modo da poter effettuare le corrette elaborazione
            String parametro ="";
            //comando utilizzato per contenere "autore" in modo da poter effettuare il controllo sullo switch
            String comando="";
            int lunghezzaArray;
            String[] mex;
            boolean a = true;
            String messaggioInput="" , messaggioOutput = "";
            
            try{
                //Istanza degli stream utili:
                //inputServerTastiera===>permette di prendere delle stringhe inserite da tastiera 
                //inputServer===>permette di ricevere i messaggi inviati dall'oggetto client
                //outputServer
                BufferedReader inputServerTastiera= new BufferedReader(new InputStreamReader(System.in));
                BufferedReader inputServer= new BufferedReader(new InputStreamReader(this.connection.getInputStream()));//prende in input il messaggio inviato dal client(non avviene più la lettura da tastiera)
                PrintStream outputServer= new PrintStream(this.connection.getOutputStream());
                
                while(a){
                    messaggioInput=inputServer.readLine(); //lettura messaggio inviato dal client
                    mex = messaggioInput.split(":"); 
                    lunghezzaArray = mex.length;
                    if(lunghezzaArray==2){ //se ci sono 2 elementi allora viene diviso in due per riconoscere il comando autore dal parametro passato(es.armando)
                        comando = mex[0];
                        parametro = mex[1];
                    }else{
                        comando = messaggioInput;
                    }
                    //stampo il nome dell'autore vicino al messaggio che invia se la variabile parametro è vuota
		    if(parametro.equals("")){
			System.out.println(VERDE+messaggioInput+Reset);
		    }else{
			System.out.println(VERDE+parametro+":"+messaggioInput+Reset);
		    }
                   
                    //switch di controllo per stabilire la risposta del server
                    switch(comando){
                            case "end":
                                messaggioOutput="Arrivederci";
                                a=false;
				System.out.println(messaggioOutput);
                                break;
                            case "ciao":
                                messaggioOutput ="salve";
				System.out.println(messaggioOutput);
                                break;
                            case "come stai?":
                                messaggioOutput ="bene";
				System.out.println(messaggioOutput);
                                break;
                            case "data"://restituzione data 
                                GregorianCalendar data =  new GregorianCalendar();
                                messaggioOutput=data.get(Calendar.DATE)+"/"+(data.get(Calendar.MONTH)+1)+"/" + data.get(Calendar.YEAR)
                                        +"  "+data.get(Calendar.HOUR)+":"+data.get(Calendar.MINUTE)+":"+data.get(Calendar.SECOND); 
				System.out.println(messaggioOutput);
                               break;
                            case "autore":
                                messaggioOutput="Autore registrato";  
				System.out.println(messaggioOutput);
                            break;
                            default://il server potrà inviare un messaggio da tastiera
				messaggioOutput=inputServerTastiera.readLine();
                                if(messaggioOutput.equals("smile")){//se l'utente scriverà la stringa "smile" allora inviarà al client uno smile
                                    messaggioOutput="\u263a";
                                }
                                if(messaggioOutput.equals("like")){//se l'utente scriverà la stringa "like" allora inviarà al client un like
                                    messaggioOutput="\uD83D\uDC4D";
                                }
                                break;
                        }
                       
                        
                        outputServer.println(messaggioOutput);
                        outputServer.flush();//svuoto lo stream e invio il messaggio
                }
            }catch(IOException e){
                   System.err.println("Errore di I/O!");
            }
		
        }
      
        
        /**
         * Ferma il Server Socket e quindi non sarà possibile  ricevere di connessione richieste 
         * se non riavviando il thread server
         */
        void chiudiConnessione(){
            try {
                if (this.connessioneServer!=null) this.connessioneServer.close();
            } catch (IOException ex) {
                System.err.println("Errore nella chiusura della connessione!");
            }
            System.out.println("Connessione chiusa!");
        
        }
}
