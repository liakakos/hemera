package hemera;

import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.TransactionFilter;
import com.daml.ledger.rxjava.DamlLedgerClient;
import com.daml.ledger.rxjava.components.Bot;
import hemera.bots.SignBot;
import hemera.model.ethereum.smartcontract.UnsignedNewContractTransaction;
import hemera.model.ethereum.transaction.UnsignedTransaction;
import hemera.model.ethereum.transfer.UnsignedTrasferTransaction;
import hemera.utils.LedgerUtils;

import java.util.*;

public class ClientMain {
  private static final String APP_ID = "HemeraClient";
  public static String PRIVATE_KEY;

  public static void main(String[] args) {
    if (args.length < 4) {
      System.err.println("Usage: HOST PORT PARTY PRIVATE_KEY");
      System.exit(-1);
    }

    String ledgerHost = args[0];
    int ledgerPort = Integer.valueOf(args[1]);
    String party = args[2];
    PRIVATE_KEY = args[3];

    DamlLedgerClient client = DamlLedgerClient.forHostWithLedgerIdDiscovery(
            ledgerHost, ledgerPort, Optional.empty());

    client.connect();

    String ledgerId = client.getLedgerId();

    // Sign
    Set<Identifier> unsignedTransactionTids = new HashSet<>(Arrays.asList(
            UnsignedNewContractTransaction.TEMPLATE_ID,
            UnsignedTransaction.TEMPLATE_ID,
            UnsignedTrasferTransaction.TEMPLATE_ID));
    TransactionFilter unsignedTransactionFilter = LedgerUtils.filterFor(unsignedTransactionTids, party);
    SignBot signBot = new SignBot(APP_ID, ledgerId, party);
    Bot.wire(APP_ID, client, unsignedTransactionFilter, signBot::process, signBot::getRecordFromContract);

    while (true)
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
  }
}
