package com.lushtechnology.zpass.xrp;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.primitives.UnsignedInteger;
import com.google.common.primitives.UnsignedLong;
import okhttp3.HttpUrl;
import org.xrpl.xrpl4j.client.JsonRpcClientErrorException;
import org.xrpl.xrpl4j.client.XrplClient;

import org.xrpl.xrpl4j.crypto.keys.Base58EncodedSecret;
import org.xrpl.xrpl4j.crypto.keys.PrivateKey;
import org.xrpl.xrpl4j.crypto.keys.PublicKey;
import org.xrpl.xrpl4j.crypto.keys.Seed;
import org.xrpl.xrpl4j.crypto.signing.SignatureService;
import org.xrpl.xrpl4j.crypto.signing.SingleSignedTransaction;
import org.xrpl.xrpl4j.crypto.signing.bc.BcSignatureService;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoRequestParams;
import org.xrpl.xrpl4j.model.client.accounts.AccountInfoResult;
import org.xrpl.xrpl4j.model.client.common.LedgerIndex;
import org.xrpl.xrpl4j.model.client.common.LedgerSpecifier;
import org.xrpl.xrpl4j.model.client.fees.FeeResult;
import org.xrpl.xrpl4j.model.client.ledger.LedgerRequestParams;
import org.xrpl.xrpl4j.model.client.transactions.SubmitResult;
import org.xrpl.xrpl4j.model.client.transactions.TransactionRequestParams;
import org.xrpl.xrpl4j.model.client.transactions.TransactionResult;
import org.xrpl.xrpl4j.model.immutables.FluentCompareTo;
import org.xrpl.xrpl4j.model.transactions.Address;
import org.xrpl.xrpl4j.model.transactions.Payment;
import org.xrpl.xrpl4j.model.transactions.Transaction;
import org.xrpl.xrpl4j.model.transactions.XrpCurrencyAmount;


import java.math.BigDecimal;

public class SendXrp {

    public static void send(String senderSecret, String receiverAddress, long amount)
            throws JsonRpcClientErrorException, JsonProcessingException, InterruptedException {
        System.out.println("Running the SendXrp sample...");

        // Construct a network client
        final HttpUrl rippledUrl = HttpUrl.get("https://s.altnet.rippletest.net:51234/");
        XrplClient xrplClient = new XrplClient(rippledUrl);

        // xrpl4j 3.0.1
        SignatureService signatureService = new BcSignatureService();

        Seed senderSeed = Seed.fromBase58EncodedSecret(Base58EncodedSecret.of(senderSecret));
        PrivateKey senderPrivateKey = senderSeed.deriveKeyPair().privateKey();
        PublicKey senderPublicKey = senderSeed.deriveKeyPair().publicKey();
        Address senderAddress = senderPublicKey.deriveAddress();

        // Look up your Account Info
        final AccountInfoRequestParams requestParams = AccountInfoRequestParams
                .builder().ledgerSpecifier(LedgerSpecifier.CLOSED.VALIDATED)
                .account(senderAddress)
                .build();

        final AccountInfoResult accountInfoResult = xrplClient.accountInfo(requestParams);
        final UnsignedInteger sequence = accountInfoResult.accountData().sequence();

        // Request current fee information from rippled
        final FeeResult feeResult = xrplClient.fee();
        final XrpCurrencyAmount openLedgerFee = feeResult.drops().openLedgerFee();

        // Construct a Payment
        // Workaround for https://github.com/XRPLF/xrpl4j/issues/84
        final LedgerIndex validatedLedger = xrplClient.ledger(LedgerRequestParams.builder().ledgerSpecifier(LedgerSpecifier.CLOSED.VALIDATED).build())
                .ledgerIndex()
                .orElseThrow(() -> new RuntimeException("LedgerIndex not available."));

        final UnsignedInteger lastLedgerSequence = UnsignedInteger.valueOf(
                validatedLedger.plus(UnsignedInteger.valueOf(4)).unsignedIntegerValue().intValue()
        ); // <-- LastLedgerSequence is the current ledger index + 4

        Payment payment = Payment.builder()
                .account(senderAddress)
                .amount(XrpCurrencyAmount.ofDrops(amount))
                .destination(Address.of(receiverAddress))
                .sequence(sequence)
                .fee(openLedgerFee)
                .signingPublicKey(senderPublicKey)
                .lastLedgerSequence(lastLedgerSequence)
                .build();
        System.out.println("Constructed Payment: " + payment);


        // Sign the Payment
        SingleSignedTransaction<Payment> signedPayment =
                signatureService.sign(senderPrivateKey, payment);
        System.out.println("Signed Payment: " + signedPayment.signedTransaction());

        // Submit the Payment
        final SubmitResult<Payment> submitResult = xrplClient.submit(signedPayment);
        System.out.println(submitResult);

        // Wait for validation
        TransactionResult<Payment> transactionResult = null;

        boolean transactionValidated = false;
        boolean transactionExpired = false;
        while (!transactionValidated && !transactionExpired) {
            Thread.sleep(4 * 1000);
            final LedgerIndex latestValidatedLedgerIndex = xrplClient.ledger(
                            LedgerRequestParams.builder().ledgerSpecifier(LedgerSpecifier.CLOSED.VALIDATED).build()
                    )
                    .ledgerIndex()
                    .orElseThrow(() -> new RuntimeException("Ledger response did not contain a LedgerIndex."));

            transactionResult = xrplClient.transaction(
                    TransactionRequestParams.of(signedPayment.hash()),
                    Payment.class
            );

            if (transactionResult.validated()) {
                System.out.println("Payment was validated with result code " + transactionResult.metadata().get().transactionResult());
                transactionValidated = true;
            } else {
                final boolean lastLedgerSequenceHasPassed = FluentCompareTo.
                        is(latestValidatedLedgerIndex.unsignedIntegerValue())
                        .greaterThan(UnsignedInteger.valueOf(lastLedgerSequence.intValue()));
                if (lastLedgerSequenceHasPassed) {
                    System.out.println("LastLedgerSequence has passed. Last tx response: " +
                            transactionResult);
                    transactionExpired = true;
                } else {
                    System.out.println("Payment not yet validated.");
                }
            }
        }

        // Check transaction results
        System.out.println(transactionResult);
        System.out.println("Explorer link: https://testnet.xrpl.org/transactions/" + signedPayment.hash());
        transactionResult.metadata().ifPresent(metadata -> {
            System.out.println("Result code: " + metadata.transactionResult());

            metadata.deliveredAmount().ifPresent(deliveredAmount ->
                    System.out.println("XRP Delivered: " + ((XrpCurrencyAmount) deliveredAmount).toXrp())
            );
        });
    }
}