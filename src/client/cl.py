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
  transport = TSocket.TSocket('localhost', 9999)

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
  #client.createUser('jwchen')
  #client.subscribe('mqin','jwchen')
  #client.printSubscribeName('mqin')
  #client.unsubscribe('mqin','jwchen')
  #client.printSubscribeName('mqin')
  client.post('mqin',"Hello guys")
  print "post sent"
  # Close!
  transport.close()
except AlreadyExistsException as tx:
  print "Already Exists Exception";

except NoSuchUserException as tx:
  print "No Such User Exception"

except NoSuchUserException as tx:
  print "No Such User Exception"

except TweetTooLongException as tx:
  print "Tweet Too Long Exception"

except NoSuchTweetException as tx:
  print("NoSuchTweetException")

except Thrift.TException, tx:
  print '%s' % (tx.user)
