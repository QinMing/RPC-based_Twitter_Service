package edu.ucsd.cse124;

import java.util.Arrays; // for Arrays.toList()
import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Calendar; //for getTimeInMillis()
import java.util.PriorityQueue;
import java.util.Comparator;

public class TwitterHandler implements Twitter.Iface {
    
    private class TweetRich extends Tweet {
        
        public HashSet<String> likedUsers;
        
        public TweetRich(
                         long tweetId,
                         String handle,
                         long posted,
                         int numStars,
                         String tweetString) {
            super(tweetId, handle, posted, numStars, tweetString);
            this.likedUsers = new HashSet<String>();
        }
    }
    
    public class TweetDateComparator implements Comparator<Tweet> {
        
        @Override
        public int compare(Tweet t1, Tweet t2) {
            if (t1.posted < t2.posted) {
                return 1;
            }
            if (t1.posted == t2.posted) {
                return 0;
            }
            return -1;
        }
        
    }
    
    private HashSet<String> userName = new HashSet<String>();
    private HashMap<String, LinkedList<String>> userSubscribeMap
    = new HashMap<String, LinkedList<String>>();
    
    public static final int MaxTweetLength = 140;
    private long nextTweetID = 0;
    //Every user has a list of tweet
    private HashMap<String, LinkedList<TweetRich>> userTweetMap
    = new HashMap<String, LinkedList<TweetRich>>();
    //Map tweet ID  to the tweet
    private HashMap<Long, TweetRich> tweetReg
    = new HashMap<Long, TweetRich>();
    
    @Override
    public void ping() {
        System.out.println("This is server ping()");
    }
    
    @Override
    public void createUser(String handle) throws AlreadyExistsException {
        
        synchronized(this){
            
            if (userName.add(handle) == false) {
                System.out.println("Fail to create ["+handle+"]. Already exist");
                AlreadyExistsException e = new AlreadyExistsException(handle);
                throw e;
            }
            LinkedList<String> subscribeList = new LinkedList<String>();
            userSubscribeMap.put(handle, subscribeList);
            
            LinkedList<TweetRich> tweetList = new LinkedList<TweetRich>();
            userTweetMap.put(handle, tweetList);
            
            System.out.println("Created User ["+handle+"]");
            
        }
    }
    
    private void checkUserExist(String handle)
    throws NoSuchUserException {
        if (userName.contains(handle) == false) {
            System.out.println("NoSuchUserException ("+handle+")");
            NoSuchUserException e = new NoSuchUserException(handle);
            throw e;
        }
    }
    
    private void checkTweetExist(long id)
    throws NoSuchTweetException {
        if (tweetReg.containsKey(new Long(id)) == false) {
            System.out.println("NoSuchTweetException ("+Long.toString(id)+")");
            throw new NoSuchTweetException();
        }
        
    }
    
    @Override
    public void printSubscribeName(String handle)
    throws NoSuchUserException {
        
        synchronized(this){
            checkUserExist(handle);
            LinkedList<String> subscribeList = userSubscribeMap.get(handle);
            ListIterator<String> listIterator = subscribeList.listIterator();
            System.out.println("Print Subscribe List ...");
            while (listIterator.hasNext()) {
                System.out.println("Subscribed: " + listIterator.next());
            }
            System.out.println("Print Complete");
        }
        
    }
    
    private void printTweetList(List<Tweet> l) {
        for (Tweet t : l) {
            System.out.print("{time=");
            System.out.print(t.posted);
            System.out.print(",user=");
            System.out.print(t.handle);
            System.out.print(",string=");
            System.out.print(t.tweetString);
            System.out.println("}");
        }
    }
    
    @Override
    public void subscribe(String handle, String theirhandle)
    throws NoSuchUserException {
        
        synchronized(this){
            checkUserExist(handle);
            checkUserExist(theirhandle);
            LinkedList<String> userSubList = userSubscribeMap.get(handle);
            userSubList.add(theirhandle);
            System.out.print("user [" + handle);
            System.out.println("] subscribed [" + theirhandle + "]");
        }
        
    }
    
    @Override
    public void unsubscribe(String handle, String theirhandle)
    throws NoSuchUserException {
        
        synchronized(this){
            checkUserExist(handle);
            checkUserExist(theirhandle);
            LinkedList<String> userSubList = userSubscribeMap.get(handle);
            userSubList.remove(theirhandle);
            System.out.print("user [" + handle);
            System.out.println("] unsubscribed [" + theirhandle + "]");
        }
        
    }
    
