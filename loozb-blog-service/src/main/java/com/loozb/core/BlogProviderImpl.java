package com.loozb.core;

import com.alibaba.dubbo.config.annotation.Service;
import com.loozb.core.base.BaseProviderImpl;
import com.loozb.provider.IBlogProvider;

@Service(interfaceClass = IBlogProvider.class)
public class BlogProviderImpl extends BaseProviderImpl implements IBlogProvider {
	
}