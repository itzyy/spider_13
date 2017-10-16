package cn.crxy.spider_13.store;

import cn.crxy.spider_13.domain.Page;

public class ConsoleStoreableImpl implements Storeable {

	@Override
	public void store(Page page) {
		System.out.println(page.getUrl()+"----"+page.getValues().get("price"));
	}

}
