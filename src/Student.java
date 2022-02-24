import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class Student {
	private static String[][] list;
	private static String[] student_token = new String[2]; // 后来已被废弃
	
	public static String getHtml(String url){
		Document doc = null;
		try {
			doc = Jsoup.connect(url ).ignoreContentType(true).userAgent("Mozilla/5.0 (Linux; Android 10; SM-G981B) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/97.0.4692.98 Mobile Safari/537.36lantuMobilecampus lantuMC").timeout(5000).get();
		} catch (IOException e) {
			// TODO 自动生成的 catch 块
			System.out.println("无法访问网页:"+url);
			e.printStackTrace();
			return null;
		}
		return doc.toString();
	}
	
	private StringBuffer post(String url, Map<String,String> headers, String str) throws IOException {
		PrintWriter out = null;
		BufferedReader in = null;
		StringBuffer result = new StringBuffer() ;
		URL theurl = new URL(url);
		URLConnection conn = theurl.openConnection();//建立连接
		conn.setReadTimeout(10000);//超时为10秒
		for(String key:headers.keySet()) {//设置请求属性
			conn.setRequestProperty(key, headers.get(key));
		}
		
		//发送post必须要设置这两行
		conn.setDoOutput(true);
		conn.setDoInput(true);
		
		//获取URLConnection对象对应的输出流
		out = new PrintWriter(conn.getOutputStream());
		//发送请求参数
		out.print(str);
		//flush输出流的缓冲
		out.flush();
		//定义BufferedReader输入流来读取URL的响应
		in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		
		String line;
		while((line = in.readLine()) != null) {
			result.append(line);
		}
		return result;
	}
	
	private boolean wk_post(String str) {//针对智慧维科的一些post配置,只需要传入请求体即可
		//请求头设置
		Map<String, String> sitMap = new HashMap<String, String>() ;
        // 添加键值对
//		sitMap.put("Content-Length", ""+str.length());//请求体长度,过长会超时,过短会截取,此消息为Close,可不设置此值
		sitMap.put("Access-Control-Allow-Origin", "*");//设置跨域请求
		sitMap.put("Accept","application/json, text/plain, */*");//客户端希望得到的数据
		sitMap.put("access_token","[object Object]");//学校把令牌放到请求体里了
		sitMap.put("User-Agent","Mozilla/5.0 (Linux; Android 10; SM-G981B) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/97.0.4692.98 Mobile Safari/537.36lantuMobilecampus lantuMC");//三星S20Ultra的请求头
		sitMap.put("Content-Type"," application/x-www-form-urlencoded");
		sitMap.put("Origin", "http://xgjktb.wfust.edu.cn");
		sitMap.put("X-Requested-With", "com.lantunet.MobileCampus.wfust");
		sitMap.put("Referer", "http://xgjktb.wfust.edu.cn/dist/jktb/index.html");
		sitMap.put("Accept-Encoding", "gzip, deflate");
		sitMap.put("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
		sitMap.put("Connection", "close");
		
		String posturl = "http://xgjktb.wfust.edu.cn/app/studentReport/updateReport";
		
		StringBuffer result = new StringBuffer();
		try {
			result = post(posturl, sitMap, str);
		} catch (IOException e) {
			return false;
		}
		if(result.indexOf("false")!=-1) {
			return false;
		}
		return true;
	}
	
	private static String get_text_mid(String start, String end, String text) {
		int wherest = text.indexOf(start)+start.length();
		int whereed = text.indexOf(end);
		if(wherest>whereed) {
//			System.out.println("获取文字失败,开始位置:"+wherest+"结束位置:"+whereed);
			if(text.indexOf("imgs/wk_ops.jpg")!=-1) {
				System.out.println("具体原因:网站崩了");
				System.exit(-1);
			}
			return null;
		}
		return text.substring(wherest, whereed);	
		
	}
	
	private static String get_access_token(String Student_id,boolean echo) {//这个会刷新一次变一次
		String token = getHtml("http://xgjktb.wfust.edu.cn/app/studentReport/getToken?studentNo="+Student_id+"&flag=0");
		String TOKEN = get_text_mid("accessToken\":\"", "\",\"teacherType", token);
		if(echo) {
			System.out.println(Student_id+" 的 access_token 为: "+TOKEN);
		}
		student_token[0] = Student_id;
		student_token[1] = TOKEN;
		return TOKEN;
	}
	
	public String get_location_1(String Student_id, boolean echo) {
		String access_token = get_access_token(Student_id,echo);
		if(access_token==null) {
			if(echo) {
				System.out.println("! 获取access_token失败,或许数据库中没有该学号?\n");
			}
			return null;
		}
//		String access_token = student_token[1];
		String location = getHtml("http://xgjktb.wfust.edu.cn/app/studentReport/getStudentReportInfo?access_token="+access_token);
		String LOCATION = get_text_mid("result\\\":\\\"", "\\\",\\\"boolselect", location);
		if(echo) {
			System.out.println("### "+Student_id+"的家庭住址:"+LOCATION+" ###\n");
		}
		return LOCATION;	
	}
	
	public String[][] get_location_2(String Student_id,int page, int num, String date, String report_submitstatus, boolean echo) throws IOException {//学生号,显示指定页数(0),显示的数量(10),指定的日期(空,2022-01-02),是否将表格打印出来
		String access_token = get_access_token(Student_id,echo);
		if(access_token==null) {
			System.out.println("获取access_token失败,或许数据库中没有该学号?\n");
			return null;
		}
		String filename = "info2.txt";
		String url = "http://xgjktb.wfust.edu.cn/app/studentReport/getReportList?access_token="+access_token+"&page="+page+"&limit="+num+"&startTime="+date+"&report_submitstatus="+report_submitstatus+"&flag=1";
		String info = getHtml(url);
		String Num = get_text_mid("count\\\":\\\"", "\\\",\\\"success", info);
		System.out.println("共有"+Num+"个记录\n");
//		System.out.println("连接为:"+url);
//		System.out.println("获取为:"+info);
//		System.out.println("日期为:"+date);
		int NUM = Integer.valueOf(Num).intValue();
		int totalpage = NUM/num + 1;
		page = page > totalpage ? totalpage : page;
		int shownum = totalpage==1?NUM+1:page==totalpage?NUM-(totalpage-1)*num+1:num+1;
		list = new String[shownum][5];//时间,状态,逻辑位置,体温,真实的地址
		list[0][0] = "\t时间\t";
		list[0][1] = "状态\t\t";
		list[0][2] = "逻辑\t\t";
		list[0][3] = "体温\t\t";
		list[0][4] = "住址\t\t";
		int startloc = info.indexOf("\"report_temperature");
		for(int i=1;i<shownum;i++) {
			int wherest0 = info.indexOf("report_time", startloc)+14;
			int whereed0 = info.indexOf("\"}",startloc);
			int wherest1 = info.indexOf("report_submitstatus", startloc)+22;
			int whereed1 = info.indexOf("report_time", startloc)-3;
			int wherest2 = info.indexOf("report_busid", startloc)+15;
			int whereed2 = info.indexOf("report_submitstatus", startloc)-3;
			int wherest3 = info.indexOf("report_temperature", startloc)+20;
			int whereed3 = info.indexOf("report_day", startloc)-2;
			startloc = whereed0+1;
			if(wherest2 > whereed2) {
				list[i][0] = "null";
				list[i][1] = "null";
				list[i][2] = "null";
				list[i][3] = "null";
				list[i][4] = "null";
			}else {
				list[i][0] = info.substring(wherest0, whereed0);
				list[i][1] = info.substring(wherest1, whereed1);
				list[i][2] = info.substring(wherest2, whereed2);
				list[i][3] = info.substring(wherest3, whereed3);
				list[i][4] = get_location_2_busid(access_token, list[i][2]);
//				System.out.println("access_token:"+list[i][2]);
			}
		}
		if(echo) {
			System.out.println("================================================================================================================================");
			FILE.write(filename, "=======================================================================================================\n");
			FILE.write(filename, "学号:"+Student_id+"\n");
			for(int i=0; i<shownum; i++) {
				for(int j=0; j<5; j++) {
					if(j==4) {
						System.out.println(list[i][j]);
						FILE.write(filename, list[i][j]+"\n");
//						System.out.print("\n");
					}else {
						System.out.print(list[i][j]);
						FILE.write(filename, list[i][j]+"\t");
						System.out.print("\t");
					}					
				}
			}
			if(totalpage > 1) {
				System.out.println("\t\t\t\t\t\t第 "+page+" / "+totalpage+" 页");
				FILE.write(filename, "\t\t\t\t\t\t第 "+page+" / "+totalpage+" 页");
			}
			System.out.println("================================================================================================================================\n");
			FILE.write(filename, "=======================================================================================================\n");
		}
		return list;
	}
	
	private String get_location_2_busid(String access_token,String busid) {
		String text = getHtml("http://xgjktb.wfust.edu.cn/app/studentReport/getReportInfo?access_token="+access_token+"&report_busid="+busid);
		//磨洋工
		String TEXT = get_text_mid("result\\\\\\\":\\\\\\\"", "\\\\\\\",\\\\\\\"boolselect", text);
		return TEXT;
	}
	
	public static String[] getmyip(String str, String type) {//type可以是文字String,也可以是网页html
		String ipjson;
		switch (type) {
			case "html" : ipjson = getHtml(str);break;
			case "String" : ipjson = str;break;
			default : return null;
		}
		String[] ipinfo = new String[2]; 
		ipinfo[0] = get_text_mid("您的IP地址是：", "</title>", ipjson);//ip
		ipinfo[1] = get_text_mid("来自：", "</p>", ipjson);//位置和运行商
		return ipinfo;
	}
	
	public String today() {
		SimpleDateFormat sdf = new SimpleDateFormat();// 格式化时间 
        sdf.applyPattern("yyyy-MM-dd"); 
        Date date = new Date();// 获取当前时间 
//        System.out.println("* 今天日期:" + sdf.format(date)); // 输出已经格式化的现在时间（24小时制）
		return sdf.format(date);
	}
	
	public boolean upTodayData(String Student_id, String temperature, String location) {//只会更新今日体温
		String Today = today();
		System.out.println("* 今天日期:" + Today); // 输出已经格式化的现在时间（24小时制）
		boolean ok = upMoreData(Student_id, temperature, location, Today, false, false, true);
		if(ok) {
			return true;
		}
		return false;
	}
	
	public boolean upMoreData(String Student_id, String temperature, String location, String Date, boolean cough, boolean boolheat, boolean echo) {//学号,体温,住址,要修改的日期,是否发热、咳嗽,是否发烧
		String access_token = get_access_token(Student_id, false);
		if(access_token == null) {
			System.out.println("！ 该学号不存在");
			return false;
		}
        String data = getHtml("http://xgjktb.wfust.edu.cn/app/studentReport/getReportList?access_token="+access_token+"&page=1&limit=10&startTime="+Date+"&report_submitstatus=&flag=1");
		String report_busid = get_text_mid("report_busid\":\"", "\",\"report_submitstatus", data);
		if(echo) {
			System.out.println("* "+Student_id+"的 token 为: "+access_token);
			System.out.println("* "+Student_id+"的 busid 为: "+report_busid);
		}
//		System.out.println(data);
		String endcompound = compoundPostBodyinfo(access_token, report_busid, location, temperature, cough, boolheat);
//		System.out.println("当前请求体:"+endcompound);
		if(endcompound == null) {
			System.out.println("! 无法合成请求体");
			return false;
		}
		boolean ok = wk_post(endcompound);
		if(ok){
			System.out.println("* 数据修改成功!\n");
			return true;
		}
		return false;
	}
	
	public boolean upAnyData(String Student_id, String temperature, String location, String Date, boolean cough, boolean boolheat, String text1, String text2, String text3, boolean echo) {
		
		boolean ok = upMoreData(Student_id, temperature, location, Date, cough, boolheat, echo);
		if(ok) {
			return true;
		}
		return false;
	}
	
	private String compoundPostBodyinfo(String access_token, String report_busid, String location, String temperature,boolean cough, boolean boolheat) {
		int report_provinceI = location.indexOf("省")+1;
		int report_cityI = location.indexOf("市")+1;
		if(report_provinceI == 0 || report_cityI == 0) {
			System.out.println("! 当前地理位置信息不完整");
			return null;
		}
		int Cough_result = 0;//0-不发热，不咳嗽 1-发热，咳嗽
		int Cough_boolselect = 1;//1-不发热，不咳嗽 0-发热，咳嗽
		if(cough) {
			System.out.print("!!! 警告:当前设置为发热,咳嗽");
//			System.exit(0);
			Cough_result = 1;
			Cough_boolselect = 0;
		}
		int Boolheat = 0;//0-不发烧,1-发烧
		if(boolheat) {
			System.out.print("!!! 警告:当前设置为发烧");
//			System.exit(0);
			Boolheat = 1;
		}
		String report_province = location.substring(0, report_provinceI);
		String report_city = location.substring(report_provinceI, report_cityI);
		String end_text = "access_token="+access_token+"&report_busid="+report_busid+"&report_province="+report_province+"&report_city="+report_city+"&report_address="+location+"&report_other={\"1\":{\"title\":\"当前位置\",\"result\":\""+location+"\",\"boolselect\":\"0\",\"isUpdate\":\"0\"},\"2\":{\"title\":\"当前体温\",\"result\":\""+temperature+"\",\"boolselect\":\"0\",\"isUpdate\":\"1\"},\"3\":{\"title\":\"是否有发热、咳嗽等 \",\"result\":\""+Cough_result+"\",\"boolselect\":\""+Cough_boolselect+"\",\"isUpdate\":\"1\"}}&report_temperature="+temperature+"&report_boolheat="+Boolheat;
		String end_texttourl = urlEncodeChinese(end_text);
//		System.out.println("* 当前编码为:"+end_texttourl);
		return end_texttourl;
	}
	private String urlEncodeChinese(String url) {
        try {
//            Matcher matcher = Pattern.compile("[\\u4e00-\\u9fa5]").matcher(url);
        	Matcher matcher = Pattern.compile("[^\\x00-\\xff]").matcher(url);
            String tmp = "";
            while (matcher.find()) {
                tmp = matcher.group();
                url = url.replaceAll(tmp, URLEncoder.encode(tmp, "UTF-8"));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return url.replace(" ","%20");
	}

}
