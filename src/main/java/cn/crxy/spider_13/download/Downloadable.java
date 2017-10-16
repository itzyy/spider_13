package cn.crxy.spider_13.download;

import cn.crxy.spider_13.domain.Page;

public interface Downloadable {
	
	public Page download(String url);

}
