# CSE 124's Project 3
Winter 2015

-------------

Team members:

Ming Qin <mingqin@ucsd.edu>

Jiawei Chen <jic023@eng.ucsd.edu>

-------------

Instructor: George Porter <gmporter@cs.ucsd.edu>

--------------

## To build and run the server:

```
$ cd src/server
$ thrift --gen java ../thrift/Twitter.thrift
$ ant
$ ./run-server.sh
```

## To build and run the client:

```
$ cd src/client
$ thrift --gen py ../thrift/Twitter.thrift
$ ./test_client.py localhost:9090
```

Additional test codes:

```
$ ./cl.py
$ ./unitTest.py
```