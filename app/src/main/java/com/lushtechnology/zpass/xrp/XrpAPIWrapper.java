package com.lushtechnology.zpass;

/*import org.xrpl.xrpl4j.client.XrplClient;
import org.xrpl.xrpl4j.crypto.keys.Base58EncodedSecret;
import org.xrpl.xrpl4j.crypto.keys.Seed;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;
*/
public class XrpAPIWrapper {

    /*
    TODO: fix xrpl4j android issue
    XrplClient xrplClient;
    Seed wallet;

    private void initXrpClient(String seed) {
        HttpUrl rippledUrl = HttpUrl
                .get("https://s.altnet.rippletest.net:51234/");
        xrplClient = new XrplClient(rippledUrl);

        Seed wallet = Seed.fromBase58EncodedSecret(Base58EncodedSecret.of(seed));
    }

    public long getAccountInfo() {
        Address classicAddress = wallet.deriveKeyPair().publicKey().deriveAddress();
        AccountInfoRequestParams requestParams =
                AccountInfoRequestParams.of(classicAddress);

        double value = -1;
        try {
            AccountInfoResult accountInfoResult =
                    xrplClient.accountInfo(requestParams);

            XrpCurrencyAmount amount = accountInfoResult.accountData().balance();

            value = amount.value().longValue();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return value;
    }*/
}
