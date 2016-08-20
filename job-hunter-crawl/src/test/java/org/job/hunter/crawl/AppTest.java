package org.job.hunter.crawl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Unit test for simple App.
 */
public class AppTest extends TestCase {
	 
	public static void main(String[] args) {
		String url ="http://www.51job.com/en/shenzhen#23" ;
		
		String regex="http://www.51job.com/.+"; ;
		System.out.println(url.matches(regex));
		
		regex="http://www.51job.com/((sitemap/.+)|([\\w\\d#\\.\\?&=]+))" ;
//		regex="http://www.51job.com/[\\w\\d#\\.\\?&]+" ;
		System.out.println(url.matches(regex));
		url ="http://www.51job.com/dd#23" ;
		System.out.println(url.matches(regex));
	}
}
