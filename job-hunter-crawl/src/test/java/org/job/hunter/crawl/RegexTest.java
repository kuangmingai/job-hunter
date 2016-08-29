package org.job.hunter.crawl;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Node;
import org.junit.Test;
import org.omg.CORBA.portable.Delegate;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.selector.Html;
import us.codecraft.webmagic.selector.PlainText;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.selector.Xpath2Selector;

public class RegexTest {

	@Test
	public void testRegex() {
		String url = "http://jobs.51job.com/xian-xcq/74999531.html?s=0";
		String regex = "((http://search.51job.com/jobsearch/.+)|(http://jobs.51job.com/.+?html)|(http://jobs.51job.com/all/\\w+.html.*)"
				+ "|(http://jobs.51job.com/.+?/\\d+.html.*))";
		Pattern pattern = Pattern.compile(regex, Pattern.DOTALL | Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(url);
		if (matcher.find()) {
			System.out.println(matcher.groupCount());
			System.out.println(matcher.group(1));
		}

		System.out.println(url.matches(regex));
	}

	@Test
	public void testXpath() throws Exception {
		String xml = "<html><span class='sp4'><em class='i3'></em>招聘1人</span></html>";
		Document document = DocumentHelper.parseText(xml);
		String xpath = "//span[@class='sp4' and contains(text(),'招聘')]/text()";
		xpath = "//span[@class='sp4']/em[@class='i3']/parent::span";
		// xpath = "//span[@class='sp4' and
		// contains(text(),'(本科)|(招聘)')]/text()";
		List<Node> list = document.selectNodes(xpath);
		System.out.println(list.size());
		for (Node node : list) {
			System.out.println(node.getText());
		}
	}

	@Test
	public void testXpath2() {
		String regex = "//span[@class='sp4']/em[@class='i1']/parent::span/text()";
//		 regex = "//span[@class='sp4']/following-sibling::*";
//		 regex = "//span[@class='sp4']/em[@class='i1']/parent/text()";
		Xpath2Selector xpath2Selector = new Xpath2Selector(regex);
		Page page = new Page();
		String rawText = "<html><span class='sp4'><em class='i1'></em>招聘1人</span><b>b node</b></html>";
		page.setRawText(rawText);
		page.setUrl(new PlainText("http://www.baidu.com"));
		page.setHtml(new Html(rawText));
		Selectable selectable = page.getHtml().select(xpath2Selector);
		System.out.println(selectable.toString());
	}

}
