package com.codeslap.github.jobs.api;

import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * // TODO write description
 *
 * @author cristian
 */
public class TestFormat {
    public static final SimpleDateFormat DATE_PARSER = new SimpleDateFormat("EEE MMM dd kk:mm:ss 'UTC' yyyy");
    @Test
    public void test() throws ParseException {
        Date parse = DATE_PARSER.parse("Tue Oct 09 00:48:46 UTC 2012");
        System.out.println(parse);
    }
}
