package org.job.hunter.crawl;

import org.junit.Test;

import us.codecraft.webmagic.Page;

public class JsonTest {

	@Test
	public void testJsonPath(){

		String jsonPath="$.id" ;
		jsonPath ="$.childs[1].name" ;
		String jsonText ="{id:1,childs:[{name:'child1'},{name:'child2'}]}";
		Page page = new Page();
		page.setRawText(jsonText) ;
		String    totalCountStr= page.getJson().jsonPath(jsonPath ) .toString();
		System.out.println( "totalCountStr :"+totalCountStr);
	}
}
