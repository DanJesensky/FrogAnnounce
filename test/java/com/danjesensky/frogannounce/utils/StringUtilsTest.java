package com.danjesensky.frogannounce.utils;

import org.junit.Assert;
import org.junit.Test;

public class StringUtilsTest {
    @Test
    public void joinSingleElement() {
        String[] arr = {"test", "123"};
        String result = StringUtils.join(1, " ", arr);
        Assert.assertEquals("123", result);
    }

    @Test
    public void joinAllElements(){
        String[] arr = {"test", "123", "456"};
        String result = StringUtils.join(0, " ", arr);
        Assert.assertEquals("test 123 456", result);
    }

    @Test
    public void joinMultipleElements(){
        String[] arr = {"test", "123", "456"};
        String result = StringUtils.join(1, " ", arr);
        Assert.assertEquals("123 456", result);
    }
}