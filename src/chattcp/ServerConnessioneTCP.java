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
 */
public class ServerConnessioneTCP extends Thread {
        private Socket connection;
        private ServerSocket connessioneServer;
		
        ServerConnessioneTCP(){
           connessioneServer=null;
           connection=null;
        }
        
        
        @Override
        public void run(){
            inAscolto(2000);
            rispondi();
            chiudiConnessione();
        }
        
        //metodo che fa si che il server passi in modalità "Ascolto" sulla porta inserita come parametro del metodo
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
        
        //risposta server da inoltrare al client
        public void rispondi(){
	    boolean a = true;
            String messaggioInput="" , messaggioOutput = "";
            try{
                BufferedReader inputServer= new BufferedReader(new InputStreamReader(this.connection.getInputStream()));//prende in input il messaggio inviato dal client(non avviene più la lettura da tastiera)
                PrintStream outputServer= new PrintStream(this.connection.getOutputStream());
                while(a){
                    messaggioInput=inputServer.readLine();
                    switch(messaggioInput){//controllo del messaggio di input con la quale si definisce la risposta da definire
                            case "end":
                                messaggioOutput="Arrivederci";
							a=false;
                                break;
                            case "ciao":
                                messaggioOutput ="salve";
                                break;
                            case "come stai?":
                                messaggioOutput ="bene";
                                break;
                            case "data"://restituzione data 
                                GregorianCalendar data =  new GregorianCalendar();
                                messaggioOutput=data.get(Calendar.DATE)+"/"+(data.get(Calendar.MONTH)+1)+"/" + data.get(Calendar.YEAR)
                                        +"  "+data.get(Calendar.HOUR)+":"+data.get(Calendar.MINUTE)+":"+data.get(Calendar.SECOND); //MONTH+1 in modo da comunicare correttamente i mesi
                                break;
							case "smile":
								messaggioOutput="\u263a";
								break;
							case "like":
								messaggioOutput="\uD83D\uDC4D";
								break;
                            default:
                                messaggioOutput="Non sono in grado di rispondere";
                                break;
                        }
                        outputServer.println(messaggioOutput);
                        outputServer.flush();//svuoto lo stream e invio il messaggio
                        System.out.println(messaggioOutput);
                }
            }catch(IOException e){
                   System.err.println("Errore di I/O!");
            }
		
        }
        
        //chiusura della connessione in seguito all'invio del messaggio "chiudi"
        void chiudiConnessione(){
            try {
                if (this.connessioneServer!=null) this.connessioneServer.close();
            } catch (IOException ex) {
                System.err.println("Errore nella chiusura della connessione!");
            }
            System.out.println("Connessione chiusa!");
        
        }
}
