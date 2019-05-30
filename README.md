[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://github.com/liakakos/hemera/blob/master/LICENSE)

# Hemera: DAML Library + Java App for Ethereum Integration

Welcome to Hemera! [Hemera](https://en.wikipedia.org/wiki/Hemera) is a set of [DAML](https://daml.com/) Modules for interfacing between the [Ethereum](https://www.ethereum.org/) blockchain and the DAML Ledger. It also includes a reference application which drives the DAML Templates. The App is implemented in Java using DAML's [Java bindings](https://docs.daml.com/app-dev/bindings-java/index.html) for interaction with the DAML Ledger, [web3j](https://web3j.io/) for driving Ethereum's JSON-RPC API and [ethereumj](https://github.com/ethereum/ethereumj) for compiling solidity on the fly. Communication with the Ethereum Network happens via an [INFURA](https://infura.io/) endpoint but the implementation is agnostic to the Ethereum node as all transaction signing happens locally.

## The DAML Ethereum Library

The [DAML Ethereum Library](https://github.com/liakakos/hemera/tree/master/src/daml/Ethereum) assumes a single Operator party and multiple User parties. Users send requests to the Operator in order to perform certain actions on the Ethereum network and get the results in the form of contract responses. The library is organized in a set of modules with distinct functionality. More specifically:

### 1. Onboarding
