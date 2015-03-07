#!/usr/bin/env python

import sys, time
import argparse

sys.path.append('gen-py')

from cse124 import Twitter
from cse124.ttypes import *

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

server = ""
port = ""

def do_RPC(service, handle, value=None):
    try:
        transport = TSocket.TSocket(server, port)

        # Buffering is critical. Raw sockets are very slow
        transport = TTransport.TBufferedTransport(transport)

        # Wrap in a protocol
        protocol = TBinaryProtocol.TBinaryProtocol(transport)

        # Create a client to use the protocol encoder
        client = Twitter.Client(protocol)
        # Connect!
        transport.open()

        ## supported services
        if service == "createUser":
            return client.createUser(handle)

        elif service == "subscribe":
            return client.subscribe(handle, value)

        elif service == "post":
            return client.post(handle, value)

        elif service == "readTweetsByUser":
            return client.readTweetsByUser(handle, int(value))

        elif service == "readTweetsByUser":
            return client.readTweetsByUser(handle, int(value))
               

        # Close!
        transport.close()
    ## supported exceptions
    except AlreadyExistsException, userx:
        return 'AlreadyExistsException'

    except NoSuchUserException:
        return 'NoSuchUserException'

    except TweetTooLongException:
        return 'TweetTooLongException'

    except Thrift.TException, tx:
        return '%s' % (tx.message)

    except Exception, userx:
        return 'ERROR:  %s' %(userx)


def do_tests():
    # createUser alice
    print "createUser for Alice .."
    if do_RPC('createUser', 'alice'):
        print 'Failed!'
    else: 
        print "Passed!"

    # createUser bob
    print "createUser for Bob .."
    if do_RPC('createUser', 'bob'):
        print 'Failed!'
    else: 
        print "Passed!"

    # createUser alice again!
    print "createUser for Alice .."
    if not (do_RPC('createUser','alice') == 'AlreadyExistsException'):
        print "Failed! Expecting \'AlreadyExistsException\'"
    else: 
        print "Passed!"

    # Bob posts a tweet 
    tweet_1 = "In high society, TCP is more welcome than UDP."
    print "Bob posts a tweet ..%s"%(tweet_1)
    if do_RPC('post','bob', tweet_1):
        print 'Failed!'
    else: 
        print "Passed!"
   
    time.sleep(2)

    # Bob posts a tweet 
    tweet_2 = " At least it knows a proper handshake."
    print "Bob posts a tweet ..%s"%(tweet_2)
    if do_RPC('post','bob', tweet_2):
        print 'Failed!'
    else: 
        print "Passed!"
  
    # Alice reads Bob's tweets
    print "Alice reads Bob's tweets"
    bob_tweets = do_RPC('readTweetsByUser','bob',10)        
    if not(bob_tweets[0].tweetString == tweet_2 and
           bob_tweets[1].tweetString == tweet_1):
         print "Failed!"     
    else: 
        print "Passed!"


if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Test createUser")
    parser.add_argument("host",  action="store", help="server:port", default="localhost:9090")
    args = parser.parse_args()
    server,port = args.host.split(":")
    do_tests()





