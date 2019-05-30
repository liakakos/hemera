[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/liakakos/hemera/blob/master/LICENSE)

# Hemera: DAML Library + Java App for Ethereum Integration

Welcome to Hemera! [Hemera](https://en.wikipedia.org/wiki/Hemera) is a set of [DAML](https://daml.com/) Modules for interfacing between the [Ethereum](https://www.ethereum.org/) blockchain and the DAML Ledger. It also includes a reference application which drives the DAML Templates. The App is implemented in Java using DAML's [Java bindings](https://docs.daml.com/app-dev/bindings-java/index.html) for interaction with the DAML Ledger, [web3j](https://web3j.io/) for driving Ethereum's JSON-RPC API and [ethereumj](https://github.com/ethereum/ethereumj) for compiling solidity on the fly. Communication with the Ethereum Network happens via an [INFURA](https://infura.io/) endpoint but the implementation is agnostic to the Ethereum node as all transaction signing happens locally.

## The DAML Ethereum Library

The [DAML Ethereum Library](https://github.com/liakakos/hemera/tree/master/src/daml/Ethereum) assumes a single operator party and multiple user parties. Users send requests to the operator in order to perform certain actions on the Ethereum network and get the results in the form of contract responses. The library is organized in a set of modules with distinct functionality. More specifically:

### Onboarding

The Onboarding module is used for bootstrapping the DAML Ledger. The party which assumes the operator role creates an instance of the `Operator` template and invites users by exercising the `Operator_InviteUser` choice. The `UserInvitation` contract is accepted by the user party through the `UserInvitation_Accept` choice and the `User` role contract is created. The user can now start sending requests to the operator. The operator can also offboard a user by exercising the `User_Revoke` choice on the `User` contract.

### SmartContract

This module enables the user to request the creation and deployment of an Ethereum smart contract. The user creates a `NewSmartContractRequest` by exercising the `User_NewSmartContract` choice on their role contract. The user populates the request with the address from which the contract will be deployed, its name and the solidity source code or the hex formatted bytecode of the contract. Optionally the user can pass in constructor arguments, any initial value that the contract will hold as well as values for gas limit and gas price.
A successful request is accepted by the operator via the `NewSmartContractRequest_Accept` choice and an `UnsignedNewContractTransaction` is created which among others contains the raw unsigned transaction payload.
The user signs the payload locally and passes it as an argument to the `UnsignedNewContractTransaction_Sign` choice creating a `SignedNewContractTransaction`.
Finally the user requests deployment of the smart contract by exercising the `SignedNewContractTransaction_Deploy` choice. The operator responds to the request by attempting to transmit the transaction to the Ethereum Network. If transmission was successful, the operator includes the transaction hash along with the broadcast time and Ethereum node client version.

### Call

The Call module is used to query deployed contracts for their state by calling functions that are constant and their `"stateMutability"` property is set to `"view"`. Such function calls do not alter the state, therefore they require no gas or transaction signing. The user creates a `CallRequest` by exercising the `User_Call` choice on their role contract and passes in the contract address, function name, any arguments and any function return types. Optionally they can include a caller address.
The operator responds to the request with a `CallResponse` that contains the original data along with the function output and the broadcast details

### Transaction

For function calls that alter the state of the deployed contracts the Transaction module is used. Similar to the Call module, the user creates a `TransactionRequest` specifying the contract address, the name of the function, the arguments and return types. Since the transaction will consume gas, the user specifies the caller address and optionally values for the gas limit and desired gas price. The user can also include ether to the request by using the optional `optValue` field.
A successful request is converted to an `UnsignedTransaction` by the operator and the raw unsigned transaction payload is populated for the user to sign. The user signs the transaction locally and exercises the `UnsignedTransaction_Sign` choice creating a `SignedTransaction` that is ready to be broadcasted. Finally the user requests transmission of the signed transaction by exercising the `SignedTransaction_Send` choice. The operator attempts to transmit the signed payload and responds with transmission details and the transaction hash.

### Transfer

If the user wants to send ether to an address they use the Transfer module. The mechanics are similar to the Transaction module with the difference that the `TransferRequest` only contains the from and to addresses as well as the value that will be sent. The user may also choose to use their custom gas limit and gas price values if they don't want to rely on the defaults.
The operator returns an `UnsignedTransferTransaction` that the user has to locally sign and convert to a `SignedTransferTransaction` which they can then broadcast to the Ethereum Network.

### ERC20Contract

This module contains a user role contract for interacting with any [ERC-20](https://theethereum.wiki/w/index.php/ERC20_Token_Standard) compliant contract on the Ethereum Network. The choices of the `ERC20Contract` template correspond to the functions of the ERC-20 interface. Exercising them creates fully populated `CallRequest`s or `TransactionRequest`s to the operator that follow the aforementioned flow.

### Utils

This is a helper module specifying the various Ether units, the solidity [ABI](https://solidity.readthedocs.io/en/v0.5.9/abi-spec.html) types and functions for converting as well as validating them.

## Getting Started

### 1. Environment

To run Hemera, the DAML SDK must be installed on your system. Click [here](https://docs.daml.com/getting-started/installation.html) for the official installation instructions.
The java app is a [Maven](https://maven.apache.org/) project. You will also need to have that installed in order to download all dependencies and execute the various targets.

### 2. Clone this repository

```
git clone git@github.com:liakakos/hemera.git
cd hemera
```

### 3. Connect to an Ethereum Node

Although the app is agnostic to the type of Ethereum Node it connects to, you can use an INFURA endpoint to get up and running smoothly. To obtain your own endpoint, you can sign up at [this](https://infura.io/register) link. Once you have the endpoint which should be of the form `mainnet.infura.io/v3/[SomeHexProjectID]` (or `ropsten.infura.io/v3/[SomeHexProjectID]` if you want to play with fake ether) you can paste it inside of the [`<infuraEndpoint></infuraEndpoint>`](https://github.com/liakakos/hemera/blob/master/pom.xml#L19) tag under the project properties in the `pom.xml` file.

### 4. Run the DAML Sandbox

For the DAML sandbox to run you first need to compile the DAML Ethereum Library to a dar. To do this run
```
daml build -o target/daml/hemera.dar
```
from the parent hemera directory. After the dar is built, run the sandbox by issuing
```
daml sandbox target/daml/hemera.dar
```

### 5. Run the Operator App

In order to serve the requests from the users, you must run the operator java target.
To do this open a new terminal and navigate to the parent hemera directory. You must first compile the java code:
```
mvn compile
```
This will generate the required java wrappers of the templates present in the DAML Ethereum Library and compile the project. Make sure that you have already created the dar file since the java codegen depends on it.
Before you run the operator target make sure that you have set the required [properties](https://github.com/liakakos/hemera/blob/master/pom.xml#L15-L17) in your `pom.xml` project file:
```
<ledgerHost>localhost</ledgerHost>
<ledgerPort>6865</ledgerPort>
<operatorParty>Alice</operatorParty>
```
where `ledgerHost` and `ledgerPort` is the host and port where the DAML Sandbox is running and `operatorParty` is the name of the operator DAML Party.
Start the Operator App by running:
```
mvn exec:java@run-operator
```

### 6. Run the Navigator

Time to launch the UI where you can create requests and view the results. For this you can use the [Navigator](https://docs.daml.com/tools/navigator/index.html) which ships with the DAML SDK. On yet another tab, navigate to the parent hemera directory and run:
```
daml navigator server
```
You are now ready to create the Operator role contract invite users and start sending requests

### 7. Signing a Transaction locally

The library is implemented in a way where the operator party has *zero knowledge of the user's private keys*. Unsigned requests need to be signed by the party who created the request using the private key that corresponds to the sender's address. Since manual transaction signing can be tedious, the java app comes with a separate client target that facilitates transaction signing. The client target is limited to a single private key per app instance. It will attempt to sign any unsigned request originating from the user on whose behalf it is running. Before executing the client target make sure you populate the [`clientParty`](https://github.com/liakakos/hemera/blob/master/pom.xml#L18) and [`privateKey`](https://github.com/liakakos/hemera/blob/master/pom.xml#L20) properties in your `pom.xml` file. Start the Client App by running:
```
mvn exec:java@run-client
```
_Note that the privateKey string must not contain any 0x prefix_

## Disclaimer

As the [License](https://github.com/liakakos/hemera/blob/master/LICENSE#L153) states I assume no liability for any damages or losses arising from the use of this Work. Please proceed at your own risk!
