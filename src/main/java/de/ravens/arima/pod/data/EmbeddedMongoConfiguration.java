//package de.ravens.arima.pod.data;
//
//import de.flapdoodle.embed.mongo.Command;
//import de.flapdoodle.embed.mongo.MongodExecutable;
//import de.flapdoodle.embed.mongo.MongodProcess;
//import de.flapdoodle.embed.mongo.MongodStarter;
//import de.flapdoodle.embed.mongo.config.IMongodConfig;
//import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
//import de.flapdoodle.embed.mongo.config.Net;
//import de.flapdoodle.embed.mongo.config.RuntimeConfigBuilder;
//import de.flapdoodle.embed.mongo.distribution.Version;
//import de.flapdoodle.embed.process.config.IRuntimeConfig;
//import de.flapdoodle.embed.process.runtime.Network;
//import org.springframework.context.annotation.Bean;
//
//import java.io.IOException;
//
//public class EmbeddedMongoConfiguration {
//    private final static Command command = Command.MongoD;
//
//    @Bean
//    public IRuntimeConfig config() {
//        IRuntimeConfig asdf = new RuntimeConfigBuilder().defaults(Command.MongoD).build();
//        return asdf;
//    }
//
//    @Bean
//    public MongodProcess mongodProcess(IRuntimeConfig asdf) throws IOException {
//        MongodStarter starter = MongodStarter.getInstance(asdf);
//        int port = Network.getFreeServerPort();
//        IMongodConfig mongodConfig = new MongodConfigBuilder()
//                .version(Version.Main.PRODUCTION)
//                .net(new Net(port, Network.localhostIsIPv6()))
//                .build();
//        MongodExecutable mongodExecutable = starter.prepare(mongodConfig);
//        MongodProcess mongod = mongodExecutable.start();
//        return mongod;
//    }
//
//
//}
