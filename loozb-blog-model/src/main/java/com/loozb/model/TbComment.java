package com.loozb.model;

import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableName;
import com.loozb.core.base.BaseModel;


/**
 * <p>
 * 评论
 * </p>
 *
 * @author 龙召碧
 * @since 2017-03-27
 */
@TableName("tb_comment")
public class TbComment extends BaseModel {

    private static final long serialVersionUID = 1L;

	private Long pid;
    /**
     * 文章ID
     */
	@TableField("article_id")
	private Long articleId;
    /**
     * 被回复人
     */
	@TableField("by_reply")
	private String byReply;
    /**
     * 评论内容
     */
	private String content;
    /**
     * 回复人
     */
	private String reply;

	/**
	 * 邮箱
	 */
	private String email;

	@TableField( exist = false)
	private String articleId_;


	public Long getPid() {
		return pid;
	}

	public void setPid(Long pid) {
		this.pid = pid;
	}

	public Long getArticleId() {
		return articleId;
	}

	public void setArticleId(Long articleId) {
		this.articleId = articleId;
	}

	public String getByReply() {
		return byReply;
	}

	public void setByReply(String byReply) {
		this.byReply = byReply;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getReply() {
		return reply;
	}

	public void setReply(String reply) {
		this.reply = reply;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getArticleId_() {
		return articleId + "";
	}
}
