package es.daniel.bmjava.common;

import es.daniel.bmjava.common.data.Config;
import es.daniel.bmjava.common.data.Protocol;
import org.apache.commons.cli.*;

public class CmdParser {
    String[] args;
    boolean full;

    public CmdParser(String[] args) {
        this.args = args;
    }

    public Config parseConfig(){
        Options options = new Options();
        options.addOption( "s", "server", true, "Server IP to use (\"server.local\" by default)" );
        options.addOption( "c", "continous", true, "Enable continuos mod" );
        options.addOption( "d", "dataset", true, "DataSet to use" );
        options.addOption( "h", "help", false, "Print this help" );
        if(full){
            options.addOption( "p", "protocol", true, "Client to use MQTT|WS|KAFKA|REST");
        }

        if(args==null || args.length ==0){
            printHelpAndExit(options);
        }

        try {
            // parse the command line arguments
            CommandLineParser parser = new DefaultParser();
            CommandLine line = parser.parse( options, args );

            // validate that block-size has been set
            if( line.hasOption( "help" ) ) {
                // print the value of block-size
                printHelpAndExit(options,0);
            }

            Config ret = new Config();
            if(line.hasOption("dataset")){
                ret.setDataset(line.getOptionValue("dataset"));
            }else{
                System.out.println("Need a DataFile\n");
                printHelpAndExit(options);
            }
            if(line.hasOption("server")){
                ret.setServer(line.getOptionValue("server"));
            }else{
                ret.setServer("server.local");
            }

            ret.setContinous(line.hasOption("continous"));

            if(full) {
                if(line.hasOption("protocol")){
                    ret.setProto(Protocol.valueOf(line.getOptionValue("protocol")));
                }else{
                    System.out.println("Need a Protocol\n");
                    printHelpAndExit(options);
                }
            }

            System.out.println(ret);
            return ret;
        }
        catch( ParseException exp ) {
            System.out.println( "Unexpected exception:" + exp.getMessage() );
            printHelpAndExit(options);
        }
        return null;
    }

    private void printHelpAndExit(Options options) {
        printHelpAndExit(options,-1);
    }

    private void printHelpAndExit(Options options, int reason) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("java -jar .... [options]",options);
        System.exit(reason);
    }

    public boolean isFull() {
        return full;
    }

    public void setFull(boolean full) {
        this.full = full;
    }
}
