package com.loozb.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.loozb.core.base.BaseModel;


/**
 * <p>
 * 文章分类
 * </p>
 *
 * @author 龙召碧
 * @since 2017-03-25
 */
@TableName("tb_classification")
public class TbClassification extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 分类名称
     */
	private String name;

	/**
	 * 分类url
	 */
	private String url;

	/**
	 * 分类描述
	 */
	private String description;

	/**
	 * 文章数量
	 */
	@TableField(exist = false)
	private Integer articleNum;


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getArticleNum() {
		return articleNum;
	}

	public void setArticleNum(Integer articleNum) {
		this.articleNum = articleNum;
	}
}
