/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author asabater
 */
public class rabbitMqBolt extends BaseRichBolt{
    private static final long serialVersionUID = 5537727428628598519L;
    private final static String QUEUE_NAME = "fluming";
    private transient Connection connection;
    private transient Channel channel;
    private OutputCollector collector;
    public int contador;
    
    public rabbitMqBolt() throws IOException{
     
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("word", "cuentaVentana"));
    }

    public void prepare(Map map, TopologyContext tc, OutputCollector oc) {
        this.collector = collector;
        this.contador = 0;
        /*
        try {
            ConnectionFactory factory = new ConnectionFactory();                        
            factory.setUsername("prueba_rabbit");
            factory.setPassword("casa1234");
            factory.setVirtualHost("prueba_rabbit-vhost");
            factory.setHost("172.22.21.196");
            factory.setPort(5672);
            this.connection = factory.newConnection();
            this.channel = connection.createChannel();
        } catch (IOException ex) {
            Logger.getLogger(rabbitMqBolt.class.getName()).log(Level.SEVERE, null, ex);
        }*/
    }

    public void execute(Tuple tuple) {

        
        String EXCHANGE_NAME = "flume_test";
        try {
          ConnectionFactory factory = new ConnectionFactory();
          factory.setHost("localhost");
          factory.setUsername("asabater-dev");
          factory.setPassword("sabas123");
          factory.setVirtualHost("asabater-dev-vhost");
          factory.setPort(5672);
          
          

          connection = factory.newConnection();
          channel = connection.createChannel();
          channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);
          channel.queueDeclare(QUEUE_NAME, true, false, false, null);
          


          String routingKey = "fluming";
          channel.queueBind(QUEUE_NAME, EXCHANGE_NAME, routingKey);
          String message = "[{ \"protocolo\":\""+tuple.getString(0)+"\", \"y\":"+tuple.getString(1)+"}]";
          
          System.out.println(message);
          channel.basicPublish(EXCHANGE_NAME, routingKey, null, message.getBytes());
          //System.out.println(" [x] Sent '": + routingKey + "':'" + message + "'");

        }
        catch  (Exception e) {
          e.printStackTrace();
        }
    }
    
    
}
