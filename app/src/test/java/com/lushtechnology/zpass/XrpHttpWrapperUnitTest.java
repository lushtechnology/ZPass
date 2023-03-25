package com.lushtechnology.zpass;

import static org.junit.Assert.assertEquals;

import com.lushtechnology.zpass.xrp.XrpHttpWrapper;

import org.junit.Test;

public class XrpHttpWrapperUnitTest {

    XrpHttpWrapper xrp = new XrpHttpWrapper();

    String add = "r4nucSPkeHNo6XNapFwPCpJdYX9XiJB7je";
    String seed = "sEdV5bYRmQS22UsGMfXy8TPimxokno5";

    @Test
    public void test_payment() {

        String receiver = "rBiQksvSvUDS91L2cbQ7EZ6PnaYNBULgyg";
        xrp.pay(seed, receiver, 5000000);
    }

}
