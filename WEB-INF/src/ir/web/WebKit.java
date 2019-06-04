package ir.web;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import org.apache.commons.fileupload.DiskFileUpload;
import org.apache.commons.fileupload.FileItem;
import ir.util.Coerce;
import ir.util.StringKit;

/**
 * This class provides utility functions for Web Apps
 */
public abstract class WebKit {
	public static final String checkmark = "&#x2713;";

	/**
	 * Builds an anchor.
	 * 
	 * @param Object
	 *            data to appear inside anchor tags
	 * @param String
	 *            base of url
	 * @param Object
	 *            ParmName,ParmValue alternating pairs
	 */
	public static String anchor(Object content, String urlBase, Object... parmsAndValues) {
		return anchorExt(content, "", urlBase, parmsAndValues);
	}

	/**
	 * Builds an anchor with extra attributes.
	 * 
	 * @param Object
	 *            data to appear inside anchor tags
	 * @param String
	 *            base of url
	 * @param Object
	 *            ParmName,ParmValue alternating pairs
	 */
	public static String anchorExt(Object content, String anchorAttributes, String urlBase, Object... parmsAndValues) {
		return "<a href=" + quote(url(urlBase, parmsAndValues)) + " " + anchorAttributes + ">" + content + "</a>";
	}

	/**
	 * Builds an anchor.
	 */
	public static String anchorNoEscape(Object displayValue, String urlBase, Object... parmsAndValues) {
		return "<a href='" + url(urlBase, parmsAndValues) + "'>" + Coerce.toString(displayValue) + "</a>";
	}

	public static String button(String name, String label, String otherAttributes) {
		return "<input type=button name='" + name + "' id='" + name + "'" + " value='" + label + "' " + otherAttributes
				+ ">";
	}

	public static String dump(javax.servlet.http.HttpServletRequest request, String equalStr, String pairDelim) {
		StringBuilder b = new StringBuilder("Request Dump:").append(pairDelim);
		try {
			b.append("URL").append(equalStr).append(request.getRequestURL()).append(pairDelim);
			b.append("Method").append(equalStr).append(request.getMethod()).append(pairDelim);
			Set<String> ts = new TreeSet<String>();
			ts.addAll(Collections.list(request.getParameterNames()));
			for (String pn : ts) {
				String[] pv = request.getParameterValues(pn);
				b.append(pn).append(equalStr).append(StringKit.join(";", pv)).append(pairDelim);
			}
			ts.clear();
			ts.addAll(Collections.list(request.getAttributeNames()));
			b.append("Attributes:").append(pairDelim);
			for (String pn : ts) {
				Object o = request.getAttribute(pn);
				b.append(pn).append(equalStr).append(o == null ? "null" : o.toString()).append(pairDelim);
			}
			HttpSession ses = request.getSession();
			if (ses == null) {
				b.append("Session is null").append(pairDelim);
			} else {
				b.append("Session:").append(pairDelim);
				ts.clear();
				b.append("Created ").append(new Date(ses.getCreationTime())).append(pairDelim);
				b.append("Last Accessed ").append(new Date(ses.getLastAccessedTime())).append(pairDelim);
				b.append("Max Inactive Interval ").append(ses.getMaxInactiveInterval()).append(pairDelim);
				ts.addAll(Collections.list(ses.getAttributeNames()));
				for (String pn : ts) {
					Object o = ses.getAttribute(pn);
					b.append(pn).append(equalStr).append(o == null ? "null" : o.toString()).append(pairDelim);
				}
			}
		} catch (Exception e) {
			b.append("failed:" + e.getMessage());
		}
		return b.toString();
	}

	public static String escapeHtml(String value) {
		return org.owasp.encoder.Encode.forHtml(value);
	}

	public static String eventButton(String name, Object label, String onclick, String attrs) {
		if (name.equals("")) {
			return "<button type='button' onclick=" + quote(onclick) + " " + attrs + ">" + label + "</button>";
		}
		return "<button type='button' onclick=" + quote(onclick) + " name=" + quote(name) + " id=" + quote(name) + " "
				+ attrs + ">" + label + "</button>";
	}

	public static String getFullContextPath(HttpServletRequest request) {
		return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort()
				+ request.getContextPath();
	}

	public static File imageUrlToFile(String url) throws Exception {
		String ext = StringKit.getExtension(url);
		File f = File.createTempFile("image", "." + ext);
		URLConnection c = new URL(url).openConnection();
		InputStream ist = null;
		FileOutputStream ost = null;
		try {
			int bytesRead;
			byte[] ba = new byte[16384];
			c.setDoInput(true);
			c.setReadTimeout(5000);
			c.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
			c.connect();
			ist = c.getInputStream();
			ost = new FileOutputStream(f);
			while (0 < (bytesRead = ist.read(ba)))
				ost.write(ba, 0, bytesRead);
			ist.close();
			ist = null;
			ost.close();
			ost = null;
			return f;
		} finally {
			if (ist != null)
				try {
					ist.close();
				} catch (Exception nevermind) {
				}
			if (ost != null)
				try {
					ost.close();
				} catch (Exception nevermind) {
				}
		}
	}

