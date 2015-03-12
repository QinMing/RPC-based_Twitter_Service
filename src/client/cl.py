#!/usr/bin/env python

import sys

sys.path.append('gen-py')

from cse124 import Twitter
from cse124.ttypes import *

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

try:
  transport = TSocket.TSocket('localhost', 9090)

  # Buffering is critical. Raw sockets are very slow
  transport = TTransport.TBufferedTransport(transport)

  # Wrap in a protocol
  protocol = TBinaryProtocol.TBinaryProtocol(transport)

  # Create a client to use the protocol encoder
  client = Twitter.Client(protocol)

  # Connect!
  transport.open()

  client.ping()
  client.createUser('jwchen')
  client.createUser('mqin')
  client.createUser('porter')
  #client.createUser('jwchen')
  client.subscribe('mqin','jwchen')
  client.subscribe('mqin','porter')
  #client.printSubscribeName('mqin')
  #client.unsubscribe('mqin','jwchen')
  #client.printSubscribeName('mqin')

  client.post('porter',"porter -1")
  client.post('jwchen',"jwchen 0")
  client.post('jwchen',"jwchen 1")
  client.post('porter',"porter 2")
  client.post('jwchen',"jwchen 3")
  client.post('jwchen',"porter 4")
  #client.post('jwchen',"Facebook intern success")
  tweets = client.readTweetsBySubscription('mqin', 100);
  tweets = client.readTweetsBySubscription('mqin', 2);
  #tweets = client.readTweetsByUser('jwchen',4)
  for item in tweets:
	print item.tweetString

  client.star('mqin',tweets[0].tweetId)
  client.star('mqin',tweets[0].tweetId)
  client.star('jwchen',tweets[0].tweetId)

  #print "post sent"
  # Close!
  transport.close()


except TweetTooLongException as tx:
  print "Tweet Too Long Exception"
except NoSuchTweetException as tx:
  print("NoSuchTweetException")
except AlreadyExistsException, tx:
  print '%s' % (tx.user)
  print 'Already Exists Exception'
except NoSuchUserException, tx:
  print '%s' % (tx.user)
  print "No Such User Exception"
except Thrift.TException, tx:
  print '%s' % (tx.message)
