package hemera;


import org.checkerframework.checker.nullness.qual.NonNull;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

public class Web3jProvider {
    public Web3j web3j;
    public String version;
    private boolean init = false;

    private static Web3jProvider instance = new Web3jProvider();

    private Web3jProvider() {}

    public void init(String infuraEndpoint) {
        if (init) {
            System.out.println("Web3jProvider has already been initialized");
            return;
        }

        String endpoint = String.format("https://%s", infuraEndpoint);
        this.web3j = Web3j.build(new HttpService(endpoint));
        try {
            this.version = web3j.web3ClientVersion().send().getWeb3ClientVersion();
            init = true;
        } catch (Exception e) {
            System.err.println(String.format("Could not get web3 client version. Please check if %s " +
                            "is the correct endpoint", endpoint));
            System.exit(-1);
        }
    }

    @NonNull
    public static Web3jProvider getInstance() { return instance; }
}