	public static boolean isMobileDevice(HttpServletRequest request) throws Exception {
		final String[] mobileAgents = new String[] { "android", "blackberry;", "blazer;", "epoc;", "handspring;",
				"ipod;", "iphone;", "kyocera;", "lg;", "motorola;", "mmp;", "nokia;", "opwv;", "palm;",
				"playstation portable;", "samsung;", "smartphone;", "sonyericsson;", "symbian;", "wap;",
				"windows ce;" };
		String agent = request.getHeader("User-Agent");
		String cpu = request.getHeader("ua-cpu");
		if (cpu != null) {
			cpu = cpu.toLowerCase();
			if (cpu.startsWith("arm")) {
				return true;
			}
		}
		if (agent != null) {
			agent = StringKit.left(agent.toLowerCase(), 60);
			for (String a : mobileAgents) {
				if (-1 < agent.indexOf(a)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Builds a submit button
	 *
	 * @param Object
	 *            data to appear inside anchor tags
	 * @param String
	 *            base of url
	 * @param Object
	 *            ... ParmName,ParmValue alternating pairs
	 */
	public static String linkButton(Object label, String urlBase, Object... parmsAndValues) {
		return "<button type='button' class='navButton back' tabindex='1' onclick=\"sr5.go("
				+ quote(url(urlBase, parmsAndValues)) + ")\" >" + label + "</button>\n";
	}

	public static String linkButtonAttrs(Object label, String attrs, String urlBase, Object... parmsAndValues) {
		if (attrs == null) {
			attrs = "";
		}
		if (attrs.indexOf("tabindex=") == -1) {
			attrs += " tabindex='1'";
		}
		return "<input type='button' " + attrs + " onclick=sr5.go(" + quote(url(urlBase, parmsAndValues)) + ") value="
				+ quote(label) + ">";
	}

	public static void main(String[] args) {
		try {
			FileInputStream is = new FileInputStream("c:\\temp\\MulipartData.txt");
			String bound = "--wiekmonxwhkgpgro";
			Map<String, byte[]> m = readMultipart(is, bound);
			for (String k : m.keySet()) {
				System.out.println(k + "=" + Coerce.toString(m.get(k)));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String quote(Object o) {
		return "'" + (o == null ? "" : StringKit.replace(o.toString(), "'", "&#39;")) + "'";
	}

	public static Map<String, byte[]> readMultipart(HttpServletRequest req) throws Exception {
		InputStream is = req.getInputStream();
		String contentType = req.getHeader("Content-type");
		String boundary = "--" + contentType.substring(contentType.indexOf("boundary=") + 9);
		return readMultipart(is, boundary);
	}

	protected static Map<String, byte[]> readMultipart(InputStream is, String boundary) throws Exception {
		Map<String, byte[]> m = new HashMap<String, byte[]>();
		boolean inField = false;
		String name = null;
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		StringBuilder sb = new StringBuilder();
		int b;
		while (-1 != (b = is.read())) {
			sb.append((char) b);
			if (sb.length() >= boundary.length() && sb.substring(sb.length() - boundary.length()).equals(boundary)) {// we're
																														// at
																														// the
																														// end
																														// of
																														// a
																														// boundary
				sb.setLength(sb.length() - boundary.length());
				if (inField && name != null) {// end of a field
					m.put(name, readMultipartItem(os, boundary.length() - 1));
					sb.setLength(0);
					name = null;
				} else {// start of a field
					inField = true;
				}
				os.reset();
				continue;
			}
			if (inField && name == null && sb.length() > 7) {
				int nameAt = sb.indexOf("name=");
				if (nameAt > -1) {
					int nameQuoteEnd = sb.indexOf("\"", nameAt + 7);
					if (nameQuoteEnd > nameAt) {
						name = sb.substring(nameAt + 6, nameQuoteEnd);
					}
				}
			}
			os.write(b);
		}
		if (name != null && os.size() > 0) {
			m.put(name, readMultipartItem(os, boundary.length() - 1));
		}
		return m;
	}

	public static Map<String, FileItem> readMultipartApache(HttpServletRequest request) throws Exception {
		File fT = File.createTempFile("temp", ".jpg");
		fT.delete();
		DiskFileUpload fu = new DiskFileUpload();
		fu.setSizeMax(9999999);
		fu.setSizeThreshold(4096);
		fu.setRepositoryPath(fT.getParent());
		List<FileItem> lst = fu.parseRequest(request);
		Map<String, FileItem> m = new HashMap<String, FileItem>();
		for (FileItem f : lst)
			m.put(f.getFieldName(), f);
		return m;
	}

	private static byte[] readMultipartItem(ByteArrayOutputStream s, int endStrip) {
		byte[] d = s.toByteArray();
		int startData = 0;
		String sd = new String(d);
		int lastHeaderLineStart = Math.max(sd.lastIndexOf("Content-Disposition:"), sd.lastIndexOf("Content-Type:"));
		if (lastHeaderLineStart > -1) {
			int newLineAt = sd.indexOf('\n', lastHeaderLineStart);
			if (newLineAt > lastHeaderLineStart) {
				startData = newLineAt;
				while (startData < d.length && Character.isWhitespace(sd.charAt(startData))) {
					startData++;
				}
			}
		}
		if (startData == d.length - endStrip)
			return new byte[0];
		byte[] x = new byte[d.length - startData - endStrip];
		System.arraycopy(d, startData, x, 0, x.length);
		return x;
	}

	public static List<String> readUrl(String url) throws Exception {
		List<String> r = new ArrayList<String>();
		URL u = new URL(url);
		InputStreamReader isr = new InputStreamReader(u.openStream());
		BufferedReader br = new BufferedReader(isr);
		String ln;
		while ((ln = br.readLine()) != null)
			r.add(ln);
		br.close();
		return r;
	}

	public static String stripQuery(String url) {
		int questionAt = url.indexOf("?");
		return questionAt == -1 ? url : url.substring(0, questionAt);
	}

	/**
	 * returns a submit button with the name and label passed
	 */
	public static String submit(String name, String label) {
		return submit(name, label, "");
	}

	/**
	 * returns a submit button with the name and label passed
	 */
	public static String submit(String name, String label, String otherAttrs) {
		return "<button type='submit' tabindex='1' name=" + quote(name) + " id=" + quote(name) + " "
				+ (otherAttrs == null ? "" : otherAttrs) + ">" + label + "</button>";
	}

	/**
	 * returns a submit button with the image passed
	 */
	public static String submitImage(String name, String imgSrc, String altTxt, int width, int height) {
		return "<input type='image' name=" + quote(name) + " id=" + quote(name) + " src=" + quote(imgSrc) + " alt="
				+ quote(altTxt) + " title=" + quote(altTxt) + " width=" + quote(width) + " height=" + quote(height)
				+ ">";
	}

	/**
	 * Builds a url with request-appropriate prefix. Assumes port less than 1000 are
	 * out on www.
	 */
	public static String url(HttpServletRequest request, Object urlBase, Object... parmsAndValues) {
		String u = url(urlBase, parmsAndValues);
		String ulc = u.toLowerCase();
		if (!ulc.startsWith("http")) {
			String prefix = request.getRequestURL().toString();
			int lastSlash = prefix.lastIndexOf('/');
			if (lastSlash > 0) {
				prefix = prefix.substring(0, lastSlash + 1);
			} else {// could RequestURL ever get messed up?
				int port = request.getServerPort();
				String protocol = "http" + (request.isSecure() ? "s" : "");
				String server = request.getServerName().toLowerCase();
				if (server.indexOf("www") == -1 && port < 1000) {
					// server = "www." + server;
				}
				String contextPath = request.getContextPath();
				if (!contextPath.startsWith("/")) {
					contextPath = "/" + contextPath;
				}
				String portSuffix = "";
				if ((protocol.equals("https") && port != 443) || (protocol.equals("http") && port != 80)) {
					portSuffix = ":" + port;
				}
				prefix = protocol + "://" + server + portSuffix + contextPath + "/";
			}
			u = prefix + u;
		}
		return u;
	}

	/**
	 * Builds a url.
	 */
	public static String url(Object urlBase, Object... parmsAndValues) {
		if (parmsAndValues == null || parmsAndValues.length == 0) {
			return urlBase.toString();
		}
		StringBuilder b = new StringBuilder(urlBase.toString());
		if (parmsAndValues.length > 0) {
			if (b.charAt(b.length() - 1) != '?' && b.charAt(b.length() - 1) != '&') {
				if (b.toString().indexOf('?') > 0) {
					b.append('&');
				} else {
					b.append('?');
				}
			}
			b.append(parmsAndValues[0]);
			for (int i = 1; i < parmsAndValues.length; i++) {
				String v = Coerce.toString(parmsAndValues[i]);
				String hash = "";
				char joinToken = '=';
				if (i > 1 && i % 2 == 0) {
					joinToken = '&';
				}
				b.append(joinToken);
				if (joinToken == '=') {
					int hashAt = v.indexOf('#');
					if (hashAt > -1) {
						hash = v.substring(hashAt);
						v = v.substring(0, hashAt);
					}
					try {
						v = URLEncoder.encode(v, "UTF-8");
					} catch (Exception nevermind) {
					}
					;
				}
				b.append(v).append(hash);
			}
		}
		return b.toString();
	}

}
