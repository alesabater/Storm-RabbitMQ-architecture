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
import backtype.storm.tuple.Values;
import java.util.Map;

/**
 *
 * @author asabater
 */
public class dataSplitBolt extends BaseRichBolt{
    private static final long serialVersionUID = 5537827428628598519L;
    private OutputCollector collector;    
    
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields("ipOrigen", "ipDestino", "protocolo"));
    }

    public void prepare(Map map, TopologyContext tc, OutputCollector oc) {
        this.collector = oc;
    }

    public void execute(Tuple tuple) {
        String ipOrigen;
        String ipDestino;
        String protocolo;
        String campos[];
        campos = tuple.getString(0).split(" ");
        if("IP".equals(campos[1])){
            ipOrigen = "+"+campos[2];
            ipDestino = "-"+campos[4];
            
            if("NBT".equals(campos[5]) || "UDP".equals(campos[5] )){
                System.out.println("UDP --> "+tuple.getString(0));
                protocolo = "UDP";
                this.collector.emit(new Values(ipOrigen, ipDestino, protocolo));
            }else if((ipOrigen.indexOf("http") > 0) || (ipDestino.indexOf("http") > 0)){
                protocolo = "TCP";
                this.collector.emit(new Values(ipOrigen, ipDestino, protocolo));
                System.out.println("HTTP --> "+tuple.getString(0));
            }
        }
        this.collector.ack(tuple);
    }
    
}
