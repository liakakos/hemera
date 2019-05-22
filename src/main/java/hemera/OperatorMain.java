package hemera;

import com.daml.ledger.javaapi.data.*;
import com.daml.ledger.rxjava.DamlLedgerClient;
import com.daml.ledger.rxjava.components.Bot;
import hemera.bots.CallBot;
import hemera.bots.TransactionBot;
import hemera.model.ethereum.call.CallRequest;
import hemera.model.ethereum.transaction.TransactionRequest;

import java.util.*;

public class OperatorMain {
    public static final String APP_ID = "HemeraOperator";

    public static void main(String[] args) {
        if (args.length < 4) {
            System.err.println("Usage: HOST PORT PARTY INFURA_ENDPOINT");
            System.exit(-1);
        }

        String ledgerHost = args[0];
        int ledgerPort = Integer.valueOf(args[1]);
        String party = args[2];
        Web3jProvider.getInstance().init(args[3]);

        DamlLedgerClient client = DamlLedgerClient.forHostWithLedgerIdDiscovery(
                ledgerHost, ledgerPort, Optional.empty());

        client.connect();

        String ledgerId = client.getLedgerId();

        Set<Identifier> callRequestTids = new HashSet<>(Collections.singletonList(CallRequest.TEMPLATE_ID));
        TransactionFilter callRequestFilter = filterFor(callRequestTids, party);
        CallBot callBot = new CallBot(APP_ID, ledgerId, party);
        Bot.wire(APP_ID, client, callRequestFilter, callBot::process, callBot::getRecordFromContract);

        Set<Identifier> transactionRequestTids = new HashSet<>(Collections.singletonList(
                TransactionRequest.TEMPLATE_ID));
        TransactionFilter transactionRequestFilter = filterFor(transactionRequestTids, party);
        TransactionBot transactionBot = new TransactionBot(APP_ID, ledgerId, party);
        Bot.wire(APP_ID, client, transactionRequestFilter,
                transactionBot::process, transactionBot::getRecordFromContract);
    }

    private static TransactionFilter filterFor(Set<Identifier> templateIds, String party) {
        InclusiveFilter inclusiveFilter = new InclusiveFilter(templateIds);
        Map<String, Filter> filter = Collections.singletonMap(party, inclusiveFilter);
        return new FiltersByParty(filter);
    }
}
