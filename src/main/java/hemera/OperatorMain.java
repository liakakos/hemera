package hemera;

import com.daml.ledger.javaapi.data.*;
import com.daml.ledger.rxjava.DamlLedgerClient;
import com.daml.ledger.rxjava.components.Bot;
import hemera.bots.*;
import hemera.model.ethereum.call.CallRequest;
import hemera.model.ethereum.smartcontract.NewSmartContractRequest;
import hemera.model.ethereum.smartcontract.SignedNewContractTransaction;
import hemera.model.ethereum.transaction.SignedTransaction;
import hemera.model.ethereum.transaction.TransactionRequest;
import hemera.model.ethereum.transfer.SignedTrasferTransaction;
import hemera.model.ethereum.transfer.TransferRequest;
import hemera.utils.LedgerUtils;

import java.math.BigInteger;
import java.util.*;

public class OperatorMain {
    private static final String APP_ID = "HemeraOperator";
    public static BigInteger DEFAULT_GAS_PRICE_WEI;

    public static void main(String[] args) {
        if (args.length < 5) {
            System.err.println("Usage: HOST PORT PARTY INFURA_ENDPOINT DEFAULT_GAS_PRICE");
            System.exit(-1);
        }

        String ledgerHost = args[0];
        int ledgerPort = Integer.valueOf(args[1]);
        String party = args[2];
        Web3jProvider.getInstance().init(args[3]);
        DEFAULT_GAS_PRICE_WEI = new BigInteger(args[4]);

        DamlLedgerClient client = DamlLedgerClient.forHostWithLedgerIdDiscovery(
                ledgerHost, ledgerPort, Optional.empty());

        client.connect();

        String ledgerId = client.getLedgerId();

        // Call
        Set<Identifier> callRequestTids = new HashSet<>(Collections.singletonList(CallRequest.TEMPLATE_ID));
        TransactionFilter callRequestFilter = LedgerUtils.filterFor(callRequestTids, party);
        CallBot callBot = new CallBot(APP_ID, ledgerId, party);
        Bot.wire(APP_ID, client, callRequestFilter, callBot::process, callBot::getRecordFromContract);

        // Transaction
        Set<Identifier> transactionRequestTids = new HashSet<>(Collections.singletonList(
                TransactionRequest.TEMPLATE_ID));
        TransactionFilter transactionRequestFilter = LedgerUtils.filterFor(transactionRequestTids, party);
        TransactionBot transactionBot = new TransactionBot(APP_ID, ledgerId, party);
        Bot.wire(APP_ID, client, transactionRequestFilter,
                transactionBot::process, transactionBot::getRecordFromContract);

        // Smart Contract
        Set<Identifier> smartContractRequestTids = new HashSet<>(Collections.singletonList(
                NewSmartContractRequest.TEMPLATE_ID));
        TransactionFilter smartContractRequestFilter = LedgerUtils.filterFor(smartContractRequestTids, party);
        SmartContractBot smartContractBot = new SmartContractBot(APP_ID, ledgerId, party);
        Bot.wire(APP_ID, client, smartContractRequestFilter,
                smartContractBot::process, smartContractBot::getRecordFromContract);

        // Transfer
        Set<Identifier> transferRequestTids = new HashSet<>(Collections.singletonList(TransferRequest.TEMPLATE_ID));
        TransactionFilter transferRequestFilter = LedgerUtils.filterFor(transferRequestTids, party);
        TransferBot transferBot = new TransferBot(APP_ID, ledgerId, party);
        Bot.wire(APP_ID, client, transferRequestFilter, transferBot::process, transferBot::getRecordFromContract);

        // Send
        Set<Identifier> signedTransactionTids = new HashSet<>(Arrays.asList(
                SignedNewContractTransaction.TEMPLATE_ID,
                SignedTransaction.TEMPLATE_ID,
                SignedTrasferTransaction.TEMPLATE_ID));
        TransactionFilter signedTransactionFilter = LedgerUtils.filterFor(signedTransactionTids, party);
        SendBot sendBot = new SendBot(APP_ID, ledgerId, party);
        Bot.wire(APP_ID, client, signedTransactionFilter, sendBot::process, sendBot::getRecordFromContract);

        while (true)
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
}
