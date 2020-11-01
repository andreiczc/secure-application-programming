package ro.ase.ism.sap.crypto01;

import java.security.Provider;
import java.security.Security;

public class Providers {

    public static void loadBCProvider() {
        // check if the provider exists
        String providerName = ProgMain.BOUNCY_CASTLE_PROVIDER;
        Provider provider = Security.getProvider(providerName);

        if (provider != null) {
            System.out.println(providerName + " is already available");
        } else {
            // load the provider
            Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());

            // do a second check
            provider = Security.getProvider(providerName);
            if (provider != null) {
                System.out.println(providerName + " now loaded");
            } else {
                System.out.println("issue encountered");
            }
        }
    }

    public static boolean checkProvider(String providerName) {
        Provider provider = Security.getProvider(providerName);

        if (provider != null) {
            System.out.println(providerName + " is loaded");
            return true;
        } else {
            System.out.println(providerName + " not avalaible");
            return false;
        }
    }
}
