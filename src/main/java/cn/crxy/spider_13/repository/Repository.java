package cn.crxy.spider_13.repository;

public interface Repository {

	String poll();

	void add(String nextUrl);

}
