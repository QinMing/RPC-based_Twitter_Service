package edu.ucsd.cse124;

import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.PriorityQueue;
import java.util.Comparator;
import java.util.Calendar;

public class TwitterHandler implements Twitter.Iface {
	
	public class TweetDateComparator implements Comparator<Tweet> {

		@Override
		public int compare(Tweet t1, Tweet t2) {
			if (t1.posted < t2.posted)
				return 1;
			if (t1.posted == t2.posted)
				return 0;
			return -1;
		}

	}

	
	private HashSet<String> userName = new HashSet<String>();
	//private HashMap<int, Tweet>  userAccount = new HashSet<int, Tweet>();
	private HashMap<String,LinkedList<String> > userSubscribeMap =
            new HashMap<String,LinkedList<String> >();
    
    public static final int MaxTweetLength = 140;
    private int nextTweetID = 0;
    private HashMap<String,LinkedList<Tweet> > userTweetMap =
            new HashMap<String,LinkedList<Tweet> >();
    

    @Override
    public void ping() {
		System.out.println("This is server ping()");
    }

    @Override
    public void createUser(String handle) throws AlreadyExistsException
    {	
		System.out.println(handle);
		if (userName.add(handle) == false) {
			AlreadyExistsException e = new AlreadyExistsException(handle);
			throw e;
		}
		LinkedList<String> subscribeList = new LinkedList<String>();
		userSubscribeMap.put(handle, subscribeList);
        
        LinkedList<Tweet> tweetList = new LinkedList<Tweet>();
        userTweetMap.put(handle, tweetList);
        
		System.out.println("Created User");
    }

	private void checkUserExist(String handle) 
		throws NoSuchUserException	  
	{
		if (userName.contains(handle) == false) {
			NoSuchUserException e = new NoSuchUserException(handle);
			throw e;
		}
	}

	@Override
	public void printSubscribeName(String handle)
		throws NoSuchUserException
	{
		checkUserExist(handle);
		LinkedList<String> subscribeList = userSubscribeMap.get(handle);
		ListIterator<String> listIterator = subscribeList.listIterator();
		System.out.println("Print Subscribe List");
	    while (listIterator.hasNext()) {
	    	System.out.println("Subscribed: " + listIterator.next());
	    }
		System.out.println("Print Complete");
	}

    @Override
    public void subscribe(String handle, String theirhandle)
        throws NoSuchUserException
    {
		System.out.println("user name:            " + handle);
		System.out.println("subscribed user name: " + theirhandle);
		checkUserExist(handle);
		checkUserExist(theirhandle);
		LinkedList<String> userSubList = userSubscribeMap.get(handle);
		userSubList.add(theirhandle);
		System.out.println("test subscribe user");
    }

    @Override
    public void unsubscribe(String handle, String theirhandle)
        throws NoSuchUserException
    {
		System.out.println("user name:            " + handle);
		System.out.println("subscribed user name: " + theirhandle);
		checkUserExist(handle);
		checkUserExist(theirhandle);
		LinkedList<String> userSubList = userSubscribeMap.get(handle);
		userSubList.remove(theirhandle);
    }

    @Override
    public void post(String handle, String tweetString)
        throws NoSuchUserException, TweetTooLongException
    {

		checkUserExist(handle);
        if (tweetString.length()>MaxTweetLength){
            throw new TweetTooLongException();
        }

        //create the tweet
        ++nextTweetID;
        Calendar cal = Calendar.getInstance();
        long time = cal.getTimeInMillis();
        Tweet t = new Tweet(nextTweetID, handle, time, 0, tweetString);
        
	//append it to the user's tweet list
        LinkedList<Tweet> userTweet = userTweetMap.get(handle);
        userTweet.addFirst(t);
        System.out.println("Tweet posted.");
    }

    @Override
    public List<Tweet> readTweetsByUser(String handle, int howmany)
        throws NoSuchUserException
    {
		checkUserExist(handle);
		if (howmany <= 0)
			return new LinkedList<Tweet>();
		LinkedList<Tweet> tweetList = userTweetMap.get(handle);
		ListIterator<Tweet> it = tweetList.listIterator();
		LinkedList<Tweet> result = new LinkedList<Tweet>();
		int count = 0;
		while (it.hasNext() && count < howmany) {
			result.addLast(it.next());
			count++;
		}	
        return result;
    }

    @Override
    public List<Tweet> readTweetsBySubscription(String handle, int howmany)
        throws NoSuchUserException
    {
		checkUserExist(handle);
		if (howmany <= 0)
			return new LinkedList<Tweet>();
		LinkedList<Tweet> result = new LinkedList<Tweet>();

		// list of subscribe users
		LinkedList<String> subscribeList = userSubscribeMap.get(handle);
		ListIterator<String> it = subscribeList.listIterator();
		int numSubscribe = subscribeList.size();
		HashMap<String, ListIterator<Tweet> > itMap = new HashMap<String, ListIterator<Tweet> >();
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
        return result;
    }

    @Override
    public void star(String handle, long tweetId) throws
        NoSuchUserException, NoSuchTweetException
    {
    }
}
