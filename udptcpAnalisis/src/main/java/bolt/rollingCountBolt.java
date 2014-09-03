/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package bolt;

import backtype.storm.Config;
import backtype.storm.Constants;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import java.util.HashMap;
import java.util.Map;
import util.slidingWindowCounter;

/**
 *
 * @author asabater
 */
public class rollingCountBolt extends BaseRichBolt{
    
    private static final long serialVersionUID = 5537727428628598519L;
    private static final int NUM_WINDOW_CHUNKS = 5; //Cuantos slots conforman la ventana de tiempo
    private static final int DEFAULT_SLIDING_WINDOW_IN_SECONDS = NUM_WINDOW_CHUNKS * 10; //Duracion de la ventana de tiempo
    private static final int DEFAULT_EMIT_FREQUENCY_IN_SECONDS = DEFAULT_SLIDING_WINDOW_IN_SECONDS / NUM_WINDOW_CHUNKS; //Cada cuanto se emite la ventana de tiempo 
    private static final String WINDOW_LENGTH_WARNING_TEMPLATE =
        "Actual window length is %d seconds when it should be %d seconds"
            + " (you can safely ignore this warning during the startup phase)";
    
    private slidingWindowCounter counter;
    private int windowLengthInSeconds;
    private int emitFrequencyInSeconds;
    private OutputCollector collector;
    
    public rollingCountBolt() {
      this.windowLengthInSeconds = 4;
      this.emitFrequencyInSeconds = 4; 
      counter = new slidingWindowCounter(deriveNumWindowChunksFrom(this.windowLengthInSeconds,this.emitFrequencyInSeconds));
    }
    
    public rollingCountBolt(int windowLengthInSeconds, int emitFrequencyInSeconds) throws InterruptedException {
      this.windowLengthInSeconds = windowLengthInSeconds;
      this.emitFrequencyInSeconds = emitFrequencyInSeconds;      
      counter = new slidingWindowCounter(deriveNumWindowChunksFrom(this.windowLengthInSeconds,this.emitFrequencyInSeconds));
    }
    
    public void prepare(Map map, TopologyContext tc, OutputCollector collector) {
        this.collector = collector;
    }
    
    public void execute(Tuple tuple) {
        if (isTickTuple(tuple)) {
        emitCurrentWindowCounts();
        System.out.println("TICK TUPLE");
      }
      else {
        countObjAndAck(tuple);
      }
    }
    
    private void emit(Map<String, Long> counts) {
      for (Map.Entry<String, Long> entry : counts.entrySet()) {
        String obj = entry.getKey();
        Long count = entry.getValue()/this.windowLengthInSeconds;
        System.out.println("CUENTA AL ENVIAR --> "+obj+" CUENTA:"+count.toString());
        collector.emit(new Values(obj, count.toString())); //Envio la ip/protocolo y la cuenta en la ventana al siguiente bolt
      }
    }
    
    private void countObjAndAck(Tuple tuple) {
        /*for (int i = 0; i < tuple.size(); i++) {
            //System.out.println(tuple.getValue(i));
            counter.incrementCount((String) tuple.getValue(i));
        }*/
        //if(tuple.getString(2).length() > 1){
            //System.out.println("Origen--> "+tuple.getString(0)+"  Destino-->"+tuple.getString(1));
            //System.out.println("PROTOCOLO --> "+tuple.getString(2));
            counter.incrementCount(tuple.getString(2));
        //}
        collector.ack(tuple);
    }
    
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
      declarer.declare(new Fields("campo", "cuentaVentana"));
    }
    
    private void emitCurrentWindowCounts() {
      Map<String, Long> counts = counter.getCountsThenAdvanceWindow();
      emit(counts);
    }
    
    public boolean isTickTuple(Tuple tuple){
        return tuple.getSourceComponent().equals(Constants.SYSTEM_COMPONENT_ID) && tuple.getSourceStreamId().equals(Constants.SYSTEM_TICK_STREAM_ID);
    }
    
    @Override
    public Map<String, Object> getComponentConfiguration() {
      Map<String, Object> conf = new HashMap<String, Object>();
      conf.put(Config.TOPOLOGY_TICK_TUPLE_FREQ_SECS, emitFrequencyInSeconds);
      return conf;
    }
    
    private int deriveNumWindowChunksFrom(int windowLengthInSeconds, int windowUpdateFrequencyInSeconds) {
      return windowLengthInSeconds / windowUpdateFrequencyInSeconds;
    }
   
}
