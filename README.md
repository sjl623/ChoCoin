# ChoCoin
A digital coin based on block chain technology,like Bitcoin.

- **Build Tool**:[Maven](https://maven.apache.org/)
- **Version Control**:[Git](https://git-scm.com/book/zh/v2)

## Project structure
The project's file structure as follows:\
├── Account\
├── BlockChain\
├── FullNode\
├── LightNode\
├── Pow\
├── Transaction\
└── Util\
Here are more details about each directory\

- **Full(Light)Node** Here are the source of the full node or light node client.After built by maven,a jar executable file should got.Then the one who participate in our game could run it.\
In other word,we would get two .jar file in the end.
As for the different between full node and light node,click [here](#node_different) to learn.

- **BlockChain** The directory include the definition about block and block chain.

- **Pow** What is Pow?It is proof of work.One node should prove it is working hard to get the award,such as some coin.\
The directory include the source code about it.

- **Transaction** Implementation of transfer,balance inquire.

- **Account** The define of an account and some function about account,such as generate an new account.

- **Util**
Some interface about data structure or algorithm that would be used by other part.

## Algorithm 
- **Hash**  [sha256](https://xorbin.com/tools/sha256-hash-calculator)
- **Encryption** [RSA](https://en.wikipedia.org/wiki/RSA_(cryptosystem\) )

# What is the difference between full node and light node?
<span id="node_different"></span>
In brief,a miner should run a full node,which keep all the transaction data of the system.But a light node is enough for the one who only want to launch a transaction.



