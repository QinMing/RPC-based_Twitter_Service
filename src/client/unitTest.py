#!/usr/bin/env python

import sys
import unittest

sys.path.append('gen-py')

from cse124 import Twitter
from cse124.ttypes import *

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

class TestTwitterServer(unittest.TestCase):
  def setUp(self):
    try:
      self.transport = TSocket.TSocket('localhost', 9999)

      # Buffering is critical. Raw sockets are very slow
      self.transport = TTransport.TBufferedTransport(self.transport)

      # Wrap in a protocol
      self.protocol = TBinaryProtocol.TBinaryProtocol(self.transport)

      # Create a client to use the protocol encoder
      self.client = Twitter.Client(self.protocol)

      # Connect!
      self.transport.open()

    except Thrift.TException, tx:
      print '%s' % (tx.message)

  def tearDown(self):
    # Close!
    self.transport.close()


  # TEST CASES


#  def test_readTweet(self):
#    tweets = self.client.readTweetsBySubscription('mqin', 6);
#    for item in tweets:
#      print item.tweetString
#    #results in output line
#
#  def test_starTwice(self):
#    #read Ming's subscription
#    tweets = self.client.readTweetsBySubscription('mqin', 1);
#    #Now Ming think he like it, but clicked 3 times
#    self.client.star('mqin',tweets[0].tweetId);
#    self.client.star('mqin',tweets[0].tweetId);
#    self.client.star('mqin',tweets[0].tweetId);
#    #Now Porter likes it as well
#    self.client.star('porter',tweets[0].tweetId);
#    #results in server side

  def test_AlreadyExistsException(self):
    with self.assertRaises(AlreadyExistsException) as e:
      self.client.createUser('mqin')
    

  def test_NoSuchUserException(self):
    with self.assertRaises(NoSuchUserException) as e1:
      self.client.subscribe('mqin','God')

  def test_NoSuchTweetException(self):
    with self.assertRaises(NoSuchTweetException) as e2:
      self.client.star('mqin',1000);
    

  def test_TweetTooLongException(self):
    with self.assertRaises(TweetTooLongException) as e3:
      self.client.post('mqin',"""\
I'm insane I'm insane I'm insane I'm insane I'm insane I'm insane 
I'm insane I'm insane I'm insane I'm insane I'm insane I'm insane 
I'm insane I'm insane I'm insane I'm insane I'm insane """)

if 1:
  if __name__ == '__main__' :
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
      
      # Basic set up
      client.createUser('jwchen')
      client.createUser('mqin')
      client.createUser('porter')
      
      client.subscribe('mqin','jwchen')
      client.subscribe('mqin','porter')
      
      client.post('porter',"porter -1")
      client.post('jwchen',"jwchen 0")
      client.post('jwchen',"jwchen 1")
      client.post('porter',"porter 2")
      client.post('jwchen',"jwchen 3")
      client.post('jwchen',"porter 4")
      
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
      
    #    unittest.main
    suite = unittest.TestLoader().loadTestsFromTestCase(TestTwitterServer)
    unittest.TextTestRunner(verbosity=2).run(suite)


