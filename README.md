### 对智慧维科每日体温填报的漏洞利用

![image](img/demo.png)

# 使用说明
- java -jar XXX.jar 2021019000-2021020999 2021021234 2021022345 可直接简易搜索指定的学号  
- 使用 -DproxyHost=代理服务器ip -DproxyPort=代理服务器端口 来设置代理  
- -Dpostid=学号 来修改当天的数据  
  可选: -Dpostloc="省市县村",默认为最新一次位置; -Dposttemp=36.8,默认体温为36.8℃  
- 配合crontab可实现自动每日体温填报,如果没有指定体温和地址默认会使用上次的体温和地址(第一次填报体温请手动填报)  
  示例：在linux终端输入 crontab -e 结尾填入以下内容:  
  `# 15 6 * * * cd /home/ubuntu/java && java -Dnoproxy -Dline -Dpostid=2021020221 -jar zhwk_Home_address_dangerous.jar`  


# 注意  
- 如果修改过去的数据，表中的填写时间也会更新成今天的
- 当查询数据时，程序会在当前目录下创建文件来保存查询信息，请确保程序有当前目录创建和写入文件的功能
