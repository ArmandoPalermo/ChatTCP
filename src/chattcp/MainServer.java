/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package chattcp;

/**
 *
 * @author Armando Palermo
 */
public class MainServer {
    
    public static void main(String[] args) {
               ServerConnessioneTCP server=new ServerConnessioneTCP();
               server.start();
    }
    
}
