package org.job.hunter.crawl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Test;

public class OTest {

	@Test
	public void testRegex(){
		String url ="http://jobs.51job.com/xian-xcq/74999531.html?s=0";
		String regex ="((http://search.51job.com/jobsearch/.+)|(http://jobs.51job.com/.+?html)|(http://jobs.51job.com/all/\\w+.html.*)"
				+ "|(http://jobs.51job.com/.+?/\\d+.html.*))";
		Pattern pattern =Pattern.compile(regex , Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher =pattern.matcher(url) ;
		if (matcher.find()) {
			System.out.println(matcher.groupCount());
			System.out.println(matcher.group(1));
		}
				
		System.out.println(url.matches(regex));
	}
}
