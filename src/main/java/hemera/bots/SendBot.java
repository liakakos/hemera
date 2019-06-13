package hemera.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import hemera.Web3jProvider;
import hemera.model.ethereum.smartcontract.SignedNewContractTransaction;
import hemera.model.ethereum.transaction.SignedTransaction;
import hemera.model.ethereum.transfer.SignedTransferTransaction;
import hemera.model.ethereum.utils.SendStatus;
import hemera.model.ethereum.utils.sendstatus.Pending;
import hemera.model.ethereum.utils.sendstatus.Sent;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.protocol.core.methods.response.EthSendTransaction;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class SendBot extends AbstractBot {

    private final static Logger log = LoggerFactory.getLogger(SendBot.class);

    public SendBot(String appId, String ledgerId, String party) {
        super.appId = appId;
        super.ledgerId = ledgerId;
        super.party = party;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Flowable<CommandsAndPendingSet> process(LedgerViewFlowable.LedgerView<Record> ledgerView) {
        List<SignedNewContractTransaction.Contract> signedNewContracts =
                (List<SignedNewContractTransaction.Contract>)(List<?>)
                        getContracts(ledgerView, SignedNewContractTransaction.TEMPLATE_ID);
        List<SignedNewContractTransaction.Contract> signedAndPendingNewContracts =
                signedNewContracts.stream().filter(c -> c.data.sendStatus instanceof Pending)
                        .collect(Collectors.toList());
        List<SignedTransaction.Contract> signedTransactions =
                (List<SignedTransaction.Contract>)(List<?>)
                        getContracts(ledgerView, SignedTransaction.TEMPLATE_ID);
        List<SignedTransaction.Contract> signedAndPendingTransactions =
                signedTransactions.stream().filter(c -> c.data.sendStatus instanceof Pending)
                        .collect(Collectors.toList());
        List<SignedTransferTransaction.Contract> signedTransferTransactions =
                (List<SignedTransferTransaction.Contract>)(List<?>)
                        getContracts(ledgerView, SignedTransferTransaction.TEMPLATE_ID);
        List<SignedTransferTransaction.Contract> signedAndPendingTransferTransactions =
                signedTransferTransactions.stream().filter(c -> c.data.sendStatus instanceof Pending)
                        .collect(Collectors.toList());

        if (signedAndPendingNewContracts.isEmpty()
                && signedAndPendingTransactions.isEmpty()
                && signedAndPendingTransferTransactions.isEmpty()) {
            return Flowable.empty();
        }

        log.info(String.format("Got %d Signed Transactions that are pending", signedAndPendingNewContracts.size()
                + signedAndPendingTransactions.size()
                + signedAndPendingTransferTransactions.size()));

        Map<Identifier, Set<String>> pending = new HashMap<>();
        pending.putIfAbsent(SignedNewContractTransaction.TEMPLATE_ID, new HashSet<>());
        pending.putIfAbsent(SignedTransaction.TEMPLATE_ID, new HashSet<>());
        pending.putIfAbsent(SignedTransferTransaction.TEMPLATE_ID, new HashSet<>());

        List<Command> commandList = signedAndPendingNewContracts.stream().map(contract -> {
            pending.get(SignedNewContractTransaction.TEMPLATE_ID).add(contract.id.contractId);
            try {
                EthSendTransaction resp = Web3jProvider.getInstance()
                        .web3j.ethSendRawTransaction(contract.data.signedTx).send();
                Thread.sleep(1000);
                String txHash = resp.getTransactionHash();
                SendStatus sendStatus = new Sent(
                        Instant.now(),
                        Web3jProvider.getInstance().version,
                        Optional.of(txHash));
                return contract.id.exerciseSignedNewContractTransaction_Sent(sendStatus);
            } catch (Exception e) {
                String reason = String.format("Failed to send new contract transaction with name %s. Exception: %s",
                        contract.data.name, e.getMessage());
                log.error(reason);
                return contract.id.exerciseSignedNewContractTransaction_Fail(reason);
            }
        }).collect(Collectors.toList());

        List<Command> transactionCommandList = signedAndPendingTransactions.stream().map(contract -> {
            pending.get(SignedTransaction.TEMPLATE_ID).add(contract.id.contractId);
            try {
                EthSendTransaction resp = Web3jProvider.getInstance()
                        .web3j.ethSendRawTransaction(contract.data.signedTx).send();
                Thread.sleep(1000);
                String txHash = resp.getTransactionHash();
                SendStatus sendStatus = new Sent(
                        Instant.now(),
                        Web3jProvider.getInstance().version,
                        Optional.of(txHash));
                return contract.id.exerciseSignedTransaction_Sent(sendStatus);
            } catch (Exception e) {
                String reason = String.format("Failed to send transaction with name %s. Exception: %s",
                        contract.data.name, e.getMessage());
                log.error(reason);
                return contract.id.exerciseSignedTransaction_Fail(reason);
            }
        }).collect(Collectors.toList());

        List<Command> transferCommandList = signedAndPendingTransferTransactions.stream().map(contract -> {
            pending.get(SignedTransferTransaction.TEMPLATE_ID).add(contract.id.contractId);
            try {
                EthSendTransaction resp = Web3jProvider.getInstance()
                        .web3j.ethSendRawTransaction(contract.data.signedTx).send();
                Thread.sleep(1000);
                String txHash = resp.getTransactionHash();
                SendStatus sendStatus = new Sent(
                        Instant.now(),
                        Web3jProvider.getInstance().version,
                        Optional.of(txHash));
                return contract.id.exerciseSignedTransferTransaction_Sent(sendStatus);
            } catch (Exception e) {
                String reason = String.format("Failed to send %s from %s to %s. Exception: %s",
                        contract.data.value, contract.data.from, contract.data.to, e.getMessage());
                log.error(reason);
                return contract.id.exerciseSignedTransferTransaction_Fail(reason);
            }
        }).collect(Collectors.toList());

        commandList.addAll(transactionCommandList);
        commandList.addAll(transferCommandList);

        if (!commandList.isEmpty()) {
            return toCommandsAndPendingSet(commandList, pending);
        } else {
            return Flowable.empty();
        }
    }
}
