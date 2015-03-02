package edu.ucsd.cse124;

import java.util.List;
import java.util.HashSet;
import java.util.HashMap;

public class TwitterHandler implements Twitter.Iface {
	
	private HashSet<String> userName = new HashSet<String>();
	//private HashMap<int, Tweet>  userAccount = new HashSet<int, Tweet>();
	private HashMap<String,List<String> > userSubscribe = new HashMap<String,List<String> >();

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
		List<String> subscribeList = new List<String>();
		userSubscribe.put(handle, subscribeList);
		System.out.println("test create User");
    }

    @Override
    public void subscribe(String handle, String theirhandle)
        throws NoSuchUserException
    {
		System.out.println("user name:            " + handle);
		System.out.println("subscribed user name: " + theirhandle);
		if (userName.contains(handle) == false) {
			NoSuchUserException e = new NoSuchUserException(handle);
			throw e;
		}
		if (userName.contains(theirhandle) == false) {
			NoSuchUserException e = new NoSuchUserException(theirhandle);
			throw e;
		}
		System.out.println("name check");
		List<String> userSubList = userSubscribe.get(handle);
		userSubList.add(theirhandle);
		System.out.println("test subscribe user");
    }

    @Override
    public void unsubscribe(String handle, String theirhandle)
        throws NoSuchUserException
    {
    }

    @Override
    public void post(String handle, String tweetString)
        throws NoSuchUserException, TweetTooLongException
    {
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
