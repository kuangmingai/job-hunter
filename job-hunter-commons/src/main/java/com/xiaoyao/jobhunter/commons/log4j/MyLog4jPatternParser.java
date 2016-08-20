package com.xiaoyao.jobhunter.commons.log4j;

import org.apache.log4j.helpers.FormattingInfo;
import org.apache.log4j.helpers.PatternConverter;
import org.apache.log4j.helpers.PatternParser;
import org.apache.log4j.spi.LoggingEvent;

public class MyLog4jPatternParser extends PatternParser {

	public MyLog4jPatternParser(String pattern) {
		super(pattern);
	}

	/**
	 * 重写finalizeConverter，对特定的占位符进行处理，T表示线程ID占位符
	 */
	@Override
	protected void finalizeConverter(char c) {
		if (c == 'T') {
			this.addConverter(new ExPatternConverter(this.formattingInfo));
		} else {
			super.finalizeConverter(c);
		}
	}

	private static class ExPatternConverter extends PatternConverter {

		public ExPatternConverter(FormattingInfo fi) {
			super(fi);
		}

		/**
		 * 当需要显示线程ID的时候，返回当前调用线程的ID
		 */
		@Override
		protected String convert(LoggingEvent event) {
			return String.valueOf(Thread.currentThread().getId());
		}
	}
}
