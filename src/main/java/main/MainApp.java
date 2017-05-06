package main;

import main.service.LongRununigServie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ConfigurableApplicationContext;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import static org.springframework.boot.Banner.Mode.*;

@SpringBootApplication
public class MainApp {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainApp.class);

    public static void main(String[] args) throws Exception {
        ConfigurableApplicationContext context = new SpringApplicationBuilder()
                .sources(MainApp.class)
                .bannerMode(OFF)
                .run();

        LongRununigServie longRununigServie = context.getBean(LongRununigServie.class);

        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        LOGGER.warn("Enter command");
        while (true) {
            String command = br.readLine();
            LOGGER.warn("Command " + command);
            if ("q".equals(command)) {
                break;
            }
            if (command.startsWith("r")) {
                String[] params = command.split(" ");
                longRununigServie.loadDataNamespacePrefixAndParam(params[1]);
                longRununigServie.simpleDataNamespacePrefixWithParams(params[1]);
                longRununigServie.simpleDataNamespacePrefixNoParams();
                longRununigServie.simpleDataNamespacePrefixNoParamsString();
                //longRununigServie.onlyCachce(params[1]);
                //LOGGER.warn(loadDataNamespacePrefixAndParam);
            }
            if (command.startsWith("c")) {
                String[] params = command.split(" ");
                if (params.length > 1) {
                    longRununigServie.clearCache(params[1]);
                } else {
                    longRununigServie.clearAllCache();
                }
            }
        }

        context.close();
    }

}
