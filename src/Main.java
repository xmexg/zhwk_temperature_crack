import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.Scanner;

public class Main {
	/**
	 * 潍坊科技学院每日填报体温系统的漏洞利用
	 * 
	 * -DproxyHost=代理服务器ip -DproxyPort=代理服务器端口 来设置代理
	 * 
	 * -Dpostid=学号 来修改当天的数据; 
	 * 		可选:-Dpostloc="省市县村",默认为最新一次位置;
	 * 			-Dposttemp=36.8,默认体温为36.8℃
	 * 
	 * java -jar XXX.jar 2021019000-2021020999 2021021234 2021022345 可直接简易搜索指定的学号
	 * 
	 */
	
	public static void main(String[] args) throws IOException {
		System.out.println("\r\n"
				+ "▄▄      ▄▄           ▄▄\r\n"
				+ "██      ██           ██\r\n"
				+ " █▄ ██ ▄█▀           ██ ▄██▀\r\n"
				+ " ██ ██ ██            ██▄██\r\n"
				+ " ███  ███   █████    ██▀██▄\r\n"
				+ " ███  ███            ██  ▀█▄\r\n"
				+ "");
		System.out.println(" ___   _  __                     __   __\r\n"
				+ "|_ _| | |/ /_ __   _____      __ \\ \\ / /__  _   _ _ __\r\n"
				+ " | |  | ' /| '_ \\ / _ \\ \\ /\\ / /  \\ V / _ \\| | | | '__|\r\n"
				+ " | |  | . \\| | | | (_) \\ V  V /    | | (_) | |_| | |\r\n"
				+ "|___| |_|\\_\\_| |_|\\___/ \\_/\\_/     |_|\\___/ \\__,_|_|\r\n"
				+ "\r\n"
				+ " _   _                                  _     _\r\n"
				+ "| | | | ___  _ __ ___   ___    __ _  __| | __| |_ __ ___  ___ ___\r\n"
				+ "| |_| |/ _ \\| '_ ` _ \\ / _ \\  / _` |/ _` |/ _` | '__/ _ \\/ __/ __|\r\n"
				+ "|  _  | (_) | | | | | |  __/ | (_| | (_| | (_| | | |  __/\\__ \\__ \\\r\n"
				+ "|_| |_|\\___/|_| |_| |_|\\___|  \\__,_|\\__,_|\\__,_|_|  \\___||___/___/\r\n"
				+ "");
		System.out.println("\t\t# 学校服务器太渣,持续loop必崩!!!!!");
		Student student =new Student();
		String filename = "info1.txt";
		String[] ipinfo = Student.getmyip("https://2022.ip138.com/", "html");
		InetAddress ip4 = Inet4Address.getLocalHost();
		System.out.println("\t\t# 当前系统内网ip:"+ip4.getHostAddress());
		System.out.println("\t\t# 当前系统外网ip:"+ipinfo[0]);
		System.out.println("\t\t# 当前ip归属地:"+ipinfo[1]);
		//-DproxySet=true -DproxyHost=myProxyServer.come -DproxyPort=80
		boolean havenotproxy = false;//默认有代理
		if(System.getProperty("proxyHost") == null || System.getProperty("proxyPort") == null) {
			System.out.println("\t\t! 提示: 当前系统未设置代理 !!!!");
			havenotproxy = true;//确认没有代理
			if(System.getProperty("noproxy") == null) {
				System.out.println("\t\t+ 请在jar文件前使用 -DproxyHost=代理服务器ip -DproxyPort=代理服务器端口 来设置代理");
				System.out.print("\n? 我知道我没有设置代理,我将要以自己的真实ip访问学校服务器(y|其他):");
				Scanner in = new Scanner(System.in);
				String input = in.next();
//				in.close();
				if(input.indexOf("y")==-1&&input.indexOf("Y")==-1) {
					System.out.println("> 程序已退出");
					System.exit(0);
				}
			}
		}
		if(System.getProperty("postid") != null) {
			String Student_id = System.getProperty("postid");
			String temperature = "36.8";
			String location = student.get_location_1(""+Student_id,false);
			if(location == null) {
				System.out.println("该学号不可用");
				System.exit(-1);
			}
			if(System.getProperty("postloc") != null) {
				location = System.getProperty("postloc");
			}
			if(System.getProperty("posttemp") != null) {
				temperature = System.getProperty("postloc");
			}
			student.upTodayData(Student_id, temperature, location);
		}
		System.out.println();
		
		if(args.length>=1) {
			int y = 0,n = 0;
			System.out.println("##########################################");
			FILE.write(filename, "##########################################\n");
			System.out.println("*     学号\t\t住址");
			FILE.write(filename, "*     学号\t\t住址\n");
			for (int i=0;i<args.length;i++) {
				String id = args[i];
//				System.out.println(i+":"+id);
				if(id.indexOf("-h")!=-1 || id.indexOf("help") != -1) {
					System.out.println("\n  潍坊科技学院每日填报体温系统的漏洞利用\n\n"
							+ " -DproxyHost=代理服务器ip -DproxyPort=代理服务器端口 来设置代理\n\n"
							+ " -Dnoproxy 来强制不使用代理\n\n"
							+ " -Dline 来设置只执行一次就退出\n\n"
							+ " -Dpostid=学号 来修改当天的数据; \n"
							+ " \t可选:-Dpostloc=\"省市县村\",默认为最新一次位置;\n"
							+ " \t    -Dposttemp=36.8,默认体温为36.8℃\n\n"
							+ " java -jar XXX.jar 2021019000-2021020999 2021021234 2021022345 可直接简易搜索指定的学号"
							+ "\n");
				}
				if(id.indexOf("-")==0) {
					System.out.println("> 请把启动配置添加到jar文件前面,位于jar文件后面的启动配置不会执行(help除外):");
					System.out.println("> "+id);
//					System.out.println(id);
//					System.out.println(System.getProperty("proxy",null));
					continue;
				}
				int index = id.indexOf("-");
				if(index!=-1){
					String stS = id.substring(0, index);
					String edS = id.substring(index+1);
					int stI = Integer.valueOf(stS).intValue();
					int edI =Integer.valueOf(edS).intValue();
					if(stI>edI) {
						int temp = stI;
						stI = edI;
						edI =temp;
					}
					for(;stI<=edI;stI++) {
						String loc = student.get_location_1(""+stI,false);
						if(loc==null) {
							loc="获取住址失败,或许数据库中没有该学号";
							n++;
							System.out.println("! "+stI+" "+loc);
							FILE.write(filename, "! "+stI+" "+loc+"\n");
						}else {
							y++;
							System.out.println("* "+stI+" "+loc);
							FILE.write(filename, "* "+stI+" "+loc+"\n");
						}	
					}
				}else {
					String loc = student.get_location_1(id,false);
					if(loc==null) {
						loc="获取住址失败,或许数据库中没有该学号";
						n++;
						System.out.println("! "+id+" "+loc);
						FILE.write(filename, "! "+id+" "+loc+"\n");
					}else {
						y++;
						System.out.println("* "+id+" "+loc);
						FILE.write(filename, "* "+id+" "+loc+"\n");
					}
				}
			}
			System.out.println("~  成功: "+y+"\t失败: "+n);
			FILE.write(filename, "~  成功: "+y+"\t失败: "+n+"\n");
			System.out.println("##########################################");
			FILE.write(filename, "##########################################\n");
		}else {
			while(System.getProperty("line") == null) {
				Scanner scanner = new Scanner(System.in);
				System.out.print("+ 请输入模式(0-简易搜索,1-高级搜索,2-简易修改今天的数据,3-我想修改任意数据,4-上帝模式):");
				int input = scanner.nextInt();
				switch (input) {
				case 0:
					while(true) {
						System.out.print("+ 请输入一个学号:");
						int idI = scanner.nextInt();
						String id = ""+idI;
						student.get_location_1(id,true);
					}
				case 1:
					while(System.getProperty("line") == null) {
						System.out.print("+ 请输入一个学号:");
						int idt = scanner.nextInt();
						String id = ""+idt;
						System.out.print("+ 请输入每页显示的个数(默认10):");
						int num = scanner.nextInt();
						if(num<1) {
							num=10;
						}
						System.out.print("+ 请指定要显示的页数(默认1):");
						int page = scanner.nextInt();
						if(page<1) {
							page=1;
						}
						System.out.print("+ 请指定日期(例如20220208,错误输入时设置为空):");
						int datet = scanner.nextInt();
						StringBuffer dateS = new StringBuffer(""+datet);
						if(dateS.length()==8) {
							dateS.insert(6, "-");
							dateS.insert(4,"-");							
						}
						String date = dateS.toString();
						String reg = "[2][0][2][0-9]-[0-1][0-9]-[0-9][0-9]";
						if(!date.matches(reg)) {
							date = "";
						}
						student.get_location_2(id, page, num, date, "1",true);
						
					}
				case 2 :
					System.out.print("+ 请输入一个学号:");
					int Student_id = scanner.nextInt();
					System.out.print("+ 请输入一个体温(36.8):");
					double temperature = scanner.nextDouble();
					System.out.println("* "+Student_id+" 的最新位置为: "+student.get_location_1(""+Student_id,false));
					System.out.print("+ 请输入完整的地址:");
					String location = scanner.next();
//					System.out.println(temperature);
//					System.out.println(location);
					student.upTodayData(""+Student_id, ""+temperature, location);
					break;
				case 3:
					System.out.print("+ 请输入一个学号:");
					int idt = scanner.nextInt();
					String id = ""+idt;
					System.out.print("+ 请输入要修改的日期(例如20220208,错误输入时设置为今天日期):");
					int datet = scanner.nextInt();
					StringBuffer dateS = new StringBuffer(""+datet);
					if(dateS.length()==8) {
						dateS.insert(6, "-");
						dateS.insert(4,"-");							
					}else {
						dateS = new StringBuffer(student.today());
					}
					String date = dateS.toString();
					String reg = "[2][0][2][0-9]-[0-1][0-9]-[0-9][0-9]";
					if(!date.matches(reg)) {
						date = student.today();
					}
					System.out.println("* 设置日期为:"+date);
					System.out.println("* 该学号该天的信息为:");
					student.get_location_2(id, 1, 10, date, "",true);
					System.out.println("* "+id+" 的最新位置为: "+student.get_location_1(""+id,false));
					System.out.print("+ 请输入新的体温:");
					String temperautre = scanner.next();
					System.out.print("+ 请输入新的位置:");
					String location_3 = scanner.next();
					System.out.print("+ 是否设置为已感冒(y/n):");
					String coughS = scanner.next();
					Boolean cough = false;
					if(coughS.equals("y")) {
						cough = true;
					}
					System.out.print("+ 是否设置为已发烧(y/n):");
					String boolheatS = scanner.next();
					Boolean boolheat = false;
					if(boolheatS.equals("y")) {
						boolheat = true;
					}
					student.upMoreData(id, temperautre, location_3, date, cough, boolheat, false);
					break;
				case 4 :
					if(havenotproxy) {
						System.out.println("!!! 本部分内容禁止以真实ip执行,请启用代理 !!!");
						break;
					}
					System.out.println("! 警告:本部分内容过于危险,暂不开放");
					System.exit(0);
					System.out.print("+ 请输入一个学号:");
					int idt4 = scanner.nextInt();
					String id4 = ""+idt4;
					System.out.print("+ 请输入要修改的日期(例如20220208,错误输入时设置为今天日期):");
					int datet4 = scanner.nextInt();
					StringBuffer dateS4 = new StringBuffer(""+datet4);
					if(dateS4.length()==8) {
						dateS4.insert(6, "-");
						dateS4.insert(4,"-");							
					}else {
						dateS4 = new StringBuffer(student.today());
					}
					String date4 = dateS4.toString();
					String reg4 = "[2][0][2][0-9]-[0-1][0-9]-[0-9][0-9]";
					if(!date4.matches(reg4)) {
						date4 = student.today();
					}
					System.out.println("* 设置日期为:"+date4);
					System.out.println("* 该学号该天的信息为:");
					student.get_location_2(id4, 1, 10, date4, "",true);
					System.out.println("* "+id4+" 的最新位置为: "+student.get_location_1(""+id4,false));
					System.out.println("[+] 请输入表格一的标题:");
					String text1_1 = scanner.nextLine();
					System.out.println("[+] 请输入表格一的内容:");
					String text1_2 = scanner.nextLine();
					System.out.println("[+] 请输入表格二的标题:");
					String text2_1 = scanner.nextLine();
					System.out.println("[+] 请输入表格二的内容:");
					String text2_2 = scanner.nextLine();
					System.out.println("[+] 请输入表格三的标题:");
					String text3_1 = scanner.nextLine();
					System.out.println("[+] 请输入\"是否发烧处\"显示是还是否:(只扫描空格前的内容,只要出现是字就为是)");
					String text3_2 = scanner.next();
					if(text3_2.indexOf("是")!=-1) {
						
					}
					break;
				default :
					System.out.println("! 输入有误");
					break;
				}
//				scanner.close();
			}
		}
	}
}