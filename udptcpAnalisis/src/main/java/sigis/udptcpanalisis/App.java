package sigis.udptcpanalisis;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.topology.TopologyBuilder;
import bolt.dataSplitBolt;
import bolt.rabbitMqBolt;
import bolt.rollingCountBolt;
import java.io.IOException;
import spout.socketSpout;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws IOException, InterruptedException 
    {
                Config conf = new Config();
        //conf.setMaxSpoutPending(20);
        conf.setDebug(false);
        conf.put(Config.TOPOLOGY_MAX_SPOUT_PENDING, 1);
        TopologyBuilder builder = new TopologyBuilder();
        //builder.setSpout("randomWordSpout", new randomWordSpout(),1);
        builder.setSpout("socketSpout", new socketSpout(),1);
        builder.setBolt("dataSplitbolt", new dataSplitBolt(),1).shuffleGrouping("socketSpout");
        builder.setBolt("rollingCountBolt", new rollingCountBolt()).shuffleGrouping("dataSplitbolt");
        builder.setBolt("rabbitMqBolt", new rabbitMqBolt()).shuffleGrouping("rollingCountBolt");
        
        /*TupleTableConfig config = new TupleTableConfig("internetAnalysis", "campo");
        config.setBatch(false);
        config.addColumn("data", "cuenta");
        //Viene el HBASE BOLT QUE PERSISTE
        builder.setBolt("hbaseBolt", new HBaseBolt()).shuffleGrouping("rollingCountBolt");
 */
        LocalCluster cluster = new LocalCluster();
        cluster.submitTopology("gpsTopology", conf, builder.createTopology());
    }
}
