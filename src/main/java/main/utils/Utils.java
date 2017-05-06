package main.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by tomek on 28.04.17.
 */
public class Utils {

    private static final Logger LOGGER = LoggerFactory.getLogger(Utils.class);

    public static void sleep(int s) {
        try {
            LOGGER.warn("Waiting...");
            Thread.sleep(s * 1000);
        } catch (Exception e){}
    }

}
