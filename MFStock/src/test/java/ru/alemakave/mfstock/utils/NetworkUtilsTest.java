package ru.alemakave.mfstock.utils;

import static ru.alemakave.mfstock.utils.NetworkUtils.isCorrectHost;
import static ru.alemakave.mfstock.utils.NetworkUtils.isCorrectIp;
import static ru.alemakave.mfstock.utils.NetworkUtils.isCorrectPort;

import junit.framework.TestCase;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.IntStream;
import java.util.stream.Stream;

public class NetworkUtilsTest extends TestCase {
    public void testIsCorrectHost() {
        System.out.println("Test assertTrue(isCorrectHost(\"http://qwe.ru\"))");
        assertTrue(isCorrectHost("http://qwe.ru"));
//        System.out.println("Test assertTrue(isCorrectHost(\"http://qwe.ru:8090\"))");
//        assertTrue(isCorrectHost("http://qwe.ru:8090"));
        System.out.println("Test assertTrue(isCorrectHost(\"127.0.0.1\"))");
        assertTrue(isCorrectHost("127.0.0.1"));
//        System.out.println("Test assertTrue(isCorrectHost(\"127.0.0.1:8090\"))");
//        assertTrue(isCorrectHost("127.0.0.1:8090"));

        System.out.println("Test assertFalse(isCorrectHost(\"127\"))");
        assertFalse(isCorrectHost("127"));
        System.out.println("Test assertFalse(isCorrectHost(\"127.0\"))");
        assertFalse(isCorrectHost("127.0"));
        System.out.println("Test assertFalse(isCorrectHost(\"127.0.0\"))");
        assertFalse(isCorrectHost("127.0.0"));
        System.out.println("Test assertFalse(isCorrectHost(\"127.0.0.1:65536\"))");
        assertFalse(isCorrectHost("127.0.0.1:655536"));
        System.out.println("Test assertFalse(isCorrectHost(\"http://qwe.ru:65536\"))");
        assertFalse(isCorrectHost("http://qwe.ru:65536"));
    }

    public void testIsCorrectIp() {
        System.out.println("Test assertTrue(isCorrectIp(\"127.0.0.1\"))");
        assertTrue(isCorrectIp("127.0.0.1"));

        System.out.println("Test assertFalse(isCorrectIp(\"127.0.0.1:8090\"))");
        assertFalse(isCorrectIp("127.0.0.1:8090"));
        System.out.println("Test assertFalse(isCorrectIp(\"127\"))");
        assertFalse(isCorrectIp("127"));
        System.out.println("Test assertFalse(isCorrectIp(\"127.0\"))");
        assertFalse(isCorrectIp("127.0"));
        System.out.println("Test assertFalse(isCorrectIp(\"127.0.0\"))");
        assertFalse(isCorrectIp("127.0.0"));
        System.out.println("Test assertFalse(isCorrectIp(\"127.0.0.1:655536\"))");
        assertFalse(isCorrectIp("127.0.0.1:655536"));

        System.out.println("Test assertFalse(isCorrectIp(\"http://qwe.ru\"))");
        assertFalse(isCorrectIp("http://qwe.ru"));
        System.out.println("Test assertFalse(isCorrectIp(\"http://qwe.ru:8090\"))");
        assertFalse(isCorrectIp("http://qwe.ru:8090"));
        System.out.println("Test assertFalse(isCorrectIp(\"http://qwe.ru:65536\"))");
        assertFalse(isCorrectIp("http://qwe.ru:65536"));
    }

    @ParameterizedTest
    @MethodSource("range")
    public void testIsCorrectPort(int port) {
        assertTrue(isCorrectPort(port));
    }

    static Stream<Integer> range() {
        return IntStream.range(0, 65536).boxed();
    }
}