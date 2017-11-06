package com.wxb.utils;

import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.parser.Tag;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import com.jfinal.kit.StrKit;

/**
 * @title html操作工具类
 * @description 用来过滤一些html边去，防止xss攻击等
 * @version 1.0
 * @created 2015年3月20日 下午12:11:24
 */
public class HtmlFilter {
	
	// 只有纯文本可以通过
	public static String getText(String html) {
		if (html == null)
			return null;
		return Jsoup.clean(html, Whitelist.none());
	}
	
	// 以下标签可以通过
	// b, em, i, strong, u. 纯文本 
	public static String getSimpleHtml(String html) {
		if (html == null)
			return null;
		return Jsoup.clean(html, Whitelist.simpleText());
	}
	
	// 以下标签可以通过
	//a, b, blockquote, br, cite, code, dd, dl, dt, em, i, li, ol, p, pre, q, small, strike, strong, sub, sup, u, ul
	public static String getBasicHtml(String html) {
		if (html == null)
			return null;
		return Jsoup.clean(html, Whitelist.basic());
	}
	
	//在basic基础上  增加图片通过
	public static String getBasicHtmlandimage(String html) {
		if (html == null)
			return null;
		return Jsoup.clean(html, Whitelist.basicWithImages());
	}
	// 以下标签可以通过	
	//a, b, blockquote, br, caption, cite, code, col, colgroup, dd, dl, dt, em, h1, h2, h3, h4, h5, h6, i, img, li, ol, p, pre, q, small, strike, strong, sub, sup, table, tbody, td, tfoot, th, thead, tr, u, ul
	public static String getFullHtml(String html) {
		if (html == null)
			return null;
		return Jsoup.clean(html, Whitelist.relaxed());
	}

	//只允许指定的html标签
	public static String clearTags(String html, String ...tags) {
		Whitelist wl = new Whitelist();
		return Jsoup.clean(html, wl.addTags(tags));
	}

	// 对关键字加上颜色
	public static String markKeywods (String keywords, String target) {
		if (StrKit.notBlank(keywords)) {
			String[] arr = keywords.split(" ");
			for (String s : arr) {
				if (StrKit.notBlank(s)) {
					String temp = "<span class=\"highlight\">" + s + "</span>";
					if(temp!=null)
						target = target.replaceAll(s, temp);
				}
			}
		}
		return target;
	}

	// 获取html中的 所有图片
	public static List<String> getImgSrcList(String html) {
		// 2015-04-09 L.cm 避免出现空指针的问题
		List<String> list = new ArrayList<String>();
		if (StrKit.isBlank(html)) {
			return list;
		}
		Document doc = Jsoup.parseBodyFragment(html);
		Elements es = doc.select("img");
		if(es != null && es.size() > 0){
			for(Element e : es){
				list.add(e.attr("src"));
			}
		}
		return list;
	}

	/**
	 * 获取html中的${count}张图片
	 * @param count 保留图片的张数
	 * @return List<String>
	 */
	public static List<String> getImgSrcList(String html, int count) {
		List<String> list = getImgSrcList(html);
		return list.size() > count ? list.subList(0, count) : list;
	}

	/**
	 * 截取字符串长字，保留HTML格式
	 * 
	 * @param content
	 * @param len 字符长度
	 */
	public static String truncateHTML(String content, int len) {
		Document dirtyDocument = Jsoup.parse(content);
		Element source = dirtyDocument.body();
		Document clean = Document.createShell(dirtyDocument.baseUri());
		Element dest = clean.body();
		truncateHTML(source, dest, len);
		return dest.html();
	}

	/**
	 * 使用Jsoup预览
	 * 
	 * @param source
	 *            需要过滤的
	 * @param dest
	 *            过滤后的对象
	 * @param len
	 *            截取字符长度
	 * 
	 *         Document dirtyDocument = Jsoup.parse(sb.toString());<br />
	 *         Element source = dirtyDocument.body();<br />
	 *         Document clean = Document.createShell(dirtyDocument.baseUri());<br />
	 *         Element dest = clean.body();<br />
	 *         int len = 6;<br />
	 *         truncateHTML(source,dest,len);<br />
	 *         System.out.println(dest.html());<br />
	 */
	private static void truncateHTML(Element source, Element dest, int len) {
		List<Node> sourceChildren = source.childNodes();
		for (Node sourceChild : sourceChildren) {
			if (sourceChild instanceof Element) {
				Element sourceEl = (Element) sourceChild;
				Element destChild = createSafeElement(sourceEl);
				int txt = dest.text().length();
				if (txt >= len) {
					break;
				} else {
					len = len - txt;
				}
				dest.appendChild(destChild);
				truncateHTML(sourceEl, destChild, len);
			} else if (sourceChild instanceof TextNode) {
				int destLeng = dest.text().length();
				if (destLeng >= len) {
					break;
				}
				TextNode sourceText = (TextNode) sourceChild;
				int txtLeng = sourceText.getWholeText().length();
				if ((destLeng + txtLeng) > len) {
					int tmp = len - destLeng;
					String txt = sourceText.getWholeText().substring(0, tmp);
					TextNode destText = new TextNode(txt, sourceChild.baseUri());
					dest.appendChild(destText);
					break;
				} else {
					TextNode destText = new TextNode(sourceText.getWholeText(), sourceChild.baseUri());
					dest.appendChild(destText);
				}
			}
		}
	}

	/**
	 * 按原Element重建一个新的Element
	 * @param sourceEl
	 * @return
	 */
	private static Element createSafeElement(Element sourceEl) {
		String sourceTag = sourceEl.tagName();
		Attributes destAttrs = new Attributes();
		Element dest = new Element(Tag.valueOf(sourceTag), sourceEl.baseUri(), destAttrs);
		Attributes sourceAttrs = sourceEl.attributes();
		for (Attribute sourceAttr : sourceAttrs) {
			destAttrs.put(sourceAttr);
		}
		return dest;
	}

}
