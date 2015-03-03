package edu.ucsd.cse124;

import java.util.List;
import java.util.HashSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Calendar; //for getTimeInMillis()
import edu.ucsd.cse124.Tweet;

public class TwitterHandler implements Twitter.Iface {
	
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
        long time = cal.getTimeInMillis() / 1000;
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
        return null;
    }

    @Override
    public List<Tweet> readTweetsBySubscription(String handle, int howmany)
        throws NoSuchUserException
    {
        return null;
    }

    @Override
    public void star(String handle, long tweetId) throws
        NoSuchUserException, NoSuchTweetException
    {
    }
}