    @Override
    public void post(String handle, String tweetString)
    throws NoSuchUserException, TweetTooLongException {
        
        synchronized(this){
            checkUserExist(handle);
            if (tweetString.length() > MaxTweetLength) {
                throw new TweetTooLongException();
            }
            
            //create the tweet
            Calendar cal = Calendar.getInstance();
            long time = cal.getTimeInMillis();
            
            TweetRich t = new TweetRich(nextTweetID, handle, time, 0, tweetString);
            tweetReg.put(new Long(nextTweetID), t);
            
            //append it to the user's tweet list
            LinkedList<TweetRich> userTweet = userTweetMap.get(handle);
            userTweet.addFirst(t);
            
            ++nextTweetID;
            System.out.print("Tweet posted: ");
            printTweetList(new LinkedList<Tweet>(Arrays.asList(t)));
        }
        
    }
    
    @Override
    public List<Tweet> readTweetsByUser(String handle, int howmany)
    throws NoSuchUserException {
        
        synchronized(this){
            checkUserExist(handle);
            if (howmany <= 0) {
                return new LinkedList<Tweet>();
            }
            LinkedList<TweetRich> tweetList = userTweetMap.get(handle);
            ListIterator<TweetRich> it = tweetList.listIterator();
            LinkedList<Tweet> result = new LinkedList<Tweet>();
            int count = 0;
            while (it.hasNext() && count < howmany) {
                result.addLast(it.next());
                count++;
            }
            System.out.println("read " + Integer.toString(howmany) + " tweets by user ["+handle+"]:");
            printTweetList(result);
            return result;
        }
        
    }
    
    @Override
    public List<Tweet> readTweetsBySubscription(String handle, int howmany)
    throws NoSuchUserException {
        
        synchronized(this){
            checkUserExist(handle);
            if (howmany <= 0) {
                return new LinkedList<Tweet>();
            }
            LinkedList<Tweet> result = new LinkedList<Tweet>();
            
            // list of subscribe users
            LinkedList<String> subscribeList = userSubscribeMap.get(handle);
            ListIterator<String> it = subscribeList.listIterator();
            int numSubscribe = subscribeList.size();
            HashMap<String, ListIterator<TweetRich>> itMap
            = new HashMap<String, ListIterator<TweetRich>>();
            Comparator<Tweet> comparator = new TweetDateComparator();
            PriorityQueue<Tweet> tweetPriorityQueue = new PriorityQueue<Tweet>(numSubscribe, comparator);
            String temp;
            // add all iterators for subscribed users with tweets
            for (int i = 0; i < numSubscribe; i++) {
                temp = it.next();
                if (!userTweetMap.get(temp).isEmpty()) {
                    itMap.put(temp, userTweetMap.get(temp).listIterator());
                    tweetPriorityQueue.add(itMap.get(temp).next());
                }
            }
            int count = 0;
            // tweet with latest posted time
            Tweet tempTweet;
            // Add iterator element to the priority queue
            // If no more tweet for any user, remove its iterator from the arraylist
            while (count < howmany && !tweetPriorityQueue.isEmpty()) {
                tempTweet = tweetPriorityQueue.remove();
                result.addLast(tempTweet);
                if (itMap.get(tempTweet.handle).hasNext()) {
                    tweetPriorityQueue.add(itMap.get(tempTweet.handle).next());
                }
                count++;
            }
            
            System.out.println("read " + Integer.toString(howmany) + " by subscription of ["+handle+"]:");
            printTweetList(result);
            
            return result;
        }
        
    }
    
    @Override
    public void star(String handle, long tweetId) throws
    NoSuchUserException, NoSuchTweetException {
        
        synchronized(this){
            checkUserExist(handle);
            checkTweetExist(tweetId);
            TweetRich t = tweetReg.get(new Long(tweetId));
            t.likedUsers.add(handle);
            t.numStars = t.likedUsers.size();
            
            System.out.print(handle);
            System.out.print(" liked Tweet ");
            System.out.print(tweetId);
            System.out.print(", which is liked ");
            System.out.print(t.numStars);
            System.out.println(" times");
        }
        
    }
}
