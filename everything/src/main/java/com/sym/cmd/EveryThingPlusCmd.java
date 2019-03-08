package com.sym.cmd;

/**
 * 交互
 */

import com.sym.config.EverythingPlusConfig;
import com.sym.core.EverythingPlusManger;
import com.sym.core.model.Condition;
import com.sym.core.model.Thing;

import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

public class EveryThingPlusCmd {
    private static Scanner scanner = new Scanner(System.in);
    
    public EveryThingPlusCmd() {
    }
    private static void parseParams(String[] args){
        //如果用户指定的用户参数格式不对，使用默认值即可
        EverythingPlusConfig everythingPlusConfig = EverythingPlusConfig.getInstance();
        for(String param :args){
            if(param.contains("=")) {
                int index = param.indexOf("=");
                if(index>param.length()-1){
                    return;
                }
                String str = param.substring(index+1);
                if (param.startsWith("--maxReturn=")) {
                    everythingPlusConfig.setMaxReturn(Integer.parseInt(str));
                } else if (param.startsWith("--deptOrderByAsc=")) {
                    everythingPlusConfig.setDeptOrderAsc(Boolean.parseBoolean(str));
                } else {
                    String[] paths = str.split(";");
                    if(paths==null ||paths.length<1){
                        return;
                    }
                    if (param.startsWith("--includePath=")) {
                        everythingPlusConfig.getIncludePath().clear();
                        for (String p : paths) {
                            everythingPlusConfig.getIncludePath().add(p);
                        }
                    } else if (param.startsWith("--excludePath=")) {
                        everythingPlusConfig.getExcludePath().clear();
                        for (String p : paths) {
                            everythingPlusConfig.getExcludePath().add(p);
                        }
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        //解析用户参数
        parseParams(args);
        System.out.println(EverythingPlusConfig.getInstance().toString());
        //欢迎
        welcome();
        //统一调度器
        EverythingPlusManger everythingManger = EverythingPlusManger.getInstance();
        //启动后台清理线程
        everythingManger.startBackGroundClearThread();
        //启动监控
        everythingManger.startFileSystemMonitor();
        //交互式
        interactive(everythingManger);
    }

    private static void welcome() {
        System.out.println("Welcome to Everything");
    }

    private static void interactive(EverythingPlusManger everythingManger) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            while (true) {
                System.out.println("everthing>>");
                String input = scanner.nextLine().trim();
                if (input.startsWith("search")) {
                    String[] values = input.split(" ");
                    if (values.length >= 2) {
                        if (!values[0].equals("search")) {
                            help();
                            continue;
                        }
                        Condition condition = new Condition();
                        String name = values[1];
                        condition.setName(name);
                        if(values.length>=3){
                            String fileType = values[2];
                            condition.setFileType(fileType.toUpperCase());
                        }
                        Search(everythingManger,condition);
                    }
                }
                switch (input){
                    case "help":
                        help();break;
                    case "quit":
                        quit();break;
                    case "index":
                        index(everythingManger);break;
                        default:
                            help();break;
                }
            }
        }
    }

    private static void Search(EverythingPlusManger everythingManger,Condition condition){
        System.out.println("检索功能");
        condition.setLimit(EverythingPlusConfig.getInstance().getMaxReturn());
        condition.setOrderByAsc(EverythingPlusConfig.getInstance().getDeptOrderAsc());
        List<Thing> things = everythingManger.search(condition);

        if (things.size() == 0) {
            System.out.println("没有");
        }
        Iterator var4 = things.iterator();
        while(var4.hasNext()) {
            Thing thing = (Thing)var4.next();
            System.out.println(thing.getPath());
        }
    }

    private static void help() {
        System.out.println("命令列表:");
        System.out.println("帮助：help");
        System.out.println("退出：exit");
        System.out.println("索引:index");
        System.out.println("检索：search fileName [fileType: img doc bin other]");
    }

    private static void quit() {
        System.out.println("Thanks use Everything Plus");
        System.exit(0);
    }

    private static void index(final EverythingPlusManger everythingManger) {
        (new Thread(() -> everythingManger.buildIndex())).start();
    }

}
