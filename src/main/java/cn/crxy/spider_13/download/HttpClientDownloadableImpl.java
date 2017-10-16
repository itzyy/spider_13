package cn.crxy.spider_13.download;

import cn.crxy.spider_13.domain.Page;
import cn.crxy.spider_13.utils.PageUtils;

public class HttpClientDownloadableImpl implements Downloadable {

	@Override
	public Page download(String url) {
		Page page = new Page();
		String content = PageUtils.getContent(url);
		page.setContent(content);
		page.setUrl(url);
		return page;
	}

}
