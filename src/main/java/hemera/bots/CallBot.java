package hemera.bots;

import com.daml.ledger.javaapi.data.Command;
import com.daml.ledger.javaapi.data.Identifier;
import com.daml.ledger.javaapi.data.Record;
import com.daml.ledger.rxjava.components.LedgerViewFlowable;
import com.daml.ledger.rxjava.components.helpers.CommandsAndPendingSet;
import hemera.Web3jProvider;
import hemera.model.ethereum.call.CallRequest;
import hemera.model.ethereum.utils.ABIValue;
import hemera.model.ethereum.utils.SendStatus;
import hemera.model.ethereum.utils.sendstatus.FailedToSend;
import hemera.model.ethereum.utils.sendstatus.Sent;
import hemera.utils.ABIUtils;
import io.reactivex.Flowable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.FunctionReturnDecoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthCall;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

public class CallBot extends AbstractBot {

    private final static Logger log = LoggerFactory.getLogger(CallBot.class);
    private static final String zeroAddress = "0x0000000000000000000000000000000000000000";


    public CallBot(String appId, String ledgerId, String party) {
        super.appId = appId;
        super.ledgerId = ledgerId;
        super.party = party;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Flowable<CommandsAndPendingSet> process(LedgerViewFlowable.LedgerView<Record> ledgerView) {
        List<CallRequest.Contract> callRequests =
                (List<CallRequest.Contract>) (List<?>) getContracts(ledgerView, CallRequest.TEMPLATE_ID);

        if (callRequests.isEmpty()) {
            return Flowable.empty();
        }

        log.info(String.format("Got %d CallRequest(s)", callRequests.size()));
        Map<Identifier, Set<String>> pending = new HashMap<>();
        pending.putIfAbsent(CallRequest.TEMPLATE_ID, new HashSet<>());

        List<Command> commandList = callRequests.stream().map(contract -> {
            List<Type> functionArgs = contract.data.args.stream()
                    .map(ABIUtils::toWeb3jType).collect(Collectors.toList());
            List<TypeReference<?>> returns = ABIUtils.toWeb3jTypeReference(contract.data.returns);
            Function function = new Function(contract.data.name, functionArgs, returns);
            String encodedFunction = FunctionEncoder.encode(function);
            String from = contract.data.optFrom.orElse(zeroAddress);
            SendStatus status;
            List<ABIValue> response;
            try {
                EthCall resp = Web3jProvider.getInstance().web3j.ethCall(Transaction.createEthCallTransaction(
                        from, contract.data.to, encodedFunction),
                        DefaultBlockParameterName.LATEST).send();
                Thread.sleep(1000);
                List<Type> result = FunctionReturnDecoder.decode(resp.getValue(), function.getOutputParameters());
                response = ABIUtils.fromWeb3jTypes(result);
                status = new Sent(Instant.now(), Web3jProvider.getInstance().version, Optional.empty());
            } catch (Exception e) {
                log.error(String.format("Failed to call function %s on contract %s",
                        contract.data.name, contract.data.to));
                response = Collections.emptyList();
                status = new FailedToSend(e.toString());
            }
            pending.get(CallRequest.TEMPLATE_ID).add(contract.id.contractId);
            return contract.id.exerciseCallRequest_Respond(response, status);
        }).collect(Collectors.toList());

        if (!commandList.isEmpty()) {
            return toCommandsAndPendingSet(commandList, pending);
        } else {
            return Flowable.empty();
        }
    }
}
