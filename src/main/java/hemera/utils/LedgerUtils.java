package hemera.utils;

import com.daml.ledger.javaapi.data.*;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class LedgerUtils {

    public static TransactionFilter filterFor(Set<Identifier> templateIds, String party) {
        InclusiveFilter inclusiveFilter = new InclusiveFilter(templateIds);
        Map<String, Filter> filter = Collections.singletonMap(party, inclusiveFilter);
        return new FiltersByParty(filter);
    }
}
