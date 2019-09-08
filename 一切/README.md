＃访达

基于Java的跨平台的命令行文件搜索工具
照一切桌面工具
解决窗口命令行下文件搜索的问题，跨平台无差异使用，锻炼编码能;
功能：
检索(文件名模糊检索，+文档类型，//最近检索文件)，
索引(全量索引，自定义目录(排除特定目录))
技术：Java databaseH2 jdbc lombok java多线程....

命令列表:
帮助：help
退出：exit
索引:index
检索：search fileName [fileType: img doc bin other]

java -var everything -plus -1.0.0.jar 
--maxReturn=40
--deptOrderByAsc=false;
--includePath=D:\;E:\work;
--excludePath=E:work\include;
