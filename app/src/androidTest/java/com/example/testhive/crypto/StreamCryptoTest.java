package com.example.testhive.crypto;

import com.goterl.lazycode.lazysodium.LazySodiumAndroid;
import com.goterl.lazycode.lazysodium.SodiumAndroid;
import com.goterl.lazycode.lazysodium.exceptions.SodiumException;
import com.goterl.lazycode.lazysodium.interfaces.PwHash;

import org.junit.BeforeClass;

import java.nio.charset.StandardCharsets;

public class StreamCryptoTest {

    @BeforeClass
    public static void setUp() {
        SodiumAndroid sodiumAndroid = new SodiumAndroid();

        LazySodiumAndroid lazySodiumAndroid = new LazySodiumAndroid(sodiumAndroid, StandardCharsets.UTF_8);

        byte[] pw = lazySodiumAndroid.bytes("password");
        byte[] outputHash = new byte[PwHash.STR_BYTES];

        boolean success = lazySodiumAndroid.cryptoPwHashStr(outputHash, pw,
                pw.length,
                PwHash.OPSLIMIT_MIN,
                PwHash.MEMLIMIT_MIN);


//        try {
//            String hash = lazySodiumAndroid.cryptoPwHashStr("a cool password", PwHash.OPSLIMIT_MIN, PwHash.MEMLIMIT_MIN);
//        } catch (SodiumException e) {
//            e.printStackTrace();
//        }
    }
}
