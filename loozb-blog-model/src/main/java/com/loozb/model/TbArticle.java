package com.loozb.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.loozb.core.base.BaseModel;


/**
 * <p>
 * 博客列表
 * </p>
 *
 * @author 龙召碧
 * @since 2017-03-22
 */
@TableName("tb_article")
public class TbArticle extends BaseModel {

    private static final long serialVersionUID = 1L;

    /**
     * 标题
     */
	private String title;
    /**
     * 内容
     */
	private String content;
    /**
     * 作者
     */
	private String author;
    /**
     * 原文url
     */
	@TableField("original_url")
	private String originalUrl;
    /**
     * 文章分类-多个以逗号分隔
     */
	private String classification;
    /**
     * 摘要-发表文章时生成插入
     */
	private String digest;
    /**
     * 阅读次数
     */
	@TableField("read_num")
	private Integer readNum;
    /**
     * 排序-默认为0，当大于0时，设为置顶状态，并根据降序排序
     */
	private Integer sort;
    /**
     * 文章类型-1.原创，2.转载
     */
	private String type;

	/**
	 * 文章类型-1.已确认，0.未确认
	 */
	private String confirm;


	/**
	 * 分类名称
	 */
	@TableField(exist = false)
	private String classificationName;


	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getOriginalUrl() {
		return originalUrl;
	}

	public void setOriginalUrl(String originalUrl) {
		this.originalUrl = originalUrl;
	}

	public String getClassification() {
		return classification;
	}

	public void setClassification(String classification) {
		this.classification = classification;
	}

	public String getDigest() {
		return digest;
	}

	public void setDigest(String digest) {
		this.digest = digest;
	}

	public Integer getReadNum() {
		return readNum;
	}

	public void setReadNum(Integer readNum) {
		this.readNum = readNum;
	}

	public Integer getSort() {
		return sort;
	}

	public void setSort(Integer sort) {
		this.sort = sort;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getConfirm() {
		return confirm;
	}

	public void setConfirm(String confirm) {
		this.confirm = confirm;
	}

	public String getClassificationName() {
		return classificationName;
	}

	public void setClassificationName(String classificationName) {
		this.classificationName = classificationName;
	}
}
