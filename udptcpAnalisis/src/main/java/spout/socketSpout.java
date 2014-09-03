/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asabater
 */
public class socketSpout extends BaseRichSpout{
    SpoutOutputCollector collector;
    private TopologyContext context;
    private ServerSocket serverSocket;

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("log"));
    }

    public void open(Map map, TopologyContext tc, SpoutOutputCollector soc) {
        this.context = tc;
        this.collector = soc;
        try {
            this.serverSocket = new ServerSocket(6068);
            this.serverSocket.setSoTimeout(3000000);
        } catch (IOException ex) {
            Logger.getLogger(socketSpout.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    public void nextTuple() {
        boolean condicion = true;
        while(condicion)
        {
           try
           {
              System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "...");
              Socket client = serverSocket.accept();
              System.out.println("Just connected to "+ client.getRemoteSocketAddress());
              BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
              String inputLine;
              while ((inputLine = in.readLine()) != null) {
                  this.collector.emit(new Values(inputLine));
              }           
              client.close();
           }catch(SocketTimeoutException s)
           {
              System.out.println("Socket timed out!");
              break;
           }catch(IOException e)
           {
              e.printStackTrace();
              break;
           }
        }
    }
    
}
