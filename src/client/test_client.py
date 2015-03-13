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

        elif service == "unsubscribe":
            return client.unsubscribe(handle, value)
        
        elif service == "post":
            return client.post(handle, value)

        elif service == "readTweetsByUser":
            return client.readTweetsByUser(handle, int(value))

        elif service == "readTweetsBySubscription":
            return client.readTweetsBySubscription(handle, int(value))

        elif service == "star":
            return client.star(handle, int(value))

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
        print 'Failed!!!!'
    else: 
        print "Passed!"

    # createUser bob
    print "createUser for Bob .."
    if do_RPC('createUser', 'bob'):
        print 'Failed!!!!'
    else: 
        print "Passed!"

	print "createUser for Jake .."
	if do_RPC('createUser', 'jake'):
		print 'Failed!!!!'
	else:
		print 'Passed!'

	print "Jake subscribe Alice and Bob .."
	if do_RPC('subscribe', 'jake', 'alice'):
		print 'Failed!!!!'
	else:
		print 'Passed!'
	if do_RPC('subscribe', 'jake', 'bob'):
		print 'Failed!!!!'
	else:
		print 'Passed!'


    # createUser alice again!
    print "createUser for Alice .."
    if not (do_RPC('createUser','alice') == 'AlreadyExistsException'):
        print "Failed!!!! Expecting \'AlreadyExistsException\'"
    else: 
        print "Passed!"

    # Bob posts a tweet 
    #tweet_1 = "In high society, TCP is more welcome than UDP."
	tweet_1 = "I am Bob post with 0"
    print "Bob posts a tweet ..%s"%(tweet_1)
    if do_RPC('post','bob', tweet_1):
        print 'Failed!!!!'
    else: 
        print "Passed!"
   
    time.sleep(2)

    # Bob posts a tweet 
    #tweet_2 = " At least it knows a proper handshake."
    tweet_2 = "I am Bob post with 1"
    print "Bob posts a tweet ..%s"%(tweet_2)
    if do_RPC('post','bob', tweet_2):
        print 'Failed!!!!'
    else: 
        print "Passed!"
  
    # Alice reads Bob's tweets
    print "Alice reads Bob's tweets 2"
    bob_tweets = do_RPC('readTweetsByUser','bob',10)        
    if not(bob_tweets[0].tweetString == tweet_2 and
           bob_tweets[1].tweetString == tweet_1):
        print "Failed!!!!"
    else: 
        print "Passed!"
	
	time.sleep(2)
	tweet_3 = "I am Alice post with 2."
	print "Alice posts the a tweet ..%s"%(tweet_3)
	if do_RPC('post', 'alice', tweet_3):
		print 'Failed!!!!'
	else:
		print 'Passed!'

	time.sleep(2)
	tweet_4 = "I am Alice post with 3."
	print "Alice posts the a tweet ..%s"%(tweet_4)
	if do_RPC('post', 'alice', tweet_4):
		print 'Failed!!!!'
	else:
		print 'Passed!'

	time.sleep(2)
	tweet_5 = "I am Bob post with 4."
	print "Bob posts the a tweet ..%s"%(tweet_5)
	if do_RPC('post', 'bob', tweet_5):
		print 'Failed!!!!'
	else:
		print 'Passed!'

	print "Jake read his subscribers' tweets"
	Jake_tweets = do_RPC('readTweetsBySubscription','jake', 7)
	for tweets in Jake_tweets:
		print tweets
	if not(len(Jake_tweets) == 5 and
	Jake_tweets[0].tweetString == tweet_5 and 
	Jake_tweets[1].tweetString == tweet_4 and 
	Jake_tweets[2].tweetString == tweet_3 and 
	Jake_tweets[3].tweetString == tweet_2 and 
	Jake_tweets[4].tweetString == tweet_1):
		print "Failed!!!!"
	else:
		print "Passed!"

    # SUBSCRIBE AND UNSUBSCRIBE
    print "Testing subscribe and unsubscribe"
    if do_RPC('unsubscribe', 'jake', 'bob'):
        print 'Failed!!!!'
    else:
        print 'Passed!'
    print "now read the tweet"
    Jake_tweets = do_RPC('readTweetsBySubscription','jake', 7)
    for tweets in Jake_tweets:
        print tweets
    if not(len(Jake_tweets) == 2 and
               Jake_tweets[0].tweetString == tweet_4 and
               Jake_tweets[1].tweetString == tweet_3):
        print "Failed!!!!"
    else:
        print "Passed!"

    #Testing "like"
    print "now no one like any tweet"
    if not(Jake_tweets[0].numStars == 0):
        print "Failed!!!!"
    else:
        print "Passed!"

    print "like it"
    if do_RPC('star', 'jake', Jake_tweets[0].tweetId):
        print 'Failed!!!!'
    else:
        print 'Passed!'
    Jake_tweets = do_RPC('readTweetsBySubscription','jake', 7)
    if not(Jake_tweets[0].numStars == 1):
        print "Failed!!!!"
    else:
        print "Passed!"

    print "like it again"
    if do_RPC('star', 'jake', Jake_tweets[0].tweetId):
        print 'Failed!!!!'
    else:
        print 'Passed!'
    Jake_tweets = do_RPC('readTweetsBySubscription','jake', 7)
    if not(Jake_tweets[0].numStars == 1):
        print "Failed!!!!"
    else:
        print "Passed!"

    print "some one else like it"
    if do_RPC('star', 'alice', Jake_tweets[0].tweetId):
        print 'Failed!!!!'
    else:
        print 'Passed!'
    Jake_tweets = do_RPC('readTweetsBySubscription','jake', 7)
    if not(Jake_tweets[0].numStars == 2):
        print "Failed!!!!"
    else:
        print "Passed!"

    print "post null tweet"
    tweet_1 = ""
    if do_RPC('post','bob', tweet_1):
        print 'Failed!!!!'
    else:
        print "Passed!"

    print "post tweet with all spaces"
    tweet_1 = "               "
    if do_RPC('post','bob', tweet_1):
        print 'Failed!!!!'
    else:
        print "Passed!"



if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="Test createUser")
    parser.add_argument("host",  action="store", help="server:port", default="localhost:9090")
    args = parser.parse_args()
    server,port = args.host.split(":")
    do_tests()





